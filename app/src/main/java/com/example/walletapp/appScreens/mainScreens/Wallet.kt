package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape
import kotlinx.coroutines.launch
import kotlin.reflect.full.memberProperties


val myMod = Modifier // просто для примера использования глобальных модификаторов
    .fillMaxWidth()
    .padding(4.dp)


@Composable
fun Wallet(viewModel: appViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    //val networks by viewModel.allNetworks.observeAsState(initial = emptyList())
    val wallets by viewModel.allWallets.observeAsState(initial = emptyList())


    Button(
        onClick = {
            coroutineScope.launch {
                viewModel.addWallets(context)
            }
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Enter")
    }

    Spacer(modifier = Modifier.height(16.dp))

    LazyColumn {
        items(wallets) { wallet ->
            WalletItem(wallet = wallet)
        }
    }
}

/*
@Composable
fun NetworkItem(network: Networks) {
    Column(modifier = Modifier
        .background(color = colorScheme.surface, shape = roundedShape)
        .padding(8.dp)
    ) {
        // Выведем данные по нашим сетям:
        Card(modifier = myMod) {
            for (net in Networks::class.memberProperties)
                Text(text = "${net.name}: ${net.get(network)}")
        }
    }
}
*/

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletItem(wallet: Wallets){
    Card(
        onClick = {},
        shape = roundedShape,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )

    ){
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = wallet.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                minLines = 1,
            )

        }

    }

}
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletItem(wallet: Wallets) {
    Card(
        onClick = {},
        shape = MaterialTheme.shapes.medium, // Используйте medium из MaterialTheme.shapes для roundedShape, если вы не определили roundedShape
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface, // Адаптируйте colorScheme к MaterialTheme.colorScheme, если не определено отдельно
            contentColor = colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "ID: ${wallet.wallet_id}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Network: ${wallet.network}", style = MaterialTheme.typography.bodyMedium)
            Text("Flags: ${wallet.myFlags}", style = MaterialTheme.typography.bodyMedium)
            Text("Type: ${wallet.wallet_type}", style = MaterialTheme.typography.bodyMedium)
            Text("Name: ${wallet.name}", style = MaterialTheme.typography.bodyMedium)
            Text("Info: ${wallet.info}", style = MaterialTheme.typography.bodyMedium)
            Text("Address: ${wallet.addr}", style = MaterialTheme.typography.bodyMedium)
            Text("Address Info: ${wallet.addr_info ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Text("UNID: ${wallet.myUNID}", style = MaterialTheme.typography.bodyMedium)
            Text("Tokens: ${wallet.tokenShortNames}", style = MaterialTheme.typography.bodyMedium)
            Text("Signers List: ${wallet.slist}", style = MaterialTheme.typography.bodyMedium)
            Text("Min Signers Count: ${wallet.minSignersCount}", style = MaterialTheme.typography.bodyMedium)
            Text("Group ID: ${wallet.group_id}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}



