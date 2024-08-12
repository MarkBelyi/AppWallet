package com.example.walletapp.Element

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.newRoundedShape

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
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        items(displayedWords) { word ->
            Box(
                modifier = Modifier
                    .aspectRatio(2f)
                    .border(
                        width = 0.5.dp,
                        color = colorScheme.onSurfaceVariant,
                        shape = newRoundedShape
                    )
                    .shadow(
                        elevation = 4.dp,
                        shape = newRoundedShape,
                        clip = true
                    )
                    .background(
                        color = colorScheme.surface
                    )
                    .padding(1.dp),

                contentAlignment = Alignment.Center

            ) {

                Text(
                    text = word,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )

            }
        }
    }
}