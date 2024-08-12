package com.example.walletapp.Settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.registrationScreens.AuthMethod
import com.example.walletapp.registrationScreens.PinLockScreenApp
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModalBottomSheet(
    showAuthSheet: MutableState<Boolean>,
    onAuthenticated: () -> Unit,
    viewModel: appViewModel
) {

    val context = LocalContext.current
    val passwordStorage = PasswordStorageHelper(context)
    val coroutineScope = rememberCoroutineScope()
    val authMethod by viewModel.getAuthMethod().observeAsState(initial = AuthMethod.PINCODE)
    val isAuthenticated = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newState ->
            newState == SheetValue.Hidden && isAuthenticated.value
        }
    )

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            if (isAuthenticated.value) onAuthenticated()
        },
        dragHandle = null,
        shape = topRoundedShape,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (authMethod) {
                    AuthMethod.PINCODE -> {
                        PinLockScreenApp(
                            onAction = {
                                if (verifyPin(context)) {
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        onAuthenticated()
                                    }
                                } else {
                                    Toast.makeText(context, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onBiometricAuthenticated = onAuthenticated
                        )
                    }
                    AuthMethod.PASSWORD -> {
                        PasswordInputField(onPasswordSubmitted = { password ->
                            if (password == passwordStorage.getPassword("MyPassword")) {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    onAuthenticated()
                                }
                            } else {
                                Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        }
    )

    LaunchedEffect(showAuthSheet.value) {
        coroutineScope.launch {
            if (showAuthSheet.value) {
                sheetState.show()
            } else {
                sheetState.hide()
            }
        }
    }
}

@Composable
fun PasswordInputField(onPasswordSubmitted: (String) -> Unit) {

    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .padding(paddingColumn)
        ) {

        Spacer(modifier = Modifier.weight(0.2f))

        Text(
            text = stringResource(id = R.string.enterPassword),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(0.05f))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text(
                text = stringResource(id = R.string.password_name),
                color = colorScheme.scrim,
                fontWeight = FontWeight.Normal
            ) },
            singleLine = true,
            maxLines = 1,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            textStyle = TextStyle(color = colorScheme.onSurface),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp, max = 72.dp),
            shape = newRoundedShape,
            trailingIcon = {
                IconButton(onClick = {
                    isPasswordVisible = !isPasswordVisible
                }) {
                    val image = painterResource(id = if (isPasswordVisible) R.drawable.ic_baseline_visibility_off_24 else R.drawable.ic_baseline_visibility_24)
                    Icon(
                        painter = image,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                focusedLabelColor = colorScheme.primary,
                unfocusedContainerColor = colorScheme.surface,
                unfocusedLabelColor = colorScheme.onBackground,
                cursorColor = colorScheme.primary
            ),
        )

        Spacer(modifier = Modifier.weight(0.1f))

        ElevatedButton(
            onClick = { onPasswordSubmitted(password) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp, max = 64.dp)
                .padding(top = 5.dp, bottom = 5.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = newRoundedShape,
                    clip = true
                ),
            enabled = password.isNotEmpty(),
            shape = newRoundedShape,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                disabledContainerColor = colorScheme.primaryContainer,
                disabledContentColor = colorScheme.onPrimaryContainer
            )
        ) {
            Text(
                text = "Confirm",
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(modifier = Modifier.weight(0.8f))
    }
}

fun verifyPin(context: Context): Boolean {
    val ps = PasswordStorageHelper(context)
    // Here you would check the pin somehow, typically comparing with stored hash
    return true
}

