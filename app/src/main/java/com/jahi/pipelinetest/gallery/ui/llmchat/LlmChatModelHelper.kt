/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jahi.pipelinetest.gallery.ui.llmchat

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.jahi.pipelinetest.gallery.data.Accelerator
import com.jahi.pipelinetest.gallery.data.ConfigKey
import com.jahi.pipelinetest.gallery.data.Model
import com.jahi.pipelinetest.gallery.ui.common.cleanUpMediapipeTaskErrorMessage
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.genai.llminference.GraphOptions
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import timber.log.Timber

private const val TAG = "DEBUG_FLOW"
private const val CLASS_PREFIX = "LlmChatModelHelper"

typealias ResultListener = (partialResult: String, done: Boolean) -> Unit
typealias CleanUpListener = () -> Unit

data class LlmModelInstance(val engine: LlmInference, var session: LlmInferenceSession)

object LlmChatModelHelper {
  // Indexed by model name.
  private val cleanUpListeners: MutableMap<String, CleanUpListener> = mutableMapOf()

  fun initialize(
    context: Context, model: Model, onDone: (String) -> Unit
  ) {
    // Prepare options.
    val maxTokens =
      model.getIntConfigValue(key = ConfigKey.MAX_TOKENS, defaultValue = DEFAULT_MAX_TOKEN)
    val topK = model.getIntConfigValue(key = ConfigKey.TOPK, defaultValue = DEFAULT_TOPK)
    val topP = model.getFloatConfigValue(key = ConfigKey.TOPP, defaultValue = DEFAULT_TOPP)
    val temperature =
      model.getFloatConfigValue(key = ConfigKey.TEMPERATURE, defaultValue = DEFAULT_TEMPERATURE)
    val accelerator =
      model.getStringConfigValue(key = ConfigKey.ACCELERATOR, defaultValue = Accelerator.GPU.label)
    Timber.tag("$TAG:$CLASS_PREFIX").d("initialize() - Starting model initialization for: ${model.name}")
    Timber.tag("$TAG:$CLASS_PREFIX").d("initialize() - Model path: ${model.getPath(context)}")
    Timber.tag("$TAG:$CLASS_PREFIX").d("initialize() - Model config: maxTokens=$maxTokens, topK=$topK, topP=$topP, temperature=$temperature, accelerator=$accelerator")
    val preferredBackend = when (accelerator) {
      Accelerator.CPU.label -> LlmInference.Backend.CPU
      Accelerator.GPU.label -> LlmInference.Backend.GPU
      else -> LlmInference.Backend.GPU
    }
    val options =
      LlmInference.LlmInferenceOptions.builder().setModelPath(model.getPath(context = context))
        .setMaxTokens(maxTokens).setPreferredBackend(preferredBackend)
        .setMaxNumImages(if (model.llmSupportImage) 1 else 0)
        .build()

    // Create an instance of the LLM Inference task and session.
    try {
      val llmInference = LlmInference.createFromOptions(context, options)

      val session = LlmInferenceSession.createFromOptions(
        llmInference,
        LlmInferenceSession.LlmInferenceSessionOptions.builder().setTopK(topK).setTopP(topP)
          .setTemperature(temperature)
          .setGraphOptions(
            GraphOptions.builder().setEnableVisionModality(model.llmSupportImage).build()
          ).build()
      )
      model.instance = LlmModelInstance(engine = llmInference, session = session)
      Timber.tag("$TAG:$CLASS_PREFIX").d("initialize() - Model initialized successfully: ${model.name}")
    } catch (e: Exception) {
      Timber.tag("$TAG:$CLASS_PREFIX").e(e, "initialize() - Failed to initialize model: ${model.name}")
      onDone(cleanUpMediapipeTaskErrorMessage(e.message ?: "Unknown error"))
      return
    }
    onDone("")
  }

  fun resetSession(model: Model) {
    try {
      Timber.tag("$TAG:$CLASS_PREFIX").d("resetSession() - Resetting session for model: ${model.name}")

      val instance = model.instance as LlmModelInstance? ?: return
      val session = instance.session
      session.close()

      val inference = instance.engine
      val topK = model.getIntConfigValue(key = ConfigKey.TOPK, defaultValue = DEFAULT_TOPK)
      val topP = model.getFloatConfigValue(key = ConfigKey.TOPP, defaultValue = DEFAULT_TOPP)
      val temperature =
        model.getFloatConfigValue(key = ConfigKey.TEMPERATURE, defaultValue = DEFAULT_TEMPERATURE)
      val newSession = LlmInferenceSession.createFromOptions(
        inference,
        LlmInferenceSession.LlmInferenceSessionOptions.builder().setTopK(topK).setTopP(topP)
          .setTemperature(temperature)
          .setGraphOptions(
            GraphOptions.builder().setEnableVisionModality(model.llmSupportImage).build()
          ).build()
      )
      instance.session = newSession
      Log.d(TAG, "Resetting done")
    } catch (e: Exception) {
      Timber.tag("$TAG:$CLASS_PREFIX").e(e, "resetSession() - Failed to reset session")
    }
  }

  fun cleanUp(model: Model) {
    if (model.instance == null) {
      return
    }

    val instance = model.instance as LlmModelInstance
    try {
      // This will also close the session. Do not call session.close manually.
      instance.engine.close()
    } catch (e: Exception) {
      // ignore
    }
    val onCleanUp = cleanUpListeners.remove(model.name)
    if (onCleanUp != null) {
      onCleanUp()
    }
    model.instance = null
    Timber.tag("$TAG:$CLASS_PREFIX").d("cleanUp() - Clean up done for model: ${model.name}")
  }

  fun runInference(
    model: Model,
    input: String,
    resultListener: ResultListener,
    cleanUpListener: CleanUpListener,
    image: Bitmap? = null,
  ) {
    Timber.tag("$TAG:$CLASS_PREFIX").d("runInference() - Starting inference for model: ${model.name}")
    Timber.tag("$TAG:$CLASS_PREFIX").d("runInference() - Input: '$input'")
    Timber.tag("$TAG:$CLASS_PREFIX").d("runInference() - Has image: ${image != null}")
    
    if (model.instance == null) {
      Timber.tag("$TAG:$CLASS_PREFIX").e("runInference() - Model instance is null for: ${model.name}")
      resultListener("", true)
      return
    }
    
    val instance = model.instance as LlmModelInstance

    // Set listener.
    if (!cleanUpListeners.containsKey(model.name)) {
      cleanUpListeners[model.name] = cleanUpListener
    }

    // Start async inference.
    //
    // For a model that supports image modality, we need to add the text query chunk before adding
    // image.
    val session = instance.session
    session.addQueryChunk(input)
    if (image != null) {
      session.addImage(BitmapImageBuilder(image).build())
    }
    Timber.tag("$TAG:$CLASS_PREFIX").d("runInference() - Calling generateResponseAsync")
    session.generateResponseAsync { partialResult, done ->
      Timber.tag("$TAG:$CLASS_PREFIX").d("runInference() - Received response: partial='$partialResult', done=$done")
      resultListener(partialResult, done)
    }
  }
}