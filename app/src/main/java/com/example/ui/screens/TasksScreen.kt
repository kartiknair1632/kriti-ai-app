package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TaskEntity
import com.example.ui.components.AiInsightCard
import com.example.ui.components.AppleCard
import com.example.ui.components.CategoryChip
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.BrandPrimary
import com.example.ui.viewmodel.LifeOsViewModel

@Composable
fun TasksScreen(viewModel: LifeOsViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, MORNING, AFTERNOON, NIGHT, COMPLETED
    var showAddDialog by remember { mutableStateOf(false) }
    var habitCardDismissed by remember { mutableStateOf(false) }

    val completedCount = tasks.count { it.status == "COMPLETED" }
    val totalCount = tasks.size
    val score = if (totalCount > 0) ((completedCount.toFloat() / totalCount) * 100).toInt() else 0

    val filteredTasks = tasks.filter { task ->
        when (selectedFilter) {
            "ALL" -> task.status == "PENDING"
            "MORNING" -> task.timeOfDay == "MORNING" && task.status == "PENDING"
            "AFTERNOON" -> task.timeOfDay == "AFTERNOON" && task.status == "PENDING"
            "NIGHT" -> task.timeOfDay == "NIGHT" && task.status == "PENDING"
            "COMPLETED" -> task.status == "COMPLETED"
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BrandPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily To-Do Manager",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Manage tasks, routines, habits, and goals",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Productivity Score Ring Card
            item {
                AppleCard(backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PRODUCTIVITY SCORE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$score%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$completedCount of $totalCount daily tasks completed",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { score / 100f },
                                modifier = Modifier.size(64.dp),
                                color = if (score >= 70) AccentSuccess else BrandPrimary,
                                trackColor = MaterialTheme.colorScheme.surface,
                                strokeWidth = 7.dp
                            )
                            Text(
                                text = "$score%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // AI Suggestion Card
            if (!habitCardDismissed) {
                item {
                    AiInsightCard(
                        insight = "You usually exercise at 7 AM. Should I create this as a daily habit with automatic reminders?",
                        actionLabel = "✓ Create Daily Habit",
                        onActionClick = {
                            viewModel.addTask(
                                title = "Morning 7 AM Daily Exercise",
                                priority = "HIGH",
                                timeOfDay = "MORNING",
                                deadline = "Everyday 7:00 AM",
                                category = "Health"
                            )
                            habitCardDismissed = true
                        }
                    )
                }
            }

            // Filter Tabs (All, Morning, Afternoon, Night, Completed)
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { CategoryChip("All Pending", selectedFilter == "ALL", { selectedFilter = "ALL" }) }
                    item { CategoryChip("Morning", selectedFilter == "MORNING", { selectedFilter = "MORNING" }) }
                    item { CategoryChip("Afternoon", selectedFilter == "AFTERNOON", { selectedFilter = "AFTERNOON" }) }
                    item { CategoryChip("Night", selectedFilter == "NIGHT", { selectedFilter = "NIGHT" }) }
                    item { CategoryChip("Completed ($completedCount)", selectedFilter == "COMPLETED", { selectedFilter = "COMPLETED" }) }
                }
            }

            // Tasks List
            if (filteredTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No tasks in this section 🎉",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap + or Scan an image to auto-generate tasks",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskItemRow(
                        task = task,
                        onToggle = { viewModel.toggleTask(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var priority by remember { mutableStateOf("MEDIUM") }
        var timeOfDay by remember { mutableStateOf("MORNING") }
        var deadline by remember { mutableStateOf("Today") }
        var category by remember { mutableStateOf("Personal") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Task", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task description") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Due time (e.g. 5:00 PM)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text("Time of Day", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("MORNING", "AFTERNOON", "NIGHT").forEach { time ->
                            CategoryChip(time, timeOfDay == time, { timeOfDay = time })
                        }
                    }
                    Text("Priority", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("HIGH", "MEDIUM", "LOW").forEach { p ->
                            CategoryChip(p, priority == p, { priority = p })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addTask(title, priority, timeOfDay, deadline, category)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add Task")
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

@Composable
fun TaskItemRow(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = task.status == "COMPLETED"

    AppleCard(
        onClick = onToggle,
        borderColor = if (isCompleted) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle checkbox
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) AccentSuccess else Color.Transparent)
                        .clickable { onToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isCompleted) AccentSuccess else MaterialTheme.colorScheme.surface,
                        border = if (!isCompleted) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline) else null,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = task.title,
                        fontSize = 15.sp,
                        fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriorityBadge(task.priority)
                        if (task.deadline.isNotBlank()) {
                            Text(
                                text = "⏰ ${task.deadline}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (task.isRecurring) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = "Recurring",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
