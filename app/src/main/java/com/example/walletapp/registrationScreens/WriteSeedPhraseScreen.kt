package com.example.walletapp.registrationScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils

@Composable
fun WriteSeedPhraseScreen(navHostController: NavHostController, viewModel: appViewModel) {
    val isContinueEnabled = remember { mutableStateOf(false) }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .padding(paddingColumn)
    ) {
        val (textHeader, writeComponent, instructionText, continueButton) = createRefs()

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
            modifier = Modifier
                .constrainAs(textHeader) {
                    top.linkTo(parent.top, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        Write(
            isContinueEnabled = isContinueEnabled,
            modifier = Modifier.constrainAs(writeComponent) {
                top.linkTo(textHeader.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            viewModel = viewModel
        )

        Text(
            text = stringResource(id = R.string.seed_phrase_paste),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(instructionText) {
                    top.linkTo(writeComponent.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        CustomButton(
            text = stringResource(id = R.string.button_continue),
            onClick = { navHostController.navigate("App") },
            enabled = isContinueEnabled.value,
        )
    }
}

@Composable
fun Write(isContinueEnabled: MutableState<Boolean>, modifier: Modifier = Modifier, viewModel: appViewModel) {
    val userPhrases = remember { mutableStateListOf(*Array(12) { "" }) }
    val context = LocalContext.current
val con=LocalContext.current
    @Composable
    fun WordInputBox(index: Int) {
        TextField(
            value = userPhrases[index],
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
            ),
            maxLines = 1,
            shape = newRoundedShape,
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
                        viewModel.insertSigner(Signer(name = context.getString(R.string.default_name_of_signer), email = "", telephone = "", type = 1, address = GetMyAddr(context)))
                        // всё хорошо, активируем кнопку шо мол можно идти дальше
                        isContinueEnabled.value = true
                    }

            },
            modifier = modifier
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
                    color = colorScheme.background
                )
                .padding(1.dp)
                .background(colorScheme.background),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface,
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
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(12) { index ->
                WordInputBox(index = index)
            }
        }
    }
}

