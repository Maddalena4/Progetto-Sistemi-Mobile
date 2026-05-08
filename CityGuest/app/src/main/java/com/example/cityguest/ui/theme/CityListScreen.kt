package com.example.cityguest.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class CityData(val name: String, val requiredPoints: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    userPoints: Int = 0,
    onCityClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val cities = listOf(
        CityData("Forlì", 0),
        CityData("Roma", 500),
        CityData("Verona", 1000)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SELEZIONA CITTÀ") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            items(cities) { city ->
                val isUnlocked = userPoints >= city.requiredPoints

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable(enabled = isUnlocked) { onCityClick(city.name) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant
                        else Color.DarkGray.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(city.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            if (!isUnlocked) {
                                Text("Richiede ${city.requiredPoints} punti", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Icon(
                            imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }
        }
    }
}