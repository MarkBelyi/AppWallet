package com.example.walletapp.activity

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import com.example.walletapp.Animation.Animations
import com.example.walletapp.appViewModel.RegistrationViewModel
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.registrationScreens.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RegistrationActivity(
    activity: Activity,
    navHostController: NavHostController,
    viewModelReg: RegistrationViewModel,
    viewModelApp: appViewModel
) {

    val anim = Animations()

    var isAddClicked by remember { mutableStateOf(false) }
    var selectedTabIndex by viewModelReg::selectedTabIndex

    val coroutineScope = rememberCoroutineScope()

    fun switchToPage(index: Int, isAddClick: Boolean = false) {
        coroutineScope.launch {
            delay(200)
        }

        viewModelReg.saveState(index)
        selectedTabIndex = index
        isAddClicked = isAddClick
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModelReg.saveState(selectedTabIndex)
        }
    }

    BackHandler(onBack = {
        when (selectedTabIndex) {
            0 -> activity.finish()
            1, 3 -> switchToPage(selectedTabIndex - 1)
            2, 4, 5 -> switchToPage(0)
        }
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = {
                    anim.slideTransitionSpec()
            }
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
                    },
                    onBiometricAuthenticated = {
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

//class