package com.jahi.pipelinetest.domain

import android.content.Context
import android.util.Log
import com.jahi.pipelinetest.gallery.data.Model
import timber.log.Timber
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
    }
    
    private var generationCount = 0

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

    // Diverse motivational prompts with randomness
    private val motivationalPrompts = listOf(
        "Write a unique motivational quote about success.",
        "Give me an inspiring message about perseverance.",
        "Write a positive quote about believing in yourself.",
        "Create a motivational message about overcoming challenges.",
        "Share an inspiring quote about personal growth.",
        "Generate an original quote about achieving dreams.",
        "Write something motivational about taking action.",
        "Create an inspiring message about resilience.",
        "Give me a powerful quote about never giving up.",
        "Write a motivational quote about self-confidence.",
        "Share an uplifting message about inner strength.",
        "Create an inspiring quote about facing fears.",
        "Write a motivational message about learning from failure.",
        "Generate a unique quote about pursuing excellence.",
        "Give me an inspiring message about staying focused.",
        "Write a powerful quote about making a difference.",
        "Create a motivational message about embracing change.",
        "Share an inspiring quote about unlimited potential.",
        "Write something uplifting about finding your purpose.",
        "Generate an original motivational quote about courage."
    )

    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("invoke() - Starting motivational text generation")
        
        try {
            // Obtain an initialized LLM model from the repository
            Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("invoke() - Requesting initialized LLM model from use case")
            val llmModel = getInitializedLlmModelUseCase()
            
            if (llmModel != null) {
                Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("invoke() - Found LLM model: ${llmModel.name}, path: ${llmModel.downloadFileName}")
                
                // Try to generate with AI
                Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("invoke() - Attempting AI text generation")
                val generatedText = generateWithAI(llmModel)
                if (!generatedText.isNullOrBlank()) {
                    Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("invoke() - AI generated text: '$generatedText'")
                    return@withContext generatedText
                } else {
                    Timber.tag("DEBUG_FLOW|GenerateMotivationalText").w("invoke() - AI generation returned null/blank")
                }
            } else {
                Timber.tag("DEBUG_FLOW|GenerateMotivationalText").w("invoke() - No LLM model found - will use fallback")
            }
        } catch (e: Exception) {
            Timber.tag("DEBUG_FLOW|GenerateMotivationalText").e(e, "invoke() - Error generating text")
        }
        
        // Fallback to hardcoded sentences
        val fallbackText = fallbackSentences.random()
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("invoke() - Using fallback sentence: '$fallbackText'")
        return@withContext fallbackText
    }


    private suspend fun generateWithAI(model: Model): String? {
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateWithAI() - Starting AI generation with model: ${model.name}")
        
        try {
            // Wait for instance to be initialized (following LlmChatViewModel pattern)
            while (model.instance == null) {
                kotlinx.coroutines.delay(100)
            }
            kotlinx.coroutines.delay(500)
            
            // Generate text with shorter timeout to prevent infinite loops
            val result = withTimeoutOrNull(30_000L) { // 30 seconds max
                suspendCancellableCoroutine { continuation ->
                    generateTextWithInitializedModel(model, continuation)
                }
            }
            
            if (result != null) {
                Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateWithAI() - AI generation completed successfully")
            } else {
                Timber.tag("DEBUG_FLOW|GenerateMotivationalText").w("generateWithAI() - AI generation timed out after 10 seconds")
            }
            
            return result
        } catch (e: Exception) {
            Timber.tag("DEBUG_FLOW|GenerateMotivationalText").e(e, "generateWithAI() - Error in AI generation")
            return null
        }
    }

    private fun generateTextWithInitializedModel(
        model: Model, 
        continuation: kotlinx.coroutines.CancellableContinuation<String?>
    ) {
        // Add extra randomness to prevent repetition
        val basePrompt = motivationalPrompts.random()
        val randomEndings = listOf(
            " Make it unique and original.",
            " Be creative and inspiring.",
            " Use fresh language and perspective.",
            " Make it stand out from typical quotes.",
            " Be innovative in your approach."
        )
        val prompt = if (kotlin.random.Random.nextFloat() < 0.7f) {
            basePrompt + randomEndings.random()
        } else {
            basePrompt
        }
        val fullResponse = StringBuilder()
        var tokenCount = 0
        val maxTokens = 150 // Increased limit for complete motivational quotes
        var isCompleted = false // Flag to prevent multiple resume calls
        
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateTextWithInitializedModel() - Running inference with prompt: '$prompt'")
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateTextWithInitializedModel() - Model instance available: ${model.instance != null}")
        
        // Reset session every 5 generations to prevent repetitive responses while maintaining some context
        generationCount++
        if (generationCount % 5 == 0) {
            try {
                Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateTextWithInitializedModel() - Resetting session after $generationCount generations to prevent repetition")
                LlmChatModelHelper.resetSession(model)
                Thread.sleep(100) // Small delay to ensure reset is complete
            } catch (e: Exception) {
                Timber.tag("DEBUG_FLOW|GenerateMotivationalText").e(e, "generateTextWithInitializedModel() - Failed to reset session")
            }
        }
        
        LlmChatModelHelper.runInference(
            model = model,
            input = prompt,
            resultListener = { partialResult, done ->
                // Ignore callbacks if we've already completed
                if (isCompleted) {
                    Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateTextWithInitializedModel() - Ignoring callback, already completed")
                    return@runInference
                }
                
                Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateTextWithInitializedModel() - Received partial result: '$partialResult', done: $done")
                fullResponse.append(partialResult)
                tokenCount++
                
                // Check for problematic patterns that indicate infinite loops
                val currentText = fullResponse.toString()
                if (tokenCount > maxTokens || currentText.length > 1000 || 
                    (currentText.contains("**") || currentText.contains("*\n*") || currentText.count { it == '*' } > 10)) {
                    Timber.tag("DEBUG_FLOW|GenerateMotivationalText").w("generateTextWithInitializedModel() - Detected problematic generation (tokens: $tokenCount, length: ${currentText.length}), stopping")
                    if (!isCompleted) {
                        isCompleted = true
                        continuation.resume(null)
                    }
                    return@runInference
                }
                
                if (done) {
                    val generatedText = fullResponse.toString().trim()
                    Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateTextWithInitializedModel() - Inference complete. Full response: '$generatedText'")
                    
                    if (!isCompleted) {
                        isCompleted = true
                        
                        // If response is empty, return null to use fallback
                        if (generatedText.isBlank()) {
                            Timber.tag("DEBUG_FLOW|GenerateMotivationalText").w("generateTextWithInitializedModel() - Generated text is empty, will use fallback")
                            continuation.resume(null)
                        } else {
                            // Clean up the response
                            val cleanedText = cleanUpResponse(generatedText)
                            Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateTextWithInitializedModel() - Cleaned response: '$cleanedText'")
                            continuation.resume(cleanedText)
                        }
                    }
                }
            },
            cleanUpListener = {
                Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("generateTextWithInitializedModel() - Cleanup called")
            }
        )
    }

    private fun cleanUpResponse(text: String): String {
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("cleanUpResponse() - Original text: '$text'")
        
        // Clean up for TTS processing but keep full content
        var cleaned = text.trim()
            .replace(Regex("\\*+"), "") // Remove asterisks
            .replace(Regex("#+"), "") // Remove hashtags
            .replace("\n", " ") // Replace newlines with spaces
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .trim()
        
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("cleanUpResponse() - After basic cleanup: '$cleaned'")
        
        // Remove attribution/author info BEFORE removing quotes
        cleaned = cleaned.replace(Regex("\\s*-\\s*This quote.*"), "")
            .replace(Regex("\\s*-\\s*[A-Z][a-zA-Z\\s]*$"), "") // Remove "- Author Name" at end
            .trim()
        
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("cleanUpResponse() - After attribution removal: '$cleaned'")
        
        // Remove surrounding quotes if present
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length - 1).trim()
        }
        
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("cleanUpResponse() - After quote removal: '$cleaned'")
        
        // If empty after cleaning, return fallback
        if (cleaned.isBlank()) {
            return fallbackSentences.random()
        }
        
        // Ensure proper ending punctuation
        val finalCleaned = when {
            cleaned.endsWith(".") || cleaned.endsWith("!") || cleaned.endsWith("?") -> cleaned
            cleaned.isNotBlank() -> "$cleaned."
            else -> fallbackSentences.random()
        }
        
        Timber.tag("DEBUG_FLOW|GenerateMotivationalText").d("cleanUpResponse() - Final result: '$finalCleaned'")
        
        // Return full cleaned text, no truncation
        return if (finalCleaned.isBlank() || finalCleaned.length < 10) {
            fallbackSentences.random()
        } else {
            finalCleaned
        }
    }
}