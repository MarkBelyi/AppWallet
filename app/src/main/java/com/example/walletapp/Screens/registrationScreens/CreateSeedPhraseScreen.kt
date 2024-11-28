package com.example.walletapp.Screens.registrationScreens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.walletapp.AppViewModel.RegistrationViewModel
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.Element.CheckboxWithText
import com.example.walletapp.AuxiliaryFunctions.Element.DividerWithText
import com.example.walletapp.AuxiliaryFunctions.Element.MnemonicPhraseGrid
import com.example.walletapp.AuxiliaryFunctions.Element.MnemonicTitleWithIcon
import com.example.walletapp.AuxiliaryFunctions.Element.ShareMnemonicPhrase
import com.example.walletapp.AuxiliaryFunctions.Element.ShowWarningMnemPhraseDialog
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PasswordStorageHelper
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.R
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import java.security.SecureRandom

@Composable
        /**Экран СОЗДАНИЯ ключевой пары*/
fun CreateSeedPhraseScreen(
    viewModelReg: RegistrationViewModel,
    viewModelApp: appViewModel,
    navHostController: NavHostController,
    onNextClick: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val ps = PasswordStorageHelper(context)

    // Используем remember для хранения начальной энтропии и мнемонической фразы
    val initialEntropy = remember { SecureRandom.getSeed(16) }
    val mnemonic = remember { MnemonicUtils.generateMnemonic(initialEntropy) }
    val mnemonicList: List<String> = mnemonic.split(" ")

    viewModelReg.setMnemonicList(mnemonicList)
    viewModelReg.setMnemonic(mnemonic)

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, R.string.mnem_is_sending, Toast.LENGTH_SHORT).show()
            }
        }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .padding(paddingColumn)
    ) {
        val isPhraseSent by viewModelReg::isPhraseSent
        var isPhraseSaved by remember { mutableStateOf(false) }

        var showWords by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }

        val (titleRef, phraseGridRef, dividerRef, sharePhraseRef, checkboxRef, buttonRef) = createRefs()

        @Composable
        fun CustomButton(
            text: String,
            enabled: Boolean,
            onClick: () -> Unit
        ) {
            ElevatedButton(
                onClick = onClick,
                modifier = Modifier
                    .constrainAs(buttonRef) {
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


        // Заголовок
        MnemonicTitleWithIcon(
            modifier = Modifier.constrainAs(titleRef) { // Применяем ограничения
                top.linkTo(parent.top, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Сетка мнемонической фразы
        MnemonicPhraseGrid(
            wordsList = mnemonicList,
            showWords = showWords,
            onCloudyClick = { showDialog = true },
            modifier = Modifier.constrainAs(phraseGridRef) {
                top.linkTo(titleRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        // Делитель
        DividerWithText(
            text = stringResource(id = R.string.or),
            modifier = Modifier.constrainAs(dividerRef) {
                top.linkTo(phraseGridRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        ShareMnemonicPhrase(
            randomSeedPhrases = mnemonicList,
            onShared = { viewModelReg.isPhraseSent = true },
            launcher = launcher,
            modifier = Modifier.constrainAs(sharePhraseRef) {
                top.linkTo(dividerRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        ShowWarningMnemPhraseDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onAgree = {
                showDialog = false
                showWords = true
            }
        )

        if (isPhraseSent) {
            CheckboxWithText(
                text = stringResource(id = R.string.i_save_seed),
                onCheckedChange = { isPhraseSaved = it },
                modifier = Modifier.constrainAs(checkboxRef) {
                    top.linkTo(sharePhraseRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }

        // Кнопка
        CustomButton(
            text = if (showWords || isPhraseSaved && isPhraseSent) stringResource(id = R.string.button_continue) else stringResource(
                id = R.string.button_see_seed
            ),
            onClick = {
                if (isPhraseSaved && isPhraseSent) {
                    //имея эту фразу можно создать ключевую пару:
                    val restoreCredentials: Credentials = WalletUtils.loadBip39Credentials(
                        "We are such stuff as dreams are made on",
                        mnemonic
                    )
                    ps.setData(
                        "MyPrivateKey",
                        restoreCredentials.ecKeyPair.privateKey.toByteArray()
                    )
                    ps.setData("MyPublicKey", restoreCredentials.ecKeyPair.publicKey.toByteArray())
                    viewModelApp.insertSigner(
                        Signer(
                            name = context.getString(R.string.default_name_of_signer),
                            email = "",
                            telephone = "",
                            type = 1,
                            address = GetMyAddr(context),
                            isFavorite = false
                        )
                    )
                    navHostController.navigate("App")
                } else if (showWords) {
                    onNextClick(true)
                } else {
                    showDialog = true
                }
            },
            enabled = true,
        )

    }
}







