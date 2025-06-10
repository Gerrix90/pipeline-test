package com.jahi.pipelinetest.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_events")
data class CustomEventEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val date: String,
    val showTime: Boolean,
    val showInWidget: Boolean
)