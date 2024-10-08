package com.example.walletapp.Screens.appScreens.mainScreens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.Element.CustomButton
import com.example.walletapp.AuxiliaryFunctions.Element.CustomOutlinedTextField
import com.example.walletapp.AuxiliaryFunctions.Element.CustomOutlinedTextFieldWithIcon
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.ui.theme.newRoundedShape

object SendingRoutes {
    const val WALLETS = "wallets"
    const val SELECT_TOKEN = "select_token/{tokenAddr}"
    const val SEND_TRANSACTION = "send_transaction"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendingScreens(
    viewModel: appViewModel,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val navController = rememberNavController()
    Scaffold(
        containerColor = colorScheme.inverseSurface,
        topBar = {
            TopAppBar(
                title = { Text("Choose", color = colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        val address = navController.currentBackStackEntry?.arguments?.getString("address") ?: ""
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(navController, startDestination = SendingRoutes.WALLETS) {
                composable(SendingRoutes.WALLETS) {
                    WalletsListScreen(navController, onCreateClick, viewModel, address)
                }
                composable(SendingRoutes.SELECT_TOKEN) { backStackEntry ->
                    backStackEntry.arguments?.getString("tokenAddr")?.let { tokenAddr ->
                        SelectTokenScreen(navController, viewModel, tokenAddr, address)
                    }
                }
                composable(SendingRoutes.SEND_TRANSACTION) {
                    TransactionScreen(viewModel, address)
                }
            }
        }
    }
}

@Composable
fun WalletsListScreen(
    navController: NavController,
    onCreateClick: () -> Unit,
    viewModel: appViewModel,
    address: String
) {
    val wallets by viewModel.filteredWallets.observeAsState(initial = emptyList())
    WalletsList(
        wallets = wallets,
        onWalletClick = { wallet ->
            viewModel.selectWallet(wallet)
            navController.navigate(
                "${
                    SendingRoutes.SELECT_TOKEN.replace(
                        "{tokenAddr}",
                        wallet.addr
                    )
                }?address=$address"
            )
        },
        onCreateClick = { onCreateClick() },
        viewModel = viewModel,
    )
}


@Composable
fun SelectTokenScreen(
    navController: NavController,
    viewModel: appViewModel,
    tokenAddr: String,
    address: String
) {
    val balansList by viewModel.getBalansForTokenAddress(tokenAddr)
        .observeAsState(initial = emptyList())

    LazyColumn {
        items(balansList) { balans ->
            TokenItem(balans = balans, onClick = {
                viewModel.selectToken(balans)
                navController.navigate("${SendingRoutes.SEND_TRANSACTION}?address=$address") {
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
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
        shape = newRoundedShape,
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
fun TransactionScreen(viewModel: appViewModel, preFilledAddress: String) {
    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openQRBottomSheet by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf(preFilledAddress) }
    var amount by remember { mutableStateOf("") }
    var paymentPurpose by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (openQRBottomSheet) {
        ModalBottomSheet(
            shape = newRoundedShape,
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

    val selectedWallet by viewModel.selectedWallet.observeAsState()
    val selectedToken by viewModel.selectedToken.observeAsState()

    selectedWallet?.let { wallet ->
        selectedToken?.let { token ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Кошелек: ${wallet.addr}", color = colorScheme.onSurface)
                    Text("Баланс: ${token.amount} ${token.name}", color = colorScheme.onSurface)

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomOutlinedTextFieldWithIcon(
                        value = address,
                        onValueChange = { address = it },
                        placeholder = "Адрес",
                        onClick = { openQRBottomSheet = true },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomOutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = "Сумма",
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Комиссия составит: ???", color = colorScheme.onSurface)
                    Text("Минимальная комиссия: ???", color = colorScheme.onSurface)
                    Text("Максимальная комиссия: ???", color = colorScheme.onSurface)
                    Text("Комиссия: ???", color = colorScheme.onSurface)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Укажите назначение платежа", color = colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomOutlinedTextField(
                        value = paymentPurpose,
                        onValueChange = { paymentPurpose = it },
                        placeholder = "Назначение платежа"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val isButtonEnabled =
                        address.isNotBlank() && amount.isNotBlank() && paymentPurpose.isNotBlank()

                    CustomButton(
                        text = "Отправить",
                        onClick = {
                            amount.toDoubleOrNull()?.let { amt ->
                                viewModel.sendTransaction(
                                    token = token,
                                    wallet = wallet,
                                    amount = amt,
                                    address = address,
                                    info = paymentPurpose,
                                    context = context
                                )
                            }
                        },
                        enabled = isButtonEnabled
                    )
                }
            }
        }
    } ?: Text("No wallet or token selected", color = colorScheme.onSurface)
}
