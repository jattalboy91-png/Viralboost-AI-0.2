package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.service.GeminiSocialContentService
import com.example.data.service.SocialContentResult
import com.example.data.service.StudioSceneItem
import com.example.data.service.StudioVideoPackage
import com.example.monetization.AdFormat
import com.example.monetization.AdManager
import com.example.monetization.AdNetwork
import com.example.monetization.MonetizationConstants
import com.example.ui.components.UniversalMediatedAdDock
import com.example.util.AndroidReelVideoRenderer
import com.example.util.RenderedReelOutput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

data class ViralVideoCategoryOption(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val emoji: String,
    val accentColor: Color,
    val secondaryColor: Color,
    val sampleHook: String,
    val defaultPrompt: String,
    val beatTrackName: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OneClickAiVideoHashtagFullScreen(
    onNavigateBack: () -> Unit,
    isVip: Boolean = false,
    userDiamonds: Int = 10,
    bannerAdUnitId: String = MonetizationConstants.ADMOB_BANNER_UNIT_ID,
    interstitialAdUnitId: String = MonetizationConstants.ADMOB_INTERSTITIAL_UNIT_ID,
    rewardedAdUnitId: String = MonetizationConstants.ADMOB_REWARDED_UNIT_ID,
    onUpgradeToVip: () -> Unit = {},
    onRedeemDiamondsForVip: () -> Unit = {},
    onRewardEarned: (diamonds: Int, coins: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val categories = remember {
        listOf(
            ViralVideoCategoryOption(
                id = "Haryanvi Comedy & Rap",
                titleHindi = "हरियाणवी रैप & स्वैग",
                titleEnglish = "Haryanvi Rap & Swag",
                emoji = "🎤",
                accentColor = Color(0xFFFFD700),
                secondaryColor = Color(0xFFFF5722),
                sampleHook = "देसी स्वैग और हरियाणवी बीट — रील देखते ही सब रुक जाएंगे! 🔥",
                defaultPrompt = "High energy Haryanvi rap attitude & desi swag viral reel with bass drop",
                beatTrackName = "Desi Bass Drop 808 • 140 BPM"
            ),
            ViralVideoCategoryOption(
                id = "Comedy & Fun",
                titleHindi = "देसी कॉमेडी & फन",
                titleEnglish = "Comedy & Pranks",
                emoji = "😂",
                accentColor = Color(0xFF00E5FF),
                secondaryColor = Color(0xFF7C4DFF),
                sampleHook = "जब दोस्त ने किया ऐसा मज़ाक — अंत तक देखो हंसी नहीं रुकेगी! 🤣",
                defaultPrompt = "Relatable Indian desi best friend comedy skit with funny twist ending",
                beatTrackName = "Funny Quirky Meme Beat • 128 BPM"
            ),
            ViralVideoCategoryOption(
                id = "Motivation & Hustle",
                titleHindi = "मोटिवेशन & सक्सेस",
                titleEnglish = "Motivation & Success",
                emoji = "💪",
                accentColor = Color(0xFFFF9100),
                secondaryColor = Color(0xFFD500F9),
                sampleHook = "आज की मेहनत कल तेरा नाम बनाएगी — कभी हार मत मानना! ⚡",
                defaultPrompt = "Powerful late night hustle & success mindset motivation short video",
                beatTrackName = "Epic Cinematic Rise • 120 BPM"
            ),
            ViralVideoCategoryOption(
                id = "Sci-Fi & Tech",
                titleHindi = "AI & टेक सीक्रेट्स",
                titleEnglish = "AI & Tech Secrets",
                emoji = "🚀",
                accentColor = Color(0xFF00E676),
                secondaryColor = Color(0xFF00B0FF),
                sampleHook = "ये 3 गुप्त AI टूल्स आपका घंटों का काम 10 सेकंड में कर देंगे! 🤖",
                defaultPrompt = "Top secret AI tools and futuristic smartphone hacks for creators",
                beatTrackName = "Cyberpunk Synthwave • 132 BPM"
            ),
            ViralVideoCategoryOption(
                id = "Vlogs & Lifestyle",
                titleHindi = "लाइफस्टाइल & मिनी व्लॉग",
                titleEnglish = "Mini Vlog & Aesthetic",
                emoji = "✨",
                accentColor = Color(0xFFFF4081),
                secondaryColor = Color(0xFF7C4DFF),
                sampleHook = "एक यादगार शाम और सुकून के पल — आज का मिनी व्लॉग! 🌸",
                defaultPrompt = "Aesthetic golden hour daily mini vlog with cinematic transitions",
                beatTrackName = "Lo-Fi Sunset Chill • 110 BPM"
            ),
            ViralVideoCategoryOption(
                id = "Story & Moral",
                titleHindi = "दिल छूने वाली कहानी",
                titleEnglish = "Heartwarming Story",
                emoji = "📖",
                accentColor = Color(0xFFF59E0B),
                secondaryColor = Color(0xFFE11D48),
                sampleHook = "एक गरीब लड़के की वो सीख जिसने करोड़पति का दिल जीत लिया... ❤️",
                defaultPrompt = "Emotional Hindi moral story short reel with twist ending and life lesson",
                beatTrackName = "Emotional Piano Strings • 96 BPM"
            ),
            ViralVideoCategoryOption(
                id = "Shayari & Status",
                titleHindi = "हिंदी शायरी & स्टेटस",
                titleEnglish = "Shayari & Sad Status",
                emoji = "🥀",
                accentColor = Color(0xFFFB7185),
                secondaryColor = Color(0xFF6D28D9),
                sampleHook = "चुप रहना हमारी आदत बन गई — वरना शिकायतें तो आज भी हज़ार हैं... 🌙",
                defaultPrompt = "Deep soulful Hindi shayari WhatsApp status reel with aesthetic rain mood",
                beatTrackName = "Midnight Acoustic Lo-Fi • 92 BPM"
            ),
            ViralVideoCategoryOption(
                id = "Viral Facts & Secrets",
                titleHindi = "चौकाने वाले फैक्ट्स",
                titleEnglish = "Mind-Blowing Facts",
                emoji = "🤯",
                accentColor = Color(0xFF38BDF8),
                secondaryColor = Color(0xFF1D4ED8),
                sampleHook = "99% लोग अपने फोन का ये छुपा हुआ फीचर आज तक नहीं जानते! 🔍",
                defaultPrompt = "Mind blowing psychology & technology secret facts in fast Hindi storytelling",
                beatTrackName = "Mystery Documentary Pulse • 118 BPM"
            ),
            ViralVideoCategoryOption(
                id = "Action & Gaming",
                titleHindi = "एक्शन & गेमिंग क्लिप्स",
                titleEnglish = "Action & Gaming",
                emoji = "🎮",
                accentColor = Color(0xFFE040FB),
                secondaryColor = Color(0xFF3D5AFE),
                sampleHook = "1 vs 4 क्लच मोमेंट — आखिरी सेकंड में पलटा पूरा गेम! 🔥",
                defaultPrompt = "Impossible 1v4 pro gaming clutch montage with neon VFX sync",
                beatTrackName = "Phonk Drift Overdrive • 150 BPM"
            )
        )
    }

    var studioMode by remember { mutableStateOf("prompt") } // "prompt" | "photo" | "script"
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var customTopicInput by remember { mutableStateOf("") }
    var customScriptInput by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedPhotoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var isGenerating by remember { mutableStateOf(false) }
    var generationProgress by remember { mutableFloatStateOf(0f) }
    var generationStageText by remember { mutableStateOf("") }

    val geminiService = remember { GeminiSocialContentService() }
    var isVideoPlaying by remember { mutableStateOf(true) }
    var previewElapsedSec by remember { mutableFloatStateOf(0f) }
    var generatedResult by remember { mutableStateOf<SocialContentResult?>(null) }
    var studioPackage by remember { mutableStateOf<StudioVideoPackage?>(null) }
    var sceneBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var renderedVideoOutput by remember { mutableStateOf<RenderedReelOutput?>(null) }
    var isRenderingMp4 by remember { mutableStateOf(false) }
    var mp4RenderProgress by remember { mutableFloatStateOf(0f) }
    var voiceEnabled by remember { mutableStateOf(true) }
    var isVoiceSpeaking by remember { mutableStateOf(false) }
    var statusBannerMessage by remember { mutableStateOf<String?>(null) }

    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale("hi", "IN")
                engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isVoiceSpeaking = true
                    }
                    override fun onDone(utteranceId: String?) {
                        isVoiceSpeaking = false
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        isVoiceSpeaking = false
                    }
                })
            }
        }
        ttsInstance = engine
        onDispose {
            try {
                engine.stop()
                engine.shutdown()
            } catch (_: Throwable) {}
        }
    }

    val speakHindiVoiceover: (String) -> Unit = { rawText ->
        val clean = rawText.replace(Regex("\\[[^\\]]+\\]"), " ").replace(Regex("\\s+"), " ").trim()
        if (clean.isNotBlank()) {
            try {
                ttsInstance?.language = Locale("hi", "IN")
                ttsInstance?.setSpeechRate(1.02f)
                ttsInstance?.speak(clean.take(500), TextToSpeech.QUEUE_FLUSH, null, "viralboost_vo")
                isVoiceSpeaking = true
            } catch (_: Throwable) {
                isVoiceSpeaking = false
            }
        }
    }

    val stopVoiceover: () -> Unit = {
        try { ttsInstance?.stop() } catch (_: Throwable) {}
        isVoiceSpeaking = false
    }

    // =========================================================================
    // 📺 INTERSTITIAL AD & REWARDED AD STATE FOR EVERY OPTION CLICK
    // =========================================================================
    var showOptionInterstitialAd by remember { mutableStateOf(false) }
    var activeAdTriggerReason by remember { mutableStateOf("AI Video Option Selected") }
    var pendingOptionAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    var showStudioRewardedAd by remember { mutableStateOf(false) }
    var rewardedAdBonusUnlocked by remember { mutableStateOf(false) }

    BackHandler {
        if (showOptionInterstitialAd) {
            showOptionInterstitialAd = false
            val action = pendingOptionAction
            pendingOptionAction = null
            action?.invoke()
        } else if (showStudioRewardedAd) {
            showStudioRewardedAd = false
        } else {
            onNavigateBack()
        }
    }

    /**
     * Intercepts ANY option click inside this feature:
     * If user is NOT VIP, triggers an Interstitial Ad first and shows clear Ad-Free subscription CTA.
     */
    val triggerOptionWithInterstitialAd: (reason: String, action: () -> Unit) -> Unit = { reason, action ->
        if (isVip) {
            action()
        } else {
            activeAdTriggerReason = reason
            AdManager.triggerMilestoneInterstitial(
                context = context,
                isVip = false,
                milestoneName = "1-Click AI Studio: $reason",
                interstitialUnitId = interstitialAdUnitId,
                onShowInterstitialFallback = {
                    pendingOptionAction = action
                    showOptionInterstitialAd = true
                },
                onComplete = {
                    action()
                }
            )
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            studioMode = "photo"
            coroutineScope.launch(Dispatchers.IO) {
                val bmp = try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                } catch (_: Throwable) {
                    null
                }
                withContext(Dispatchers.Main) {
                    uploadedPhotoBitmap = bmp
                    if (bmp != null) {
                        sceneBitmaps = listOf(bmp, bmp, bmp, bmp)
                    }
                    renderedVideoOutput = null
                    statusBannerMessage = "📸 फोटो लोड हो गई! अब '1-Click Generate' दबाकर Photo-to-AI Video बनाएं।"
                }
            }
        }
    }

    // Animate 12-second 4-scene playback clock smoothly while preview is playing
    LaunchedEffect(isVideoPlaying) {
        while (isVideoPlaying) {
            delay(50L)
            previewElapsedSec = (previewElapsedSec + 0.05f) % 12f
        }
    }

    // Load initial 4-scene storyboard for the default category
    LaunchedEffect(Unit) {
        val cat = categories.first()
        val pkg = geminiService.generateStudioVideoPackage(
            mode = "prompt",
            topic = cat.defaultPrompt,
            customScript = "",
            categoryTitle = "${cat.titleHindi} (${cat.titleEnglish})",
            beatName = cat.beatTrackName
        )
        studioPackage = pkg
        generatedResult = SocialContentResult(
            fullResponse = "${pkg.hook}\n\n${pkg.script}",
            hook = pkg.hook,
            bodyCaption = pkg.script,
            callToAction = pkg.callToAction,
            hashtags = pkg.hashtags,
            platformTips = "4-Scene 9:16 Cinema Ready (${pkg.modelUsed})",
            isRealApiCall = pkg.isRealApiCall,
            errorMessage = pkg.errorMessage
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "studio_glow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val executeOneClickGenerate: () -> Unit = {
        if (!isGenerating) {
            if (studioMode == "photo" && uploadedPhotoBitmap == null) {
                statusBannerMessage = "📸 पहले गैलरी से अपनी फोटो चुनें!"
                Toast.makeText(context, "Please select a photo first!", Toast.LENGTH_SHORT).show()
            } else if (studioMode == "script" && customScriptInput.isBlank()) {
                statusBannerMessage = "📝 पहले अपनी स्क्रिप्ट या शायरी लिखें!"
                Toast.makeText(context, "Please paste your script first!", Toast.LENGTH_SHORT).show()
            } else {
                isGenerating = true
                renderedVideoOutput = null
                stopVoiceover()
                generationProgress = 0.25f
                generationStageText = when (studioMode) {
                    "photo" -> "📸 Gemini Vision आपकी फोटो समझकर 4-Scene वीडियो लिख रहा है..."
                    "script" -> "📝 Gemini आपकी स्क्रिप्ट को 4 सिनेमैटिक सीन में बदल रहा है..."
                    else -> "✨ Gemini 3.8 / 3.5 Flash 4-Scene AI वीडियो और वायरल हैशटैग बना रहा है..."
                }
                statusBannerMessage = null

                coroutineScope.launch {
                    val effectiveTopic = customTopicInput.trim().ifEmpty {
                        if (studioMode == "script") {
                            customScriptInput.trim().lineSequence().firstOrNull().orEmpty().take(70)
                        } else {
                            "${selectedCategory.defaultPrompt} (${selectedCategory.titleHindi})"
                        }
                    }

                    val pkg = geminiService.generateStudioVideoPackage(
                        mode = studioMode,
                        topic = effectiveTopic,
                        customScript = customScriptInput,
                        categoryTitle = "${selectedCategory.titleHindi} (${selectedCategory.titleEnglish})",
                        beatName = selectedCategory.beatTrackName,
                        photoBitmap = if (studioMode == "photo") uploadedPhotoBitmap else null
                    )

                    generationProgress = 0.68f
                    generationStageText = "🎞️ 4-Scene 9:16 Cinema स्टिल्स और हैशटैग्स तैयार हो रहे हैं..."

                    var imageNote: String? = null
                    val nextBitmaps = if (studioMode == "photo" && uploadedPhotoBitmap != null) {
                        val photo = uploadedPhotoBitmap!!
                        listOf(photo, photo, photo, photo)
                    } else if (pkg.isRealApiCall) {
                        val (genBmps, err) = geminiService.tryGenerateSceneImages(
                            scenes = pkg.scenes,
                            categoryTitle = selectedCategory.titleEnglish
                        )
                        imageNote = err
                        genBmps
                    } else {
                        emptyList()
                    }

                    sceneBitmaps = nextBitmaps
                    studioPackage = pkg
                    generatedResult = SocialContentResult(
                        fullResponse = "${pkg.hook}\n\n${pkg.script}\n\n${pkg.hashtags.joinToString(" ")}",
                        hook = pkg.hook,
                        bodyCaption = pkg.script,
                        callToAction = pkg.callToAction,
                        hashtags = pkg.hashtags,
                        platformTips = imageNote ?: "4-Scene 9:16 Cinema Engine (${pkg.modelUsed})",
                        isRealApiCall = pkg.isRealApiCall,
                        errorMessage = pkg.errorMessage
                    )
                    generationProgress = 1.0f
                    isGenerating = false
                    isVideoPlaying = true
                    previewElapsedSec = 0f

                    statusBannerMessage = if (pkg.isRealApiCall) {
                        "🎬 ${pkg.modelUsed} ने 4-Scene AI वीडियो & वायरल हैशटैग्स तैयार कर दिए! नीचे 'गैलरी में सेव करें' पर .MP4 डाउनलोड करें।"
                    } else {
                        "🎞️ Smart Studio 4-Scene AI वीडियो तैयार! (Live AI busy — फिर भी पूरा वीडियो और हैशटैग्स रेडी हैं)"
                    }
                    if (voiceEnabled) {
                        speakHindiVoiceover(pkg.voiceoverText)
                    }
                }
            }
        }
    }

    val renderAndSaveOrShareMp4: (shareAfterRender: Boolean) -> Unit = { shareAfterRender ->
        if (!isRenderingMp4) {
            isRenderingMp4 = true
            mp4RenderProgress = 0.05f
            statusBannerMessage = "🎬 720×1280 @ 30FPS HD .MP4 वीडियो रेंडर हो रहा है..."
            coroutineScope.launch {
                val scenes = studioPackage?.scenes ?: emptyList()
                val res = AndroidReelVideoRenderer.renderAndSaveMp4(
                    context = context,
                    scenes = scenes,
                    sceneBitmaps = sceneBitmaps,
                    categoryLabel = selectedCategory.titleEnglish,
                    accentColorInt = selectedCategory.accentColor.toArgb(),
                    secondaryColorInt = selectedCategory.secondaryColor.toArgb(),
                    onProgress = { pct -> mp4RenderProgress = pct }
                )
                isRenderingMp4 = false
                res.onSuccess { output ->
                    renderedVideoOutput = output
                    statusBannerMessage = "✅ .MP4 वीडियो गैलरी (${output.displayPath}) में सेव हो गया!"
                    Toast.makeText(
                        context,
                        "✅ HD .MP4 Video saved to Gallery!",
                        Toast.LENGTH_LONG
                    ).show()
                    if (shareAfterRender) {
                        val pkgText = buildString {
                            appendLine("🔥 ${generatedResult?.hook ?: selectedCategory.sampleHook}")
                            appendLine()
                            appendLine(generatedResult?.bodyCaption ?: "")
                            appendLine()
                            append(generatedResult?.hashtags?.joinToString(" ") ?: "")
                        }
                        shareVideoFileToSocials(context, output.shareUri, pkgText)
                    }
                }.onFailure { err ->
                    statusBannerMessage = "⚠️ .MP4 Export Error: ${err.localizedMessage ?: "Unsupported codec"}"
                    Toast.makeText(context, "MP4 export failed: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070712))
            .testTag("one_click_ai_video_full_screen"),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = Color(0xFF070712),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0E0C24),
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("one_click_ai_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFF5722), Color(0xFFFFD700))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "1-Click AI Video & Viral हैशटैग",
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Full-Screen AI Reel Studio • Auto Viral Tags",
                                fontSize = 11.sp,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFF5722))
                                )
                            )
                            .clickable { onUpgradeToVip() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("top_bar_ad_free_vip_badge")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isVip) "VIP AD-FREE" else "GO AD-FREE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            // Persistent Bottom Mediated Banner Ad (BP_Banner_Android / AdMob) + Rewarded Ad Trigger (BP_Rewarded_Android / AdMob) + Quick Ad-Free Subscription Strip
            if (!isVip) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B091B))
                        .testTag("one_click_studio_bottom_banner_ad_container")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1F1235))
                            .clickable { onUpgradeToVip() }
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🚫 विज्ञापनों (Ads) से छुटकारा पाएं — 100% Ad-Free VIP लें!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = "अभी हटाएं →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00E5FF)
                        )
                    }
                    UniversalMediatedAdDock(
                        isVip = false,
                        admobBannerUnitId = bannerAdUnitId,
                        admobRewardedUnitId = rewardedAdUnitId,
                        featureLabel = "1-Click AI Video & Hashtag Studio",
                        onRewardEarned = onRewardEarned,
                        onUpgradeClick = onUpgradeToVip
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0E0C24),
                            Color(0xFF070712),
                            Color(0xFF100A26)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 0. PROMINENT AD-FREE SUBSCRIPTION DESCRIPTION & UPGRADE CARD
                if (!isVip) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF230F2E)),
                            border = BorderStroke(
                                2.dp,
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFF4081), Color(0xFF00E5FF))
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ad_free_subscription_description_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFFD700)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Block,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "🚫 बार-बार आने वाले Ads से छुटकारा पाएं!",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFFFD700)
                                            )
                                            Text(
                                                text = "100% Ad-Free VIP Subscription • Zero Interruption",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF00E5FF)
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFD50000))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "NO ADS",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "📌 साफ-साफ जानकारी: फ्री प्लान में इस फीचर के किसी भी ऑप्शन, कैटेगरी या बटन पर क्लिक करने पर Interstitial Ad, Rewarded Ad और Banner Ad आते हैं। अगर आप बिना किसी विज्ञापन (100% Ad-Free) के तुरंत 1-Click में अनलिमिटेड 4K AI वीडियो और वायरल हैशटैग बनाना चाहते हैं, तो अभी अपना VIP सब्सक्रिप्शन एक्टिवेट करें!",
                                    fontSize = 12.5.sp,
                                    color = Color.White,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Plan comparison pills
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF140921))
                                            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text("STARTER VIP (15 दिन)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                                            Text("मात्र ₹49", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                            Text("• 100% सभी Ads बंद", fontSize = 10.sp, color = Color(0xFF86EFAC))
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF140921))
                                            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text("PRO VIP (1 महीना)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                                            Text("मात्र ₹79 / ₹179", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                            Text("• अनलिमिटेड Ad-Free", fontSize = 10.sp, color = Color(0xFF86EFAC))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = onUpgradeToVip,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1.2f)
                                            .height(46.dp)
                                            .testTag("btn_upgrade_ad_free_vip")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "👑 अभी Ad-Free VIP लें",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = onRedeemDiamondsForVip,
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.2.dp, Color(0xFF00E5FF)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp)
                                            .testTag("btn_redeem_diamonds_ad_free_vip")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Diamond,
                                            contentDescription = null,
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "150 💎 से फ्री VIP",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00E5FF)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 1. HERO BANNER: Attractive Hindi + English Studio Header with Connected Accounts
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    selectedCategory.accentColor,
                                    Color(0xFF8A2BE2),
                                    Color(0xFF00E5FF)
                                )
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF211242),
                                            Color(0xFF121836),
                                            Color(0xFF1F0E33)
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFFFFD700).copy(alpha = 0.16f))
                                            .border(
                                                1.dp,
                                                Color(0xFFFFD700).copy(alpha = 0.6f),
                                                RoundedCornerShape(50)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "🔥 #1 VIRAL VIDEO & HASHTAG ENGINE",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFFFD700)
                                        )
                                    }

                                    Text(
                                        text = "98.8% Viral Score",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00E676)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "एक क्लिक में बनाएं धमाकेदार AI वीडियो और टॉप रैंकिंग वायरल हैशटैग्स! 🚀",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    lineHeight = 24.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "अपनी मनपसंद कैटेगरी चुनें या अपनी फोटो लगाएं — AI अपने आप वायरल टाइटल, हुक स्क्रिप्ट और ट्रेंडिंग हैशटैग्स तैयार कर देगा।",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFFD1D5DB),
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Connected Social Platforms Row (Clicking any platform triggers Interstitial Ad)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val platforms = listOf(
                                        "Instagram Reels ✓" to Color(0xFFE1306C),
                                        "YouTube Shorts ✓" to Color(0xFFFF0000),
                                        "Facebook Reels ✓" to Color(0xFF1877F2),
                                        "GoLiveGram ✓" to Color(0xFF00E5FF)
                                    )
                                    platforms.forEach { (name, color) ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(color.copy(alpha = 0.16f))
                                                .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                                .clickable {
                                                    triggerOptionWithInterstitialAd("Platform Selected: $name") {
                                                        statusBannerMessage = "✅ $name ऑटो-सिंक और वायरल हैशटैग्स के लिए एक्टिवेट हो गया!"
                                                    }
                                                }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = name,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. CATEGORY SELECTOR: 1-Click Trending Categories (Triggers Interstitial Ad on click)
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1️⃣ अपनी वीडियो कैटेगरी चुनें (Select Category)",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = selectedCategory.titleHindi,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedCategory.accentColor
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(categories) { category ->
                                val isSelected = selectedCategory.id == category.id
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            category.accentColor.copy(alpha = 0.20f)
                                        } else {
                                            Color(0xFF14132B)
                                        }
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) category.accentColor else Color(0xFF2D2B52)
                                    ),
                                    modifier = Modifier
                                        .width(155.dp)
                                        .clickable {
                                            triggerOptionWithInterstitialAd("Category: ${category.titleHindi}") {
                                                selectedCategory = category
                                                executeOneClickGenerate()
                                            }
                                        }
                                        .testTag("full_page_cat_${category.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Brush.linearGradient(
                                                            listOf(category.accentColor, category.secondaryColor)
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = category.emoji,
                                                    fontSize = 18.sp
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Selected",
                                                    tint = category.accentColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = category.titleHindi,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = category.titleEnglish,
                                            fontSize = 10.5.sp,
                                            color = Color(0xFFB0B8D1),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2B. REWARDED VIDEO AD CARD INSIDE THIS FEATURE (Earn +5 Diamonds + Unlock 4K Boost)
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF102027)),
                        border = BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(listOf( Color(0xFF00E5FF), Color(0xFFFFD700)))
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("one_click_studio_rewarded_ad_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00E5FF).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircleFilled,
                                            contentDescription = null,
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "🎁 Rewarded Video Ad (रिवॉर्ड एड देखें)",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "छोटा वीडियो विज्ञापन देखें और पाएं +5 💎 डायमंड्स + 4K HD नो-वॉटरमार्क बूस्ट!",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFFB2EBF2),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val act = context as? Activity
                                        if (act != null) {
                                            AdManager.showRewardedAd(
                                                activity = act,
                                                rewardedUnitId = rewardedAdUnitId,
                                                unityPlacementId = AdManager.activeUnityRewardedPlacementId,
                                                onUserEarnedReward = {
                                                    rewardedAdBonusUnlocked = true
                                                    onRewardEarned(5, 15)
                                                    Toast.makeText(
                                                        context,
                                                        "🎉 +5 💎 डायमंड्स और +15 🪙 कॉइन्स आपके वॉलेट में जुड़ गए!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                onFallbackSimulation = {
                                                    showStudioRewardedAd = true
                                                }
                                            )
                                        } else {
                                            AdManager.selectNextMediatedAd(
                                                format = AdFormat.REWARDED,
                                                admobUnitId = rewardedAdUnitId,
                                                unityPlacementId = AdManager.activeUnityRewardedPlacementId
                                            )
                                            showStudioRewardedAd = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B8D4)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .testTag("btn_watch_studio_rewarded_ad")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OndemandVideo,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (rewardedAdBonusUnlocked) "Watch Again (+5 💎)" else "Watch Rewarded Ad (+5 💎)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                }

                                if (!isVip) {
                                    OutlinedButton(
                                        onClick = onUpgradeToVip,
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFFD700)),
                                        modifier = Modifier.height(42.dp)
                                    ) {
                                        Text(
                                            text = "Skip Ads (VIP)",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD700)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. INTERACTIVE 9:16 VERTICAL AI VIDEO PREVIEW CANVAS & STUDIO CONTROLS
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111026)),
                        border = BorderStroke(
                            1.5.dp,
                            selectedCategory.accentColor.copy(alpha = pulseGlow)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MovieCreation,
                                        contentDescription = null,
                                        tint = selectedCategory.accentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "🎬 Live 9:16 AI Reel & Hook Preview",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF00E676).copy(alpha = 0.18f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "4K 60FPS HD",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF00E676)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 9:16 Vertical 4-Scene Studio Preview Monitor
                            val previewScenes = studioPackage?.scenes ?: emptyList()
                            val sceneCount = previewScenes.size.coerceAtLeast(1)
                            val activeSceneIdx = ((previewElapsedSec / 3.0f).toInt()).coerceIn(0, sceneCount - 1)
                            val sceneProgress = ((previewElapsedSec - activeSceneIdx * 3.0f) / 3.0f).coerceIn(0f, 1f)
                            val activeScene = previewScenes.getOrNull(activeSceneIdx)
                            val activeBitmap = sceneBitmaps.getOrNull(activeSceneIdx) ?: sceneBitmaps.firstOrNull() ?: uploadedPhotoBitmap
                            val motionScale = when (activeScene?.cameraMotion) {
                                "zoom-out" -> 1.18f - 0.12f * sceneProgress
                                "zoom-in" -> 1.04f + 0.14f * sceneProgress
                                else -> 1.10f + 0.04f * sceneProgress
                            }
                            val motionTx = when (activeScene?.cameraMotion) {
                                "pan-right" -> (sceneProgress - 0.5f) * -28f
                                "pan-left" -> (sceneProgress - 0.5f) * 28f
                                else -> 0f
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(330.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF140D2E),
                                                selectedCategory.secondaryColor.copy(alpha = 0.45f),
                                                Color(0xFF070918)
                                            )
                                        )
                                    )
                                    .border(
                                        1.5.dp,
                                        Brush.verticalGradient(
                                            listOf(
                                                selectedCategory.accentColor,
                                                selectedCategory.secondaryColor
                                            )
                                        ),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .clickable {
                                        triggerOptionWithInterstitialAd("9:16 AI Reel Preview Player") {
                                            isVideoPlaying = !isVideoPlaying
                                        }
                                    }
                                    .testTag("ai_video_preview_canvas")
                            ) {
                                if (activeBitmap != null) {
                                    Image(
                                        bitmap = activeBitmap.asImageBitmap(),
                                        contentDescription = "AI Scene Visual",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer(
                                                scaleX = motionScale,
                                                scaleY = motionScale,
                                                translationX = motionTx
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.28f))
                                    )
                                } else if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Uploaded Photo Preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer(
                                                scaleX = motionScale,
                                                scaleY = motionScale,
                                                translationX = motionTx
                                            )
                                    )
                                } else {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val centerX = size.width / 2f
                                        val centerY = size.height / 2f
                                        val driftX = kotlin.math.cos(Math.toRadians(wavePhase.toDouble())).toFloat() * 48f
                                        val driftY = kotlin.math.sin(Math.toRadians(wavePhase.toDouble())).toFloat() * 28f
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color.White.copy(alpha = 0.25f),
                                                    selectedCategory.accentColor.copy(alpha = 0.35f * pulseGlow),
                                                    Color.Transparent
                                                ),
                                                center = Offset(centerX + driftX, size.height * 0.28f),
                                                radius = size.minDimension * 0.65f
                                            ),
                                            radius = size.minDimension * 0.65f,
                                            center = Offset(centerX + driftX, size.height * 0.28f)
                                        )
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    selectedCategory.secondaryColor.copy(alpha = 0.40f),
                                                    Color.Transparent
                                                ),
                                                center = Offset(centerX - driftX, size.height * 0.65f + driftY),
                                                radius = size.minDimension * 0.55f
                                            ),
                                            radius = size.minDimension * 0.55f,
                                            center = Offset(centerX - driftX, size.height * 0.65f + driftY)
                                        )
                                    }
                                }

                                // 4-Segment Top Story / Reels Progress Indicator
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .align(Alignment.TopCenter),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    for (sIdx in 0 until 4) {
                                        val segFill = when {
                                            sIdx < activeSceneIdx -> 1f
                                            sIdx == activeSceneIdx -> sceneProgress
                                            else -> 0f
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(3.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(Color.White.copy(alpha = 0.28f))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(segFill)
                                                    .height(3.dp)
                                                    .background(selectedCategory.accentColor)
                                            )
                                        }
                                    }
                                }

                                // Top Overlay inside Video Player: Audio Track & Play/Voice Buttons
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, end = 12.dp, top = 20.dp)
                                        .align(Alignment.TopCenter),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Black.copy(alpha = 0.65f))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = null,
                                            tint = selectedCategory.accentColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = selectedCategory.beatTrackName,
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        IconButton(
                                            onClick = {
                                                if (isVoiceSpeaking) {
                                                    stopVoiceover()
                                                } else {
                                                    val vo = studioPackage?.voiceoverText ?: generatedResult?.hook ?: selectedCategory.sampleHook
                                                    speakHindiVoiceover(vo)
                                                }
                                            },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(if (isVoiceSpeaking) Color(0xFF00E5FF) else Color.Black.copy(alpha = 0.65f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.RecordVoiceOver,
                                                contentDescription = "Hindi Voiceover",
                                                tint = if (isVoiceSpeaking) Color.Black else Color.White,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                triggerOptionWithInterstitialAd("Play/Pause AI Video Preview") {
                                                    isVideoPlaying = !isVideoPlaying
                                                }
                                            },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.65f))
                                        ) {
                                            Icon(
                                                imageVector = if (isVideoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = "Play/Pause Preview",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Lower-Third Dynamic Reel Scene Caption & Bottom Controls
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.90f))
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xB8070914))
                                            .border(1.dp, selectedCategory.accentColor.copy(alpha = 0.75f), RoundedCornerShape(14.dp))
                                            .padding(horizontal = 12.dp, vertical = 9.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "SCENE ${activeSceneIdx + 1}/$sceneCount • ${selectedCategory.titleEnglish.uppercase()}",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = selectedCategory.accentColor
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = activeScene?.caption ?: generatedResult?.hook ?: selectedCategory.sampleHook,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                maxLines = 3,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = Color(0xFF00E5FF),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isVoiceSpeaking) "🎙️ Hindi AI Voiceover Speaking..." else if (isVideoPlaying) "9:16 30FPS Motion Active" else "Preview Paused",
                                                fontSize = 10.5.sp,
                                                color = Color(0xFF00E5FF),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "00:${String.format(Locale.US, "%02d", previewElapsedSec.toInt())} / 00:12",
                                            fontSize = 10.5.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = generatedResult?.hashtags?.take(5)?.joinToString(" ")
                                            ?: "#ViralReels #TrendingShorts #AIStudio #ExplorePage",
                                        fontSize = 10.5.sp,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. 3-MODE FLOW / VEO CREATION CONTROLS (PROMPT / PHOTO / SCRIPT) & 1-CLICK GENERATE
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131229)),
                        border = BorderStroke(1.dp, Color(0xFF2E2C54)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "2️⃣ वीडियो बनाने का तरीका चुनें (Flow AI Studio)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // 3-Mode Selector Row (Prompt | Photo | Script)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Triple("prompt", "✨ Prompt", "Topic to AI"),
                                    Triple("photo", "📸 Photo", "Photo + Vision"),
                                    Triple("script", "📝 Script", "Custom Text")
                                ).forEach { (id, title, sub) ->
                                    val active = studioMode == id
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (active) Color(0xFF312E81) else Color(0xFF0B0A1A))
                                            .border(
                                                width = if (active) 1.5.dp else 1.dp,
                                                color = if (active) Color(0xFF00E5FF) else Color(0xFF2D2B52),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { studioMode = id }
                                            .padding(vertical = 8.dp, horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (active) Color.White else Color(0xFFD1D5DB)
                                        )
                                        Text(
                                            text = sub,
                                            fontSize = 9.5.sp,
                                            color = if (active) Color(0xFF00E5FF) else Color.Gray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (studioMode == "script") {
                                OutlinedTextField(
                                    value = customScriptInput,
                                    onValueChange = { customScriptInput = it },
                                    minLines = 3,
                                    placeholder = {
                                        Text(
                                            text = "अपनी शायरी, कहानी या स्क्रिप्ट यहाँ पेस्ट करें — AI इसे 4 सीन में बदल देगा...",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = selectedCategory.accentColor,
                                        unfocusedBorderColor = Color(0xFF3B3866),
                                        focusedContainerColor = Color(0xFF0B0A1A),
                                        unfocusedContainerColor = Color(0xFF0B0A1A)
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("one_click_topic_input")
                                )
                            } else {
                                OutlinedTextField(
                                    value = customTopicInput,
                                    onValueChange = { customTopicInput = it },
                                    placeholder = {
                                        Text(
                                            text = if (studioMode == "photo") {
                                                "Optional: फोटो के लिए मूड या स्टोरी लिखें..."
                                            } else {
                                                "उदा: देसी जिम मोटिवेशन, हरियाणवी स्वैग, या कोई भी टॉपिक..."
                                            },
                                            fontSize = 12.5.sp,
                                            color = Color.Gray
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = selectedCategory.accentColor,
                                        unfocusedBorderColor = Color(0xFF3B3866),
                                        focusedContainerColor = Color(0xFF0B0A1A),
                                        unfocusedContainerColor = Color(0xFF0B0A1A)
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("one_click_topic_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Photo Picker & Sample/Idea Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        triggerOptionWithInterstitialAd("Photo-to-Video Gallery Picker") {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("btn_upload_photo_for_video")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (selectedImageUri != null) "फोटो बदलें ✓" else "फोटो से वीडियो",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        triggerOptionWithInterstitialAd("Trending AI Idea Generator") {
                                            if (studioMode == "script") {
                                                customScriptInput = selectedCategory.sampleHook + "\nहर सपना सच होता है अगर हिम्मत न टूटे।\nआज से अपनी नई शुरुआत करो!\nवीडियो पसंद आए तो अभी शेयर करें ❤️"
                                            } else {
                                                customTopicInput = selectedCategory.defaultPrompt
                                            }
                                            executeOneClickGenerate()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFD700)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("btn_trending_idea_fill")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (studioMode == "script") "सैंपल स्क्रिप्ट" else "ट्रेंडिंग आइडिया",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD700)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // MAIN GIANT 1-CLICK GENERATE CTA BUTTON (Triggers Interstitial Ad)
                            Button(
                                onClick = {
                                    triggerOptionWithInterstitialAd("1-Click AI Video & Viral Hashtag Generation") {
                                        executeOneClickGenerate()
                                    }
                                },
                                enabled = !isGenerating,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7C3AED),
                                    disabledContainerColor = Color(0xFF4C1D95)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("generate_video_watch_ad_button")
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(
                                        color = Color(0xFFFFD700),
                                        strokeWidth = 2.5.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "AI वीडियो और हैशटैग बन रहे हैं...",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "🔥 1-Click AI Video & Viral हैशटैग बनाएं",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.5.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isGenerating) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    LinearProgressIndicator(
                                        progress = { generationProgress },
                                        color = Color(0xFF00E5FF),
                                        trackColor = Color(0xFF1F2937),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(50))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = generationStageText,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF00E5FF)
                                    )
                                }
                            }

                            statusBannerMessage?.let { msg ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF064E3B).copy(alpha = 0.55f))
                                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6EE7B7)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4B. 4-SCENE AI STORYBOARD STRIP
                studioPackage?.scenes?.takeIf { it.isNotEmpty() }?.let { scenes ->
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF101329)),
                            border = BorderStroke(1.dp, Color(0xFF28305A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🎞️ 4-Scene AI Storyboard (12s Reel)",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF00E5FF)
                                    )
                                    Text(
                                        text = studioPackage?.modelUsed ?: "Gemini AI",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF86EFAC)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(scenes.indices.toList()) { idx ->
                                        val sc = scenes[idx]
                                        val thumb = sceneBitmaps.getOrNull(idx) ?: uploadedPhotoBitmap
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF080A18)),
                                            border = BorderStroke(1.dp, selectedCategory.accentColor.copy(alpha = 0.45f)),
                                            modifier = Modifier.width(136.dp)
                                        ) {
                                            Column {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(82.dp)
                                                        .background(
                                                            Brush.linearGradient(
                                                                listOf(
                                                                    selectedCategory.accentColor.copy(alpha = 0.25f),
                                                                    selectedCategory.secondaryColor.copy(alpha = 0.4f)
                                                                )
                                                            )
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (thumb != null) {
                                                        Image(
                                                            bitmap = thumb.asImageBitmap(),
                                                            contentDescription = "Scene ${idx + 1}",
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    } else {
                                                        Text(text = selectedCategory.emoji, fontSize = 24.sp)
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.TopStart)
                                                            .padding(5.dp)
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(Color.Black.copy(alpha = 0.72f))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "S${idx + 1} • ${sc.timeRange}",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF00E5FF)
                                                        )
                                                    }
                                                }
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Text(
                                                        text = sc.caption,
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = sc.cameraMotion,
                                                        fontSize = 9.sp,
                                                        color = Color(0xFF9CA3AF)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. VIRAL HASHTAG VAULT & GENERATED CONTENT SHOWCASE (Every option triggers Interstitial Ad)
                generatedResult?.let { result ->
                    item {
                        Card(
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF14112E)),
                            border = BorderStroke(
                                1.5.dp,
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFF00E5FF))
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("viral_hashtag_vault_card")
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Tag,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "🏷️ ऑटो-वायरल हैशटैग्स (Viral Hashtags)",
                                                fontSize = 15.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "High-Reach Tags for Reels, Shorts & GoLiveGram",
                                                fontSize = 11.sp,
                                                color = Color(0xFF00E5FF)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            triggerOptionWithInterstitialAd("Copy All Viral Hashtags") {
                                                val allTags = result.hashtags.joinToString(" ")
                                                copyToClipboard(context, "Viral Hashtags", allTags)
                                                statusBannerMessage = "✅ सभी वायरल हैशटैग्स कॉपी हो गए! (Copied ${result.hashtags.size} Hashtags)"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .height(36.dp)
                                            .testTag("btn_copy_all_viral_hashtags")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Copy All",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Interactive Hashtag Chips FlowRow (Clicking any chip triggers Interstitial Ad)
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    result.hashtags.forEachIndexed { index, tag ->
                                        val chipColor = when (index % 3) {
                                            0 -> Color(0xFFFFD700)
                                            1 -> Color(0xFF00E5FF)
                                            else -> Color(0xFFD500F9)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(chipColor.copy(alpha = 0.14f))
                                                .border(1.dp, chipColor.copy(alpha = 0.65f), RoundedCornerShape(50))
                                                .clickable {
                                                    triggerOptionWithInterstitialAd("Copy Hashtag: $tag") {
                                                        copyToClipboard(context, "Hashtag", tag)
                                                    }
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = tag,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Viral Title / Hook Box
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B091B)),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFF2E2A5A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "🔥 वायरल टाइटल & 3-सेकंड हुक (Viral Title)",
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFD700)
                                            )
                                            Text(
                                                text = "📋 कॉपी करें",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00E5FF),
                                                modifier = Modifier.clickable {
                                                    triggerOptionWithInterstitialAd("Copy Viral Title & Hook") {
                                                        copyToClipboard(context, "Viral Hook", result.hook)
                                                    }
                                                }
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = result.hook,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Viral Caption & Script Box
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B091B)),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFF2E2A5A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "📝 AI वीडियो स्क्रिप्ट & कैप्शन (Script & Caption)",
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00E5FF)
                                            )
                                            Text(
                                                text = "📋 कॉपी करें",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFD700),
                                                modifier = Modifier.clickable {
                                                    triggerOptionWithInterstitialAd("Copy AI Video Script & Caption") {
                                                        copyToClipboard(
                                                            context,
                                                            "Script & Caption",
                                                            "${result.bodyCaption}\n\n${result.callToAction}"
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = result.bodyCaption,
                                            fontSize = 12.5.sp,
                                            color = Color(0xFFE5E7EB),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // 1-Click Auto-Post & Real MP4 Gallery Export Buttons
                                Button(
                                    onClick = {
                                        triggerOptionWithInterstitialAd("1-Click Auto-Post to Socials") {
                                            val existingUri = renderedVideoOutput?.shareUri
                                            val fullSharePayload = buildString {
                                                appendLine("🔥 ${result.hook}")
                                                appendLine()
                                                appendLine(result.bodyCaption)
                                                appendLine()
                                                append(result.hashtags.joinToString(" "))
                                            }
                                            if (existingUri != null) {
                                                shareVideoFileToSocials(context, existingUri, fullSharePayload)
                                                statusBannerMessage = "🚀 .MP4 वीडियो और वायरल हैशटैग्स शेयर के लिए तैयार!"
                                            } else {
                                                renderAndSaveOrShareMp4(true)
                                            }
                                        }
                                    },
                                    enabled = !isRenderingMp4,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00C853)
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("auto_post_all_socials_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isRenderingMp4) "⏳ .MP4 रेंडर हो रहा है (${(mp4RenderProgress * 100).toInt()}%)..." else "🌐 1-Click Share .MP4 to IG, YT & WhatsApp",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.5.sp,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            triggerOptionWithInterstitialAd("Download HD Video to Gallery") {
                                                renderAndSaveOrShareMp4(false)
                                            }
                                        },
                                        enabled = !isRenderingMp4,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF1E293B)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp)
                                            .testTag("download_phone_gallery_button")
                                    ) {
                                        if (isRenderingMp4) {
                                            CircularProgressIndicator(
                                                color = Color(0xFF00E5FF),
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = null,
                                                tint = Color(0xFF00E5FF),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isRenderingMp4) "${(mp4RenderProgress * 100).toInt()}% .MP4" else "गैलरी में .MP4 सेव करें",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp,
                                            color = Color.White
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            triggerOptionWithInterstitialAd("Copy Full Viral Package") {
                                                val fullText = "${result.hook}\n\n${result.bodyCaption}\n\n${result.callToAction}\n\n${result.hashtags.joinToString(" ")}"
                                                copyToClipboard(context, "Full Viral Package", fullText)
                                                statusBannerMessage = "✅ पूरा पैकेज (टाइटल + स्क्रिप्ट + हैशटैग्स) कॉपी हो गया!"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF312E81)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp)
                                            .testTag("btn_copy_full_viral_package")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "पूरा पोस्ट कॉपी करें",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // 📺 FULL-SCREEN INTERSTITIAL AD MODAL ON EVERY OPTION CLICK (WITH VIP UPGRADE CTA)
    // =========================================================================
    if (showOptionInterstitialAd) {
        OneClickStudioInterstitialAdModal(
            triggerReason = activeAdTriggerReason,
            onDismissAndContinue = {
                showOptionInterstitialAd = false
                val action = pendingOptionAction
                pendingOptionAction = null
                action?.invoke()
            },
            onUpgradeToVip = {
                showOptionInterstitialAd = false
                pendingOptionAction = null
                onUpgradeToVip()
            }
        )
    }

    // =========================================================================
    // 🎁 FULL-SCREEN REWARDED VIDEO AD MODAL (+5 DIAMONDS & +15 COINS)
    // =========================================================================
    if (showStudioRewardedAd) {
        OneClickStudioRewardedAdModal(
            onDismiss = { showStudioRewardedAd = false },
            onRewardCompleted = {
                showStudioRewardedAd = false
                rewardedAdBonusUnlocked = true
                onRewardEarned(5, 15)
                statusBannerMessage = "🎉 बधाई हो! Rewarded Ad पूरा देखने पर आपको +5 💎 डायमंड्स और +15 🪙 कॉइन्स मिले!"
                Toast.makeText(
                    context,
                    "💎 +5 Diamonds & +15 Coins credited from Rewarded Ad!",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onUpgradeToVip = {
                showStudioRewardedAd = false
                onUpgradeToVip()
            }
        )
    }
}

@Composable
private fun OneClickStudioInterstitialAdModal(
    triggerReason: String,
    onDismissAndContinue: () -> Unit,
    onUpgradeToVip: () -> Unit
) {
    val mediationState by AdManager.mediationState.collectAsState()
    val lastDecision by AdManager.lastInterstitialDecision.collectAsState()
    val unityGameId = mediationState.unityGameId.ifBlank { AdManager.DEFAULT_UNITY_GAME_ID }
    val unityPlacementId = mediationState.unityInterstitialPlacementId.ifBlank { AdManager.DEFAULT_UNITY_INTERSTITIAL_PLACEMENT_ID }
    val isUnitySelected = lastDecision?.selectedNetwork == AdNetwork.UNITY_ADS

    var secondsLeft by remember { mutableIntStateOf(3) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
    }
    val progress by animateFloatAsState(
        targetValue = (3 - secondsLeft) / 3f,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "studio_interstitial_progress"
    )

    Dialog(
        onDismissRequest = onDismissAndContinue,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF5070612))
                .padding(16.dp)
                .testTag("one_click_studio_interstitial_ad_modal"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14112E)),
                border = BorderStroke(
                    2.dp,
                    Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFF00E5FF)))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isUnitySelected) Color(0xFF00E5FF) else Color(0xFFFFD700))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isUnitySelected) "Unity Ads [$unityGameId] • $unityPlacementId" else "AdMob Interstitial Ad",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (secondsLeft > 0) "${secondsLeft}s..." else "Ready ✓",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF)
                            )
                        }

                        IconButton(
                            onClick = onDismissAndContinue,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .testTag("btn_close_studio_interstitial_ad")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Ad",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        color = Color(0xFFFFD700),
                        trackColor = Color(0xFF1F2937),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF8A2BE2), Color(0xFF00E5FF)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isUnitySelected) {
                            "📺 Unity Interstitial Ad ($unityPlacementId)"
                        } else {
                            "📺 Sponsored Interstitial Ad"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Action: $triggerReason • Game ID: $unityGameId",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00E5FF),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Clear Ad-Free VIP Pitch inside the Interstitial Ad
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF240F30)),
                        border = BorderStroke(1.2.dp, Color(0xFFFFD700)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🚫 क्या आप बार-बार आने वाले Ads से छुटकारा पाना चाहते हैं?",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD700),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "फ्री यूजर के लिए हर ऑप्शन पर क्लिक करने पर विज्ञापन (Ad) आता है। सभी ऐड्स को 100% बंद करने और बिना रुकावट AI वीडियो व वायरल हैशटैग बनाने के लिए अभी VIP सब्सक्रिप्शन लें!",
                                fontSize = 11.5.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onUpgradeToVip,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_interstitial_upgrade_vip")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "👑 अभी Ad-Free VIP लें (₹49 / ₹79)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.5.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onDismissAndContinue,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_skip_studio_interstitial_ad")
                    ) {
                        Text(
                            text = if (secondsLeft > 0) "Skip Ad & Continue (${secondsLeft}s) →" else "Skip Ad & Continue to Option →",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OneClickStudioRewardedAdModal(
    onDismiss: () -> Unit,
    onRewardCompleted: () -> Unit,
    onUpgradeToVip: () -> Unit
) {
    val mediationState by AdManager.mediationState.collectAsState()
    val lastDecision by AdManager.lastRewardedDecision.collectAsState()
    val unityGameId = mediationState.unityGameId.ifBlank { AdManager.DEFAULT_UNITY_GAME_ID }
    val unityPlacementId = mediationState.unityRewardedPlacementId.ifBlank { AdManager.DEFAULT_UNITY_REWARDED_PLACEMENT_ID }
    val isUnitySelected = lastDecision?.selectedNetwork == AdNetwork.UNITY_ADS

    var secondsLeft by remember { mutableIntStateOf(5) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }
    }
    val progress by animateFloatAsState(
        targetValue = (5 - secondsLeft) / 5f,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "studio_rewarded_progress"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF5070612))
                .padding(16.dp)
                .testTag("one_click_studio_rewarded_ad_modal"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101E2B)),
                border = BorderStroke(
                    2.dp,
                    Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFFFD700)))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF00E5FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isUnitySelected) "Unity Ads [$unityGameId] • $unityPlacementId" else "AdMob Rewarded Video Ad",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = if (secondsLeft > 0) "Reward in ${secondsLeft}s" else "Reward Unlocked! 🎉",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        color = Color(0xFF00E5FF),
                        trackColor = Color(0xFF1F2937),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(56.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isUnitySelected) {
                            "🎁 Unity Rewarded Ad ($unityPlacementId) • +5 💎 +15 🪙"
                        } else {
                            "🎁 Watch & Earn +5 💎 Diamonds + 15 🪙 Coins"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "विज्ञापन से हमेशा के लिए छुटकारा पाने के लिए आप कभी भी Ad-Free VIP सब्सक्रिप्शन ले सकते हैं!",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onRewardCompleted,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_claim_studio_rewarded_ad")
                    ) {
                        Text(
                            text = if (secondsLeft > 0) "Claim +5 💎 Reward (${secondsLeft}s)" else "🎉 Claim +5 💎 Diamonds & Continue",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onUpgradeToVip,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFD700)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Text(
                            text = "👑 Ads हटाएँ — अभी VIP सब्सक्रिप्शन लें",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, "✅ $label कॉपी हो गया!", Toast.LENGTH_SHORT).show()
    } catch (_: Throwable) {
    }
}

private fun shareContentToSocials(context: Context, content: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share AI Viral Video & Hashtags"))
    } catch (_: Throwable) {
    }
}

private fun shareVideoFileToSocials(context: Context, videoUri: Uri, captionText: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_STREAM, videoUri)
            putExtra(Intent.EXTRA_TEXT, captionText)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share HD AI Reel (.MP4)"))
    } catch (_: Throwable) {
        shareContentToSocials(context, captionText)
    }
}
