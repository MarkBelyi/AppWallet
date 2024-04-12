package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape
import kotlinx.coroutines.launch

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
    val signerKeys = remember { mutableStateListOf<String>("") }
    var requiredSigners by remember { mutableStateOf(1f) }

    val qrBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openQRBottomSheet by remember { mutableStateOf(false) }

    val signerBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openSignerBottomSheet by remember { mutableStateOf(false) }


    if(openQRBottomSheet){
        ModalBottomSheet(
            shape = roundedShape,
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
            shape = roundedShape,
            containerColor = colorScheme.surface,
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.create_wallet_title),
                        color = colorScheme.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground,
                    scrolledContainerColor = colorScheme.background
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
                .background(colorScheme.background)
                .padding(padding)
                .padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedNetwork,
                    onValueChange = {},
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.select_blockchain),
                            maxLines = 1,
                            color = Color.Gray
                        )
                    },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = colorScheme.onBackground,
                        unfocusedTextColor = colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded = false},
                    modifier = Modifier.fillMaxWidth()
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

            OutlinedTextField(
                value = walletNameText,
                onValueChange = { walletNameText = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.default_name_wallet),
                        color = Color.Gray
                    )
                },
                shape = roundedShape,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = colorScheme.onBackground,
                    unfocusedTextColor = colorScheme.onBackground,
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Text(
                text = stringResource(id = R.string.signers),
                maxLines = 1,
                color = colorScheme.onBackground
            )

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
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Text(
                text = "Необходимое количество подписантов: ${requiredSigners.toInt()} из ${signerKeys.size}",
                fontSize = 14.sp
            )

            RequiredSignersSelector(
                numberOfSigners = signerKeys.size,
                requiredSigners = requiredSigners.toInt(),
                onRequiredSignersChange = { newRequiredSigners ->
                    requiredSigners = newRequiredSigners.toFloat()
                }
            )

            ElevatedButton(
                onClick = { coroutineScope.launch{
                    //GetAPIString(context, "newWallet", POST = true)
                    //signerKeys - массив содержит все адреса подписантов, что юзер указал. Их отдадим на сервер через запятую
                    val EC = signerKeys
                        .filter { !it.isNullOrEmpty() } // отфильтруем пустые (ну а вдруг!)
                        .toList()

                    var ss:String="" // Json тело запроса
                    ss="\"slist\":{"
                    for (i in 0..EC.size-1) {
                        ss += "\""+i+"\":{\"type\":" + "\"any\"," + "\"ecaddress\":\"" + EC[i] + "\"}"
                        if (i<EC.size-1)ss+=","
                    }
                    // "type":"any" значит что сервер примет любой метод подписи от подписанта[смс, емаил или ECDSA] (но мы пока используем ECDSA)

                    if (requiredSigners>0) // минимальное кол-во подписантов для кворума (минимум один, максимум все) когда-то был возможен ноль.
                        ss+=",\"min_signs\":\""+requiredSigners.toString()+"\""
                    ss+="},"
                    // selectedNetworkid - код сети, меняется в момент смены сети юзером в поле выбора сети
                    ss+="\"network\":\""+selectedNetworkId+"\"," // код сети блокчейна (3000 эфир, 5000 трон итд)
                    ss+="\"info\":\""+walletNameText+"\"" // Имя кошелька
                    // Когда ss набит инфой, шлём его на сервер:
                    viewModel.createWallet(context,ss)
                }
                    onCreateClick()
                },
                shape = roundedShape,
                enabled = walletNameText.isNotEmpty() && signerKeys.all { it.isNotEmpty() } && requiredSigners <= signerKeys.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text(text = stringResource(id = R.string.createWallet))
            }
        }
    }
}

@Composable
fun SignerRow(
    index: Int,
    signerKeys: MutableList<String>,
    numberOfSigner: Int,
    onSignerIconClick: (Int) -> Unit,
    onQrScanClick: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedTextColor = colorScheme.onBackground,
                unfocusedTextColor = colorScheme.onBackground,
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        IconButton(onClick = { onQrScanClick(index) }) {
            Icon(
                painter = painterResource(id = R.drawable.qr_code_scanner),
                contentDescription = "QR",
                tint = colorScheme.primary,
                modifier = Modifier.scale(1.2f)
            )
        }

        IconButton(onClick = { onSignerIconClick(index) }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Choose Signer",
                tint = colorScheme.primary,
                modifier = Modifier.scale(1.2f)
            )
        }
    }

    fun addSigner() {
        signerKeys.add("")
    }
    fun removeSigner(index: Int) {
        signerKeys.removeAt(index)
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (index == signerKeys.lastIndex) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = { removeSigner(index) },
                enabled = signerKeys.size > 1, // Disable if this is the only signer left
                //modifier = Modifier.weight(1f),
                shape = roundedShape,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 16.dp,
                    disabledElevation = 8.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.background,
                    contentColor = colorScheme.onBackground,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.DarkGray
                )
            ) {
                Text(
                    text = "Add",
                    //modifier = Modifier.scale(1.5f)
                )
            }
            Spacer(Modifier.width(8.dp))
            TextButton(
                onClick = { addSigner() },
                shape = roundedShape,
                enabled = signerKeys.size < numberOfSigner, // Disable if the max number of signers is reached
                //modifier = Modifier.weight(1f),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 16.dp,
                    disabledElevation = 8.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.background,
                    contentColor = colorScheme.onBackground,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.DarkGray
                )
            ) {
                Text(
                    text = "Remove",
                    //modifier = Modifier.scale(1.5f)
                    )
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
        Button(
            onClick = {
                if (requiredSigners > 1) onRequiredSignersChange(requiredSigners - 1)
            },
            shape = roundedShape,
            enabled = requiredSigners > 1
        ) {
            Text("-")
        }
        Text(
            text = "$requiredSigners из $numberOfSigners",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Button(
            onClick = {
                if (requiredSigners < numberOfSigners) onRequiredSignersChange(requiredSigners + 1)
            },
            shape = roundedShape,
            enabled = requiredSigners < numberOfSigners
        ) {
            Text("+")
        }
    }
}


