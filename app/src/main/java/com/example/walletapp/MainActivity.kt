package com.example.walletapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room.databaseBuilder
import com.example.walletapp.DataBase.DataBase
import com.example.walletapp.DataBase.SignerData.SignerViewModel
import com.example.walletapp.activity.AppActivity
import com.example.walletapp.activity.RegistrationActivity
import com.example.walletapp.registrationScreens.NewUserScreenColumn
import com.example.walletapp.registrationViewModel.RegistrationViewModel
import com.example.walletapp.ui.theme.WalletAppTheme

class MainActivity : ComponentActivity() {

    private val db by lazy {
        databaseBuilder(
            applicationContext,
            DataBase::class.java,
            "database.db"
        ).build()
    }
    private val viewModelDB by viewModels<SignerViewModel> (
        factoryProducer = {
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SignerViewModel(db.dao) as T
                }
            }
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelReg: RegistrationViewModel by viewModels()



        val startDestination = if (hasVisitedApp()) "App" else "Registration"

        setContent {
            val navController = rememberNavController()
            val state by viewModelDB.state.collectAsState()

            WalletAppTheme {

                NavHost(navController, startDestination = startDestination) {
                    composable("Registration"){
                        RegistrationActivity(activity = this@MainActivity, navHostController = navController, viewModel = viewModelReg)
                    }
                    composable("App"){
                        AppActivity(this@MainActivity, state = state, onEvent = viewModelDB::onEvent)
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

/*
fun fetchAndSaveNetworks(
    context: Context,
    */
/*dao: NetworksDAO,
    restoreCredentials: Credentials*//*

): String {
    val ps = PasswordStorageHelper(context)
    ps.setData("MyPrivateKey", restoreCredentials.ecKeyPair.privateKey.toByteArray())
    ps.setData("MyPublicKey", restoreCredentials.ecKeyPair.publicKey.toByteArray() )
    val s = GetAPIString(context, api = "netlist/1")
    return s
}*/
