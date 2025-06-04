package com.jahi.pipelinetest.repository

import android.content.Context
import android.util.Log
import com.jahi.pipelinetest.gallery.data.Model
import timber.log.Timber
import com.jahi.pipelinetest.gallery.ui.llmchat.LlmChatModelHelper
import com.jahi.pipelinetest.gallery.ui.llmchat.createLlmChatConfigs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import kotlin.coroutines.resume

class LlmModelRepository {
    companion object {
        private const val TAG = "LlmModelRepository"
        private const val MIN_MODEL_SIZE_BYTES = 10_000_000L
        private const val INITIALIZATION_TIMEOUT_MS = 120_000L
    }

    private var cachedModel: Model? = null

    suspend fun getInitializedModel(context: Context): Model? = withContext(Dispatchers.IO) {
        Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Starting model search")
        
        cachedModel?.let { model ->
            if (model.instance != null) {
                Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Using cached model: ${model.name}")
                return@withContext model
            } else {
                Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Cached model has no instance: ${model.name}")
            }
        }

        Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Searching for downloaded models")
        val model = findDownloadedLlmModel(context)
        if (model == null) {
            Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - No model found")
            return@withContext null
        }
        
        Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Found model: ${model.name}, path: ${model.downloadFileName}")

        if (model.instance == null) {
            Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Initializing model: ${model.name}")
            val success = withTimeoutOrNull(INITIALIZATION_TIMEOUT_MS) {
                suspendCancellableCoroutine<Boolean> { cont ->
                    LlmChatModelHelper.initialize(
                        context = context,
                        model = model,
                        onDone = { error ->
                            if (error.isEmpty()) {
                                Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Model initialized successfully: ${model.name}")
                            } else {
                                Timber.tag("DEBUG_FLOW|LlmModelRepository").e("getInitializedModel() - Model initialization failed: $error")
                            }
                            cont.resume(error.isEmpty())
                        }
                    )
                }
            } == true

            if (!success) {
                Timber.tag("DEBUG_FLOW|LlmModelRepository").e("getInitializedModel() - Model initialization failed or timed out")
                return@withContext null
            }
        } else {
            Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Model already has instance: ${model.name}")
        }

        cachedModel = model
        Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Model instance status: ${model.instance != null}")
        Timber.tag("DEBUG_FLOW|LlmModelRepository").d("getInitializedModel() - Returning initialized model: ${model.name}")
        return@withContext model
    }

    private fun findDownloadedLlmModel(context: Context): Model? {
        val externalDir = context.getExternalFilesDir(null)
        if (externalDir == null) {
            Timber.tag("DEBUG_FLOW|LlmModelRepository").w("findDownloadedLlmModel() - External files directory is null")
            return null
        }
        Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - External files dir: ${externalDir.absolutePath}")

        val importsDir = File(externalDir, "__imports")
        Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Checking __imports directory: ${importsDir.absolutePath}, exists: ${importsDir.exists()}")
        
        if (importsDir.exists()) {
            val files = importsDir.listFiles()
            Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Found ${files?.size ?: 0} files in __imports")
            
            files?.forEach { file ->
                Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Checking file: ${file.name}, size: ${file.length() / 1024 / 1024}MB")
                
                if ((file.name.endsWith(".task") || file.name.endsWith(".bin")) &&
                    file.length() > MIN_MODEL_SIZE_BYTES
                ) {
                    Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Found valid model file: ${file.name}")
                    
                    val model = Model(
                        name = file.nameWithoutExtension,
                        downloadFileName = "__imports/${file.name}",
                        sizeInBytes = file.length(),
                        version = "imported",
                        url = "",
                        imported = true,
                        configs = createLlmChatConfigs() // Add proper LLM configs
                    )
                    model.preProcess()
                    
                    Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Created imported model: ${model.name}, path: ${model.getPath(context)}")
                    return model
                }
            }
        }

        val modelDirs = externalDir.listFiles { f -> f.isDirectory && !f.name.startsWith("__") }
        Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Found ${modelDirs?.size ?: 0} model directories")
        
        modelDirs?.forEach { modelDir ->
            Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Checking model directory: ${modelDir.name}")
            
            val versionDirs = modelDir.listFiles { f -> f.isDirectory }
            versionDirs?.forEach { versionDir ->
                Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Checking version directory: ${modelDir.name}/${versionDir.name}")
                
                val modelFiles = versionDir.listFiles { f ->
                    f.name.endsWith(".task") || f.name.endsWith(".bin")
                }
                
                modelFiles?.forEach { modelFile ->
                    Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Found model file: ${modelFile.name} in ${modelDir.name}/${versionDir.name}, size: ${modelFile.length() / 1024 / 1024}MB")
                    
                    if (modelFile.length() > MIN_MODEL_SIZE_BYTES) {
                        val model = Model(
                            name = modelDir.name,
                            downloadFileName = modelFile.name,
                            sizeInBytes = modelFile.length(),
                            version = versionDir.name,
                            url = "",
                            imported = false,
                            configs = createLlmChatConfigs() // Add proper LLM configs
                        )
                        model.preProcess()
                        
                        Timber.tag("DEBUG_FLOW|LlmModelRepository").d("findDownloadedLlmModel() - Created model from directory: ${model.name}, path: ${model.getPath(context)}")
                        return model
                    }
                }
            }
        }

        Timber.tag("DEBUG_FLOW|LlmModelRepository").w("findDownloadedLlmModel() - No LLM model found in any location")
        return null
    }
}
