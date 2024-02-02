package com.example.walletapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.walletapp.registrationScreens.CreateSeedPhraseScreen
import com.example.walletapp.registrationViewModel.RegistrationViewModel
import com.example.walletapp.ui.theme.WalletAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: RegistrationViewModel by viewModels()
        setContent {
            WalletAppTheme {
                CreateSeedPhraseScreen(viewModel = viewModel)
            }
        }
    }
}
