package com.example.walletapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.walletapp.registrationScreens.CreatePasswordScreen
import com.example.walletapp.registrationScreens.NewUserPage
import com.example.walletapp.ui.theme.WalletAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletAppTheme {
                CreatePasswordScreen()
            }
        }
    }
}
