package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppleCard
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.AccentWarning
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.viewmodel.LifeOsViewModel

@Composable
fun HomeScreen(
    viewModel: LifeOsViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val loans by viewModel.loans.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val healthRecords by viewModel.healthRecords.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }

    val pendingTasksCount = tasks.count { it.status == "PENDING" }
    val completedTasksCount = tasks.count { it.status == "COMPLETED" }
    val totalTasks = tasks.size
    val completionPercent = if (totalTasks > 0) ((completedTasksCount.toFloat() / totalTasks) * 100).toInt() else 0

    val todayExpenses = expenses.filter { it.date.contains("10 Sept", ignoreCase = true) || it.date.contains("Today", ignoreCase = true) }
    val todaySpent = todayExpenses.sumOf { it.amount }

    val moneyToReceive = loans.filter { it.type == "LENT" && it.status == "PENDING" }.sumOf { it.amount }
    val latestWeight = healthRecords.firstOrNull()?.weight ?: 68.5
    val nextTrip = bookings.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // 1. Header Greeting & Actions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good morning,",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "Kartik",
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                }
                Row {
                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { showExportDialog = true },
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ) {
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Export Data",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. AI Daily Summary Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF312E81),
                                    Color(0xFF4338CA),
                                    Color(0xFF0E7490)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI DAILY SUMMARY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF93C5FD),
                                    letterSpacing = 1.2.sp
                                )
                            }
                            Surface(
                                color = Color(0x33FFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Live Sync",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"Today you completed $completionPercent% of your tasks and spent ₹${todaySpent.toInt()}. You have $pendingTasksCount pending items remaining.\"",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }

        // 3. Central Hero Scan & Organize Button
        item {
            Button(
                onClick = { onNavigateToTab(2) }, // Tab 2 is Scan
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Scan",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Scan & Organize Anything",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        // 4. Section Title: Today's Overview
        item {
            Text(
                text = "Today's Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // 5. Overview Grid / Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Card 1: Tasks
                MetricSummaryCard(
                    icon = Icons.Default.CheckCircle,
                    iconColor = AccentSuccess,
                    title = "Daily Tasks",
                    metric = "$pendingTasksCount pending",
                    subtext = "$completedTasksCount completed ($completionPercent%)",
                    onClick = { onNavigateToTab(1) } // Tab 1 is Tasks
                )

                // Card 2: Expenses
                MetricSummaryCard(
                    icon = Icons.Default.Payments,
                    iconColor = BrandSecondary,
                    title = "Expenses Today",
                    metric = "₹${todaySpent.toInt()}",
                    subtext = "${expenses.size} total items logged this month",
                    onClick = { onNavigateToTab(3) } // Tab 3 is Money
                )

                // Card 3: Borrow / Lending
                MetricSummaryCard(
                    icon = Icons.Default.Handshake,
                    iconColor = AccentWarning,
                    title = "Pending to Receive",
                    metric = "₹${moneyToReceive.toInt()}",
                    subtext = "From Rahul & Amit (2 active loans)",
                    onClick = { onNavigateToTab(3) } // Tab 3 is Money
                )

                // Card 4: Upcoming Trip
                if (nextTrip != null) {
                    MetricSummaryCard(
                        icon = Icons.Default.DirectionsTransit,
                        iconColor = Color(0xFF8B5CF6),
                        title = "Upcoming Trip",
                        metric = nextTrip.title,
                        subtext = "${nextTrip.date} • ${nextTrip.origin} ➔ ${nextTrip.destination}",
                        onClick = { onNavigateToTab(4) } // Tab 4 is Assistant & Vault
                    )
                }

                // Card 5: Weight & Health
                MetricSummaryCard(
                    icon = Icons.Default.FitnessCenter,
                    iconColor = Color(0xFFEC4899),
                    title = "Weight Progress",
                    metric = "$latestWeight kg",
                    subtext = "-1.5 kg from baseline • Goal: 66.0 kg",
                    onClick = { onNavigateToTab(4) }
                )
            }
        }
    }

    // Export Data Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Personal Life Data", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Export a full structured backup of your tasks, expenses, loans, bookings, and health records.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Formats: Plaintext Summary / JSON Backup\nStatus: Local Database Ready",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, viewModel.getFullExportText())
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Export LifeOS AI Data"))
                        showExportDialog = false
                    }
                ) {
                    Text("Share / Export")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MetricSummaryCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    metric: String,
    subtext: String,
    onClick: () -> Unit
) {
    AppleCard(
        onClick = onClick,
        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = metric,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtext,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
