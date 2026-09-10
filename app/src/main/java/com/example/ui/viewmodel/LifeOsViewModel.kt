package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAnalysisResult
import com.example.ai.LifeOsAiService
import com.example.data.db.LifeOsDatabase
import com.example.data.model.BookingEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.GoalEntity
import com.example.data.model.HealthEntity
import com.example.data.model.LoanEntity
import com.example.data.model.TaskEntity
import com.example.data.repository.LifeOsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PresetSample(val label: String, val iconName: String, val rawText: String) {
    SWIGGY_RECEIPT(
        "Swiggy Dinner ₹420",
        "receipt",
        "Swiggy Delivery\nOrder #SW-9821\nSpice Kitchen\n1x Paneer Butter Masala ₹280\n2x Butter Naan ₹100\nDelivery & Taxes ₹40\nTotal Paid: ₹420\n10 Sept 2026\nPayment: UPI / Paid"
    ),
    IRCTC_TICKET(
        "IRCTC Train Ticket",
        "train",
        "INDIAN RAILWAYS E-TICKET\nPNR: 2849-201948\nTrain: 12952 Rajdhani Express\nDate: 20 Sept 2026\nDeparture: 19:30 (7:30 PM)\nFrom: New Delhi (NDLS)\nTo: Mumbai Central (MMCT)\nClass: 3A (Coach B3, Berth 42)\nStatus: CONFIRMED"
    ),
    RAHUL_LOAN(
        "Rahul Borrowed ₹5,000",
        "handshake",
        "WhatsApp Chat Note:\nRahul borrowed ₹5000 on 2 September 2026 for urgent car tyre replacement.\nPromise to return by: 15 September 2026.\nStatus: Pending repayment."
    ),
    DAILY_TASKS_PLAN(
        "Handwritten Daily Plan",
        "checklist",
        "Daily Action Plan - Sept 10:\n- Morning workout & 30 min run (7 AM)\n- Buy groceries: milk, eggs, oats\n- Pay electricity bill online (due today!)\n- Client sync call at 3:30 PM\n- Gym at 7 PM"
    ),
    WEIGHT_SCALE(
        "Weight Scale 68.5 kg",
        "scale",
        "Smart Scale Log:\nDate: 10 Sept 2026 07:15 AM\nWeight: 68.5 kg\nBody Fat: 18.2%\nTarget Goal: 66.0 kg\nNotes: Fasted morning check."
    ),
    PYTHON_GOAL(
        "Vision: Learn Python",
        "target",
        "Q4 Learning Goal:\nGoal: Master Modern Python & AI Pipelines\nCategory: Career & Learning\nTarget Deadline: 31 Dec 2026\nMilestones:\n- Complete Python core basics\n- Practice coding 30 days\n- Build AI personal assistant"
    )
}

class LifeOsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LifeOsRepository
    private val aiService = LifeOsAiService()

    init {
        val database = LifeOsDatabase.getDatabase(application)
        repository = LifeOsRepository(database.lifeOsDao())
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    // --- State Streams ---
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<LoanEntity>> = repository.allLoans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthRecords: StateFlow<List<HealthEntity>> = repository.allHealthRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documents: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- AI Scan & Organize State ---
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    private val _currentBitmap = MutableStateFlow<Bitmap?>(null)
    val currentBitmap = _currentBitmap.asStateFlow()

    private val _currentScannedText = MutableStateFlow("")
    val currentScannedText = _currentScannedText.asStateFlow()

    private val _analysisResult = MutableStateFlow<AiAnalysisResult?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    private val _chosenSection = MutableStateFlow("TASKS")
    val chosenSection = _chosenSection.asStateFlow()

    private val _statusNotification = MutableStateFlow<String?>(null)
    val statusNotification = _statusNotification.asStateFlow()

    private val _isAiReplying = MutableStateFlow(false)
    val isAiReplying = _isAiReplying.asStateFlow()

    fun clearStatusNotification() {
        _statusNotification.value = null
    }

    fun setChosenSection(section: String) {
        _chosenSection.value = section
    }

    fun resetScanState() {
        _currentBitmap.value = null
        _currentScannedText.value = ""
        _analysisResult.value = null
        _isAnalyzing.value = false
    }

    fun analyzeImage(bitmap: Bitmap) {
        _currentBitmap.value = bitmap
        _currentScannedText.value = "[Photo Scanned: ${bitmap.width}x${bitmap.height} px]"
        triggerAiAnalysis(bitmap, null)
    }

    fun analyzeText(text: String) {
        _currentBitmap.value = null
        _currentScannedText.value = text
        triggerAiAnalysis(null, text)
    }

    fun loadPreset(preset: PresetSample) {
        _currentBitmap.value = null
        _currentScannedText.value = preset.rawText
        triggerAiAnalysis(null, preset.rawText)
    }

    private fun triggerAiAnalysis(bitmap: Bitmap?, text: String?) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val expList = expenses.value.joinToString("; ") { "${it.title}: ₹${it.amount.toInt()}" }
                val taskList = tasks.value.joinToString("; ") { it.title }

                val result = aiService.analyzeContent(bitmap, text, expList, taskList)
                _analysisResult.value = result
                _chosenSection.value = result.suggestedSection
            } catch (e: Exception) {
                _statusNotification.value = "Analysis error: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun confirmAndSaveAnalysis(overrideSection: String? = null) {
        val result = _analysisResult.value ?: return
        val section = (overrideSection ?: _chosenSection.value).uppercase()

        viewModelScope.launch {
            when (section) {
                "TASKS" -> {
                    val taskEntities = if (result.tasks.isNotEmpty()) {
                        result.tasks.map {
                            TaskEntity(
                                title = it.title,
                                priority = it.priority,
                                status = "PENDING",
                                deadline = it.deadline,
                                timeOfDay = it.timeOfDay,
                                category = "AI Organized"
                            )
                        }
                    } else {
                        listOf(
                            TaskEntity(
                                title = result.title,
                                priority = "MEDIUM",
                                status = "PENDING",
                                deadline = "Today",
                                category = "General"
                            )
                        )
                    }
                    repository.insertTasks(taskEntities)
                    _statusNotification.value = "Saved ${taskEntities.size} task(s) to Daily To-Do!"
                }
                "EXPENSES" -> {
                    val exp = result.expense ?: com.example.ai.ExtractedExpense(
                        title = result.title,
                        amount = 100.0,
                        category = "Other",
                        date = "Today"
                    )
                    repository.insertExpense(
                        ExpenseEntity(
                            title = exp.title,
                            amount = exp.amount,
                            category = exp.category,
                            date = exp.date,
                            notes = result.summary
                        )
                    )
                    _statusNotification.value = "Saved ₹${exp.amount.toInt()} expense to Money Tracker!"
                }
                "LOANS" -> {
                    val l = result.loan ?: com.example.ai.ExtractedLoan(
                        person = "Friend",
                        amount = 1000.0,
                        type = "LENT",
                        dueDate = "Next Week"
                    )
                    repository.insertLoan(
                        LoanEntity(
                            person = l.person,
                            amount = l.amount,
                            type = l.type,
                            status = "PENDING",
                            date = "Today",
                            dueDate = l.dueDate,
                            note = l.note.ifEmpty { result.summary }
                        )
                    )
                    _statusNotification.value = "Recorded loan with ${l.person} for ₹${l.amount.toInt()}!"
                }
                "BOOKINGS" -> {
                    val b = result.booking ?: com.example.ai.ExtractedBooking(
                        type = "TRAIN",
                        title = result.title,
                        date = "Upcoming",
                        time = "10:00 AM",
                        pnr = "AUTO-EXTRACTED"
                    )
                    repository.insertBooking(
                        BookingEntity(
                            type = b.type,
                            title = b.title,
                            date = b.date,
                            time = b.time,
                            pnr = b.pnr,
                            origin = b.origin,
                            destination = b.destination,
                            details = b.details
                        )
                    )
                    _statusNotification.value = "Added travel booking '${b.title}' to Upcoming Trips!"
                }
                "HEALTH" -> {
                    val h = result.health ?: com.example.ai.ExtractedHealth(
                        weight = 68.5,
                        unit = "kg",
                        date = "Today"
                    )
                    repository.insertHealthRecord(
                        HealthEntity(
                            weight = h.weight,
                            unit = h.unit,
                            date = h.date,
                            measurements = h.measurements,
                            notes = h.notes.ifEmpty { result.summary }
                        )
                    )
                    _statusNotification.value = "Logged weight ${h.weight} kg in Health Tracker!"
                }
                "GOALS" -> {
                    val g = result.goal ?: com.example.ai.ExtractedGoal(
                        title = result.title,
                        category = "Learning",
                        deadline = "Target 2026",
                        subtasks = listOf("Start milestone", "Continue practice", "Achieve target")
                    )
                    val subtaskStr = g.subtasks.joinToString("|") { "$it:pending" }
                    repository.insertGoal(
                        GoalEntity(
                            title = g.title,
                            category = g.category,
                            deadline = g.deadline,
                            progress = 0,
                            subtasks = subtaskStr
                        )
                    )
                    _statusNotification.value = "Created new goal '${g.title}' with milestones!"
                }
                else -> {
                    // Save to Document Vault
                    repository.insertDocument(
                        DocumentEntity(
                            title = result.title,
                            category = "Scanned Documents",
                            uploadedDate = "Today",
                            aiAnalysis = result.summary + "\n" + result.rawText
                        )
                    )
                    _statusNotification.value = "Saved document to Vault!"
                }
            }

            // Always preserve in Document Vault as well
            repository.insertDocument(
                DocumentEntity(
                    title = result.title,
                    category = when (section) {
                        "EXPENSES" -> "Receipts"
                        "BOOKINGS" -> "Tickets"
                        "HEALTH" -> "Health Records"
                        "LOANS" -> "Financial Records"
                        else -> "Personal Notes"
                    },
                    uploadedDate = "10 Sept 2026",
                    aiAnalysis = "${result.summary}\n[Auto-Organized to $section]"
                )
            )

            resetScanState()
        }
    }

    // --- Task Actions ---
    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch { repository.toggleTaskStatus(task) }
    }

    fun addTask(title: String, priority: String, timeOfDay: String, deadline: String, category: String) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title,
                    priority = priority,
                    status = "PENDING",
                    deadline = deadline,
                    timeOfDay = timeOfDay,
                    category = category
                )
            )
            _statusNotification.value = "Task created!"
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    // --- Expense Actions ---
    fun addExpense(title: String, amount: Double, category: String, date: String, notes: String?) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    notes = notes
                )
            )
            _statusNotification.value = "Expense logged!"
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    // --- Loan Actions ---
    fun toggleLoan(loan: LoanEntity) {
        viewModelScope.launch { repository.toggleLoanStatus(loan) }
    }

    fun addLoan(person: String, amount: Double, type: String, dueDate: String, note: String?) {
        viewModelScope.launch {
            repository.insertLoan(
                LoanEntity(
                    person = person,
                    amount = amount,
                    type = type,
                    status = "PENDING",
                    date = "Today",
                    dueDate = dueDate,
                    note = note
                )
            )
            _statusNotification.value = "Loan record created!"
        }
    }

    fun deleteLoan(loan: LoanEntity) {
        viewModelScope.launch { repository.deleteLoan(loan) }
    }

    // --- Booking Actions ---
    fun addBooking(
        type: String,
        title: String,
        date: String,
        time: String,
        pnr: String,
        origin: String,
        destination: String,
        details: String
    ) {
        viewModelScope.launch {
            repository.insertBooking(
                BookingEntity(
                    type = type,
                    title = title,
                    date = date,
                    time = time,
                    pnr = pnr,
                    origin = origin,
                    destination = destination,
                    details = details
                )
            )
            _statusNotification.value = "Booking added!"
        }
    }

    fun deleteBooking(booking: BookingEntity) {
        viewModelScope.launch { repository.deleteBooking(booking) }
    }

    // --- Health Actions ---
    fun addHealthRecord(weight: Double, notes: String?) {
        viewModelScope.launch {
            repository.insertHealthRecord(
                HealthEntity(
                    weight = weight,
                    unit = "kg",
                    date = "10 Sept 2026",
                    notes = notes
                )
            )
            _statusNotification.value = "Weight logged!"
        }
    }

    fun deleteHealthRecord(record: HealthEntity) {
        viewModelScope.launch { repository.deleteHealthRecord(record) }
    }

    // --- Goal Actions ---
    fun toggleGoalSubtask(goal: GoalEntity, subtaskIndex: Int) {
        viewModelScope.launch {
            val items = goal.subtasks.split("|").filter { it.isNotBlank() }.toMutableList()
            if (subtaskIndex in items.indices) {
                val parts = items[subtaskIndex].split(":")
                val text = parts[0]
                val currentStatus = parts.getOrNull(1) ?: "pending"
                val newStatus = if (currentStatus == "done") "pending" else "done"
                items[subtaskIndex] = "$text:$newStatus"

                val doneCount = items.count { it.endsWith(":done") }
                val newProgress = if (items.isNotEmpty()) ((doneCount.toFloat() / items.size) * 100).toInt() else 0
                val updatedGoal = goal.copy(
                    subtasks = items.joinToString("|"),
                    progress = newProgress
                )
                repository.updateGoal(updatedGoal)
            }
        }
    }

    fun addGoal(title: String, category: String, deadline: String, subtasks: List<String>) {
        viewModelScope.launch {
            val subtaskStr = subtasks.joinToString("|") { "$it:pending" }
            repository.insertGoal(
                GoalEntity(
                    title = title,
                    category = category,
                    deadline = deadline,
                    progress = 0,
                    subtasks = subtaskStr
                )
            )
            _statusNotification.value = "Goal created!"
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }

    // --- Document Vault Actions ---
    fun deleteDocument(doc: DocumentEntity) {
        viewModelScope.launch { repository.deleteDocument(doc) }
    }

    // --- AI Chat Actions ---
    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            // 1. Insert user message
            repository.insertChatMessage(
                ChatMessageEntity(
                    sender = "USER",
                    message = userText,
                    timestamp = System.currentTimeMillis()
                )
            )

            _isAiReplying.value = true
            try {
                // Build complete database snapshot for context
                val snapshot = buildDataSnapshot()
                val aiReply = aiService.chatWithAssistant(userText, snapshot)
                repository.insertChatMessage(
                    ChatMessageEntity(
                        sender = "AI",
                        message = aiReply,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                repository.insertChatMessage(
                    ChatMessageEntity(
                        sender = "AI",
                        message = "I encountered an issue retrieving that information. Please try asking again!",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } finally {
                _isAiReplying.value = false
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch { repository.clearChatHistory() }
    }

    private fun buildDataSnapshot(): String {
        val tList = tasks.value.joinToString("\n") { "• [${it.status}] ${it.title} (${it.priority} • ${it.timeOfDay} • ${it.deadline})" }
        val eList = expenses.value.joinToString("\n") { "• ${it.title}: ₹${it.amount.toInt()} [${it.category}] on ${it.date}" }
        val lList = loans.value.joinToString("\n") { "• ${it.person}: ₹${it.amount.toInt()} (${it.type} • Status: ${it.status} • Due: ${it.dueDate})" }
        val bList = bookings.value.joinToString("\n") { "• ${it.title} (${it.type}): ${it.date} at ${it.time}, PNR ${it.pnr}, Route: ${it.origin} -> ${it.destination}" }
        val hList = healthRecords.value.joinToString("\n") { "• ${it.date}: ${it.weight} kg (${it.measurements ?: ""})" }
        val gList = goals.value.joinToString("\n") { "• ${it.title}: ${it.progress}% [${it.category}] Deadline: ${it.deadline}" }

        return """
            TASKS:
            $tList

            EXPENSES:
            $eList

            LOANS (BORROW / LEND):
            $lList

            UPCOMING TRAVEL & BOOKINGS:
            $bList

            HEALTH & WEIGHT:
            $hList

            GOALS:
            $gList
        """.trimIndent()
    }

    // Export user data as structured summary text
    fun getFullExportText(): String {
        return """
            =========================================
            Kriti AI - Personal Life Export
            Date: 10 Sept 2026
            =========================================

            ${buildDataSnapshot()}

            =========================================
            Generated by Kriti AI Personal Organizer
        """.trimIndent()
    }
}
