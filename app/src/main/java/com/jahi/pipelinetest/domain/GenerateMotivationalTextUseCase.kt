package com.jahi.pipelinetest.domain

import android.content.Context
import com.jahi.pipelinetest.gallery.data.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class GenerateMotivationalTextUseCase(
    @Suppress("unused") private val context: Context,
    @Suppress("unused") private val appContainer: AppContainer
) {
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

    // Additional AI-powered sentences (pre-generated for demo)
    private val aiGeneratedSentences = listOf(
        "Every sunrise brings a new opportunity to rewrite your story.",
        "The strength you seek already lies within you, waiting to be awakened.",
        "Your journey may be challenging, but your destination will be worth it.",
        "Transform your obstacles into stepping stones towards greatness.",
        "Today's efforts are tomorrow's achievements in disguise.",
        "Embrace the challenge, for it shapes the champion within you.",
        "Your potential is limitless when you believe in your power to grow.",
        "Each small step forward is a victory worth celebrating.",
        "The courage to begin is half the journey to success.",
        "Let your dreams be bigger than your fears and your actions louder than your words."
    )

    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        try {
            // For now, we'll simulate AI generation by randomly selecting from a larger pool
            // In a real implementation, this would check for downloaded models and generate dynamically
            val useAiGenerated = Random.nextBoolean() && Random.nextFloat() > 0.3f // 70% chance if random is true
            
            return@withContext if (useAiGenerated) {
                // Simulate AI-generated content
                aiGeneratedSentences.random()
            } else {
                // Use fallback sentences
                fallbackSentences.random()
            }
        } catch (e: Exception) {
            // Log error but don't crash, fall back to hardcoded sentences
            e.printStackTrace()
            return@withContext fallbackSentences.random()
        }
    }
}