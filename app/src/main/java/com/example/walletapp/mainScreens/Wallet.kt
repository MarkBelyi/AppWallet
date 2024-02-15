package com.example.walletapp.mainScreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.walletapp.Server.GetAPIString

@Composable
fun Wallet(){
    GetAPIString(con = LocalContext.current, "netlist/1")


}