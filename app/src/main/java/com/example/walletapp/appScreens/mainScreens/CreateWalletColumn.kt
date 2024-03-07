/*
package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletColumn(viewModel: appViewModel){
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

    Column(modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ){
        Text(
            text = "Выберите сеть блокчейна",
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground
        )

        Box{
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedNetwork.ifEmpty { "Выберите сеть" },
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
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
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Light
                                )
                            },
                            onClick = {
                                selectedNetwork = network.network_name
                                expanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Text(
            text = "Название кошелька",
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextField(
            value = walletNameText,
            onValueChange = {walletNameText = it},
            singleLine = true,
            maxLines = 1,
            shape = roundedShape,
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )

        )

        Text(
            text = "Подписанты",
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn(
            userScrollEnabled = true
        ) {
            items(signerKeys.size) { index ->
                TextField(
                    value = signerKeys[index],
                    onValueChange = { signerKeys[index] = it },
                    singleLine = true,
                    maxLines = 1,
                    shape = roundedShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )

                )
                if (index == signerKeys.lastIndex && signerKeys.size < numberOfSigner) {
                    IconButton(onClick = { signerKeys.add("") }) {
                        Icon(Icons.Default.Add, "add another one signer")
                    }
                }
            }
        }

        Text(
            text = "Необходимое количество подписантов: ${requiredSigners.toInt()} из ${signerKeys.size}",
            modifier = Modifier.constrainAs(requiredSignersLabel) {
                top.linkTo(signerKeysList.bottom)
                bottom.linkTo(requiredSignersSlider.top, margin = 8.dp)
            },
            fontSize = 14.sp
        )

        Slider(
            value = requiredSigners,
            onValueChange = { newSigners ->
                requiredSigners = newSigners.coerceIn(1f, signerKeys.size.toFloat())
            },
            onValueChangeFinished = {
                // Этот блок вызывается, когда пользователь отпускает слайдер.
                // Можно использовать для финализации значения, если нужно.
            },
            valueRange = 1f..numberOfSigner.toFloat(),
            steps = numberOfSigner - 1,
            modifier = Modifier
                .constrainAs(requiredSignersSlider) {
                    bottom.linkTo(createButton.top, margin = 8.dp)
                }
                .fillMaxWidth()
        )


        ElevatedButton(
            onClick = {},
            shape = roundedShape,
            enabled = walletNameText.isNotEmpty() && signerKeys.all { it.isNotEmpty() } && requiredSigners <= signerKeys.size,
            modifier = Modifier
                .constrainAs(createButton) {
                    bottom.linkTo(parent.bottom, margin = 8.dp) }
                .fillMaxWidth()
                .height(45.dp)
        ) {
            Text("Создать кошелек")
        }

    }


}*/
