package com.example.cityguest.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data class Home(
        val email: String,
        val username: String
    ) : Route

    @Serializable
    data class Profile(
        val email: String,
        val username: String
    ) : Route

    @Serializable
    data class Map(
        val email: String,
        val username: String
    ) : Route
    @Serializable
    data object CityList : Route

    @Serializable
    data class CityMap(val cityName: String) : Route

    @Serializable
    data object GameRules : Route

    @Serializable
    data class PoiDetail(
        val id: Int,
        val name: String,
        val description: String,
        val lat: Float,
        val lng: Float,
        val basePoints: Int
    ) : Route
    @Serializable data class PhotoReview(
        val photoUri: String,
        val poiId: Int,
        val poiName: String,
        val calculatedPoints: Int,
        val userEmail: String,
        val distanceKm: Float
    ) : Route
    @Serializable
    data class Favorites(
        val email: String
    ) : Route
    @Serializable
    data class VisitedPlaces(val email: String) : Route

}