package com.example.walletapp.Settings

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.ENUM.AuthMethod
import com.example.walletapp.AuxiliaryFunctions.Element.PasswordInputField
import com.example.walletapp.AuxiliaryFunctions.Functions.verifyPin
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PasswordStorageHelper
import com.example.walletapp.Screens.registrationScreens.PinLockScreenApp
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModalBottomSheet(
    showAuthSheet: MutableState<Boolean>,
    onAuthenticated: () -> Unit,
    viewModel: appViewModel
) {

    val context = LocalContext.current
    val passwordStorage = PasswordStorageHelper(context)
    val coroutineScope = rememberCoroutineScope()
    val authMethod by viewModel.getAuthMethod().observeAsState(initial = AuthMethod.PINCODE)
    val isAuthenticated = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newState ->
            newState == SheetValue.Hidden && isAuthenticated.value
        }
    )

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            if (isAuthenticated.value) onAuthenticated()
        },
        dragHandle = null,
        shape = topRoundedShape,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (authMethod) {
                    AuthMethod.PINCODE -> {
                        PinLockScreenApp(
                            onAction = {
                                if (verifyPin(context)) {
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        onAuthenticated()
                                    }
                                } else {
                                    Toast.makeText(context, "Incorrect PIN", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                            onBiometricAuthenticated = onAuthenticated
                        )
                    }

                    AuthMethod.PASSWORD -> {
                        PasswordInputField(onPasswordSubmitted = { password ->
                            if (password == passwordStorage.getPassword("MyPassword")) {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    onAuthenticated()
                                }
                            } else {
                                Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                    }
                }
            }
        }
    )

    LaunchedEffect(showAuthSheet.value) {
        coroutineScope.launch {
            if (showAuthSheet.value) {
                sheetState.show()
            } else {
                sheetState.hide()
            }
        }
    }
}


