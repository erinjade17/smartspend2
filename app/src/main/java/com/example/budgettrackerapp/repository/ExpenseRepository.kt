package com.example.budgettrackerapp.repository

import androidx.lifecycle.LiveData
import com.example.budgettrackerapp.data.dao.ExpenseDao
import com.example.budgettrackerapp.data.entities.Expense
import java.util.Calendar
import java.util.Date

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getExpensesForPeriod(userId: Long, startDate: Long, endDate: Long): LiveData<List<Expense>> {
        return expenseDao.getExpensesByUserAndDateRange(userId, startDate, endDate)
    }

    suspend fun addExpense(
        amount: Double,
        description: String,
        date: Long,
        startTime: Long?,
        endTime: Long?,
        photoPath: String?,
        categoryId: Long,
        userId: Long
    ): Result<Long> {
        return try {
            val expense = Expense(
                amount = amount,
                description = description,
                date = date,
                startTime = startTime,
                endTime = endTime,
                photoPath = photoPath,
                categoryId = categoryId,
                userId = userId
            )
            val expenseId = expenseDao.insertExpense(expense)
            Result.success(expenseId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            expenseDao.updateExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(expense: Expense): Result<Unit> {
        return try {
            expenseDao.deleteExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTotalAmountByCategory(
        userId: Long,
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Result<Double> {
        return try {
            val total = expenseDao.getTotalAmountByCategory(userId, categoryId, startDate, endDate) ?: 0.0
            Result.success(total)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpenseById(expenseId: Long): Result<Expense> {
        return try {
            val expense = expenseDao.getExpenseById(expenseId)
            if (expense != null) {
                Result.success(expense)
            } else {
                Result.failure(Exception("Expense not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}