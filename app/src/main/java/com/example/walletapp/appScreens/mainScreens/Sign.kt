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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.walletapp.DataBase.Entities.TX
import com.example.walletapp.PullToRefreshLazyColumn.PullToRefreshWithCustomIndicator
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.roundedShape

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
fun SignItem(tx: TX, onSign: () -> Unit, onReject: (String) -> Unit) {
    val context = LocalContext.current
    val userAddress = remember { mutableStateOf("") }

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

                when (tx.status) {
                    0 -> { // IDLE
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                onSign()
                            }) {
                                Text("Sign")
                            }
                            Button(onClick = {
                                showDialog.value = true
                            }) {
                                Text("Reject")
                            }
                        }
                    }
                    2 -> { // SIGNED
                        Text(
                            text = "Transaction signed",
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.primary
                        )
                    }
                    3 -> { // REJECTED
                        Text(
                            text = "Отказано вами по причине: ${rejectReason.value}",
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.error
                        )
                    }
                    /*1 -> { // SIGNING
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            CircularProgressIndicator(
                                color = colorScheme.primary,
                                strokeWidth = 5.dp,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(text = "Ожидайте", color = colorScheme.onSurface, fontWeight = FontWeight.Light, fontSize = 16.sp)
                        }
                    }*/
                    /*5 -> { // WAITING
                        Text(
                            text = "Ожидайте подписей",
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.onSurface
                        )
                    }*/
                }
            }
        }
    }
}

/*@Composable
fun SignItem(tx: TX, onSign: () -> Unit, onReject: (String) -> Unit) {
    val context = LocalContext.current
    val userAddress = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userAddress.value = GetMyAddr(context)
    }

    val signingState = remember { mutableStateOf(SigningState.entries.find { it.ordinal == tx.status } ?: SigningState.IDLE) }
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
                when (signingState.value) {
                    SigningState.IDLE -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
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
                        }
                    }
                    SigningState.SIGNING -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            CircularProgressIndicator(
                                color = colorScheme.primary,
                                strokeWidth = 5.dp,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(text = "Ожидайте", color = colorScheme.onSurface, fontWeight = FontWeight.Light, fontSize = 16.sp)
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
                    SigningState.WAITING -> {
                        Text(
                            text = "Ожидайте подписей",
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}*/

@Composable
fun TXScreens(viewModel: appViewModel) {
    val txs by viewModel.allTX.observeAsState(initial = emptyList())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ) {
        items(txs) { tx ->
            SignItem(
                tx = tx,
                onSign = { viewModel.signTransaction(tx.unid) },
                onReject = { reason -> viewModel.rejectTransaction(tx.unid, reason = reason) }
            )
        }
    }
}
