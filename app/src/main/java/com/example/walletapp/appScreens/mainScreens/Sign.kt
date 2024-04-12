package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun Sign(viewModel: appViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ){
        WalletsAndBalancesScreen(viewModel = viewModel)
    }
}

@Composable
fun WalletsAndBalancesScreen(viewModel: appViewModel) {
    val wallets by viewModel.allWallets.observeAsState(initial = emptyList())
    val balanses by viewModel.getAllBalans().observeAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            Text(
                text = "Кошельки",
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
        items(wallets) { wallet ->
            WalletItem(wallet = wallet){}
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

