package com.example.walletapp.Screens.registrationScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.walletapp.AppViewModel.RegistrationViewModel
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.Element.Tap
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PasswordStorageHelper
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.R
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils

@Composable
        /** Экран проверки сгенерированной мнемонической фразы - юзеру нужно верно расставить слова*/
fun TapSeedPhraseScreen(
    navHostController: NavHostController,
    viewModelReg: RegistrationViewModel,
    viewModelApp: appViewModel
) {
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
                val restoreCredentials: Credentials = WalletUtils.loadBip39Credentials(
                    "We are such stuff as dreams are made on",
                    mnemonic
                )
                ps.setData("MyPrivateKey", restoreCredentials.ecKeyPair.privateKey.toByteArray())
                ps.setData("MyPublicKey", restoreCredentials.ecKeyPair.publicKey.toByteArray())
                navHostController.navigate("App")
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
            },
            enabled = isContinueEnabled.value
        )
    }
}




