package com.example.budgettrackerapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "incomes")
data class Income(
    @PrimaryKey(autoGenerate = true)
    val incomeId: Long = 0,
    val amount: Double,
    val source: String,
    val date: Date,
    val notes: String?,
    val userId: Long
)