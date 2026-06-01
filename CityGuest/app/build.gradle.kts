import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.example.cityguest"
    compileSdk = 37

    buildFeatures {
        compose = true //Attiva l'interfaccia da Compose
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.cityguest"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //serve per leggere le chiavi segrete contenute nel file locale
        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }
        // Carica la chiave delle Mappe di Google nel file AndroidManifest
        val mapsKey = properties.getProperty("MAPS_API_KEY") ?: ""

        manifestPlaceholders["MAPS_API_KEY"] = mapsKey

        // Carica l'ID Client di Google per permettere l'accesso con l'account Google
        val googleClientId = properties.getProperty("GOOGLE_CLIENT_ID") ?: "\"\""
        buildConfigField("String", "GOOGLE_CLIENT_ID", googleClientId)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    // Grafica di base e componenti standard (Jetpack Compose e Material 3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material.icons.extended)

    // Database locale (Room) per salvare i dati degli utenti sul telefono
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Gestione del cambio schermata (Navigation) e passaggio di dati tra di esse (Serialization)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.json.vx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Caricamento e visualizzazione delle immagini da internet (Coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.compose.v260)

    // Mappe di Google, geolocalizzazione del telefono e gestione dei permessi della posizione
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.maps.v1820)
    implementation(libs.play.services.location)
    implementation(libs.maps.utils.ktx)

    // Salvataggio delle preferenze (DataStore) e strumenti per l'accesso con Google
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)

    // Strumenti per i test di funzionamento dell'app
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

}