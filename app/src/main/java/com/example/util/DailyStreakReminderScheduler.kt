package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DailyReminderConfig(
    val isEnabled: Boolean = true,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val streakDays: Int = 1,
    val nextTriggerAtMillis: Long = 0L,
    val lastNotificationAtMillis: Long = 0L
) {
    val formattedTime: String
        get() {
            val amPm = if (reminderHour < 12) "AM" else "PM"
            val displayHour = when {
                reminderHour == 0 -> 12
                reminderHour > 12 -> reminderHour - 12
                else -> reminderHour
            }
            return String.format(Locale.US, "%02d:%02d %s", displayHour, reminderMinute, amPm)
        }
}

/**
 * Manages daily engagement reminders using Android [AlarmManager] to notify users
 * to check their daily login streak (+2 Diamonds & +100 Coins) and generate fresh
 * AI viral content on the ViralBoost platform.
 */
object DailyStreakReminderScheduler {

    const val ACTION_DAILY_STREAK_REMINDER = "com.example.ACTION_DAILY_STREAK_REMINDER"
    const val EXTRA_STREAK_DAYS = "extra_streak_days"
    const val REMINDER_REQUEST_CODE = 20269
    const val REMINDER_NOTIFICATION_ID = 20269
    const val CHANNEL_ID = "viralboost_daily_streak_reminder"
    private const val CHANNEL_NAME = "Daily Streak & Content Reminders"
    private const val CHANNEL_DESC = "Daily reminders to claim your streak and generate viral AI content"

    private const val PREFS_NAME = "viralboost_daily_reminder_prefs"
    private const val KEY_INITIALIZED = "key_initialized"
    private const val KEY_ENABLED = "key_enabled"
    private const val KEY_HOUR = "key_hour"
    private const val KEY_MINUTE = "key_minute"
    private const val KEY_STREAK_DAYS = "key_streak_days"
    private const val KEY_NEXT_TRIGGER = "key_next_trigger"
    private const val KEY_LAST_NOTIFIED = "key_last_notified"

    private val _reminderConfigFlow = MutableStateFlow(DailyReminderConfig())
    val reminderConfigFlow: StateFlow<DailyReminderConfig> = _reminderConfigFlow.asStateFlow()

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    /**
     * Ensures the default daily reminder (9:00 AM) is scheduled on first app launch
     * or restores the saved configuration into [reminderConfigFlow].
     */
    fun ensureDefaultReminderScheduled(context: Context, currentStreakDays: Int = 1) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val initialized = prefs.getBoolean(KEY_INITIALIZED, false)
        if (!initialized) {
            scheduleDailyReminder(
                context = context,
                hourOfDay = 9,
                minute = 0,
                streakDays = currentStreakDays
            )
            prefs.edit().putBoolean(KEY_INITIALIZED, true).apply()
        } else {
            val enabled = prefs.getBoolean(KEY_ENABLED, true)
            val hour = prefs.getInt(KEY_HOUR, 9)
            val minute = prefs.getInt(KEY_MINUTE, 0)
            val savedStreak = prefs.getInt(KEY_STREAK_DAYS, currentStreakDays).coerceAtLeast(currentStreakDays)
            val lastNotified = prefs.getLong(KEY_LAST_NOTIFIED, 0L)
            if (enabled) {
                scheduleDailyReminder(
                    context = context,
                    hourOfDay = hour,
                    minute = minute,
                    streakDays = savedStreak
                )
            } else {
                _reminderConfigFlow.value = DailyReminderConfig(
                    isEnabled = false,
                    reminderHour = hour,
                    reminderMinute = minute,
                    streakDays = savedStreak,
                    nextTriggerAtMillis = 0L,
                    lastNotificationAtMillis = lastNotified
                )
            }
        }
    }

    /**
     * Computes the next occurrence of [hourOfDay]:[minute] in milliseconds.
     * If that time has already passed today, schedules for tomorrow at the same time.
     */
    fun computeNextTriggerTimeMillis(
        hourOfDay: Int,
        minute: Int,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hourOfDay.coerceIn(0, 23))
            set(Calendar.MINUTE, minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= nowMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis
    }

    /**
     * Schedules a daily repeating reminder via [AlarmManager.setInexactRepeating].
     */
    fun scheduleDailyReminder(
        context: Context,
        hourOfDay: Int = 9,
        minute: Int = 0,
        streakDays: Int = 1
    ): DailyReminderConfig {
        createNotificationChannel(context)
        val triggerAtMillis = computeNextTriggerTimeMillis(hourOfDay, minute)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = buildReminderPendingIntent(context, streakDays)

        try {
            alarmManager?.cancel(pendingIntent)
            alarmManager?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: SecurityException) {
            // Handled gracefully
        } catch (_: Throwable) {
            // Safe fallback
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastNotified = prefs.getLong(KEY_LAST_NOTIFIED, 0L)
        prefs.edit()
            .putBoolean(KEY_INITIALIZED, true)
            .putBoolean(KEY_ENABLED, true)
            .putInt(KEY_HOUR, hourOfDay)
            .putInt(KEY_MINUTE, minute)
            .putInt(KEY_STREAK_DAYS, streakDays)
            .putLong(KEY_NEXT_TRIGGER, triggerAtMillis)
            .apply()

        val updated = DailyReminderConfig(
            isEnabled = true,
            reminderHour = hourOfDay,
            reminderMinute = minute,
            streakDays = streakDays,
            nextTriggerAtMillis = triggerAtMillis,
            lastNotificationAtMillis = lastNotified
        )
        _reminderConfigFlow.value = updated
        return updated
    }

    /**
     * Cancels the scheduled [AlarmManager] daily reminder.
     */
    fun cancelDailyReminder(context: Context): DailyReminderConfig {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val current = _reminderConfigFlow.value
        val pendingIntent = buildReminderPendingIntent(context, current.streakDays)
        try {
            alarmManager?.cancel(pendingIntent)
        } catch (_: Throwable) {
            // Safe fallback
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_ENABLED, false)
            .putLong(KEY_NEXT_TRIGGER, 0L)
            .apply()

        val updated = current.copy(
            isEnabled = false,
            nextTriggerAtMillis = 0L
        )
        _reminderConfigFlow.value = updated
        return updated
    }

    /**
     * Updates the cached streak count so upcoming daily notifications reflect the latest streak.
     */
    fun updateStreakSnapshot(context: Context, streakDays: Int) {
        val current = _reminderConfigFlow.value
        if (current.isEnabled) {
            scheduleDailyReminder(
                context = context,
                hourOfDay = current.reminderHour,
                minute = current.reminderMinute,
                streakDays = streakDays
            )
        } else {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putInt(KEY_STREAK_DAYS, streakDays).apply()
            _reminderConfigFlow.value = current.copy(streakDays = streakDays)
        }
    }

    /**
     * Builds the broadcast [PendingIntent] targeted at [DailyStreakReminderReceiver].
     */
    fun buildReminderPendingIntent(context: Context, streakDays: Int = 1): PendingIntent {
        val intent = Intent(context, DailyStreakReminderReceiver::class.java).apply {
            action = ACTION_DAILY_STREAK_REMINDER
            putExtra(EXTRA_STREAK_DAYS, streakDays)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        return PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            flags
        )
    }

    /**
     * Dispatches the daily streak & AI content generation reminder notification immediately.
     */
    fun showDailyStreakAndContentNotification(context: Context, streakDays: Int = 1) {
        try {
            createNotificationChannel(context)

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                REMINDER_REQUEST_CODE,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                    (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            val safeStreak = streakDays.coerceAtLeast(1)
            val title = "🔥 Keep Your $safeStreak-Day Viral Streak Alive!"
            val shortMessage = "Claim your +2 Diamonds & +100 Coins and generate today's viral AI Reels & Shorts!"
            val expandedMessage = "$shortMessage\n\n" +
                "• 💎 Daily Streak Bonus: +2 Diamonds & +100 Coins ready\n" +
                "• ⚡ Gemini AI Studio: Generate high-retention hooks, scripts & hashtags in 1 click\n" +
                "• 🚀 Stay consistent to hit your next Streak Milestone bonus!"

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(title)
                .setContentText(shortMessage)
                .setStyle(NotificationCompat.BigTextStyle().bigText(expandedMessage))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build()

            NotificationManagerCompat.from(context).notify(REMINDER_NOTIFICATION_ID, notification)

            val now = System.currentTimeMillis()
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putLong(KEY_LAST_NOTIFIED, now).apply()
            _reminderConfigFlow.value = _reminderConfigFlow.value.copy(
                lastNotificationAtMillis = now,
                streakDays = safeStreak
            )
        } catch (_: SecurityException) {
            // Notification permission not yet granted on Android 13+
        } catch (_: Throwable) {
            // Safe fallback
        }
    }
}

/**
 * BroadcastReceiver triggered by [AlarmManager] (or [Intent.ACTION_BOOT_COMPLETED])
 * to notify the user to check their daily streak and generate viral content.
 */
class DailyStreakReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: DailyStreakReminderScheduler.ACTION_DAILY_STREAK_REMINDER
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            DailyStreakReminderScheduler.ensureDefaultReminderScheduled(context)
            return
        }

        val prefs = context.getSharedPreferences("viralboost_daily_reminder_prefs", Context.MODE_PRIVATE)
        val fallbackStreak = prefs.getInt("key_streak_days", 1)
        val streakDays = intent?.getIntExtra(
            DailyStreakReminderScheduler.EXTRA_STREAK_DAYS,
            fallbackStreak
        ) ?: fallbackStreak

        DailyStreakReminderScheduler.showDailyStreakAndContentNotification(
            context = context,
            streakDays = streakDays
        )
    }
}
