package com.example.walletapp.Screens.appScreens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.ENUM.Actions
import com.example.walletapp.AuxiliaryFunctions.Element.AppBottomBar
import com.example.walletapp.AuxiliaryFunctions.SealedClass.BottomBarTab
import com.example.walletapp.R
import com.example.walletapp.Screens.appScreens.mainScreens.Home
import com.example.walletapp.Screens.appScreens.mainScreens.Sign
import com.example.walletapp.Screens.appScreens.mainScreens.Wallet
import com.example.walletapp.markAsVisitedApp

val actionItems = listOf(
    Triple(R.string.action_transfer, R.drawable.send_light, Actions.Send),
    Triple(R.string.action_receive, R.drawable.receive_light, Actions.Receive),
    Triple(R.string.action_sign_history, R.drawable.sign_history, Actions.SignHistory),
    Triple(R.string.action_create_wallet, R.drawable.create_light, Actions.CreateWallet),
    Triple(R.string.action_buy_crypto, R.drawable.buy_light, Actions.BuyCrypto),
    Triple(R.string.action_tx_history, R.drawable.history_light, Actions.TxHistory),
    Triple(R.string.action_share_my_address, R.drawable.share_light, Actions.ShareMyAddress),
    Triple(R.string.action_signers, R.drawable.signers_light, Actions.Signers),
    Triple(R.string.action_co_signer, R.drawable.cosigner_light, Actions.CoSigner),
    Triple(R.string.support, R.drawable.support_light, Actions.Support),
    Triple(R.string.action_settings, R.drawable.settings, Actions.Settings),
    Triple(R.string.action_qr, R.drawable.qr_light, Actions.QR)
)

@Composable
fun MainPagesActivity(
    viewModel: appViewModel,
    onSettingsClick: () -> Unit,
    onShareClick: () -> Unit,
    onSignersClick: () -> Unit,
    onCreateWalletClick: () -> Unit,
    onMatrixClick: () -> Unit,
    onSend: (String) -> Unit,
    onReceive: () -> Unit,
    onSignHistory: () -> Unit,
    onPurchase: () -> Unit,
    onTxHistory: () -> Unit,
    onCreateSimpleWalletClick: () -> Unit,
) {
    val navController = rememberNavController()
    val bottomBarTabs =
        mutableListOf(BottomBarTab.Wallet, BottomBarTab.Home, BottomBarTab.Subscriptions)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        context.markAsVisitedApp()
    }

    Scaffold(
        containerColor = colorScheme.surface,
        bottomBar = {
            AppBottomBar(bottomBarTabs, currentRoute, navController::navigate)
        }
    ) { padding ->
        NavHost(
            navController,
            startDestination = BottomBarTab.Home.route,
            Modifier.padding(padding)
        ) {
            composable(BottomBarTab.Wallet.route) {
                Wallet(viewModel = viewModel, onCreateWalletClick)
            }
            composable(BottomBarTab.Home.route) {
                Home(
                    viewModel = viewModel,
                    onSettingsClick = onSettingsClick,
                    onShareClick = onShareClick,
                    onSignersClick = onSignersClick,
                    onCreateWalletClick = onCreateWalletClick,
                    onMatrixClick = onMatrixClick,
                    onSend = { onSend("") },
                    onReceive = onReceive,
                    onSignHistory = onSignHistory,
                    onPurchase = onPurchase,
                    onTxHistory = onTxHistory,
                    onCreateSimpleWalletClick = onCreateSimpleWalletClick
                )
            }
            composable(BottomBarTab.Subscriptions.route) {
                Sign(viewModel = viewModel)
            }
        }
    }

}









