package com.example.budgettrackerapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "budget_goals",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BudgetGoal(
    @PrimaryKey(autoGenerate = true)
    val goalId: Long = 0,
    val minimumGoal: Double,
    val maximumGoal: Double,
    val month: Int, // 1-12
    val year: Int,
    val userId: Long
)