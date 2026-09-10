package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookingEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.GoalEntity
import com.example.data.model.HealthEntity
import com.example.data.model.LoanEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeOsDao {

    // --- Tasks ---
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = 'PENDING' ORDER BY priority = 'HIGH' DESC, priority = 'MEDIUM' DESC, createdAt DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: String)

    // --- Expenses ---
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    suspend fun searchExpenses(query: String): List<ExpenseEntity>

    // --- Loans (Borrow / Lend) ---
    @Query("SELECT * FROM loans ORDER BY createdAt DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)

    @Query("UPDATE loans SET status = :status WHERE id = :id")
    suspend fun updateLoanStatus(id: Long, status: String)

    // --- Bookings / Travel ---
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    // --- Health Records ---
    @Query("SELECT * FROM health_records ORDER BY createdAt DESC")
    fun getAllHealthRecords(): Flow<List<HealthEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthEntity): Long

    @Delete
    suspend fun deleteHealthRecord(record: HealthEntity)

    // --- Goals ---
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    // --- Documents (Vault) ---
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity): Long

    @Delete
    suspend fun deleteDocument(doc: DocumentEntity)

    @Query("SELECT * FROM documents WHERE title LIKE '%' || :query || '%' OR aiAnalysis LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchDocuments(query: String): Flow<List<DocumentEntity>>

    // --- AI Chat History ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}
