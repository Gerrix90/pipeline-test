package com.jahi.pipelinetest.domain

import android.content.Context
import android.media.MediaPlayer
import com.jahi.pipelinetest.Prefs
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.random.Random

class GenerateAudioUseCase(private val context: Context) {

    private val prefs = Prefs(context)
    
    private val motivationalSentences = listOf(
        "You can achieve anything you set your mind to.",
        "Believe in yourself and all that you are.",
        "Every day is a chance to get better."
    )

    operator fun invoke() {
        thread {
            val apiKey = prefs.elevenLabsApiKey
            if (apiKey.isBlank()) {
                return@thread
            }
            val text = motivationalSentences.random()
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
            val body = "{\"text\": \"$text\"}"
            conn.outputStream.use { it.write(body.toByteArray()) }
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val file = File.createTempFile("tts", ".mp3", context.cacheDir)
                conn.inputStream.use { input ->
                    file.outputStream().use { input.copyTo(it) }
                }
                file
            } else null
        } catch (_: Exception) {
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
