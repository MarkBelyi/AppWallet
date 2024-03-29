package com.example.walletapp.appScreens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.util.Pair
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
    onQRClick: () -> Unit,
    onShareClick : () -> Unit,
    onSignersClick: () -> Unit,
    onCreateWalletClick: () -> Unit,
    onMatrixClick:()->Unit
){

    val navController = rememberNavController()
    val screens = mutableListOf(Screen.Wallet, Screen.Home, Screen.Subscriptions)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        context.markAsVisitedApp()
    }


    Scaffold(
        containerColor = colorScheme.background,
        bottomBar = {
            AppBottomBar(screens, currentRoute, navController::navigate)
        }

    ) { padding ->
        NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(padding)) {
            composable(Screen.Wallet.route) {
                Wallet(viewModel)
            }
            composable(Screen.Home.route) {
                Home(
                    onSettingsClick,
                    onQRClick,
                    onShareClick,
                    onSignersClick,
                    onCreateWalletClick,
                    onMatrixClick
                )
            }
            composable(Screen.Subscriptions.route) {
                Sign(viewModel = viewModel)
            }
        }
    }
}


val actionItems = listOf(
        Triple("Перевести", R.drawable.send,Actions.send),
        Triple("Получить", R.drawable.receive,Actions.recieve),
        Triple("История", R.drawable.history_fill1_wght400_grad0_opsz24,Actions.history),
        Triple("Создать кошелек", R.drawable.add, Actions.createWallet),
        Triple("Купить", R.drawable.buy, Actions.buyCrypto),
        Triple("Обменять", R.drawable.excange, Actions.exchangeCrypto),
        Triple("Поделиться публичным ключем", R.drawable.share, Actions.shareMyAddr),
        Triple("Подписанты", R.drawable.signers, Actions.signers),
        Triple("Соподписант", R.drawable.sosigner, Actions.coSigner),
        Triple("Поддержка", R.drawable.headset_mic_fill1_wght400_grad0_opsz24, Actions.support),
        Triple("Настройки", R.drawable.settings, Actions.settings),
        Triple("QR", R.drawable.qr_code_scanner, Actions.QR)
    )


val cards =
    mutableListOf(
        "H2K",
        "Bitcoin",
        "Ethereum",
        "Tron",
        "Polygon"
    )

sealed class Screen(val route: String, val icon: Int, val label: String) {
    object Wallet : Screen("wallet", R.drawable.wallet, "Кошелек")
    object Home : Screen("home", R.drawable.home, "Главная")
    object Subscriptions : Screen("subscriptions", R.drawable.sign, "Запросы")
}



@Composable
fun AppBottomBar(
    screens: List<Screen>,
    currentRoute: String?,
    navigateToRoute: (String) -> Unit
) {
    NavigationBar(
        containerColor = colorScheme.background
    ) {
        screens.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.label,
                        tint = if (isSelected) colorScheme.primary else Color.LightGray,
                        modifier = Modifier.scale(1.2f)
                    )

                },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                alwaysShowLabel = false,
                onClick = {
                    if (currentRoute != screen.route) {
                        navigateToRoute(screen.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorScheme.primary,
                    unselectedIconColor = colorScheme.secondary,
                    selectedTextColor = colorScheme.primary,
                    unselectedTextColor = colorScheme.secondary,
                    indicatorColor = colorScheme.background
                )
            )
        }
    }
}