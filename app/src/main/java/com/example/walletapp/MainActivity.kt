package com.example.walletapp

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.Activity.AppActivity
import com.example.walletapp.Activity.RegistrationActivity
import com.example.walletapp.Activity.SignerModeActivity
import com.example.walletapp.AppViewModel.AppViewModelFactory
import com.example.walletapp.AppViewModel.RegistrationViewModel
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.DataBase.DataBase
import com.example.walletapp.Repository.AppRepository
import com.example.walletapp.Settings.AuthModalBottomSheet
import com.example.walletapp.ui.theme.WalletAppTheme

class MainApplication : Application() {
    var isInBackground =
        false // Применяется для понимания что приложение перешло в фоновый режим работы
    val database by lazy { DataBase.getDatabase(this) }
    val repository by lazy {
        AppRepository(
            database.signerDao(),
            database.networksDao(),
            database.walletsDao(),
            database.tokensDao(),
            database.balansDAO(),
            database.TxDAO(),
            database.allTXDAO(),
            database.walletAddressDao()
        )
    }
}

class MainActivity : AppCompatActivity() {
    //инициализируем viewModel
    private val appViewModel: appViewModel by viewModels {
        AppViewModelFactory((application as MainApplication).repository, application)
    }

    private val showAuthSheet = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkTheme by appViewModel.isDarkTheme.observeAsState(false)

            WalletAppTheme(isDarkTheme) {
                val registrationViewModel: RegistrationViewModel by viewModels()
                val navController = rememberNavController()
                val startDestination = if (hasVisitedApp()) {
                    if (getElectronicApprovalEnabled()) {
                        "SignerMode"
                    } else "App"
                } else {
                    "Registration"
                }

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("Registration") {
                        RegistrationActivity(
                            activity = this@MainActivity,
                            navHostController = navController,
                            viewModelReg = registrationViewModel,
                            viewModelApp = appViewModel
                        )
                    }
                    composable("App") {
                        AppActivity(
                            activity = this@MainActivity,
                            viewModel = appViewModel,
                            navHostController = navController,
                        )
                    }
                    composable("SignerMode") {
                        SignerModeActivity(
                            activity = this@MainActivity,
                            navHostController = navController,
                            viewModel = appViewModel
                        )
                    }

                }

                if (showAuthSheet.value) {
                    AuthModalBottomSheet(
                        showAuthSheet,
                        onAuthenticated = { showAuthSheet.value = false },
                        viewModel = appViewModel
                    )
                }
            }
            requestAuth()
        }
    }


    /*override fun onResume() {
        super.onResume()
        if((application as MainApplication).isInBackground) {
            requestAuth()
            (application as MainApplication).isInBackground=false}
    }*/

    override fun onStop() {
        super.onStop()
        val app = application as MainApplication
        if (!app.isInBackground) {
            app.isInBackground = true
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            (application as MainApplication).isInBackground = true
        }
    }

    private fun getElectronicApprovalEnabled(): Boolean {
        val sharedPrefs = getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("electronic_approval", false)
    }

    private fun requestAuth() {
        val sharedPreferences = getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
        val requireContinuousAuth = sharedPreferences.getBoolean("continuous_authorization", false)
        if (requireContinuousAuth) {
            showAuthSheet.value = true
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



