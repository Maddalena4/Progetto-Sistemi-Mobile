package com.example.cityguest.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Schermata introduttiva dell'applicazione.
 * Fornisce un semplice saluto di "BENVENUTO" accompagnato da un pulsante
 * di chiamata all'azione per iniziare la navigazione verso il flusso principale.
 *
 * @param onIniziaClick Callback invocato quando l'utente preme il pulsante "INIZIA".
 */
@Composable
fun HomeScreen(
    onIniziaClick: () ->Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "BENVENUTO",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onIniziaClick() },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.width(150.dp)
        ) {
            Text("INIZIA")
        }
    }
}