package com.gndy.camman.presentation.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gndy.camman.data.chat.ConnectionState
import com.gndy.camman.domain.model.ChatUserType
import com.gndy.camman.domain.model.MessageDeliveryStatus
import com.gndy.camman.domain.model.MessageType
import com.gndy.camman.domain.model.Message
import com.gndy.camman.domain.model.QuickReply
import com.gndy.camman.presentation.theme.Gold
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String? = null,
    photographerId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Load conversation
    LaunchedEffect(conversationId, photographerId) {
        when {
            conversationId != null -> viewModel.loadConversation(conversationId)
            photographerId != null -> viewModel.loadOrCreateConversation(photographerId)
        }
    }
    
    // Handle UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is ChatUiEvent.ScrollToBottom -> {
                    if (uiState.messages.isNotEmpty()) {
                        scope.launch {
                            listState.animateScrollToItem(uiState.messages.size - 1)
                        }
                    }
                }
                else -> {}
            }
        }
    }
    
    // Get the other participant (photographer)
    val otherParticipant = uiState.conversation?.participants?.find { 
        it.userType == ChatUserType.PHOTOGRAPHER 
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (otherParticipant?.imageUrl != null) {
                            AsyncImage(
                                model = otherParticipant.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Column {
                            Text(
                                text = otherParticipant?.name ?: "Chat",
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            // Show status: typing > online/offline > connection state
                            when {
                                uiState.isOtherUserTyping -> {
                                    Text(
                                        text = "typing...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Gold
                                    )
                                }
                                uiState.connectionState == ConnectionState.RECONNECTING -> {
                                    Text(
                                        text = "Reconnecting...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFFA000)
                                    )
                                }
                                uiState.connectionState == ConnectionState.ERROR -> {
                                    Text(
                                        text = "Connection error",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFF44336)
                                    )
                                }
                                otherParticipant?.isOnline == true -> {
                                    Text(
                                        text = "Online",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "Offline",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            // Loading state
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold)
                }
            } else {
                // Messages list
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.messages) { message ->
                        MessageBubble(
                            message = message,
                            isOwnMessage = message.senderId == viewModel.currentUserId,
                            onLongClick = { viewModel.setReplyToMessage(message) }
                        )
                    }
                    
                    // Show typing indicator at the bottom
                    if (uiState.isOtherUserTyping) {
                        item {
                            TypingIndicatorBubble(
                                userName = otherParticipant?.name ?: "User",
                                imageUrl = otherParticipant?.imageUrl
                            )
                        }
                    }
                }
                
                // Quick replies
                AnimatedVisibility(
                    visible = uiState.showQuickReplies,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    QuickRepliesRow(
                        quickReplies = uiState.quickReplies,
                        onQuickReplyClick = { viewModel.useQuickReply(it) }
                    )
                }
                
                // Reply preview
                AnimatedVisibility(visible = uiState.replyToMessage != null) {
                    ReplyPreview(
                        message = uiState.replyToMessage,
                        onCancelReply = { viewModel.cancelReply() }
                    )
                }
                
                // Input area
                ChatInputArea(
                    messageText = uiState.messageText,
                    isSending = uiState.isSending,
                    onMessageTextChanged = { viewModel.onMessageTextChanged(it) },
                    onSendClick = { viewModel.sendMessage() },
                    onQuickRepliesToggle = { viewModel.toggleQuickReplies() }
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isOwnMessage: Boolean,
    onLongClick: () -> Unit
) {
    val bubbleColor = if (isOwnMessage) {
        Gold
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = if (isOwnMessage) {
        Color.Black
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
    ) {
        // Show avatar for received messages
        if (!isOwnMessage && message.senderImageUrl != null) {
            AsyncImage(
                model = message.senderImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isOwnMessage) 16.dp else 4.dp,
                    bottomEnd = if (isOwnMessage) 4.dp else 16.dp
                ),
                color = bubbleColor,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clickable(onClick = onLongClick)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Image attachment (if present)
                    if (message.messageType == MessageType.IMAGE && message.attachmentUrl != null) {
                        AsyncImage(
                            model = message.attachmentUrl,
                            contentDescription = "Attached image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        if (message.content.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    // Message text
                    if (message.content.isNotEmpty()) {
                        Text(
                            text = message.content,
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // Timestamp and delivery status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatMessageTime(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                // Show delivery status for own messages
                if (isOwnMessage) {
                    DeliveryStatusIcon(
                        status = message.deliveryStatus,
                        isRead = message.isRead
                    )
                }
            }
        }
    }
}

@Composable
private fun DeliveryStatusIcon(
    status: MessageDeliveryStatus,
    isRead: Boolean
) {
    val (icon, tint, description) = when {
        isRead || status == MessageDeliveryStatus.READ -> Triple(
            "✓✓", // Double check
            Color(0xFF4CAF50), // Green for read
            "Read"
        )
        status == MessageDeliveryStatus.DELIVERED -> Triple(
            "✓✓", // Double check
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            "Delivered"
        )
        status == MessageDeliveryStatus.SENT -> Triple(
            "✓", // Single check
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            "Sent"
        )
        status == MessageDeliveryStatus.SENDING -> Triple(
            "◷", // Clock icon
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            "Sending"
        )
        status == MessageDeliveryStatus.FAILED -> Triple(
            "!", // Error
            Color(0xFFF44336), // Red for failed
            "Failed"
        )
        else -> Triple("✓", MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), "Sent")
    }
    
    Text(
        text = icon,
        style = MaterialTheme.typography.labelSmall,
        color = tint
    )
}

@Composable
private fun QuickRepliesRow(
    quickReplies: List<QuickReply>,
    onQuickReplyClick: (QuickReply) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        items(quickReplies) { quickReply ->
            Card(
                onClick = { onQuickReplyClick(quickReply) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = quickReply.text,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ReplyPreview(
    message: Message?,
    onCancelReply: () -> Unit
) {
    if (message == null) return
    
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(36.dp)
                    .background(Gold, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Replying to ${message.senderName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = onCancelReply) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel reply",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatInputArea(
    messageText: String,
    isSending: Boolean,
    onMessageTextChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    onQuickRepliesToggle: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick replies toggle
            IconButton(onClick = onQuickRepliesToggle) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Quick replies",
                    tint = Gold
                )
            }
            
            // Text input
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageTextChanged,
                placeholder = { Text("Type a message...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendClick() })
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Send button
            IconButton(
                onClick = onSendClick,
                enabled = messageText.isNotBlank() && !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Gold,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (messageText.isNotBlank()) Gold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TypingIndicatorBubble(
    userName: String,
    imageUrl: String?
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    
    val dot1Alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    
    val dot2Alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    
    val dot3Alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        // Typing bubble with animated dots
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 16.dp
            ),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = dot1Alpha.value)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = dot2Alpha.value)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = dot3Alpha.value)
                        )
                )
            }
        }
    }
}

private fun formatMessageTime(timestamp: Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
}
