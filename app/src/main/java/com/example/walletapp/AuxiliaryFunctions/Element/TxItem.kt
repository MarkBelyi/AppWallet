package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AuxiliaryFunctions.Functions.formatTimestamp
import com.example.walletapp.DataBase.Entities.AllTX
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun TXItem(tx: AllTX) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth(),
        shape = newRoundedShape,
        border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.9f)
            ) {
                Text(
                    text = tx.info,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                val hexPattern = Regex("^[0-9a-fA-F]{64}$")

                if (tx.tx.isEmpty() || tx.tx == "null") {
                    Text(
                        text = stringResource(id = R.string.tx_not_in_block),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.onSurface,
                        maxLines = 1
                    )
                } else if (!hexPattern.matches(tx.tx)) {
                    Text(
                        text = stringResource(id = R.string.tx_error) + tx.tx,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.primary,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.tx_success),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = tx.tx,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        color = colorScheme.onSurface,
                        maxLines = 1
                    )
                }
                Text(
                    text = stringResource(id = R.string.to_address) + tx.to_addr,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = stringResource(id = R.string.amount_of_money) + tx.tx_value + " " + tx.token,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = stringResource(id = R.string.tx_time) + formatTimestamp(tx.init_ts),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }
    }
}