package com.jahi.pipelinetest.domain

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
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
        thread {
            val apiKey = prefs.elevenLabsApiKey
            if (apiKey.isBlank()) {
                return@thread
            }
            
            // Generate text using AI or fallback
            val text = runBlocking {
                generateMotivationalTextUseCase()
            }
            
            Log.d(TAG, "Generated text for TTS: '$text'")
            val file = fetchAudio(text, apiKey)
            file?.let { playAudio(it) }
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
            }
            mp.prepare()
            mp.start()
        } catch (_: Exception) {
        }
    }
}
