package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppleCard
import com.example.ui.components.CategoryChip
import com.example.ui.theme.AccentDanger
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.AccentWarning
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.viewmodel.LifeOsViewModel
import com.example.ui.viewmodel.PresetSample

@Composable
fun ScanScreen(
    viewModel: LifeOsViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val context = LocalContext.current
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val analysisResult by viewModel.analysisResult.collectAsStateWithLifecycle()
    val chosenSection by viewModel.chosenSection.collectAsStateWithLifecycle()
    val currentBitmap by viewModel.currentBitmap.collectAsStateWithLifecycle()
    val currentText by viewModel.currentScannedText.collectAsStateWithLifecycle()

    var showTextInputDialog by remember { mutableStateOf(false) }
    var showCameraPermissionRationale by remember { mutableStateOf(false) }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.analyzeImage(bitmap)
        }
    }

    // Camera Runtime Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                showCameraPermissionRationale = true
            }
        } else {
            showCameraPermissionRationale = true
        }
    }

    // Photo Picker Launcher (Zero-Permission per Play Policy)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                viewModel.analyzeImage(bitmap)
            } catch (e: Exception) {
                viewModel.analyzeText("Document uploaded from Gallery (${uri.lastPathSegment})")
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scan & Organize",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Snap or upload bills, handwritten plans, tickets, loans, or fitness records. AI reads and organizes them automatically.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        // 2. Action Capture Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Camera Button
                CaptureActionButton(
                    icon = Icons.Default.CameraAlt,
                    label = "Take Photo",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            try {
                                cameraLauncher.launch(null)
                            } catch (e: Exception) {
                                showCameraPermissionRationale = true
                            }
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )
                // Gallery Picker Button
                CaptureActionButton(
                    icon = Icons.Default.PhotoLibrary,
                    label = "Gallery / File",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
                // Paste Text / Note Button
                CaptureActionButton(
                    icon = Icons.Default.EditNote,
                    label = "Paste Note",
                    modifier = Modifier.weight(1f),
                    onClick = { showTextInputDialog = true }
                )
            }
        }

        // 3. Quick Demo Presets
        item {
            Column {
                Text(
                    text = "Quick Demo Presets (Instant AI Test)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PresetSample.values()) { preset ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.loadPreset(preset) },
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = preset.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Loading / Analyzing State
        if (isAnalyzing) {
            item {
                AppleCard(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = BrandPrimary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Multimodal AI Reading Document...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Extracting text, amounts, dates, and categories",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 5. Analysis Result Card
        if (analysisResult != null && !isAnalyzing) {
            val res = analysisResult!!

            // Duplicate Detection Alert if triggered
            if (res.isDuplicate && res.duplicateMessage != null) {
                item {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Duplicate Warning",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = res.duplicateMessage,
                                color = Color(0xFF92400E),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Preview Thumbnail if Bitmap exists
            if (currentBitmap != null) {
                item {
                    Image(
                        bitmap = currentBitmap!!.asImageBitmap(),
                        contentDescription = "Scanned Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(18.dp))
                    )
                }
            }

            // AI "Where should I organize this?" Section
            item {
                AppleCard(borderColor = BrandPrimary.copy(alpha = 0.4f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "AI CLASSIFICATION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Where should I organize this?",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                color = AccentSuccess.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "AI Suggested",
                                    color = AccentSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Category Chips to choose / switch destination
                        val sections = listOf(
                            "TASKS" to "Tasks & Daily To-Do",
                            "EXPENSES" to "Expense Tracker",
                            "LOANS" to "Borrow / Lend",
                            "BOOKINGS" to "Travel & Tickets",
                            "HEALTH" to "Weight & Health",
                            "GOALS" to "Goal Planner",
                            "DOCUMENTS" to "Document Vault"
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(sections) { (key, label) ->
                                CategoryChip(
                                    text = label,
                                    isSelected = chosenSection.uppercase() == key,
                                    onClick = { viewModel.setChosenSection(key) }
                                )
                            }
                        }

                        // Structured Extraction Details
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Extracted: ${res.title}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = res.summary,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Specific field highlights
                                if (res.expense != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "💰 Amount: ₹${res.expense.amount.toInt()} • Category: ${res.expense.category} • Date: ${res.expense.date}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandPrimary
                                    )
                                }
                                if (res.loan != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "🤝 Person: ${res.loan.person} • ₹${res.loan.amount.toInt()} (${res.loan.type}) • Due: ${res.loan.dueDate}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AccentWarning
                                    )
                                }
                                if (res.booking != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "🚆 ${res.booking.title} • PNR: ${res.booking.pnr} • Date: ${res.booking.date} at ${res.booking.time}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF8B5CF6)
                                    )
                                }
                                if (res.health != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "⚖️ Weight: ${res.health.weight} ${res.health.unit} • Date: ${res.health.date}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AccentSuccess
                                    )
                                }
                                if (res.tasks.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    res.tasks.take(3).forEach { t ->
                                        Text(
                                            text = "• ${t.title} (${t.priority} • ${t.timeOfDay})",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // Confirm & Organize Button
                        Button(
                            onClick = {
                                viewModel.confirmAndSaveAnalysis()
                                // Navigate to corresponding tab
                                when (chosenSection.uppercase()) {
                                    "TASKS" -> onNavigateToTab(1)
                                    "EXPENSES", "LOANS" -> onNavigateToTab(3)
                                    else -> onNavigateToTab(4)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text(
                                text = "Confirm & Organize to $chosenSection",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Text Input / Paste Notes Dialog
    if (showTextInputDialog) {
        var inputNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showTextInputDialog = false },
            title = { Text("Paste Any Note or Plan", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Paste raw text, bills, travel PNR, or to-do lists. LifeOS will categorize it automatically.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputNotes,
                        onValueChange = { inputNotes = it },
                        placeholder = { Text("e.g. Swiggy Dinner ₹420 on 10 Sept\nor\nRahul borrowed ₹5000 due next week") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputNotes.isNotBlank()) {
                            viewModel.analyzeText(inputNotes)
                            showTextInputDialog = false
                        }
                    }
                ) {
                    Text("Analyze with AI")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTextInputDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Camera Permission Rationale / Fallback Dialog
    if (showCameraPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showCameraPermissionRationale = false },
            title = { Text("Camera Access", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Camera permission is required to capture photos of bills, receipts, or notes. You can also pick photos directly from your Gallery or use the quick demo presets below!",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCameraPermissionRationale = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCameraPermissionRationale = false }) {
                    Text("Use Presets / Gallery")
                }
            }
        )
    }
}

@Composable
fun CaptureActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = BrandPrimary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
