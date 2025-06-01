package com.jahi.pipelinetest.viewmodel

import androidx.lifecycle.ViewModel
import com.jahi.pipelinetest.domain.GenerateAudioUseCase

class WidgetViewModel(
    private val generateAudioUseCase: GenerateAudioUseCase
) : ViewModel() {

    fun playMotivationalAudio() {
        generateAudioUseCase()
    }
}
