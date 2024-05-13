package com.example.walletapp.appScreens.mainScreens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.walletapp.appViewModel.appViewModel

@Composable
fun ReceiveScreen(viewModel: appViewModel){
    val wallets by viewModel.allWallets.observeAsState(initial = emptyList())
}