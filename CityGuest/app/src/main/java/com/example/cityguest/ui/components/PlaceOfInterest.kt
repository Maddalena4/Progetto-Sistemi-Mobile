package com.example.cityguest.ui.components

import com.google.android.gms.maps.model.LatLng

data class PlaceOfInterest(
    val id: String,
    val name: String,
    val description: String,
    val location: LatLng,
    val basePoints: Int,
    val imageRes: String,
    val visits: Int = 0,
    val stars: Int = 0
)