package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletapp.appViewModel.appViewModel

@Composable
fun EditSigner(
    viewModel: appViewModel,
    signerAddress: String,
    onSaveClick: () -> Unit
) {
    val signer by viewModel.getSignerAddress(signerAddress).observeAsState()

    // Храним изменяемое состояние для каждого поля
    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val addressState = remember { mutableStateOf("") }
    val telephoneState = remember { mutableStateOf("") }
    val typeState = remember { mutableStateOf("") }

    // Обновляем состояния полей, когда объект signer загружен или его данные изменены
    LaunchedEffect(signer) {
        nameState.value = signer?.name ?: ""
        emailState.value = signer?.email ?: ""
        addressState.value = signer?.address ?: ""
        telephoneState.value = signer?.telephone ?: ""
        typeState.value = signer?.type?.toString() ?: ""
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Имя") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = addressState.value,
            onValueChange = { addressState.value = it },
            label = { Text("Адрес") }
        )

        Spacer(modifier = Modifier.height(8.dp))


        TextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = telephoneState.value,
            onValueChange = { telephoneState.value = it },
            label = { Text("Номер телефона") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
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
            Text("Сохранить")
        }
    }
}