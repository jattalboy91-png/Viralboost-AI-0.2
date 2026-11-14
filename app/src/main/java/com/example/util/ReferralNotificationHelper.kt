package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

object ReferralNotificationHelper {

    private const val CHANNEL_ID = "viralboost_referral_rewards"
    private const val CHANNEL_NAME = "Referrals & Free Subscriptions"
    private const val CHANNEL_DESC = "Real-time alerts for active friend referrals and free full-app subscriptions"
    private const val NOTIFICATION_ID = 20261

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 100, 250)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Dispatches real-time push notification/alert to user's device:
     * "Congratulations! Your friend joined and explored the app. You have unlocked 1 Day of Full-App Free Subscription!"
     */
    fun showActiveReferralUnlockedNotification(context: Context, friendName: String = "Your friend") {
        try {
            createNotificationChannel(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            val title = "Full-App Free Subscription Unlocked! 🚀"
            val message = "Congratulations! Your friend joined and explored the app. You have unlocked 1 Day of Full-App Free Subscription!"

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$message\n\nEnjoy 24 hours of unrestricted VIP Pro access: unlimited 4K AI video generation, zero watermarks, high-CTR tags, and all premium creator tools!")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            val manager = NotificationManagerCompat.from(context)
            manager.notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // Permission might not be granted yet on Android 13+, handled gracefully
        } catch (_: Throwable) {
            // Safe fallback
        }
    }

    /**
     * Dispatches real-time push notification/alert immediately after a successful video/deal share:
     * "Congratulations! Your post is ready to go viral!"
     */
    fun showPostReadyToGoViralNotification(context: Context) {
        try {
            createNotificationChannel(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            val title = "Post Broadcasted! 🚀"
            val message = "Congratulations! Your post is ready to go viral!"

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$message\n\nYour post is now broadcasting across your connected social channels with auto-embedded viral tags, high-converting shopping hooks, and download links!")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            val manager = NotificationManagerCompat.from(context)
            manager.notify(20262, builder.build())
        } catch (_: SecurityException) {
            // Handled gracefully if notification permission is pending
        } catch (_: Throwable) {
            // Safe fallback
        }
    }

    /**
     * Dispatches real-time push notification/alert when friend downloads & opens app:
     * "बधाई हो! आपके फ्रेंड ने ऐप डाउनलोड कर लिया है, आपको 10 डायमंड मिल गए हैं!"
     */
    fun showFriendDownloadedBonusNotification(context: Context, friendName: String = "Friend") {
        try {
            createNotificationChannel(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            val title = "बधाई हो! +10 डायमंड मिल गए! 💎"
            val message = "बधाई हो! आपके फ्रेंड ने ऐप डाउनलोड कर लिया है, आपको 10 डायमंड मिल गए हैं!"

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$message\n\nInstant Download Bonus Credited! When your friend explores the app for 2 minutes, you will unlock 24 Hours of 100% Free VIP Pro!")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            val manager = NotificationManagerCompat.from(context)
            manager.notify(20263, builder.build())
        } catch (_: Throwable) {
            // Safe fallback
        }
    }

    /**
     * Dispatches real-time push notification/alert when friend explores for 2 minutes:
     * "बधाई हो! आपके फ्रेंड ने 2 मिनट तक ऐप यूज़ किया, आपको 24 घंटे (1 दिन) के लिए फ्री वीआईपी प्रो प्लान मिल गया है!"
     */
    fun showActiveExplorationVipUnlockedNotification(context: Context, friendName: String = "Friend") {
        try {
            createNotificationChannel(context)

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                3,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            val title = "🎉 24 घंटे फ्री वीआईपी प्रो अनलॉक! 🌟"
            val message = "बधाई हो! आपके फ्रेंड ने 2 मिनट तक ऐप यूज़ किया, आपको 24 घंटे (1 दिन) के लिए फ्री वीआईपी प्रो प्लान मिल गया है!"

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$message\n\nAll premium features unlocked: Unlimited AI Videos, AI Image Generators, Zero Watermark exports, and 100% Ad-Free experience for 24 hours!")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            val manager = NotificationManagerCompat.from(context)
            manager.notify(NOTIFICATION_ID, builder.build())
        } catch (_: Throwable) {
            // Safe fallback
        }
    }
}
