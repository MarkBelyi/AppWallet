package com.example.walletapp.Screens.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.Element.ClickableText
import com.example.walletapp.AuxiliaryFunctions.Element.SearchBar
import com.example.walletapp.AuxiliaryFunctions.Element.WalletItem
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PullToRefreshWithCustomIndicator
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.R

@Composable
fun Wallet(viewModel: appViewModel, onCreateClick: () -> Unit) {
    val wallets by viewModel.filteredWallets.observeAsState(initial = emptyList())
    val context = LocalContext.current
    val selectedWallet by viewModel.chooseWallet.observeAsState(initial = null)
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshWallets(context) {}
        viewModel.filterWallets()
    }

    PullToRefreshWithCustomIndicator(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.refreshWallets(context) {
                isRefreshing = false
            }
        },
        content = {
            if (selectedWallet == null) {
                WalletsList(wallets = wallets, onWalletClick = { wallet ->
                    viewModel.chooseWallet(wallet)
                }, onCreateClick = onCreateClick, viewModel = viewModel)
            } else {
                WalletDetailScreen(wallet = selectedWallet!!, viewModel = viewModel, onBack = {
                    viewModel.chooseWallet(null)
                })
            }
        }
    )
}

@Composable
fun WalletsList(
    wallets: List<Wallets>,
    onWalletClick: (Wallets) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: appViewModel
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ) {
        SearchBar(searchText, onTextChange = { newValue ->
            searchText = newValue
            viewModel.filterWalletsByName(newValue.text)
        }, viewModel = viewModel)

        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (wallets.isEmpty()) {
                items(1) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_wallets),
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ClickableText(
                            text = stringResource(id = R.string.createWallet),
                            onClick = onCreateClick
                        )
                    }
                }
            } else {
                items(wallets) { wallet ->
                    WalletItem(wallet = wallet, onWalletClick = onWalletClick)
                }
            }
        }
    }
}