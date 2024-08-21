package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.DataBase.Entities.TX
import com.example.walletapp.R
import com.example.walletapp.Screens.appScreens.mainScreens.AuthModalBottomSheet
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun SignItem(tx: TX, onSign: () -> Unit, onReject: (String) -> Unit, viewModel: appViewModel) {
    val context = LocalContext.current
    val userAddress = remember { mutableStateOf("") }
    val showAuthSheet = remember { mutableStateOf(false) }
    val authAction = remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(Unit) {
        userAddress.value = GetMyAddr(context)
    }

    val showDialog = remember { mutableStateOf(false) }
    val rejectReason = remember { mutableStateOf("") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(id = R.string.reject_reason)) },
            text = {
                OutlinedTextField(
                    value = rejectReason.value,
                    onValueChange = { rejectReason.value = it },
                    singleLine = true,
                    maxLines = 1,
                    shape = newRoundedShape,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.write_reject_reason),
                            fontWeight = FontWeight.Light
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedContainerColor = colorScheme.surface,
                        unfocusedLabelColor = colorScheme.onBackground,
                        cursorColor = colorScheme.primary
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onReject(rejectReason.value)
                }) {
                    Text(text = "Ок")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (showAuthSheet.value) {
        AuthModalBottomSheet(
            showAuthSheet = showAuthSheet,
            onAuthenticated = {
                authAction.value?.invoke()
            },
            viewModel = viewModel
        )
    }


    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth(),
        shape = newRoundedShape,
        border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.9f)
            ) {
                Text(
                    text = tx.info,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = stringResource(id = R.string.to_address) + tx.to_addr,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = stringResource(id = R.string.amount_of_money) + tx.tx_value + " " + tx.token,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )

                when (tx.status) {
                    0 -> { // IDLE
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showAuthSheet.value = true
                                    authAction.value = {
                                        onSign()
                                        showAuthSheet.value = false
                                    }

                                },
                                shape = RoundedCornerShape(
                                    topStart = 24.dp,
                                    bottomStart = 24.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorScheme.onSurface,
                                    containerColor = colorScheme.surface
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(id = R.string.sign))
                            }

                            OutlinedButton(
                                onClick = {
                                    showAuthSheet.value = true
                                    authAction.value = {
                                        showDialog.value = true
                                        showAuthSheet.value = false
                                    }
                                },
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    bottomStart = 0.dp,
                                    topEnd = 24.dp,
                                    bottomEnd = 24.dp
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorScheme.onSurface,
                                    containerColor = colorScheme.surface
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(id = R.string.reject))
                            }
                        }
                    }

                    2 -> { // SIGNED
                        Text(
                            text = stringResource(id = R.string.you_signed),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            color = colorScheme.primary
                        )
                    }

                    3 -> { // REJECTED
                        Text(
                            text = stringResource(id = R.string.your_reject_reason) + tx.deny,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.error
                        )
                    }
                }
            }

            Column(
                modifier = if (tx.status == 0) {
                    Modifier
                } else {
                    Modifier.weight(0.1f)
                },
                verticalArrangement = Arrangement.Center
            ) {
                when (tx.status) {
                    2 -> {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null,
                            tint = colorScheme.error,
                            modifier = Modifier.scale(1.4f)
                        )
                    }

                    3 -> {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = null,
                            tint = colorScheme.error,
                            modifier = Modifier.scale(1.4f)
                        )
                    }
                }
            }
        }
    }
}
