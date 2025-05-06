package com.example.budgettrackerapp.repository

import androidx.lifecycle.LiveData
import com.example.budgettrackerapp.data.dao.BudgetGoalDao
import com.example.budgettrackerapp.data.entities.BudgetGoal

class BudgetGoalRepository(private val budgetGoalDao: BudgetGoalDao) {

    suspend fun setBudgetGoal(
        minimumGoal: Double,
        maximumGoal: Double,
        month: Int,
        year: Int,
        userId: Long
    ): Result<Long> {
        return try {
            val budgetGoal = BudgetGoal(
                minimumGoal = minimumGoal,
                maximumGoal = maximumGoal,
                month = month,
                year = year,
                userId = userId
            )
            val goalId = budgetGoalDao.insertOrUpdateBudgetGoal(budgetGoal)
            Result.success(goalId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBudgetGoal(budgetGoal: BudgetGoal): Result<Unit> {
        return try {
            budgetGoalDao.updateBudgetGoal(budgetGoal)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBudgetGoalByMonthYear(userId: Long, month: Int, year: Int): Result<BudgetGoal?> {
        return try {
            val budgetGoal = budgetGoalDao.getBudgetGoalByMonthYear(userId, month, year)
            Result.success(budgetGoal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllBudgetGoalsByUser(userId: Long): LiveData<List<BudgetGoal>> {
        return budgetGoalDao.getAllBudgetGoalsByUser(userId)
    }
}