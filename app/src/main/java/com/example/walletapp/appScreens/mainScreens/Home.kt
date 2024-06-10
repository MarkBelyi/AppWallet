package com.example.walletapp.appScreens.mainScreens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.walletapp.appScreens.Actions
import com.example.walletapp.appScreens.actionItems
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils

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
    val context  = LocalContext.current

    var qrScanResult by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val preventSecondBottomSheetReopening by remember { mutableStateOf(false) }
    var openQRBottomSheet by remember { mutableStateOf(false) }
    var openSecondBottomSheet by remember { mutableStateOf(false) }
    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val secondBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
        val (gridRef, button) = createRefs()

        ActionGrid(actionItems = actionItems, onItemClick = { itemName ->
            when (itemName) {
                Actions.settings -> onSettingsClick()
                Actions.QR -> { openQRBottomSheet = true }
                Actions.shareMyAddr -> onShareClick()
                Actions.signers -> onSignersClick()
                Actions.createWallet -> onCreateWalletClick()
                Actions.send -> onSend()
                Actions.recieve -> onReceive()
                Actions.history -> onHistory()
                else -> onMatrixClick()
            }
        }, modifier = Modifier.constrainAs(gridRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        })

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
){
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
                .fillMaxSize() // Заполняет весь доступный размер
                .aspectRatio(1f)
        )  {
            Box(
                contentAlignment = Alignment.Center
            ){
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
            ){
                // basicMarquee создаёт автопрокрутку!
                Text(
                    text = text,
                    maxLines = 2,
                    /*modifier = Modifier.basicMarquee(
                        iterations = Int.MAX_VALUE,
                        animationMode = MarqueeAnimationMode.Immediately,
                        delayMillis = 1000),*/
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
