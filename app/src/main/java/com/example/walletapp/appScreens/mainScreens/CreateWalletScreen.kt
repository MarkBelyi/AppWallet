package com.example.walletapp.appScreens.mainScreens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletScreen(viewModel: appViewModel, onCreateClick: () -> Unit, onBackClick: () -> Unit) {
    var selectedNetworkId by remember { mutableStateOf<Int?>(null) }
    var selectingSignerIndex by remember { mutableStateOf<Int?>(null) }
    var selectingSignerIndexQR by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val networks by viewModel.allNetworks.observeAsState(initial = emptyList())

    val numberOfSigner = 9
    var selectedNetwork by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var walletNameText by remember { mutableStateOf("") }
    val signerKeys = remember { mutableStateListOf("") }
    var requiredSigners by remember { mutableIntStateOf(1) }

    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openQRBottomSheet by remember { mutableStateOf(false) }

    val signerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openSignerBottomSheet by remember { mutableStateOf(false) }


    if(openQRBottomSheet){
        ModalBottomSheet(
            shape = topRoundedShape,
            tonalElevation = 0.dp,
            containerColor = colorScheme.inverseSurface,
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

    if(openSignerBottomSheet){
        ModalBottomSheet(
            shape = topRoundedShape,
            tonalElevation = 0.dp,
            containerColor = colorScheme.inverseSurface,
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
            LazyColumn(
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(signers) { signer ->
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

    LaunchedEffect(networks) {
        if (networks.isEmpty()) {
            coroutineScope.launch {
                viewModel.addNetworks(context)
            }
        }
    }

    Scaffold(
        containerColor = colorScheme.inverseSurface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.create_wallet_title),
                        color = colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    scrolledContainerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    )  { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedTextField(
                value = walletNameText,
                onValueChange = { walletNameText = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, shape = newRoundedShape, color = colorScheme.primary),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.default_name_wallet),
                        color = colorScheme.scrim
                    )
                },
                shape = newRoundedShape,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface,
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                modifier = Modifier
                    .border(
                        width = 0.5.dp,
                        shape = newRoundedShape,
                        color = colorScheme.primary
                    )
                    .fillMaxWidth()
                    .background(color = colorScheme.surface, shape = newRoundedShape)
            ) {
                TextField(
                    value = selectedNetwork,
                    onValueChange = {},
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.select_blockchain),
                            maxLines = 1,
                            color = colorScheme.scrim
                        )
                    },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface,
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
                    onDismissRequest = {expanded = false},
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorScheme.surface)
                        .clip(newRoundedShape),
                ) {
                    networks.forEach { network ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = network.network_name,
                                    color = colorScheme.onSurface,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedNetwork = network.network_name
                                selectedNetworkId = network.network_id
                                expanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            ){
                Text(
                    text = stringResource(id = R.string.signers),
                    maxLines = 1,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp),
            ) {
                items(signerKeys.size) { index ->
                    SignerRow(
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

            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    text =
                    "Необходимое количество подписантов: ${requiredSigners} "
                            + stringResource(id = R.string.of)
                            + " ${signerKeys.size}",

                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                )
            }

            RequiredSignersSelector(
                numberOfSigners = signerKeys.size,
                requiredSigners = requiredSigners.toInt(),
                onRequiredSignersChange = { newRequiredSigners ->
                    requiredSigners = newRequiredSigners
                }
            )

            ElevatedButton(
                onClick = {
                    viewModel.createNewWallet(
                        context = context,
                        signerKeys = signerKeys,
                        requiredSigners = requiredSigners,
                        selectedNetworkId = selectedNetworkId.toString(),
                        walletNameText = walletNameText,
                        onComplete = onCreateClick
                    )
                    onCreateClick()
                },
                shape = newRoundedShape,
                enabled = walletNameText.isNotEmpty() && signerKeys.all { it.isNotEmpty() } && requiredSigners <= signerKeys.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    disabledContainerColor = colorScheme.primaryContainer,
                    disabledContentColor = colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = stringResource(id = R.string.createWallet)
                )
            }
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SignerRow(
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

    fun removeSigner(index: Int) {
        signerKeys.removeAt(index)
    }

    if (swipeableState.currentValue == -1) {
        LaunchedEffect(swipeableState) {
            swipeableState.animateTo(0)
            onDismiss(index)
        }
    }

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
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    enabled = signerKeys.size > 1,
                    thresholds = { _, _ -> FractionalThreshold(0.3f) },
                    orientation = Orientation.Horizontal
                )
                .background(
                    if (swipeableState.offset.value < -sizePx / 2) Color.Transparent else Color.Transparent,
                    shape = newRoundedShape
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .border(width = 0.5.dp, color = colorScheme.primary, shape = newRoundedShape)
                    .background(colorScheme.surface, shape = newRoundedShape)
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
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface,
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface,
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
                        tint = colorScheme.primary,
                        modifier = Modifier.scale(1.2f)
                    )
                }

                IconButton(
                    onClick = { onSignerIconClick(index) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Choose Signer",
                        tint = colorScheme.primary,
                        modifier = Modifier
                            .scale(1.2f)
                    )
                }

            }

            if (index == signerKeys.lastIndex) {
                IconButton(
                    onClick = { removeSigner(index) },
                    enabled = signerKeys.size > 1,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "remove",
                        tint = colorScheme.primary,
                        modifier = Modifier.scale(1.2f)
                    )
                }
            }
        }

        if (swipeableState.offset.value < -sizePx / 2) {
            Text(
                "Remove item?",
                color = colorScheme.onSurface.copy(alpha = 0.5f),
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
        if(index < 8) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { addSigner() },
                    enabled = signerKeys.size < numberOfSigner
                ){
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "add",
                        tint = colorScheme.primary,
                        modifier = Modifier.scale(1.2f)
                    )
                }

            }
        }
    }
}

@Composable
fun RequiredSignersSelector(
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
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                disabledContainerColor = colorScheme.primaryContainer,
                disabledContentColor = colorScheme.onPrimaryContainer
            ),

        ) {
            Text("-")
        }

        Text(
            text = "$requiredSigners " + stringResource(id = R.string.of) + " $numberOfSigners",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight= FontWeight.Light,
            color = colorScheme.onSurface
        )

        ElevatedButton(
            onClick = {
                if (requiredSigners < numberOfSigners) onRequiredSignersChange(requiredSigners + 1)
            },
            shape = newRoundedShape,
            enabled = requiredSigners < numberOfSigners,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                disabledContainerColor = colorScheme.primaryContainer,
                disabledContentColor = colorScheme.onPrimaryContainer
            )
        ) {
            Text("+")
        }
    }
}




