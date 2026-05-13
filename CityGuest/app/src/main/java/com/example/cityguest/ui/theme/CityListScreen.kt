package com.example.cityguest.ui.theme

import androidx.compose.foundation.BorderStroke
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
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SELEZIONA CITTÀ",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            items(cities) { city ->

                val isUnlocked = userPoints >= city.requiredPoints

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable(enabled = isUnlocked) {
                            onCityClick(city.name)
                        },

                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (isUnlocked)
                                MaterialTheme.colorScheme.surfaceVariant
                            else
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
                    ),

                    border = BorderStroke(
                        width = 1.dp,
                        color =
                            if (isUnlocked)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                city.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            if (isUnlocked) {

                                Text(
                                    text = "Città sbloccata",
                                    color = Color(0xFF4CAF50),
                                    style = MaterialTheme.typography.bodySmall
                                )

                            } else {

                                Text(
                                    "Richiede ${city.requiredPoints} punti",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector =
                                if (isUnlocked)
                                    Icons.Default.LockOpen
                                else
                                    Icons.Default.Lock,

                            contentDescription = null,

                            tint = MaterialTheme.colorScheme.onBackground,

                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}