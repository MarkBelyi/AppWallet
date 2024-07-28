package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.Element.CustomButton
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SendingScreen_V2(
    viewModel: appViewModel,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.refreshWallets(context){}
        viewModel.filterWallets()
    }
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val address by viewModel.qrResult.observeAsState(initial = "")
    val wallets by viewModel.filteredWallets.observeAsState(initial = emptyList())

    Scaffold(
        containerColor = colorScheme.background,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    0 -> WalletsListScreen(
                        viewModel = viewModel,
                        onCreateClick = onCreateClick,
                        onWalletClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page = 1)
                            }
                        },
                        wallets = wallets
                    )
                    1 -> {
                        val selectedWallet by viewModel.selectedWallet.observeAsState()
                        val tokenAddr = selectedWallet?.addr ?: ""
                        SelectTokenScreen(
                            viewModel = viewModel,
                            tokenAddr = tokenAddr,
                            onTokenClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(page = 2)
                                }
                            }
                        )
                    }
                    2 -> {
                        val selectedToken by viewModel.selectedToken.observeAsState()
                        TransactionScreen(viewModel, selectedToken, address ?: "", onNextClick = onNextClick)
                        viewModel.clearQrResult()
                    }
                }
            }
        }
    }
}


@Composable
fun WalletsListScreen(
    viewModel: appViewModel,
    onCreateClick: () -> Unit,
    onWalletClick: (wallet: Wallets) -> Unit,
    wallets: List<Wallets>
) {

    WalletsList(
        wallets = wallets,
        onWalletClick = { wallet ->
            viewModel.selectWallet(wallet)
            onWalletClick(wallet)
        },
        onCreateClick = { onCreateClick() },
        viewModel = viewModel,
    )
}

@Composable
fun SelectTokenScreen(
    viewModel: appViewModel,
    tokenAddr: String,
    onTokenClick: (balans: Balans) -> Unit
) {
    val balansList by viewModel.getBalansForTokenAddress(tokenAddr).observeAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.background(color = colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(balansList) { balans ->
            TokenItem(balans = balans, onClick = {
                viewModel.selectToken(balans)
                onTokenClick(balans)
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(viewModel: appViewModel, selectedToken: Balans?, initialAddress: String, onNextClick: () -> Unit) {

    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openQRBottomSheet by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf(initialAddress) }
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

                    val isButtonEnabled = address.isNotBlank() && amount.isNotBlank() && paymentPurpose.isNotBlank()

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
                            onNextClick()
                        },
                        enabled = isButtonEnabled
                    )

                }
            }
        }
    } ?: Text("No wallet or token selected", color = colorScheme.onSurface)
}