package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.LifeOsDao
import com.example.data.model.BookingEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.GoalEntity
import com.example.data.model.HealthEntity
import com.example.data.model.LoanEntity
import com.example.data.model.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        ExpenseEntity::class,
        LoanEntity::class,
        BookingEntity::class,
        HealthEntity::class,
        GoalEntity::class,
        DocumentEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeOsDatabase : RoomDatabase() {
    abstract fun lifeOsDao(): LifeOsDao

    companion object {
        @Volatile
        private var INSTANCE: LifeOsDatabase? = null

        fun getDatabase(context: Context): LifeOsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOsDatabase::class.java,
                    "lifeos_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
