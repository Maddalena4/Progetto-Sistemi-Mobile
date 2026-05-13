package com.example.cityguest.ui.theme

import android.Manifest
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.cityguest.navigation.Route
import com.google.android.gms.maps.model.LatLng
import java.io.File

@Composable
fun PoiDetailScreen(
    poi: Route.PoiDetail,
    userLocation: LatLng?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val mapsApiKey = remember {
        val appInfo: ApplicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        appInfo.metaData.getString("com.google.android.geo.API_KEY") ?: ""
    }

    val distance = remember(userLocation) {
        if (userLocation != null) {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                userLocation.latitude.toDouble(), userLocation.longitude.toDouble(),
                poi.lat.toDouble(), poi.lng.toDouble(), results
            )
            results[0] / 1000f
        } else 0f
    }

    val calculatedPoints = poi.basePoints + (distance * 25).toInt()

    val googleMapsPhotoUrl = "https://maps.googleapis.com/maps/api/streetview" +
            "?size=800x600" +
            "&location=${poi.lat},${poi.lng}" +
            "&fov=90&heading=235&pitch=10" +
            "&key=$mapsApiKey"

    val photoUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { _ -> }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val file = File(context.externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            photoUri.value = uri
            cameraLauncher.launch(uri)
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            AsyncImage(
                model = googleMapsPhotoUrl,
                contentDescription = poi.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 8.dp, start = 12.dp)
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = poi.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("4.5", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                Spacer(modifier = Modifier.width(4.dp))
                repeat(4) { Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp)) }
                Icon(Icons.Outlined.StarBorder, null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                Text(" • Monumento", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Descrizione:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = poi.description, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DetailStat("N. volte visitato:", "0")
                DetailStat("Punti:", "$calculatedPoints", Color(0xFF2E7D32))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("N. stelle:", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                    Row { repeat(5) { Icon(Icons.Outlined.StarBorder, null, tint = Color.LightGray) } }
                }
                DetailStat("Distanza:", "${"%.2f".format(distance)} km")
            }

            Spacer(modifier = Modifier.height(40.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        val gmmIntentUri = "google.navigation:q=${poi.lat},${poi.lng}".toUri()
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply { setPackage("com.google.android.apps.maps") }
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text("AVVIA", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                }

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            val file = File(context.externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                            photoUri.value = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text("POSTA", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun DetailStat(label: String, value: String, color: Color = Color.Black) {
    Column {
        Text(label, fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
    }
}