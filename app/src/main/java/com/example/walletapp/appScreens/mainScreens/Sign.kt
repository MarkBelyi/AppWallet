package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.DataBase.Entities.TX
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun Sign(viewModel: appViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ){
        TxsAndBalancesScreen(viewModel = viewModel)
    }
}

@Composable
fun SignItem(tx: TX, onSign: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth(),
        shape = roundedShape,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant,
            contentColor = colorScheme.onSurfaceVariant
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
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "To: ${tx.to_addr}",
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Amount: ${tx.network}: {${tx.tx_value}}",
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Status: ${tx.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.primary,
                    overflow = TextOverflow.Ellipsis
                )

                if (tx.tx.isEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(
                            onClick = { onSign() }
                        ) {
                            Text("Sign")
                        }
                        Button(
                            onClick = { onReject() } // You might want to capture this reason from user input
                        ) {
                            Text("Reject")
                        }
                    }
                } else {
                    Text("Transaction ${tx.tx}")
                }

            }

        }
    }
}


@Composable
fun TxsAndBalancesScreen(viewModel: appViewModel) {
    val txs by viewModel.allTX.observeAsState(initial = emptyList())
    val balanses by viewModel.getAllBalans().observeAsState(initial = emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {  // This should ideally trigger once or based on a specific condition
        viewModel.fetchAndStoreTransactions(context)
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            Text(
                text = "Подписи",
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
        items(txs) { tx ->
            SignItem(
                tx = tx,
                onSign = { viewModel.signTransaction(context, tx.unid) },
                onReject = { viewModel.rejectTransaction(context, tx.unid, reason = "ASD") }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Балансы",
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )
        }

        items(balanses) { balans ->
            BalansItem(balans = balans)
        }
    }
}

@Composable
fun BalansItem(balans: Balans) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = roundedShape,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onBackground
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Название: ${balans.name}", style = MaterialTheme.typography.bodyLarge, overflow = TextOverflow.Ellipsis,)
            Text(text = "Адрес: ${balans.addr}", style = MaterialTheme.typography.bodySmall, overflow = TextOverflow.Ellipsis,)
            Text(text = "Сеть ID: ${balans.network_id}", style = MaterialTheme.typography.bodySmall, overflow = TextOverflow.Ellipsis,)
            Text(text = "Количество: ${balans.amount}", style = MaterialTheme.typography.bodySmall, overflow = TextOverflow.Ellipsis,)
            Text(text = "Цена: ${balans.price}", style = MaterialTheme.typography.bodySmall, overflow = TextOverflow.Ellipsis,)
        }
    }
}

