package com.example.budgettrackerapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgettrackerapp.data.entities.Category
import com.example.budgettrackerapp.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {

    private val _categoryOperationResult = MutableLiveData<Result<Any>>()
    val categoryOperationResult: LiveData<Result<Any>> = _categoryOperationResult

    fun getUserCategories(userId: Long): LiveData<List<Category>> {
        return categoryRepository.getUserCategories(userId)
    }

    fun addCategory(name: String, userId: Long) {
        viewModelScope.launch {
            val result = categoryRepository.addCategory(name, userId)
            _categoryOperationResult.postValue(result)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            val result = categoryRepository.updateCategory(category)
            _categoryOperationResult.postValue(result)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            val result = categoryRepository.deleteCategory(category)
            _categoryOperationResult.postValue(result)
        }
    }
}