package com.example.walletapp.appScreens.mainScreens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.TX
import com.example.walletapp.PullToRefreshLazyColumn.PullToRefreshWithCustomIndicator
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.Settings.PasswordInputField
import com.example.walletapp.Settings.verifyPin
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.registrationScreens.AuthMethod
import com.example.walletapp.registrationScreens.PinLockScreenApp
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.launch

@Composable
fun Sign(viewModel: appViewModel) {

    var isRefreshing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.needSignTX(context){}
    }

    PullToRefreshWithCustomIndicator(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.needSignTX(context){
                isRefreshing = false
            }
        },
        content = {
            TXScreens(viewModel = viewModel)
        }
    )
}

@Composable
fun SignItem(tx: TX, onSign: () -> Unit, onReject: (String) -> Unit, viewModel: appViewModel) {
    val context = LocalContext.current
    val userAddress = remember { mutableStateOf("") }
    val showAuthSheet = remember { mutableStateOf(false) }
    val authAction = remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(Unit) {
        userAddress.value = GetMyAddr(context)
    }

    val showDialog = remember { mutableStateOf(false) }
    val rejectReason = remember { mutableStateOf("") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Причина отказа") },
            text = {
                OutlinedTextField(
                    value = rejectReason.value,
                    onValueChange = { rejectReason.value = it },
                    singleLine = true,
                    maxLines = 1,
                    shape = newRoundedShape,
                    placeholder = {
                        Text(text = "Напишите причину отказа", fontWeight = FontWeight.Light)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedContainerColor = colorScheme.surface,
                        unfocusedLabelColor = colorScheme.onBackground,
                        cursorColor = colorScheme.primary
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onReject(rejectReason.value)
                }) {
                    Text(text = "Ок")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showAuthSheet.value) {
        AuthModalBottomSheet(
            showAuthSheet = showAuthSheet,
            onAuthenticated = {
                authAction.value?.invoke()
            },
            viewModel = viewModel
        )
    }


    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth(),
        shape = newRoundedShape,
        border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.9f)
            ) {
                Text(
                    text = tx.info,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "To address: ${tx.to_addr}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "Amount: ${tx.tx_value} ${tx.token}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )

                when (tx.status) {
                    0 -> { // IDLE
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            OutlinedButton(
                                onClick = {
                                    showAuthSheet.value = true
                                    authAction.value = onSign
                                },
                                shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 0.dp, bottomEnd = 0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorScheme.onSurface,
                                    containerColor = colorScheme.surface
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sign")
                            }
                            OutlinedButton( onClick = {
                                showAuthSheet.value = true
                                authAction.value = {
                                    showDialog.value = true
                                }
                            },
                                shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 24.dp, bottomEnd = 24.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorScheme.onSurface,
                                    containerColor = colorScheme.surface
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reject")
                            }
                        }
                    }
                    2 -> { // SIGNED
                        Text(
                            text = "Транзакция подписана вами",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            color = colorScheme.primary
                        )
                    }
                    3 -> { // REJECTED
                        Text(
                            text = "Отказано вами по причине: ${tx.deny}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.error
                        )
                    }
                }
            }

            Column(
                modifier = if(tx.status == 0) {Modifier} else {Modifier.weight(0.1f)},
                verticalArrangement = Arrangement.Center
            ){
                when (tx.status) {
                    2 -> {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null,
                            tint = colorScheme.error,
                            modifier = Modifier.scale(1.4f)
                        )
                    }
                    3 -> {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = null,
                            tint = colorScheme.error,
                            modifier = Modifier.scale(1.4f)
                        )
                    }
                }
            }
        }
    }
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
        if(filteredTxs.isNotEmpty()){
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
        }else{
            item {
                Text(
                    text = "You don't have any transactions that you need to sign!",
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
                                    Toast.makeText(context, "Incorrect PIN", Toast.LENGTH_SHORT).show()
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
                                Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
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

