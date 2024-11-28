package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AuxiliaryFunctions.ENUM.Actions
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun ActionGrid(
    actionItems: List<Triple<Int, Int, Actions>>,
    onItemClick: (Actions) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = 3
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier
            .background(color = colorScheme.surface, shape = newRoundedShape),
    ) {
        items(actionItems) { actionItem ->
            ActionCell(
                text = stringResource(actionItem.first),
                imageVector = actionItem.second,
                onClick = { onItemClick(actionItem.third) },
                modifier = Modifier
            )
        }
    }
}

@Composable
fun ActionCell(
    text: String,
    imageVector: Int,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1.4f),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        shape = newRoundedShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        interactionSource = interactionSource
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = imageVector),
                    contentDescription = text,
                    modifier = modifier
                        .scale(1.4f),
                    tint = colorScheme.primary
                )
            }
            Spacer(modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    maxLines = 2,
                    style = TextStyle(
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}
