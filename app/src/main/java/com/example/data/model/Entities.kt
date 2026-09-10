package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val status: String = "PENDING",   // PENDING, COMPLETED
    val deadline: String = "",
    val timeOfDay: String = "MORNING", // MORNING, AFTERNOON, NIGHT
    val isRecurring: Boolean = false,
    val category: String = "General",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String = "Food", // Food, Travel, Shopping, Bills, Health, Entertainment, Other
    val date: String = "",
    val sourceImage: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val person: String,
    val amount: Double,
    val type: String = "LENT", // LENT (Money Given), BORROWED (Money Received)
    val status: String = "PENDING", // PENDING, SETTLED
    val date: String = "",
    val dueDate: String = "",
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String = "TRAIN", // TRAIN, FLIGHT, BUS, HOTEL, MOVIE
    val title: String,
    val date: String = "",
    val time: String = "",
    val pnr: String = "",
    val origin: String = "",
    val destination: String = "",
    val details: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "health_records")
data class HealthEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weight: Double,
    val unit: String = "kg",
    val date: String = "",
    val measurements: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Career", // Career, Fitness, Finance, Learning, Personal
    val deadline: String = "",
    val progress: Int = 0, // 0 to 100
    val subtasks: String = "", // e.g. "Step 1:done|Step 2:pending"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Receipts", // Bills, Receipts, Tickets, Personal Notes, Financial Records, Health Records
    val uploadedDate: String = "",
    val imageUrl: String? = null,
    val aiAnalysis: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String = "AI", // USER, AI
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
