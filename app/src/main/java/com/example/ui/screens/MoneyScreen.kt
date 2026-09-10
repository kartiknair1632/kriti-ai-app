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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpenseEntity
import com.example.data.model.LoanEntity
import com.example.ui.components.AiInsightCard
import com.example.ui.components.AppleCard
import com.example.ui.components.CategoryChip
import com.example.ui.components.ProgressBarWithLabel
import com.example.ui.theme.AccentDanger
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.AccentWarning
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.viewmodel.LifeOsViewModel

@Composable
fun MoneyScreen(viewModel: LifeOsViewModel) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val loans by viewModel.loans.collectAsStateWithLifecycle()

    var selectedSubTab by remember { mutableIntStateOf(0) } // 0 = Expenses, 1 = Borrow / Lend
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showAddLoanDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedSubTab == 0) showAddExpenseDialog = true else showAddLoanDialog = true
                },
                containerColor = BrandPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Money & Finances",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Track expenses, category budgets, and borrow/lend records",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Segmented Tab Switcher (Expenses vs Borrow / Lend)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedSubTab = 0 },
                            color = if (selectedSubTab == 0) MaterialTheme.colorScheme.surface else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Personal Expenses",
                                fontSize = 13.sp,
                                fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedSubTab == 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .align(Alignment.CenterVertically),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedSubTab = 1 },
                            color = if (selectedSubTab == 1) MaterialTheme.colorScheme.surface else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Borrow / Lend",
                                fontSize = 13.sp,
                                fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedSubTab == 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .align(Alignment.CenterVertically),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // TAB 0: EXPENSES
            if (selectedSubTab == 0) {
                val totalSpent = expenses.sumOf { it.amount }
                val budgetLimit = 15000.0
                val budgetProgress = (totalSpent / budgetLimit).toFloat()

                // Monthly Expenses Summary Card
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
                                        text = "SEPTEMBER SPENDING",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandPrimary,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹${totalSpent.toInt()}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Surface(
                                    color = if (budgetProgress > 0.85f) Color(0xFFFFE4E6) else Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Budget: ₹${budgetLimit.toInt()}",
                                        color = if (budgetProgress > 0.85f) AccentDanger else AccentSuccess,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            LinearProgressIndicator(
                                progress = { budgetProgress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (budgetProgress > 0.85f) AccentDanger else BrandPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "₹${(budgetLimit - totalSpent).coerceAtLeast(0.0).toInt()} remaining under monthly cap",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // AI Insights Card
                item {
                    AiInsightCard(
                        insight = "You spent 35% more on food this month compared to last month. Your bills are currently 15% lower than average."
                    )
                }

                // Category Breakdown Progress Bars
                item {
                    AppleCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Spending by Category",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            val catMap = expenses.groupBy { it.category }
                                .mapValues { entry -> entry.value.sumOf { it.amount } }

                            val foodSum = catMap["Food"] ?: 420.0
                            val travelSum = catMap["Travel"] ?: 500.0
                            val shopSum = catMap["Shopping"] ?: 1250.0
                            val billsSum = catMap["Bills"] ?: 999.0

                            ProgressBarWithLabel("Food", "₹${foodSum.toInt()}", (foodSum / 5000f).toFloat(), Color(0xFFF59E0B))
                            ProgressBarWithLabel("Shopping", "₹${shopSum.toInt()}", (shopSum / 5000f).toFloat(), Color(0xFF3B82F6))
                            ProgressBarWithLabel("Bills", "₹${billsSum.toInt()}", (billsSum / 5000f).toFloat(), Color(0xFF10B981))
                            ProgressBarWithLabel("Travel", "₹${travelSum.toInt()}", (travelSum / 3000f).toFloat(), Color(0xFF8B5CF6))
                        }
                    }
                }

                // Recent Expenses List
                item {
                    Text(
                        text = "Recent Transactions (${expenses.size})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(expenses, key = { it.id }) { exp ->
                    ExpenseRowItem(
                        expense = exp,
                        onDelete = { viewModel.deleteExpense(exp) }
                    )
                }
            }

            // TAB 1: BORROW / LEND
            if (selectedSubTab == 1) {
                val lentPending = loans.filter { it.type == "LENT" && it.status == "PENDING" }.sumOf { it.amount }
                val borrowedPending = loans.filter { it.type == "BORROWED" && it.status == "PENDING" }.sumOf { it.amount }

                // Money Given vs Money Received Summary
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "MONEY GIVEN (LENT)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857),
                                    letterSpacing = 0.8.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${lentPending.toInt()}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = "To receive back",
                                    fontSize = 11.sp,
                                    color = Color(0xFF047857)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFFFFBEB),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "MONEY BORROWED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    letterSpacing = 0.8.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${borrowedPending.toInt()}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "To pay back",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }

                // Smart Reminder Alert Card
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Reminder",
                                tint = BrandPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "SMART REMINDER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary
                                )
                                Text(
                                    text = "Rahul borrowed ₹5,000 on 2 September (8 days ago). Due date is 15 September.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Loan Records List
                item {
                    Text(
                        text = "Active Loan Records (${loans.size})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(loans, key = { it.id }) { loan ->
                    LoanRowItem(
                        loan = loan,
                        onToggle = { viewModel.toggleLoan(loan) },
                        onDelete = { viewModel.deleteLoan(loan) }
                    )
                }
            }
        }
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        var title by remember { mutableStateOf("") }
        var amountStr by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Food") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddExpenseDialog = false },
            title = { Text("Log Personal Expense", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Vendor / Expense title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("Food", "Travel", "Shopping", "Bills", "Health", "Other")) { cat ->
                            CategoryChip(cat, category == cat, { category = cat })
                        }
                    }
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountStr.toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && amt > 0.0) {
                            viewModel.addExpense(title, amt, category, "10 Sept 2026", notes)
                            showAddExpenseDialog = false
                        }
                    }
                ) {
                    Text("Save Expense")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExpenseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Loan Dialog
    if (showAddLoanDialog) {
        var person by remember { mutableStateOf("") }
        var amountStr by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("LENT") }
        var dueDate by remember { mutableStateOf("15 Sept 2026") }
        var note by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddLoanDialog = false },
            title = { Text("Record Borrow / Loan", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = person,
                        onValueChange = { person = it },
                        label = { Text("Person name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Type", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CategoryChip("Money Given (Lent)", type == "LENT", { type = "LENT" })
                        CategoryChip("Money Received", type == "BORROWED", { type = "BORROWED" })
                    }
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Context / reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountStr.toDoubleOrNull() ?: 0.0
                        if (person.isNotBlank() && amt > 0.0) {
                            viewModel.addLoan(person, amt, type, dueDate, note)
                            showAddLoanDialog = false
                        }
                    }
                ) {
                    Text("Record Loan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLoanDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExpenseRowItem(
    expense: ExpenseEntity,
    onDelete: () -> Unit
) {
    AppleCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (expense.category) {
                                "Food" -> Color(0xFFFEF3C7)
                                "Travel" -> Color(0xFFEDE9FE)
                                "Shopping" -> Color(0xFFDBEAFE)
                                else -> Color(0xFFD1FAE5)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (expense.category) {
                            "Food" -> Icons.Default.Fastfood
                            "Travel" -> Icons.Default.Flight
                            "Shopping" -> Icons.Default.ShoppingBag
                            else -> Icons.Default.ReceiptLong
                        },
                        contentDescription = expense.category,
                        tint = when (expense.category) {
                            "Food" -> Color(0xFFD97706)
                            "Travel" -> Color(0xFF7C3AED)
                            "Shopping" -> Color(0xFF2563EB)
                            else -> Color(0xFF059669)
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = expense.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${expense.category} • ${expense.date}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "₹${expense.amount.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDelete) {
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

@Composable
fun LoanRowItem(
    loan: LoanEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val isSettled = loan.status == "SETTLED"

    AppleCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = loan.person,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (loan.type == "LENT") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (loan.type == "LENT") "GIVEN" else "BORROWED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (loan.type == "LENT") Color(0xFF047857) else Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Due: ${loan.dueDate} ${if (loan.note != null) "• ${loan.note}" else ""}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${loan.amount.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggle() },
                        color = if (isSettled) Color(0xFFE2E8F0) else Color(0xFFDBEAFE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isSettled) "✓ Settled" else "Pending",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSettled) Color(0xFF64748B) else Color(0xFF1D4ED8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                IconButton(onClick = onDelete) {
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
