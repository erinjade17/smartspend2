package com.example.budgettrackerapp.repository

import androidx.lifecycle.LiveData
import com.example.budgettrackerapp.data.dao.CategoryDao
import com.example.budgettrackerapp.data.entities.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getUserCategories(userId: Long): LiveData<List<Category>> {
        return categoryDao.getCategoriesByUser(userId)
    }

    suspend fun addCategory(name: String, userId: Long): Result<Long> {
        return try {
            val categoryId = categoryDao.insertCategory(Category(name = name, userId = userId))
            Result.success(categoryId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(category: Category): Result<Unit> {
        return try {
            categoryDao.updateCategory(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(category: Category): Result<Unit> {
        return try {
            categoryDao.deleteCategory(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryById(categoryId: Long): Result<Category> {
        return try {
            val category = categoryDao.getCategoryById(categoryId)
            if (category != null) {
                Result.success(category)
            } else {
                Result.failure(Exception("Category not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}