package com.digitalarchitects.data.user

import com.digitalarchitects.data.requests.UpdateUserRequest

interface UserDataSource {

    suspend fun getUserByEmail(email: String): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun getUsers(): List<User>
    suspend fun getUserById(userId: String): User?
    suspend fun updateUser(userId: String, updatedUser: UpdateUserRequest): Boolean
    suspend fun deleteUserById(userId: String): Boolean
}