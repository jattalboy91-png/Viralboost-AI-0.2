package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.data.service.StudioSceneItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import kotlin.math.abs
import kotlin.math.sin

data class RenderedReelOutput(
    val shareUri: Uri,
    val gallerySaved: Boolean,
    val displayPath: String
)

/**
 * Hardware-accelerated 9:16 HD (720x1280 @ 30FPS, 12s) H.264 MP4 encoder
 * using Android MediaCodec + MediaMuxer + StaticLayout Devanagari caption rendering.
 */
object AndroidReelVideoRenderer {

    private const val WIDTH = 720
    private const val HEIGHT = 1280
    private const val FPS = 30
    private const val DURATION_SECONDS = 12
    private const val TOTAL_FRAMES = FPS * DURATION_SECONDS
    private const val BIT_RATE = 4_000_000
    private const val FRAME_US = 1_000_000L / FPS

    suspend fun renderAndSaveMp4(
        context: Context,
        scenes: List<StudioSceneItem>,
        sceneBitmaps: List<Bitmap>,
        categoryLabel: String,
        accentColorInt: Int,
        secondaryColorInt: Int,
        onProgress: (Float) -> Unit = {}
    ): Result<RenderedReelOutput> = withContext(Dispatchers.IO) {
        val outFile = File(context.cacheDir, "viralboost_reel_${System.currentTimeMillis()}.mp4")
        var codec: MediaCodec? = null
        var muxer: MediaMuxer? = null
        var inputSurface: android.view.Surface? = null

        try {
            val safeScenes = if (scenes.isNotEmpty()) scenes.take(4) else listOf(
                StudioSceneItem("0-3s", "ViralBoost AI 9:16 Studio", "", "zoom-in"),
                StudioSceneItem("3-6s", "Trending Reels & Shorts", "", "pan-right"),
                StudioSceneItem("6-9s", "Cinema Motion Engine", "", "zoom-out"),
                StudioSceneItem("9-12s", "Save & Share Now!", "", "pan-left")
            )

            val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, WIDTH, HEIGHT).apply {
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
                setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
                setInteger(MediaFormat.KEY_FRAME_RATE, FPS)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            }

            codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC).apply {
                configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            }
            inputSurface = codec.createInputSurface()
            codec.start()

            muxer = MediaMuxer(outFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            var trackIndex = -1
            var muxerStarted = false
            var outputFrameCount = 0L
            val bufferInfo = MediaCodec.BufferInfo()

            fun drainEncoder(endOfStream: Boolean) {
                if (endOfStream) {
                    codec?.signalEndOfInputStream()
                }
                while (true) {
                    val idx = codec?.dequeueOutputBuffer(bufferInfo, if (endOfStream) 10_000L else 0L) ?: break
                    when {
                        idx == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                            if (!endOfStream) break
                        }
                        idx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                            if (!muxerStarted) {
                                trackIndex = muxer!!.addTrack(codec!!.outputFormat)
                                muxer!!.start()
                                muxerStarted = true
                            }
                        }
                        idx >= 0 -> {
                            val encoded = codec!!.getOutputBuffer(idx)
                            if (encoded != null && bufferInfo.size > 0) {
                                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                                    bufferInfo.size = 0
                                } else if (muxerStarted) {
                                    encoded.position(bufferInfo.offset)
                                    encoded.limit(bufferInfo.offset + bufferInfo.size)
                                    bufferInfo.presentationTimeUs = outputFrameCount * FRAME_US
                                    outputFrameCount++
                                    muxer!!.writeSampleData(trackIndex, encoded, bufferInfo)
                                }
                            }
                            codec!!.releaseOutputBuffer(idx, false)
                            if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                break
                            }
                        }
                    }
                }
            }

            for (frame in 0 until TOTAL_FRAMES) {
                val elapsedSec = frame.toFloat() / FPS.toFloat()
                val canvas = inputSurface.lockCanvas(null)
                try {
                    drawFrameToCanvas(
                        canvas = canvas,
                        elapsedSec = elapsedSec,
                        scenes = safeScenes,
                        sceneBitmaps = sceneBitmaps,
                        categoryLabel = categoryLabel,
                        accentColorInt = accentColorInt,
                        secondaryColorInt = secondaryColorInt
                    )
                } finally {
                    inputSurface.unlockCanvasAndPost(canvas)
                }
                drainEncoder(endOfStream = false)
                if (frame % 15 == 0) {
                    onProgress((frame.toFloat() / TOTAL_FRAMES.toFloat()).coerceIn(0.05f, 0.95f))
                }
            }

            drainEncoder(endOfStream = true)
            try { codec.stop() } catch (_: Throwable) {}
            try { codec.release() } catch (_: Throwable) {}
            codec = null
            try { inputSurface.release() } catch (_: Throwable) {}
            inputSurface = null
            if (muxerStarted) {
                try { muxer.stop() } catch (_: Throwable) {}
            }
            try { muxer.release() } catch (_: Throwable) {}
            muxer = null

            onProgress(1f)
            saveRenderedMp4ToGalleryAndShareUri(context, outFile)
        } catch (t: Throwable) {
            try { codec?.release() } catch (_: Throwable) {}
            try { inputSurface?.release() } catch (_: Throwable) {}
            try { muxer?.release() } catch (_: Throwable) {}
            Result.failure(t)
        }
    }

    private fun saveRenderedMp4ToGalleryAndShareUri(
        context: Context,
        cachedMp4: File
    ): Result<RenderedReelOutput> {
        val fileName = "ViralBoost_Reel_${System.currentTimeMillis()}.mp4"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/ViralBoostAI")
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val mediaUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                if (mediaUri != null) {
                    resolver.openOutputStream(mediaUri)?.use { out ->
                        FileInputStream(cachedMp4).use { input ->
                            input.copyTo(out)
                        }
                    }
                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    resolver.update(mediaUri, values, null, null)
                    return Result.success(
                        RenderedReelOutput(
                            shareUri = mediaUri,
                            gallerySaved = true,
                            displayPath = "Movies/ViralBoostAI/$fileName"
                        )
                    )
                }
            } catch (_: Throwable) {
            }
        }

        // Fallback for API < 29 or scoped fallback: save under app Movies dir + expose FileProvider content:// URI
        val moviesDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: context.cacheDir, "ViralBoostAI").apply {
            mkdirs()
        }
        val dest = File(moviesDir, fileName)
        cachedMp4.copyTo(dest, overwrite = true)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", dest)
        return Result.success(
            RenderedReelOutput(
                shareUri = uri,
                gallerySaved = true,
                displayPath = dest.name
            )
        )
    }

    private fun drawFrameToCanvas(
        canvas: Canvas,
        elapsedSec: Float,
        scenes: List<StudioSceneItem>,
        sceneBitmaps: List<Bitmap>,
        categoryLabel: String,
        accentColorInt: Int,
        secondaryColorInt: Int
    ) {
        val w = WIDTH.toFloat()
        val h = HEIGHT.toFloat()
        val sceneDur = 3.0f
        val count = scenes.size.coerceAtLeast(1)
        val totalDur = sceneDur * count
        val t = (elapsedSec % totalDur).coerceAtLeast(0f)
        val sceneIdx = (t / sceneDur).toInt().coerceIn(0, count - 1)
        val prevIdx = (sceneIdx - 1 + count) % count
        val localSec = t - sceneIdx * sceneDur
        val progress = (localSec / sceneDur).coerceIn(0f, 1f)
        val fadeAlpha = if (localSec < 0.45f) (localSec / 0.45f).coerceIn(0f, 1f) else 1f

        canvas.drawColor(Color.rgb(5, 6, 13))

        fun bitmapFor(idx: Int): Bitmap? {
            if (sceneBitmaps.isEmpty()) return null
            return sceneBitmaps.getOrNull(idx) ?: sceneBitmaps.firstOrNull()
        }

        if (fadeAlpha < 1f && count > 1) {
            val prevBmp = bitmapFor(prevIdx)
            if (prevBmp != null) {
                drawBitmapKenBurns(canvas, prevBmp, w, h, 0.90f + localSec * 0.1f, scenes[prevIdx].cameraMotion, 255)
            } else {
                drawProceduralCinemaBackground(canvas, w, h, elapsedSec, prevIdx, accentColorInt, secondaryColorInt, 255)
            }
        }

        val curBmp = bitmapFor(sceneIdx)
        val curAlpha = (fadeAlpha * 255).toInt().coerceIn(0, 255)
        if (curBmp != null) {
            drawBitmapKenBurns(canvas, curBmp, w, h, progress, scenes[sceneIdx].cameraMotion, curAlpha)
        } else {
            drawProceduralCinemaBackground(canvas, w, h, elapsedSec, sceneIdx, accentColorInt, secondaryColorInt, curAlpha)
        }

        // Ambient floating particles
        val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        for (p in 0 until 14) {
            val px = ((p * 114f + elapsedSec * (20f + (p % 4) * 8f)) % (w + 40f)) - 20f
            val py = (((p * 182f - elapsedSec * (16f + (p % 3) * 6f)) % h) + h) % h
            val pr = 2.2f + (p % 3) * 1.4f
            particlePaint.color = if (p % 2 == 0) Color.argb(65, 255, 255, 255) else applyAlpha(accentColorInt, 75)
            canvas.drawCircle(px, py, pr, particlePaint)
        }

        // Subtle cinema top/bottom vignettes
        val topVig = Paint().apply {
            shader = LinearGradient(0f, 0f, 0f, h * 0.22f, Color.argb(185, 4, 6, 14), Color.TRANSPARENT, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, w, h * 0.22f, topVig)

        val botVig = Paint().apply {
            shader = LinearGradient(0f, h * 0.56f, 0f, h, Color.TRANSPARENT, Color.argb(235, 4, 6, 14), Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, h * 0.56f, w, h, botVig)

        // 4-Segment top Reels progress bar
        val segGap = 10f
        val segW = (w - 56f - segGap * (count - 1)) / count
        val barBgPaint = Paint().apply { color = Color.argb(70, 255, 255, 255) }
        val barFillPaint = Paint().apply { color = accentColorInt }
        for (i in 0 until count) {
            val sx = 28f + i * (segW + segGap)
            canvas.drawRoundRect(RectF(sx, 24f, sx + segW, 30f), 3f, 3f, barBgPaint)
            val fill = when {
                i < sceneIdx -> 1f
                i == sceneIdx -> progress
                else -> 0f
            }
            if (fill > 0f) {
                canvas.drawRoundRect(RectF(sx, 24f, sx + segW * fill, 30f), 3f, 3f, barFillPaint)
            }
        }

        // Scene badge + Hindi/Hinglish StaticLayout caption card at lower third
        val cardLeft = 40f
        val cardRight = w - 40f
        val cardTop = h * 0.70f
        val cardBottom = h * 0.85f
        val cardRect = RectF(cardLeft, cardTop, cardRight, cardBottom)

        val cardBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(195, 7, 9, 20) }
        val cardBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = applyAlpha(accentColorInt, 180)
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(cardRect, 28f, 28f, cardBg)
        canvas.drawRoundRect(cardRect, 28f, 28f, cardBorder)

        val tagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColorInt
            textSize = 21f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "SCENE ${sceneIdx + 1}/$count • ${categoryLabel.uppercase()}",
            w / 2f,
            cardTop + 38f,
            tagPaint
        )

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 31f
            typeface = Typeface.DEFAULT_BOLD
        }
        val caption = scenes[sceneIdx].caption
        val layoutWidth = (cardRight - cardLeft - 48f).toInt().coerceAtLeast(100)
        val staticLayout = StaticLayout.Builder
            .obtain(caption, 0, caption.length, textPaint, layoutWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setMaxLines(3)
            .build()

        canvas.save()
        canvas.translate(cardLeft + 24f, cardTop + 56f)
        staticLayout.draw(canvas)
        canvas.restore()

        // Bottom waveform bars
        val bars = 20
        val bw = (w - 180f) / bars
        val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        for (b in 0 until bars) {
            val bh = 10f + abs(sin(elapsedSec * 7f + b * 0.7f)) * 28f
            wavePaint.color = if (b % 2 == 0) Color.argb(150, 0, 229, 255) else applyAlpha(accentColorInt, 190)
            val bx = 90f + b * bw + 3f
            canvas.drawRoundRect(RectF(bx, h - 108f - bh / 2f, bx + bw - 6f, h - 108f + bh / 2f), 4f, 4f, wavePaint)
        }
    }

    private fun drawBitmapKenBurns(
        canvas: Canvas,
        bmp: Bitmap,
        w: Float,
        h: Float,
        progress: Float,
        motion: String,
        alpha: Int
    ) {
        val bw = bmp.width.toFloat().coerceAtLeast(1f)
        val bh = bmp.height.toFloat().coerceAtLeast(1f)
        val baseScale = maxOf(w / bw, h / bh)
        val p = progress.coerceIn(0f, 1f)
        val eased = if (p < 0.5f) 2f * p * p else 1f - (-2f * p + 2f) * (-2f * p + 2f) / 2f
        val zoom = when (motion) {
            "zoom-out" -> 1.36f - 0.28f * eased
            "zoom-in" -> 1.04f + 0.32f * eased
            else -> 1.22f + 0.10f * eased
        }
        val shiftX = when (motion) {
            "pan-right" -> (eased - 0.5f) * -96f
            "pan-left" -> (eased - 0.5f) * 96f
            else -> 0f
        }
        val shiftY = when (motion) {
            "zoom-in" -> (0.5f - eased) * 44f
            "zoom-out" -> (eased - 0.5f) * 44f
            else -> sin(p * Math.PI).toFloat() * -18f
        }
        val angleDeg = when (motion) {
            "pan-right" -> (eased - 0.5f) * 2.4f
            "pan-left" -> (0.5f - eased) * 2.4f
            else -> sin(p * Math.PI).toFloat() * 1.2f
        }
        val dw = bw * baseScale * zoom
        val dh = bh * baseScale * zoom
        val left = (w - dw) / 2f + shiftX
        val top = (h - dh) / 2f + shiftY
        val paint = Paint(Paint.FILTER_BITMAP_FLAG).apply {
            this.alpha = alpha
        }
        canvas.save()
        canvas.rotate(angleDeg, w / 2f, h / 2f)
        canvas.drawBitmap(bmp, null, RectF(left, top, left + dw, top + dh), paint)
        canvas.restore()
    }

    private fun drawProceduralCinemaBackground(
        canvas: Canvas,
        w: Float,
        h: Float,
        elapsedSec: Float,
        sceneIdx: Int,
        accentColorInt: Int,
        secondaryColorInt: Int,
        alpha: Int
    ) {
        if (alpha <= 2) return
        val saveCount = canvas.saveLayerAlpha(0f, 0f, w, h, alpha)
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, w, h,
                intArrayOf(
                    Color.rgb(12, 16, 40),
                    applyAlpha(secondaryColorInt, 210),
                    Color.rgb(5, 6, 14)
                ),
                floatArrayOf(0f, 0.52f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        val drift = sin(elapsedSec * 0.8f + sceneIdx) * 36f
        val lightX = listOf(520f, 210f, 480f, 360f)[sceneIdx % 4] + drift
        val lightY = listOf(340f, 440f, 300f, 380f)[sceneIdx % 4]
        val glowPaint = Paint().apply {
            shader = RadialGradient(
                lightX, lightY, 500f,
                intArrayOf(Color.argb(180, 255, 255, 255), applyAlpha(accentColorInt, 140), Color.TRANSPARENT),
                floatArrayOf(0f, 0.3f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, glowPaint)

        // Horizon silhouette
        val horizonY = 760f + (sceneIdx % 2) * 40f
        val hillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(175, 8, 10, 22) }
        val hillPath = Path().apply {
            moveTo(0f, h)
            lineTo(0f, horizonY)
            quadTo(180f, horizonY - 90f, 360f, horizonY - 20f)
            quadTo(560f, horizonY + 45f, w, horizonY - 50f)
            lineTo(w, h)
            close()
        }
        canvas.drawPath(hillPath, hillPaint)
        canvas.restoreToCount(saveCount)
    }

    private fun applyAlpha(colorInt: Int, alpha: Int): Int {
        return Color.argb(alpha.coerceIn(0, 255), Color.red(colorInt), Color.green(colorInt), Color.blue(colorInt))
    }
}
