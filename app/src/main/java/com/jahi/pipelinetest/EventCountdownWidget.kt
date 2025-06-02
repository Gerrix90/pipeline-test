package com.jahi.pipelinetest

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.widget.RemoteViews
import com.jahi.pipelinetest.domain.GenerateAudioUseCase
import com.jahi.pipelinetest.viewmodel.WidgetViewModel
import com.jahi.pipelinetest.model.CustomEvent
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.jahi.pipelinetest.Prefs

class EventCountdownWidget : AppWidgetProvider() {

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
        super.onEnabled(context)
        scheduleUpdates(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelUpdates(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_DATE_CHANGED,
            ACTION_UPDATE_EVENT_WIDGET -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context, EventCountdownWidget::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
            ACTION_GENERATE_AUDIO -> {
                val vm = WidgetViewModel(GenerateAudioUseCase(context))
                vm.playMotivationalAudio()
            }
        }
    }

    private fun scheduleUpdates(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventCountdownWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, EventCountdownWidget::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            REQUEST_CODE_UPDATE, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.setRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis() + UPDATE_INTERVAL_MILLIS,
            UPDATE_INTERVAL_MILLIS.toLong(),
            pendingIntent
        )
    }

    private fun cancelUpdates(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventCountdownWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, EventCountdownWidget::class.java))
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            REQUEST_CODE_UPDATE, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        private const val REQUEST_CODE_UPDATE = 300
        private const val UPDATE_INTERVAL_MILLIS = 60000 // Update every minute
        const val ACTION_UPDATE_EVENT_WIDGET = "com.jahi.pipelinetest.UPDATE_EVENT_WIDGET"
        const val ACTION_GENERATE_AUDIO = "com.jahi.pipelinetest.GENERATE_AUDIO"

        private fun parseEventDateTime(dateString: String): LocalDateTime {
            return com.jahi.pipelinetest.parseEventDateTime(dateString)
                ?: throw IllegalArgumentException("Invalid date format")
        }

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.event_countdown_widget)
            
            val prefs = Prefs(context)
            val events = prefs.customEvents.filter { it.showInWidget }
            val now = LocalDateTime.now()
            
            try {
                if (events.isEmpty()) {
                    // Show placeholder when no events are selected
                    views.setTextViewText(R.id.event_name, "No events")
                    views.setTextViewText(R.id.time_text, "--:--")
                    views.setImageViewResource(R.id.circular_progress_view, R.drawable.widget_background)
                } else {
                    // Filter out past events and find next upcoming event
                    val futureEvents = events.filter { event ->
                        try {
                            val eventDateTime = parseEventDateTime(event.date)
                            eventDateTime.isAfter(now)
                        } catch (e: Exception) {
                            false
                        }
                    }
                    
                    val nextEvent = futureEvents.minByOrNull { event ->
                        val eventDateTime = parseEventDateTime(event.date)
                        ChronoUnit.MILLIS.between(now, eventDateTime)
                    }
                    
                    if (nextEvent == null) {
                        views.setTextViewText(R.id.event_name, "No upcoming events")
                        views.setTextViewText(R.id.time_text, "--:--")
                        views.setImageViewResource(R.id.circular_progress_view, R.drawable.widget_background)
                    } else {
                        // Calculate remaining time to event
                        val eventTime = parseEventDateTime(nextEvent.date)
                        val remainingDuration = Duration.between(now, eventTime)
                        val remainingSeconds = remainingDuration.seconds
                        
                        // Format remaining time
                        val timeText = when {
                            remainingSeconds < 0 -> "Expired"
                            remainingSeconds < 3600 -> {
                                val minutes = remainingSeconds / 60
                                String.format(java.util.Locale.US, "%d min", minutes)
                            }
                            remainingSeconds < 86400 -> {
                                val hours = remainingSeconds / 3600
                                val minutes = (remainingSeconds % 3600) / 60
                                String.format(java.util.Locale.US, "%02d:%02d", hours, minutes)
                            }
                            else -> {
                                val days = remainingSeconds / 86400
                                val hours = (remainingSeconds % 86400) / 3600
                                String.format(java.util.Locale.US, "%dd %dh", days, hours)
                            }
                        }
                        
                        // Determine color based on time remaining
                        val color = when {
                            remainingSeconds > 24 * 3600 -> 0xFF4ADE80.toInt() // Green
                            remainingSeconds > 12 * 3600 -> 0xFFFBBF24.toInt() // Yellow
                            remainingSeconds > 6 * 3600 -> 0xFFFB923C.toInt() // Orange
                            else -> 0xFFEF4444.toInt() // Red
                        }
                        
                        // Create circular progress bitmap
                        val totalEventSeconds = Duration.between(now, eventTime.plusDays(1)).seconds
                        val progress = (remainingSeconds.toFloat() / totalEventSeconds) * 360f
                        val bitmap = createCircularProgressBitmap(progress, 200, 200, color)
                        
                        // Update views
                        views.setTextViewText(R.id.event_name, nextEvent.name)
                        views.setImageViewBitmap(R.id.circular_progress_view, bitmap)
                        views.setTextViewText(R.id.time_text, timeText)
                    }
                }
            } catch (e: Exception) {
                views.setTextViewText(R.id.event_name, "Error")
                views.setTextViewText(R.id.time_text, "--:--")
            }
            
            // Set click to open app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.event_countdown_widget_container, pendingIntent)

            val genIntent = Intent(context, EventCountdownWidget::class.java).apply {
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
        
        private fun createCircularProgressBitmap(
            progress: Float,
            width: Int,
            height: Int,
            progressColor: Int
        ): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            val padding = 20f
            val strokeWidth = 15f
            
            // Circle rect for remaining time (colored)
            val rect = RectF(padding, padding, width - padding, height - padding)
            
            // Draw full background circle in a subtle color
            val backgroundPaint = Paint().apply {
                color = 0x20FFFFFF // Very subtle white
                style = Paint.Style.STROKE
                this.strokeWidth = strokeWidth
                isAntiAlias = true
            }
            canvas.drawArc(rect, 0f, 360f, false, backgroundPaint)
            
            // Draw elapsed time arc with light blue
            val progressPaint = Paint().apply {
                color = 0xFF60A5FA.toInt() // Light blue for elapsed time
                style = Paint.Style.STROKE
                this.strokeWidth = strokeWidth
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
            }
            if (progress > 0) {
                canvas.drawArc(rect, -90f, progress, false, progressPaint)
            }
            
            // Draw remaining time arc with dynamic color on top
            val remainingPaint = Paint().apply {
                color = progressColor
                style = Paint.Style.STROKE
                this.strokeWidth = strokeWidth
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
            }
            val remainingAngle = 360f - progress
            if (remainingAngle > 0) {
                canvas.drawArc(rect, -90f + progress, remainingAngle, false, remainingPaint)
            }

            return bitmap
        }

    }
}
