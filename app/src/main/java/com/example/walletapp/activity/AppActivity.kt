package com.example.walletapp.activity

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.walletapp.MyAnimation.MyAnimations
import com.example.walletapp.Settings.ChangeLanguageScreen
import com.example.walletapp.Settings.ChangePasswordScreen
import com.example.walletapp.appScreens.MainPagesActivity
import com.example.walletapp.appScreens.mainScreens.AddSignerScreen
import com.example.walletapp.appScreens.mainScreens.CreateWalletScreen_v2
import com.example.walletapp.appScreens.mainScreens.EditSigner
import com.example.walletapp.appScreens.mainScreens.MatrixRain
import com.example.walletapp.appScreens.mainScreens.PurchaseScreen
import com.example.walletapp.appScreens.mainScreens.ReceiveScreen
import com.example.walletapp.appScreens.mainScreens.SendingScreen_V2
import com.example.walletapp.appScreens.mainScreens.SettingsScreen
import com.example.walletapp.appScreens.mainScreens.ShareAddress
import com.example.walletapp.appScreens.mainScreens.SignHistoryScreen
import com.example.walletapp.appScreens.mainScreens.SignersScreen
import com.example.walletapp.appScreens.mainScreens.TxHistoryScreen
import com.example.walletapp.appViewModel.appViewModel

@Composable
fun AppActivity(
    activity: Activity,
    viewModel: appViewModel,
    navHostController: NavHostController
){
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedSignerAddress by remember { mutableStateOf("") }
    var qrScanResult by remember { mutableStateOf<String?>(null) }
    val anim = MyAnimations()

    fun switchToPage(index: Int) {
        selectedTabIndex = index
    }

    fun switchToPage(index: Int, address: String = "", qrResult: String? = null) {
        selectedTabIndex = index
        selectedSignerAddress = address
        qrScanResult = qrResult
    }

    BackHandler(
        onBack = {
            when (selectedTabIndex) {
                0 -> { activity.finish() }
                4, 8 -> { switchToPage(3) }
                13 -> { switchToPage(7) }
                else -> { switchToPage(0) }
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        AnimatedContent(
            targetState = selectedTabIndex,
            modifier = Modifier.background(color = colorScheme.background),
            transitionSpec = {
                anim.fadeTransitionSpec()
            }, label = ""
        ) { screen ->
            when (screen) {
                0 -> MainPagesActivity(
                    viewModel = viewModel,
                    onSettingsClick = {switchToPage(7)},
                    onShareClick = {switchToPage(2)},
                    onSignersClick = {switchToPage(3)},
                    onCreateWalletClick = {switchToPage(5)},
                    onMatrixClick = {switchToPage(6)},
                    onSend = {switchToPage(1)},
                    onReceive = {switchToPage(9)},
                    onSignHistory = {switchToPage(10)},
                    onPurchase = {switchToPage(11)},
                    onTxHistory = {switchToPage(14)}
                )

                1 -> SendingScreen_V2(
                    viewModel = viewModel,
                    onCreateClick = {switchToPage(5)},
                    onBackClick = {switchToPage(0)},
                    onNextClick = {switchToPage(0)}
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

                5 -> CreateWalletScreen_v2(
                    viewModel = viewModel,
                    onCreateClick = {switchToPage(0)},
                    onBackClick = {switchToPage(0)}

                )

                6 -> MatrixRain()

                7 -> SettingsScreen(
                    viewModel = viewModel,
                    onChangePasswordClick = {switchToPage(12)},
                    onChangeLanguageClick = {switchToPage(13)},
                    onBackClick = {switchToPage(0)},
                    navHostController = navHostController
                )

                8 -> AddSignerScreen(
                    viewModel = viewModel,
                    onBackClick = {switchToPage(3)}
                )

                9 -> ReceiveScreen(
                    viewModel = viewModel,
                    onCreateClick = { switchToPage(5) },
                    onBackClick = { switchToPage(0) }
                )

                10 -> SignHistoryScreen(
                    viewModel = viewModel,
                    onSendingClick = {switchToPage(1)},
                    onBackClick = {switchToPage(0)}
                )

                11 -> PurchaseScreen(
                    onBackClick = {switchToPage(0)}
                )

                12 -> ChangePasswordScreen(
                    onSuccessClick = {switchToPage(0)},
                    viewModel = viewModel
                )

                13 -> ChangeLanguageScreen(
                    viewModel = viewModel,
                    onBackClick = {switchToPage(7)}
                )

                14 -> TxHistoryScreen(
                    viewModel = viewModel,
                    onBackClick = {switchToPage(0)}
                )
            }
        }
    }
}