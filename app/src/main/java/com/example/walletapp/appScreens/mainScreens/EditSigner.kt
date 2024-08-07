package com.example.walletapp.appScreens.mainScreens

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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.walletapp.appViewModel.appViewModel
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

    // Обновляем состояния полей, когда объект signer загружен или его данные изменены
    LaunchedEffect(signer) {
        nameState.value = signer?.name ?: ""
        emailState.value = signer?.email ?: ""
        addressState.value = signer?.address ?: ""
        telephoneState.value = signer?.telephone ?: ""
        typeState.value = signer?.type?.toString() ?: ""
    }

    //AlertDialog для подтверждения сохранения изменений
    //TODO(сделать AlertDialog для подтверждения сохранения изменений)

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit Signer", color = colorScheme.onSurface) },
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

            CustomOutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                placeholder = "Name",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )

            CustomOutlinedTextFieldWithLockIcon(
                value = addressState.value,
                onValueChange = { addressState.value = it },
                placeholder = "Address",
                isLocked = isAddressLocked.value,
                onLockClick = {
                    isAddressLocked.value = !isAddressLocked.value
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )

            CustomOutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                placeholder = "Email",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down) // Переход к следующему элементу
                })
            )

            CustomOutlinedTextField(
                value = telephoneState.value,
                onValueChange = { telephoneState.value = it },
                placeholder = "Phone Number",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    // Действие для кнопки "Done"
                    focusManager.clearFocus() // Скрывает клавиатуру
                    val updatedSigner = signer?.copy(
                        name = nameState.value,
                        address = addressState.value,
                        email = emailState.value,
                        telephone = telephoneState.value
                    )
                    if (updatedSigner != null) {
                        viewModel.updateSigner(updatedSigner)
                    }
                    onSaveClick()
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
                onClick = {
                // Обработка нажатия на кнопку "Сохранить"
                val updatedSigner = signer?.copy(
                    name = nameState.value,
                    address = addressState.value,
                    email = emailState.value,
                    telephone = telephoneState.value
                )
                if (updatedSigner != null) {
                    viewModel.updateSigner(updatedSigner)
                }
                onSaveClick()

            }) {
                Text("Save")
            }
        }
    }
}

@Composable
fun CustomOutlinedTextFieldWithLockIcon(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isLocked: Boolean,
    onLockClick: () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            ) },
        singleLine = true,
        shape = newRoundedShape,
        enabled = !isLocked,
        colors = TextFieldDefaults.colors(
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface,
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledContainerColor = colorScheme.surface,
            disabledTextColor = colorScheme.onSurface,
            disabledPlaceholderColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        maxLines = 1,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = {
            IconButton(onClick = {
                onLockClick()
            }) {
                Icon(
                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.Create,
                    contentDescription = "Lock",
                    tint = colorScheme.primary,
                    modifier = Modifier.scale(1.2f)
                )
            }
        }
    )
}