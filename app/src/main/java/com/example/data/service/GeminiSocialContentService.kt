package com.example.data.service

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
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

sealed interface GeminiUiState {
    data object Idle : GeminiUiState
    data object Loading : GeminiUiState
    data class Streaming(val text: String) : GeminiUiState
    data class Success(val result: SocialContentResult) : GeminiUiState
    data class Error(val message: String) : GeminiUiState
}

/**
 * Firebase AI Logic service powered by `Firebase.ai.generativeModel("gemini-3.8-flash")`
 * and Firebase App Check attestation. Requires no client-side API key.
 */
class GeminiSocialContentService {

    // Define structured JSON schema via Schema.obj(...) per Firebase AI Logic guidelines
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

    // Lazily initialize structured JSON generative model ("gemini-3.8-flash")
    private val structuredModel by lazy {
        val config = generationConfig {
            responseMimeType = "application/json"
            responseSchema = socialContentSchema
            temperature = 0.75f
        }
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = config
        )
    }

    // Lazily initialize streaming / conversational generative model ("gemini-3.8-flash")
    private val streamingModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash"
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
     * Generates structured viral social media content using Firebase AI Logic (`gemini-3.8-flash`)
     * with `Schema.obj(...)` and a realistic 60-second coroutine timeout.
     */
    suspend fun generateSocialContent(
        prompt: String,
        platform: String,
        tone: String,
        contentType: String,
        targetAudience: String = "Content Creators & General Public"
    ): SocialContentResult = withContext(Dispatchers.IO) {
        val fullPrompt = buildPrompt(prompt, platform, tone, contentType, targetAudience)

        try {
            val response = withTimeout(60_000L) {
                structuredModel.generateContent(fullPrompt)
            }
            val jsonString = response.text
                ?: throw IllegalStateException("Gemini model returned an empty response.")

            parseStructuredJsonResponse(jsonString)
        } catch (e: TimeoutCancellationException) {
            logError("Firebase AI generation timed out after 60 seconds", e)
            produceSmartFallback(prompt, platform, tone, contentType).copy(
                isRealApiCall = false,
                errorMessage = "Generation timed out after 60 seconds. Please try again."
            )
        } catch (e: CancellationException) {
            // ALWAYS re-throw CancellationException to preserve cooperative coroutine cancellation
            throw e
        } catch (e: Exception) {
            logError("Firebase AI generation failed", e)
            produceSmartFallback(prompt, platform, tone, contentType).copy(
                isRealApiCall = false,
                errorMessage = e.localizedMessage ?: "Firebase AI generation failed."
            )
        }
    }

    /**
     * Streams live Gemini 3.8 Flash responses chunk-by-chunk to minimize Time-to-First-Token (TTFT).
     */
    fun generateSocialContentStream(
        prompt: String,
        platform: String,
        tone: String,
        contentType: String,
        targetAudience: String = "Content Creators & General Public"
    ): Flow<String> = flow {
        val streamPrompt = buildStreamingPrompt(prompt, platform, tone, contentType, targetAudience)
        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                streamingModel.generateContentStream(streamPrompt).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        accumulated.append(chunkText)
                        emit(accumulated.toString())
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logError("Firebase AI streaming generation failed", e)
            throw e
        }
    }

    /**
     * Parses raw valid JSON guaranteed by `responseMimeType = "application/json"` and `responseSchema`
     * directly without regex or markdown code fence stripping.
     */
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
