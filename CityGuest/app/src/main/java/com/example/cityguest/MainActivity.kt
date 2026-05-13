package com.example.cityguest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.cityguest.data.AppDatabase
import com.example.cityguest.data.UserRepository
import com.example.cityguest.navigation.Route
import com.example.cityguest.ui.components.LocationPermissionWrapper
import com.example.cityguest.ui.components.MainLayout
import com.example.cityguest.ui.theme.*
import com.example.cityguest.viewmodel.AppViewModelFactory
import com.example.cityguest.viewmodel.LoginViewModel
import com.example.cityguest.viewmodel.ProfileViewModel
import com.example.cityguest.viewmodel.RegisterViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())
        val factory = AppViewModelFactory(repository)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            CityGuestTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val profileVm: ProfileViewModel = viewModel(factory = factory)
                    var userLocation by remember { mutableStateOf<LatLng?>(null) }

                    val performLogout = {
                        navController.navigate(Route.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Route.Login
                    ) {
                        composable<Route.Login> {
                            val loginVm: LoginViewModel = viewModel(factory = factory)
                            LoginScreen(
                                viewModel = loginVm,
                                onNavigateToRegister = { navController.navigate(Route.Register) },
                                onLoginSuccess = { user ->
                                    navController.navigate(
                                        Route.Home(email = user.email, username = user.username)
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
                                onNavigateBack = { navController.popBackStack() },
                                onRegisterSuccess = {
                                    navController.navigate(Route.Login) {
                                        popUpTo(Route.Login) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<Route.Home> { backStackEntry ->
                            val homeArgs = backStackEntry.toRoute<Route.Home>()
                            LaunchedEffect(homeArgs.email) {
                                profileVm.initUser(homeArgs.email, homeArgs.username)
                            }
                            MainLayout(
                                userEmail = homeArgs.email,
                                userName = profileVm.username.ifEmpty { homeArgs.username },
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {  },
                                onProfileClick = {
                                    navController.navigate(Route.Profile(homeArgs.email, homeArgs.username))
                                },
                                onMapClick = {
                                    navController.navigate(Route.Map(homeArgs.email, homeArgs.username))
                                }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    HomeScreen(onIniziaClick = { navController.navigate(Route.CityList) })
                                }
                            }
                        }

                        composable<Route.CityList> {
                            CityListScreen(
                                userPoints = 0,
                                onCityClick = { cityName ->
                                    navController.navigate(Route.CityMap(cityName))
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable<Route.CityMap> { backStackEntry ->
                            val mapArgs = backStackEntry.toRoute<Route.CityMap>()

                            MainLayout(
                                userEmail = profileVm.email,
                                userName = profileVm.username,
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {
                                    navController.navigate(Route.Home(profileVm.email, profileVm.username))
                                },
                                onProfileClick = {
                                    navController.navigate(Route.Profile(profileVm.email, profileVm.username))
                                },
                                onMapClick = {
                                    navController.navigate(Route.Map(profileVm.email, profileVm.username))
                                }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    LocationPermissionWrapper {
                                        CityMapScreen(
                                            cityName = mapArgs.cityName,
                                            onInfoClick = {
                                                navController.navigate(Route.GameRules)
                                            },
                                            onPoiClick = { poi ->
                                                navController.navigate(
                                                    Route.PoiDetail(
                                                        id = poi.id.toInt(),
                                                        name = poi.name,
                                                        description = poi.description,
                                                        lat = poi.location.latitude.toFloat(),
                                                        lng = poi.location.longitude.toFloat(),
                                                        basePoints = poi.basePoints
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        composable<Route.PoiDetail> { backStackEntry ->
                            val detailArgs = backStackEntry.toRoute<Route.PoiDetail>()

                            MainLayout(
                                userEmail = profileVm.email,
                                userName = profileVm.username,
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = { navController.navigate(Route.Home(profileVm.email, profileVm.username)) },
                                onProfileClick = { navController.navigate(Route.Profile(profileVm.email, profileVm.username)) },
                                onMapClick = { navController.navigate(Route.Map(profileVm.email, profileVm.username)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    LocationPermissionWrapper {
                                        LaunchedEffect(Unit) {
                                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                                if (location != null) {
                                                    userLocation = LatLng(location.latitude, location.longitude)
                                                }
                                            }
                                        }

                                        PoiDetailScreen(
                                            poi = detailArgs,
                                            userLocation = userLocation,
                                            onBack = { navController.popBackStack() }
                                        )
                                    }
                                }
                            }
                        }

                        composable<Route.GameRules> {
                            RulesScreen(onBack = { navController.popBackStack() })
                        }

                        composable<Route.Profile> { backStackEntry ->
                            val profileArgs = backStackEntry.toRoute<Route.Profile>()

                            MainLayout(
                                userEmail = profileArgs.email,
                                userName = profileVm.username.ifEmpty { profileArgs.username },
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {
                                    navController.navigate(Route.Home(profileArgs.email, profileVm.username))
                                },
                                onProfileClick = {  },
                                onMapClick = {
                                    navController.navigate(Route.Map(profileArgs.email, profileArgs.username))
                                }
                            ) { innerPadding ->
                                ProfileScreen(
                                    email = profileArgs.email,
                                    username = profileArgs.username,
                                    viewModel = profileVm,
                                    onLogout = performLogout,
                                    onSaveSuccess = { newName ->
                                        navController.navigate(Route.Home(email = profileArgs.email, username = newName)) {
                                            popUpTo(Route.Home(profileArgs.email, profileArgs.username)) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }

                        composable<Route.Map> { backStackEntry ->
                            val mapArgs = backStackEntry.toRoute<Route.Map>()

                            MainLayout(
                                userEmail = mapArgs.email,
                                userName = profileVm.username.ifEmpty { mapArgs.username },
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {
                                    navController.navigate(Route.Home(mapArgs.email, mapArgs.username))
                                },
                                onProfileClick = {
                                    navController.navigate(Route.Profile(mapArgs.email, mapArgs.username))
                                },
                                onMapClick = { }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    LocationPermissionWrapper {
                                        MapScreen()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}