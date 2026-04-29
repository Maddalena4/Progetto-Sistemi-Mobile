package com.example.cityguest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    userEmail: String,
    userName: String,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope() // Per aprire il menu con un bottone

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header del Menu (Mockup 4)
                Column(modifier = Modifier.padding(24.dp)) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(64.dp))
                    Text(userEmail, style = MaterialTheme.typography.bodySmall)
                    Text(userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider()

                // Voci del menu
                NavigationDrawerItem(
                    label = { Text("Luoghi visitati") },
                    selected = false,
                    icon = { Icon(Icons.Default.LocationOn, null) },
                    onClick = { }
                )
                NavigationDrawerItem(
                    label = { Text("Luoghi preferiti") },
                    selected = false,
                    icon = { Icon(Icons.Default.Favorite, null) },
                    onClick = { }
                )
                NavigationDrawerItem(
                    label = { Text("Punti Guadagnati") },
                    selected = false,
                    icon = { Icon(Icons.Default.Star, null) },
                    onClick = { }
                )

                Spacer(modifier = Modifier.weight(1f)) // Spinge il logout in basso

                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(Icons.Default.ExitToApp, null) },
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
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, null) },
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, null) },
                        selected = false,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Notifications, null) },
                        selected = false,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, null) },
                        selected = false,
                        onClick = { }
                    )
                }
            },
            content = content
        )
    }
}