package com.example.walletapp

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.example.walletapp.registrationScreens.CreatePasswordScreen
import com.example.walletapp.registrationScreens.NewUserScreenColumn
import com.example.walletapp.repository.AppRepository
import com.example.walletapp.ui.theme.WalletAppTheme
import kotlinx.coroutines.launch

class MainApplication : Application(){
    var isInBackground=false // Применяется для понимания что приложение перешло в фоновый режим работы
    val database by lazy { DataBase.getDatabase(this) }
    val repository by lazy {
        AppRepository(
            database.signerDao(),
            database.networksDao(),
            database.walletsDao(),
            database.tokensDao(),
            database.balansDAO()
        )
    }
}

class MainActivity : AppCompatActivity() {
    //инициализируем viewModel
    private val appViewModel: appViewModel by viewModels {
        AppViewModelFactory((application as MainApplication).repository)
    }

    private val showAuthSheet = mutableStateOf(false)


    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) (application as MainApplication).isInBackground = true // Приложение перешло в фоновый режим работы
    }

    @OptIn(ExperimentalMaterial3Api::class)
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
                            viewModel = appViewModel)
                    }
                }
                
            }
        }
    }



    override fun onResume() {
        super.onResume()
        if((application as MainApplication).isInBackground) {
            requestAuth()
            (application as MainApplication).isInBackground=false}
    }

    fun requestAuth(){
        // TODO: тут сопсна можно спросить у юзера биометрию или пароль
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



