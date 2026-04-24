package com.example.cityguest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cityguest.navigation.Route
import com.example.cityguest.ui.theme.LoginScreen
import com.example.cityguest.ui.theme.RegisterScreen
import com.example.cityguest.viewmodel.LoginViewModel
import com.example.cityguest.viewmodel.RegisterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost( //contenitore schermate navigabili)
                navController = navController, //gestione navigazione tra schermate
                startDestination = Route.Login
            ) {
                composable<Route.Login> { //route -> UI reale, crea o recupera la schermata di VieModel e mostra la schermata di Login
                    val loginVm: LoginViewModel = viewModel()
                    LoginScreen(
                        viewModel = loginVm,
                        onNavigateToRegister = { navController.navigate(Route.Register) }, //quando si clicca registra si va alla schermata di registrazione
                        onLoginSuccess = {
                            navController.navigate(Route.Home) { // home
                                popUpTo(Route.Login) { inclusive = true } //cancella il login dallo stack
                            }
                        }
                    )
                }

                composable<Route.Register> {
                    val registerVm: RegisterViewModel = viewModel()
                    RegisterScreen(
                        viewModel = registerVm,
                        onNavigateBack = {
                            navController.popBackStack() // Torna semplicemente indietro
                        },
                        onRegisterSuccess = {
                            // Dopo la registrazione, si ritorna alla Home
                            navController.navigate(Route.Home) {
                                popUpTo(Route.Login) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Route.Home> {
                    Text("Benvenuto nella Home!")
                }
            }
        }
    }
}