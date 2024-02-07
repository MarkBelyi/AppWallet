package com.example.walletapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.activity.RegistrationActivity
import com.example.walletapp.registrationViewModel.RegistrationViewModel
import com.example.walletapp.ui.theme.WalletAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: RegistrationViewModel by viewModels()


        //val startDestination = if (hasVisitedApp()) "App" else "Registration"

        setContent {
            val navController = rememberNavController()
            WalletAppTheme {
                RegistrationActivity(activity = this@MainActivity, navHostController = navController, viewModel = viewModel)
            }
        }
    }
}


fun Context.markAsVisitedApp() {
    val sharedPrefs = getSharedPreferences("com.example.h2k.PREFS", Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean("VisitedApp", true).apply()
}

fun Context.hasVisitedApp(): Boolean {
    val sharedPrefs = getSharedPreferences("com.example.h2k.PREFS", Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean("VisitedApp", false)
}
