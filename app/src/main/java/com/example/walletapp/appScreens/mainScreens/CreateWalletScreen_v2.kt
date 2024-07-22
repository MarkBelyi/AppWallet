package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateWalletScreen_v2(
    viewModel: appViewModel,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit
) {

    val context = LocalContext.current
    val networks by viewModel.networks.observeAsState(initial = emptyList())

    val coroutineScope = rememberCoroutineScope()

    val numberOfSigner = 9
    val signerKeys = remember { mutableStateListOf("") }

    var walletNameText by remember { mutableStateOf("") }
    var selectedNetwork by remember { mutableStateOf("") }
    var selectedNetworkId by remember { mutableStateOf<Int?>(null) }

    var requiredSigners by remember { mutableIntStateOf(1) }

    fun checkWalletName(walletNameText: String): String {
        return walletNameText.ifEmpty { "Wallet Name" }
    }

    fun checkNetwork(selectedNetwork: String): String {
        return selectedNetwork.ifEmpty { "Network" }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.create_wallet_title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->

        ConstraintLayout(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {

            val (gridRef, pager) = createRefs()
            val pageCount = 3
            val pagerState = rememberPagerState(pageCount = { pageCount })
            val pages = listOf(Step.Name, Step.Network, Step.Signers)
            Column(
                modifier = Modifier
                    .constrainAs(pager) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(gridRef.top)
                    }
                    .padding(top = 16.dp, bottom = 12.dp)
                    .background(color = MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.9f)
                        .background(color = MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.Top,
                    key = { it },
                    userScrollEnabled = false
                ) { pageIndex ->
                    when (pages[pageIndex]) {
                        Step.Name -> NameStep(walletNameText) { walletNameText = it }
                        Step.Network -> NetworkStep(
                            networks = networks,
                            selectedNetwork = selectedNetwork,
                            onNetworkSelected = { networkName, networkId ->
                                selectedNetwork = networkName
                                selectedNetworkId = networkId
                            }
                        )

                        Step.Signers -> SignersStep(
                            viewModel = viewModel,
                            signerKeys = signerKeys,
                            numberOfSigner = numberOfSigner,
                            requiredSigners = requiredSigners,
                            onRequiredSignersChange = { newRequiredSigners ->
                                requiredSigners = newRequiredSigners
                            }
                        )
                    }

                }


                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(0.1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.3f)
                            .align(Alignment.CenterVertically)
                    ) {
                        if (pagerState.currentPage != 0) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }, modifier = Modifier
                                    .padding(end = 32.dp, start = 40.dp)
                                    .height(48.dp)
                                    .align(Alignment.Center)
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                            }
                        }
                    }

                    var inBetweenText = ""

                    when (pagerState.currentPage) {
                        0 -> inBetweenText = checkWalletName(walletNameText)
                        1 -> inBetweenText = checkNetwork(selectedNetwork)
                        2 -> inBetweenText = "Create"
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxWidth()
                            .padding(4.dp)
                            .align(alignment = Alignment.CenterVertically),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = inBetweenText,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.3f)
                            .align(Alignment.CenterVertically)
                    ) {
                        if (pagerState.currentPage != 2) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }, modifier = Modifier
                                    .padding(start = 32.dp, end = 40.dp)
                                    .height(48.dp)
                                    .align(Alignment.Center)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.ArrowForward, "Forward"
                                )
                            }
                        }
                        if (selectedNetworkId != null && walletNameText != "" && pagerState.currentPage == 2) {
                            val showDialog = remember { mutableStateOf(false) }

                            ElevatedButton(
                                onClick = {
                                    showDialog.value = true
                                },
                                shape = newRoundedShape,
                                enabled = walletNameText.isNotEmpty() && signerKeys.all { it.isNotEmpty() } && requiredSigners <= signerKeys.size,
                                modifier = Modifier
                                    .height(48.dp)
                                    .padding(start = 12.dp),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.done),
                                    contentDescription = "Create Wallet",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            if (showDialog.value) {
                                AlertDialog(
                                    onDismissRequest = {
                                        showDialog.value = false
                                    },
                                    title = {
                                        Text(text = "Вы готовы создать кошелек?")
                                    },
                                    text = {
                                        Text("Далее вы не сможете изменить данные, касающиеся вашего кошелька. Если вы не уверены в введенной информации, пожалуйста, перепроверьте ее.")
                                    },
                                    shape = newRoundedShape,
                                    confirmButton = {
                                        TextButton(onClick = {
                                            viewModel.createNewWallet(
                                                context = context,
                                                signerKeys = signerKeys,
                                                requiredSigners = requiredSigners,
                                                selectedNetworkId = selectedNetworkId.toString(),
                                                walletNameText = walletNameText,
                                                onComplete = onCreateClick
                                            )
                                            onCreateClick()
                                        }) {
                                            Text("Да", color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = {
                                            showDialog.value = false
                                        }) {
                                            Text("Нет", color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                )
                            }
                        }
                    }

                }

            }

        }

    }

}

sealed class Step {
    data object Name : Step()
    data object Network : Step()
    data object Signers : Step()
}

@Composable
fun NameStep(walletNameText: String, onNameChange: (String) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {

        Text(
            text = "Введите имя кошелька:",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        OutlinedTextField(
            value = walletNameText,
            onValueChange = onNameChange,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 0.5.dp,
                    shape = newRoundedShape,
                    color = MaterialTheme.colorScheme.primary
                ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.default_name_wallet),
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)
                )
            },
            shape = newRoundedShape,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        Text(
            text = "Зачем это нужно? ",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Text(
            text = "Имя вашего кошелька поможет вам легко идентифицировать его среди других кошельков. Это особенно полезно, если вы используете несколько кошельков для различных целей, например, для личных и рабочих нужд.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        )

        Text(
            text = "Выбор уникального и запоминающегося имени сделает использование вашего кошелька более удобным.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkStep(
    networks: List<Networks>,
    selectedNetwork: String,
    onNetworkSelected: (String, Int?) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {

        Text(
            text = "Выберите блокчейн-сеть кошелька:",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    shape = newRoundedShape,
                    color = MaterialTheme.colorScheme.primary
                )
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface, shape = newRoundedShape)
        ) {
            TextField(
                value = selectedNetwork,
                onValueChange = {},
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.select_blockchain),
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.scrim
                    )
                },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = newRoundedShape
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clip(newRoundedShape),
            ) {
                networks.forEach { network ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = network.network_name,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Normal
                            )
                        },
                        onClick = {
                            onNetworkSelected(network.network_name, network.network_id)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Text(
            text = "Зачем это нужно? ",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Text(
            text = "Разные блокчейн-сети предлагают различные функции и уровни безопасности. Выбор сети зависит от ваших целей и нужд.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        )

        val text = buildAnnotatedString {
            append("Вы можете включить или отключить показ тестовых сетей в настройках, выбрав опцию ")

            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append("\"Показывать тестовые сети\".")
            }
        }

        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignersStep(
    viewModel: appViewModel,
    signerKeys: SnapshotStateList<String>,
    numberOfSigner: Int,
    requiredSigners: Int,
    onRequiredSignersChange: (Int) -> Unit
) {
    var selectingSignerIndex by remember { mutableStateOf<Int?>(null) }
    var selectingSignerIndexQR by remember { mutableStateOf<Int?>(null) }
    var openQRBottomSheet by remember { mutableStateOf(false) }
    var openSignerBottomSheet by remember { mutableStateOf(false) }
    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val signerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (openQRBottomSheet) {
        ModalBottomSheet(
            shape = topRoundedShape,
            tonalElevation = 0.dp,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
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
                    if (selectingSignerIndexQR != null) {
                        signerKeys[selectingSignerIndexQR!!] = result
                    }
                    openQRBottomSheet = false
                }
            )
        }
    }

    if (openSignerBottomSheet) {
        ModalBottomSheet(
            shape = topRoundedShape,
            tonalElevation = 0.dp,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            sheetState = signerBottomSheetState,
            onDismissRequest = { openSignerBottomSheet = false },
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                }
            }
        ) {
            val signers by viewModel.allSigners.observeAsState(initial = emptyList())
            val sortedSigners = signers.sortedWith(compareByDescending<Signer> { it.isFavorite }.thenBy { it.name })

            LazyColumn(
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(sortedSigners) { signer ->
                    SignerItem(
                        signer = signer,
                        viewModel = viewModel,
                        onClick = {
                            if (selectingSignerIndex != null) {
                                signerKeys[selectingSignerIndex!!] = signer.address
                                openSignerBottomSheet = false
                            }
                        }
                    )
                }

            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {

        Text(
            text = stringResource(id = R.string.signers),
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()

        )

        LazyColumn(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 8.dp)
                .weight(1f),
        ) {
            items(signerKeys.size) { index ->
                SignerRow_v2(
                    viewModel = viewModel,
                    index = index,
                    signerKeys = signerKeys,
                    numberOfSigner = numberOfSigner,
                    onSignerIconClick = {
                        selectingSignerIndex = index
                        openSignerBottomSheet = true
                    },
                    onQrScanClick = {
                        selectingSignerIndexQR = index
                        openQRBottomSheet = true
                    },
                    onDismiss = { removeIndex ->
                        if (signerKeys.size > 1) signerKeys.removeAt(removeIndex)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text =
                "Необходимое количество подписантов: $requiredSigners "
                        + stringResource(id = R.string.of)
                        + " ${signerKeys.size}",

                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
        }

        RequiredSignersSelector_v2(
            numberOfSigners = signerKeys.size,
            requiredSigners = requiredSigners,
            onRequiredSignersChange = onRequiredSignersChange
        )
    }

}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SignerRow_v2(
    viewModel: appViewModel,
    index: Int,
    signerKeys: MutableList<String>,
    numberOfSigner: Int,
    onSignerIconClick: (Int) -> Unit,
    onQrScanClick: (Int) -> Unit,
    onDismiss: (Int) -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 70.dp.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to -1)

    fun addSigner() {
        signerKeys.add("")
    }


    if (swipeableState.currentValue == -1) {
        LaunchedEffect(swipeableState) {
            swipeableState.animateTo(0)
            onDismiss(index)
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .background(Color.Transparent)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val offset = swipeableState.offset.value
                    translationX = offset
                    alpha = 1f - abs(offset) / sizePx
                }
                .background(
                    if (swipeableState.offset.value < -sizePx / 2) Color.Transparent else Color.Transparent,
                    shape = newRoundedShape
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = newRoundedShape
                    )
                    .background(MaterialTheme.colorScheme.surface, shape = newRoundedShape)
                    .fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = signerKeys[index],
                    onValueChange = { signerKeys[index] = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.new_signer),
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = newRoundedShape,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(0.5f)
                )

                IconButton(
                    onClick = { onQrScanClick(index) },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr_code_scanner),
                        contentDescription = "QR",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(1.2f)
                    )
                }

                IconButton(
                    onClick = { onSignerIconClick(index) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Choose Signer",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .scale(1.2f)
                    )
                }

            }

            if (index == signerKeys.lastIndex) {
                IconButton(
                    onClick = { viewModel.removeSigner(index, signerKeys) },
                    enabled = signerKeys.size > 1,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "remove",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(1.2f)
                    )
                }
            }
        }

        if (swipeableState.offset.value < -sizePx / 2) {
            Text(
                "Remove item?",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .graphicsLayer {
                        alpha = max(0f, -2 * swipeableState.offset.value / sizePx - 1)
                    }
            )
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    if (index == signerKeys.lastIndex) {
        if (index < 8) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { addSigner() },
                    enabled = signerKeys.size < numberOfSigner
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,

                        contentDescription = "add",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(1.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun RequiredSignersSelector_v2(
    numberOfSigners: Int,
    requiredSigners: Int,
    onRequiredSignersChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        ElevatedButton(
            onClick = {
                if (requiredSigners > 1) onRequiredSignersChange(requiredSigners - 1)
            },
            shape = newRoundedShape,
            enabled = requiredSigners > 1,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),

            ) {
            Text("-")
        }

        Text(
            text = "$requiredSigners " + stringResource(id = R.string.of) + " $numberOfSigners",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurface
        )

        ElevatedButton(
            onClick = {
                if (requiredSigners < numberOfSigners) onRequiredSignersChange(requiredSigners + 1)
            },
            shape = newRoundedShape,
            enabled = requiredSigners < numberOfSigners,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text("+")
        }
    }
}