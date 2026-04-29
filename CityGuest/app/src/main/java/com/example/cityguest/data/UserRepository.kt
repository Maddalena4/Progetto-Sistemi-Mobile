package com.example.cityguest.data

// Il repository usa il DAO per leggere/scrivere dati, ma nasconde i dettagli al resto dell’app
class UserRepository(private val userDao: UserDao) {
    // Controlla se l'amil esiste già
    suspend fun isEmailRegistered(email: String) = userDao.getUserByEmail(email) != null
    // Registrazione utente
    suspend fun register(user: User) = userDao.insertUser(user)
    // Ottiene un utente
    suspend fun getUser(email: String) = userDao.getUserByEmail(email)
}