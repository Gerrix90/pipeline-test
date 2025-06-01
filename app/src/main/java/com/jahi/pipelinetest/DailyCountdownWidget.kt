package com.jahi.pipelinetest

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.RemoteViews
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.random.Random
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class DailyCountdownWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        scheduleUpdates(context)
    }

    override fun onDisabled(context: Context) {
        cancelUpdates(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED ||
            intent.action == Intent.ACTION_DATE_CHANGED
        ) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, DailyCountdownWidget::class.java)
            )
            for (id in ids) {
                updateAppWidget(context, manager, id)
            }
            scheduleUpdates(context)
        } else if (intent.action == ACTION_GENERATE_AUDIO) {
            generateAndPlay(context)
        }
    }

    private fun scheduleUpdates(context: Context) {
        val intent = Intent(context, DailyCountdownWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, DailyCountdownWidget::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis(),
            60_000,
            pendingIntent
        )
    }

    private fun cancelUpdates(context: Context) {
        val intent = Intent(context, DailyCountdownWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, DailyCountdownWidget::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent)
    }

    companion object {
        const val ACTION_GENERATE_AUDIO = "com.jahi.pipelinetest.GENERATE_AUDIO"

        private val motivationalSentences = listOf(
            "You can achieve anything you set your mind to.",
            "Believe in yourself and all that you are.",
            "Every day is a chance to get better."
        )

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.daily_countdown_widget)
            val remaining = durationToEndOfDay()
            views.setTextViewText(R.id.countdown_text, formatDuration(remaining))

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.countdown_container, pendingIntent)

            val genIntent = Intent(context, DailyCountdownWidget::class.java).apply {
                action = ACTION_GENERATE_AUDIO
            }
            val genPending = PendingIntent.getBroadcast(
                context,
                1,
                genIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.generate_button, genPending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun durationToEndOfDay(): Duration {
            val now = Instant.now()
            val z = ZoneId.systemDefault()
            val endOfDay = LocalDate.now(z).plusDays(1).atStartOfDay(z).toInstant()
            return Duration.between(now, endOfDay)
        }

        private fun formatDuration(d: Duration): String {
            var seconds = d.seconds
            val days = seconds / 86_400
            seconds %= 86_400
            val hours = seconds / 3_600
            seconds %= 3_600
            val minutes = seconds / 60
            return if (days > 0) {
                String.format("%d days %02d:%02d", days, hours, minutes)
            } else {
                String.format("%02d:%02d", hours, minutes)
            }
        }

        private fun generateAndPlay(context: Context) {
            thread {
                val text = motivationalSentences[Random.nextInt(motivationalSentences.size)]
                val file = fetchAudio(context, text)
                file?.let { playAudio(it) }
            }
        }

        private fun fetchAudio(context: Context, text: String): File? {
            return try {
                val url = URL("https://api.elevenlabs.io/v1/text-to-speech/EXAVITQu4vr4xnSDxMaL/stream")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("xi-api-key", BuildConfig.ELEVEN_LAB_KEY)
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                val body = "{\"text\": \"$text\"}"
                conn.outputStream.use { it.write(body.toByteArray()) }
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val file = File.createTempFile("tts", ".mp3", context.cacheDir)
                    conn.inputStream.use { input -> file.outputStream().use { input.copyTo(it) } }
                    file
                } else null
            } catch (e: Exception) {
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
}
