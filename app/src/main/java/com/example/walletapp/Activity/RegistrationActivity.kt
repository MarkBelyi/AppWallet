package com.example.walletapp.Activity

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.walletapp.AppViewModel.RegistrationViewModel
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.MyAnimation.MyAnimations
import com.example.walletapp.Screens.registrationScreens.CreatePasswordScreen
import com.example.walletapp.Screens.registrationScreens.CreateSeedPhraseScreen
import com.example.walletapp.Screens.registrationScreens.NewUserScreenColumn
import com.example.walletapp.Screens.registrationScreens.PinLockScreen
import com.example.walletapp.Screens.registrationScreens.TapSeedPhraseScreen
import com.example.walletapp.Screens.registrationScreens.WriteSeedPhraseScreen


@Composable
fun RegistrationActivity(
    activity: Activity,
    navHostController: NavHostController,
    viewModelReg: RegistrationViewModel,
    viewModelApp: appViewModel
) {
    var isAddClicked by remember { mutableStateOf(false) }
    var selectedTabIndex by viewModelReg::selectedTabIndex
    fun switchToPage(index: Int, isAddClick: Boolean = false) {
        viewModelReg.saveState(index)
        selectedTabIndex = index
        isAddClicked = isAddClick
    }

    val anim = MyAnimations()

    DisposableEffect(Unit) {
        onDispose {
            viewModelReg.saveState(selectedTabIndex)
        }
    }

    BackHandler(onBack = {
        when (selectedTabIndex) {
            0 -> {
                activity.finish()
            }

            1, 3 -> {
                switchToPage(selectedTabIndex - 1)
            }

            2, 4, 5 -> {
                switchToPage(0)
            }
        }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = selectedTabIndex,
            modifier = Modifier.background(color = colorScheme.background),
            transitionSpec = {
                anim.fadeTransitionSpec()
            }, label = ""
        ) { screen ->
            when (screen) {
                0 -> NewUserScreenColumn(
                    onCreateClick = { switchToPage(1) },
                    onAddClick = { switchToPage(1, isAddClick = true) },
                )

                1 -> CreatePasswordScreen(
                    onNextAction = {
                        if (isAddClicked) switchToPage(4) else switchToPage(2)
                    },
                    onPinCodeClick = {
                        if (isAddClicked) switchToPage(5, isAddClick = true) else switchToPage(5)
                    },
                    viewModel = viewModelApp
                )

                2 -> CreateSeedPhraseScreen(
                    onNextClick = { switchToPage(3) },
                    navHostController = navHostController,
                    viewModelReg = viewModelReg,
                    viewModelApp = viewModelApp
                )

                5 -> PinLockScreen(
                    onAction = {
                        if (isAddClicked) switchToPage(4) else switchToPage(2)
                    }
                )

                3 -> TapSeedPhraseScreen(
                    navHostController = navHostController,
                    viewModelReg = viewModelReg,
                    viewModelApp = viewModelApp
                )

                4 -> WriteSeedPhraseScreen(
                    navHostController = navHostController,
                    viewModel = viewModelApp
                )
            }
        }
    }
}
