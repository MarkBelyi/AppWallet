@file:OptIn(ExperimentalFoundationApi::class)

package com.example.walletapp.appScreens.mainScreens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.walletapp.appScreens.Actions
import com.example.walletapp.appScreens.actionItems
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Home(
    viewModel: appViewModel,
    onSettingsClick: () -> Unit,
    onShareClick: () -> Unit,
    onSignersClick: () -> Unit,
    onCreateWalletClick: () -> Unit,
    onMatrixClick: () -> Unit,
    onSend: () -> Unit,
    onReceive: () -> Unit,
    onSignHistory: () -> Unit,
    onPurchase: () -> Unit,
    onTxHistory: () -> Unit,
    onCreateSimpleWalletClick: () -> Unit,
) {
    val networks by viewModel.networks.observeAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
    val advancedUser = sharedPreferences.getBoolean("advanced_user", false)

    var qrScanResult by remember { mutableStateOf<String?>(null) }

    val preventSecondBottomSheetReopening by remember { mutableStateOf(false) }
    var openQRBottomSheet by remember { mutableStateOf(false) }
    var openSecondBottomSheet by remember { mutableStateOf(false) }
    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val secondBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(networks) {
        if (networks.isEmpty()) {
            coroutineScope.launch {
                viewModel.addNetworks(context = context)
                viewModel.refreshNetworks()
            }
        }else{
            coroutineScope.launch {
                viewModel.refreshNetworks()
            }
        }
    }

    LaunchedEffect(openSecondBottomSheet && !preventSecondBottomSheetReopening) {
        if (openSecondBottomSheet) {
            secondBottomSheetState.show()
        }
    }

    if (openQRBottomSheet) {
        ModalBottomSheet(
            shape = topRoundedShape,
            containerColor = colorScheme.surface,
            sheetState = qrBottomSheetState,
            onDismissRequest = { openQRBottomSheet = false },
            tonalElevation = 0.dp,
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
                    qrScanResult = result
                    openSecondBottomSheet = true
                }
            )
        }
    }

    if (openSecondBottomSheet) {
        ModalBottomSheet(
            shape = topRoundedShape,
            containerColor = colorScheme.surface,
            sheetState = secondBottomSheetState,
            onDismissRequest = { openSecondBottomSheet = false },
        ) {

            SecondBottomSheetContent(
                viewModel = viewModel,
                qrResult = qrScanResult,
                context = context,
                onHideButtonClick = {
                    coroutineScope.launch {
                        qrBottomSheetState.hide()
                        delay(300)
                        openQRBottomSheet = false
                        secondBottomSheetState.hide()
                        delay(300)
                        openSecondBottomSheet = false
                    }
                },
                onSend = {
                    onSend()
                }
            )

        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .padding(paddingColumn)
    ) {
        val (gridRef, assetsWidget) = createRefs()

        Column(
            modifier = Modifier
                .background(color = colorScheme.surface, shape = newRoundedShape)
                .fillMaxWidth()
                .constrainAs(assetsWidget) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(gridRef.top)
                }
        ) {
            val pageCount = 1//4
            val pagerState = rememberPagerState(pageCount = { pageCount })
            val pages = listOf(Page.Assets/*, Page.Future0, Page.Future1, Page.Future2*/)

            HorizontalPager(state = pagerState,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .background(color = colorScheme.surface, shape = newRoundedShape)
                    .align(alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                key = { it }
            ) { pageIndex ->
                when (pages[pageIndex]) {
                    Page.Assets -> AssetsWidget(viewModel = viewModel)
                    /*Page.Future0 -> Future(pageNum = pagerState.currentPage)
                    Page.Future1 -> Future(pageNum = pagerState.currentPage)
                    Page.Future2 -> Future(pageNum = pagerState.currentPage)*/
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                items(pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.LightGray else Color.DarkGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp, bottom = 8.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(6.dp)
                    )
                }
            }
        }

        ActionGrid(actionItems = actionItems, onItemClick = { itemName ->
            when (itemName) {
                Actions.settings -> onSettingsClick()
                Actions.QR -> { openQRBottomSheet = true }
                Actions.shareMyAddr -> onShareClick()
                Actions.signers -> onSignersClick()
                Actions.createWallet -> if(advancedUser) onCreateWalletClick() else onCreateSimpleWalletClick()
                Actions.send -> onSend()
                Actions.recieve -> onReceive()
                Actions.signHistory -> onSignHistory()
                Actions.buyCrypto -> onPurchase()
                Actions.txHistory -> onTxHistory()
                Actions.coSigner -> {}
                Actions.support -> {}
                else -> onMatrixClick()
            }
        }, modifier = Modifier
            .constrainAs(gridRef) {
                top.linkTo(assetsWidget.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )
    }
}

sealed class Page {
    data object Assets : Page()
}

@Composable
fun AssetsWidget(viewModel: appViewModel) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Мои Активы:",
            maxLines = 1,
            color = colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )

        BalancesView(viewModel = viewModel)

    }
}

@Composable
fun BalancesView(viewModel: appViewModel) {
    val combinedBalances by viewModel.getCombinedBalances().observeAsState(initial = emptyMap())

    Column(modifier = Modifier.padding(4.dp)) {
        val nonZeroBalances = combinedBalances.entries.filter { it.value != 0.0 }

        if (nonZeroBalances.isEmpty()) {
            Text(
                text = "У вас нет доступных активов!",
                maxLines = 1,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
        } else {
            TableHeader()
            if (nonZeroBalances.size > 3) {
                LazyColumn {
                    items(nonZeroBalances) { entry ->
                        BalanceRow(entry.key, entry.value)
                    }
                }
            } else {
                nonZeroBalances.forEach { entry ->
                    BalanceRow(entry.key, entry.value)
                }
            }
        }
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Token",
            fontWeight = FontWeight.Normal,
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
        Text(
            text = "Total Balance",
            fontWeight = FontWeight.Normal,
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            fontSize = 14.sp
        )
    }
}

@Composable
fun BalanceRow(currency: String, balance: Double) {
    Row(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = currency,
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Light,
            fontSize = 12.sp
        )
        Text(
            text = "$balance",
            color = colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp
        )
    }
}

@Composable
fun ActionGrid(
    actionItems: List<Triple<Int, Int, Actions>>,
    onItemClick: (Actions) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = 3
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier
            .background(color = colorScheme.surface, shape = newRoundedShape),
    ) {
        items(actionItems) { actionItem ->
            ActionCell(
                text = stringResource(actionItem.first),
                imageVector = actionItem.second,
                onClick = { onItemClick(actionItem.third) },
                modifier = Modifier
            )
        }
    }
}

@Composable
fun ActionCell(
    text: String,
    imageVector: Int,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1.4f),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        shape = newRoundedShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        interactionSource = interactionSource
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = imageVector),
                    contentDescription = text,
                    modifier = modifier
                        .scale(1.4f),
                    tint = colorScheme.primary
                )
            }
            Spacer(modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    maxLines = 2,
                    style = TextStyle(
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
fun BottomSheetContent(
    onQRScanned: (String) -> Unit
) {
    QrScreen(onScanResult = { result ->
        onQRScanned(result)
    })
}

@Composable
fun SecondBottomSheetContent(
    viewModel: appViewModel,
    qrResult: String?,
    context: Context,
    onHideButtonClick: () -> Unit,
    onSend: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(IntrinsicSize.Min)
    ) {
        ElevatedButton(
            onClick = {
                if (qrResult != null) {
                    onHideButtonClick()
                    viewModel.setQrResult(qrResult)
                    onSend()
                } else {
                    Toast.makeText(context, "Пустая строка адреса", Toast.LENGTH_SHORT).show()
                }
            },
            shape = newRoundedShape,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp, max = 64.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
            )
        ) {
            Text("Перевести")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = {
                if (qrResult != null) {
                    onHideButtonClick()
                    viewModel.addNewSignerFromQR(qrResult)
                    Toast.makeText(context, "Подписант создан", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Пустая строка адреса", Toast.LENGTH_SHORT).show()
                }
            },
            shape = newRoundedShape,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp, max = 64.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
            )
        ) {
            Text("Новый подписант")
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}


@Composable
fun ShowKeyDialog(onDismiss: () -> Unit) {
    val mnemonic = "We are such stuff as dreams are made on"
    val restoreCredentials: Credentials = WalletUtils.loadBip39Credentials(mnemonic, mnemonic)
    val privateKeyBytes = restoreCredentials.ecKeyPair.privateKey.toByteArray()
    val publicKeyBytes = restoreCredentials.ecKeyPair.publicKey.toByteArray()

    val privateKey = privateKeyBytes.toHexString()
    val publicKey = publicKeyBytes.toHexString()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ключи") },
        text = {
            Column {
                Text("Private Key: $privateKey")
                Text("Public Key: $publicKey")
            }
        },
        confirmButton = {
            ElevatedButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

// Extension function to convert ByteArray to hexadecimal String
fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}
