package com.example.budgettrackerapp

import android.app.Application
import com.example.budgettrackerapp.data.AppDatabase

class BudgetTrackerApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}