package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn

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
            placeholder = {
                Text(
                    text = stringResource(id = R.string.password_name),
                    color = colorScheme.scrim,
                    fontWeight = FontWeight.Normal
                )
            },
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
                    val image =
                        painterResource(id = if (isPasswordVisible) R.drawable.ic_baseline_visibility_off_24 else R.drawable.ic_baseline_visibility_24)
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
