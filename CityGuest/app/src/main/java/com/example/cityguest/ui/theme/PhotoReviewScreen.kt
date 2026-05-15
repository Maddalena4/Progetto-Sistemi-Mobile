package com.example.cityguest.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cityguest.data.PoiDao
import com.example.cityguest.navigation.Route
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.example.cityguest.data.PoiStatus
import com.example.cityguest.data.UserDao
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

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val modelToLoad: Any = if (args.photoUri.startsWith("/")) {
                File(args.photoUri)
            } else {
                args.photoUri.toUri()
            }

            AsyncImage(
                model = modelToLoad,
                contentDescription = "Anteprima",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Text(
                text = args.poiName,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(24.dp)
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                        onClick = onRetry,
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color.Black),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                    ) {
                        Text("RIPROVA", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            scope.launch {

                                val currentStatus = poiDao.getPoiStatus(args.poiId, args.userEmail)
                                val newVisits = (currentStatus?.visits ?: 0) + 1
                                val currentStars = currentStatus?.stars ?: 0

                                val updatedStatus = PoiStatus(
                                    userEmail = args.userEmail,
                                    poiId = args.poiId,
                                    photoUri = args.photoUri,
                                    visits = newVisits,
                                    stars = currentStars
                                )
                                poiDao.insertOrUpdatePoiStatus(updatedStatus)

                                val currentUser = userDao.getUserByEmail(args.userEmail)
                                if (currentUser != null) {
                                    val updatedUser = currentUser.copy(
                                        points = currentUser.points + args.calculatedPoints
                                    )
                                    userDao.updateUser(updatedUser)
                                }

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
}