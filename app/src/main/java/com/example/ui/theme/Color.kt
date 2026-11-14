package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Brand Palette
val BrandPurple = Color(0xFF8B5CF6)
val BrandPurpleDark = Color(0xFF6D28D9)
val BrandPurpleLight = Color(0xFFA78BFA)

val NeonCyan = Color(0xFF06B6D4)
val NeonCyanLight = Color(0xFF38BDF8)
val NeonCyanDark = Color(0xFF0891B2)

val DiamondCyan = Color(0xFF22D3EE)
val GoldAccent = Color(0xFFF59E0B)
val GoldLight = Color(0xFFFDE68A)

// Pure Dark Palette Tokens
val PureDarkBackground = Color(0xFF0A0915)
val PureDarkSurface = Color(0xFF131127)
val PureDarkSurfaceElevated = Color(0xFF1C1938)
val PureDarkSurfaceBorder = Color(0xFF2A2750)

// Pure Light Palette Tokens
val LightBackground = Color(0xFFF1F5F9)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFE2E8F0)
val LightSurfaceBorder = Color(0xFFCBD5E1)

// Dynamic Background & Surface Tokens (reactively respond to ThemeController.isDarkMode)
val DarkBackground: Color
    get() = if (ThemeController.isDarkMode) PureDarkBackground else LightBackground

val DarkSurface: Color = PureDarkSurface
val DarkSurfaceElevated: Color = PureDarkSurfaceElevated

val DarkSurfaceBorder: Color
    get() = if (ThemeController.isDarkMode) PureDarkSurfaceBorder else Color(0xFF475569)

val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextTertiaryDark = Color(0xFF64748B)

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)

val SuccessGreen = Color(0xFF10B981)
val ErrorRed = Color(0xFFEF4444)
val VipGold = Color(0xFFFFB800)
val VipGoldDark = Color(0xFFD97706)
