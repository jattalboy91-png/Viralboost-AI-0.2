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

    private val model by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val socialService by lazy {
        GeminiSocialContentService()
    }

    private val _uiState = MutableStateFlow<GeminiAiUiState>(GeminiAiUiState.Idle)
    val uiState: StateFlow<GeminiAiUiState> = _uiState.asStateFlow()

    fun generateContent(prompt: String) {
        viewModelScope.launch {
            _uiState.value = GeminiAiUiState.Loading
            try {
                val response = withTimeout(60_000L) {
                    model.generateContent(prompt)
                }
                val text = response.text
                if (!text.isNullOrBlank()) {
                    _uiState.value = GeminiAiUiState.Success(text = text)
                } else {
                    _uiState.value = GeminiAiUiState.Error("Model returned an empty response.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Generation timed out", e)
                _uiState.value = GeminiAiUiState.Error(
                    "Generation timed out after 60 seconds. Please try again."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI generation failed", e)
                _uiState.value = GeminiAiUiState.Error(
                    e.localizedMessage ?: "Generation failed."
                )
            }
        }
    }

    fun generateStream(prompt: String) {
        viewModelScope.launch {
            _uiState.value = GeminiAiUiState.Loading
            val accumulatedText = StringBuilder()
            try {
                withTimeout(60_000L) {
                    model.generateContentStream(prompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulatedText.append(chunkText)
                        _uiState.value = GeminiAiUiState.Streaming(accumulatedText.toString())
                    }
                }
                if (accumulatedText.isNotBlank()) {
                    _uiState.value = GeminiAiUiState.Success(text = accumulatedText.toString())
                } else {
                    _uiState.value = GeminiAiUiState.Error("Model returned an empty response.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Streaming timed out", e)
                _uiState.value = GeminiAiUiState.Error("Streaming timed out after 60 seconds.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Streaming generation failed", e)
                _uiState.value = GeminiAiUiState.Error(
                    e.localizedMessage ?: "Streaming failed."
                )
            }
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
