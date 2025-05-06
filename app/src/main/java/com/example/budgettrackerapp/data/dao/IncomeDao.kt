package com.example.budgettrackerapp.data.dao

import androidx.room.*
import com.example.budgettrackerapp.data.entities.Income

@Dao
interface IncomeDao {
    @Insert
    suspend fun insertIncome(income: Income): Long

    @Update
    suspend fun updateIncome(income: Income)

    @Query("SELECT * FROM incomes WHERE userId = :userId")
    fun getIncomesByUser(userId: Long): List<Income>
}