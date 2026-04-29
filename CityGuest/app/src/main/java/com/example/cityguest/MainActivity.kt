package com.example.cityguest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cityguest.data.AppDatabase
import com.example.cityguest.data.UserRepository
import com.example.cityguest.navigation.Route
import com.example.cityguest.ui.theme.LoginScreen
import com.example.cityguest.ui.theme.RegisterScreen
import com.example.cityguest.viewmodel.AppViewModelFactory
import com.example.cityguest.viewmodel.LoginViewModel
import com.example.cityguest.viewmodel.RegisterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inizializzazione del Database e del Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())

        // 2. Creazione della Factory per i ViewModel
        val factory = AppViewModelFactory(repository)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Route.Login
            ) {
                // Schermata di Login
                composable<Route.Login> {
                    // Passiamo la factory per iniettare il repository nel ViewModel
                    val loginVm: LoginViewModel = viewModel(factory = factory)
                    LoginScreen(
                        viewModel = loginVm,
                        onNavigateToRegister = { navController.navigate(Route.Register) },
                        onLoginSuccess = {
                            navController.navigate(Route.Home) {
                                popUpTo(Route.Login) { inclusive = true }
                            }
                        }
                    )
                }

                // Schermata di Registrazione
                composable<Route.Register> {
                    // Passiamo la factory anche qui
                    val registerVm: RegisterViewModel = viewModel(factory = factory)
                    RegisterScreen(
                        viewModel = registerVm,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onRegisterSuccess = {
                            navController.navigate(Route.Home) {
                                popUpTo(Route.Login) { inclusive = true }
                            }
                        }
                    )
                }

                // Schermata Home
                composable<Route.Home> {
                    Text("Benvenuto nella Home!")
                }
            }
        }
    }
}