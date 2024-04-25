package com.example.walletapp.registrationScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.elements.checkbox.PasswordFieldWithLabel
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.ui.theme.gradientColors
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape

enum class AuthMethod {
    PASSWORD,
    PINCODE
}

@Composable
fun CreatePasswordScreen(onNextAction: () -> Unit, onPinCodeClick: () -> Unit, viewModel: appViewModel){
    var showPasswordAlert by remember { mutableStateOf(false) }
    var passwordAlertMessage by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var repeatPasswordValue by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val passwordErrorMessage = stringResource(id = R.string.alert_password_message)

    //Работа с сохранением пароля
    val context = LocalContext.current
    val ps = PasswordStorageHelper(context)
    val isPasswordValid = checkPasswordsMatch(passwordValue, repeatPasswordValue)





    if (showPasswordAlert) {
        AlertDialog(
            onDismissRequest = { showPasswordAlert = false },
            title = { Text(
                text = stringResource(id = R.string.error_password),
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            ) },
            text = { Text(
                text = passwordAlertMessage,
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface
            ) },
            confirmButton = {
                TextButton(
                    onClick = { showPasswordAlert = false },
                ) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            },
            shape = newRoundedShape
        )
    }



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .padding(paddingColumn)
    ){

        Spacer(modifier = Modifier.weight(0.1f))

        PasswordFieldWithLabel(
            labelText = stringResource(id = R.string.new_password),
            onValueChange = { newValue ->
                passwordValue = newValue
            },
            passwordValue = passwordValue,
            labelColor = colorScheme.onSurface,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.weight(0.005f))

        PasswordFieldWithLabel(
            labelText = stringResource(id = R.string.repeat_password),
            onValueChange = { newValue ->
                repeatPasswordValue = newValue
            },
            passwordValue = repeatPasswordValue,
            labelColor = colorScheme.onSurface,
            onImeAction = {}
            /*onImeAction = {
                // Вызываем функцию проверки пароля, если пароль валиден
                viewModel.updateAuthMethod(AuthMethod.PASSWORD)
                if (isPasswordValid(passwordValue)) {
                    ps.setData("MyPassword", passwordValue.toByteArray())
                    onNextAction()
                } else {
                    passwordAlertMessage = passwordErrorMessage
                    showPasswordAlert = true
                }
            }*/
        )

        Spacer(modifier = Modifier.weight(0.05f))

        DividerWithText(
            text = stringResource(id = R.string.or),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ClickedText(
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
                viewModel.updateAuthMethod(AuthMethod.PASSWORD, context = context)
                if (isPasswordValid(passwordValue)) {
                    Toast.makeText(context, R.string.password_saved, Toast.LENGTH_SHORT).show()
                    ps.setData("MyPassword", passwordValue.toByteArray())
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

@Composable
fun CustomButton(
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp, max = 64.dp)
            .padding(top = 5.dp, bottom = 5.dp)
            .shadow(
                elevation = 16.dp,
                shape = newRoundedShape,
                clip = true
            ),
        enabled = enabled,
        shape = newRoundedShape,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            disabledContainerColor = colorScheme.primaryContainer,
            disabledContentColor = colorScheme.onPrimaryContainer
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ClickedText(
    text: String,
    onClick: () -> Unit
){

    TextButton(
        onClick = onClick,
        shape = newRoundedShape
    ) {
        Text(
            text = text,
            color = colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Light
        )
    }

}

