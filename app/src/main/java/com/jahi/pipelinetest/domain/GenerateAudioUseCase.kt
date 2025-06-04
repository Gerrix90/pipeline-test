package com.jahi.pipelinetest.domain

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.RemoteViews
import timber.log.Timber
import com.jahi.pipelinetest.Prefs
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlinx.coroutines.runBlocking

class GenerateAudioUseCase(
    private val context: Context,
    private val generateMotivationalTextUseCase: GenerateMotivationalTextUseCase
) {
    companion object {
        private const val TAG = "GenerateAudioUseCase"
    }

    private val prefs = Prefs(context)

    operator fun invoke() {
        Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").d("invoke() - Starting audio generation process")
        
        thread {
            val apiKey = prefs.elevenLabsApiKey
            if (apiKey.isBlank()) {
                Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").w("invoke() - ElevenLabs API key is blank, cannot generate audio")
                return@thread
            }
            
            Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").d("invoke() - API key available, proceeding with generation")
            
            // Show progress indicator
            updateWidgetState(isGenerating = true)
            
            try {
                // Generate text using AI or fallback
                Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").d("invoke() - Calling GenerateMotivationalTextUseCase")
                val text = runBlocking {
                    generateMotivationalTextUseCase()
                }
                
                Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").d("invoke() - Generated text for TTS: '$text'")
                val file = fetchAudio(text, apiKey)
                if (file != null) {
                    Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").d("invoke() - Audio file generated successfully, playing audio")
                    playAudio(file)
                } else {
                    Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").w("invoke() - Audio generation failed, restoring widget state")
                    // No audio to play, restore button immediately
                    updateWidgetState(isGenerating = false)
                }
            } catch (e: Exception) {
                Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").e(e, "invoke() - Error in audio generation")
                // Restore button state on error
                updateWidgetState(isGenerating = false)
            }
        }
    }
    
    private fun updateWidgetState(isGenerating: Boolean) {
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            
            // Update Daily Countdown Widget
            updateDailyCountdownWidgets(appWidgetManager, isGenerating)
            
            // Update Circular Progress Widget  
            updateCircularProgressWidgets(appWidgetManager, isGenerating)
            
            // Update Event Countdown Widget
            updateEventCountdownWidgets(appWidgetManager, isGenerating)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update widget state", e)
        }
    }
    
    private fun updateDailyCountdownWidgets(appWidgetManager: AppWidgetManager, isGenerating: Boolean) {
        val componentName = ComponentName(context, com.jahi.pipelinetest.DailyCountdownWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, com.jahi.pipelinetest.R.layout.daily_countdown_widget)
            
            if (isGenerating) {
                // Hide button, show progress
                views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.GONE)
                views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.VISIBLE)
            } else {
                // Show button, hide progress
                views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.VISIBLE)
                views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.GONE)
            }
            
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }
    
    private fun updateCircularProgressWidgets(appWidgetManager: AppWidgetManager, isGenerating: Boolean) {
        val componentName = ComponentName(context, com.jahi.pipelinetest.CircularProgressWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, com.jahi.pipelinetest.R.layout.circular_progress_widget)
            
            if (isGenerating) {
                views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.GONE)
                views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.VISIBLE)
            } else {
                views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.VISIBLE)
                views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.GONE)
            }
            
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }
    
    private fun updateEventCountdownWidgets(appWidgetManager: AppWidgetManager, isGenerating: Boolean) {
        val componentName = ComponentName(context, com.jahi.pipelinetest.EventCountdownWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, com.jahi.pipelinetest.R.layout.event_countdown_widget)
            
            if (isGenerating) {
                views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.GONE)
                views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.VISIBLE)
            } else {
                views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.VISIBLE)
                views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.GONE)
            }
            
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }

    private fun fetchAudio(text: String, apiKey: String): File? {
        return try {
            val url = URL("https://api.elevenlabs.io/v1/text-to-speech/EXAVITQu4vr4xnSDxMaL/stream")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("xi-api-key", apiKey)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            // Escape quotes and newlines for JSON
            val escapedText = text.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
            val body = "{\"text\": \"$escapedText\"}"
            Log.d(TAG, "Sending TTS request: $body")
            conn.outputStream.use { it.write(body.toByteArray()) }
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val file = File.createTempFile("tts", ".mp3", context.cacheDir)
                conn.inputStream.use { input ->
                    file.outputStream().use { input.copyTo(it) }
                }
                Log.d(TAG, "TTS audio generated successfully")
                file
            } else {
                Log.e(TAG, "TTS request failed with response code: ${conn.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "TTS request failed with exception: ${e.message}")
            null
        }
    }

    private fun playAudio(file: File) {
        try {
            val mp = MediaPlayer()
            mp.setDataSource(file.absolutePath)
            mp.setOnCompletionListener {
                it.release()
                file.delete()
                // Restore button state when audio finishes
                updateWidgetState(isGenerating = false)
            }
            mp.prepare()
            mp.start()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play audio", e)
            // Restore button state on error
            updateWidgetState(isGenerating = false)
        }
    }
}
