package com.example.cityguest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

/**
 * Layout strutturale principale dell'applicazione che centralizza i componenti di navigazione globali.
 * Integra un Navigation Drawer (menu laterale), una TopAppBar (barra superiore) e una NavigationBar (barra inferiore).
 *
 * @param userEmail Email dell'utente corrente (visualizzata nel drawer).
 * @param userName Username dell'utente corrente.
 * @param profileImageString URI o URL dell'immagine del profilo per il caricamento asincrono.
 * @param onLogout Callback invocata al click del pulsante di disconnessione.
 * @param onProfileClick Callback per navigare alla sezione profilo.
 * @param onHomeClick Callback per navigare alla Dashboard/Home.
 * @param onMapClick Callback per navigare alla mappa interattiva.
 * @param onFavoritesClick Callback per la sezione dei preferiti.
 * @param onPointsHistoryClick Callback per visualizzare il registro dei punti.
 * @param onVisitedClick Callback per visualizzare la cronologia dei luoghi visitati.
 * @param onBadgesClick Callback per la sezione dei trofei/badge.
 * @param content Contenuto specifico della pagina corrente che verrà iniettato all'interno del container.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    userEmail: String,
    userName: String,
    profileImageString: String? = null,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onPointsHistoryClick: () -> Unit,
    onVisitedClick: () -> Unit,
    onBadgesClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    // Gestione dello stato del pannello laterale a scomparsa (Drawer)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Scope coroutine necessario per l'apertura e chiusura animata e asincrona del drawer
    val scope = rememberCoroutineScope()

    //implementa il menu laterale a scorrimento (ModalNavigationDrawer)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(24.dp)) {
                    AsyncImage(
                        model = profileImageString?.takeIf { it.isNotEmpty() } ?: "",
                        contentDescription = "Foto Profilo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = android.R.drawable.ic_menu_gallery),
                        fallback = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(userEmail, style = MaterialTheme.typography.bodySmall)
                    Text(userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider()

                // Generazione dei singoli tab di navigazione rapidi (Home, Mappa, Registro Punti, Profilo)
                NavigationDrawerItem(
                    label = { Text("Luoghi visitati") },
                    selected = false,
                    icon = { Icon(Icons.Default.LocationOn, null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVisitedClick()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Luoghi preferiti") },
                    selected = false,
                    icon = { Icon(Icons.Default.Favorite, null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onFavoritesClick()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("I tuoi badge") },
                    selected = false,
                    icon = { Icon(Icons.Default.MilitaryTech, null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onBadgesClick()
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                    onClick = onLogout
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("City Quest", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                        selected = false,
                        onClick = onHomeClick
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Place, null) },
                        label = { Text("Mappa", style = MaterialTheme.typography.labelSmall) },
                        selected = false,
                        onClick = onMapClick
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.EmojiEvents, null) },
                        label = { Text("Punti", style = MaterialTheme.typography.labelSmall) },
                        selected = false,
                        onClick = onPointsHistoryClick
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("Profilo", style = MaterialTheme.typography.labelSmall) },
                        selected = false,
                        onClick = onProfileClick
                    )
                }
            },
            content = content
        )
    }
}