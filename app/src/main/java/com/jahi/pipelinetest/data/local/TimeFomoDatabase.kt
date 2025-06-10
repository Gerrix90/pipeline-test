package com.jahi.pipelinetest.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.jahi.pipelinetest.data.local.dao.CustomEventDao
import com.jahi.pipelinetest.data.local.dao.TaskDao
import com.jahi.pipelinetest.data.local.entities.CustomEventEntity
import com.jahi.pipelinetest.data.local.entities.TaskEntity

@Database(
    entities = [CustomEventEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TimeFomoDatabase : RoomDatabase() {
    
    abstract fun customEventDao(): CustomEventDao
    abstract fun taskDao(): TaskDao
    
    companion object {
        const val DATABASE_NAME = "time_fomo_database"
        
        @Volatile
        private var INSTANCE: TimeFomoDatabase? = null
        
        fun getDatabase(context: Context): TimeFomoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TimeFomoDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}