package com.example.cityguest.ui.screens.profile

import android.Manifest
import android.R
import android.content.pm.PackageManager
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.cityguest.utils.saveImageToInternalStorage
import com.example.cityguest.viewmodel.ProfileViewModel
import java.io.File

/**
 * Schermata dedicata alla visualizzazione e gestione del profilo utente.
 *
 * Fornisce all'utente gli strumenti per aggiornare la propria immagine di profilo,
 * modificare il proprio nome utente e cambiare la password.
 * Include i collegamenti per il logout e le impostazioni dell'app.
 *
 * @param email L'indirizzo email dell'utente attualmente connesso.
 * @param username Il nome utente corrente da visualizzare e potenzialmente modificare.
 * @param viewModel Il [ProfileViewModel] incaricato di gestire la logica di aggiornamento dei dati e lo stato della UI.
 * @param onLogout Callback invocato quando l'utente preme il pulsante di disconnessione rapida.
 * @param onSaveSuccess Callback invocato al termine di un salvataggio completato con successo.
 * @param onSettingsClick Callback invocato per navigare verso la schermata delle impostazioni.
 */
@Composable
fun ProfileScreen(
    email: String,
    username: String,
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onSaveSuccess: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.initUser(email, username) }

    var passwordVisible by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    var localUsername by remember { mutableStateOf(username) }
    var localPassword by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.username) {
        if (viewModel.username.isNotEmpty()) {
            localUsername = viewModel.username
        }
    }

    fun getTempUri(): Uri {
        val cacheDir = context.externalCacheDir ?: context.cacheDir
        val tempFile = File(cacheDir, "temp_profile_capture.jpg")
        if (tempFile.exists()) tempFile.delete()
        tempFile.createNewFile()
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val uniqueName = "profile_${email.replace("@", "").replace(".", "")}_${System.currentTimeMillis()}.jpg"
            val permanentPath = saveImageToInternalStorage(context, it, uniqueName)
            if (permanentPath != null) {
                viewModel.profileImageUri = Uri.fromFile(File(permanentPath))
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            val uniqueName = "profile_${email.replace("@", "").replace(".", "")}_${System.currentTimeMillis()}.jpg"
            val permanentPath = saveImageToInternalStorage(context, tempCameraUri!!, uniqueName)
            if (permanentPath != null) {
                viewModel.profileImageUri = Uri.fromFile(File(permanentPath))
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = getTempUri()
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPermission) {
                                val uri = getTempUri()
                                tempCameraUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                            showDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Scatta una foto", fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    }
                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch("image/*")
                            showDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("Scegli dalla galleria", fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    }
                    if (viewModel.profileImageUri != null) {
                        OutlinedButton(
                            onClick = {
                                viewModel.profileImageUri = null
                                showDialog.value = false
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            border = BorderStroke(1.5.dp, Color.Red)
                        ) {
                            Text("Rimuovi foto attuale", fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        }
                    }
                    TextButton(
                        onClick = { showDialog.value = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Annulla", color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }
            },
            dismissButton = null
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.clickable { showDialog.value = true }
                ) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        shadowElevation = 2.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        val currentUriString = viewModel.profileImageUri?.toString() ?: ""
                        val modelToLoad: Any = when {
                            currentUriString.startsWith("file://") -> File(viewModel.profileImageUri?.path ?: "")
                            currentUriString.startsWith("/") -> File(currentUriString)
                            currentUriString.isNotEmpty() -> viewModel.profileImageUri!!
                            else -> ""
                        }
                        AsyncImage(
                            model = modelToLoad,
                            contentDescription = "Foto profilo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.ic_menu_camera),
                            fallback = painterResource(id = R.drawable.ic_menu_camera)
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
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = viewModel.email, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(30.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "NOME",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = localUsername,
                        onValueChange = { localUsername = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "CAMBIO PASSWORD",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = localPassword,
                        onValueChange = { localPassword = it },
                        label = { Text("Nuova Password") },
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
                    if (viewModel.errorMessage != null) {
                        Text(
                            text = viewModel.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        viewModel.username = localUsername
                        viewModel.newPassword = localPassword
                        viewModel.saveProfileChanges { newUsername ->
                            localPassword = ""
                            viewModel.newPassword = ""
                            onSaveSuccess(newUsername)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("SALVA", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallFloatingActionButton(
                        onClick = onLogout,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.error,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    SmallFloatingActionButton(
                        onClick = onSettingsClick,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Impostazioni",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}