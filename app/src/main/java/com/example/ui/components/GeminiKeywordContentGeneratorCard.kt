package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.service.GeminiSocialContentService
import com.example.data.service.SocialContentResult
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SuccessGreen
import com.example.util.SocialShareHelper
import kotlinx.coroutines.launch

private val AccentPink = Color(0xFFEC4899)

/**
 * Reusable Compose UI component that allows users to input keywords (typed or chip-selected),
 * customize platform & format, and trigger an AI content generation request via the configured
 * Gemini API ([GeminiSocialContentService] using `BuildConfig.GEMINI_API_KEY`).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeminiKeywordContentGeneratorCard(
    modifier: Modifier = Modifier,
    isVip: Boolean = false,
    onOpenFullGeminiStudio: (() -> Unit)? = null,
    onContentGenerated: ((SocialContentResult) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val geminiService = remember { GeminiSocialContentService() }
    val triggerInterstitial = rememberMediatedInterstitialTrigger(isVip = isVip)

    val apiKeyConfigured = remember {
        try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        } catch (_: Throwable) {
            false
        }
    }

    var keywordInput by remember { mutableStateOf("") }
    val activeKeywords = remember {
        mutableStateListOf("Viral Growth", "Creator Economy", "Reels Hook")
    }

    val suggestedKeywords = remember {
        listOf(
            "AI Productivity",
            "Passive Income",
            "YouTube Shorts",
            "Fitness Hacks",
            "Tech Review",
            "Storytelling",
            "Daily Vlog",
            "Digital Marketing"
        )
    }

    val platforms = remember {
        listOf("Instagram Reels", "YouTube Shorts", "TikTok", "X / Twitter", "LinkedIn")
    }
    var selectedPlatform by remember { mutableStateOf(platforms.first()) }

    val contentFormats = remember {
        listOf("Complete Viral Suite", "Viral 60s Script", "Engaging Caption", "High-CTR Hooks")
    }
    var selectedFormat by remember { mutableStateOf(contentFormats.first()) }

    val tones = remember {
        listOf("High Energy & Viral", "Educational & Actionable", "Storytelling & Relatable")
    }
    var selectedTone by remember { mutableStateOf(tones.first()) }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<SocialContentResult?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val addKeywordFromInput = {
        val raw = keywordInput.trim()
        if (raw.isNotEmpty()) {
            raw.split(",", ";")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { token ->
                    if (activeKeywords.none { it.equals(token, ignoreCase = true) }) {
                        activeKeywords.add(token)
                    }
                }
            keywordInput = ""
        }
    }

    val triggerGeneration: () -> Unit = {
        // Also capture any un-added text currently in the input field
        val pendingText = keywordInput.trim()
        val combinedKeywords = buildList {
            addAll(activeKeywords)
            if (pendingText.isNotEmpty()) {
                pendingText.split(",", ";")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .forEach { token ->
                        if (none { it.equals(token, ignoreCase = true) }) {
                            add(token)
                        }
                    }
            }
        }

        if (combinedKeywords.isEmpty()) {
            Toast.makeText(
                context,
                "Please enter at least one keyword to generate AI content.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (!isGenerating) {
            if (pendingText.isNotEmpty()) {
                addKeywordFromInput()
            }
            triggerInterstitial("Gemini Keyword Content Generation") {
                isGenerating = true
                statusMessage = "Generating viral content with Gemini API..."
                coroutineScope.launch {
                    val promptString = "Keywords: ${combinedKeywords.joinToString(", ")}"
                    val result = geminiService.generateSocialContent(
                        prompt = promptString,
                        platform = selectedPlatform,
                        tone = selectedTone,
                        contentType = selectedFormat
                    )
                    generatedResult = result
                    isGenerating = false
                    statusMessage = if (result.isRealApiCall) {
                        "Generated live via Google Gemini API"
                    } else {
                        result.errorMessage ?: "Generated with Gemini Creator Blueprint"
                    }
                    onContentGenerated?.invoke(result)
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gemini_keyword_generator_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(BrandPurple, NeonCyan, AccentPink))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(BrandPurple, NeonCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini Keyword AI Generator",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Gemini Keyword Content Studio",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Enter keywords to generate hooks, scripts & hashtags via Gemini API",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // API Status Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (apiKeyConfigured) SuccessGreen.copy(alpha = 0.16f) else BrandPurple.copy(alpha = 0.22f),
                    border = BorderStroke(
                        1.dp,
                        if (apiKeyConfigured) SuccessGreen else NeonCyan
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Gemini API Status",
                            tint = if (apiKeyConfigured) SuccessGreen else NeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (apiKeyConfigured) "Gemini Live" else "Gemini API",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (apiKeyConfigured) SuccessGreen else NeonCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Keyword Input TextField + Add Button
            OutlinedTextField(
                value = keywordInput,
                onValueChange = { keywordInput = it },
                label = {
                    Text(
                        text = "Enter keywords (e.g. AI tools, Reels growth, Tech)",
                        fontSize = 12.sp
                    )
                },
                placeholder = {
                    Text(
                        text = "Type keyword(s) and tap + or Generate...",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = "Keyword Input",
                        tint = NeonCyan
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (keywordInput.isNotBlank()) {
                            IconButton(
                                onClick = { addKeywordFromInput() },
                                modifier = Modifier.testTag("gemini_add_keyword_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add keyword chip",
                                    tint = SuccessGreen
                                )
                            }
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (keywordInput.isNotBlank()) {
                            addKeywordFromInput()
                        } else {
                            triggerGeneration()
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedLabelColor = NeonCyan,
                    unfocusedLabelColor = Color(0xFF94A3B8),
                    cursorColor = NeonCyan,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_keyword_input_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Active Keywords Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Keywords (${activeKeywords.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                if (activeKeywords.isNotEmpty()) {
                    Text(
                        text = "Clear All",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentPink,
                        modifier = Modifier
                            .clickable { activeKeywords.clear() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("gemini_clear_keywords_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (activeKeywords.isEmpty()) {
                Text(
                    text = "No active chips yet — type keywords above or tap a trending keyword below.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    activeKeywords.forEach { keyword ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = BrandPurple.copy(alpha = 0.28f),
                            border = BorderStroke(1.dp, BrandPurple)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { activeKeywords.remove(keyword) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#$keyword",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove $keyword",
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Suggested Keywords Row
            Text(
                text = "Quick Suggested Keywords (Tap to add):",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(end = 4.dp)
            ) {
                items(suggestedKeywords) { suggestion ->
                    val isAdded = activeKeywords.any { it.equals(suggestion, ignoreCase = true) }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isAdded) SuccessGreen.copy(alpha = 0.2f) else DarkSurfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isAdded) SuccessGreen else Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .clickable {
                                if (isAdded) {
                                    activeKeywords.removeAll { it.equals(suggestion, ignoreCase = true) }
                                } else {
                                    activeKeywords.add(suggestion)
                                }
                            }
                            .testTag("gemini_suggested_keyword_${suggestion.replace(" ", "_").lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAdded) Icons.Default.CheckCircle else Icons.Default.Add,
                                contentDescription = suggestion,
                                tint = if (isAdded) SuccessGreen else NeonCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = suggestion,
                                fontSize = 11.sp,
                                color = if (isAdded) SuccessGreen else Color(0xFFE2E8F0),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Platform & Format Selector Pills
            Text(
                text = "Target Platform & Output Format:",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(platforms) { platform ->
                    val selected = platform == selectedPlatform
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) BrandPurple else DarkSurfaceElevated)
                            .border(
                                1.dp,
                                if (selected) NeonCyan else Color(0xFF334155),
                                RoundedCornerShape(50)
                            )
                            .clickable { selectedPlatform = platform }
                            .padding(horizontal = 11.dp, vertical = 5.dp)
                            .testTag("gemini_keyword_platform_${platform.replace(" ", "_").lowercase()}")
                    ) {
                        Text(
                            text = platform,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(contentFormats) { format ->
                    val selected = format == selectedFormat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) Color(0xFF0284C7) else DarkSurfaceElevated)
                            .border(
                                1.dp,
                                if (selected) NeonCyan else Color(0xFF334155),
                                RoundedCornerShape(50)
                            )
                            .clickable { selectedFormat = format }
                            .padding(horizontal = 11.dp, vertical = 5.dp)
                            .testTag("gemini_keyword_format_${format.replace(" ", "_").lowercase()}")
                    ) {
                        Text(
                            text = format,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Generate CTA Button
            Button(
                onClick = { triggerGeneration() },
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPurple,
                    disabledContainerColor = BrandPurple.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("gemini_keyword_generate_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generating with Gemini AI...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate AI Content from Keywords",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            // Generated Output Display
            AnimatedVisibility(visible = generatedResult != null) {
                val result = generatedResult
                if (result != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                            .testTag("gemini_keyword_result_container")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (result.isRealApiCall) "Gemini Live Output" else "Gemini AI Content Ready",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SuccessGreen
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        copyTextToClipboard(
                                            context = context,
                                            label = "Gemini AI Content",
                                            text = "${result.hook}\n\n${result.bodyCaption}\n\n${result.callToAction}\n\n${result.hashtags.joinToString(" ")}"
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    border = BorderStroke(1.dp, NeonCyan),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("gemini_keyword_copy_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy generated content",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Copy All",
                                        fontSize = 11.sp,
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        SocialShareHelper.shareAiContent(
                                            context = context,
                                            platform = selectedPlatform,
                                            format = selectedFormat,
                                            hook = result.hook,
                                            bodyCaption = result.bodyCaption,
                                            callToAction = result.callToAction,
                                            hashtags = result.hashtags
                                        )
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    border = BorderStroke(1.dp, SuccessGreen),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("gemini_keyword_share_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share generated content",
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Share",
                                        fontSize = 11.sp,
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(
                                    onClick = { triggerGeneration() },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("gemini_keyword_regenerate_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Regenerate",
                                        tint = GoldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        if (!statusMessage.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = statusMessage.orEmpty(),
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Hook Section
                        Text(
                            text = "🔥 Pattern-Interrupt Hook (0-3s)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.hook,
                            fontSize = 12.sp,
                            color = Color.White,
                            lineHeight = 17.sp,
                            modifier = Modifier.testTag("gemini_keyword_result_hook")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Main Script / Caption Body
                        Text(
                            text = "📝 Script & Caption ($selectedPlatform • $selectedFormat)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.bodyCaption,
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 17.sp,
                            modifier = Modifier.testTag("gemini_keyword_result_body")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Call to Action
                        Text(
                            text = "🎯 Call to Action",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentPink
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.callToAction,
                            fontSize = 12.sp,
                            color = Color.White,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Hashtags FlowRow
                        Text(
                            text = "#️⃣ High-Ranking Hashtags",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            result.hashtags.forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = BrandPurple.copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, BrandPurple.copy(alpha = 0.6f)),
                                    modifier = Modifier.clickable {
                                        copyTextToClipboard(context, "Hashtag", tag)
                                    }
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 11.sp,
                                        color = NeonCyan,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        if (onOpenFullGeminiStudio != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = onOpenFullGeminiStudio,
                                border = BorderStroke(1.dp, BrandPurple),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("gemini_open_full_studio_from_card")
                            ) {
                                Text(
                                    text = "Open Full Gemini Social Studio →",
                                    fontSize = 12.sp,
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
}

private fun copyTextToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
}
