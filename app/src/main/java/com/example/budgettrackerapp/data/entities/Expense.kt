package com.example.budgettrackerapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["userId"]),
        // Optional compound index if you frequently query by both user and date
        Index(value = ["userId", "date"])
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Long = 0,
    val amount: Double,
    val description: String,
    val date: Long, // Store date as timestamp
    val startTime: Long?, // Optional start time as timestamp
    val endTime: Long?, // Optional end time as timestamp
    val photoPath: String?, // Path to stored photo
    val categoryId: Long,
    val userId: Long
)