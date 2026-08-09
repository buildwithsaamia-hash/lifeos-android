package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ChatMessage
import com.example.ui.components.LifeOSCard
import com.example.ui.theme.AccentLime
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class FileType { IMAGE, PDF }

data class AttachedFileItem(
    val name: String,
    val type: FileType,
    val size: String,
    val drawableRes: Int? = null
)

data class ConversationHistoryThread(
    val id: String,
    val title: String,
    val date: String,
    val previewText: String
)

@Composable
fun AIChatScreen(
    messages: List<ChatMessage>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onGeneratePlan: ((String) -> Unit)? = null,
    onSummarizeText: ((String) -> Unit)? = null,
    onTranslateText: ((String, String) -> Unit)? = null,
    onAnswerQuestion: ((String) -> Unit)? = null,
    onSuggestDailyTasks: (() -> Unit)? = null,
    onOptimizeSchedule: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("Smart AI Engine") }
    var showHistoryDrawer by remember { mutableStateOf(false) }
    var showVoiceModal by remember { mutableStateOf(false) }
    var showAttachmentDialog by remember { mutableStateOf(false) }

    // Dialogs for explicit Gemini features
    var showPlanDialog by remember { mutableStateOf(false) }
    var showSummarizeDialog by remember { mutableStateOf(false) }
    var showTranslateDialog by remember { mutableStateOf(false) }

    // Attached files preview before sending
    val attachedFiles = remember { mutableStateListOf<AttachedFileItem>() }

    // Conversation history items
    val conversationThreads = remember {
        mutableStateListOf(
            ConversationHistoryThread("1", "Architecture Review & Specs", "Today 09:15 AM", "Reviewed system modularity & Room DB schema."),
            ConversationHistoryThread("2", "Weekly Productivity Plan", "Yesterday", "Optimized focus blocks and habit routines."),
            ConversationHistoryThread("3", "Emergency Readiness Audit", "Jul 22", "Drafted ICE emergency contact list & medical details."),
            ConversationHistoryThread("4", "Python Data Analytics Script", "Jul 20", "Generated pandas script for score computation.")
        )
    }
    var activeThreadId by remember { mutableStateOf("1") }

    val presetPrompts = listOf(
        "⚡ Analyze my productivity & suggest schedule tweaks",
        "🎯 Help me break down my weekly goals into daily tasks",
        "🧘 Give me a 5-minute mental focus exercise",
        "📄 Summarize attached document and highlight action items"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_chat_screen_root")
    ) {
        val isWideScreen = maxWidth > 650.dp

        Row(modifier = Modifier.fillMaxSize()) {

            // --- 1. Responsive Conversation History Sidebar ---
            if (isWideScreen || showHistoryDrawer) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight(),
                    tonalElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        // Header & New Chat Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Chat History",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Chat History",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (!isWideScreen) {
                                IconButton(onClick = { showHistoryDrawer = false }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close History", tint = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                onClearChat()
                                activeThreadId = System.currentTimeMillis().toString()
                                conversationThreads.add(0, ConversationHistoryThread(activeThreadId, "New Conversation", "Just now", "Started new chat session."))
                                Toast.makeText(context, "Started New Chat Session", Toast.LENGTH_SHORT).show()
                                if (!isWideScreen) showHistoryDrawer = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "New Chat", tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("New Chat", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Recent Conversations", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(conversationThreads, key = { it.id }) { thread ->
                                val isSelected = thread.id == activeThreadId
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                                        .border(
                                            1.dp,
                                            if (isSelected) PrimaryCyan else Color.Transparent,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            activeThreadId = thread.id
                                            Toast.makeText(context, "Loaded: ${thread.title}", Toast.LENGTH_SHORT).show()
                                            if (!isWideScreen) showHistoryDrawer = false
                                        }
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = thread.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = thread.previewText,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 2. Main Chat Workspace ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 14.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // --- Top Model & Action Bar ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isWideScreen) {
                            IconButton(onClick = { showHistoryDrawer = !showHistoryDrawer }) {
                                Icon(imageVector = Icons.Default.History, contentDescription = "History Drawer", tint = PrimaryCyan)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Model", tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "LifeOS AI Assistant",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentLime.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ONLINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentLime)
                                }
                            }

                            Text(
                                text = "Model: $selectedModel • Multimodal",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { onClearChat() }) {
                            Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear Chat", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- Smart AI Engine Core Toolkit Bar ---
                Text("Smart AI Engine Toolkit:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Button(
                            onClick = { showPlanDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📝 Generate Plan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    item {
                        Button(
                            onClick = { showSummarizeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📄 Summarize", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    item {
                        Button(
                            onClick = { showTranslateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🌐 Translate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    item {
                        Button(
                            onClick = {
                                if (onSuggestDailyTasks != null) {
                                    onSuggestDailyTasks()
                                } else {
                                    onSendMessage("⚡ Suggest daily high-priority tasks for today and add them to my planner.")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentLime),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("⚡ Suggest Daily Tasks", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    item {
                        Button(
                            onClick = {
                                if (onOptimizeSchedule != null) {
                                    onOptimizeSchedule()
                                } else {
                                    onSendMessage("⏳ Optimize my daily schedule timeline to maximize productivity and balance energy.")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("⏳ Optimize Schedule", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- Chat Messages List ---
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatMessageItem(
                            message = msg,
                            onCopyText = {
                                clipboardManager.setText(AnnotatedString(it))
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    // --- Typing Animation Indicator ---
                    if (isThinking) {
                        item {
                            TypingAnimationIndicator()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- Attached Files Preview Box (if any) ---
                if (attachedFiles.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        items(attachedFiles) { file ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PrimaryCyan.copy(alpha = 0.15f))
                                    .border(1.dp, PrimaryCyan, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (file.type == FileType.IMAGE) Icons.Default.Image else Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(text = file.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = file.size, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = EmergencyRed,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { attachedFiles.remove(file) }
                                    )
                                }
                            }
                        }
                    }
                }

                // --- Input Bar with Voice, Image, PDF Attachments & Send ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment Action Icon
                    IconButton(
                        onClick = { showAttachmentDialog = true },
                        modifier = Modifier.testTag("attach_file_button")
                    ) {
                        Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attach File", tint = PrimaryCyan)
                    }

                    // Voice Mic Icon
                    IconButton(
                        onClick = { showVoiceModal = true },
                        modifier = Modifier.testTag("voice_input_button")
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Input", tint = AccentLime)
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask LifeOS Assistant, paste image or doc...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(PrimaryCyan)
                            .clickable {
                                if (inputText.isNotBlank() || attachedFiles.isNotEmpty()) {
                                    val prefix = if (attachedFiles.isNotEmpty()) {
                                        "[Attached: ${attachedFiles.joinToString { it.name }}]\n"
                                    } else ""
                                    onSendMessage(prefix + inputText)
                                    inputText = ""
                                    attachedFiles.clear()
                                }
                            }
                            .testTag("send_chat_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // --- Voice Recording Modal Dialog ---
    if (showVoiceModal) {
        VoiceRecordingModal(
            onDismiss = { showVoiceModal = false },
            onTranscribed = { text ->
                inputText = text
                showVoiceModal = false
            }
        )
    }

    // --- Attachment Picker Dialog ---
    if (showAttachmentDialog) {
        AttachmentPickerDialog(
            onDismiss = { showAttachmentDialog = false },
            onSelectFile = { item ->
                attachedFiles.add(item)
                showAttachmentDialog = false
            }
        )
    }

    // --- LifeOS AI Generate Plan Dialog ---
    if (showPlanDialog) {
        var planTopic by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPlanDialog = false },
            title = { Text("📝 Generate Action Plan with LifeOS AI", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your goal, project, or topic. Smart AI Engine will generate a multi-step execution breakdown.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = planTopic,
                        onValueChange = { planTopic = it },
                        placeholder = { Text("e.g. Master Kotlin Jetpack Compose in 30 days") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (planTopic.isNotBlank()) {
                            if (onGeneratePlan != null) {
                                onGeneratePlan(planTopic)
                            } else {
                                onSendMessage("📝 Generate step-by-step action plan for: $planTopic")
                            }
                            showPlanDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple)
                ) {
                    Text("Generate Plan", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPlanDialog = false }) { Text("Cancel") }
            }
        )
    }

    // --- LifeOS AI Summarize Dialog ---
    if (showSummarizeDialog) {
        var summarizeInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSummarizeDialog = false },
            title = { Text("📄 LifeOS AI Executive Summarizer", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste any article, long document text, or notes below:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = summarizeInput,
                        onValueChange = { summarizeInput = it },
                        placeholder = { Text("Paste text or notes to summarize...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (summarizeInput.isNotBlank()) {
                            if (onSummarizeText != null) {
                                onSummarizeText(summarizeInput)
                            } else {
                                onSendMessage("📄 Summarize the following document concisely in bullet points:\n$summarizeInput")
                            }
                            showSummarizeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                ) {
                    Text("Summarize Text", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSummarizeDialog = false }) { Text("Cancel") }
            }
        )
    }

    // --- LifeOS AI Translate Dialog ---
    if (showTranslateDialog) {
        var translateInput by remember { mutableStateOf("") }
        var targetLang by remember { mutableStateOf("Spanish") }
        val languages = listOf("Spanish", "French", "German", "Japanese", "Chinese", "Hindi", "Arabic", "Portuguese")

        AlertDialog(
            onDismissRequest = { showTranslateDialog = false },
            title = { Text("🌐 LifeOS AI Polyglot Translator", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Target Language:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(languages) { lang ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (targetLang == lang) PrimaryCyan else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { targetLang = lang }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(lang, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (targetLang == lang) Color.Black else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = translateInput,
                        onValueChange = { translateInput = it },
                        placeholder = { Text("Type or paste text to translate...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (translateInput.isNotBlank()) {
                            if (onTranslateText != null) {
                                onTranslateText(translateInput, targetLang)
                            } else {
                                onSendMessage("🌐 Translate the following text into $targetLang:\n$translateInput")
                            }
                            showTranslateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D))
                ) {
                    Text("Translate to $targetLang", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTranslateDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// --- Typing Animation Component ---
@Composable
fun TypingAnimationIndicator() {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "typing_dots")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200, easing = LinearEasing), RepeatMode.Reverse),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400, easing = LinearEasing), RepeatMode.Reverse),
        label = "dot3"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, PrimaryCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("LifeOS AI is generating response", fontSize = 12.sp, color = PrimaryCyan, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("•", fontSize = 18.sp, color = PrimaryCyan.copy(alpha = dot1Alpha))
            Spacer(modifier = Modifier.width(2.dp))
            Text("•", fontSize = 18.sp, color = PrimaryCyan.copy(alpha = dot2Alpha))
            Spacer(modifier = Modifier.width(2.dp))
            Text("•", fontSize = 18.sp, color = PrimaryCyan.copy(alpha = dot3Alpha))
        }
    }
}

// --- Chat Message Item Component ---
@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onCopyText: (String) -> Unit
) {
    val isUser = message.sender.lowercase() == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.85f else 0.95f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) PrimaryCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                )
                .border(
                    1.dp,
                    if (isUser) PrimaryCyan else SecondaryPurple.copy(alpha = 0.4f),
                    RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .testTag("chat_message_bubble_${message.id}")
        ) {
            Column {
                // Header with Sender name & Copy Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isUser) Icons.Default.AutoAwesome else Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = if (isUser) PrimaryCyan else SecondaryPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUser) "You" else "LifeOS AI Assistant",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUser) PrimaryCyan else SecondaryPurple
                        )
                    }

                    IconButton(
                        onClick = { onCopyText(message.text) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Text",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Message Text with Markdown Formatting Support
                MarkdownText(text = message.text)
            }
        }
    }
}

// --- Simplified Markdown & Formatting Renderer ---
@Composable
fun MarkdownText(text: String) {
    val lines = text.split("\n")

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        lines.forEach { line ->
            when {
                // Code block line (starts with ``` or contains code)
                line.startsWith("```") -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E1E2E))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = line.replace("```", "").trim(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = AccentLime
                        )
                    }
                }
                // Bullet points
                line.trim().startsWith("•") || line.trim().startsWith("-") -> {
                    Row(modifier = Modifier.padding(start = 6.dp)) {
                        Text("• ", fontWeight = FontWeight.Bold, color = PrimaryCyan, fontSize = 13.sp)
                        Text(
                            text = line.trim().removePrefix("•").removePrefix("-").trim(),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
                // Bold Headers (### or **Header**)
                line.startsWith("###") || (line.startsWith("**") && line.endsWith("**")) -> {
                    Text(
                        text = line.replace("###", "").replace("**", "").trim(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                }
                // Standard Body Text
                else -> {
                    Text(
                        text = line,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// --- Voice Input Modal Component ---
@Composable
fun VoiceRecordingModal(
    onDismiss: () -> Unit,
    onTranscribed: (String) -> Unit
) {
    var isRecording by remember { mutableStateOf(true) }
    var voiceText by remember { mutableStateOf("Listening... \"Analyze my daily goals and create a focus plan for tomorrow afternoon.\"") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = AccentLime)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voice Assistant Input", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Pulsating Wave Circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (isRecording) AccentLime.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, if (isRecording) AccentLime else Color.Gray, CircleShape)
                        .clickable { isRecording = !isRecording },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Mic Record",
                        tint = if (isRecording) AccentLime else Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isRecording) "Listening to speech..." else "Recording Paused",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRecording) AccentLime else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        text = voiceText,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onTranscribed("Analyze my daily goals and create a focus plan for tomorrow afternoon.") },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
            ) {
                Text("Insert Transcript", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// --- Attachment Picker Dialog Component ---
@Composable
fun AttachmentPickerDialog(
    onDismiss: () -> Unit,
    onSelectFile: (AttachedFileItem) -> Unit
) {
    val mockSampleFiles = listOf(
        AttachedFileItem("Architecture_Diagram.png", FileType.IMAGE, "1.4 MB", R.drawable.img_lifeos_hero_1784907572607),
        AttachedFileItem("Medical_Record_Scan.jpg", FileType.IMAGE, "850 KB", R.drawable.img_ai_assistant_1784907594826),
        AttachedFileItem("Financial_Report_Q3.pdf", FileType.PDF, "2.8 MB"),
        AttachedFileItem("Blood_Test_Summary.pdf", FileType.PDF, "1.1 MB"),
        AttachedFileItem("Project_Specs_v2.pdf", FileType.PDF, "3.5 MB")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = PrimaryCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Image or PDF Document", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Choose file to analyze with LifeOS AI Vision & Document OCR:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                mockSampleFiles.forEach { file ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onSelectFile(file) }
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (file.type == FileType.IMAGE) Icons.Default.Image else Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = file.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(text = "${file.type.name} • ${file.size}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Text("+ Attach", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryCyan)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
