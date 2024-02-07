package com.example.walletapp.registrationScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.walletapp.R
import com.example.walletapp.elements.checkbox.CheckboxWithLabel
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun NewUserScreenColumn(onCreateClick: () -> Unit, onAddClick: () -> Unit){
    var termsAccepted by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .padding(paddingColumn)
    ){
        Spacer(modifier = Modifier.weight(0.35f))
        //Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(fraction = 0.6f)
                .heightIn(min = 75.dp, max = 202.dp)
                .widthIn(min = 108.dp, max = 232.dp)
                .aspectRatio(1f)
        )
        Spacer(modifier = Modifier.weight(0.35f))

        // CreateButton
        CustomButton(
            textResource = R.string.create_button,
            enabled = termsAccepted,
            onClick = onCreateClick
        )

        Spacer(modifier = Modifier.height(15.dp))

        // AddButton
        CustomButton(
            textResource = R.string.add_button,
            enabled = termsAccepted,
            onClick = onAddClick
        )

        Spacer(modifier = Modifier.weight(0.1f))

        // Checkbox with label
        CheckboxWithLabel(
            text = stringResource(id = R.string.terms_of_use),
            isChecked = termsAccepted,
            onCheckedChange = { isChecked -> termsAccepted = isChecked },
            modifier = Modifier
                .fillMaxWidth(0.75f)
        )

        Spacer(modifier = Modifier.weight(0.2f))
    }
}

@Composable
fun CustomButton(textResource: Int, onClick: () -> Unit, enabled: Boolean) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .heightIn(min = 48.dp, max = 64.dp),
        enabled = enabled,
        shape = roundedShape,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            disabledContainerColor = colorScheme.primaryContainer,
            disabledContentColor = colorScheme.onPrimaryContainer
        )
    ) {
        Text(text = stringResource(textResource))
    }
}
