package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.Element.ClickableText
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel

@Composable
fun Wallet(viewModel: appViewModel, onCreateClick: () -> Unit) {
    val wallets by viewModel.allWallets.observeAsState(initial = emptyList())
    val context = LocalContext.current
    var selectedWallet by remember { mutableStateOf<Wallets?>(null) }

    LaunchedEffect(wallets) {
        wallets.forEach { wallet ->
            /*if(wallet.addr.isNotEmpty() && wallet.slist.isEmpty()){
                viewModel.signersList(context, wallet.myUNID) { response ->
                    val (parsedSlist, minSigns) = parseSlist(response)
                    viewModel.updateWalletSlistAndMinSigns(wallet.wallet_id, parsedSlist, minSigns)
                }
            }else{
                //Тут должна быть классная логика для одного кошелька
            }*/
            viewModel.addWallets(context)
        }

    }

    if (selectedWallet == null) {
        WalletsList(wallets, onWalletClick = { wallet ->
            selectedWallet = wallet
        }, onCreateClick = onCreateClick
            )
    } else {
        WalletDetailScreen(wallet = selectedWallet!!) {
            selectedWallet = null
        }

    }
}

@Composable
fun WalletsList(wallets: List<Wallets>, onWalletClick: (Wallets) -> Unit, onCreateClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if(wallets.isEmpty()){
            items(1){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = stringResource(id = R.string.no_wallets), color = colorScheme.onSurface, modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    ClickableText(text = stringResource(id = R.string.createWallet), onClick = onCreateClick)
                }
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
    val context = LocalContext.current
    val isAddressEmpty = wallet.addr.isEmpty()

    Card(
        border = BorderStroke(width = 2.dp, color = colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        onClick = {
            if (!isAddressEmpty) onWalletClick(wallet)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = context.getString(R.string.name_of_wallet) + ": " + wallet.info,
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))

            if (isAddressEmpty) {
                Text(context.getString(R.string.pending_wallet), fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(
                    text = context.getString(R.string.Address) + ": \n${wallet.addr}",
                    fontWeight = FontWeight.Light,
                    color = colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = context.getString(R.string.token) + ": " + wallet.tokenShortNames,
                    fontWeight = FontWeight.Light,
                    color = colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(8.dp))
            Divider(color = colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(
                text = context.getString(R.string.min_signers_count) + ": ${wallet.minSignersCount}",
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}







