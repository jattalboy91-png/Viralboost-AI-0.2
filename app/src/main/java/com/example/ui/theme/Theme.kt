package com.example.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    DARK,
    LIGHT
}

/**
 * Centralized reactive controller for the application's Material 3 Dark / Light mode switcher.
 * Persists user preference in SharedPreferences and updates both Compose state and StateFlow.
 */
object ThemeController {
    private const val PREFS_NAME = "viralboost_theme_prefs"
    private const val KEY_IS_DARK_MODE = "is_dark_mode"

    var isDarkMode: Boolean by mutableStateOf(true)
        private set

    private val _isDarkModeFlow = MutableStateFlow(true)
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkModeFlow.asStateFlow()

    val currentMode: AppThemeMode
        get() = if (isDarkMode) AppThemeMode.DARK else AppThemeMode.LIGHT

    fun initialize(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val saved = prefs.getBoolean(KEY_IS_DARK_MODE, true)
            isDarkMode = saved
            _isDarkModeFlow.value = saved
        } catch (_: Throwable) {
            // Safe fallback in test/preview environments
        }
    }

    fun setDarkMode(enabled: Boolean, context: Context? = null) {
        isDarkMode = enabled
        _isDarkModeFlow.value = enabled
        if (context != null) {
            try {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_IS_DARK_MODE, enabled)
                    .apply()
            } catch (_: Throwable) {
            }
        }
    }

    fun toggleTheme(context: Context? = null): Boolean {
        val next = !isDarkMode
        setDarkMode(next, context)
        return next
    }
}

val LocalIsDarkTheme = compositionLocalOf { true }
val LocalToggleTheme = compositionLocalOf<() -> Unit> { {} }

private val DarkColorScheme = darkColorScheme(
    primary = BrandPurple,
    onPrimary = TextPrimaryDark,
    primaryContainer = BrandPurpleDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = NeonCyan,
    onSecondary = PureDarkBackground,
    secondaryContainer = PureDarkSurfaceElevated,
    onSecondaryContainer = NeonCyanLight,
    tertiary = GoldAccent,
    onTertiary = PureDarkBackground,
    background = PureDarkBackground,
    onBackground = TextPrimaryDark,
    surface = PureDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = PureDarkSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = PureDarkSurfaceBorder,
    outlineVariant = PureDarkSurfaceElevated
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPurpleDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF2E1065),
    secondary = NeonCyanDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF0E7490),
    tertiary = VipGoldDark,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightSurfaceBorder,
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun ViralBoostTheme(
    darkTheme: Boolean = ThemeController.isDarkMode,
    dynamicColor: Boolean = false,
    onToggleTheme: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val resolvedToggle: () -> Unit = onToggleTheme ?: {
        ThemeController.toggleTheme(context)
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = colorScheme.background.toArgb()
                it.navigationBarColor = colorScheme.surface.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalIsDarkTheme provides darkTheme,
        LocalToggleTheme provides resolvedToggle
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Retain alias for compatibility if needed
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = ThemeController.isDarkMode,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ViralBoostTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
