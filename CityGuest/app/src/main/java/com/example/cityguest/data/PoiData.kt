package com.example.cityguest.data

import com.example.cityguest.ui.components.PlaceOfInterest
import com.google.android.gms.maps.model.LatLng

object PoiData {

    val pointsOfInterest = listOf(
        // Luoghi di Roma
        PlaceOfInterest("101", "Colosseo", "L'anfiteatro più famoso del mondo romano.", LatLng(41.8902, 12.4922), 150, "Roma"),
        PlaceOfInterest("102", "Fontana di Trevi", "La celebre fontana barocca.", LatLng(41.9009, 12.4833), 100, "Roma"),
        PlaceOfInterest("103", "Pantheon", "Antico tempio romano ora basilica.", LatLng(41.8986, 12.4769), 120, "Roma"),

        // Luoghi di Verona
        PlaceOfInterest("201", "Arena di Verona", "Anfiteatro romano ancora in uso per l'opera.", LatLng(45.4382, 10.9944), 140, "Verona"),
        PlaceOfInterest("202", "Casa di Giulietta", "Il famoso balcone della tragedia di Shakespeare.", LatLng(45.4419, 11.0000), 110, "Verona"),
        PlaceOfInterest("203", "Piazza delle Erbe", "La piazza più antica di Verona.", LatLng(45.4422, 10.9975), 90, "Verona"),

        // Luoghi di Forlì
        PlaceOfInterest("1", "Duomo di Forlì", "Un'abbazia storica nel cuore della città.", LatLng(44.2221, 12.0390), 100, "Forlì"),
        PlaceOfInterest("2", "Abbazia di San Mercuriale", "Famoso campanile e chiostro duecentesco.", LatLng(44.2226, 12.0422), 120, "Forlì"),
        PlaceOfInterest("3", "Rocca di Ravaldino", "Fortezza medievale legata a Caterina Sforza.", LatLng(44.2175, 12.0360), 150, "Forlì")
    )

    /**
     * Funzione di utility per estrarre al volo i punti di una specifica città
     * all'interno della schermata della mappa.
     */
    fun getPoiForCity(cityName: String): List<PlaceOfInterest> {
        return pointsOfInterest.filter { it.imageRes.lowercase().trim() == cityName.lowercase().trim() }
    }
}