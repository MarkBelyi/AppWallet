package com.example.walletapp.activity

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.walletapp.DataBase.Event.SignerEvent
import com.example.walletapp.DataBase.State.SignerState
import com.example.walletapp.appScreens.MainPagesActivity
import com.example.walletapp.appScreens.SignersScreen
import com.example.walletapp.appViewModel.appViewModel

@Composable
fun AppActivity(activity: Activity,
                viewModel: appViewModel
                )
{
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    fun switchToPage(index: Int) {
        selectedTabIndex = index

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
                /*4 -> {
                    switchToPage(3)
                }*/
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
                onSignersClick = {switchToPage(3)}
            )
            3 -> SignersScreen(
                viewModel = viewModel
            )


        }
    }
}