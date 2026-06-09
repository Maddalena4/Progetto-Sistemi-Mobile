package com.example.cityguest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.example.cityguest.data.database.AppDatabase
import com.example.cityguest.data.poi.PoiData
import com.example.cityguest.data.poi.PoiVisit
import com.example.cityguest.data.points.PointsExpense
import com.example.cityguest.data.points.UnlockedCity
import com.example.cityguest.data.user.ThemeMode
import com.example.cityguest.data.user.UserRepository
import com.example.cityguest.navigation.Route
import com.example.cityguest.ui.components.LocationPermissionWrapper
import com.example.cityguest.ui.components.MainLayout
import com.example.cityguest.ui.screens.auth.LoginScreen
import com.example.cityguest.ui.screens.auth.RegisterScreen
import com.example.cityguest.ui.screens.gamification.BadgesScreen
import com.example.cityguest.ui.screens.gamification.FavoritesScreen
import com.example.cityguest.ui.screens.gamification.PhotoReviewScreen
import com.example.cityguest.ui.screens.gamification.PointTransaction
import com.example.cityguest.ui.screens.gamification.PointsHistoryScreen
import com.example.cityguest.ui.screens.gamification.RulesScreen
import com.example.cityguest.ui.screens.gamification.VisitedPlacesScreen
import com.example.cityguest.ui.screens.home.HomeScreen
import com.example.cityguest.ui.screens.map.CityListScreen
import com.example.cityguest.ui.screens.map.CityMapScreen
import com.example.cityguest.ui.screens.map.MapScreen
import com.example.cityguest.ui.screens.map.PoiDetailScreen
import com.example.cityguest.ui.screens.profile.ProfileScreen
import com.example.cityguest.ui.screens.profile.SettingsScreen
import com.example.cityguest.ui.theme.*
import com.example.cityguest.viewmodel.AppViewModelFactory
import com.example.cityguest.viewmodel.LoginViewModel
import com.example.cityguest.viewmodel.ProfileViewModel
import com.example.cityguest.viewmodel.RegisterViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import com.example.cityguest.viewmodel.SessionViewModel
/**
 * Funge da contenitore per l'intera interfaccia utente basata su Jetpack Compose.
 * Qui vengono istanziate le dipendenze globali (Database, Repository, Location Provider),
 * viene configurato il tema in base alle preferenze dell'utente e, soprattutto,
 * viene definito il `NavHost`, il cuore della navigazione dell'app, che definisce il grafo delle rotte
 * e gestisce le transizioni tra le viste con un approccio type-safe.
 */
class MainActivity : ComponentActivity() {
    // Sopprime il warning per i permessi di localizzazione, in quanto l'app gestisce
    // le autorizzazioni a runtime internamente tramite LocationPermissionWrapper.
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Abilita il disegno UI dietro le barre di sistema

        // Inizializzazione Dipendenze (Database, Repository, Factory)
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())
        val factory = AppViewModelFactory(repository, applicationContext)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val poiDao = database.poiDao()

        setContent {
            // Inizializza il ViewModel del Profilo, condiviso a livello globale per mantenere lo stato dell'utente
            val profileVm: ProfileViewModel = viewModel(factory = factory)

            // Gestione Tema
            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (profileVm.themeMode) {
                ThemeMode.DARK  -> true
                ThemeMode.LIGHT -> false
                ThemeMode.AUTO  -> systemDark
            }

            CityGuestTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Stati globali mantenuti a livello di MainActivity
                    val sessionVm: SessionViewModel = viewModel(factory = factory)
                    val loggedInUserEmail by sessionVm.userEmail.collectAsState()

                    var userLocation by remember { mutableStateOf<LatLng?>(null) }

                    // Funzione centralizzata per gestire il logout
                    val performLogout = {
                        sessionVm.logout()
                        navController.navigate(Route.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }

                    // Grafo di Navigazione
                    NavHost(
                        navController = navController,
                        startDestination = Route.Login
                    ) {

                        // Rotta: LOGIN
                        composable<Route.Login> {
                            val loginVm: LoginViewModel = viewModel(factory = factory)
                            LoginScreen(
                                viewModel = loginVm,
                                onNavigateToRegister = { navController.navigate(Route.Register) },
                                onLoginSuccess = { user ->
                                    sessionVm.login(user.email)
                                    navController.navigate(
                                        Route.Home(email = user.email, username = user.username)
                                    ) {
                                        popUpTo(Route.Login) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Rotta: REGISTRAZIONE
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

                        // Rotta: HOME PRINCIPALE
                        composable<Route.Home> { backStackEntry ->
                            val homeArgs = backStackEntry.toRoute<Route.Home>()
                            LaunchedEffect(homeArgs.email) {
                                sessionVm.login(homeArgs.email)
                                profileVm.initUser(homeArgs.email, homeArgs.username)
                            }

                            // Wrap con MainLayout per avere il Navigation Drawer e l'AppBar
                            MainLayout(
                                userEmail = homeArgs.email,
                                userName = profileVm.username.ifEmpty { homeArgs.username },
                                profileImageString = profileVm.profileImageUri?.toString(),
                                onLogout = performLogout,
                                onHomeClick = { },
                                onProfileClick = {
                                    navController.navigate(Route.Profile(homeArgs.email, homeArgs.username))
                                },
                                onMapClick = {
                                    navController.navigate(Route.Map(homeArgs.email, homeArgs.username))
                                },
                                onFavoritesClick = { navController.navigate(Route.Favorites(homeArgs.email)) },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(homeArgs.email)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) },
                                onBadgesClick = { navController.navigate(Route.Badges(homeArgs.email)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    HomeScreen(onIniziaClick = { navController.navigate(Route.CityList) })
                                }
                            }
                        }

                        // Rotta: LISTA CITTÀ
                        composable<Route.CityList> {
                            val userState by database.userDao()
                                .observeUserByEmail(loggedInUserEmail)
                                .collectAsState(initial = null)
                            val currentUserPoints = userState?.points ?: 0
                            val unlockedCities by database.userDao()
                                .observeUnlockedCities(loggedInUserEmail)
                                .collectAsState(initial = emptyList())
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
                                        // Verifica che l'utente abbia abbastanza punti per sbloccare la città
                                        if (currentUser != null && currentUser.points >= city.requiredPoints) {
                                            val updatedUser = currentUser.copy(
                                                points = currentUser.points - city.requiredPoints
                                            )
                                            // Aggiorna il saldo punti
                                            database.userDao().updateUser(updatedUser)
                                            // Salva la città sbloccata
                                            database.userDao().insertUnlockedCity(
                                                UnlockedCity(
                                                    userEmail = loggedInUserEmail,
                                                    cityName = city.name
                                                )
                                            )
                                            // Registra la spesa nello storico
                                            database.userDao().insertPointsExpense(
                                                PointsExpense(
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

                        // Rotta: MAPPA DELLA SINGOLA CITTÀ
                        composable<Route.CityMap> { backStackEntry ->
                            val mapArgs = backStackEntry.toRoute<Route.CityMap>()
                            val currentEmail = loggedInUserEmail.ifEmpty { profileVm.email }

                            // Cerca le coordinate di centro della città selezionata
                            val cityLocation = PoiData.pointsOfInterest
                                .find { it.imageRes.equals(mapArgs.cityName, ignoreCase = true) }?.location
                                ?: LatLng(41.9028, 12.4964)

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
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) },
                                onBadgesClick = { navController.navigate(Route.Badges(currentEmail)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    // Assicura che i permessi di geolocalizzazione siano concessi prima di renderizzare la mappa
                                    LocationPermissionWrapper {
                                        CityMapScreen(
                                            cityName = mapArgs.cityName,
                                            cityLocation = cityLocation,
                                            onInfoClick = { navController.navigate(Route.GameRules) },
                                            onPoiClick = { poi ->
                                                navController.navigate(
                                                    Route.PoiDetail(
                                                        id = poi.id,
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

                        // Rotta: STORICO PUNTI TRANSAZIONI
                        composable<Route.PointsHistory> { backStackEntry ->
                            val historyArgs = backStackEntry.toRoute<Route.PointsHistory>()
                            val userState by database.userDao()
                                .observeUserByEmail(historyArgs.email)
                                .collectAsState(initial = null)
                            val currentUserPoints = userState?.points ?: 0

                            // Aggrega le spese (città sbloccate) e i guadagni (visite POI)
                            val expensesState = database.userDao()
                                .observePointsExpenses(historyArgs.email)
                                .collectAsState(initial = emptyList())
                            val earningsState = database.userDao()
                                .observePointsEarnings(historyArgs.email)
                                .collectAsState(initial = emptyList())
                            val transactions = remember(expensesState.value, earningsState.value) {
                                val expenses = expensesState.value.map {
                                    PointTransaction(
                                        title = "Sbloccata: ${it.cityName}",
                                        points = it.pointsSpent,
                                        timestamp = it.timestamp,
                                        isExpense = true
                                    )
                                }
                                val earnings = earningsState.value.map {
                                    PointTransaction(
                                        title = "Visitato: ${it.poiName}",
                                        points = it.pointsEarned,
                                        timestamp = it.timestamp,
                                        isExpense = false
                                    )
                                }
                                // Ordina tutte le transazioni dalla più recente
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
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) },
                                onBadgesClick = { navController.navigate(Route.Badges(historyArgs.email)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    PointsHistoryScreen(
                                        transactions = transactions,
                                        totalPoints = currentUserPoints,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }

                        // Rotta: DETTAGLIO PUNTO DI INTERESSE (POI)
                        composable<Route.PoiDetail> { backStackEntry ->
                            val detailArgs = backStackEntry.toRoute<Route.PoiDetail>()

                            // Parametro passato indietro da PhotoReview per aggiornare la UI se è stata appena caricata una foto
                            val isJustUploaded = backStackEntry.savedStateHandle
                                .get<Boolean>("justUploaded") ?: false
                            val currentEmail = loggedInUserEmail.ifEmpty { profileVm.email }

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
                                },
                                onFavoritesClick = { navController.navigate(Route.Favorites(currentEmail)) },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(currentEmail)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) },
                                onBadgesClick = { navController.navigate(Route.Badges(currentEmail)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    // Aggiorna la posizione dell'utente per calcolare la distanza e verificare il check-in
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

                        // Rotta: REVISIONE FOTO
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
                                        // Registra lo storico della visita
                                        database.poiDao().insertPoiVisit(
                                            PoiVisit(
                                                userEmail = reviewArgs.userEmail,
                                                poiId = reviewArgs.poiId,
                                                poiName = reviewArgs.poiName,
                                                distanceKm = reviewArgs.distanceKm
                                            )
                                        )
                                    }
                                    // Notifica alla schermata di dettaglio che il caricamento è andato a buon fine
                                    navController.previousBackStackEntry?.savedStateHandle
                                        ?.set("justUploaded", true)
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Rotta: REGOLE DEL GIOCO
                        composable<Route.GameRules> {
                            RulesScreen(onBack = { navController.popBackStack() })
                        }

                        // Rotta: PROFILO UTENTE
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
                                onProfileClick = { },
                                onMapClick = {
                                    navController.navigate(Route.Map(profileArgs.email, profileArgs.username))
                                },
                                onFavoritesClick = { navController.navigate(Route.Favorites(currentEmail)) },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(currentEmail)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) },
                                onBadgesClick = { navController.navigate(Route.Badges(currentEmail)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    ProfileScreen(
                                        email = currentEmail,
                                        username = profileArgs.username,
                                        viewModel = profileVm,
                                        onLogout = performLogout,
                                        onSaveSuccess = { newName ->
                                            navController.navigate(
                                                Route.Home(email = currentEmail, username = newName)
                                            ) {
                                                popUpTo(
                                                    Route.Home(currentEmail, profileArgs.username)
                                                ) { inclusive = true }
                                            }
                                        },
                                        onSettingsClick = {
                                            navController.navigate(Route.Settings(currentEmail))
                                        }
                                    )
                                }
                            }
                        }

                        // Rotta: IMPOSTAZIONI
                        composable<Route.Settings> {
                            SettingsScreen(
                                currentTheme = profileVm.themeMode,
                                onThemeChange = { mode ->
                                    profileVm.saveThemeMode(mode)
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Rotta: SCHERMATA BADGES
                        composable<Route.Badges> { backStackEntry ->
                            val badgesArgs = backStackEntry.toRoute<Route.Badges>()
                            val currentEmail = loggedInUserEmail.ifEmpty { badgesArgs.email }
                            val userState by database.userDao()
                                .observeUserByEmail(currentEmail)
                                .collectAsState(initial = null)
                            val userPoints = userState?.points ?: 0

                            MainLayout(
                                userEmail = currentEmail,
                                userName = profileVm.username.ifEmpty { "Utente" },
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
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(currentEmail)) },
                                onBadgesClick = { }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    BadgesScreen(userPoints = userPoints)
                                }
                            }
                        }

                        // Rotta: STORICO LUOGHI VISITATI
                        composable<Route.VisitedPlaces> { backStackEntry ->
                            val visitedArgs = backStackEntry.toRoute<Route.VisitedPlaces>()
                            val visitsState = poiDao.observePoiVisits(visitedArgs.email)
                                .collectAsState(initial = emptyList())

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
                                onVisitedClick = { },
                                onBadgesClick = { navController.navigate(Route.Badges(visitedArgs.email)) }
                            ) {
                                VisitedPlacesScreen(
                                    visits = visitsState.value,
                                    onBack = { navController.popBackStack() },
                                    onPoiClick = { poiId ->
                                        // Cerca il POI nel dataset statico
                                        val poiReale = PoiData
                                            .pointsOfInterest
                                            .find { it.id == poiId }
                                        if (poiReale != null) {
                                            navController.navigate(
                                                Route.PoiDetail(
                                                    id = poiReale.id,
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

                        // Rotta: MAPPA GLOBALE
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
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) },
                                onBadgesClick = { navController.navigate(Route.Badges(currentEmail)) }
                            ) { innerPadding ->
                                Box(Modifier.padding(innerPadding)) {
                                    LocationPermissionWrapper {
                                        MapScreen()
                                    }
                                }
                            }
                        }

                        // Rotta: LUOGHI PREFERITI
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
                                onFavoritesClick = { },
                                onPointsHistoryClick = { navController.navigate(Route.PointsHistory(favArgs.email)) },
                                onVisitedClick = { navController.navigate(Route.VisitedPlaces(profileVm.email)) },
                                onBadgesClick = { navController.navigate(Route.Badges(favArgs.email)) }
                            ) {
                                FavoritesScreen(
                                    userEmail = favArgs.email,
                                    poiDao = poiDao,
                                    onPoiClick = { poiId ->
                                        val poiReale = PoiData
                                            .pointsOfInterest
                                            .find { it.id == poiId }
                                        if (poiReale != null) {
                                            navController.navigate(
                                                Route.PoiDetail(
                                                    id = poiReale.id,
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