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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.jahi.pipelinetest.domain.GenerateAudioUseCase
import com.jahi.pipelinetest.domain.GenerateMotivationalTextUseCase
import com.jahi.pipelinetest.viewmodel.WidgetViewModel
import java.time.Duration
import java.time.LocalDateTime

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_preferences")

class CircularProgressWidget : AppWidgetProvider() {

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
            Intent.ACTION_DATE_CHANGED -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context, CircularProgressWidget::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
            ACTION_GENERATE_AUDIO -> {
                // Get the app container to access AI models
                val application = context.applicationContext as? com.jahi.pipelinetest.gallery.GalleryApplication
                val appContainer = application?.container
                
                // Create the use cases
                val generateMotivationalTextUseCase = if (appContainer != null) {
                    GenerateMotivationalTextUseCase(context, appContainer)
                } else {
                    // Create a stub AppContainer for fallback
                    val dataStore = context.dataStore
                    val stubContainer = com.jahi.pipelinetest.gallery.data.DefaultAppContainer(context, dataStore)
                    GenerateMotivationalTextUseCase(context, stubContainer)
                }
                
                val generateAudioUseCase = GenerateAudioUseCase(context, generateMotivationalTextUseCase)
                val vm = WidgetViewModel(generateAudioUseCase)
                vm.playMotivationalAudio()
            }
        }
    }

    private fun scheduleUpdates(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CircularProgressWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, CircularProgressWidget::class.java))
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
        val intent = Intent(context, CircularProgressWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, CircularProgressWidget::class.java))
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
        private const val REQUEST_CODE_UPDATE = 200
        private const val UPDATE_INTERVAL_MILLIS = 60000 // Update every minute
        const val ACTION_GENERATE_AUDIO = "com.jahi.pipelinetest.GENERATE_AUDIO"

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.circular_progress_widget)
            
            // Calculate remaining time until end of day
            val now = LocalDateTime.now()
            val endOfDay = now.toLocalDate().plusDays(1).atStartOfDay()
            val remainingDuration = Duration.between(now, endOfDay)
            val totalDaySeconds = 24 * 60 * 60
            val remainingSeconds = remainingDuration.seconds
            val elapsedSeconds = totalDaySeconds - remainingSeconds
            val progress = (elapsedSeconds.toFloat() / totalDaySeconds) * 360f
            
            // Format remaining time (without seconds)
            val hours = remainingSeconds / 3600
            val minutes = (remainingSeconds % 3600) / 60
            val timeText = String.format("%02d:%02d", hours, minutes)
            
            // Determine color based on remaining time
            val color = when {
                remainingSeconds > 12 * 3600 -> 0xFF4ADE80.toInt() // Green - more than 12 hours
                remainingSeconds > 6 * 3600 -> 0xFFFBBF24.toInt() // Yellow - 6-12 hours
                remainingSeconds > 3 * 3600 -> 0xFFFB923C.toInt() // Orange - 3-6 hours
                else -> 0xFFEF4444.toInt() // Red - less than 3 hours
            }
            
            // Create circular progress bitmap with dynamic color
            val bitmap = createCircularProgressBitmap(progress, 200, 200, color)
            
            // Update views
            views.setImageViewBitmap(R.id.circular_progress_view, bitmap)
            views.setTextViewText(R.id.time_text, timeText)
            
            // Set click to open app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.circular_progress_widget_container, pendingIntent)

            val genIntent = Intent(context, CircularProgressWidget::class.java).apply {
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