package com.example.cityguest.ui.theme

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cityguest.navigation.Route
import com.google.android.gms.maps.model.LatLng
import androidx.core.net.toUri

@Composable
fun PoiDetailScreen(
    poi: Route.PoiDetail,
    userLocation: LatLng?, // Passata dal sistema GPS
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Calcolo della distanza in KM
    val distance = remember(userLocation) {
        if (userLocation != null) {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                poi.lat, poi.lng, results
            )
            results[0] / 1000 // Trasforma metri in Km
        } else 0.0f
    }

    // Calcolo Punti (Esempio: 10 punti per ogni km di distanza)
    val calculatedPoints = (distance * 10).toInt() + poi.basePoints

    // Launcher per Foto/Galleria
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Gestisci l'immagine postata qui
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Immagine del posto
        AsyncImage(
            model = "https://example.com/image.jpg", // Usa l'URL reale
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = poi.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = poi.description, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Tabella Informazioni
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📍 Distanza: ${"%.2f".format(distance)} km")
                Text("🏆 Punti attuali: $calculatedPoints")
                Text("次数 Visite: 3") // Esempio statico
                Text("⭐ Valutazione: 4/5") // Esempio statico
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Pulsanti Azione
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Pulsante AVVIA (Navigatore Google)
            Button(
                onClick = {
                    val gmmIntentUri = "google.navigation:q=${poi.lat},${poi.lng}".toUri()
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("AVVIA")
            }

            // Pulsante POSTA (Galleria/Camera)
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("POSTA")
            }
        }
    }
}