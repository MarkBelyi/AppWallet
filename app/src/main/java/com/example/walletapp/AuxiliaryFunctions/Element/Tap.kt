package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AppViewModel.RegistrationViewModel
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun Tap(
    wordsList: List<String>,
    isContinueEnabled: MutableState<Boolean>,
    viewModel: RegistrationViewModel,
    modifier: Modifier = Modifier
) {
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
                    val updatedWords =
                        displayedWords.toMutableList().apply { set(firstEmptyIndex, word) }
                    setDisplayedWords(updatedWords)
                    setAvailableWords(availableWords - word)
                }
            }
        }
    }

    @Composable
    fun WordBox(word: String?, onClick: () -> Unit) {
        val textColor =
            if (word in initiallyBottomWords) colorScheme.secondary else colorScheme.onSurface
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
                .padding(1.dp)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            word?.let {
                Text(
                    text = it,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }

    @Composable
    fun SmallWordBox(word: String?, onClick: () -> Unit) {
        val textColor =
            if (word in initiallyBottomWords) colorScheme.primary else colorScheme.onSurface
        Box(
            modifier = Modifier
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
                .padding(1.dp)
                .clickable(enabled = word in availableWords, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {

            word?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    color = textColor,
                    maxLines = 1,
                    modifier = Modifier.padding(12.dp)
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),

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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                wordRow.forEach { word ->
                    SmallWordBox(word = word, onClick = {
                        handleWordClick(displayedWords.indexOfFirst { it == null }, word)
                    })
                }
            }
        }
    }

    val mnemonicList = viewModel.getMnemonicList()
    isContinueEnabled.value = displayedWords.filterNotNull().size == mnemonicList.size &&
            displayedWords.filterNotNull() == mnemonicList
}
