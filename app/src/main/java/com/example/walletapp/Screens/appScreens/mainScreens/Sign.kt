package com.example.walletapp.Screens.appScreens.mainScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.ENUM.AuthMethod
import com.example.walletapp.AuxiliaryFunctions.Element.PasswordInputField
import com.example.walletapp.AuxiliaryFunctions.Element.SignItem
import com.example.walletapp.AuxiliaryFunctions.Functions.verifyPin
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PasswordStorageHelper
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PullToRefreshWithCustomIndicator
import com.example.walletapp.R
import com.example.walletapp.Screens.registrationScreens.PinLockScreenApp
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.launch

@Composable
fun Sign(viewModel: appViewModel) {

    var isRefreshing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.needSignTX(context) {}
    }

    PullToRefreshWithCustomIndicator(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.needSignTX(context) {
                isRefreshing = false
            }
        },
        content = {
            TXScreens(viewModel = viewModel)
        }
    )
}

@Composable
fun TXScreens(viewModel: appViewModel) {
    val txs by viewModel.allTX.observeAsState(initial = emptyList())
    val filteredTxs = txs.filter { it.status == 0 }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ) {
        if (filteredTxs.isNotEmpty()) {
            items(filteredTxs) { tx ->
                SignItem(
                    tx = tx,
                    onSign = { viewModel.signTransaction(tx.unid) },
                    onReject = { reason ->
                        viewModel.rejectTransaction(tx.unid, reason = reason)
                    },
                    viewModel = viewModel
                )
            }
        } else {
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,

                    ) {
                    Text(
                        text = stringResource(id = R.string.no_need_to_sign),
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
        }

    }
}

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
                                    Toast.makeText(
                                        context,
                                        R.string.incorrect_pin,
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                                Toast.makeText(context, R.string.incorrect_pass, Toast.LENGTH_SHORT)
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

