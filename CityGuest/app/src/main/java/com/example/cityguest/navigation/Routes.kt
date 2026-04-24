package com.example.cityguest.navigation

import kotlinx.serialization.Serializable

sealed interface Route /*interfaccia chiusa (da aggiungere qui le implementazioni)*/{
    @Serializable
    data object Login : Route /*identificatore logico per arrivare alla schermata*/

    @Serializable
    data object Register : Route

    /*utilizzo di singleton in modo da avere un'unica istanza per ognuno*/
    @Serializable
    data object Home : Route
}