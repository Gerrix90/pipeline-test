package com.jahi.pipelinetest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.res.stringResource
import com.jahi.pipelinetest.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PADDING_DEFAULT = 16
private const val PADDING_SMALL = 8
private const val MESSAGE_CORNER_RADIUS = 12

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Sample responses for demo
    val sampleResponses = listOf(
        "Hello! I'm a demo AI assistant. How can I help you today?",
        "That's an interesting question! Let me think about that...",
        "I understand. Here's what I think about that topic:",
        "Great point! I'd be happy to help you with that.",
        "Thanks for sharing that. Here's my perspective:",
        "I see what you mean. Let me provide some insights:",
        "That's a good question. Based on my knowledge:",
        "Interesting! I'd like to explore this further with you."
    )

    fun sendMessage() {
        if (inputText.isBlank()) return
        
        val userMessage = ChatMessage(inputText, true)
        messages = messages + userMessage
        inputText = ""
        isLoading = true
        
        // Simulate AI response delay
        coroutineScope.launch {
            delay(1000L + (0..2000).random())
            val aiResponse = ChatMessage(sampleResponses.random(), false)
            messages = messages + aiResponse
            isLoading = false
            
            // Auto-scroll to bottom
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_llm_chat)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = PADDING_DEFAULT.dp),
                verticalArrangement = Arrangement.spacedBy(PADDING_SMALL.dp),
                contentPadding = PaddingValues(vertical = PADDING_DEFAULT.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
                
                if (isLoading) {
                    item {
                        LoadingMessage()
                    }
                }
            }
            
            // Input area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PADDING_DEFAULT.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PADDING_DEFAULT.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text(stringResource(R.string.chat_textinput_placeholder)) },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        maxLines = 3
                    )
                    
                    Spacer(modifier = Modifier.width(PADDING_SMALL.dp))
                    
                    IconButton(
                        onClick = { sendMessage() },
                        enabled = inputText.isNotBlank() && !isLoading
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank() && !isLoading) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(MESSAGE_CORNER_RADIUS.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(PADDING_DEFAULT.dp),
                color = if (message.isUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoadingMessage(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 120.dp),
            shape = RoundedCornerShape(MESSAGE_CORNER_RADIUS.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(PADDING_DEFAULT.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(PADDING_SMALL.dp))
                Text(
                    text = "Typing...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}