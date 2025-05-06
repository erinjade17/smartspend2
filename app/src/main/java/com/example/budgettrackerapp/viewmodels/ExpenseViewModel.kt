package com.example.budgettrackerapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettrackerapp.data.entities.Expense
import com.example.budgettrackerapp.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ExpenseViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private val _expenseOperationResult = MutableLiveData<Result<Any>>()
    val expenseOperationResult: LiveData<Result<Any>> = _expenseOperationResult

    private val _categoryTotalAmount = MutableLiveData<Map<Long, Double>>()
    val categoryTotalAmount: LiveData<Map<Long, Double>> = _categoryTotalAmount

    fun getExpensesForPeriod(userId: Long, startDate: Long, endDate: Long): LiveData<List<Expense>> {
        return expenseRepository.getExpensesForPeriod(userId, startDate, endDate)
    }

    fun addExpense(
        amount: Double,
        description: String,
        date: Long,
        startTime: Long?,
        endTime: Long?,
        photoPath: String?,
        categoryId: Long,
        userId: Long
    ) {
        viewModelScope.launch {
            val result = expenseRepository.addExpense(
                amount, description, date, startTime, endTime, photoPath, categoryId, userId
            )
            _expenseOperationResult.postValue(result)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            val result = expenseRepository.updateExpense(expense)
            _expenseOperationResult.postValue(result)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            val result = expenseRepository.deleteExpense(expense)
            _expenseOperationResult.postValue(result)
        }
    }

    fun calculateTotalAmountByCategories(
        userId: Long,
        categoryIds: List<Long>,
        startDate: Long,
        endDate: Long
    ) {
        viewModelScope.launch {
            val totalsMap = mutableMapOf<Long, Double>()

            for (categoryId in categoryIds) {
                val result = expenseRepository.getTotalAmountByCategory(
                    userId, categoryId, startDate, endDate
                )
                if (result.isSuccess) {
                    totalsMap[categoryId] = result.getOrNull() ?: 0.0
                }
            }

            _categoryTotalAmount.postValue(totalsMap)
        }
    }

    suspend fun getExpenseById(expenseId: Long): Expense? {
        val result = expenseRepository.getExpenseById(expenseId)
        return result.getOrNull()
    }
}
