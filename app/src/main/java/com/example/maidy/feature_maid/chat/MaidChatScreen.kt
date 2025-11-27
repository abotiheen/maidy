package com.example.maidy.feature_maid.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.core.model.ChatMessage
import com.example.maidy.feature_maid.auth.*
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MaidChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: MaidChatViewModel = koinViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaidAppBackgroundLight)
    ) {
        // Chat messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            items(messages) { message ->
                MaidChatMessageItem(message = message)
            }
        }

        // Input section
        Surface(
            shadowElevation = 0.dp,
            color = MaidAppBackgroundWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Ask me anything about your bookings...",
                            color = MaidAppTextSecondary
                        )
                    },
                    enabled = !isProcessing,
                    singleLine = false,
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaidAppBorderLight,
                        focusedBorderColor = MaidAppGreen,
                        disabledBorderColor = MaidAppBorderLight,
                        unfocusedContainerColor = MaidAppBackgroundWhite,
                        focusedContainerColor = MaidAppBackgroundWhite,
                        disabledContainerColor = MaidAppBackgroundLight,
                        unfocusedTextColor = MaidAppTextPrimary,
                        focusedTextColor = MaidAppTextPrimary
                    )
                )

                FilledIconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(messageText)
                            messageText = ""
                        }
                    },
                    enabled = messageText.isNotBlank() && !isProcessing,
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaidAppGreen,
                        contentColor = Color.White,
                        disabledContainerColor = MaidAppButtonDisabled,
                        disabledContentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}

@Composable
fun MaidChatMessageItem(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isFromUser) 20.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 20.dp
                    )
                )
                .background(
                    if (message.isFromUser) {
                        MaidAppGreen
                    } else {
                        MaidAppBackgroundWhite
                    }
                )
                .then(
                    if (!message.isFromUser) {
                        Modifier.border(
                            width = 1.dp,
                            color = MaidAppBorderLight,
                            shape = RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 20.dp
                            )
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            if (message.isLoading) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaidAppGreen
                    )
                    Text(
                        text = "Thinking...",
                        fontSize = 14.sp,
                        color = MaidAppTextSecondary
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = message.content,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = if (message.isFromUser) {
                            MaidAppTextLight
                        } else {
                            MaidAppTextPrimary
                        }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = formatTimestamp(message.timestamp.toDate()),
                        fontSize = 11.sp,
                        color = if (message.isFromUser) {
                            MaidAppTextLight.copy(alpha = 0.75f)
                        } else {
                            MaidAppTextSecondary
                        }
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(date: Date): String {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}
