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

@Composable
fun MainPagesActivity(
    viewModel: appViewModel,
    /*onSettingClick: () -> Unit,*/
    onQRClick: () -> Unit,
    onShareClick : () -> Unit,
    onSignersClick: () -> Unit,
    onCreateWalletClick: () -> Unit
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
                    /*onSettingClick,*/
                    onQRClick,
                    onShareClick,
                    onSignersClick,
                    onCreateWalletClick,
                )
            }
            composable(Screen.Subscriptions.route) {
                Sign()
            }
        }
    }
}


val actionItems =
    mutableListOf(
        Pair("Перевести", R.drawable.send),
        Pair("Получить", R.drawable.receive),
        Pair("История", R.drawable.history_fill1_wght400_grad0_opsz24),
        Pair("Создать кошелек", R.drawable.add),
        Pair("Купить", R.drawable.buy),
        Pair("Обменять", R.drawable.excange),
        Pair("Поделиться публичным ключем", R.drawable.share),
        Pair("Подписанты", R.drawable.signers),
        Pair("Соподписант", R.drawable.sosigner),
        Pair("Поддержка", R.drawable.headset_mic_fill1_wght400_grad0_opsz24),
        Pair("Настройки", R.drawable.settings),
        Pair("QR", R.drawable.qr_code_scanner)
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