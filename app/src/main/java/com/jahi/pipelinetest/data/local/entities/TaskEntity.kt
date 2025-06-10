package com.jahi.pipelinetest.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CustomEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"])]
)
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    val eventId: Int,
    val description: String,
    val isCompleted: Boolean,
    val createdAt: String,
    val dueDate: String?
)