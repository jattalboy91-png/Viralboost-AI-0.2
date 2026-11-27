package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.service.GeminiSocialContentService
import com.example.data.service.SocialContentResult
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiAiViewModel"

sealed interface GeminiAiUiState {
    data object Idle : GeminiAiUiState
    data object Loading : GeminiAiUiState
    data class Streaming(val text: String) : GeminiAiUiState
    data class Success(val text: String, val structured: SocialContentResult? = null) : GeminiAiUiState
    data class Error(val message: String) : GeminiAiUiState
}

/**
 * ViewModel for Firebase AI Logic (`Firebase.ai.generativeModel("gemini-3.8-flash")`)
 * with lazy model initialization, 60-second coroutine timeouts, streaming support,
 * and transparent error propagation.
 */
class GeminiAiViewModel : ViewModel() {

    private val models by lazy {
        listOf(
            Firebase.ai.generativeModel("gemini-3.8-flash"),
            Firebase.ai.generativeModel("gemini-3.5-flash-lite")
        )
    }

    private val socialService by lazy {
        GeminiSocialContentService()
    }

    private val _uiState = MutableStateFlow<GeminiAiUiState>(GeminiAiUiState.Idle)
    val uiState: StateFlow<GeminiAiUiState> = _uiState.asStateFlow()

    fun generateContent(prompt: String) {
        viewModelScope.launch {
            _uiState.value = GeminiAiUiState.Loading
            var lastError: Exception? = null
            for (m in models) {
                try {
                    val response = withTimeout(60_000L) {
                        m.generateContent(prompt)
                    }
                    val text = response.text
                    if (!text.isNullOrBlank()) {
                        _uiState.value = GeminiAiUiState.Success(text = text)
                        return@launch
                    } else {
                        lastError = IllegalStateException("Model returned an empty response.")
                    }
                } catch (e: CancellationException) {
                    if (e is TimeoutCancellationException) {
                        lastError = e
                    } else {
                        throw e
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Firebase AI generation failed, trying fallback model...", e)
                    lastError = e
                }
            }
            _uiState.value = GeminiAiUiState.Error(
                lastError?.localizedMessage ?: "Generation failed."
            )
        }
    }

    fun generateStream(prompt: String) {
        viewModelScope.launch {
            _uiState.value = GeminiAiUiState.Loading
            var lastError: Exception? = null
            for (m in models) {
                val accumulatedText = StringBuilder()
                try {
                    withTimeout(60_000L) {
                        m.generateContentStream(prompt).collect { chunk ->
                            val chunkText = chunk.text.orEmpty()
                            accumulatedText.append(chunkText)
                            _uiState.value = GeminiAiUiState.Streaming(accumulatedText.toString())
                        }
                    }
                    if (accumulatedText.isNotBlank()) {
                        _uiState.value = GeminiAiUiState.Success(text = accumulatedText.toString())
                        return@launch
                    } else {
                        lastError = IllegalStateException("Model returned an empty response.")
                    }
                } catch (e: CancellationException) {
                    if (e is TimeoutCancellationException) {
                        lastError = e
                    } else {
                        throw e
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Streaming failed on primary model, trying fallback...", e)
                    lastError = e
                }
            }
            _uiState.value = GeminiAiUiState.Error(
                lastError?.localizedMessage ?: "Streaming failed."
            )
        }
    }

    fun generateStructuredSocialContent(
        prompt: String,
        platform: String,
        tone: String,
        contentType: String,
        onComplete: ((SocialContentResult) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.value = GeminiAiUiState.Loading
            try {
                val result = socialService.generateSocialContent(
                    prompt = prompt,
                    platform = platform,
                    tone = tone,
                    contentType = contentType
                )
                if (result.isRealApiCall || result.errorMessage == null) {
                    _uiState.value = GeminiAiUiState.Success(
                        text = result.fullResponse,
                        structured = result
                    )
                } else {
                    _uiState.value = GeminiAiUiState.Error(result.errorMessage)
                }
                onComplete?.invoke(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Structured social content generation failed", e)
                _uiState.value = GeminiAiUiState.Error(
                    e.localizedMessage ?: "Failed to generate content."
                )
            }
        }
    }
}
