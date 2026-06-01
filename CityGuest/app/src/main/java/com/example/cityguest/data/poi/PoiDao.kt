package com.example.cityguest.data.poi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cityguest.data.poi.PoiStatus
import com.example.cityguest.data.poi.PoiVisit
import kotlinx.coroutines.flow.Flow

@Dao
interface PoiDao {
    /**
     * Osserva in modo reattivo lo stato di un specifico Punto di Interesse per un determinato utente.
     *
     * @param poiId L'ID del Punto di Interesse da osservare.
     * @param userEmail L'email dell'utente proprietario dello stato.
     * @return Un [Flow] che emette l'oggetto [PoiStatus] aggiornato ogni volta che la riga corrispondente subisce variazioni, o `null` se il record non esiste.
     */
    @Query("SELECT * FROM poi_status WHERE poiId = :poiId AND userEmail = :userEmail LIMIT 1")
    fun observePoiStatus(poiId: Int, userEmail: String): Flow<PoiStatus?>

    /**
     * Recupera lo stato di un Punto di Interesse per un determinato utente.
     *
     * @param poiId L'ID del Punto di Interesse.
     * @param userEmail L'email dell'utente proprietario dello stato.
     * @return L'oggetto [PoiStatus] se presente nel database, altrimenti `null`.
     */
    @Query("SELECT * FROM poi_status WHERE poiId = :poiId AND userEmail = :userEmail LIMIT 1")
    suspend fun getPoiStatus(poiId: Int, userEmail: String): PoiStatus?

    /**
     * Inserisce un nuovo stato per un POI o aggiorna quello esistente.
     *
     * Se esiste già un record nel database con la stessa chiave primaria composta (`userEmail` e `poiId`),
     * questo verrà sostituito interamente con i nuovi dati grazie alla strategia [OnConflictStrategy.REPLACE].
     *
     * @param poiStatus L'istanza di [PoiStatus] da salvare o aggiornare.
     */
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertOrUpdatePoiStatus(poiStatus: PoiStatus)

    /**
     * Osserva in modo reattivo l'elenco dei Punti di Interesse che un utente ha contrassegnato come preferiti.
     *
     * @param userEmail L'email dell'utente di cui recuperare i preferiti.
     * @return Un [Flow] che emette una lista di [PoiStatus] filtrata per `isFavorite = 1`.
     */
    @Query("SELECT * FROM poi_status WHERE userEmail = :userEmail AND isFavorite = 1")
    fun observeFavoritePois(userEmail: String): Flow<List<PoiStatus>>

    /**
     * Registra una nuova visita nello storico del database.
     *
     * @param poiVisit L'oggetto [PoiVisit] contenente i dettagli del check-in da inserire.
     */
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPoiVisit(poiVisit: PoiVisit)

    /**
     * Osserva in modo reattivo lo storico di tutte le visite effettuate da un utente,
     * ordinandole cronologicamente dalla più recente alla più vecchia.
     *
     * @param userEmail L'email dell'utente di cui recuperare lo storico visite.
     * @return Un [Flow] che emette la lista cronologica inversa di [PoiVisit].
     */
    @Query("SELECT * FROM poi_visits WHERE userEmail = :userEmail ORDER BY timestamp DESC")
    fun observePoiVisits(userEmail: String): Flow<List<PoiVisit>>
}