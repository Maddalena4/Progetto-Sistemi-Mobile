package com.example.cityguest.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RulesScreen(onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Regole del Gioco", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            Text("1. Seleziona una città sbloccata.\n" +
                    "2. Naviga sulla mappa per trovare i punti di interesse.\n" +
                    "3. Raggiungi fisicamente i luoghi per ottenere punti.\n" +
                    "4. Accumula punti per sbloccare Roma, Verona e altre città!",
                style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("HO CAPITO")
            }
        }
    }
}