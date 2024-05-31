package com.example.walletapp.registrationScreens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.R
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.appViewModel.RegistrationViewModel
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils


@Composable
/** Экран проверки сгенерированной мнемонической фразы - юзеру нужно верно расставить слова*/
fun TapSeedPhraseScreen(navHostController: NavHostController, viewModelReg: RegistrationViewModel, viewModelApp: appViewModel) {
    val context = LocalContext.current
    val isContinueEnabled = remember { mutableStateOf(false) }
    val mnemonicList = viewModelReg.getMnemonicList()
    val mnemonic = viewModelReg.getMnemonic()
    val ps = PasswordStorageHelper(LocalContext.current)



    ConstraintLayout(

        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .padding(paddingColumn)

    ) {
        val (title, tapArea, continueButton) = createRefs()

        @Composable
        fun CustomButton(
            text: String,
            enabled: Boolean,
            onClick: () -> Unit
        ) {
            ElevatedButton(
                onClick = onClick,
                modifier = Modifier
                    .constrainAs(continueButton) {
                        bottom.linkTo(parent.bottom, margin = 32.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .heightIn(min = 56.dp, max = 64.dp)
                    .padding(top = 5.dp, bottom = 5.dp),
                enabled = enabled,
                shape = newRoundedShape,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    disabledContainerColor = colorScheme.primaryContainer,
                    disabledContentColor = colorScheme.onPrimaryContainer
                )
            ) {
                Text(text = text)
            }
        }

        Text(
            text = stringResource(id = R.string.tap_seed_phrase),
            style = TextStyle(
                fontSize = typography.titleLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Tap(
            wordsList = mnemonicList,
            isContinueEnabled = isContinueEnabled,
            viewModel = viewModelReg,
            modifier = Modifier.constrainAs(tapArea) {
                top.linkTo(title.bottom, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(continueButton.top, margin = 32.dp)
                width = Dimension.fillToConstraints
            }

        )

        CustomButton(
            text = stringResource(id = R.string.button_continue),
            onClick = {
                val restoreCredentials: Credentials = WalletUtils.loadBip39Credentials("We are such stuff as dreams are made on", mnemonic)
                ps.setData("MyPrivateKey", restoreCredentials.ecKeyPair.privateKey.toByteArray())
                ps.setData("MyPublicKey", restoreCredentials.ecKeyPair.publicKey.toByteArray())
                navHostController.navigate("App")
                viewModelApp.insertSigner(
                    Signer(
                        name = context.getString(R.string.default_name_of_signer),
                        email = "",
                        telephone = "",
                        type = 1,
                        address = GetMyAddr(context)
                    )
                )
            },
            enabled = isContinueEnabled.value
        )
    }
}

@Composable
fun Tap(wordsList: List<String>, isContinueEnabled: MutableState<Boolean>, viewModel: RegistrationViewModel, modifier: Modifier = Modifier) {
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
        val textColor = if (word in initiallyBottomWords) colorScheme.secondary else colorScheme.onSurface
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
        val textColor = if (word in initiallyBottomWords) colorScheme.secondary else colorScheme.onSurface
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



