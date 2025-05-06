package com.example.budgettrackerapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettrackerapp.data.entities.BudgetGoal
import com.example.budgettrackerapp.repository.BudgetGoalRepository
import kotlinx.coroutines.launch

class BudgetGoalViewModel(private val budgetGoalRepository: BudgetGoalRepository) : ViewModel() {

    private val _budgetGoalOperationResult = MutableLiveData<Result<Any>>()
    val budgetGoalOperationResult: LiveData<Result<Any>> = _budgetGoalOperationResult

    private val _currentBudgetGoal = MutableLiveData<BudgetGoal?>()
    val currentBudgetGoal: LiveData<BudgetGoal?> = _currentBudgetGoal

    fun setBudgetGoal(minimumGoal: Double, maximumGoal: Double, month: Int, year: Int, userId: Long) {
        viewModelScope.launch {
            val result = budgetGoalRepository.setBudgetGoal(minimumGoal, maximumGoal, month, year, userId)
            _budgetGoalOperationResult.postValue(result)
        }
    }

    fun getAllBudgetGoalsByUser(userId: Long): LiveData<List<BudgetGoal>> {
        return budgetGoalRepository.getAllBudgetGoalsByUser(userId)
    }

    fun getBudgetGoalForMonth(userId: Long, month: Int, year: Int) {
        viewModelScope.launch {
            val result = budgetGoalRepository.getBudgetGoalByMonthYear(userId, month, year)
            if (result.isSuccess) {
                _currentBudgetGoal.postValue(result.getOrNull())
            } else {
                _currentBudgetGoal.postValue(null)
            }
        }
    }
}