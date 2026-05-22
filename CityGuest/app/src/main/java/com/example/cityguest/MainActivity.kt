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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())
        val factory = AppViewModelFactory(repository)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val poiDao = database.poiDao()

        setContent {
            CityGuestTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val profileVm: ProfileViewModel = viewModel(factory = factory)
                    var userLocation by remember { mutableStateOf<LatLng?>(null) }

                    var loggedInUserEmail by remember { mutableStateOf("") }

                    val performLogout = {
                        loggedInUserEmail = ""
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
                                    loggedInUserEmail = user.email
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
                                loggedInUserEmail = homeArgs.email
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
                                },
                                onFavoritesClick = { navController.navigate(Route.Favorites(homeArgs.email)) },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(homeArgs.email)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    HomeScreen(onIniziaClick = { navController.navigate(Route.CityList) })
                                }
                            }
                        }

                        composable<Route.CityList> {
                            val userState by database.userDao().observeUserByEmail(loggedInUserEmail).collectAsState(initial = null)
                            val currentUserPoints = userState?.points ?: 0
                            val unlockedCities by database.userDao().observeUnlockedCities(loggedInUserEmail).collectAsState(initial = emptyList())
                            val scope = rememberCoroutineScope()

                            CityListScreen(
                                userPoints = currentUserPoints,
                                unlockedCities = unlockedCities,
                                onCityClick = { cityName ->
                                    navController.navigate(Route.CityMap(cityName))
                                },
                                onUnlockCity = { city ->
                                    scope.launch {
                                        val currentUser = userState
                                        if (currentUser != null && currentUser.points >= city.requiredPoints) {
                                            val updatedUser = currentUser.copy(points = currentUser.points - city.requiredPoints)
                                            database.userDao().updateUser(updatedUser)

                                            database.userDao().insertUnlockedCity(
                                                com.example.cityguest.data.UnlockedCity(
                                                    userEmail = loggedInUserEmail,
                                                    cityName = city.name
                                                )
                                            )

                                            database.userDao().insertPointsExpense(
                                                com.example.cityguest.data.PointsExpense(
                                                    userEmail = loggedInUserEmail,
                                                    cityName = city.name,
                                                    pointsSpent = city.requiredPoints,
                                                    timestamp = System.currentTimeMillis()
                                                )
                                            )
                                        }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable<Route.CityMap> { backStackEntry ->
                            val mapArgs = backStackEntry.toRoute<Route.CityMap>()
                            val currentEmail = loggedInUserEmail.ifEmpty { profileVm.email }
                            val cityLocation = com.example.cityguest.data.PoiData.pointsOfInterest
                                .find { it.imageRes.equals(mapArgs.cityName, ignoreCase = true) }?.location
                                ?: com.google.android.gms.maps.model.LatLng(41.9028, 12.4964)
                            MainLayout(
                                userEmail = currentEmail,
                                userName = profileVm.username,
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {
                                    navController.navigate(Route.Home(currentEmail, profileVm.username))
                                },
                                onProfileClick = {
                                    navController.navigate(Route.Profile(currentEmail, profileVm.username))
                                },
                                onMapClick = {
                                    navController.navigate(Route.Map(currentEmail, profileVm.username))
                                },
                                onFavoritesClick = { navController.navigate(Route.Favorites(currentEmail)) },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(currentEmail)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    LocationPermissionWrapper {
                                        CityMapScreen(
                                            cityName = mapArgs.cityName,
                                            cityLocation = cityLocation,
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
                                            },
                                            onBack = { navController.popBackStack() }
                                        )
                                    }
                                }
                            }
                        }

                        composable<Route.PointsHistory> { backStackEntry ->
                            val historyArgs = backStackEntry.toRoute<Route.PointsHistory>()

                            val expensesState = database.userDao().observePointsExpenses(historyArgs.email).collectAsState(initial = emptyList())
                            val earningsState = database.userDao().observePointsEarnings(historyArgs.email).collectAsState(initial = emptyList())

                            val transactions = remember(expensesState.value, earningsState.value) {
                                val expenses = expensesState.value.map {
                                    com.example.cityguest.ui.theme.PointTransaction(
                                        title = "Sbloccata: ${it.cityName}",
                                        points = it.pointsSpent,
                                        timestamp = it.timestamp,
                                        isExpense = true
                                    )
                                }
                                val earnings = earningsState.value.map {
                                    com.example.cityguest.ui.theme.PointTransaction(
                                        title = "Visitato: ${it.poiName}",
                                        points = it.pointsEarned,
                                        timestamp = it.timestamp,
                                        isExpense = false
                                    )
                                }
                                (expenses + earnings).sortedByDescending { it.timestamp }
                            }

                            MainLayout(
                                userEmail = historyArgs.email,
                                userName = profileVm.username.ifEmpty { "Utente" },
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {
                                    navController.navigate(Route.Home(historyArgs.email, profileVm.username))
                                },
                                onProfileClick = {
                                    navController.navigate(Route.Profile(historyArgs.email, profileVm.username))
                                },
                                onMapClick = {
                                    navController.navigate(Route.Map(historyArgs.email, profileVm.username))
                                },
                                onFavoritesClick = { navController.navigate(Route.Favorites(historyArgs.email)) },
                                onPointsHistoryClick = { },
                                onVisitedClick = {
                                    navController.navigate(Route.VisitedPlaces(profileVm.email))
                                }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    PointsHistoryScreen(
                                        transactions = transactions,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }

                        composable<Route.PoiDetail> { backStackEntry ->
                            val detailArgs = backStackEntry.toRoute<Route.PoiDetail>()
                            val isJustUploaded = backStackEntry.savedStateHandle.get<Boolean>("justUploaded") ?: false
                            val currentEmail = loggedInUserEmail.ifEmpty { profileVm.email }
                            MainLayout(
                                userEmail = profileVm.email,
                                userName = profileVm.username,
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = { navController.navigate(Route.Home(profileVm.email, profileVm.username)) },
                                onProfileClick = { navController.navigate(Route.Profile(profileVm.email, profileVm.username)) },
                                onMapClick = { navController.navigate(Route.Map(profileVm.email, profileVm.username)) },
                                onFavoritesClick = { navController.navigate(Route.Favorites(currentEmail)) },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(currentEmail)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    LocationPermissionWrapper {
                                        LaunchedEffect(Unit) {
                                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                                if (location != null) {
                                                    userLocation = LatLng(
                                                        location.latitude,
                                                        location.longitude
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                PoiDetailScreen(
                                    poi = detailArgs,
                                    userLocation = userLocation,
                                    poiDao = database.poiDao(),
                                    navController = navController,
                                    isJustUploaded = isJustUploaded,
                                    currentUserEmail = currentEmail,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                        composable<Route.PhotoReview> { backStackEntry ->
                            val reviewArgs = backStackEntry.toRoute<Route.PhotoReview>()
                            val scope = rememberCoroutineScope()

                            PhotoReviewScreen(
                                args = reviewArgs,
                                poiDao = database.poiDao(),
                                userDao = database.userDao(),
                                onRetry = { navController.popBackStack() },
                                onUploadSuccess = {
                                    scope.launch {
                                        database.poiDao().insertPoiVisit(
                                            com.example.cityguest.data.PoiVisit(
                                                userEmail = reviewArgs.userEmail,
                                                poiId = reviewArgs.poiId,
                                                poiName = reviewArgs.poiName,
                                                distanceKm = reviewArgs.distanceKm
                                            )
                                        )
                                    }
                                    navController.previousBackStackEntry?.savedStateHandle?.set("justUploaded", true)
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable<Route.GameRules> {
                            RulesScreen(onBack = { navController.popBackStack() })
                        }

                        composable<Route.Profile> { backStackEntry ->
                            val profileArgs = backStackEntry.toRoute<Route.Profile>()
                            val currentEmail = loggedInUserEmail.ifEmpty { profileArgs.email }

                            MainLayout(
                                userEmail = currentEmail,
                                userName = profileVm.username.ifEmpty { profileArgs.username },
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {
                                    navController.navigate(Route.Home(currentEmail, profileVm.username))
                                },
                                onProfileClick = {  },
                                onMapClick = {
                                    navController.navigate(Route.Map(profileArgs.email, profileArgs.username))
                                },
                                onFavoritesClick = { navController.navigate(Route.Favorites(currentEmail)) },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(currentEmail)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    ProfileScreen(
                                        email = currentEmail,
                                        username = profileArgs.username,
                                        viewModel = profileVm,
                                        onLogout = performLogout,
                                        onSaveSuccess = { newName ->
                                            navController.navigate(Route.Home(email = currentEmail, username = newName)) {
                                                popUpTo(Route.Home(currentEmail, profileArgs.username)) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        composable<Route.VisitedPlaces> { backStackEntry ->
                            val visitedArgs = backStackEntry.toRoute<Route.VisitedPlaces>()
                            val visitsState = poiDao.observePoiVisits(visitedArgs.email).collectAsState(initial = emptyList())
                            MainLayout(
                                userEmail = visitedArgs.email,
                                userName = profileVm.username.ifEmpty { "Utente" },
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {
                                    navController.navigate(Route.Home(visitedArgs.email, profileVm.username))
                                },
                                onProfileClick = {
                                    navController.navigate(Route.Profile(visitedArgs.email, profileVm.username))
                                },
                                onMapClick = {
                                    navController.navigate(Route.Map(visitedArgs.email, profileVm.username))
                                },
                                onFavoritesClick = { navController.navigate(Route.Favorites(visitedArgs.email)) },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(visitedArgs.email)) },
                                onVisitedClick = { }
                            ) { innerPadding ->
                                VisitedPlacesScreen(
                                    visits = visitsState.value,
                                    onBack = { navController.popBackStack() },
                                    onPoiClick = { poiId ->
                                        val poiReale =
                                            com.example.cityguest.data.PoiData.pointsOfInterest.find { it.id == poiId.toString() }

                                        if (poiReale != null) {
                                            navController.navigate(
                                                Route.PoiDetail(
                                                    id = poiReale.id.toInt(),
                                                    name = poiReale.name,
                                                    description = poiReale.description,
                                                    lat = poiReale.location.latitude.toFloat(),
                                                    lng = poiReale.location.longitude.toFloat(),
                                                    basePoints = poiReale.basePoints
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        composable<Route.Map> { backStackEntry ->
                            val mapArgs = backStackEntry.toRoute<Route.Map>()
                            val currentEmail = loggedInUserEmail.ifEmpty { mapArgs.email }

                            MainLayout(
                                userEmail = currentEmail,
                                userName = profileVm.username.ifEmpty { mapArgs.username },
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {
                                    navController.navigate(Route.Home(currentEmail, mapArgs.username))
                                },
                                onProfileClick = {
                                    navController.navigate(Route.Profile(currentEmail, mapArgs.username))
                                },
                                onMapClick = { },
                                onFavoritesClick = { navController.navigate(Route.Favorites(currentEmail)) },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(currentEmail)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    LocationPermissionWrapper {
                                        MapScreen()
                                    }
                                }
                            }
                        }

                        composable<Route.Favorites> { backStackEntry ->
                            val favArgs = backStackEntry.toRoute<Route.Favorites>()

                            MainLayout(
                                userEmail = favArgs.email,
                                userName = profileVm.username.ifEmpty { "Utente" },
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = {
                                    navController.navigate(Route.Home(favArgs.email, profileVm.username))
                                },
                                onProfileClick = {
                                    navController.navigate(Route.Profile(favArgs.email, profileVm.username))
                                },
                                onMapClick = {
                                    navController.navigate(Route.Map(favArgs.email, profileVm.username))
                                },
                                onFavoritesClick = {  },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(favArgs.email)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) }
                            ) { innerPadding ->

                                FavoritesScreen(
                                    userEmail = favArgs.email,
                                    poiDao = poiDao,
                                    onPoiClick = { poiId ->
                                        val poiReale = com.example.cityguest.data.PoiData.pointsOfInterest.find { it.id == poiId.toString() }
                                        if (poiReale != null) {
                                            navController.navigate(
                                                Route.PoiDetail(
                                                    id = poiReale.id.toInt(),
                                                    name = poiReale.name,
                                                    description = poiReale.description,
                                                    lat = poiReale.location.latitude.toFloat(),
                                                    lng = poiReale.location.longitude.toFloat(),
                                                    basePoints = poiReale.basePoints
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}