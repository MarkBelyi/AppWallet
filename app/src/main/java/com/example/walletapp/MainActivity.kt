package com.example.walletapp

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.DataBase.DataBase
import com.example.walletapp.activity.AppActivity
import com.example.walletapp.activity.RegistrationActivity
import com.example.walletapp.appScreens.mainScreens.CreateWalletScreen
import com.example.walletapp.appViewModel.AppViewModelFactory
import com.example.walletapp.appViewModel.RegistrationViewModel
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.repository.AppRepository
import com.example.walletapp.ui.theme.WalletAppTheme

class MainApplication : Application(){
    val database by lazy { DataBase.getDatabase(this) }
    val repository by lazy {
        AppRepository(
            database.signerDao(),
            database.networksDao(),
            database.walletsDao(),
            database.tokensDao()
        )
    }
}

class MainActivity : ComponentActivity() {

    //инициализируем viewModel
    private val appViewModel: appViewModel by viewModels {
        AppViewModelFactory((application as MainApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WalletAppTheme {
                val registrationViewModel: RegistrationViewModel by viewModels()
                val navController = rememberNavController()
                val startDestination = if (hasVisitedApp()) "App" else "Registration"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("Registration") {
                        RegistrationActivity(activity = this@MainActivity, navHostController = navController, viewModelReg = registrationViewModel, viewModelApp = appViewModel)
                    }
                    composable("App") {
                        AppActivity(this@MainActivity, viewModel = appViewModel)
                    }
                }

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





