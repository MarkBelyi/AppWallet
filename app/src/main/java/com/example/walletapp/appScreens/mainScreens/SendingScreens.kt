package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape

object Routes {
    const val WALLETS = "wallets"
    const val SELECT_TOKEN = "select_token/{tokenAddr}"
    const val SEND_TRANSACTION = "send_transaction"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendingScreens(viewModel: appViewModel, onBackClick: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        topBar = {
            TopAppBar(
                title = { Text("Choose", color = MaterialTheme.colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
        ) {
            NavHost(navController, startDestination = Routes.WALLETS) {
                composable(Routes.WALLETS) {
                    WalletsListScreen(navController, viewModel)
                }
                composable(Routes.SELECT_TOKEN) { backStackEntry ->
                    backStackEntry.arguments?.getString("tokenAddr")?.let { tokenAddr ->
                        SelectTokenScreen(navController, viewModel, tokenAddr)
                    }
                }
                composable(Routes.SEND_TRANSACTION) {
                    SendTransactionScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun WalletsListScreen(navController: NavController, viewModel: appViewModel) {
    val wallets by viewModel.allWallets.observeAsState(initial = emptyList())
    WalletsList(
        wallets = wallets,
        onWalletClick = { wallet ->
            viewModel.selectWallet(wallet)
            navController.navigate(Routes.SELECT_TOKEN.replace("{tokenAddr}", wallet.addr))
        },
        onCreateClick = {}
    )
}

@Composable
fun SelectTokenScreen(navController: NavController, viewModel: appViewModel, tokenAddr: String) {
    val balansList by viewModel.getBalansForTokenAddress(tokenAddr).observeAsState(initial = emptyList())

    LazyColumn {
        items(balansList) { balans ->
            TokenItem(balans = balans, onClick = {
                viewModel.selectToken(balans)
                navController.navigate(Routes.SEND_TRANSACTION) {
                    launchSingleTop = true
                    restoreState = true
                }
            })
        }
    }
}

@Composable
fun TokenItem(balans: Balans, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${balans.amount} ${balans.name}",
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendTransactionScreen(viewModel: appViewModel) {

    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openQRBottomSheet by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("") }

    if(openQRBottomSheet){
        ModalBottomSheet(
            shape = roundedShape,
            containerColor = colorScheme.surface,
            sheetState = qrBottomSheetState,
            onDismissRequest = { openQRBottomSheet = false },
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                }
            }
        ) {
            BottomSheetContent(
                onQRScanned = { result ->
                    address = result
                    openQRBottomSheet = false
                }
            )
        }
    }

    fun updateState(updateFunc: (String) -> Unit): (String) -> Unit = { newValue ->
        updateFunc(newValue)
    }


    val selectedWallet by viewModel.selectedWallet.observeAsState()
    val selectedToken by viewModel.selectedToken.observeAsState()
    var amount by remember { mutableStateOf("") }
    val context = LocalContext.current

    selectedWallet?.let { wallet ->
        selectedToken?.let { token ->
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Wallet:", color = colorScheme.onSurface)
                Text("Send transaction from ${wallet.addr}", color = colorScheme.onSurface)
                Text("Token: ${token.amount} ${token.name}", color = colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                CustomOutlinedTextFieldWithIcon(
                    value = address,
                    onValueChange = updateState { address = it },
                    placeholder = "Address",
                    onClick = { openQRBottomSheet = true },  // При нажатии открывать ModalBottomSheet
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount", color = colorScheme.onSurface) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    amount.toDoubleOrNull()?.let { amt ->
                        viewModel.sendTransaction(token = token, wallet = wallet, amount = amt, address = address, context = context)
                    }
                }) {
                    Text("Send")
                }
            }
        }
    } ?: Text("No wallet or token selected", color = colorScheme.onSurface)
}
