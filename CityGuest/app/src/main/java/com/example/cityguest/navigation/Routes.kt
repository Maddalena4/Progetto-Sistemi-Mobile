package com.example.cityguest.navigation

import kotlinx.serialization.Serializable

/**
 * Definisce tutte le rotte di navigazione disponibili all'interno dell'applicazione.
 */
sealed interface Route {
    /**
     * Rotta per la schermata di accesso (Login).
     * Non richiede parametri.
     */
    @Serializable
    data object Login : Route

    /**
     * Rotta per la schermata di registrazione di un nuovo account.
     * Non richiede parametri.
     */
    @Serializable
    data object Register : Route

    /**
     * Rotta per la schermata principale (Home) dell'utente autenticato.
     *
     * @property email L'indirizzo email dell'utente connesso.
     * @property username Il nome utente dell'utente connesso.
     */
    @Serializable
    data class Home(
        val email: String,
        val username: String
    ) : Route

    /**
     * Rotta per la schermata del profilo utente.
     *
     * @property email L'indirizzo email dell'utente.
     * @property username Il nome utente da visualizzare.
     */
    @Serializable
    data class Profile(
        val email: String,
        val username: String
    ) : Route

    /**
     * Rotta per la schermata delle impostazioni dell'applicazione.
     *
     * @property email L'indirizzo email dell'utente per gestire configurazioni specifiche legate all'account.
     */
    @Serializable
    data class Settings(
        val email: String
    ) : Route

    /**
     * Rotta per la schermata dei distintivi (Badges) sbloccati dall'utente.
     *
     * @property email L'indirizzo email dell'utente per recuperare i badge a lui associati.
     */
    @Serializable
    data class Badges(
        val email: String
    ) : Route

    /**
     * Rotta per la schermata della mappa globale che mostra la posizione dell'utente o dei POI.
     *
     * @property email L'indirizzo email dell'utente connesso.
     * @property username Il nome utente dell'utente connesso.
     */
    @Serializable
    data class Map(
        val email: String,
        val username: String
    ) : Route

    /**
     * Rotta per la schermata che mostra l'elenco delle città disponibili da esplorare.
     * Non richiede parametri.
     */
    @Serializable
    data object CityList : Route

    /**
     * Rotta per la mappa specifica di una singola città.
     *
     * @property cityName Il nome della città di cui mostrare la mappa.
     */
    @Serializable
    data class CityMap(val cityName: String) : Route

    /**
     * Rotta per la schermata che illustra le regole del gioco o il funzionamento dell'app.
     * Non richiede parametri.
     */
    @Serializable
    data object GameRules : Route

    /**
     * Rotta per la schermata di dettaglio di uno specifico Punto di Interesse (POI).
     *
     * @property id L'identificativo univoco numerico del POI.
     * @property name Il nome del POI.
     * @property description La descrizione storica o turistica del luogo.
     * @property lat La latitudine geografica del POI.
     * @property lng La longitudine geografica del POI.
     * @property basePoints I punti base assegnati all'utente per la visita o l'interazione con questo POI.
     */
    @Serializable
    data class PoiDetail(
        val id: String,
        val name: String,
        val description: String,
        val lat: Float,
        val lng: Float,
        val basePoints: Int
    ) : Route

    /**
     * Rotta per la schermata di revisione di una foto scattata dall'utente in un POI.
     *
     * @property photoUri L'URI (percorso locale) della foto appena scattata.
     * @property poiId L'ID del Punto di Interesse a cui si riferisce la foto.
     * @property poiName Il nome del Punto di Interesse.
     * @property calculatedPoints I punti calcolati e assegnati per questa specifica interazione.
     * @property userEmail L'indirizzo email dell'utente che ha scattato la foto.
     * @property distanceKm La distanza in chilometri dell'utente dal POI al momento dello scatto.
     */
    @Serializable
    data class PhotoReview(
        val photoUri: String,
        val poiId: String,
        val poiName: String,
        val calculatedPoints: Int,
        val userEmail: String,
        val distanceKm: Float
    ) : Route

    /**
     * Rotta per la schermata che mostra l'elenco dei Punti di Interesse salvati nei preferiti.
     *
     * @property email L'indirizzo email dell'utente per recuperare la sua lista preferiti.
     */
    @Serializable
    data class Favorites(
        val email: String
    ) : Route

    /**
     * Rotta per la schermata che mostra lo storico dei luoghi già visitati dall'utente.
     *
     * @property email L'indirizzo email dell'utente.
     */
    @Serializable
    data class VisitedPlaces(val email: String) : Route

    /**
     * Rotta per la schermata che mostra lo storico dei punti guadagnati dall'utente nel tempo.
     *
     * @property email L'indirizzo email dell'utente.
     */
    @Serializable
    data class PointsHistory(val email: String) : Route
}
