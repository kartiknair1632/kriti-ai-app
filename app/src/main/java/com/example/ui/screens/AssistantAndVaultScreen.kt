package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BookingEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.GoalEntity
import com.example.data.model.HealthEntity
import com.example.ui.components.AppleCard
import com.example.ui.components.CategoryChip
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.AccentWarning
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.viewmodel.LifeOsViewModel

@Composable
fun AssistantAndVaultScreen(viewModel: LifeOsViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val healthRecords by viewModel.healthRecords.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val documents by viewModel.documents.collectAsStateWithLifecycle()
    val isAiReplying by viewModel.isAiReplying.collectAsStateWithLifecycle()

    var activeSubSection by remember { mutableIntStateOf(0) }
    // 0 = AI Assistant, 1 = Trips & Bookings, 2 = Weight & Health, 3 = Goals, 4 = Document Vault

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "Assistant & Life Hub",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Navigation Segment Chips
                val hubSections = listOf(
                    "🤖 AI Assistant",
                    "🚆 Trips & Tickets",
                    "⚖️ Weight & Health",
                    "🎯 Goals",
                    "🗂️ Document Vault"
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(hubSections.indices.toList()) { index ->
                        CategoryChip(
                            text = hubSections[index],
                            isSelected = activeSubSection == index,
                            onClick = { activeSubSection = index }
                        )
                    }
                }
            }

            // Body content depending on selected section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                when (activeSubSection) {
                    0 -> AiAssistantChatView(
                        messages = chatMessages,
                        isReplying = isAiReplying,
                        onSendMessage = { viewModel.sendChatMessage(it) },
                        onClearChat = { viewModel.clearChatHistory() }
                    )
                    1 -> BookingsView(
                        bookings = bookings,
                        onAddBooking = { type, title, date, time, pnr, origin, dest, details ->
                            viewModel.addBooking(type, title, date, time, pnr, origin, dest, details)
                        },
                        onDeleteBooking = { viewModel.deleteBooking(it) }
                    )
                    2 -> HealthTrackerView(
                        healthRecords = healthRecords,
                        onAddWeight = { w, n -> viewModel.addHealthRecord(w, n) },
                        onDeleteRecord = { viewModel.deleteHealthRecord(it) }
                    )
                    3 -> GoalsView(
                        goals = goals,
                        onToggleSubtask = { goal, idx -> viewModel.toggleGoalSubtask(goal, idx) },
                        onAddGoal = { title, cat, dl, subtasks -> viewModel.addGoal(title, cat, dl, subtasks) },
                        onDeleteGoal = { viewModel.deleteGoal(it) }
                    )
                    4 -> DocumentVaultView(
                        documents = documents,
                        onDeleteDocument = { viewModel.deleteDocument(it) }
                    )
                }
            }
        }
    }
}

// ---------------- 1. AI ASSISTANT CHAT VIEW ----------------
@Composable
fun AiAssistantChatView(
    messages: List<com.example.data.model.ChatMessageEntity>,
    isReplying: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit
) {
    var queryInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val suggestedPrompts = listOf(
        "How much money did I spend this month?",
        "What tasks are pending today?",
        "When is my next trip?",
        "Who owes me money?",
        "How much weight did I lose?"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat History List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Suggested Queries Chips
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Suggested Questions",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Clear Chat",
                            fontSize = 11.sp,
                            color = BrandPrimary,
                            modifier = Modifier.clickable { onClearChat() }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(suggestedPrompts) { prompt ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSendMessage(prompt) },
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = prompt,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Message Bubbles
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == "USER"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        color = if (isUser) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (!isUser) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.message,
                            fontSize = 14.sp,
                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (isReplying) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = BrandPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LifeOS AI is checking your records...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Input Field Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 84.dp, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = queryInput,
                onValueChange = { queryInput = it },
                placeholder = { Text("Ask anything about your bills, tasks, trips...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (queryInput.isNotBlank()) {
                            onSendMessage(queryInput)
                            queryInput = ""
                        }
                    },
                color = BrandPrimary,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ---------------- 2. UPCOMING TRIPS & TICKETS VIEW ----------------
@Composable
fun BookingsView(
    bookings: List<BookingEntity>,
    onAddBooking: (String, String, String, String, String, String, String, String) -> Unit,
    onDeleteBooking: (BookingEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Trips & Bookings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { showAddDialog = true }) {
                    Text("+ Add Booking", color = BrandPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (bookings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming travel bookings yet.\nScan ticket or IRCTC confirmation to add!",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(bookings, key = { it.id }) { b ->
                AppleCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFEDE9FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (b.type == "TRAIN") Icons.Default.DirectionsTransit else Icons.Default.Flight,
                                        contentDescription = b.type,
                                        tint = Color(0xFF7C3AED),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = b.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "PNR: ${b.pnr}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandPrimary
                                    )
                                }
                            }
                            IconButton(onClick = { onDeleteBooking(b) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "DATE & TIME",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${b.date} • ${b.time}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "ROUTE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${b.origin} ➔ ${b.destination}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        if (!b.details.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• ${b.details}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("TRAIN") }
        var date by remember { mutableStateOf("20 Sept 2026") }
        var time by remember { mutableStateOf("7:30 PM") }
        var pnr by remember { mutableStateOf("") }
        var origin by remember { mutableStateOf("New Delhi") }
        var dest by remember { mutableStateOf("Mumbai") }
        var details by remember { mutableStateOf("Coach B3, Seat 42") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Travel Booking", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Train / Flight name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CategoryChip("Train", type == "TRAIN", { type = "TRAIN" })
                        CategoryChip("Flight", type == "FLIGHT", { type = "FLIGHT" })
                        CategoryChip("Bus", type == "BUS", { type = "BUS" })
                    }
                    OutlinedTextField(
                        value = pnr,
                        onValueChange = { pnr = it },
                        label = { Text("PNR or Booking ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onAddBooking(type, title, date, time, pnr, origin, dest, details)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Save Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ---------------- 3. WEIGHT & HEALTH TRACKER VIEW ----------------
@Composable
fun HealthTrackerView(
    healthRecords: List<HealthEntity>,
    onAddWeight: (Double, String?) -> Unit,
    onDeleteRecord: (HealthEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val latest = healthRecords.firstOrNull()
    val targetWeight = 66.0
    val startWeight = 70.0
    val currentWeight = latest?.weight ?: 68.5
    val totalLost = startWeight - currentWeight

    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Safe Compliance Disclaimer Card
        item {
            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = "Health notice",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Personal fitness records only. LifeOS organizes your notes and does not provide medical advice or diagnosis.",
                        fontSize = 12.sp,
                        color = Color(0xFF1E40AF),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Weight Progress Hero Card
        item {
            AppleCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CURRENT WEIGHT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEC4899),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$currentWeight kg",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "-${String.format("%.1f", totalLost)} kg progress",
                                color = Color(0xFF047857),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    val progressRatio = ((startWeight - currentWeight) / (startWeight - targetWeight)).toFloat()
                    LinearProgressIndicator(
                        progress = { progressRatio.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFEC4899),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Start: $startWeight kg", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Goal: $targetWeight kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Log Weight Button
        item {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
            ) {
                Text("+ Log Today's Weight", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // History Log List
        item {
            Text(
                text = "Weigh-in History",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(healthRecords, key = { it.id }) { rec ->
            AppleCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${rec.weight} ${rec.unit}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${rec.date} • ${rec.notes ?: "Fasted morning check"}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onDeleteRecord(rec) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var weightStr by remember { mutableStateOf("68.5") }
        var notes by remember { mutableStateOf("Morning check") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log Weight", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (e.g. fasted, gym scale)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightStr.toDoubleOrNull()
                        if (w != null && w > 0.0) {
                            onAddWeight(w, notes)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ---------------- 4. GOALS VIEW ----------------
@Composable
fun GoalsView(
    goals: List<GoalEntity>,
    onToggleSubtask: (GoalEntity, Int) -> Unit,
    onAddGoal: (String, String, String, List<String>) -> Unit,
    onDeleteGoal: (GoalEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vision & Long-Term Goals",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { showAddDialog = true }) {
                    Text("+ New Goal", color = BrandPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(goals, key = { it.id }) { goal ->
            val subtasks = goal.subtasks.split("|").filter { it.isNotBlank() }

            AppleCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = BrandPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = goal.category.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = goal.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Target: ${goal.deadline}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDeleteGoal(goal) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { goal.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = BrandPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "${goal.progress}% Completed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandPrimary
                    )

                    // Interactive Connected Milestones Checklist
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        subtasks.forEachIndexed { idx, sub ->
                            val parts = sub.split(":")
                            val taskText = parts[0]
                            val isDone = parts.getOrNull(1) == "done"

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onToggleSubtask(goal, idx) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isDone) AccentSuccess else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.5.dp, if (isDone) AccentSuccess else MaterialTheme.colorScheme.outline),
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Done",
                                            tint = Color.White,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = taskText,
                                    fontSize = 13.sp,
                                    color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                    textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Learning") }
        var deadline by remember { mutableStateOf("31 Dec 2026") }
        var milestone1 by remember { mutableStateOf("") }
        var milestone2 by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Goal title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Target deadline") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = milestone1,
                        onValueChange = { milestone1 = it },
                        label = { Text("Milestone 1") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = milestone2,
                        onValueChange = { milestone2 = it },
                        label = { Text("Milestone 2") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val subs = listOfNotNull(milestone1.ifBlank { null }, milestone2.ifBlank { null })
                        if (title.isNotBlank()) {
                            onAddGoal(title, category, deadline, subs)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Save Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ---------------- 5. DOCUMENT VAULT VIEW ----------------
@Composable
fun DocumentVaultView(
    documents: List<DocumentEntity>,
    onDeleteDocument: (DocumentEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val filteredDocs = documents.filter { doc ->
        val matchesQuery = doc.title.contains(searchQuery, ignoreCase = true) ||
                (doc.aiAnalysis?.contains(searchQuery, ignoreCase = true) == true)
        val matchesCategory = selectedCategoryFilter == "ALL" || doc.category.equals(selectedCategoryFilter, ignoreCase = true)
        matchesQuery && matchesCategory
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search vault records, bills, notes...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // Vault Categories Filter Chips
        item {
            val categories = listOf("ALL", "Receipts", "Tickets", "Personal Notes", "Financial Records", "Health Records")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    CategoryChip(
                        text = cat,
                        isSelected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat }
                    )
                }
            }
        }

        // Document List
        if (filteredDocs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records matching search.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredDocs, key = { it.id }) { doc ->
                AppleCard {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = doc.category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = doc.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Scanned on ${doc.uploadedDate}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onDeleteDocument(doc) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        if (!doc.aiAnalysis.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = doc.aiAnalysis,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(10.dp),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
