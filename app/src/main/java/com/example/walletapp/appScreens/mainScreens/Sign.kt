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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
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
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (tx.status == 2) {
                Icon(
                    Icons.Rounded.Check, // Замените на ваш ресурс галочки
                    contentDescription = null,
                    tint = colorScheme.error,
                    modifier = Modifier.padding(end = 16.dp).scale(1.4f)
                )
            } else if (tx.status == 3) {
                Icon(
                    Icons.Rounded.Close, // Замените на ваш ресурс крестика
                    contentDescription = null,
                    tint = colorScheme.error,
                    modifier = Modifier.padding(end = 16.dp).scale(1.4f)
                )
            }

            Column {
                Text(
                    text = tx.info,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "To address: ${tx.to_addr}",
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "Amount: ${tx.tx_value} ${tx.token}",
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )



                when (tx.status) {
                    0 -> { // IDLE
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            OutlinedButton(onClick = {
                                onSign()
                            }) {
                                Text("Sign")
                            }
                            OutlinedButton(onClick = {
                                showDialog.value = true
                            }) {
                                Text("Reject")
                            }
                        }
                    }
                    2 -> { // SIGNED
                        Text(
                            text = "Транзакция подписана вами",
                            style = MaterialTheme.typography.bodySmall,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = colorScheme.onSurface
                        )
                    }
                    3 -> { // REJECTED
                        Text(
                            text = "Отказано вами по причине: ${tx.deny}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.error
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

                    }
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
