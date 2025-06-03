package com.jahi.pipelinetest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jahi.pipelinetest.gallery.ui.ViewModelProvider
import com.jahi.pipelinetest.gallery.ui.modelmanager.ModelManagerViewModel
import com.jahi.pipelinetest.gallery.data.ImportedModelInfo
import com.jahi.pipelinetest.gallery.data.Task
import com.jahi.pipelinetest.gallery.ui.common.TaskIcon
import com.jahi.pipelinetest.gallery.ui.common.getTaskBgColor
import com.jahi.pipelinetest.gallery.ui.theme.customColors
import com.jahi.pipelinetest.gallery.ui.theme.titleMediumNarrow
import com.jahi.pipelinetest.gallery.ui.home.ModelImportDialog
import com.jahi.pipelinetest.gallery.ui.home.ModelImportingDialog
import com.jahi.pipelinetest.gallery.ui.home.NewReleaseNotification
import com.jahi.pipelinetest.gallery.ui.home.SettingsDialog
import com.jahi.pipelinetest.gallery.ui.modelmanager.ModelManager
import com.jahi.pipelinetest.gallery.data.TaskType
import com.jahi.pipelinetest.gallery.data.Model
import com.jahi.pipelinetest.gallery.ui.llmchat.LlmChatScreen
import com.jahi.pipelinetest.gallery.ui.llmsingleturn.LlmSingleTurnScreen
import com.jahi.pipelinetest.gallery.ui.textclassification.TextClassificationScreen
import com.jahi.pipelinetest.gallery.ui.imageclassification.ImageClassificationScreen
import com.jahi.pipelinetest.gallery.ui.imagegeneration.ImageGenerationScreen
import com.jahi.pipelinetest.gallery.ui.common.chat.ChatView
import com.jahi.pipelinetest.gallery.ui.common.chat.ChatInputType
import com.jahi.pipelinetest.gallery.ui.llmchat.LlmChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.background
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.graphicsLayer
import com.jahi.pipelinetest.gallery.data.ModelDownloadStatusType
import com.jahi.pipelinetest.gallery.ui.common.chat.ChatPanel
import com.jahi.pipelinetest.gallery.ui.common.chat.ChatMessage
import com.jahi.pipelinetest.gallery.ui.common.chat.ChatMessageImage
import com.jahi.pipelinetest.gallery.ui.common.chat.ChatMessageInfo
import com.jahi.pipelinetest.gallery.ui.common.chat.ChatMessageText
import com.jahi.pipelinetest.gallery.ui.common.chat.ModelDownloadStatusInfoPanel
import com.jahi.pipelinetest.gallery.ui.modelmanager.PagerScrollState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "AGGalleryScreen"
private const val TASK_COUNT_ANIMATION_DURATION = 250
private const val MAX_TASK_CARD_PADDING = 24
private const val MIN_TASK_CARD_PADDING = 18
private const val MAX_TASK_CARD_RADIUS = 43.5
private const val MIN_TASK_CARD_RADIUS = 30
private const val MAX_TASK_CARD_ICON_SIZE = 56
private const val MIN_TASK_CARD_ICON_SIZE = 50

@Composable
private fun CustomLlmChatScreen(
    modelManagerViewModel: ModelManagerViewModel,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LlmChatViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val modelManagerUiState by modelManagerViewModel.uiState.collectAsState()
    val selectedModel = modelManagerUiState.selectedModel
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    val task = viewModel.task
    
    val pagerState = rememberPagerState(
        initialPage = task.models.indexOf(selectedModel),
        pageCount = { task.models.size }
    )
    val scope = rememberCoroutineScope()
    var navigatingUp by remember { mutableStateOf(false) }

    val handleNavigateUp = {
        navigatingUp = true
        navigateUp()
        scope.launch(Dispatchers.Default) {
            for (model in task.models) {
                modelManagerViewModel.cleanupModel(task = task, model = model)
            }
        }
    }

    // Initialize model when model/download state changes
    val curDownloadStatus = modelManagerUiState.modelDownloadStatus[selectedModel.name]
    LaunchedEffect(curDownloadStatus, selectedModel.name) {
        if (!navigatingUp) {
            if (curDownloadStatus?.status == ModelDownloadStatusType.SUCCEEDED) {
                modelManagerViewModel.initializeModel(context, task = task, model = selectedModel)
            }
        }
    }

    // Update selected model when page is settled
    LaunchedEffect(pagerState.settledPage) {
        val curSelectedModel = task.models[pagerState.settledPage]
        if (curSelectedModel.name != selectedModel.name) {
            modelManagerViewModel.cleanupModel(task = task, model = selectedModel)
        }
        modelManagerViewModel.selectModel(curSelectedModel)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow {
            PagerScrollState(
                page = pagerState.currentPage, 
                offset = pagerState.currentPageOffsetFraction
            )
        }.collect { scrollState ->
            modelManagerViewModel.pagerScrollState.value = scrollState
        }
    }

    // Content without Scaffold
    Box(modifier = modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) { pageIndex ->
            val curSelectedModel = task.models[pageIndex]
            val curModelDownloadStatus = modelManagerUiState.modelDownloadStatus[curSelectedModel.name]

            val pageOffset = ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction).absoluteValue
            val curAlpha = 1f - pageOffset.coerceIn(0f, 1f)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                ModelDownloadStatusInfoPanel(
                    model = curSelectedModel, 
                    task = task, 
                    modelManagerViewModel = modelManagerViewModel
                )

                if (curModelDownloadStatus?.status == ModelDownloadStatusType.SUCCEEDED) {
                    ChatPanel(
                        modelManagerViewModel = modelManagerViewModel,
                        task = task,
                        selectedModel = curSelectedModel,
                        viewModel = viewModel,
                        navigateUp = { navigateUp() },
                        onSendMessage = { model, messages ->
                            for (message in messages) {
                                viewModel.addMessage(model = model, message = message)
                            }

                            var text = ""
                            var image: Bitmap? = null
                            var chatMessageText: ChatMessageText? = null
                            for (message in messages) {
                                if (message is ChatMessageText) {
                                    chatMessageText = message
                                    text = message.content
                                } else if (message is ChatMessageImage) {
                                    image = message.bitmap
                                }
                            }
                            if (text.isNotEmpty() && chatMessageText != null) {
                                modelManagerViewModel.addTextInputHistory(text)
                                viewModel.generateResponse(model = model, input = text, image = image, onError = {
                                    viewModel.handleError(
                                        context = context,
                                        model = model,
                                        modelManagerViewModel = modelManagerViewModel,
                                        triggeredMessage = chatMessageText,
                                    )
                                })
                            }
                        },
                        onRunAgainClicked = { model, message ->
                            if (message is ChatMessageText) {
                                viewModel.runAgain(model = model, message = message, onError = {
                                    viewModel.handleError(
                                        context = context,
                                        model = model,
                                        modelManagerViewModel = modelManagerViewModel,
                                        triggeredMessage = message,
                                    )
                                })
                            }
                        },
                        onBenchmarkClicked = { _, _, _, _ ->
                            // Benchmark functionality disabled for now
                        },
                        onStreamImageMessage = { _, _ -> },
                        onStopButtonClicked = {
                            viewModel.stopResponse(model = curSelectedModel)
                        },
                        onImageSelected = { bitmap ->
                            selectedImage = bitmap
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 80.dp)
                            .graphicsLayer { alpha = curAlpha },
                        chatInputType = ChatInputType.TEXT,
                        showStopButtonInInputWhenInProgress = false,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(modifier: Modifier = Modifier) {
    val modelManagerViewModel: ModelManagerViewModel = viewModel(factory = ViewModelProvider.Factory)
    val uiState by modelManagerViewModel.uiState.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showImportModelSheet by remember { mutableStateOf(false) }
    var showUnsupportedFileTypeDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showImportDialog by remember { mutableStateOf(false) }
    var showImportingDialog by remember { mutableStateOf(false) }
    val selectedLocalModelFileUri = remember { mutableStateOf<Uri?>(null) }
    val selectedImportedModelInfo = remember { mutableStateOf<ImportedModelInfo?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Model manager navigation state
    var showModelManager by remember { mutableStateOf(false) }
    var pickedTask by remember { mutableStateOf<Task?>(null) }
    
    // AI task screen navigation state
    var showTaskScreen by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf<Model?>(null) }
    var selectedTaskType by remember { mutableStateOf<TaskType?>(null) }

    val filePickerLauncher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileName(context = context, uri = uri)
                Log.d(TAG, "Selected file: $fileName")
                if (fileName != null && !fileName.endsWith(".task")) {
                    showUnsupportedFileTypeDialog = true
                } else {
                    selectedLocalModelFileUri.value = uri
                    showImportDialog = true
                }
            } ?: run {
                Log.d(TAG, "No file selected or URI is null.")
            }
        } else {
            Log.d(TAG, "File picking cancelled.")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        TaskList(
            tasks = uiState.tasks,
            navigateToTaskScreen = { task ->
                pickedTask = task
                showModelManager = true
            },
            loadingModelAllowlist = uiState.loadingModelAllowlist,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp),
        )

        // Floating Action Button
        SmallFloatingActionButton(
            onClick = {
                showImportModelSheet = true
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, "")
        }

        SnackbarHost(
            hostState = snackbarHostState, 
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }

    // Model manager overlay
    AnimatedVisibility(
        visible = showModelManager,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
    ) {
        val curPickedTask = pickedTask
        if (curPickedTask != null) {
            ModelManager(
                viewModel = modelManagerViewModel,
                task = curPickedTask,
                onModelClicked = { model ->
                    // Navigate to specific AI task screen
                    selectedModel = model
                    selectedTaskType = curPickedTask.type
                    modelManagerViewModel.selectModel(model)
                    showModelManager = false
                    showTaskScreen = true
                },
                navigateUp = { showModelManager = false }
            )
        }
    }

    // AI Task Screen overlay
    AnimatedVisibility(
        visible = showTaskScreen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
    ) {
        val currentModel = selectedModel
        val currentTaskType = selectedTaskType
        if (currentModel != null && currentTaskType != null) {
            when (currentTaskType) {
                TaskType.LLM_CHAT -> {
                    CustomLlmChatScreen(
                        modelManagerViewModel = modelManagerViewModel,
                        navigateUp = { showTaskScreen = false }
                    )
                }
                TaskType.LLM_PROMPT_LAB -> {
                    LlmSingleTurnScreen(
                        modelManagerViewModel = modelManagerViewModel,
                        navigateUp = { showTaskScreen = false }
                    )
                }
                TaskType.TEXT_CLASSIFICATION -> {
                    TextClassificationScreen(
                        modelManagerViewModel = modelManagerViewModel,
                        navigateUp = { showTaskScreen = false }
                    )
                }
                TaskType.IMAGE_CLASSIFICATION -> {
                    ImageClassificationScreen(
                        modelManagerViewModel = modelManagerViewModel,
                        navigateUp = { showTaskScreen = false }
                    )
                }
                TaskType.IMAGE_GENERATION -> {
                    ImageGenerationScreen(
                        modelManagerViewModel = modelManagerViewModel,
                        navigateUp = { showTaskScreen = false }
                    )
                }
                else -> {
                    // Fallback to LLM Chat for unknown task types
                    CustomLlmChatScreen(
                        modelManagerViewModel = modelManagerViewModel,
                        navigateUp = { showTaskScreen = false }
                    )
                }
            }
        }
    }

    // Settings dialog.
    if (showSettingsDialog) {
        SettingsDialog(
            curThemeOverride = modelManagerViewModel.readThemeOverride(),
            modelManagerViewModel = modelManagerViewModel,
            onDismissed = { showSettingsDialog = false },
        )
    }

    // Import model bottom sheet.
    if (showImportModelSheet) {
        ModalBottomSheet(
            onDismissRequest = { showImportModelSheet = false },
            sheetState = sheetState,
        ) {
            Text(
                "Import model",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
            )
            Box(modifier = Modifier.clickable {
                coroutineScope.launch {
                    delay(200)
                    showImportModelSheet = false

                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    }
                    filePickerLauncher.launch(intent)
                }
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.NoteAdd, contentDescription = "")
                    Text("From local model file")
                }
            }
        }
    }

    // Import dialog
    if (showImportDialog) {
        selectedLocalModelFileUri.value?.let { uri ->
            ModelImportDialog(uri = uri, onDismiss = { showImportDialog = false }, onDone = { info ->
                selectedImportedModelInfo.value = info
                showImportDialog = false
                showImportingDialog = true
            })
        }
    }

    // Importing in progress dialog.
    if (showImportingDialog) {
        selectedLocalModelFileUri.value?.let { uri ->
            selectedImportedModelInfo.value?.let { info ->
                ModelImportingDialog(uri = uri,
                    info = info,
                    onDismiss = { showImportingDialog = false },
                    onDone = {
                        modelManagerViewModel.addImportedLlmModel(
                            info = it,
                        )
                        showImportingDialog = false

                        scope.launch {
                            snackbarHostState.showSnackbar("Model imported successfully")
                        }
                    })
            }
        }
    }

    // Alert dialog for unsupported file type.
    if (showUnsupportedFileTypeDialog) {
        AlertDialog(
            onDismissRequest = { showUnsupportedFileTypeDialog = false },
            title = { Text("Unsupported file type") },
            text = {
                Text("Only \".task\" file type is supported.")
            },
            confirmButton = {
                Button(onClick = { showUnsupportedFileTypeDialog = false }) {
                    Text(stringResource(R.string.ok))
                }
            },
        )
    }

    if (uiState.loadingModelAllowlistError.isNotEmpty()) {
        AlertDialog(
            icon = {
                Icon(Icons.Rounded.Error, contentDescription = "", tint = MaterialTheme.colorScheme.error)
            },
            title = {
                Text(uiState.loadingModelAllowlistError)
            },
            text = {
                Text("Please check your internet connection and try again later.")
            },
            onDismissRequest = {
                modelManagerViewModel.loadModelAllowlist()
            },
            confirmButton = {
                TextButton(onClick = {
                    modelManagerViewModel.loadModelAllowlist()
                }) {
                    Text("Retry")
                }
            },
        )
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    navigateToTaskScreen: (Task) -> Unit,
    loadingModelAllowlist: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val screenWidthDp = remember {
        with(density) {
            windowInfo.containerSize.width.toDp()
        }
    }
    val screenHeightDp = remember {
        with(density) {
            windowInfo.containerSize.height.toDp()
        }
    }
    val sizeFraction = remember { ((screenWidthDp - 360.dp) / (410.dp - 360.dp)).coerceIn(0f, 1f) }
    val linkColor = MaterialTheme.customColors.linkColor

    val introText = buildAnnotatedString {
        append("Welcome to Google AI Edge Gallery! Explore a world of amazing on-device models from ")
        withLink(
            link = LinkAnnotation.Url(
                url = "https://huggingface.co/litert-community",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline,
                    )
                )
            )
        ) {
            append("LiteRT community")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 2),
            contentPadding = contentPadding,
            modifier = modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "newReleaseNotification", span = { GridItemSpan(2) }) {
                NewReleaseNotification()
            }

            item(key = "headline", span = { GridItemSpan(2) }) {
                Text(
                    introText,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(bottom = 20.dp).padding(horizontal = 16.dp)
                )
            }

            if (loadingModelAllowlist) {
                item(key = "loading", span = { GridItemSpan(2) }) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    ) {
                        CircularProgressIndicator(
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeWidth = 3.dp,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp)
                        )
                        Text("Loading model list...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                item(key = "llmCardsHeader", span = { GridItemSpan(2) }) {
                    Text(
                        "Example LLM Use Cases",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(tasks) { task ->
                    TaskCard(
                        sizeFraction = sizeFraction, task = task, onClick = {
                            navigateToTaskScreen(task)
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
            }

            item(key = "bottomPadding", span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeightDp * 0.25f)
                .background(
                    Color(0x1AF6AD01)
                )
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TaskCard(
    task: Task, onClick: () -> Unit, sizeFraction: Float, modifier: Modifier = Modifier
) {
    val padding =
        (MAX_TASK_CARD_PADDING - MIN_TASK_CARD_PADDING) * sizeFraction + MIN_TASK_CARD_PADDING
    val radius = (MAX_TASK_CARD_RADIUS - MIN_TASK_CARD_RADIUS) * sizeFraction + MIN_TASK_CARD_RADIUS
    val iconSize =
        (MAX_TASK_CARD_ICON_SIZE - MIN_TASK_CARD_ICON_SIZE) * sizeFraction + MIN_TASK_CARD_ICON_SIZE

    val modelCount by remember {
        derivedStateOf {
            val trigger = task.updateTrigger.value
            if (trigger >= 0) {
                task.models.size
            } else {
                0
            }
        }
    }
    val modelCountLabel by remember {
        derivedStateOf {
            when (modelCount) {
                1 -> "1 Model"
                else -> "%d Models".format(modelCount)
            }
        }
    }
    var curModelCountLabel by remember { mutableStateOf("") }
    var modelCountLabelVisible by remember { mutableStateOf(true) }
    val modelCountAlpha: Float by animateFloatAsState(
        targetValue = if (modelCountLabelVisible) 1f else 0f,
        animationSpec = tween(durationMillis = TASK_COUNT_ANIMATION_DURATION)
    )
    val modelCountScale: Float by animateFloatAsState(
        targetValue = if (modelCountLabelVisible) 1f else 0.7f,
        animationSpec = tween(durationMillis = TASK_COUNT_ANIMATION_DURATION)
    )

    LaunchedEffect(modelCountLabel) {
        if (curModelCountLabel.isEmpty()) {
            curModelCountLabel = modelCountLabel
        } else {
            modelCountLabelVisible = false
            delay(TASK_COUNT_ANIMATION_DURATION.toLong())
            curModelCountLabel = modelCountLabel
            modelCountLabelVisible = true
        }
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(radius.dp))
            .clickable(
                onClick = onClick,
            ),
        colors = CardDefaults.cardColors(
            containerColor = getTaskBgColor(task = task)
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding.dp),
        ) {
            TaskIcon(task = task, width = iconSize.dp)

            Spacer(modifier = Modifier.weight(2f))

            Text(
                task.type.label,
                color = MaterialTheme.colorScheme.primary,
                style = titleMediumNarrow.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                curModelCountLabel,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .alpha(modelCountAlpha)
                    .scale(modelCountScale),
            )
        }
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return cursor.getString(nameIndex)
                }
            }
        }
    } else if (uri.scheme == "file") {
        return uri.lastPathSegment
    }
    return null
}