package com.example.walletapp.registrationScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.walletapp.Element.ClickableText
import com.example.walletapp.Element.CustomButton
import com.example.walletapp.Element.PasswordAlertDialog
import com.example.walletapp.Element.PasswordFieldWithLabel
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.ui.theme.paddingColumn

enum class AuthMethod {
    PASSWORD,
    PINCODE
}

@Composable
fun CreatePasswordScreen(
    onNextAction: () -> Unit,
    onPinCodeClick: () -> Unit,
    viewModel: appViewModel
) {
    var showPasswordAlert by remember { mutableStateOf(false) }
    var passwordAlertMessage by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var repeatPasswordValue by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val passwordStorageHelper = remember { PasswordStorageHelper(context) }
    val passwordErrorMessage = stringResource(id = R.string.alert_password_message)

    val isPasswordValid = remember(passwordValue, repeatPasswordValue) {
        checkPasswordsMatch(passwordValue, repeatPasswordValue) && isPasswordValid(passwordValue)
    }

    if (showPasswordAlert) {
        PasswordAlertDialog(
            message = passwordAlertMessage,
            onDismiss = { showPasswordAlert = false }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(paddingColumn)
    ) {
        Spacer(modifier = Modifier.weight(0.1f))

        PasswordFieldWithLabel(
            labelText = stringResource(id = R.string.new_password),
            onValueChange = { passwordValue = it },
            passwordValue = passwordValue,
            labelColor = MaterialTheme.colorScheme.onSurface,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.weight(0.005f))

        PasswordFieldWithLabel(
            labelText = stringResource(id = R.string.repeat_password),
            onValueChange = { repeatPasswordValue = it },
            passwordValue = repeatPasswordValue,
            labelColor = MaterialTheme.colorScheme.onSurface,
            onImeAction = {}
        )

        Spacer(modifier = Modifier.weight(0.05f))

        DividerWithText(
            text = stringResource(id = R.string.or),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ClickableText(
            text = stringResource(id = R.string.using_pin_code),
            onClick = {
                onPinCodeClick()
                viewModel.updateAuthMethod(AuthMethod.PINCODE, context = context)
            }
        )

        Spacer(modifier = Modifier.weight(0.05f))

        CustomButton(
            text = stringResource(id = R.string.button_continue),
            enabled = isPasswordValid,
            onClick = {
                if (isPasswordValid) {
                    viewModel.updateAuthMethod(AuthMethod.PASSWORD, context = context)
                    Toast.makeText(context, R.string.password_saved, Toast.LENGTH_SHORT).show()
                    passwordStorageHelper.setData("MyPassword", passwordValue.toByteArray())
                    onNextAction()
                } else {
                    passwordAlertMessage = passwordErrorMessage
                    showPasswordAlert = true
                }
            }
        )

        Spacer(modifier = Modifier.weight(0.8f))
    }
}



fun checkPasswordsMatch(password1: String, password2: String): Boolean {
    return password1 == password2 && password1.isNotEmpty()
}

fun isPasswordValid(password: String): Boolean {
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
    val isLongEnough = password.length >= 8
    return hasUpperCase && hasDigit && hasSpecialChar && isLongEnough
}



