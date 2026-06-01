package com.example.cityguest.data

class UserRepository(private val userDao: UserDao) {

    suspend fun isEmailRegistered(email: String) = userDao.getUserByEmail(email) != null

    suspend fun register(user: User) = userDao.insertUser(user)

    suspend fun getUser(email: String) = userDao.getUserByEmail(email)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

}