package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.DataClass.Blockchain
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun SearchBar(
    searchText: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    viewModel: appViewModel
) {
    val showPopup = remember { mutableStateOf(false) }
    val showHidden by viewModel.showWalletWithTestNetwork.observeAsState(initial = false)
    val blockchains = listOf(
        Blockchain(1000, "Bitcoin (BTC)"),
        Blockchain(3000, "Ethereum (ETH)"),
        Blockchain(5000, "Tron (TRX)")
    ).sortedBy { it.name }
    val selectedBlockchain by viewModel.selectedBlockchain.observeAsState(initial = null)
    val id = when (selectedBlockchain?.id) {
        1000 -> R.drawable.btc
        3000 -> R.drawable.eth
        5000 -> R.drawable.tron
        else -> R.drawable.history_fill1_wght400_grad0_opsz24
    }

    Column(
        modifier = Modifier.background(
            color = colorScheme.surface,
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        )
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colorScheme.onSurface),
            singleLine = true,
            maxLines = 1,
            shape = newRoundedShape,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "search",
                    tint = colorScheme.primary
                )
            },
            trailingIcon = {
                IconButton(onClick = { showPopup.value = true }) {
                    if (selectedBlockchain == null) {
                        Text(
                            stringResource(id = R.string.All),
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Light
                        )
                    } else {
                        Icon(painter = painterResource(id = id), contentDescription = "blockchain")
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                focusedLabelColor = colorScheme.primary,
                unfocusedContainerColor = colorScheme.surface,
                unfocusedLabelColor = colorScheme.onBackground,
                cursorColor = colorScheme.primary
            )
        )

        if (showPopup.value) {
            Popup(
                alignment = Alignment.TopEnd,
                offset = IntOffset(x = -16, y = 200),
                onDismissRequest = { showPopup.value = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .background(colorScheme.surface, shape = newRoundedShape)
                        .border(
                            width = 0.5.dp,
                            color = colorScheme.primary,
                            shape = newRoundedShape
                        )
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = showHidden,
                            onCheckedChange = {
                                viewModel.toggleShowHidden()
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = colorScheme.surface,
                                uncheckedColor = colorScheme.primaryContainer,
                                checkmarkColor = colorScheme.primary
                            ),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.hidden),
                            color = colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Light
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    LazyColumn {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        viewModel.updateSelectedBlockchain(null)
                                        showPopup.value = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.wallet),
                                    contentDescription = null,
                                    tint = colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = R.string.all_wallets),
                                    color = colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Light
                                )
                                if (selectedBlockchain == null) {
                                    Icon(
                                        Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(blockchains) { blockchain ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        viewModel.updateSelectedBlockchain(blockchain)
                                        showPopup.value = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val iconId = when (blockchain.id) {
                                    1000 -> R.drawable.btc
                                    3000 -> R.drawable.eth
                                    5000 -> R.drawable.tron
                                    else -> R.drawable.history_fill1_wght400_grad0_opsz24
                                }
                                Icon(
                                    painter = painterResource(id = iconId),
                                    contentDescription = null,
                                    tint = colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = blockchain.name,
                                    color = colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Light
                                )
                                if (selectedBlockchain == blockchain) {
                                    Icon(
                                        Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}