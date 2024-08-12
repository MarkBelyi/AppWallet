package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.DataBase.Entities.WalletAddress
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.Element.CustomButton
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.topRoundedShape
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


    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.surface,
            tonalElevation = 0.dp,
            onDismissRequest = { showDialog = false },
            title = {
                Text("Подтверждение транзакции",
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    fontSize = 18.sp
                ) },
            text = { Text("Вы уверены, что хотите cовершить транзакцию?",
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface
            ) },
            confirmButton = {
                TextButton(onClick = {
                    onNextClick()
                    showDialog = false
                }) {
                    Text("Подтвердить",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            shape = newRoundedShape
        )
    }
    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openQRBottomSheet by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf(initialAddress) }
    var amount by remember { mutableStateOf("") }
    var paymentPurpose by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Состояние для управления открытием/закрытием WalletAddresses Bottom Sheet
    val walletAddressesBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openWalletAddressesBottomSheet by remember { mutableStateOf(false) }

    if (openQRBottomSheet) {
        ModalBottomSheet(
            shape = topRoundedShape,
            containerColor = colorScheme.background,
            sheetState = qrBottomSheetState,
            tonalElevation = 0.dp,
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

    if (openWalletAddressesBottomSheet) {
        ModalBottomSheet(
            shape = topRoundedShape,
            containerColor = colorScheme.background,
            sheetState = walletAddressesBottomSheetState,
            tonalElevation = 0.dp,
            onDismissRequest = { openWalletAddressesBottomSheet = false },
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                }
            }
        ) {
            var showAddWalletScreen by remember { mutableStateOf(false) }

            if (showAddWalletScreen) {
                AddWalletAddressScreen(
                    viewModel = viewModel,
                    onBackClick = { showAddWalletScreen = false }  // Вернуться к списку адресов
                )
            } else {
                WalletAddressesContent(
                    viewModel = viewModel,
                    onWalletAddressClick = { selectedAddress ->
                        address = selectedAddress
                        openWalletAddressesBottomSheet = false
                    },
                    onAddWalletAddressClick = { showAddWalletScreen = true }  // Показать экран добавления
                )
            }
        }
    }


    val selectedWallet by viewModel.selectedWallet.observeAsState()

    selectedWallet?.let { wallet ->
        selectedToken?.let { token ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize().padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Кошелек: ${wallet.addr}", color = colorScheme.onSurface)
                    Text("Баланс: ${token.amount} ${token.name}", color = colorScheme.onSurface)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row (
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        CustomOutlinedTextFieldWithTwoIcon(
                            value = address,
                            onValueChange = { address = it },
                            placeholder = "Адрес",
                            onClick = { openQRBottomSheet = true },
                            onOpenWalletAddressesBottomSheet = {openWalletAddressesBottomSheet = true}
                        )

                    }



                    Spacer(modifier = Modifier.height(8.dp))

                    CustomOutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = "Сумма",
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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
                            showDialog = true
                        },
                        enabled = isButtonEnabled
                    )

                }
            }
        }
    } ?: Text("No wallet or token selected", color = colorScheme.onSurface)
}

@Composable
fun WalletAddressesContent(
    viewModel: appViewModel,
    onWalletAddressClick: (String) -> Unit,
    onAddWalletAddressClick: () -> Unit
) {
    val walletAddresses by viewModel.allWalletAddresses.observeAsState(initial = emptyList())
    LazyColumn(
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AddSignerCard(
                onClick = { onAddWalletAddressClick() }
            )
        }
        items(walletAddresses) { address ->
            WalletAddressItem(
                walletAddress = address,
                viewModel = viewModel,
                onClick = {
                    onWalletAddressClick(address.address)
                }
            )
        }
    }
}

@Composable
fun AddWalletAddressScreen(
    viewModel: appViewModel,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var blockchain by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            CustomOutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Name",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )
            CustomOutlinedTextField(
                value = address,
                onValueChange = { address = it },
                placeholder = "Address",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )
            CustomOutlinedTextField(
                value = blockchain,
                onValueChange = { blockchain = it },
                placeholder = "Blockchain",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )
            CustomOutlinedTextField(
                value = token,
                onValueChange = { token = it },
                placeholder = "Token",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
            Spacer(modifier = Modifier.weight(1f))
            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp, max = 64.dp),
                shape = newRoundedShape,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    disabledContainerColor = colorScheme.primaryContainer,
                    disabledContentColor = colorScheme.onPrimaryContainer
                ),
                onClick = {
                    viewModel.insertWalletAddress(
                        WalletAddress(
                            ownerName = name,
                            address = address,
                            blockchain = blockchain,
                            token = token
                        )
                    )
                    onBackClick()
                }
            ) {
                Text(text = "Save")
            }
        }
}

@Composable
fun CustomOutlinedTextFieldWithTwoIcon(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onClick: () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onOpenWalletAddressesBottomSheet: () -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            ) },
        singleLine = true,
        shape = newRoundedShape,
        colors = TextFieldDefaults.colors(
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface,
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
        ),
        maxLines = 1,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = {
                    onClick()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr_code_scanner),
                        contentDescription = "QR",
                        tint = colorScheme.primary,
                        modifier = Modifier.scale(1.2f)
                    )
                }

                IconButton(onClick = {
                    onOpenWalletAddressesBottomSheet()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.wallet_address),
                        contentDescription = "Wallet Addresses",
                        tint = colorScheme.primary,
                        modifier = Modifier.scale(1.2f)
                    )
                }
            }

        }
    )
}


@Composable
fun WalletAddressItem(walletAddress: WalletAddress, viewModel: appViewModel, onClick: (String) -> Unit) {
    Card(
        onClick = {onClick(walletAddress.address)},
        shape = newRoundedShape,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
        border = BorderStroke(width = 0.75.dp, color = colorScheme.primary),

        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = walletAddress.ownerName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    minLines = 1,
                )
                Text(
                    text = walletAddress.address,
                    fontSize = 14.sp,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    minLines = 1,

                    )
                Text(
                    text = walletAddress.blockchain,
                    fontSize = 14.sp,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    minLines = 1,

                    )
                Text(
                    text = walletAddress.token,
                    fontSize = 14.sp,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    minLines = 1,
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ){
                IconButton(
                    onClick = { viewModel.deleteWalletAddress(walletAddress) },
                    modifier = Modifier
                        .scale(1.2f)
                        .alpha(0.9f),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete signer",
                        tint = colorScheme.primary
                    )
                }
            }
        }
    }
}