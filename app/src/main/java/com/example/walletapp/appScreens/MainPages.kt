package com.example.walletapp.appScreens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.R
import com.example.walletapp.appScreens.mainScreens.Home
import com.example.walletapp.appScreens.mainScreens.Sign
import com.example.walletapp.appScreens.mainScreens.Wallet
import com.example.walletapp.appScreens.mainScreens.WalletDetailScreen
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.markAsVisitedApp
import com.example.walletapp.ui.theme.gradient
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.topRoundedShape

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
    onMatrixClick:()->Unit
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
        bottomBar = {
            AppBottomBar(bottomBarTabs, currentRoute, navController::navigate)
        }
    ) { padding ->
        NavHost(navController, startDestination = BottomBarTab.Home.route, Modifier.padding(padding)) {
            composable(BottomBarTab.Wallet.route) {
                Wallet(viewModel)
            }
            composable(BottomBarTab.Home.route) {
                Home(
                    viewModel,
                    onSettingsClick,
                    onShareClick,
                    onSignersClick,
                    onCreateWalletClick,
                    onMatrixClick
                )
            }
            composable(BottomBarTab.Subscriptions.route) {
                Sign(viewModel = viewModel)
            }
        }
    }

}




val actionItems = listOf(
        Triple("Перевести", R.drawable.send, Actions.send),
        Triple("Получить", R.drawable.receive, Actions.recieve),
        Triple("История", R.drawable.history_fill1_wght400_grad0_opsz24, Actions.history),
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

@Composable
fun AppBottomBar(
    bottomBarTabs: List<BottomBarTab>,
    currentRoute: String?,
    navigateToRoute: (String) -> Unit
) {
    NavigationBar(
        containerColor = colorScheme.surface,
        modifier = Modifier
            .shadow(
                elevation = 32.dp,
                shape = topRoundedShape,
                clip = true
            ),
    ) {
        bottomBarTabs.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = screen.label,
                        tint = if (isSelected) colorScheme.primary else colorScheme.primaryContainer,
                        modifier = Modifier.scale(1.2f)
                    )
                },
                label = { Text(
                    text = screen.label,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorScheme.primary
                    )
                ) },
                selected = currentRoute == screen.route,
                alwaysShowLabel = false,
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

sealed class BottomBarTab(val route: String, val icon: Int, val label: String) {
    object Wallet : BottomBarTab("wallet", R.drawable.wallet, "Кошелек")
    object Home : BottomBarTab("home", R.drawable.home, "Главная")
    object Subscriptions : BottomBarTab("subscriptions", R.drawable.sign, "Запросы")
}






