package com.example.cityguest.data

import androidx.room.*

@Dao // dice a Room che questa interfaccia contiene operazioni sul database
interface UserDao {
    // Cerca un utente con questa email e lo restituisce se esiste
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    // Suspende --> funzione asincrona (usare con coroutines, evita blocchi della UI)
    suspend fun getUserByEmail(email: String): User?

    // Inserimento di un utente
    @Insert(onConflict = OnConflictStrategy.ABORT) // Fallisce se l'email esiste già
    suspend fun insertUser(user: User)
}