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
    
    private enum class WidgetState {
        NORMAL,     // Show generate button
        GENERATING, // Show progress bar
        PLAYING     // Show speaker icon
    }

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
            updateWidgetState(WidgetState.GENERATING)
            
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
                    // Switch to playing state before starting playback
                    updateWidgetState(WidgetState.PLAYING)
                    playAudio(file)
                } else {
                    Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").w("invoke() - Audio generation failed, restoring widget state")
                    // No audio to play, restore button immediately
                    updateWidgetState(WidgetState.NORMAL)
                }
            } catch (e: Exception) {
                Timber.tag("DEBUG_FLOW|GenerateAudioUseCase").e(e, "invoke() - Error in audio generation")
                // Restore button state on error
                updateWidgetState(WidgetState.NORMAL)
            }
        }
    }
    
    private fun updateWidgetState(state: WidgetState) {
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            
            // Update Daily Countdown Widget
            updateDailyCountdownWidgets(appWidgetManager, state)
            
            // Update Circular Progress Widget  
            updateCircularProgressWidgets(appWidgetManager, state)
            
            // Update Event Countdown Widget
            updateEventCountdownWidgets(appWidgetManager, state)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update widget state", e)
        }
    }
    
    private fun updateDailyCountdownWidgets(appWidgetManager: AppWidgetManager, state: WidgetState) {
        val componentName = ComponentName(context, com.jahi.pipelinetest.DailyCountdownWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, com.jahi.pipelinetest.R.layout.daily_countdown_widget)
            
            when (state) {
                WidgetState.NORMAL -> {
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.VISIBLE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.speaker_icon, android.view.View.GONE)
                }
                WidgetState.GENERATING -> {
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.VISIBLE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.speaker_icon, android.view.View.GONE)
                }
                WidgetState.PLAYING -> {
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.speaker_icon, android.view.View.VISIBLE)
                }
            }
            
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }
    
    private fun updateCircularProgressWidgets(appWidgetManager: AppWidgetManager, state: WidgetState) {
        val componentName = ComponentName(context, com.jahi.pipelinetest.CircularProgressWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, com.jahi.pipelinetest.R.layout.circular_progress_widget)
            
            when (state) {
                WidgetState.NORMAL -> {
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.VISIBLE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.speaker_icon, android.view.View.GONE)
                }
                WidgetState.GENERATING -> {
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.VISIBLE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.speaker_icon, android.view.View.GONE)
                }
                WidgetState.PLAYING -> {
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.speaker_icon, android.view.View.VISIBLE)
                }
            }
            
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
        }
    }
    
    private fun updateEventCountdownWidgets(appWidgetManager: AppWidgetManager, state: WidgetState) {
        val componentName = ComponentName(context, com.jahi.pipelinetest.EventCountdownWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, com.jahi.pipelinetest.R.layout.event_countdown_widget)
            
            when (state) {
                WidgetState.NORMAL -> {
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.VISIBLE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.speaker_icon, android.view.View.GONE)
                }
                WidgetState.GENERATING -> {
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.VISIBLE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.speaker_icon, android.view.View.GONE)
                }
                WidgetState.PLAYING -> {
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.generate_button, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.progress_indicator, android.view.View.GONE)
                    views.setViewVisibility(com.jahi.pipelinetest.R.id.speaker_icon, android.view.View.VISIBLE)
                }
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
                updateWidgetState(WidgetState.NORMAL)
            }
            mp.prepare()
            mp.start()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play audio", e)
            // Restore button state on error
            updateWidgetState(WidgetState.NORMAL)
        }
    }
}
