plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.5.31"
}

android {
    namespace = "com.example.walletapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.walletapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.89"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    //Базовые настройки с которыми приложение запускается нормально
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.6")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("androidx.compose.material3:material3-android:1.2.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose-android:2.8.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //для horizontal pager
    implementation ("androidx.compose.foundation:foundation:1.6.8")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.30.1")

    //используется для изменения UI элементов самого андроида
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

    //ConstraintLayout to construct adaptive screens
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    //Seed phrase
    implementation("org.web3j:core:4.8.7-android")

    //Блюр
    implementation("com.github.skydoves:cloudy:0.1.2")

    //viewModel для отправки мнем фразы
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    //ROOM тут вот такая дата база
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")

    //Server
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.moshi:moshi:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")

    //JSON Parsing
    implementation("com.google.code.gson:gson:2.8.8")

    //LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.2")

    //QR generator and scanner
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.4.1")

    //CameraX
    implementation("androidx.camera:camera-core:1.3.2")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.camera:camera-lifecycle:1.3.2")
    implementation("androidx.camera:camera-view:1.3.2")
    implementation("com.google.mlkit:barcode-scanning:17.0.2")

    //Biometric
    implementation("androidx.biometric:biometric:1.1.0")

    // Required for compatibility
    implementation("androidx.fragment:fragment:1.6.2")

    // QR AdvancedVersion
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:16.1.3")

    //Animation
    implementation ("androidx.compose.animation:animation:1.6.8")
    implementation ("androidx.compose.ui:ui:1.6.8")
    implementation ("androidx.compose.runtime:runtime:1.6.8")
    implementation ("androidx.compose.animation:animation:1.6.8")

    //для horizontal pager
    implementation ("androidx.compose.foundation:foundation:1.6.8")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.30.1")

    //SwipeRefresh
    implementation (platform("androidx.compose:compose-bom:2024.06.00"))
}