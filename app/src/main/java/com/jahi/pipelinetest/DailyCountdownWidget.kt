package com.jahi.pipelinetest

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.jahi.pipelinetest.domain.GenerateAudioUseCase
import com.jahi.pipelinetest.domain.GenerateMotivationalTextUseCase
import com.jahi.pipelinetest.viewmodel.WidgetViewModel
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_preferences")

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
            // Get the app container to access AI models
            val application = context.applicationContext as? com.jahi.pipelinetest.gallery.GalleryApplication
            val appContainer = application?.container
            
            // Create the use cases
            val generateMotivationalTextUseCase = if (appContainer != null) {
                GenerateMotivationalTextUseCase(context, appContainer)
            } else {
                // Create a stub AppContainer for fallback
                // We'll use a minimal container that won't have any downloaded models
                val dataStore = context.dataStore
                val stubContainer = com.jahi.pipelinetest.gallery.data.DefaultAppContainer(context, dataStore)
                GenerateMotivationalTextUseCase(context, stubContainer)
            }
            
            val generateAudioUseCase = GenerateAudioUseCase(context, generateMotivationalTextUseCase)
            val vm = WidgetViewModel(generateAudioUseCase)
            vm.playMotivationalAudio()
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

    }
}
