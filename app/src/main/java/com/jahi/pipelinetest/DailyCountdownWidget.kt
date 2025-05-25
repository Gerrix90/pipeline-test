package com.jahi.pipelinetest

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.ComponentName
import android.widget.RemoteViews
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
            seconds %= 60
            return if (days > 0) {
                String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds)
            } else {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }
        }
    }
}
