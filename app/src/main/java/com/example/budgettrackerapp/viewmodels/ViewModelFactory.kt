package com.example.budgettrackerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.budgettrackerapp.repository.BudgetGoalRepository
import com.example.budgettrackerapp.repository.CategoryRepository
import com.example.budgettrackerapp.repository.ExpenseRepository
import com.example.budgettrackerapp.repository.UserRepository
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository,
    private val budgetGoalRepository: BudgetGoalRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(categoryRepository) as T
            }
            modelClass.isAssignableFrom(ExpenseViewModel::class.java) -> {
                ExpenseViewModel(expenseRepository) as T
            }
            modelClass.isAssignableFrom(BudgetGoalViewModel::class.java) -> {
                BudgetGoalViewModel(budgetGoalRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}