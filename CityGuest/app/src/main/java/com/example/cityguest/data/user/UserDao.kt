package com.example.cityguest.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cityguest.data.points.PointsEarning
import com.example.cityguest.data.points.PointsExpense
import com.example.cityguest.data.points.UnlockedCity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    /**
     * Recupera un utente in modo sincrono/asincrono tramite la sua email.
     * Utilizzato principalmente durante le fasi di Login e Verifica Credenziali.
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    /**
     * Inserisce un nuovo utente nel database.
     * La strategia [OnConflictStrategy.ABORT] interrompe l'operazione in caso di email duplicata,
     */
    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertUser(user: User)

    /**
     * Aggiorna i dati esistenti di un utente (es. aggiornamento punteggio o cambio immagine profilo).
     */
    @Update
    suspend fun updateUser(user: User)

    /**
     * Osserva in tempo reale le variazioni del profilo utente.
     * Restituisce un flusso reattivo [Flow] che emette automaticamente un nuovo oggetto [User]
     * ogni volta che i dati sul database subiscono una modifica (es. incremento punti).
     */
    @Query("SELECT * FROM users WHERE email = :email")
    fun observeUserByEmail(email: String): Flow<User?>

    /**
     * Registra lo sblocco di una nuova città da parte di un utente.
     * La strategia [OnConflictStrategy.IGNORE] previene errori qualora si tenti di sbloccare una città già sbloccata.
     */
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertUnlockedCity(unlockedCity: UnlockedCity)

    /**
     * Restituisce un flusso continuo ([Flow]) contenente l'elenco dei nomi delle città sbloccate da uno specifico utente.
     */
    @Query("SELECT cityName FROM unlocked_cities WHERE userEmail = :email")
    fun observeUnlockedCities(email: String): Flow<List<String>>

    /**
     * Inserisce un record relativo alla spesa di punti per lo sblocco di contenuti (es. una città).
     * Sostituisce il record precedente ([OnConflictStrategy.REPLACE]) in caso di collisione di ID.
     */
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPointsExpense(expense: PointsExpense)

    /**
     * Fornisce uno storico reattivo delle spese effettuate dall'utente, ordinato dalla più recente.
     */
    @Query("SELECT * FROM points_expenses WHERE userEmail = :email ORDER BY timestamp DESC")
    fun observePointsExpenses(email: String): Flow<List<PointsExpense>>

    /**
     * Inserisce un record relativo al guadagno di punti (es. visita a un POI).
     */
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPointsEarning(earning: PointsEarning)

    /**
     * Fornisce uno storico reattivo dei punti accumulati dall'utente, ordinato dal guadagno più recente.
     */
    @Query("SELECT * FROM points_earnings WHERE userEmail = :email ORDER BY timestamp DESC")
    fun observePointsEarnings(email: String): Flow<List<PointsEarning>>
}