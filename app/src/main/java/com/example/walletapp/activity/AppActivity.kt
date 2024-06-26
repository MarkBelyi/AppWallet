package com.example.walletapp.activity

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.walletapp.appScreens.MainPagesActivity
import com.example.walletapp.appScreens.mainScreens.AddSignerScreen
import com.example.walletapp.appScreens.mainScreens.CreateWalletScreen
import com.example.walletapp.appScreens.mainScreens.EditSigner
import com.example.walletapp.appScreens.mainScreens.HistoryScreen
import com.example.walletapp.appScreens.mainScreens.MatrixRain
import com.example.walletapp.appScreens.mainScreens.ReceiveScreen
import com.example.walletapp.appScreens.mainScreens.SendingScreens
import com.example.walletapp.appScreens.mainScreens.SettingsScreen
import com.example.walletapp.appScreens.mainScreens.ShareAddress
import com.example.walletapp.appScreens.mainScreens.SignersScreen
import com.example.walletapp.appViewModel.appViewModel

@Composable
fun AppActivity(
    activity: Activity,
    viewModel: appViewModel
){
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedSignerAddress by remember { mutableStateOf("") }
    var qrScanResult by remember { mutableStateOf<String?>(null) }

    fun switchToPage(index: Int) {
        selectedTabIndex = index
    }

    fun switchToPage(index: Int, address: String = "", qrResult: String? = null) {
        selectedTabIndex = index
        selectedSignerAddress = address
        qrScanResult = qrResult // Сохраняем результат сканирования QR, если он есть
    }

    BackHandler(
        onBack = {
            when (selectedTabIndex) {
                0 -> { activity.finish() }
                4, 8 -> { switchToPage(3) }
                else -> { switchToPage(0) }// в любой непонятной ситуации возвращаемся в кабинет
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        when(selectedTabIndex){
            0 -> MainPagesActivity(
                viewModel = viewModel,
                onSettingsClick = {switchToPage(7)},
                onShareClick = {switchToPage(2)},
                onSignersClick = {switchToPage(3)},
                onCreateWalletClick = {switchToPage(5)},
                onMatrixClick= {switchToPage(6)},
                onSend = {switchToPage(1)},
                onReceive = {switchToPage(9)},
                onHistory = {switchToPage(10)}
            )

            1 -> SendingScreens(
                viewModel = viewModel,
                onCreateClick = {switchToPage(5)},
                onBackClick = {switchToPage(0)}
            )

            2 -> ShareAddress(
                onBackClick = {switchToPage(0)}
            )

            3 -> SignersScreen(
                viewModel = viewModel,
                onCurrentSignerClick = { address -> switchToPage(4, address) },
                onAddSignerClick = {switchToPage(8)},
                onBackClick = {switchToPage(0)}
            )

            4 -> EditSigner(
                viewModel = viewModel,
                signerAddress = selectedSignerAddress,
                onSaveClick = {switchToPage(3)},
                onBackClick = {switchToPage(3)}
            )

            5 -> CreateWalletScreen(
                viewModel = viewModel,
                onCreateClick = {switchToPage(0)},
                onBackClick = {switchToPage(0)}
            )

            6 -> MatrixRain()

            7 -> SettingsScreen()

            8 -> AddSignerScreen(
                viewModel = viewModel,
                onBackClick = {switchToPage(3)}
            )
            9 -> ReceiveScreen(
                viewModel = viewModel,
                onCreateClick = { switchToPage(5) },
                onBackClick = { switchToPage(0) }
            )

            10 -> HistoryScreen(
                viewModel = viewModel,
                onSendingClick = {switchToPage(1)},
                onBackClick = {switchToPage(0)}
            )
        }
    }
}