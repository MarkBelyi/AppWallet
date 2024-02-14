package com.example.walletapp.registrationScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.walletapp.R
import com.example.walletapp.settings.mnemonicList
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun TapSeedPhraseScreen(navHostController: NavHostController) {
    val isContinueEnabled = remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .padding(paddingColumn)
    ) {
        val (title, tapArea, continueButton) = createRefs()

        Text(
            text = stringResource(id = R.string.tap_seed_phrase),
            style = TextStyle(
                fontSize = typography.titleLarge.fontSize,
                color = colorScheme.onBackground
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Tap(
            mnemonicList,
            isContinueEnabled,
            modifier = Modifier.constrainAs(tapArea) {
                top.linkTo(title.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        CustomButton(
            text = stringResource(id = R.string.button_continue),
            onClick = {navHostController.navigate("App")},
            enabled = isContinueEnabled.value,
            modifier = Modifier.constrainAs(continueButton) {
                top.linkTo(tapArea.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 16.dp)
            }
        )
    }
}

@Composable
fun Tap(wordsList: List<String>,  isContinueEnabled: MutableState<Boolean>, modifier: Modifier = Modifier) {
    val (displayedWords, setDisplayedWords) = remember {
        val chosenIndices = (wordsList.indices).shuffled().take(8).toSet()
        val displayedWithEmptySlots = MutableList<String?>(12) { null }
        for (i in 0 until 12) {
            if (i in chosenIndices) {
                displayedWithEmptySlots[i] = wordsList[i]
            }
        }
        mutableStateOf(displayedWithEmptySlots)
    }

    val initiallyBottomWords = remember {
        (wordsList.toSet() - displayedWords.filterNotNull().toSet()).shuffled()
    }

    val (availableWords, setAvailableWords) = remember {
        mutableStateOf(initiallyBottomWords)
    }

    fun handleWordClick(index: Int?, word: String?) {
        index?.let {
            val currentWord = displayedWords[it]
            if (currentWord != null && currentWord in initiallyBottomWords) {
                val updatedWords = displayedWords.toMutableList().apply { set(it, null) }
                setDisplayedWords(updatedWords)
                setAvailableWords(availableWords + currentWord)
            } else if (word != null && word in initiallyBottomWords) {
                val firstEmptyIndex = displayedWords.indexOfFirst { it == null }
                if (firstEmptyIndex != -1) {
                    val updatedWords = displayedWords.toMutableList().apply { set(firstEmptyIndex, word) }
                    setDisplayedWords(updatedWords)
                    setAvailableWords(availableWords - word)
                }
            }
        }
    }

    @Composable
    fun WordBox(word: String?, onClick: () -> Unit) {
        val textColor = if (word in initiallyBottomWords) colorScheme.primary else colorScheme.onBackground
        Box(
            modifier = Modifier
                .border(1.5.dp, colorScheme.onBackground, roundedShape)
                .clip(roundedShape)
                .background(colorScheme.surface)
                .aspectRatio(2f)
                .padding(8.dp)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            word?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = textColor
                )
            }
        }
    }

    @Composable
    fun SmallWordBox(word: String?, onClick: () -> Unit) {
        val textColor = if (word in initiallyBottomWords) colorScheme.primary else colorScheme.onBackground
        Box(
            modifier = Modifier
                .border(0.75.dp, colorScheme.onBackground, roundedShape)
                .clip(roundedShape)
                .background(colorScheme.surface)
                .padding(6.dp)
                .clickable(enabled = word in availableWords, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            word?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = textColor
                )
            }
        }
    }

    val paddedAvailableWords = availableWords.toMutableList()
    while (paddedAvailableWords.size % 4 != 0) {
        paddedAvailableWords.add(null.toString())
    }

    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(12) { index ->
                val word = displayedWords[index]
                WordBox(word = word) {
                    handleWordClick(index, null)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        availableWords.chunked(4).forEach { wordRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                wordRow.forEach { word ->
                    SmallWordBox(word = word, onClick = {
                        handleWordClick(displayedWords.indexOfFirst { it == null }, word)
                    })
                }
            }
        }
    }

    isContinueEnabled.value = displayedWords.filterNotNull().size == mnemonicList.size &&
            displayedWords.filterNotNull() == mnemonicList
}
