package com.example.walletapp.appScreens.mainScreens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.res.stringResource
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

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
    var expanded by remember { mutableStateOf(false) }

    val walletAddressExportFilePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val outputStream = context.contentResolver.openOutputStream(uri)!!
                val walletAddresses = viewModel.allWalletAddresses.value ?: emptyList()
                val json = Gson().toJson(walletAddresses)

                outputStream.write(json.toByteArray())
                outputStream.flush()
                outputStream.close()
                Toast.makeText(
                    context,
                    "Адресная книга экспортирована",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun showExportSignersDialog() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, "wallet_address_backup.asfn")
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json"))
        }
        walletAddressExportFilePicker.launch(intent)
    }

    val signersImportFilePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)!!
                val bytes = inputStream.readBytes()
                inputStream.close()

                val json = String(bytes, StandardCharsets.UTF_8)
                val type = object : TypeToken<List<WalletAddress>>() {}.type
                val walletAddresses: List<WalletAddress> = Gson().fromJson(json, type)

                walletAddresses.forEach { walletAddress ->
                    viewModel.insertWalletAddress(walletAddress)
                }

                Toast.makeText(
                    context,
                    "Адресная книга импортирована",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun showImportSignersDialog() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        signersImportFilePicker.launch(intent)
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text( text = stringResource(id = R.string.choosewallet), color = colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = colorScheme.onSurface)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(color = colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                showExportSignersDialog()
                            },
                            text = { Text("Экспорт адресной книги", fontWeight = FontWeight.Light) },
                            leadingIcon = {
                                Icon(Icons.Rounded.Share, contentDescription = "Export")
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = colorScheme.onSurface,
                                leadingIconColor = colorScheme.onSurface
                            )
                        )
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                showImportSignersDialog()
                            },
                            text = { Text("Импорт адресной книги", fontWeight = FontWeight.Light) },
                            leadingIcon = {
                                Icon(painterResource(id = R.drawable.receive), contentDescription = "Import")
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = colorScheme.onSurface,
                                leadingIconColor = colorScheme.onSurface
                            )
                        )
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
    var useAutoExchange by remember { mutableStateOf(false) }  // Новая переменная для CheckBox
    val context = LocalContext.current
    val walletAddressesBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openWalletAddressesBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.surface,
            tonalElevation = 0.dp,
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    stringResource(id = R.string.accept_tx),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    fontSize = 18.sp
                ) },
            text = { Text(
                stringResource(id = R.string.are_you_sure_tx),
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface
            ) },
            confirmButton = {
                TextButton(onClick = {
                    onNextClick()
                    showDialog = false
                }) {
                    Text(
                        stringResource(id = R.string.accept),
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        stringResource(id = R.string.cancel),
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            shape = newRoundedShape
        )
    }

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
    var trxBalance by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(selectedWallet?.addr) {
        selectedWallet?.addr?.let { addr ->
            trxBalance = viewModel.getTrxBalance(addr)
        }
    }

    var showInfoDialog by remember { mutableStateOf(false) }

    if(showInfoDialog){
        AlertDialog(
            containerColor = colorScheme.surface,
            tonalElevation = 0.dp,
            onDismissRequest = { showDialog = false },
            text = {
                Text(
                    stringResource(id = R.string.exchange_explain),
                    fontWeight = FontWeight.Light,
                    color = colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showInfoDialog = false
                }) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface
                    )
                }
            },
            shape = newRoundedShape
        )
    }


    selectedWallet?.let { wallet ->
        selectedToken?.let { token ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(stringResource(id = R.string.wallet_name) + " : " + wallet.addr, color = colorScheme.onSurface)
                    Text(stringResource(id = R.string.balance) + " : " + token.amount + " " + token.name, color = colorScheme.onSurface)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row (
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        CustomOutlinedTextFieldWithTwoIcon(
                            value = address,
                            onValueChange = { address = it },
                            placeholder = stringResource(id = R.string.address),
                            onClick = { openQRBottomSheet = true },
                            onOpenWalletAddressesBottomSheet = { openWalletAddressesBottomSheet = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomOutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = stringResource(id = R.string.amount),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (token.name == "USDT" && trxBalance < 60.0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(onClick = { showInfoDialog = true }) {
                                Icon(Icons.Outlined.Info, contentDescription = "info", tint = colorScheme.primary, modifier = Modifier.scale(1.2f))
                            }
                            Text(
                                text = stringResource(id = R.string.use_auto_exchange),
                                color = colorScheme.onSurface,
                                fontWeight = FontWeight.Normal,
                            )
                            Checkbox(
                                checked = useAutoExchange,
                                onCheckedChange = { useAutoExchange = it },
                                modifier = Modifier
                                    .scale(1.2f),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Transparent,
                                    uncheckedColor = colorScheme.primaryContainer,
                                    checkmarkColor = colorScheme.primary
                                ),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(stringResource(id = R.string.comission), color = colorScheme.onSurface)
                    Text(stringResource(id = R.string.min_comission), color = colorScheme.onSurface)
                    Text(stringResource(id = R.string.max_comission), color = colorScheme.onSurface)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(id = R.string.input_purpose), color = colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomOutlinedTextField(
                        value = paymentPurpose,
                        onValueChange = { paymentPurpose = it },
                        placeholder = stringResource(id = R.string.purpose)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val isButtonEnabled = address.isNotBlank() && amount.isNotBlank() && paymentPurpose.isNotBlank()

                    CustomButton(
                        text = stringResource(id = R.string.send),
                        onClick = {
                            amount.toDoubleOrNull()?.let { amt ->
                                // Здесь можно использовать флаг useAutoExchange для управления API вызовом
                                if (useAutoExchange) {
                                    viewModel.sendTransactionWithAutoExchange(
                                        token = token,
                                        wallet = wallet,
                                        amount = amt,
                                        address = address,
                                        info = paymentPurpose,
                                        context = context
                                    )
                                } else {
                                    viewModel.sendTransaction(
                                        token = token,
                                        wallet = wallet,
                                        amount = amt,
                                        address = address,
                                        info = paymentPurpose,
                                        context = context
                                    )
                                }
                            }
                            showDialog = true
                        },
                        enabled = isButtonEnabled
                    )
                }
            }
        }
    } ?: Text(stringResource(id = R.string.no_wallet_or_token), color = colorScheme.onSurface)
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
                placeholder = stringResource(id = R.string.owner_name),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )
            CustomOutlinedTextField(
                value = address,
                onValueChange = { address = it },
                placeholder = stringResource(id = R.string.owner_address),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )
            CustomOutlinedTextField(
                value = blockchain,
                onValueChange = { blockchain = it },
                placeholder = stringResource(id = R.string.block),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )
            CustomOutlinedTextField(
                value = token,
                onValueChange = { token = it },
                placeholder = stringResource(id = R.string.token),
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
                Text(text = stringResource(id = R.string.save))
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