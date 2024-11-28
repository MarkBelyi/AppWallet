package com.example.walletapp.AuxiliaryFunctions.SealedClass

import com.example.walletapp.R

sealed class BottomBarTab(val route: String, val icon: Int, val label: Int) {

    data object Wallet : BottomBarTab("wallet", R.drawable.wallet_light, R.string.wallet_name)
    data object Home : BottomBarTab("home", R.drawable.home_2, R.string.home)
    data object Subscriptions :
        BottomBarTab("subscriptions", R.drawable.sign_light, R.string.requests)

}