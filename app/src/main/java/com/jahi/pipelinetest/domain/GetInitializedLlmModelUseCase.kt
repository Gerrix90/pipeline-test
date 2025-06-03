package com.jahi.pipelinetest.domain

import android.content.Context
import com.jahi.pipelinetest.gallery.data.Model
import com.jahi.pipelinetest.repository.LlmModelRepository

class GetInitializedLlmModelUseCase(
    private val context: Context,
    private val repository: LlmModelRepository
) {
    suspend operator fun invoke(): Model? = repository.getInitializedModel(context)
}
