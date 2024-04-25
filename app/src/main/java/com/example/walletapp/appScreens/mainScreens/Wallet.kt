package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.appViewModel.appViewModel

@Composable
fun Wallet(viewModel: appViewModel) {
    val wallets by viewModel.allWallets.observeAsState(initial = emptyList())
    val context = LocalContext.current
    var selectedWallet by remember { mutableStateOf<Wallets?>(null) }

    LaunchedEffect(wallets) {
        viewModel.addWallets(context)
    }

    if (selectedWallet == null) {
        WalletsList(wallets, onWalletClick = { wallet ->
            selectedWallet = wallet
        })
    } else {
        WalletDetailScreen(wallet = selectedWallet!!) {
            selectedWallet = null
        }
    }
}

@Composable
fun WalletsList(wallets: List<Wallets>, onWalletClick: (Wallets) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if(wallets.isEmpty()){
            item{
                Text(text = "У вас нет активных кошельков", color = colorScheme.onSurface)
            }
        }
        else{
            items(wallets) { wallet ->
                WalletItem(wallet = wallet, onWalletClick = onWalletClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletItem(wallet: Wallets, onWalletClick: (Wallets) -> Unit) {
    val isAddressEmpty = wallet.addr.isEmpty()

    Card(
        onClick = {
            // Если адрес не пуст, обрабатываем нажатие
            if (!isAddressEmpty) onWalletClick(wallet)
        },
        modifier = Modifier
            //.background(brush = gradientCell, shape = roundedShape, alpha = 0.2f)
            .fillMaxWidth(),
        /*colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),*/
        /*elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )*/
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Wallet Name: ${wallet.info}",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))

            // Отображаем различный текст в зависимости от того, пустой ли адрес кошелька
            if (isAddressEmpty) {
                Text("Кошелек создается", style = MaterialTheme.typography.bodySmall)
            } else {
                // Прочая информация о кошельке
                Text("Address:\n${wallet.addr}", style = MaterialTheme.typography.bodySmall)
                Text("Token: ${wallet.tokenShortNames}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(4.dp))
            Divider(color = colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(Modifier.height(4.dp))
            Text("Min Signers Count: ${wallet.minSignersCount}", style = MaterialTheme.typography.bodySmall)
        }
    }
}







