@file:OptIn(ExperimentalFoundationApi::class)

package com.example.walletapp.Screens.appScreens.mainScreens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.ENUM.Actions
import com.example.walletapp.AuxiliaryFunctions.Element.ActionGrid
import com.example.walletapp.AuxiliaryFunctions.Element.AssetsWidget
import com.example.walletapp.AuxiliaryFunctions.SealedClass.Page
import com.example.walletapp.R
import com.example.walletapp.Screens.appScreens.actionItems
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val tokens by viewModel.tokens.observeAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPreferences =
        context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
    val advancedUser = sharedPreferences.getBoolean("advanced_user", false)

    var qrScanResult by remember { mutableStateOf<String?>(null) }
    var showUnavailableFeatureDialog by remember { mutableStateOf(false) }

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
        } else {
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

    LaunchedEffect(tokens) {
        coroutineScope.launch {
            viewModel.getTokensInfoComission()
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

    if (showUnavailableFeatureDialog) {
        AlertDialog(
            onDismissRequest = { showUnavailableFeatureDialog = false },
            confirmButton = {
                TextButton(onClick = { showUnavailableFeatureDialog = false }) {
                    Text("OK")
                }
            },
            shape = roundedShape,
            containerColor = colorScheme.surface,
            textContentColor = colorScheme.onSurface,
            titleContentColor = colorScheme.primary,
            title = {
                Text(
                    text = stringResource(id = R.string.unavailable_fun),
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.explain_unavailable_fun),
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light
                )
            }
        )
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
            val pageCount = 1
            val pagerState = rememberPagerState(pageCount = { pageCount })
            val pages = listOf(Page.Assets)

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
                Actions.Settings -> onSettingsClick()
                Actions.QR -> {
                    openQRBottomSheet = true
                }

                Actions.ShareMyAddress -> onShareClick()
                Actions.Signers -> onSignersClick()
                Actions.CreateWallet -> if (advancedUser) onCreateWalletClick() else onCreateSimpleWalletClick()
                Actions.Send -> onSend()
                Actions.Receive -> onReceive()
                Actions.SignHistory -> onSignHistory()
                Actions.BuyCrypto -> onPurchase()
                Actions.TxHistory -> onTxHistory()
                Actions.CoSigner -> {
                    showUnavailableFeatureDialog = true
                }

                Actions.Support -> {
                    showUnavailableFeatureDialog = true
                }
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
                    Toast.makeText(context, R.string.empty_string, Toast.LENGTH_SHORT).show()
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
            Text(stringResource(id = R.string.send))
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = {
                if (qrResult != null) {
                    onHideButtonClick()
                    viewModel.addNewSignerFromQR(qrResult)
                    Toast.makeText(context, R.string.signer_plus, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, R.string.empty_string, Toast.LENGTH_SHORT).show()
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
            Text(stringResource(id = R.string.new_signer))
        }

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
            onClick = {
                if (qrResult != null) {
                    onHideButtonClick()
                    viewModel.addNewAddressFromQR(qrResult)
                    Toast.makeText(context, R.string.add_wallet_address, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, R.string.empty_string, Toast.LENGTH_SHORT).show()
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
            Text(stringResource(id = R.string.new_wallet_address))
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
