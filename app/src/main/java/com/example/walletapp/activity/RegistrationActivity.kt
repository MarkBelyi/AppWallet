package com.example.walletapp.activity

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.registrationScreens.CreatePasswordScreen
import com.example.walletapp.registrationScreens.CreateSeedPhraseScreen
import com.example.walletapp.registrationScreens.NewUserScreenColumn
import com.example.walletapp.registrationScreens.TapSeedPhraseScreen
import com.example.walletapp.registrationScreens.WriteSeedPhraseScreen


@Composable
fun RegistrationActivity(activity: Activity, navHostController: NavHostController, viewModel: appViewModel){
    var isAddClicked by remember { mutableStateOf(false) }

    var selectedTabIndex by viewModel::selectedTabIndex

    fun switchToPage(index: Int, isAddClick: Boolean = false) {
        viewModel.saveState(index)
        selectedTabIndex = index
        isAddClicked = isAddClick
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveState(selectedTabIndex)
        }
    }

    BackHandler(
        onBack = {
            when (selectedTabIndex) {
                0 -> {
                    activity.finish()
                }
                1, 3 -> {
                    switchToPage(selectedTabIndex - 1)
                }
                2, 4 -> {
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
            0 -> NewUserScreenColumn(
                onCreateClick = { switchToPage(1) },
                onAddClick = { switchToPage(1, isAddClick = true) },
            )

            1 -> CreatePasswordScreen(onNextAction = {
                if (isAddClicked) {
                    switchToPage(4)
                } else {
                    switchToPage(2)
                }
            }
            )

            2 -> CreateSeedPhraseScreen(
                onNextClick = {switchToPage(3)},
                navHostController = navHostController,
                viewModel = viewModel
            )

            3 -> TapSeedPhraseScreen(navHostController = navHostController, viewModel = viewModel)

            4 -> WriteSeedPhraseScreen(navHostController = navHostController)
        }
    }

}