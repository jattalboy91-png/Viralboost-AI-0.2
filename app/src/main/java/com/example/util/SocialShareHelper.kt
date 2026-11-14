package com.example.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.monetization.MonetizationConstants

/**
 * Centralized Android Share Intent (`Intent.ACTION_SEND`) helper that enables users
 * to share their generated AI content (hooks, scripts, captions, hashtags) and
 * daily streak milestones directly to external social media apps (WhatsApp, Instagram,
 * YouTube, X/Twitter, Telegram, LinkedIn, etc.).
 */
object SocialShareHelper {

    fun buildAiContentShareText(
        platform: String,
        format: String,
        hook: String,
        bodyCaption: String,
        callToAction: String,
        hashtags: List<String>
    ): String {
        val tagLine = if (hashtags.isNotEmpty()) hashtags.joinToString(" ") else "#ViralBoostAI #CreatorGrowth"
        return buildString {
            appendLine("✨ Generated with ViralBoost AI ($platform • $format)")
            appendLine()
            if (hook.isNotBlank()) {
                appendLine("🔥 Hook (0-3s):")
                appendLine(hook.trim())
                appendLine()
            }
            if (bodyCaption.isNotBlank()) {
                appendLine("📝 Script & Caption:")
                appendLine(bodyCaption.trim())
                appendLine()
            }
            if (callToAction.isNotBlank()) {
                appendLine("🎯 Call to Action:")
                appendLine(callToAction.trim())
                appendLine()
            }
            appendLine(tagLine)
            append("🚀 Create viral content: ${MonetizationConstants.OFFICIAL_STORE_DOWNLOAD_URL}")
        }
    }

    fun buildRawAiAssetShareText(
        type: String,
        topic: String,
        niche: String,
        content: String
    ): String {
        val cleanTopic = topic.ifBlank { "Viral Creator Strategy" }
        return buildString {
            appendLine("🎬 ViralBoost AI • $type ($niche)")
            appendLine("📌 Topic: \"$cleanTopic\"")
            appendLine()
            appendLine(content.trim())
            appendLine()
            append("🚀 Powered by ViralBoost AI: ${MonetizationConstants.OFFICIAL_STORE_DOWNLOAD_URL}")
        }
    }

    fun buildStreakMilestoneShareText(
        streakDays: Int,
        nextMilestone: Int,
        milestoneBonus: Int,
        dailyReward: Int = 2,
        username: String = "Creator"
    ): String {
        val safeDays = streakDays.coerceAtLeast(1)
        val cleanUser = username.ifBlank { "Creator" }
        return buildString {
            appendLine("🔥 $safeDays-Day Creator Streak Unlocked on ViralBoost AI!")
            appendLine("👤 Creator: $cleanUser")
            appendLine("💎 Daily Streak Reward: +$dailyReward Diamonds")
            appendLine("🏆 Next Milestone: Day $nextMilestone (+$milestoneBonus 💎 Bonus)")
            appendLine()
            appendLine("I'm building daily viral consistency with AI hooks, scripts, and multi-platform tools!")
            appendLine("#ViralBoostAI #CreatorStreak #Day$safeDays #ContentCreator #ViralGrowth")
            append("🚀 Join me on ViralBoost AI: ${MonetizationConstants.OFFICIAL_STORE_DOWNLOAD_URL}")
        }
    }

    fun createShareIntent(
        subject: String,
        shareText: String
    ): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    }

    fun shareAiContent(
        context: Context,
        platform: String,
        format: String,
        hook: String,
        bodyCaption: String,
        callToAction: String,
        hashtags: List<String>
    ) {
        val text = buildAiContentShareText(
            platform = platform,
            format = format,
            hook = hook,
            bodyCaption = bodyCaption,
            callToAction = callToAction,
            hashtags = hashtags
        )
        shareTextViaAndroidIntent(
            context = context,
            subject = "ViralBoost AI Content ($platform - $format)",
            shareText = text,
            chooserTitle = "Share AI Content via"
        )
    }

    fun shareRawAiAsset(
        context: Context,
        type: String,
        topic: String,
        niche: String,
        content: String
    ) {
        val text = buildRawAiAssetShareText(
            type = type,
            topic = topic,
            niche = niche,
            content = content
        )
        shareTextViaAndroidIntent(
            context = context,
            subject = "ViralBoost AI $type: $topic",
            shareText = text,
            chooserTitle = "Share AI Asset to Social Apps"
        )
    }

    fun shareStreakMilestone(
        context: Context,
        streakDays: Int,
        nextMilestone: Int,
        milestoneBonus: Int,
        dailyReward: Int = 2,
        username: String = "Creator"
    ) {
        val text = buildStreakMilestoneShareText(
            streakDays = streakDays,
            nextMilestone = nextMilestone,
            milestoneBonus = milestoneBonus,
            dailyReward = dailyReward,
            username = username
        )
        shareTextViaAndroidIntent(
            context = context,
            subject = "🔥 My $streakDays-Day Creator Streak on ViralBoost AI!",
            shareText = text,
            chooserTitle = "Share Streak Milestone via"
        )
    }

    fun shareTextViaAndroidIntent(
        context: Context,
        subject: String,
        shareText: String,
        chooserTitle: String = "Share via"
    ) {
        try {
            val sendIntent = createShareIntent(subject = subject, shareText = shareText)
            val chooserIntent = Intent.createChooser(sendIntent, chooserTitle).apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Unable to open share sheet: ${e.localizedMessage ?: "No compatible app found"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
