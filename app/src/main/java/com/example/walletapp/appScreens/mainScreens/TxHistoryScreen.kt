package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.AllTX
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TxHistoryScreen(viewModel: appViewModel, onBackClick: () -> Unit) {
    val allUserTX by viewModel.allUserTX.observeAsState(initial = emptyList())
    val context = LocalContext.current

    LaunchedEffect(key1 = allUserTX) {
        //Нужно переделать внутри чтобы статусы не обновлялись
        viewModel.fetchAndStoreTransactions(context = context)
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "История транзакций",
                        color = colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    scrolledContainerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    ) {padding ->
        LazyColumn(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(color = colorScheme.background)
        ) {
            if(allUserTX.isEmpty()){
                item { Text(
                    text = "Здесь пока ничего нет!",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(padding),
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                    )
                }
            }else{
                items(allUserTX) { tx ->
                    TXItem(
                        tx = tx
                    )
                }
            }
        }

    }
}

@Composable
fun TXItem(tx: AllTX) {
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
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                val hexPattern = Regex("^[0-9a-fA-F]{64}$")

                if (tx.tx.isEmpty() || tx.tx == "null") {
                    Text(
                        text = "Транзакция еще не в блокчейне!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.onSurface,
                        maxLines = 1
                    )
                } else if (!hexPattern.matches(tx.tx)) {
                    Text(
                        text = "Ошибка: ${tx.tx}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.primary,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = "Транзакция прошла успешно!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = tx.tx,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.onSurface,
                        maxLines = 1
                    )
                }
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
                Text(
                    text = "Time: ${formatTimestamp(tx.init_ts)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }
    }
}

fun formatTimestamp(initTs: Int): String {
    val date = Date(initTs * 1000L)
    val formatter = SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}