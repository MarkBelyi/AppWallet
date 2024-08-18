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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.walletapp.MyAnimation.MyAnimations
import com.example.walletapp.Settings.ChangeLanguageScreen
import com.example.walletapp.Settings.ChangePasswordScreen
import com.example.walletapp.appScreens.mainScreens.ShareAddress
import com.example.walletapp.appScreens.mainScreens.SignerModeScreen
import com.example.walletapp.appViewModel.appViewModel

@Composable
fun SignerModeActivity(
    activity: Activity,
    navHostController: NavHostController,
    viewModel: appViewModel
) {

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    fun switchToPage(index: Int) {
        selectedTabIndex = index
    }

    val anim = MyAnimations()

    BackHandler(onBack = {
        when (selectedTabIndex) {
            0 -> {
                activity.finish()
            }

            1, 12 -> {
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
            transitionSpec = {
                anim.slideTransitionSpec()
            },
            label = "",
            modifier = Modifier.background(color = colorScheme.surface)
        ) { screen ->
            when (screen) {
                0 -> SignerModeScreen(
                    viewModel = viewModel,
                    onShareClick = { switchToPage(1) },
                    navHostController = navHostController,
                    onChangePasswordClick = {switchToPage(12)},
                    onChangeLanguageClick = {switchToPage(13)}
                )

                1 -> ShareAddress(
                    onBackClick = { switchToPage(0) }
                )

                12 -> ChangePasswordScreen(
                    onSuccessClick = {switchToPage(0)},
                    viewModel = viewModel
                )

                13 -> ChangeLanguageScreen(
                    viewModel = viewModel,
                    onBackClick = {switchToPage(0)}
                )
            }
        }
    }
}