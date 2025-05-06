package com.example.budgettrackerapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budgettrackerapp.data.dao.BudgetGoalDao
import com.example.budgettrackerapp.data.dao.CategoryDao
import com.example.budgettrackerapp.data.dao.ExpenseDao
import com.example.budgettrackerapp.data.dao.UserDao
import com.example.budgettrackerapp.data.entities.BudgetGoal
import com.example.budgettrackerapp.data.entities.Category
import com.example.budgettrackerapp.data.entities.Expense
import com.example.budgettrackerapp.data.entities.User

@Database(
    entities = [User::class, Category::class, Expense::class, BudgetGoal::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetGoalDao(): BudgetGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
