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
    data object CityList : Route

    @Serializable
    data class CityMap(val cityName: String) : Route

    @Serializable
    data object GameRules : Route
}