package com.jahi.pipelinetest.model

data class CustomEvent(
    val id: Int = kotlin.random.Random.nextInt(),
    var name: String = "",
    var date: String = "",
    var showTime: Boolean = false,
    var showInWidget: Boolean = false
)
