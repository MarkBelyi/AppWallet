package com.example.walletapp.elements.checkbox

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.walletapp.R
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun PasswordFieldWithLabel(
    labelText: String,
    onValueChange: (String) -> Unit,
    passwordValue: String,
    labelColor: Color,

    onImeAction: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp)
    ) {
        Text(
            text = labelText,
            style = TextStyle(color = labelColor)
        )

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = passwordValue,
            onValueChange = {
                if (!it.contains("\n")) {
                    onValueChange(it)
                }
            },
            label = { Text(stringResource(id = R.string.password_name)) },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onImeAction() // Вызываем переданную функцию, когда пользователь нажимает "Done"
                }
            ),
            textStyle = TextStyle(color = colorScheme.onBackground),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp, max = 72.dp),
            shape = roundedShape,
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    isPasswordVisible = !isPasswordVisible
                }) {
                    // painterResource для загрузки векторного изображения из ресурсов
                    val image = painterResource(id = if (isPasswordVisible) R.drawable.ic_baseline_visibility_off_24 else R.drawable.ic_baseline_visibility_24)
                    Icon(
                        painter = image,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = colorScheme.onBackground
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.background,
                focusedLabelColor = colorScheme.primary,
                unfocusedContainerColor = colorScheme.background,
                unfocusedLabelColor = colorScheme.onBackground,
                cursorColor = colorScheme.primary
            ),
        )
    }
}
