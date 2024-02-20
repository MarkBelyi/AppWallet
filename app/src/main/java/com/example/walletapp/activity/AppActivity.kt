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
import com.example.walletapp.DataBase.Event.SignerEvent
import com.example.walletapp.DataBase.State.SignerState
import com.example.walletapp.actionScreens.MainPagesActivity
import com.example.walletapp.actionScreens.SignersScreen

@Composable
fun AppActivity(activity: Activity,
                state: SignerState,
                onEvent: (SignerEvent) -> Unit )
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
                //onSettingClick = {switchToPage(1)},
                //onShareClick = {switchToPage(2)},
                onSignersClick = {switchToPage(3)}
            )
            3 -> SignersScreen(
                state = state,
                onEvent = onEvent
            )


        }
    }



}