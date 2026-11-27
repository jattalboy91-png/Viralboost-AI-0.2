package com.example.data.service

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "GeminiSocialService"

data class SocialContentResult(
    val fullResponse: String,
    val hook: String,
    val bodyCaption: String,
    val callToAction: String,
    val hashtags: List<String>,
    val platformTips: String,
    val isRealApiCall: Boolean,
    val errorMessage: String? = null
)

data class StudioSceneItem(
    val timeRange: String,
    val caption: String,
    val visualPrompt: String,
    val cameraMotion: String
)

data class StudioVideoPackage(
    val title: String,
    val hook: String,
    val script: String,
    val voiceoverText: String,
    val callToAction: String,
    val hashtags: List<String>,
    val scenes: List<StudioSceneItem>,
    val isRealApiCall: Boolean,
    val modelUsed: String = "gemini-3.8-flash",
    val errorMessage: String? = null
)

sealed interface GeminiUiState {
    data object Idle : GeminiUiState
    data object Loading : GeminiUiState
    data class Streaming(val text: String) : GeminiUiState
    data class Success(val result: SocialContentResult) : GeminiUiState
    data class Error(val message: String) : GeminiUiState
}

/**
 * Firebase AI Logic service powered by `gemini-3.8-flash` with automatic
 * `gemini-3.5-flash-lite` cascade fallback on transient high-demand (500) spikes,
 * plus `gemini-nano-banana-2.1` image synthesis support. Requires no client-side API key.
 */
class GeminiSocialContentService {

    companion object {
        val TEXT_MODEL_CASCADE = listOf("gemini-3.8-flash", "gemini-3.5-flash-lite")
    }

    // Structured JSON schema for social content generation
    private val socialContentSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "hook" to Schema.string(
                    description = "2-3 high-retention pattern-interrupt hooks for the first 3 seconds"
                ),
                "bodyCaption" to Schema.string(
                    description = "Main viral video script or post caption with pacing and emojis"
                ),
                "callToAction" to Schema.string(
                    description = "High-converting Call To Action driving comments, saves, and shares"
                ),
                "hashtags" to Schema.array(
                    items = Schema.string(),
                    description = "8-15 high-ranking viral hashtags starting with #"
                ),
                "platformTips" to Schema.string(
                    description = "Actionable algorithmic strategy on sound selection and posting window"
                )
            )
        )
    }

    // Structured JSON schema for 4-scene 9:16 video studio
    private val studioScenesSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "title" to Schema.string(
                    description = "Catchy viral Reel title in Hindi/Hinglish with emoji"
                ),
                "hook" to Schema.string(
                    description = "Scroll-stopping 3-second opening hook line"
                ),
                "script" to Schema.string(
                    description = "Full timestamped script across 4 scenes"
                ),
                "voiceoverText" to Schema.string(
                    description = "Clean spoken Hindi/Hinglish narration without bracket stage tags"
                ),
                "callToAction" to Schema.string(
                    description = "High-engagement closing Call-to-Action"
                ),
                "hashtags" to Schema.array(
                    items = Schema.string(),
                    description = "8-12 trending viral hashtags starting with #"
                ),
                "scenes" to Schema.array(
                    items = Schema.obj(
                        properties = mapOf(
                            "timeRange" to Schema.string(description = "0-3s, 3-6s, 6-9s, or 9-12s"),
                            "caption" to Schema.string(description = "Short punchy on-screen Reel subtitle in Hindi/Hinglish"),
                            "visualPrompt" to Schema.string(description = "Photorealistic 9:16 cinema scene visual description"),
                            "cameraMotion" to Schema.string(description = "One of: zoom-in, pan-right, zoom-out, pan-left")
                        )
                    ),
                    description = "Exactly 4 timed scenes for a 12-second 9:16 Reel"
                )
            )
        )
    }

    private val cachedStructuredModels = java.util.concurrent.ConcurrentHashMap<String, GenerativeModel>()
    private val cachedStudioModels = java.util.concurrent.ConcurrentHashMap<String, GenerativeModel>()
    private val cachedStreamingModels = java.util.concurrent.ConcurrentHashMap<String, GenerativeModel>()

    private fun structuredModelFor(modelName: String): GenerativeModel {
        return cachedStructuredModels.getOrPut(modelName) {
            val config = generationConfig {
                responseMimeType = "application/json"
                responseSchema = socialContentSchema
                temperature = 0.75f
            }
            Firebase.ai.generativeModel(modelName = modelName, generationConfig = config)
        }
    }

    private fun studioModelFor(modelName: String): GenerativeModel {
        return cachedStudioModels.getOrPut(modelName) {
            val config = generationConfig {
                responseMimeType = "application/json"
                responseSchema = studioScenesSchema
                temperature = 0.85f
            }
            Firebase.ai.generativeModel(modelName = modelName, generationConfig = config)
        }
    }

    private fun streamingModelFor(modelName: String): GenerativeModel {
        return cachedStreamingModels.getOrPut(modelName) {
            Firebase.ai.generativeModel(modelName = modelName)
        }
    }

    private val imageModel by lazy {
        val config = generationConfig {
            responseModalities = listOf(ResponseModality.IMAGE)
        }
        Firebase.ai.generativeModel(
            modelName = "gemini-nano-banana-2.1",
            generationConfig = config
        )
    }

    private fun logError(msg: String, err: Throwable? = null) {
        try {
            Log.e(TAG, msg, err)
        } catch (_: Throwable) {
            println("[$TAG] $msg: ${err?.message}")
        }
    }

    /**
     * Generates structured viral social media content using Firebase AI Logic
     * (`gemini-3.8-flash` -> `gemini-3.5-flash-lite` fallback) with `Schema.obj(...)`.
     */
    suspend fun generateSocialContent(
        prompt: String,
        platform: String,
        tone: String,
        contentType: String,
        targetAudience: String = "Content Creators & General Public"
    ): SocialContentResult = withContext(Dispatchers.IO) {
        val fullPrompt = buildPrompt(prompt, platform, tone, contentType, targetAudience)
        var lastError: Throwable? = null

        for (modelName in TEXT_MODEL_CASCADE) {
            try {
                val model = structuredModelFor(modelName)
                val response = withTimeout(14_000L) {
                    model.generateContent(fullPrompt)
                }
                val jsonString = response.text
                    ?: throw IllegalStateException("$modelName returned an empty response.")
                return@withContext parseStructuredJsonResponse(jsonString)
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) {
                    lastError = e
                    logError("$modelName timed out", e)
                } else {
                    throw e
                }
            } catch (t: Throwable) {
                lastError = t
                logError("$modelName failed, checking fallback...", t)
            }
        }

        produceSmartFallback(prompt, platform, tone, contentType).copy(
            isRealApiCall = false,
            errorMessage = lastError?.localizedMessage ?: "Firebase AI generation failed."
        )
    }

    /**
     * Generates a complete 4-scene Hindi/Hinglish AI Video package across Prompt, Photo, and Script modes,
     * cascading `gemini-3.8-flash` -> `gemini-3.5-flash-lite` on transient high-demand spikes.
     */
    suspend fun generateStudioVideoPackage(
        mode: String,
        topic: String,
        customScript: String,
        categoryTitle: String,
        beatName: String,
        photoBitmap: Bitmap? = null
    ): StudioVideoPackage = withContext(Dispatchers.IO) {
        val safeTopic = topic.trim().ifBlank { "$categoryTitle Viral Reel" }
        val instruction = when (mode) {
            "photo" -> buildString {
                appendLine("Analyze the attached photo carefully and create a viral 4-scene Hindi/Hinglish 9:16 Reel in category \"$categoryTitle\" synced to \"$beatName\".")
                if (topic.isNotBlank()) appendLine("Creator Mood / Direction: ${topic.trim()}")
                append("Write a natural spoken Hindi/Hinglish voiceover matching the photo's subject and mood, split into 4 timed scenes (0-3s, 3-6s, 6-9s, 9-12s), plus 10 trending #hashtags.")
            }
            "script" -> buildString {
                appendLine("Split this creator's custom Hindi/Hinglish script/shayari/story for category \"$categoryTitle\" synced to \"$beatName\" into 4 timed 9:16 Reel scenes:")
                appendLine(customScript.trim())
                append("Preserve the user's meaning across voiceoverText and the 4 scene captions, generate photorealistic visualPrompt cues, title, hook, CTA, and 10 trending #hashtags.")
            }
            else -> buildString {
                appendLine("Write a viral 4-scene Hindi/Hinglish 9:16 Reel package for category \"$categoryTitle\" synced to \"$beatName\" on topic: \"$safeTopic\".")
                append("Include clickable title, hook, full script, smooth Hindi/Hinglish voiceoverText (no bracket tags), CTA, 10 trending #hashtags, and 4 scenes (0-3s, 3-6s, 6-9s, 9-12s) with punchy captions, cameraMotion, and photorealistic visualPrompt.")
            }
        }

        val scaledPhoto = photoBitmap?.let { bmp ->
            val maxSide = 768
            val scale = minOf(1f, maxSide.toFloat() / maxOf(1, bmp.width), maxSide.toFloat() / maxOf(1, bmp.height))
            if (scale < 0.99f) {
                Bitmap.createScaledBitmap(bmp, (bmp.width * scale).toInt().coerceAtLeast(1), (bmp.height * scale).toInt().coerceAtLeast(1), true)
            } else bmp
        }

        var lastError: Throwable? = null
        for (modelName in TEXT_MODEL_CASCADE) {
            try {
                val model = studioModelFor(modelName)
                val response = withTimeout(14_000L) {
                    if (mode == "photo" && scaledPhoto != null) {
                        model.generateContent(
                            content {
                                image(scaledPhoto)
                                text(instruction)
                            }
                        )
                    } else {
                        model.generateContent(instruction)
                    }
                }
                val jsonText = response.text ?: throw IllegalStateException("$modelName returned empty studio JSON.")
                return@withContext parseStudioPackageJson(jsonText, safeTopic, customScript, categoryTitle, modelName)
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) {
                    lastError = e
                    logError("$modelName studio scenes timed out", e)
                } else {
                    throw e
                }
            } catch (t: Throwable) {
                lastError = t
                logError("$modelName studio scenes failed, trying next model...", t)
            }
        }

        buildSmartStudioFallback(
            mode = mode,
            topic = safeTopic,
            customScript = customScript,
            categoryTitle = categoryTitle,
            errorMsg = lastError?.localizedMessage
        )
    }

    /**
     * Calls `gemini-nano-banana-2.1` to generate up to 4 photorealistic 9:16 scene stills.
     * Transparently returns any billing/quota/network error message without fabricating fake AI images.
     */
    suspend fun tryGenerateSceneImages(
        scenes: List<StudioSceneItem>,
        categoryTitle: String
    ): Pair<List<Bitmap>, String?> = withContext(Dispatchers.IO) {
        val bitmaps = mutableListOf<Bitmap>()
        var errMsg: String? = null
        for ((idx, scene) in scenes.take(4).withIndex()) {
            try {
                val prompt = "Vertical 9:16 cinematic film still, scene ${idx + 1}, $categoryTitle, ${scene.visualPrompt}, dramatic lighting, photorealistic, no text"
                val response = withTimeout(10_000L) {
                    imageModel.generateContent(prompt)
                }
                val bmp = response.candidates.firstOrNull()
                    ?.content?.parts?.filterIsInstance<ImagePart>()
                    ?.firstOrNull()?.image
                if (bmp != null) {
                    bitmaps.add(bmp)
                }
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) {
                    errMsg = "Image generation timed out"
                    break
                } else {
                    throw e
                }
            } catch (t: Throwable) {
                errMsg = t.localizedMessage ?: "Image model unavailable"
                logError("Nano Banana image generation skipped for scene $idx", t)
                break
            }
        }
        bitmaps to errMsg
    }

    /**
     * Streams live Gemini responses chunk-by-chunk (`gemini-3.8-flash` -> `gemini-3.5-flash-lite` fallback).
     */
    fun generateSocialContentStream(
        prompt: String,
        platform: String,
        tone: String,
        contentType: String,
        targetAudience: String = "Content Creators & General Public"
    ): Flow<String> = flow {
        val streamPrompt = buildStreamingPrompt(prompt, platform, tone, contentType, targetAudience)
        var lastError: Throwable? = null
        for (modelName in TEXT_MODEL_CASCADE) {
            val accumulated = StringBuilder()
            try {
                val model = streamingModelFor(modelName)
                withTimeout(60_000L) {
                    model.generateContentStream(streamPrompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        if (chunkText.isNotEmpty()) {
                            accumulated.append(chunkText)
                            emit(accumulated.toString())
                        }
                    }
                }
                if (accumulated.isNotEmpty()) return@flow
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) {
                    lastError = e
                } else {
                    throw e
                }
            } catch (t: Throwable) {
                lastError = t
                logError("Streaming failed on $modelName", t)
            }
        }
        throw (lastError ?: IllegalStateException("Firebase AI streaming failed."))
    }

    private fun parseStudioPackageJson(
        jsonString: String,
        safeTopic: String,
        customScript: String,
        categoryTitle: String,
        modelName: String
    ): StudioVideoPackage {
        val json = JSONObject(jsonString)
        val motions = listOf("zoom-in", "pan-right", "zoom-out", "pan-left")
        val rawScenes = json.optJSONArray("scenes")
        val fallbackLines = (json.optString("script").ifBlank { customScript.ifBlank { safeTopic } })
            .split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        val scenes = (0 until 4).map { idx ->
            val obj = if (rawScenes != null && idx < rawScenes.length()) rawScenes.optJSONObject(idx) else null
            val cap = obj?.optString("caption").orEmpty().ifBlank {
                fallbackLines.getOrElse(idx) { safeTopic }.take(95)
            }
            val vp = obj?.optString("visualPrompt").orEmpty().ifBlank {
                "Photorealistic 9:16 vertical scene ${idx + 1} for $categoryTitle: $safeTopic"
            }
            val motion = obj?.optString("cameraMotion").orEmpty().let { m ->
                if (m in motions) m else motions[idx]
            }
            StudioSceneItem(
                timeRange = obj?.optString("timeRange").orEmpty().ifBlank { "${idx * 3}-${(idx + 1) * 3}s" },
                caption = cap,
                visualPrompt = vp,
                cameraMotion = motion
            )
        }

        val tagsArr = json.optJSONArray("hashtags")
        val parsedTags = if (tagsArr != null) {
            (0 until tagsArr.length()).mapNotNull { idx ->
                val t = tagsArr.optString(idx).trim()
                if (t.isBlank()) null else if (t.startsWith("#")) t else "#$t"
            }
        } else emptyList()
        val hashtags = parsedTags.ifEmpty {
            listOf("#ViralReels", "#TrendingShorts", "#ViralBoostAI", "#HindiReels", "#ExplorePage")
        }

        val title = json.optString("title").ifBlank { "🔥 $safeTopic" }
        val hook = json.optString("hook").ifBlank { scenes.first().caption }
        val script = json.optString("script").ifBlank {
            scenes.joinToString("\n") { "[${it.timeRange}] ${it.caption}" }
        }
        val voiceoverText = json.optString("voiceoverText").ifBlank {
            scenes.joinToString(". ") { it.caption }
        }
        val cta = json.optString("cta").ifBlank { "वीडियो पसंद आए तो Save & Share ज़रूर करें! 🔥" }

        return StudioVideoPackage(
            title = title,
            hook = hook,
            script = script,
            voiceoverText = voiceoverText,
            callToAction = cta,
            hashtags = hashtags,
            scenes = scenes,
            isRealApiCall = true,
            modelUsed = modelName,
            errorMessage = null
        )
    }

    private fun buildSmartStudioFallback(
        mode: String,
        topic: String,
        customScript: String,
        categoryTitle: String,
        errorMsg: String?
    ): StudioVideoPackage {
        val motions = listOf("zoom-in", "pan-right", "zoom-out", "pan-left")
        val shortTopic = topic.take(65)
        val captions = when {
            mode == "script" && customScript.isNotBlank() -> {
                val lines = customScript.split(Regex("\\n+|(?<=[।.!?])\\s+"))
                    .map { it.replace(Regex("^\\[[^\\]]+\\]\\s*"), "").trim() }
                    .filter { it.isNotEmpty() }
                (0 until 4).map { i -> lines.getOrElse(i) { lines.getOrElse(i % lines.size.coerceAtLeast(1)) { shortTopic } }.take(100) }
            }
            mode == "photo" -> listOf(
                "इस तस्वीर के पीछे छुपी एक अनकही कहानी... 👀",
                "$shortTopic — हर पल में एक नया एहसास ✨",
                "ज़िंदगी की असली खूबसूरती यहीं नज़र आती है ❤️",
                "अच्छा लगा तो अभी Save & Share ज़रूर करें! 🔥"
            )
            else -> listOf(
                "रुक जाओ! आज $shortTopic आपकी सोच बदल देगा 🔥",
                "$categoryTitle — हर बड़ी कामयाबी छोटे कदम से शुरू होती है ⚡",
                "भीड़ से अलग चलना सीखो — रिज़ल्ट खुद बोलेगा 🚀",
                "बात दिल को लगी हो तो अभी Share & Follow करें! ❤️"
            )
        }
        val scenes = (0 until 4).map { i ->
            StudioSceneItem(
                timeRange = "${i * 3}-${(i + 1) * 3}s",
                caption = captions[i],
                visualPrompt = "Photorealistic 9:16 scene ${i + 1} for $categoryTitle: $topic",
                cameraMotion = motions[i]
            )
        }
        val script = scenes.joinToString("\n") { "[${it.timeRange}] ${it.caption}" }
        return StudioVideoPackage(
            title = "🔥 $shortTopic",
            hook = captions[0],
            script = script,
            voiceoverText = captions.joinToString(". "),
            callToAction = "वीडियो पसंद आए तो अभी सेव और शेयर करें! 🔥",
            hashtags = listOf("#ViralReels", "#TrendingShorts", "#HindiReels", "#ViralBoostAI", "#ExplorePage", "#FYP"),
            scenes = scenes,
            isRealApiCall = false,
            modelUsed = "Smart Studio Fallback",
            errorMessage = errorMsg
        )
    }

    private fun parseStructuredJsonResponse(jsonString: String): SocialContentResult {
        val json = JSONObject(jsonString)
        val hook = json.optString("hook", "Stop scrolling! Here is what 99% of creators miss...")
        val bodyCaption = json.optString("bodyCaption", jsonString)
        val callToAction = json.optString("callToAction", "Save this post and share with a fellow creator!")
        val hashtagsArray = json.optJSONArray("hashtags")
        val rawHashtags = if (hashtagsArray != null) {
            (0 until hashtagsArray.length()).mapNotNull { idx ->
                val tag = hashtagsArray.optString(idx).trim()
                if (tag.isBlank()) null else if (tag.startsWith("#")) tag else "#$tag"
            }
        } else {
            emptyList()
        }
        val hashtags = if (rawHashtags.isNotEmpty()) {
            rawHashtags
        } else {
            listOf("#ViralBoostAI", "#CreatorEconomy", "#TrendingReels", "#GrowthHacks", "#ViralShorts")
        }
        val platformTips = json.optString(
            "platformTips",
            "Post during peak hours (6:30 PM - 9:00 PM) with high-retention audio."
        )

        val formattedFullText = buildString {
            appendLine("🔥 VIRAL HOOK:")
            appendLine(hook)
            appendLine()
            appendLine("🎬 SCRIPT & CAPTION:")
            appendLine(bodyCaption)
            appendLine()
            appendLine("📣 CALL TO ACTION:")
            appendLine(callToAction)
            appendLine()
            appendLine("#️⃣ VIRAL HASHTAGS:")
            appendLine(hashtags.joinToString(" "))
            appendLine()
            appendLine("📈 ALGORITHMIC STRATEGY:")
            append(platformTips)
        }

        return SocialContentResult(
            fullResponse = formattedFullText,
            hook = hook,
            bodyCaption = bodyCaption,
            callToAction = callToAction,
            hashtags = hashtags,
            platformTips = platformTips,
            isRealApiCall = true,
            errorMessage = null
        )
    }

    private fun buildPrompt(
        prompt: String,
        platform: String,
        tone: String,
        contentType: String,
        targetAudience: String
    ): String {
        return """
            You are a world-class viral social media strategist and algorithmic growth engineer.
            Craft high-converting, zero-drop social media content for: "$prompt"
            - Target Platform: $platform
            - Content Format: $contentType
            - Tone: $tone
            - Target Audience: $targetAudience
            Provide a magnetic 3-second hook, engaging script/caption, high-converting CTA, 10+ trending hashtags starting with #, and platform growth strategy.
        """.trimIndent()
    }

    private fun buildStreamingPrompt(
        prompt: String,
        platform: String,
        tone: String,
        contentType: String,
        targetAudience: String
    ): String {
        return """
            You are a world-class viral social media strategist.
            Create a complete viral content package for "$prompt" on $platform ($tone tone, $contentType format, audience: $targetAudience).
            Include:
            1. 3-Second Pattern-Interrupt Hook
            2. Full Script / Caption with pacing & emojis
            3. High-Converting Call-to-Action (CTA)
            4. 10+ Trending Viral Hashtags
            5. Algorithmic Posting Strategy
        """.trimIndent()
    }

    private fun produceSmartFallback(
        prompt: String,
        platform: String,
        tone: String,
        contentType: String
    ): SocialContentResult {
        val safePrompt = prompt.trim().ifBlank { "Top secrets to grow faster online" }
        val cleanTopic = safePrompt.take(40)

        val hook = """
            🔥 [Pattern Interrupt (0:00 - 0:03)]:
            "Stop scrolling if you care about $cleanTopic. 99% of people are doing this completely backwards..."
        """.trimIndent()

        val body = """
            📱 $platform Optimized Content ($tone • $contentType):
            
            Here's the exact framework for "$safePrompt":
            
            1️⃣ The Foundation:
            Focus on high-retention pacing and removing unnecessary fluff in the first 5 seconds.
            
            2️⃣ The Secret Lever:
            Show proof, reveal before/after contrast, and create curiosity loops that keep viewers watching till the end.
            
            3️⃣ Action Step:
            Implement this for 7 days straight without breaking consistency. Track your reach vs your saves.
        """.trimIndent()

        val cta = "🔖 Save this post so you can reference it when creating, and comment 'VIRAL' to get the full blueprint!"

        val tags = listOf(
            "#${platform.replace(" ", "").replace("/", "").lowercase()}",
            "#growthhacks",
            "#viralcontent",
            "#creatoreconomy",
            "#reelsviral",
            "#contentstrategy",
            "#algorithmsecrets",
            "#${cleanTopic.replace(Regex("[^A-Za-z0-9]"), "").lowercase().take(12).ifBlank { "trending" }}"
        )

        val tips = "📈 Pro Tip: For $platform, post between 6:30 PM - 9:00 PM local time. Reply to every comment in the first 30 minutes to boost algorithmic distribution."

        val fullText = """
            🔥 VIRAL HOOK:
            $hook

            🎬 SCRIPT & CAPTION:
            $body

            📣 CALL TO ACTION:
            $cta

            #️⃣ VIRAL HASHTAGS:
            ${tags.joinToString(" ")}

            📈 ALGORITHMIC STRATEGY:
            $tips
        """.trimIndent()

        return SocialContentResult(
            fullResponse = fullText,
            hook = hook,
            bodyCaption = body,
            callToAction = cta,
            hashtags = tags,
            platformTips = tips,
            isRealApiCall = false
        )
    }
}
