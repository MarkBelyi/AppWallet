@file:OptIn(ExperimentalFoundationApi::class)

package com.example.walletapp.appScreens.mainScreens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.navigation.NavHostController
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.appScreens.Actions
import com.example.walletapp.appScreens.actionItems
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
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
    onHistory: () -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current

    var qrScanResult by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val preventSecondBottomSheetReopening by remember { mutableStateOf(false) }
    var openQRBottomSheet by remember { mutableStateOf(false) }
    var openSecondBottomSheet by remember { mutableStateOf(false) }
    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val secondBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

//    val wallets by viewModel.filteredWallets.observeAsState(initial = emptyList())

//    fun combineBalances(): Map<String, Double> {
//        val balances = mutableMapOf<String, Double>()
//
//        wallets.forEach { wallet ->
//            val tokenParts = wallet.tokenShortNames.split(" ")
//            if (tokenParts.size >= 2) {
//                val amount = tokenParts[0].toDoubleOrNull()
//
//                if (amount != null) {
//                    when (wallet.network) {
//                        1000 -> balances["BTC"] = (balances["BTC"] ?: 0.0) + amount
//                        3000 -> balances["ETH"] = (balances["ETH"] ?: 0.0) + amount
//                        3300 -> balances["BNB"] = (balances["BNB"] ?: 0.0) + amount
//                        5000 -> balances["TRX"] = (balances["TRX"] ?: 0.0) + amount
//                        else -> Log.d("combineBalances", "Unknown network: ${wallet.network}")
//                    }
//                }
//            }
//        }
//
//        return balances
//    }



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
                    scope.launch {
                        qrBottomSheetState.hide()
                        delay(300) // Небольшая задержка для завершения анимации
                        openQRBottomSheet = false
                        secondBottomSheetState.hide()
                        delay(300) // Небольшая задержка для завершения анимации
                        openSecondBottomSheet = false
                    }
                },
                navController = navController
            )

        }
    }

    var showKeyDialog by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .padding(paddingColumn)
    ) {
        val (gridRef, button, assetsWidget) = createRefs()

        Column(
            modifier = Modifier
                .constrainAs(assetsWidget) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(gridRef.top)
                }
                .background(color = colorScheme.surface, shape = newRoundedShape)
                .padding(vertical = 12.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 120.dp)
        ) {
//            AssetsWidget(viewModel = viewModel)
//            SwipeableWidgetList(lisOfWidgets, viewModel = viewModel)

            val pageCount:Int = 4

            val pagerState = rememberPagerState(pageCount = {
                pageCount
            })

            val pages = listOf(
                Page.Assets, Page.Future0, Page.Future1, Page.Future2
            )

            HorizontalPager(state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.CenterHorizontally)
                    .background(color = colorScheme.surface,shape = newRoundedShape)
                    .defaultMinSize(minHeight = 104.dp),
                verticalAlignment = Alignment.CenterVertically,
                key = { it }
            ) { pageIndex ->

                when (pages[pageIndex]) {
                    Page.Assets -> AssetsWidget(viewModel = viewModel)
                    Page.Future0 -> Future(pageNum = pagerState.currentPage, modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                    Page.Future1 -> Future(pageNum = pagerState.currentPage, modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                    Page.Future2 -> Future(pageNum = pagerState.currentPage, modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                }
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                items(pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.LightGray else Color.DarkGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
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
                Actions.QR -> {
                    openQRBottomSheet = true
                }

                Actions.shareMyAddr -> onShareClick()
                Actions.signers -> onSignersClick()
                Actions.createWallet -> onCreateWalletClick()
                Actions.send -> onSend()
                Actions.recieve -> onReceive()
                Actions.history -> onHistory()
                else -> onMatrixClick()
            }
        }, modifier = Modifier
            .constrainAs(gridRef) {
                top.linkTo(assetsWidget.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(button.top)
                width = Dimension.fillToConstraints
            }
        )

        if (showKeyDialog) {
            ShowKeyDialog(onDismiss = { showKeyDialog = false })
        }

        ElevatedButton(
            onClick = {
                showKeyDialog = true
            },
            shape = roundedShape,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp, max = 64.dp)
                .constrainAs(button)
                {
                    top.linkTo(gridRef.bottom, margin = 16.dp)
                    bottom.linkTo(parent.bottom)
                },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
            )
        ) {
            Text("Показать ключи")
        }


    }
}

sealed class Page {
    object Assets : Page()
    object Future0 : Page()
    object Future1 : Page()
    object Future2 : Page()
}

@Composable
fun Future(pageNum: Int, modifier: Modifier){
    Text(
        text = "Page: $pageNum",
        modifier = Modifier.fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp)
    )
}

@Composable
fun AssetsWidget(viewModel: appViewModel) {

    val wallets by viewModel.filteredWallets.observeAsState(initial = emptyList())

    Column (modifier = Modifier
        .fillMaxWidth()
        .background(color = colorScheme.surface, shape = newRoundedShape) ){
        Text(
            text = "Мои Активы:",
            maxLines = 1,
            color = colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
        )

        val combinedBalances = combineBalances(wallets)

        LazyRow(
            modifier = Modifier
                .background(color = colorScheme.surface, shape = newRoundedShape)
                .align(alignment = Alignment.CenterHorizontally)

        ) {
            items(combinedBalances.entries.toList()) { entry ->
                BalanceCard(entry.key, entry.value)
            }
        }
    }
}

fun combineBalances(wallets: List<Wallets>): Map<String, Double> {
    val balances = mutableMapOf<String, Double>()

    wallets.forEach { wallet ->
        val tokenParts = wallet.tokenShortNames.split(" ")
        if (tokenParts.size >= 2) {
            val amount = tokenParts[0].toDoubleOrNull()

            if (amount != null) {
                when (wallet.network) {
                    1000 -> balances["BTC"] = (balances["BTC"] ?: 0.0) + amount
                    3000 -> balances["ETH"] = (balances["ETH"] ?: 0.0) + amount
//                    3300 -> balances["BNB"] = (balances["BNB"] ?: 0.0) + amount
                    5000 -> balances["TRX"] = (balances["TRX"] ?: 0.0) + amount
                    else -> Log.d("combineBalances", "Unknown network: ${wallet.network}")
                }
            }
        }
    }

    return balances
}

@Composable
fun BalanceCard(currency: String, balance: Double) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .background(colorScheme.background, shape = newRoundedShape)
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.background(colorScheme.background)) {
            Text(text = "$currency: $balance $currency",
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        }
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
            .background(color = colorScheme.surface, shape = roundedShape),
    ) {
        items(actionItems) { actionItem ->
            ActionCell(
                text = stringResource(actionItem.first),
                imageVector = actionItem.second,
                onClick = { onItemClick(actionItem.third) },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCell(
    text: String,
    imageVector: Int,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1.4f),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        shape = roundedShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        interactionSource = interactionSource
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = imageVector),
                    contentDescription = text,
                    modifier = Modifier
                        .scale(1.4f),
                    tint = colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
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
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(IntrinsicSize.Min)
    )
    {
        ElevatedButton(
            onClick = {
                if (qrResult != null) {
                    onHideButtonClick()
                    navController.navigate("${SendingRoutes.WALLETS}?address=$qrResult")
                } else {
                    Toast.makeText(context, "Пустая строка адреса", Toast.LENGTH_SHORT).show()
                }
            },
            shape = roundedShape,
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
            shape = roundedShape,
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
