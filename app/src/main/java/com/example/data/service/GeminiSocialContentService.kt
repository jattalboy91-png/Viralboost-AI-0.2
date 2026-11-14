package com.example.data.service

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

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

class GeminiSocialContentService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val supportedModels = listOf(
        "gemini-2.5-flash",
        "gemini-2.0-flash",
        "gemini-1.5-flash"
    )

    private fun log(tag: String, msg: String, err: Throwable? = null) {
        try {
            android.util.Log.d(tag, msg, err)
        } catch (_: Throwable) {
            if (err != null) println("[$tag] $msg: ${err.message}")
            else println("[$tag] $msg")
        }
    }

    suspend fun generateSocialContent(
        prompt: String,
        platform: String,
        tone: String,
        contentType: String,
        targetAudience: String = "Content Creators & General Public"
    ): SocialContentResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }

        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (!hasValidKey) {
            log("GeminiService", "GEMINI_API_KEY not configured or is placeholder. Using smart blueprint fallback.")
            return@withContext produceSmartFallback(prompt, platform, tone, contentType)
        }

        val systemPrompt = buildSystemPrompt(platform, tone, contentType, targetAudience)
        val userPrompt = "Create viral social media content for: '$prompt'. Follow the requested format strictly."

        try {
            val responseText = withTimeout(60_000L) {
                callGeminiApiWithFallback(apiKey, systemPrompt, userPrompt)
            }
            parseSocialContent(responseText, isRealApi = true)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log("GeminiService", "Gemini API call failed: ${e.message}", e)
            val fallback = produceSmartFallback(prompt, platform, tone, contentType)
            fallback.copy(
                isRealApiCall = false,
                errorMessage = "Live Gemini request notice: ${e.localizedMessage ?: "Network issue"}. Displaying optimized creator output."
            )
        }
    }

    private fun buildSystemPrompt(
        platform: String,
        tone: String,
        contentType: String,
        targetAudience: String
    ): String {
        return """
            You are a world-class viral social media strategist and algorithmic growth engineer.
            Your task is to craft high-converting, zero-drop social media content optimized specifically for:
            - Target Platform: $platform (e.g. Instagram Reels, TikTok, YouTube Shorts, X/Twitter, LinkedIn)
            - Content Type: $contentType (e.g. Viral 60s Script, Engaging Post Caption, High-CTR Hooks, Complete Suite)
            - Tone: $tone (e.g. High Energy Hype, Informative & Educating, Funny & Relatable, Storytelling)
            - Audience: $targetAudience

            CRITICAL FORMATTING INSTRUCTIONS:
            Structure your output clearly with the following labelled sections so they can be parsed:
            ===HOOK===
            [Provide 2-3 pattern-interrupt hooks designed for the first 3 seconds to prevent scrolling]
            ===CONTENT===
            [The main body, script with timestamps/pacing, or post caption with emojis and paragraph breaks]
            ===CTA===
            [A high-converting Call To Action that drives comments, saves, and shares]
            ===HASHTAGS===
            [8-15 high-ranking hashtags categorized: #BroadReach #NicheSpecific #Trending]
            ===STRATEGY===
            [1-2 bullet points on sound selection, visual cue, and best posting window]
        """.trimIndent()
    }

    private fun callGeminiApiWithFallback(apiKey: String, systemPrompt: String, userMessage: String): String {
        var lastError: Exception? = null

        for (model in supportedModels) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

                val jsonBody = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", "$systemPrompt\n\nUser Request:\n$userMessage"))
                            })
                        })
                    }
                    put("contents", contentsArray)

                    val generationConfig = JSONObject().apply {
                        put("temperature", 0.75)
                        put("topK", 40)
                        put("topP", 0.95)
                        put("maxOutputTokens", 2048)
                    }
                    put("generationConfig", generationConfig)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonBody.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseString = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    val errorMsg = "HTTP ${response.code}: $responseString"
                    log("GeminiService", "Model $model returned error: $errorMsg")
                    lastError = IOException(errorMsg)
                    continue
                }

                val jsonResponse = JSONObject(responseString)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotBlank()) {
                            return text
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e
                log("GeminiService", "Attempt with $model failed: ${e.message}")
            }
        }

        throw lastError ?: IOException("Gemini API returned empty response across all models")
    }

    private fun parseSocialContent(rawText: String, isRealApi: Boolean): SocialContentResult {
        var hook = ""
        var content = ""
        var cta = ""
        var hashtags = ""
        var strategy = ""

        if (rawText.contains("===HOOK===") && rawText.contains("===CONTENT===")) {
            val hookPart = rawText.substringAfter("===HOOK===").substringBefore("===CONTENT===").trim()
            val contentPart = rawText.substringAfter("===CONTENT===").substringBefore("===CTA===").trim()
            val ctaPart = if (rawText.contains("===CTA===")) {
                rawText.substringAfter("===CTA===").substringBefore("===HASHTAGS===").trim()
            } else ""
            val hashtagPart = if (rawText.contains("===HASHTAGS===")) {
                rawText.substringAfter("===HASHTAGS===").substringBefore("===STRATEGY===").trim()
            } else ""
            val strategyPart = if (rawText.contains("===STRATEGY===")) {
                rawText.substringAfter("===STRATEGY===").trim()
            } else ""

            hook = hookPart
            content = contentPart
            cta = ctaPart
            hashtags = hashtagPart
            strategy = strategyPart
        } else {
            // General parsing if sections are formatted differently
            val lines = rawText.lines()
            hook = lines.firstOrNull { it.isNotBlank() } ?: "Viral Pattern Interrupt"
            content = rawText
            cta = "Save this post and share with someone who needs it!"
            hashtags = "#ViralContent #CreatorEconomy #SocialGrowth #TrendingNow"
            strategy = "Post during peak hours (6-9 PM) with high-retention audio."
        }

        val tagList = extractHashtags(hashtags.ifBlank { rawText })

        return SocialContentResult(
            fullResponse = rawText,
            hook = hook.ifBlank { "Stop scrolling! Here is what 99% of people miss..." },
            bodyCaption = content.ifBlank { rawText },
            callToAction = cta.ifBlank { "Double tap if you found this valuable & comment below!" },
            hashtags = if (tagList.isNotEmpty()) tagList else listOf("#viral", "#creator", "#trending", "#fyp", "#growth"),
            platformTips = strategy.ifBlank { "Use trending audio and pin your best comment for maximum interaction." },
            isRealApiCall = isRealApi
        )
    }

    private fun extractHashtags(text: String): List<String> {
        val regex = Regex("#[A-Za-z0-9_]+")
        return regex.findAll(text).map { it.value }.distinct().take(15).toList()
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
            📱 $platform Optimized Content ($tone Tone):
            
            Here's the exact framework for "$safePrompt":
            
            1️⃣ The Foundation:
            Most people overcomplicate it. Focus on high-retention pacing and removing unnecessary fluff in the first 5 seconds.
            
            2️⃣ The Secret Lever:
            Notice how top creators in this space never just give advice—they show proof, reveal before/after contrast, and create curiosity loops.
            
            3️⃣ Action Step:
            Implement this for 7 days straight without breaking consistency. Track your reach vs your saves.
            
            💬 Share your biggest struggle with $cleanTopic in the comments!
        """.trimIndent()

        val cta = "🔖 Save this post so you can reference it when creating, and comment 'VIRAL' to get the full blueprint!"

        val tags = listOf(
            "#${platform.replace(" ", "").lowercase()}",
            "#growthhacks",
            "#viralcontent",
            "#creatoreconomy",
            "#learnontiktok",
            "#reelsviral",
            "#contentstrategy",
            "#algorithmsecrets",
            "#${cleanTopic.replace(" ", "").lowercase().take(12)}"
        )

        val tips = "📈 Pro Tip: For $platform, post between 6:30 PM - 9:00 PM local time. Reply to every comment in the first 30 minutes to boost algorithmic distribution."

        val fullText = """
            ===HOOK===
            $hook
            ===CONTENT===
            $body
            ===CTA===
            $cta
            ===HASHTAGS===
            ${tags.joinToString(" ")}
            ===STRATEGY===
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
