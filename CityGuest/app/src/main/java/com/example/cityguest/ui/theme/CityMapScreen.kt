package com.example.cityguest.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cityguest.ui.components.PlaceOfInterest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun CityMapScreen(
    cityName: String,
    onInfoClick: () -> Unit,
    onPoiClick: (PlaceOfInterest) -> Unit
) {
    val (cityLocation, pointsOfInterest) = remember(cityName) {
        when (cityName.lowercase().trim()) {
            "roma" -> {
                val centroRoma = LatLng(41.8902, 12.4922)
                val poiRoma = listOf(
                    PlaceOfInterest("101", "Colosseo", "L'anfiteatro più famoso del mondo romano.", LatLng(41.8902, 12.4922), 150, "Roma"),
                    PlaceOfInterest("102", "Fontana di Trevi", "La celebre fontana barocca.", LatLng(41.9009, 12.4833), 100, "Roma"),
                    PlaceOfInterest("103", "Pantheon", "Antico tempio romano ora basilica.", LatLng(41.8986, 12.4769), 120, "Roma")
                )
                Pair(centroRoma, poiRoma)
            }
            "verona" -> {
                val centroVerona = LatLng(45.4387, 10.9928)
                val poiVerona = listOf(
                    PlaceOfInterest("201", "Arena di Verona", "Anfiteatro romano ancora in uso per l'opera.", LatLng(45.4382, 10.9944), 140, "Verona"),
                    PlaceOfInterest("202", "Casa di Giulietta", "Il famoso balcone della tragedia di Shakespeare.", LatLng(45.4419, 11.0000), 110, "Verona"),
                    PlaceOfInterest("203", "Piazza delle Erbe", "La piazza più antica di Verona.", LatLng(45.4422, 10.9975), 90, "Verona")
                )
                Pair(centroVerona, poiVerona)
            }
            else -> {
                val centroForli = LatLng(44.2221, 12.0390)
                val poiForli = listOf(
                    PlaceOfInterest("1", "Duomo di Forlì", "Un'abbazia storica nel cuore della città.", LatLng(44.2221, 12.0390), 100, "Forlì"),
                    PlaceOfInterest("2", "Abbazia di San Mercuriale", "Famoso campanile e chiostro duecentesco.", LatLng(44.2226, 12.0422), 120, "Forlì"),
                    PlaceOfInterest("3", "Rocca di Ravaldino", "Fortezza medievale legata a Caterina Sforza.", LatLng(44.2175, 12.0360), 150, "Forlì")
                )
                Pair(centroForli, poiForli)
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cityLocation, 14f)
    }

    androidx.compose.runtime.LaunchedEffect(cityLocation) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(cityLocation, 14f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)
        ) {
            pointsOfInterest.forEach { poi ->
                Marker(
                    state = MarkerState(position = poi.location),
                    title = poi.name,
                    snippet = "Clicca qui per dettagli",
                    onInfoWindowClick = { onPoiClick(poi) }
                )
            }
        }

        Surface(
            onClick = onInfoClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .size(44.dp),
            shape = RoundedCornerShape(8.dp),
            color = PureWhite,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Regole",
                    tint = MapsBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}