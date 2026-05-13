package com.example.cityguest.ui.theme

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.cityguest.navigation.Route
import com.google.android.gms.maps.model.LatLng

@Composable
fun PoiDetailScreen(
    poi: Route.PoiDetail,
    userLocation: LatLng?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val distance = remember(userLocation) {
        if (userLocation != null) {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                poi.lat.toDouble(), poi.lng.toDouble(), results
            )
            results[0] / 1000f
        } else {
            0f
        }
    }

    val calculatedPoints = poi.basePoints + (distance * 10).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        AsyncImage(
            model = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=...",
            contentDescription = poi.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = poi.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
            Text(" 0.0 (0 recensioni) • ", style = MaterialTheme.typography.bodyMedium)
            Text("${"%.1f".format(distance)} km da te", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { /* Azione selezione */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.wrapContentWidth()
        ) {
            Text("Select", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Descrizione:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            text = poi.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("N. volte visitato:", fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("0", fontSize = 20.sp, fontWeight = FontWeight.Bold) // Fisso a 0 come richiesto
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Punti:", fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("$calculatedPoints", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("N. stelle:", fontWeight = FontWeight.Bold, color = Color.Gray)
                // Stelle vuote (StarBorder)
                Row {
                    repeat(5) {
                        Icon(Icons.Outlined.StarBorder, contentDescription = null, tint = Color.LightGray)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Distanza:", fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("${"%.2f".format(distance)} km", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val gmmIntentUri = "google.navigation:q=${poi.lat},${poi.lng}".toUri()
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Text("AVVIA", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }

            Button(
                onClick = { /* Qui andrà la fotocamera */ },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Text("POSTA", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Spazio per non coprire dietro la nav bar
    }
}