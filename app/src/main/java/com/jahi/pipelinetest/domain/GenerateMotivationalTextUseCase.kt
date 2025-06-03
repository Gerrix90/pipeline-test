package com.jahi.pipelinetest.domain

import android.content.Context
import android.util.Log
import com.jahi.pipelinetest.gallery.data.AppContainer
import com.jahi.pipelinetest.gallery.data.Model
import com.jahi.pipelinetest.gallery.ui.llmchat.LlmChatModelHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import kotlin.coroutines.resume

class GenerateMotivationalTextUseCase(
    private val context: Context,
    @Suppress("unused") private val appContainer: AppContainer
) {
    companion object {
        private const val TAG = "GenerateMotivationalText"
        private const val INITIALIZATION_TIMEOUT_MS = 120_000L // 2 minutes for initialization
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
            // Try to find and use a downloaded LLM model
            val llmModel = findDownloadedLlmModel()
            
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

    private fun findDownloadedLlmModel(): Model? {
        val externalDir = context.getExternalFilesDir(null)
        if (externalDir != null) {
            Log.d(TAG, "External files dir: ${externalDir.absolutePath}")
            
            // List all directories to see what's there
            val directories = externalDir.listFiles { file -> file.isDirectory }
            directories?.forEach { dir ->
                Log.d(TAG, "Found directory: ${dir.name}")
            }
            
            // Check __imports directory (note the double underscore)
            val importsDir = File(externalDir, "__imports")
            Log.d(TAG, "Checking __imports directory: ${importsDir.absolutePath}, exists: ${importsDir.exists()}")
            
            if (importsDir.exists()) {
                val files = importsDir.listFiles()
                Log.d(TAG, "Found ${files?.size ?: 0} files in __imports")
                
                files?.forEach { file ->
                    Log.d(TAG, "Found file: ${file.name}, size: ${file.length() / 1024 / 1024}MB")
                    
                    // Check for .task or .bin files that look like LLM models
                    if ((file.name.endsWith(".task") || file.name.endsWith(".bin")) && 
                        file.length() > 10_000_000) { // At least 10MB
                        
                        // Create a model for this file
                        val model = Model(
                            name = file.nameWithoutExtension,
                            downloadFileName = "__imports/${file.name}", // Include __imports in the path
                            sizeInBytes = file.length(),
                            version = "imported",
                            url = "",
                            imported = true
                        )
                        model.preProcess()
                        
                        Log.d(TAG, "Created imported model: ${model.name}")
                        Log.d(TAG, "Model path will be: ${model.getPath(context)}")
                        return model
                    }
                }
            }
            
            // Also check for any downloaded models in versioned directories
            val modelDirs = externalDir.listFiles { file -> 
                file.isDirectory && !file.name.startsWith("__")
            }
            
            modelDirs?.forEach { modelDir ->
                Log.d(TAG, "Checking model directory: ${modelDir.name}")
                
                // Look for version directories inside
                val versionDirs = modelDir.listFiles { file -> file.isDirectory }
                versionDirs?.forEach { versionDir ->
                    Log.d(TAG, "Checking version directory: ${versionDir.name}")
                    
                    // Look for model files
                    val modelFiles = versionDir.listFiles { file ->
                        file.name.endsWith(".task") || file.name.endsWith(".bin")
                    }
                    
                    modelFiles?.forEach { modelFile ->
                        Log.d(TAG, "Found model file: ${modelFile.name} in ${modelDir.name}/${versionDir.name}")
                        
                        if (modelFile.length() > 10_000_000) { // At least 10MB
                            val model = Model(
                                name = modelDir.name,
                                downloadFileName = modelFile.name,
                                sizeInBytes = modelFile.length(),
                                version = versionDir.name,
                                url = "",
                                imported = false
                            )
                            model.preProcess()
                            
                            Log.d(TAG, "Created model from directory: ${model.name}")
                            return model
                        }
                    }
                }
            }
        }
        
        Log.d(TAG, "No LLM model found in any location")
        return null
    }

    private suspend fun generateWithAI(model: Model): String? {
        try {
            // Initialize the model if needed
            if (model.instance == null) {
                Log.d(TAG, "Initializing model ${model.name}")
                
                val initResult = withTimeoutOrNull(INITIALIZATION_TIMEOUT_MS) {
                    suspendCancellableCoroutine { continuation ->
                        LlmChatModelHelper.initialize(
                            context = context,
                            model = model,
                            onDone = { error ->
                                if (error.isNotEmpty()) {
                                    Log.e(TAG, "Failed to initialize model: $error")
                                    continuation.resume(false)
                                } else {
                                    Log.d(TAG, "Model initialized successfully")
                                    continuation.resume(true)
                                }
                            }
                        )
                    }
                }
                
                if (initResult != true) {
                    Log.e(TAG, "Model initialization failed or timed out")
                    return null
                }
            }
            
            // Now generate text with timeout
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