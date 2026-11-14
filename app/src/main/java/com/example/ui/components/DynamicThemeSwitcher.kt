package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.BrandPurpleDark
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ThemeController

/**
 * Global Material 3 Dynamic Theme Switcher Top Bar.
 * Allows users to toggle between 'Dark' and 'Light' mode for the entire application interface
 * with 1 tap from any screen.
 */
@Composable
fun DynamicThemeTopBar(
    isDarkTheme: Boolean = ThemeController.isDarkMode,
    onToggleTheme: (Boolean) -> Unit = { isDark ->
        ThemeController.setDarkMode(isDark)
    },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val barBgColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.surface,
        animationSpec = tween(250),
        label = "TopBarBg"
    )
    val borderColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.outline,
        animationSpec = tween(250),
        label = "TopBarBorder"
    )
    val titleColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(250),
        label = "TopBarTitle"
    )

    Surface(
        color = barBgColor,
        tonalElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.8.dp, color = borderColor)
            .testTag("dynamic_theme_top_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: App Brand Identity & Official Play Store Logo Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.viralboost_store_icon_1794678253429),
                    contentDescription = "ViralBoost AI Store Logo",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.2.dp, GoldAccent, RoundedCornerShape(10.dp))
                        .testTag("top_bar_app_logo_icon")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "ViralBoost AI",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = titleColor
                    )
                    Text(
                        text = if (isDarkTheme) "🌙 Dark Mode Active" else "☀️ Light Mode Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Right: Segmented Dark / Light Pill Switcher + Material 3 Switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Interactive Dark / Light Segmented Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .padding(3.dp)
                        .testTag("theme_toggle_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dark Option
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isDarkTheme) BrandPurple else Color.Transparent
                            )
                            .clickable {
                                if (!isDarkTheme) {
                                    ThemeController.setDarkMode(true, context)
                                    onToggleTheme(true)
                                    Toast.makeText(context, "🌙 Switched to Dark Mode", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("theme_option_dark"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = "Dark Mode",
                                tint = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Dark",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Light Option
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (!isDarkTheme) GoldAccent else Color.Transparent
                            )
                            .clickable {
                                if (isDarkTheme) {
                                    ThemeController.setDarkMode(false, context)
                                    onToggleTheme(false)
                                    Toast.makeText(context, "☀️ Switched to Light Mode", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("theme_option_light"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LightMode,
                                contentDescription = "Light Mode",
                                tint = if (!isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Light",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Material 3 Switch for direct toggle
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { checked ->
                        ThemeController.setDarkMode(checked, context)
                        onToggleTheme(checked)
                        Toast.makeText(
                            context,
                            if (checked) "🌙 Dark Mode Enabled" else "☀️ Light Mode Enabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    thumbContent = {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = if (isDarkTheme) "Dark Theme" else "Light Theme",
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandPurple,
                        uncheckedThumbColor = Color.Black,
                        uncheckedTrackColor = GoldAccent
                    ),
                    modifier = Modifier.testTag("theme_mode_switch")
                )
            }
        }
    }
}

/**
 * Full-width Material 3 Appearance & Theme Switcher Card for Profile/Settings or Dashboard views.
 */
@Composable
fun DynamicThemeSwitcherCard(
    isDarkTheme: Boolean = ThemeController.isDarkMode,
    onToggleTheme: (Boolean) -> Unit = { isDark ->
        ThemeController.setDarkMode(isDark)
    },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dynamic_theme_switcher_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Interface Appearance (Material 3)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isDarkTheme) "Active: Cyber Dark Mode 🌙" else "Active: Daylight Light Mode ☀️",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { isDark ->
                        ThemeController.setDarkMode(isDark, context)
                        onToggleTheme(isDark)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandPurpleDark,
                        uncheckedThumbColor = Color.Black,
                        uncheckedTrackColor = GoldAccent
                    ),
                    modifier = Modifier.testTag("profile_theme_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dark Mode Option Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isDarkTheme) BrandPurple else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            1.dp,
                            if (isDarkTheme) BrandPurple else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            ThemeController.setDarkMode(true, context)
                            onToggleTheme(true)
                        }
                        .testTag("select_dark_mode_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dark Mode",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Light Mode Option Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (!isDarkTheme) GoldAccent else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            1.dp,
                            if (!isDarkTheme) GoldAccent else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            ThemeController.setDarkMode(false, context)
                            onToggleTheme(false)
                        }
                        .testTag("select_light_mode_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (!isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Light Mode",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
