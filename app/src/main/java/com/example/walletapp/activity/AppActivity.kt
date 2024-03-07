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
import com.example.walletapp.appScreens.mainScreens.CreateWalletScreen
import com.example.walletapp.appScreens.mainScreens.SignersScreen
import com.example.walletapp.appScreens.mainScreens.EditSigner
import com.example.walletapp.appViewModel.appViewModel

@Composable
fun AppActivity(
    activity: Activity,
    viewModel: appViewModel
){
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedSignerAddress by remember { mutableStateOf("") }

    fun switchToPage(index: Int) {
        selectedTabIndex = index

    }

    fun switchToPage(index: Int, address: String = "") {
        selectedTabIndex = index
        selectedSignerAddress = address
    }

    BackHandler(
        onBack = {
            when (selectedTabIndex) {
                0 -> {
                    activity.finish()
                }
                3 -> {
                    switchToPage(0)
                }
                4 -> {
                    switchToPage(3)
                }
                5 -> {
                    switchToPage(0)
                }
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
                //onSettingClick = {switchToPage(1)},
                //onShareClick = {switchToPage(2)},
                onSignersClick = {switchToPage(3)},
                onCreateWalletClick = {switchToPage(5)}
            )

            3 -> SignersScreen(
                viewModel = viewModel,
                onCurrentSignerClick = { address -> switchToPage(4, address) }
            )

            4 -> EditSigner(
                viewModel = viewModel,
                signerAddress = selectedSignerAddress,
                onSaveClick = {switchToPage(3)}
            )

            5 -> CreateWalletScreen(
                viewModel = viewModel
            )



        }
    }
}