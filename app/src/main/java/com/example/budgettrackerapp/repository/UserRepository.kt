package com.example.budgettrackerapp.repository

import com.example.budgettrackerapp.data.dao.UserDao
import com.example.budgettrackerapp.data.entities.User

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(username: String, password: String): Result<Long> {
        return try {
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                Result.failure(Exception("Username already exists"))
            } else {
                val userId = userDao.insertUser(User(username = username, password = password))
                Result.success(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(username: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByCredentials(username, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid username or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}