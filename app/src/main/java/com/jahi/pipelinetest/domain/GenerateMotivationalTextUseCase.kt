package com.jahi.pipelinetest.domain

import android.content.Context
import android.util.Log
import com.jahi.pipelinetest.gallery.data.Model
import com.jahi.pipelinetest.gallery.ui.llmchat.LlmChatModelHelper
import com.jahi.pipelinetest.domain.GetInitializedLlmModelUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class GenerateMotivationalTextUseCase(
    private val context: Context,
    private val getInitializedLlmModelUseCase: GetInitializedLlmModelUseCase
) {
    companion object {
        private const val TAG = "GenerateMotivationalText"
        private const val GENERATION_TIMEOUT_MS = 30_000L // 30 seconds for generation
    }

    // Fallback sentences when AI is not available
    private val fallbackSentences = listOf(
        "You can achieve anything you set your mind to.",
        "Believe in yourself and all that you are.",
        "Every day is a chance to get better.",
        "Success is not final, failure is not fatal: It is the courage to continue that counts.",
        "The only way to do great work is to love what you do.",
        "Don't watch the clock; do what it does. Keep going.",
        "The future depends on what you do today.",
        "Difficult roads often lead to beautiful destinations.",
        "Your limitation—it's only your imagination.",
        "Great things never come from comfort zones."
    )

    // Prompts for generating motivational sentences with examples
    private val motivationalPrompts = listOf(
        "Write one motivational sentence about success. Example: Success comes to those who never give up. Your response:",
        "Create an inspiring quote about perseverance. Example: Every setback is a setup for a comeback. Your response:",
        "Generate a positive message about believing in yourself. Example: You are stronger than you think. Your response:",
        "Write an encouraging sentence about overcoming challenges. Example: Challenges are opportunities in disguise. Your response:",
        "Create a motivational quote about personal growth. Example: Growth begins at the end of your comfort zone. Your response:"
    )

    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        try {
            // Obtain an initialized LLM model from the repository
            val llmModel = getInitializedLlmModelUseCase()
            
            if (llmModel != null) {
                Log.d(TAG, "Found LLM model: ${llmModel.name}")
                
                // Try to generate with AI
                val generatedText = generateWithAI(llmModel)
                if (!generatedText.isNullOrBlank()) {
                    Log.d(TAG, "Generated text: $generatedText")
                    return@withContext generatedText
                }
            } else {
                Log.d(TAG, "No LLM model found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating text", e)
        }
        
        // Fallback to hardcoded sentences
        Log.d(TAG, "Using fallback sentence")
        return@withContext fallbackSentences.random()
    }


    private suspend fun generateWithAI(model: Model): String? {
        try {
            // Generate text with timeout using the already initialized model
            return withTimeoutOrNull(GENERATION_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    generateTextWithInitializedModel(model, continuation)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in AI generation", e)
            return null
        }
    }

    private fun generateTextWithInitializedModel(
        model: Model, 
        continuation: kotlinx.coroutines.CancellableContinuation<String?>
    ) {
        val prompt = motivationalPrompts.random()
        val fullResponse = StringBuilder()
        
        Log.d(TAG, "Running inference with prompt: $prompt")
        
        LlmChatModelHelper.runInference(
            model = model,
            input = prompt,
            resultListener = { partialResult, done ->
                Log.d(TAG, "Received partial result: '$partialResult', done: $done")
                fullResponse.append(partialResult)
                
                if (done) {
                    val generatedText = fullResponse.toString().trim()
                    Log.d(TAG, "Inference complete. Full response: '$generatedText'")
                    
                    // If response is empty, return null to use fallback
                    if (generatedText.isBlank()) {
                        Log.w(TAG, "Generated text is empty, will use fallback")
                        continuation.resume(null)
                    } else {
                        // Clean up the response
                        val cleanedText = cleanUpResponse(generatedText)
                        Log.d(TAG, "Cleaned response: '$cleanedText'")
                        continuation.resume(cleanedText)
                    }
                }
            },
            cleanUpListener = {
                Log.d(TAG, "Cleanup called")
            }
        )
    }

    private fun cleanUpResponse(text: String): String {
        // Remove any extra quotes or formatting
        var cleaned = text.trim()
        
        // Remove surrounding quotes if present
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length - 1).trim()
        }
        
        // Remove common prefixes that models might add
        val prefixesToRemove = listOf(
            "Here's a motivational sentence:",
            "Here is an inspiring quote:",
            "Here's an inspiring quote:",
            "Motivational quote:",
            "Inspiring message:",
            "Here's one:",
            "Quote:",
            "Here's an inspiring quote about perseverance:",
            "Here's an inspiring quote about",
            "Your response:",
            "My response:",
            "Response:"
        )
        
        for (prefix in prefixesToRemove) {
            if (cleaned.startsWith(prefix, ignoreCase = true)) {
                cleaned = cleaned.substring(prefix.length).trim()
                break
            }
        }
        
        // Also remove anything before the first quote if it starts with "Here"
        if (cleaned.startsWith("Here", ignoreCase = true)) {
            val quoteStart = cleaned.indexOf('"')
            if (quoteStart > 0) {
                cleaned = cleaned.substring(quoteStart).trim()
            }
        }
        
        // Remove newlines and extra whitespace
        cleaned = cleaned.replace("\n", " ").replace(Regex("\\s+"), " ").trim()
        
        // Remove common suffixes that models might add
        val suffixesToRemove = listOf(
            "Would you like me provide some other options?",
            "Would you like me to provide some other options?",
            "Would you like another one?",
            "Do you want more quotes?",
            "Let me know if you need more!"
        )
        
        for (suffix in suffixesToRemove) {
            if (cleaned.endsWith(suffix, ignoreCase = true)) {
                cleaned = cleaned.substring(0, cleaned.length - suffix.length).trim()
                break
            }
        }
        
        // If still empty after cleaning, return null for fallback
        if (cleaned.isBlank()) {
            return fallbackSentences.random()
        }
        
        // Take only the first sentence if multiple are generated
        val sentences = cleaned.split(Regex("[.!?]"))
        val firstSentence = sentences.firstOrNull()?.trim() ?: cleaned
        
        // Ensure it ends with proper punctuation and isn't too long
        val finalSentence = when {
            firstSentence.endsWith(".") || firstSentence.endsWith("!") || firstSentence.endsWith("?") -> firstSentence
            firstSentence.isNotBlank() -> "$firstSentence."
            else -> fallbackSentences.random()
        }
        
        // If sentence is too long, truncate it reasonably
        return if (finalSentence.length > 150) {
            val truncated = finalSentence.substring(0, 147).trim()
            if (truncated.endsWith(".") || truncated.endsWith("!") || truncated.endsWith("?")) {
                truncated
            } else {
                "$truncated..."
            }
        } else {
            finalSentence
        }
    }
}