package com.example.walletapp.appScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.R
import com.example.walletapp.appScreens.mainScreens.Home
import com.example.walletapp.appScreens.mainScreens.Sign
import com.example.walletapp.appScreens.mainScreens.Wallet
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.markAsVisitedApp

enum class Actions{
    send,
    recieve,
    history,
    createWallet,
    buyCrypto,
    exchangeCrypto,
    shareMyAddr,
    signers,
    coSigner,
    support,
    settings,
    QR
}
@Composable
fun MainPagesActivity(
    viewModel: appViewModel,
    onSettingsClick: () -> Unit,
    onShareClick : () -> Unit,
    onSignersClick: () -> Unit,
    onCreateWalletClick: () -> Unit,
    onMatrixClick:() -> Unit,
    onSend: () -> Unit,
    onReceive: () -> Unit,
    onHistory: () -> Unit,
){
    val navController = rememberNavController()
    val bottomBarTabs = mutableListOf(BottomBarTab.Wallet, BottomBarTab.Home, BottomBarTab.Subscriptions)
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
        NavHost(navController, startDestination = BottomBarTab.Home.route, Modifier.padding(padding)) {
            composable(BottomBarTab.Wallet.route) {
                Wallet(viewModel, onCreateWalletClick)
            }
            composable(BottomBarTab.Home.route) {
                Home(
                    viewModel,
                    onSettingsClick,
                    onShareClick,
                    onSignersClick,
                    onCreateWalletClick,
                    onMatrixClick,
                    onSend,
                    onReceive,
                    onHistory,
                    navController = navController,
                )
            }
            composable(BottomBarTab.Subscriptions.route) {
                Sign(viewModel = viewModel)
            }
        }
    }

}

val actionItems = listOf(
        Triple(R.string.action_transfer, R.drawable.send_light, Actions.send),
        Triple(R.string.action_receive, R.drawable.receive_light, Actions.recieve),
        Triple(R.string.action_history, R.drawable.history_light, Actions.history),
        Triple(R.string.action_create_wallet, R.drawable.create_light, Actions.createWallet),
        Triple(R.string.action_buy_crypto, R.drawable.buy_light, Actions.buyCrypto),
        Triple(R.string.action_exchange_crypto, R.drawable.exchange_light, Actions.exchangeCrypto),
        Triple(R.string.action_share_my_address, R.drawable.share_light, Actions.shareMyAddr),
        Triple(R.string.action_signers, R.drawable.signers_light, Actions.signers),
        Triple(R.string.action_co_signer, R.drawable.cosigner_light, Actions.coSigner),
        Triple(R.string.support, R.drawable.support_light, Actions.support),
        Triple(R.string.action_settings, R.drawable.settings, Actions.settings),
        Triple(R.string.action_qr, R.drawable.qr_light, Actions.QR)
)

@Composable
fun AppBottomBar(
    bottomBarTabs: List<BottomBarTab>,
    currentRoute: String?,
    navigateToRoute: (String) -> Unit
) {
    NavigationBar(
        containerColor = colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        bottomBarTabs.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = stringResource(id = screen.label),
                        modifier = Modifier.scale(1.4f),
                        tint = if (isSelected) colorScheme.primary else colorScheme.onSurface
                    )
                },
                label = { Text(
                    text = stringResource(id = screen.label),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    ),
                    color = if (isSelected) colorScheme.primary else colorScheme.onSurface
                ) },
                selected = currentRoute == screen.route,
                alwaysShowLabel = true,
                onClick = {
                    if (currentRoute != screen.route) {
                        navigateToRoute(screen.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = colorScheme.primary,
                    indicatorColor = colorScheme.surface
                )
            )
        }
    }
}

sealed class BottomBarTab(val route: String, val icon: Int, val label: Int) {

    data object Wallet : BottomBarTab("wallet", R.drawable.wallet_light, R.string.wallet_name)
    data object Home : BottomBarTab("home", R.drawable.home_2, R.string.home)
    data object Subscriptions : BottomBarTab("subscriptions", R.drawable.sign_light, R.string.requests)

}






