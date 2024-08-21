package com.example.walletapp.AuxiliaryFunctions.Element

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PasswordStorageHelper
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.R
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.ui.theme.newRoundedShape
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils

@Composable
fun Write(
    isContinueEnabled: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    viewModel: appViewModel
) {
    val userPhrases = remember { mutableStateListOf(*Array(12) { "" }) }
    val con = LocalContext.current

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
                // Установить значение слова, даже если оно пустое
                val trimmedValue = newValue.trim()
                if (trimmedValue.contains(" ")) {
                    val words = trimmedValue.split(" ").filterNot { it.isBlank() }
                    words.forEachIndexed { wordIndex, word ->
                        val targetIndex = index + wordIndex
                        if (targetIndex < userPhrases.size) {
                            userPhrases[targetIndex] = word
                        }
                    }
                } else {
                    userPhrases[index] = trimmedValue
                }

                // Проверка на наличие всех 12 слов и их валидность
                if (userPhrases.filter { it.isNotBlank() }.size == 12) {
                    val mnemonic = userPhrases.joinToString(" ")
                    if (!MnemonicUtils.validateMnemonic(mnemonic)) {
                        Toast.makeText(con, R.string.toast_write_seed_phrase, Toast.LENGTH_SHORT)
                            .show()
                        isContinueEnabled.value = false
                        return@TextField
                    }

                    val restoreCredentials: Credentials = WalletUtils.loadBip39Credentials(
                        "We are such stuff as dreams are made on",
                        mnemonic
                    )
                    val ps = PasswordStorageHelper(con)
                    ps.setData(
                        "MyPrivateKey",
                        restoreCredentials.ecKeyPair.privateKey.toByteArray()
                    )
                    ps.setData("MyPublicKey", restoreCredentials.ecKeyPair.publicKey.toByteArray())
                    viewModel.insertSigner(
                        Signer(
                            name = con.getString(R.string.default_name_of_signer),
                            email = "",
                            telephone = "",
                            type = 1,
                            address = GetMyAddr(con),
                            isFavorite = false
                        )
                    )
                    isContinueEnabled.value = true
                } else {
                    isContinueEnabled.value = false
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
                .background(color = colorScheme.surface)
                .padding(1.dp),
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
