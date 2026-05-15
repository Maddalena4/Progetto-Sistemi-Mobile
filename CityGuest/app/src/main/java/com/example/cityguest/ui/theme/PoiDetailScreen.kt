package com.example.cityguest.ui.theme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cityguest.data.PoiDao
import com.example.cityguest.navigation.Route
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text

@Composable
fun PoiDetailScreen(
    poi: Route.PoiDetail,
    userLocation: LatLng?,
    poiDao: PoiDao,
    navController: NavController,
    isJustUploaded: Boolean,
    currentUserEmail: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val poiStatusState = poiDao.observePoiStatus(poi.id, currentUserEmail).collectAsState(initial = null)
    val poiStatus = poiStatusState.value

    val visits = poiStatus?.visits ?: 0
    val savedStars = poiStatus?.stars ?: 0
    val savedPhotoUri = poiStatus?.photoUri

    var selectedStars by remember(savedStars) { mutableIntStateOf(if (savedStars == 0) 5 else savedStars) }
    LaunchedEffect(selectedStars) {
        if (poiStatus != null && selectedStars != poiStatus.stars) {
            poiDao.insertOrUpdatePoiStatus(poiStatus.copy(stars = selectedStars))
        }
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
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri.value != null) {

            navController.navigate(
                Route.PhotoReview(
                    photoUri = photoUri.value.toString(),
                    poiId = poi.id,
                    poiName = poi.name,
                    calculatedPoints = calculatedPoints,
                    userEmail = currentUserEmail
                )
            )
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val file = File(context.externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
                val uri =
                    FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                photoUri.value = uri
                cameraLauncher.launch(uri)
            }
        }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).background(Color(0xFFF8F9FA))) {

        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            if (!savedPhotoUri.isNullOrEmpty()) {
                AsyncImage(
                    model = savedPhotoUri.toUri(),
                    contentDescription = poi.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE9ECEF)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Nessuna tua foto caricata", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier.statusBarsPadding().padding(12.dp).size(40.dp).background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = poi.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = { /* Azione opzionale se cliccato */ },
                    label = { Text("$visits Visite") },
                    //leadingIcon = { Icon(Icons.Default.Visibility, null, modifier = Modifier.size(16.dp)) }
                )

                SuggestionChip(
                    onClick = { /* Azione opzionale se cliccato */ },
                    label = { Text("${"%.2f".format(distance)} km") },
                    //leadingIcon = { Icon(Icons.Default.DirectionsCar, null, modifier = Modifier.size(16.dp)) }
                )

                SuggestionChip(
                    onClick = { /* Azione opzionale se cliccato */ },
                    label = { Text("$calculatedPoints pt") },
                    //leadingIcon = { Icon(Icons.Default.EmojiEvents, null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Descrizione:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = poi.description, style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(24.dp))

            if (isJustUploaded) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = BorderStroke(1.5.dp, Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Congratulazioni!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Hai completato lo scatto ottenendo +$calculatedPoints Punti!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFC8E6C9))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Valuta il posto:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)

                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < selectedStars) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clickable {
                                            selectedStars = index + 1
                                            scope.launch {
                                                poiStatus?.let {
                                                    poiDao.insertOrUpdatePoiStatus(it.copy(stars = index + 1))
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            } else {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = {
                            val gmmIntentUri = "google.navigation:q=${poi.lat},${poi.lng}".toUri()
                            context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri).setPackage("com.google.android.apps.maps"))
                        },
                        modifier = Modifier.weight(1f).height(54.dp),
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
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Text("POSTA", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
