plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tunipromos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tunipromos"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    // Dépendances de base
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Glide pour images
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // CardView et CoordinatorLayout
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase via BoM
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // SERVICES Firebase GRATUITS (tous dans le plan Spark gratuit) :
    implementation("com.google.firebase:firebase-auth")        // Authentification
    implementation("com.google.firebase:firebase-firestore")   // Base de données
    implementation("com.google.firebase:firebase-storage")     // Stockage fichiers
    implementation("com.google.firebase:firebase-messaging")   // Notifications push (GRATUIT)
    implementation("com.google.firebase:firebase-analytics")   // Analytics (GRATUIT)

    // ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4")
}