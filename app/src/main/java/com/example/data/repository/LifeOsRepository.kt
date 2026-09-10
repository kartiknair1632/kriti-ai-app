package com.example.data.repository

import com.example.data.dao.LifeOsDao
import com.example.data.model.BookingEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.GoalEntity
import com.example.data.model.HealthEntity
import com.example.data.model.LoanEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LifeOsRepository(private val dao: LifeOsDao) {

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val pendingTasks: Flow<List<TaskEntity>> = dao.getPendingTasks()

    suspend fun insertTask(task: TaskEntity) = dao.insertTask(task)
    suspend fun insertTasks(tasks: List<TaskEntity>) = dao.insertTasks(tasks)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)
    suspend fun toggleTaskStatus(task: TaskEntity) {
        val newStatus = if (task.status == "COMPLETED") "PENDING" else "COMPLETED"
        dao.updateTaskStatus(task.id, newStatus)
    }

    // Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    suspend fun insertExpense(expense: ExpenseEntity) = dao.insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseEntity) = dao.updateExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = dao.deleteExpense(expense)

    // Loans
    val allLoans: Flow<List<LoanEntity>> = dao.getAllLoans()
    suspend fun insertLoan(loan: LoanEntity) = dao.insertLoan(loan)
    suspend fun updateLoan(loan: LoanEntity) = dao.updateLoan(loan)
    suspend fun deleteLoan(loan: LoanEntity) = dao.deleteLoan(loan)
    suspend fun toggleLoanStatus(loan: LoanEntity) {
        val newStatus = if (loan.status == "SETTLED") "PENDING" else "SETTLED"
        dao.updateLoanStatus(loan.id, newStatus)
    }

    // Bookings
    val allBookings: Flow<List<BookingEntity>> = dao.getAllBookings()
    suspend fun insertBooking(booking: BookingEntity) = dao.insertBooking(booking)
    suspend fun deleteBooking(booking: BookingEntity) = dao.deleteBooking(booking)

    // Health
    val allHealthRecords: Flow<List<HealthEntity>> = dao.getAllHealthRecords()
    suspend fun insertHealthRecord(record: HealthEntity) = dao.insertHealthRecord(record)
    suspend fun deleteHealthRecord(record: HealthEntity) = dao.deleteHealthRecord(record)

    // Goals
    val allGoals: Flow<List<GoalEntity>> = dao.getAllGoals()
    suspend fun insertGoal(goal: GoalEntity) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = dao.deleteGoal(goal)

    // Documents
    val allDocuments: Flow<List<DocumentEntity>> = dao.getAllDocuments()
    fun searchDocuments(query: String): Flow<List<DocumentEntity>> = dao.searchDocuments(query)
    suspend fun insertDocument(doc: DocumentEntity) = dao.insertDocument(doc)
    suspend fun deleteDocument(doc: DocumentEntity) = dao.deleteDocument(doc)

    // Chat
    val allChatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
    suspend fun insertChatMessage(msg: ChatMessageEntity) = dao.insertChatMessage(msg)
    suspend fun clearChatHistory() = dao.clearChatHistory()

    // Pre-populate initial data if empty so user has a rich experience immediately
    suspend fun seedSampleDataIfEmpty() {
        val existingTasks = allTasks.first()
        if (existingTasks.isNotEmpty()) return

        // 1. Initial Tasks
        dao.insertTasks(
            listOf(
                TaskEntity(
                    title = "Morning 30-min brisk walk & workout",
                    priority = "HIGH",
                    status = "COMPLETED",
                    deadline = "Today 7:30 AM",
                    timeOfDay = "MORNING",
                    isRecurring = true,
                    category = "Health"
                ),
                TaskEntity(
                    title = "Read 20 pages of Deep Work",
                    priority = "MEDIUM",
                    status = "COMPLETED",
                    deadline = "Today 8:30 AM",
                    timeOfDay = "MORNING",
                    isRecurring = true,
                    category = "Learning"
                ),
                TaskEntity(
                    title = "Pay electricity & utility bill",
                    priority = "HIGH",
                    status = "PENDING",
                    deadline = "Today 5:00 PM",
                    timeOfDay = "AFTERNOON",
                    isRecurring = false,
                    category = "Bills"
                ),
                TaskEntity(
                    title = "Q3 Product roadmap client sync",
                    priority = "MEDIUM",
                    status = "PENDING",
                    deadline = "Today 3:30 PM",
                    timeOfDay = "AFTERNOON",
                    isRecurring = false,
                    category = "Work"
                ),
                TaskEntity(
                    title = "Review weekly personal expenses & save 15%",
                    priority = "LOW",
                    status = "PENDING",
                    deadline = "Today 9:00 PM",
                    timeOfDay = "NIGHT",
                    isRecurring = true,
                    category = "Finance"
                )
            )
        )

        // 2. Initial Expenses
        dao.insertExpense(
            ExpenseEntity(
                title = "Swiggy Dinner with Team",
                amount = 420.0,
                category = "Food",
                date = "10 Sept 2026",
                notes = "Extracted from restaurant invoice"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                title = "Metro Smart Card Recharge",
                amount = 500.0,
                category = "Travel",
                date = "08 Sept 2026",
                notes = "Auto-scanned receipt"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                title = "Amazon Books & Notebooks",
                amount = 1250.0,
                category = "Shopping",
                date = "05 Sept 2026",
                notes = "Amazon invoice order #402"
            )
        )
        dao.insertExpense(
            ExpenseEntity(
                title = "Airtel Fiber Broadband",
                amount = 999.0,
                category = "Bills",
                date = "01 Sept 2026",
                notes = "Monthly internet payment"
            )
        )

        // 3. Initial Loan (Borrow / Lending)
        dao.insertLoan(
            LoanEntity(
                person = "Rahul Verma",
                amount = 5000.0,
                type = "LENT",
                status = "PENDING",
                date = "2 Sept 2026",
                dueDate = "15 Sept 2026",
                note = "Rahul borrowed for car repair"
            )
        )
        dao.insertLoan(
            LoanEntity(
                person = "Amit Sharma",
                amount = 3000.0,
                type = "LENT",
                status = "PENDING",
                date = "20 Aug 2026",
                dueDate = "10 Sept 2026",
                note = "Concert tickets advance"
            )
        )

        // 4. Initial Booking
        dao.insertBooking(
            BookingEntity(
                type = "TRAIN",
                title = "Rajdhani Express (12952)",
                date = "20 Sept 2026",
                time = "7:30 PM",
                pnr = "2849-201948",
                origin = "New Delhi (NDLS)",
                destination = "Mumbai Central (MMCT)",
                details = "Coach B3, Berth 42 (Confirmed)"
            )
        )

        // 5. Initial Health Record
        dao.insertHealthRecord(
            HealthEntity(
                weight = 68.5,
                unit = "kg",
                date = "10 Sept 2026",
                measurements = "Waist: 31 in, BP: 118/76",
                notes = "Post-workout morning weigh-in"
            )
        )
        dao.insertHealthRecord(
            HealthEntity(
                weight = 69.2,
                unit = "kg",
                date = "03 Sept 2026",
                measurements = "Waist: 31.5 in",
                notes = "Start of month check"
            )
        )
        dao.insertHealthRecord(
            HealthEntity(
                weight = 70.0,
                unit = "kg",
                date = "20 Aug 2026",
                measurements = "Waist: 32 in",
                notes = "Initial baseline"
            )
        )

        // 6. Initial Goal
        dao.insertGoal(
            GoalEntity(
                title = "Master Modern Python & AI Pipelines",
                category = "Learning",
                deadline = "31 Oct 2026",
                progress = 65,
                subtasks = "Complete Python data structures:done|30 Days LeetCode Practice:done|Build Multimodal LLM App:pending|Deploy to Cloud:pending"
            )
        )
        dao.insertGoal(
            GoalEntity(
                title = "Target Weight 66.0 kg & 10k Daily Steps",
                category = "Fitness",
                deadline = "15 Nov 2026",
                progress = 50,
                subtasks = "Consistent morning workouts:done|Calorie tracking:done|Reach 67 kg:pending|Reach 66 kg:pending"
            )
        )

        // 7. Initial Document in Vault
        dao.insertDocument(
            DocumentEntity(
                title = "IRCTC E-Ticket Confirmation",
                category = "Tickets",
                uploadedDate = "05 Sept 2026",
                aiAnalysis = "Confirmed Train Ticket for Rajdhani Express on 20 Sept. PNR 2849-201948, NDLS to MMCT."
            )
        )
        dao.insertDocument(
            DocumentEntity(
                title = "Swiggy Dinner Tax Invoice #SW-9821",
                category = "Receipts",
                uploadedDate = "10 Sept 2026",
                aiAnalysis = "Restaurant billing for ₹420, payment completed via UPI. Categorized to Food expenses."
            )
        )

        // 8. Initial Welcome Chat Message from AI
        dao.insertChatMessage(
            ChatMessageEntity(
                sender = "AI",
                message = "Hello! I'm your LifeOS AI Assistant. I can organize anything you scan—receipts, travel tickets, handwritten plans, loans, or fitness records. You can also ask me anything about your schedule, expenses, or trips!"
            )
        )
    }
}
