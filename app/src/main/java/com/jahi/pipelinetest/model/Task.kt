package com.jahi.pipelinetest.model

data class Task(
    val id: Int,
    val eventId: Int,
    var description: String = "",
    var isCompleted: Boolean = false,
    val createdAt: String = java.time.LocalDateTime.now().toString(),
    var dueDate: String? = null
)