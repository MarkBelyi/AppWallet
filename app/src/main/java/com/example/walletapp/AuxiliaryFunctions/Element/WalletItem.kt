package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Wallets
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun WalletItem(wallet: Wallets, onWalletClick: (Wallets) -> Unit) {
    val context = LocalContext.current
    val isAddressEmpty = wallet.addr.isEmpty()
    val network = wallet.network
    val iconResource = when (network) {
        1000, 1010 -> R.drawable.btc
        3000, 3040 -> R.drawable.eth
        5000, 5010 -> R.drawable.tron
        else -> R.drawable.wait
    }
    val isHidden = wallet.myFlags.startsWith("1")

    val tokensList = mutableListOf<String>()

    if (wallet.tokenShortNames.isNotBlank()) {

        wallet.tokenShortNames.split(";").filter { it.isNotBlank() }.forEach { token ->
            val parts = token.split(" ")
            val tokenName = parts.getOrNull(1) ?: ""
            tokensList.add(tokenName)
        }

    }

    Card(
        border = BorderStroke(
            width = 0.5.dp,
            color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.primary
        ),
        onClick = {
            if (!isAddressEmpty) {
                onWalletClick(wallet)
            }
        },
        shape = newRoundedShape,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.padding(start = 0.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(28.dp)
                    .background(
                        color = when {
                            isHidden -> colorScheme.onSurface.copy(alpha = 0.5f)
                            network in listOf(5010, 1010, 3040) -> colorScheme.primary
                            else -> Color.Transparent
                        }, shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (network in listOf(5010, 1010, 3040)) {
                    Text(
                        text = "TEST",
                        maxLines = 1,
                        color = colorScheme.onPrimary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .align(Alignment.Center)
                            .rotate(90f)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                    .size(36.dp),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    painter = painterResource(iconResource),
                    contentDescription = "Blockchain network logo",
                    tint = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.primary,
                    modifier = Modifier.scale(1.2f)
                )

            }

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = wallet.info.uppercase(),
                    color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isAddressEmpty) {
                    Spacer(Modifier.height(4.dp))

                    Text(
                        context.getString(R.string.pending_wallet),
                        fontWeight = FontWeight.Light,
                        color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                } else {
                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val displayTokens =
                            if (tokensList.size > 2) tokensList.take(2) else tokensList
                        displayTokens.forEach { token ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .border(
                                        width = 0.5.dp,
                                        color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.primary,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = token,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.onSurface
                                )
                            }
                        }
                        if (tokensList.size > 2) {
                            Text(
                                text = "+${tokensList.size - 2}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isHidden) colorScheme.onSurface.copy(alpha = 0.5f) else colorScheme.onSurface,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
            ) {
                if (!isHidden) {
                    Text(
                        text = wallet.tokenShortNames.split(";")
                            .find { it.contains("TRX") || it.contains("BTC") || it.contains("ETH") }
                            ?: "",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.hide),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}