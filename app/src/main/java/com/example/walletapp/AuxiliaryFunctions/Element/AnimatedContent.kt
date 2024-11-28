package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R
import com.example.walletapp.ui.theme.topRoundedShape


@Composable
fun AnimatedContent(
    termsAccepted: Boolean,
    onCreateClick: () -> Unit,
    onAddClick: () -> Unit,
    onTermsAcceptedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.7f),
        color = MaterialTheme.colorScheme.surface,
        shape = topRoundedShape
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.2f))

            Text(
                text = stringResource(id = R.string.hello),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(0.15f))

            // Create Button
            CustomButton(
                textResource = R.string.create_button,
                enabled = termsAccepted,
                onClick = onCreateClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Button
            CustomButton(
                textResource = R.string.add_button,
                enabled = termsAccepted,
                onClick = onAddClick
            )

            Spacer(modifier = Modifier.weight(0.1f))

            CheckboxWithLabel(
                text = stringResource(id = R.string.terms_of_use),
                isChecked = termsAccepted,
                onCheckedChange = onTermsAcceptedChange,
                modifier = Modifier.fillMaxWidth(0.75f)
            )

            Spacer(modifier = Modifier.weight(0.4f))
        }
    }
}
