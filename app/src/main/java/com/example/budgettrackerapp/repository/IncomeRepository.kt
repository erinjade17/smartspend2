package com.example.budgettrackerapp.repository

import com.example.budgettrackerapp.data.dao.IncomeDao
import com.example.budgettrackerapp.data.entities.Income
import javax.inject.Inject

class IncomeRepository @Inject constructor(
    private val incomeDao: IncomeDao
) {
    suspend fun addIncome(income: Income) = incomeDao.insertIncome(income)

    fun getIncomes(userId: Long) = incomeDao.getIncomesByUser(userId)
}