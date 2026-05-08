package com.example.cityguest.ui.theme

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cityguest.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    email: String,
    username: String,
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onSaveSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.initUser(email, username) }

    var passwordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    // URI temporaneo per la foto scattata dalla camera
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher per la GALLERIA
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.profileImageUri = it }
    }

    // Launcher per la FOTOCAMERA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            viewModel.profileImageUri = tempCameraUri
        }
    }

    // Funzione per creare un URI sicuro per la fotocamera
    fun getTempUri(): Uri {
        val tempFile = java.io.File.createTempFile("temp_image", ".jpg", context.externalCacheDir)
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // Deve corrispondere al Manifest
            tempFile
        )
    }

    // Dialog di scelta
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White,
            title = {

                Text(
                    text = "Foto Profilo",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {

                Text(
                    text = "Scegli come aggiornare la tua immagine",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Button(
                        onClick = {
                            val uri = getTempUri()
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Scatta una foto",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch("image/*")
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            "Scegli dalla galleria",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    TextButton(
                        onClick = { showDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Annulla",
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            dismissButton = null
        )
    }
    Scaffold(
        containerColor = Color(0xFFFBFBFB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(100.dp))

            // Immagine Profilo
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.clickable { showDialog = true }
            ) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    shadowElevation = 2.dp,
                    color = Color(0xFFF0F0F0)
                ) {
                    AsyncImage(
                        model = viewModel.profileImageUri ?: "",
                        contentDescription = "Foto profilo",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = android.R.drawable.ic_menu_camera),
                        fallback = painterResource(id = android.R.drawable.ic_menu_camera)
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = viewModel.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(30.dp))

            // Form Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "NOME",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.username = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "CAMBIO PASSWORD",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = viewModel.newPassword,
                    onValueChange = { viewModel.newPassword = it },
                    label = { Text("Cambia Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Tasto SALVA
            Button(
                onClick = {
                    viewModel.saveProfileChanges { newUsername ->
                        onSaveSuccess(newUsername)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("SALVA", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Tasto LOGOUT
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Color.Black),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text("LOGOUT", fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}