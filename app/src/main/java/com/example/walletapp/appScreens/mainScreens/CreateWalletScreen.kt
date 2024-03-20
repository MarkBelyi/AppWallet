package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.R
import com.example.walletapp.Server.GetAPIString
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletScreen(viewModel: appViewModel, onCreateClick: () -> Unit) {
    var selectedNetworkId by remember { mutableStateOf<Int?>(null) }

    var selectingSignerIndex by remember { mutableStateOf<Int?>(null) }
    var selectedSignerAddress by remember { mutableStateOf<String?>(null) }

    var isQrScannerActive by remember { mutableStateOf(false) }
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



    LaunchedEffect(networks) {
        if (networks.isEmpty()) {
            coroutineScope.launch {
                viewModel.addNetworks(context)
            }
        }
        if (networks.isNotEmpty() && selectedNetwork.isEmpty()) {
            selectedNetwork = networks.first().network_name
        }
    }

    if (isQrScannerActive) {
        QrScreen { result ->
            if (selectingSignerIndexQR != null) {
                signerKeys[selectingSignerIndexQR!!] = result
                isQrScannerActive = false
            }
        }
    } else if (selectingSignerIndex != null) {
        SignersScreen(viewModel = viewModel, onCurrentSignerClick = { address ->
            selectedSignerAddress = address
            signerKeys[selectingSignerIndex!!] = address
            selectingSignerIndex = null
        })
    }
    else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "Выберите сеть блокчейна",
                maxLines = 1,
                color = colorScheme.onBackground
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedNetwork.ifEmpty { "" },
                    onValueChange = { },
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
                label = { Text("Название кошелька") },
                shape = roundedShape,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = colorScheme.onBackground,
                    unfocusedTextColor = colorScheme.onBackground,
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Text(
                text = "Подписанты",
                maxLines = 1,
                color = colorScheme.onBackground
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(signerKeys.size) { index ->
                    SignerRow(
                        index = index,
                        signerKeys = signerKeys,
                        numberOfSigner = numberOfSigner,
                        onSignerIconClick = {
                            selectingSignerIndex = index
                        },
                        onQrScanClick = {
                            selectingSignerIndexQR = index
                            isQrScannerActive = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Text(
                text = "Необходимое количество подписантов: ${requiredSigners.toInt()} из ${signerKeys.size}",
                fontSize = 14.sp
            )

            Slider(
                value = requiredSigners,
                onValueChange = { newSigners ->
                    requiredSigners = newSigners.coerceIn(1f, signerKeys.size.toFloat())
                },
                valueRange = 1f..numberOfSigner.toFloat(),
                steps = numberOfSigner - 1,
                modifier = Modifier
                    .fillMaxWidth()
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
                    .height(45.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text("Создать кошелек")
            }
        }
    }
}

@Composable
fun SignerRow(index: Int, signerKeys: MutableList<String>, numberOfSigner: Int, onSignerIconClick: (Int) -> Unit, onQrScanClick: (Int) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = signerKeys[index],
            onValueChange = { signerKeys[index] = it },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = roundedShape,
            colors = TextFieldDefaults.colors(
                focusedTextColor = colorScheme.onBackground,
                unfocusedTextColor = colorScheme.onBackground,
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )

        )
        IconButton(onClick = {
            onQrScanClick(index)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.qr_code_scanner),
                contentDescription = "QR",
                tint = colorScheme.primary,
                modifier = Modifier.scale(1.2f)
            )
        }
        IconButton(onClick = {
            onSignerIconClick(index)
        }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Choose Signer",
                tint = colorScheme.primary,
                modifier = Modifier.scale(1.2f)
            )
        }
    }
    if (index == signerKeys.lastIndex && signerKeys.size < numberOfSigner) {
        IconButton(onClick = { signerKeys.add("") }) {
            Icon(Icons.Filled.Add, "Добавить подписанта")
        }
    }
}


