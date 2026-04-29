package com.example.cityguest.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cityguest.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel, /*stato e logica*/
    onNavigateToRegister: () -> Unit, /*cosa fare quando clicchi registrati*/
    onLoginSuccess: () -> Unit /*cosa fare dopo login riuscito*/
) {
    Column( //elementi in verticale
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Surface(
            modifier = Modifier.size(80.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer
        ) { }

        Spacer(modifier = Modifier.height(16.dp))
        Text("CityGuest", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Benvenuto/a!", color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(32.dp))

        /*legge lo stato e aggiorna lo stato*/
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(), //nasconde i caratteri
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onLoginClick(onLoginSuccess) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("ACCEDI")
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("Non hai un account? Registrati")
        }
    }
}