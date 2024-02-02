package com.example.walletapp.elements.checkbox

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun MnemonicPhraseGrid(wordsList: List<String>, showWords: Boolean, onCloudyClick: () -> Unit, modifier: Modifier = Modifier) {
    val itemSpacing = 8.dp

    val displayedWords = if (showWords) wordsList else List(12) { (it + 1).toString() }
    if (showWords) {
        MnemonicCells(
            displayedWords,
            itemSpacing,
            modifier = modifier
        )
    } else {
        /*Cloudy(
            radius = 25,
            modifier = modifier.clickable(onClick = onCloudyClick)
        ) {
            MnemonicCells(
                displayedWords,
                itemSpacing,
                modifier = modifier
            )
        }*/

        MnemonicCells(
            displayedWords,
            itemSpacing,
            modifier = modifier
                .clickable(onClick = onCloudyClick)
        )


    }
}

@Composable
fun MnemonicCells(
    displayedWords: List<String>,
    itemSpacing: Dp,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        items(displayedWords) { word ->
            Box(
                modifier = Modifier
                    .border(1.5.dp, colorScheme.onBackground, roundedShape)
                    .clip(roundedShape)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = colorScheme.onBackground,
                    maxLines = 1
                )
            }
        }
    }
}