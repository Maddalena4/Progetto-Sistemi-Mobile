package com.example.cityguest.data

import com.example.cityguest.ui.components.PlaceOfInterest
import com.google.android.gms.maps.model.LatLng

object PoiData {

    val pointsOfInterest = listOf(
        PlaceOfInterest("1", "Duomo di Forlì", "Un'abbazia storica nel cuore della città.", LatLng(44.2221, 12.0390), 100, "url_immagine"),
        PlaceOfInterest("2", "Parco Urbano", "Il polmone verde di Forlì.", LatLng(44.2250, 12.0450), 50, "url_immagine")
    )
}