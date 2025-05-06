package com.example.budgettrackerapp.di

import android.content.Context
import androidx.room.Room
import com.example.budgettrackerapp.data.AppDatabase
import com.example.budgettrackerapp.data.dao.*
import com.example.budgettrackerapp.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Database
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "budget_tracker_database"
        ).build()
    }

    // DAOs
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideBudgetGoalDao(database: AppDatabase): BudgetGoalDao = database.budgetGoalDao()

    // Repositories
    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepository(userDao)

    @Provides
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository = CategoryRepository(categoryDao)

    @Provides
    fun provideExpenseRepository(expenseDao: ExpenseDao): ExpenseRepository = ExpenseRepository(expenseDao)

    @Provides
    fun provideBudgetGoalRepository(budgetGoalDao: BudgetGoalDao): BudgetGoalRepository = BudgetGoalRepository(budgetGoalDao)
}