package com.example.cityguest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cityguest.data.AppDatabase
import com.example.cityguest.data.UserRepository
import com.example.cityguest.navigation.Route
import com.example.cityguest.ui.components.MainLayout
import com.example.cityguest.ui.theme.HomeScreen
import com.example.cityguest.ui.theme.LoginScreen
import com.example.cityguest.ui.theme.RegisterScreen
import com.example.cityguest.viewmodel.AppViewModelFactory
import com.example.cityguest.viewmodel.LoginViewModel
import com.example.cityguest.viewmodel.RegisterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())

        val factory = AppViewModelFactory(repository)

        setContent {
            val navController = rememberNavController()

            NavHost(

                navController = navController,
                startDestination = Route.Login
            ) {
                composable<Route.Login> {
                    val loginVm: LoginViewModel = viewModel(factory = factory)
                    LoginScreen(
                        viewModel = loginVm,
                        onNavigateToRegister = { navController.navigate(Route.Register) },
                        onLoginSuccess = {
                                user ->

                            navController.navigate(
                                Route.Home(
                                    email = user.email,
                                    username = user.username
                                )
                            ) {
                                popUpTo(Route.Login) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Route.Register> {
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

                composable<Route.Home> {
                        backStackEntry ->

                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val username = backStackEntry.arguments?.getString("username") ?: ""

                    MainLayout(
                        userEmail = email,
                        userName = username,
                        onLogout = {
                            navController.navigate(Route.Login) {
                                popUpTo(Route.Home) { inclusive = true }
                            }
                        }
                    ) { innerPadding ->
                        Box(Modifier.padding(innerPadding)) {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }
}