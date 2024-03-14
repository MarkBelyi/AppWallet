package com.example.walletapp.registrationScreens

import android.widget.Toast
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import com.example.walletapp.R
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils

@Composable
fun WriteSeedPhraseScreen(navHostController: NavHostController) {
    val isContinueEnabled = remember { mutableStateOf(false) }
    //val ps = PasswordStorageHelper(LocalContext.current)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .padding(paddingColumn)
    ) {
        val (textHeader, writeComponent, instructionText, continueButton) = createRefs()

        Text(
            text = stringResource(id = R.string.tap_seed_phrase),
            style = TextStyle(
                fontSize = typography.titleLarge.fontSize,
                color = colorScheme.onBackground
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(textHeader) {
                    top.linkTo(parent.top, margin = paddingColumn)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        Write(
            isContinueEnabled,
            modifier = Modifier.constrainAs(writeComponent) {
                top.linkTo(textHeader.bottom, margin = paddingColumn)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = stringResource(id = R.string.seed_phrase_paste),
            style = TextStyle(
                fontSize = 16.sp,
                color = colorScheme.onBackground
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .constrainAs(instructionText) {
                    top.linkTo(writeComponent.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        CustomButton(
            text = stringResource(id = R.string.button_continue),
            onClick = { navHostController.navigate("App") },
            enabled = /*isContinueEnabled.value*/ false,
            modifier = Modifier.constrainAs(continueButton) {
                top.linkTo(instructionText.bottom, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Composable
fun Write(isContinueEnabled: MutableState<Boolean>, modifier: Modifier = Modifier) {
    val userPhrases = remember { mutableStateListOf(*Array(12) { "" }) }
val con=LocalContext.current
    @Composable
    fun WordInputBox(index: Int) {
        TextField(
            value = userPhrases[index],
            onValueChange = { newValue ->
                if (newValue.contains(" ")) {
                    val words = newValue.split(" ").filterNot { it.isBlank() }
                    words.forEachIndexed { wordIndex, word ->
                        if (wordIndex < userPhrases.size) {
                            userPhrases[wordIndex] = word
                        }
                    }
                } else userPhrases[index] = newValue
                    // Итак, в итоге мы здесь имеем 12 слов.
                    if (userPhrases.filter { !it.isBlank() }.size==12) {
                        // Вот наша мнемоФраза одной строкой
                       val mnemonic = userPhrases.joinToString(" ")
                        // проверка на валидность фразы, ато мош юзер навтыкал слов ваще не отсюда
                        if (!MnemonicUtils.validateMnemonic(mnemonic))
                        {// проверка на валидность не прошла, всё плохо и ключи из этих слов сгенерить не получится
                            Toast.makeText(con, R.string.toast_write_seed_phrase, Toast.LENGTH_SHORT).show(); isContinueEnabled.value = false; return@TextField }
                        // ну раз мы добрались досюда, значит всё круто. Создаём ключи по фразе:
                       val restoreCredentials: Credentials = WalletUtils.loadBip39Credentials("We are such stuff as dreams are made on", mnemonic)
                        // Сохраняем эти ключи в наше шифрохранилище
                       val ps = PasswordStorageHelper(con)
                       ps.setData("MyPrivateKey", restoreCredentials.ecKeyPair.privateKey.toByteArray())
                       ps.setData("MyPublicKey", restoreCredentials.ecKeyPair.publicKey.toByteArray())
                        // всё хорошо, активируем кнопку шо мол можно идти дальше
                        isContinueEnabled.value = true
                    }

            },
            modifier = modifier
                .aspectRatio(2f)
                .border(1.5.dp, colorScheme.onBackground, roundedShape)
                .clip(roundedShape)
                .background(colorScheme.background),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                cursorColor = colorScheme.onSurface,
                disabledLabelColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(12) { index ->
                WordInputBox(index = index)
            }
        }
    }
}

