package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.walletapp.DataBase.Entities.TX
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun Sign(viewModel: appViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.inverseSurface)
    ) {
        TXScreens(viewModel = viewModel)
    }
}

@Composable
fun SignItem(viewModel: appViewModel, tx: TX, onSign: () -> Unit, onReject: (String) -> Unit) {
    val context = LocalContext.current
    val userAddress = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userAddress.value = GetMyAddr(context) // Get the user's address
    }

    val isTxValid = tx.tx.replace(" ", "").matches(Regex("^[a-fA-F0-9\\s]{64}$"))
    val signingState = remember { mutableStateOf(SigningState.IDLE) }
    val showDialog = remember { mutableStateOf(false) }
    val rejectReason = remember { mutableStateOf("") }

    val rejectionReason = viewModel.isTransactionRejected(tx.unid)
    if (rejectionReason != null) {
        signingState.value = SigningState.REJECTED
        rejectReason.value = rejectionReason
    }

    val isSigned = viewModel.isTransactionSigned(tx.unid)
    if (isSigned) {
        signingState.value = SigningState.SIGNED
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Причина отказа") },
            text = {
                Column {
                    OutlinedTextField(
                        value = rejectReason.value,
                        onValueChange = { rejectReason.value = it },
                        label = { Text("Причина") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    signingState.value = SigningState.REJECTED
                    onReject(rejectReason.value)
                }) {
                    Text("Ок")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    val isSigner = tx.signedEC.split(",").contains(userAddress.value) || tx.waitEC.split(",").contains(userAddress.value)

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .border(width = 0.5.dp, color = colorScheme.primary, shape = roundedShape)
            .fillMaxWidth(),
        shape = roundedShape,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Info: ${tx.info}",
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "To: ${tx.to_addr}",
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Amount: ${tx.network}: ${tx.tx_value}",
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface
                )
                when {
                    !isTxValid -> {
                        when (signingState.value) {
                            SigningState.IDLE -> {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    if (isSigner) {
                                        Button(onClick = {
                                            signingState.value = SigningState.SIGNING
                                            onSign()
                                        }) {
                                            Text("Sign")
                                        }
                                        Button(onClick = {
                                            showDialog.value = true
                                        }) {
                                            Text("Reject")
                                        }
                                    } else {
                                        Text(text = "Waiting for signer's signature", color = colorScheme.onSurface)
                                    }
                                }
                            }
                            SigningState.SIGNING -> {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Text(text = "Ожидайте", color = colorScheme.onSurface)
                                    CircularProgressIndicator()
                                }
                            }
                            SigningState.REJECTED -> {
                                Text(
                                    text = "Отказано вами по причине: ${rejectReason.value}",
                                    style = MaterialTheme.typography.bodySmall,
                                    overflow = TextOverflow.Ellipsis,
                                    color = colorScheme.error
                                )
                            }
                            SigningState.SIGNED -> {
                                Text(
                                    text = "Transaction signed",
                                    style = MaterialTheme.typography.bodySmall,
                                    overflow = TextOverflow.Ellipsis,
                                    color = colorScheme.primary
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Transaction: ${tx.tx}",
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.onSurface,
                            maxLines = 1,
                            minLines = 1
                        )
                    }
                }
            }
        }
    }
}

enum class SigningState {
    IDLE,
    SIGNING,
    REJECTED,
    SIGNED
}

@Composable
fun TXScreens(viewModel: appViewModel) {
    val txs by viewModel.allTX.observeAsState(initial = emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.needSignTX(context)
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(txs) { tx ->
            SignItem(
                viewModel = viewModel,
                tx = tx,
                onSign = { viewModel.signTransaction(tx.unid) },
                onReject = { reason -> viewModel.rejectTransaction(tx.unid, reason = reason) }
            )
        }
    }
}
