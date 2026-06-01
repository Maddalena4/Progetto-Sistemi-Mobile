package com.example.cityguest.ui.screens.gamification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cityguest.data.poi.PoiDao
import com.example.cityguest.navigation.Route
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.example.cityguest.data.poi.PoiStatus
import com.example.cityguest.data.points.PointsEarning
import com.example.cityguest.data.user.UserDao
import java.io.File

@Composable
fun PhotoReviewScreen(
    args: Route.PhotoReview,
    poiDao: PoiDao,
    userDao: UserDao,
    onRetry: () -> Unit,
    onUploadSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val showDistanceError =  remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val modelToLoad: Any = if (args.photoUri.startsWith("/")) {
                File(args.photoUri)
            } else {
                args.photoUri.toUri()
            }

            AsyncImage(
                model = modelToLoad,
                contentDescription = "Anteprima Foto",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Vuoi caricare questa foto?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Guadagnerai +${args.calculatedPoints} punti!",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val file = File(args.photoUri.toUri().path ?: "")
                                if (file.exists()) file.delete()
                            } catch (e: Exception) { e.printStackTrace() }
                            onRetry()
                        },
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color.Black),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Text("RIPROVA", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {

                            //raggio massimo di tolleranza
                            val maxDistanceKm = 0.5f

                            if (args.distanceKm > maxDistanceKm) {
                                // Se l'utente è troppo lontano, mostriamo l'errore e fermiamo l'esecuzione
                                showDistanceError.value = true
                                return@Button
                            }
                            scope.launch {
                                val currentStatus = poiDao.getPoiStatus(args.poiId, args.userEmail)
                                val newVisits = (currentStatus?.visits ?: 0) + 1
                                val currentStars = currentStatus?.stars ?: 0
                                val currentFavorite = currentStatus?.isFavorite ?: false

                                val updatedStatus = PoiStatus(
                                    userEmail = args.userEmail,
                                    poiId = args.poiId,
                                    poiName = args.poiName,
                                    photoUri = args.photoUri,
                                    visits = newVisits,
                                    stars = currentStars,
                                    isFavorite = currentFavorite
                                )
                                poiDao.insertOrUpdatePoiStatus(updatedStatus)

                                val currentUser = userDao.getUserByEmail(args.userEmail)
                                if (currentUser != null) {
                                    val updatedUser = currentUser.copy(
                                        points = currentUser.points + args.calculatedPoints
                                    )
                                    userDao.updateUser(updatedUser)
                                }

                                userDao.insertPointsEarning(
                                    PointsEarning(
                                        userEmail = args.userEmail,
                                        poiName = args.poiName,
                                        pointsEarned = args.calculatedPoints
                                    )
                                )

                                onUploadSuccess()
                            }
                        },
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("CARICA", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                }
            }
        }
    }
    if (showDistanceError.value) {
        AlertDialog(
            onDismissRequest = { showDistanceError.value = false },
            title = { Text(text = "Sei troppo lontano!", fontWeight = FontWeight.Bold) },
            text = { Text("Sembra che tu non sia nelle vicinanze di ${args.poiName}. Avvicinati al luogo per poter confermare e caricare la tua visita.") },
            confirmButton = {
                TextButton(onClick = { showDistanceError.value = false }) {
                    Text("Ho capito", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }
}