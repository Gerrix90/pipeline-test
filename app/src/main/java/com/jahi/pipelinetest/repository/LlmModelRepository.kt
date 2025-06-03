package com.jahi.pipelinetest.repository

import android.content.Context
import android.util.Log
import com.jahi.pipelinetest.gallery.data.Model
import com.jahi.pipelinetest.gallery.ui.llmchat.LlmChatModelHelper
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
        cachedModel?.let { model ->
            if (model.instance != null) {
                return@withContext model
            }
        }

        val model = findDownloadedLlmModel(context) ?: return@withContext null

        if (model.instance == null) {
            val success = withTimeoutOrNull(INITIALIZATION_TIMEOUT_MS) {
                suspendCancellableCoroutine<Boolean> { cont ->
                    LlmChatModelHelper.initialize(
                        context = context,
                        model = model,
                        onDone = { error ->
                            cont.resume(error.isEmpty())
                        }
                    )
                }
            } == true

            if (!success) {
                Log.e(TAG, "Failed to initialize model")
                return@withContext null
            }
        }

        cachedModel = model
        return@withContext model
    }

    private fun findDownloadedLlmModel(context: Context): Model? {
        val externalDir = context.getExternalFilesDir(null) ?: return null
        Log.d(TAG, "External files dir: ${'$'}{externalDir.absolutePath}")

        val importsDir = File(externalDir, "__imports")
        if (importsDir.exists()) {
            importsDir.listFiles()?.forEach { file ->
                if ((file.name.endsWith(".task") || file.name.endsWith(".bin")) &&
                    file.length() > MIN_MODEL_SIZE_BYTES
                ) {
                    val model = Model(
                        name = file.nameWithoutExtension,
                        downloadFileName = "__imports/${'$'}{file.name}",
                        sizeInBytes = file.length(),
                        version = "imported",
                        url = "",
                        imported = true
                    )
                    model.preProcess()
                    return model
                }
            }
        }

        val modelDirs = externalDir.listFiles { f -> f.isDirectory && !f.name.startsWith("__") }
        modelDirs?.forEach { modelDir ->
            val versionDirs = modelDir.listFiles { f -> f.isDirectory }
            versionDirs?.forEach { versionDir ->
                val modelFiles = versionDir.listFiles { f ->
                    f.name.endsWith(".task") || f.name.endsWith(".bin")
                }
                modelFiles?.forEach { modelFile ->
                    if (modelFile.length() > MIN_MODEL_SIZE_BYTES) {
                        val model = Model(
                            name = modelDir.name,
                            downloadFileName = modelFile.name,
                            sizeInBytes = modelFile.length(),
                            version = versionDir.name,
                            url = "",
                            imported = false
                        )
                        model.preProcess()
                        return model
                    }
                }
            }
        }

        Log.d(TAG, "No LLM model found in any location")
        return null
    }
}
