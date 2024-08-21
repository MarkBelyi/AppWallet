package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun AddSignerCard(onClick: () -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Card(
            modifier = Modifier
                .height(48.dp),
            shape = newRoundedShape,
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
            onClick = onClick
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add signer",
                    modifier = Modifier.size(24.dp),
                    tint = colorScheme.primary
                )
            }
        }
    }

}