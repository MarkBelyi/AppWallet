package com.example.walletapp.Screens.appScreens.mainScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.Element.CustomOutlinedTextFieldWithKeyBoardOptions
import com.example.walletapp.AuxiliaryFunctions.Element.CustomOutlinedTextFieldWithLockIcon
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSigner(
    viewModel: appViewModel,
    signerAddress: String,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val signer by viewModel.getSignerAddress(signerAddress).observeAsState()
    val focusManager = LocalFocusManager.current

    // Храним изменяемое состояние для каждого поля
    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val addressState = remember { mutableStateOf("") }
    val telephoneState = remember { mutableStateOf("") }
    val typeState = remember { mutableStateOf("") }
    val isAddressLocked = remember { mutableStateOf(true) }

    LaunchedEffect(signer) {
        nameState.value = signer?.name ?: ""
        emailState.value = signer?.email ?: ""
        addressState.value = signer?.address ?: ""
        telephoneState.value = signer?.telephone ?: ""
        typeState.value = signer?.type?.toString() ?: ""
    }

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.surface,
            tonalElevation = 0.dp,
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    stringResource(id = R.string.confirm_changes),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val updatedSigner = signer?.copy(
                        name = nameState.value,
                        address = addressState.value,
                        email = emailState.value,
                        telephone = telephoneState.value
                    )
                    if (updatedSigner != null) {
                        viewModel.updateSigner(updatedSigner)
                    }
                    showDialog = false
                    onSaveClick()
                }) {
                    Text(
                        stringResource(id = R.string.accept),
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        stringResource(id = R.string.cancel),
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            shape = newRoundedShape
        )
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.edit_signer),
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Normal,

                        )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    scrolledContainerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {

            CustomOutlinedTextFieldWithKeyBoardOptions(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                placeholder = stringResource(id = R.string.name_of_signer),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )

            CustomOutlinedTextFieldWithLockIcon(
                value = addressState.value,
                onValueChange = { addressState.value = it },
                placeholder = stringResource(id = R.string.address_of_signer),
                isLocked = isAddressLocked.value,
                onLockClick = {
                    isAddressLocked.value = !isAddressLocked.value
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )

            CustomOutlinedTextFieldWithKeyBoardOptions(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                placeholder = stringResource(id = R.string.email_of_signer),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down) // Переход к следующему элементу
                })
            )

            CustomOutlinedTextFieldWithKeyBoardOptions(
                value = telephoneState.value,
                onValueChange = { telephoneState.value = it },
                placeholder = stringResource(id = R.string.phone_of_signer),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    showDialog = true
                })
            )

            Spacer(modifier = Modifier.weight(1f))

            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp, max = 64.dp),
                shape = newRoundedShape,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    disabledContainerColor = colorScheme.primaryContainer,
                    disabledContentColor = colorScheme.onPrimaryContainer
                ),
                onClick = { showDialog = true }
            ) {
                Text(stringResource(id = R.string.save_of_signer))
            }
        }
    }
}

