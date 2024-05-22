package com.example.walletapp

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.DataBase.DataBase
import com.example.walletapp.Settings.AuthModalBottomSheet
import com.example.walletapp.activity.AppActivity
import com.example.walletapp.activity.RegistrationActivity
import com.example.walletapp.appViewModel.AppViewModelFactory
import com.example.walletapp.appViewModel.RegistrationViewModel
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.repository.AppRepository
import com.example.walletapp.ui.theme.WalletAppTheme

class MainApplication : Application(){
    var isInBackground=false // Применяется для понимания что приложение перешло в фоновый режим работы
    val database by lazy { DataBase.getDatabase(this) }
    val repository by lazy {
        AppRepository(
            database.signerDao(),
            database.networksDao(),
            database.walletsDao(),
            database.tokensDao(),
            database.balansDAO(),
            database.TxDAO()
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
            WalletAppTheme {
                val registrationViewModel: RegistrationViewModel by viewModels()
                val navController = rememberNavController()
                val startDestination = if (hasVisitedApp()) "App" else "Registration"

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
                            viewModel = appViewModel
                        )
                    }
                }

                // Включаем AuthModalBottomSheet в основной тематический контент
                if (showAuthSheet.value) {
                    AuthModalBottomSheet(showAuthSheet, onAuthenticated = { showAuthSheet.value = false }, viewModel = appViewModel)
                }
            }
            requestAuth()
        }
    }



    override fun onResume() {
        super.onResume()
        if((application as MainApplication).isInBackground) {
            requestAuth()
            (application as MainApplication).isInBackground=false}
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            (application as MainApplication).isInBackground = true
        }
    }


    //Пример использования настройки
    fun Context.setContinuousAuthorizationEnabled(enabled: Boolean) {
        val sharedPrefs = getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean("continuous_authorization", enabled)
            apply()
        }
    }

    private fun requestAuth() {
        val sharedPreferences = getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
        val requireContinuousAuth = sharedPreferences.getBoolean("continuous_authorization", false)
        if (requireContinuousAuth) {
            showAuthSheet.value = true
        }
    }



    // Функция для определения, требуется ли повторная аутентификация
    private fun someConditionForReAuthentication(): Boolean {
        // Пример условия, вы можете определить свои собственные правила
        return true
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



