package com.example.cityguest.data.user

class UserRepository(private val userDao: UserDao) {

    /**
     * Verifica la presenza di una determinata email all'interno del database.
     * Utilizzata per validare l'univocità dell'account in fase di registrazione.
     *
     * @return `true` se l'email è già associata a un account, `false` altrimenti.
     */
    suspend fun isEmailRegistered(email: String) = userDao.getUserByEmail(email) != null

    /**
     * Esegue la persistenza di un nuovo utente registrato all'interno del database locale.
     */
    suspend fun register(user: User) = userDao.insertUser(user)

    /**
     * Recupera l'istanza correntemente salvata dell'utente tramite la chiave primaria (email).
     */
    suspend fun getUser(email: String) = userDao.getUserByEmail(email)

    /**
     * Aggiorna le informazioni dell'utente all'interno del database.
     */
    suspend fun updateUser(user: User) = userDao.updateUser(user)

}