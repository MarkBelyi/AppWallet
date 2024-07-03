package com.example.walletapp.registrationScreens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.walletapp.elements.checkbox.CheckboxWithText
import com.example.walletapp.elements.checkbox.MnemonicPhraseGrid
import com.example.walletapp.elements.checkbox.MnemonicTitleWithIcon
import com.example.walletapp.elements.checkbox.ShareMnemonicPhrase
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import java.security.SecureRandom

@Composable
/**Экран СОЗДАНИЯ ключевой пары*/
fun CreateSeedPhraseScreen(viewModelReg: RegistrationViewModel, viewModelApp: appViewModel, navHostController: NavHostController, onNextClick: (Boolean) -> Unit){
    val context = LocalContext.current
    val ps = PasswordStorageHelper(context)
    val initialEntropy = SecureRandom.getSeed(16)
    //Потом из них генерим мнемоническую фразу
    val mnemonic = MnemonicUtils.generateMnemonic(initialEntropy)
    // Преобразуем строку мнемонической фразы в список слов:
    val mnemonicList: List<String> = mnemonic.split(" ")

    viewModelReg.setMnemonicList(mnemonicList)
    viewModelReg.setMnemonic(mnemonic)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) { Toast.makeText(context, R.string.mnem_is_sending, Toast.LENGTH_SHORT).show() } }

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

        ShowWarningDialog(
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
            text = if (showWords || isPhraseSaved && isPhraseSent) stringResource(id = R.string.button_continue) else stringResource(id = R.string.button_see_seed),
            onClick = {
                if (isPhraseSaved && isPhraseSent){
                    //имея эту фразу можно создать ключевую пару:
                    val restoreCredentials : Credentials = WalletUtils.loadBip39Credentials("We are such stuff as dreams are made on", mnemonic)
                    ps.setData("MyPrivateKey", restoreCredentials.ecKeyPair.privateKey.toByteArray())
                    ps.setData("MyPublicKey", restoreCredentials.ecKeyPair.publicKey.toByteArray())
                    viewModelApp.insertSigner(Signer(name = context.getString(R.string.default_name_of_signer), email = "", telephone = "", type = 1, address = GetMyAddr(context)))
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


// Когда пользователь будет сканировать экран то мы ему запретим,
// но пока оно не работает,
// потому что как это контролировать я не знаю
@Composable
fun ShowSecurityWarningDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(
                text = stringResource(id = R.string.cybersecurity_title),
                color = colorScheme.onBackground
            ) },
            text = {
                Text(text = stringResource(id = R.string.cybersecurity_body))
            },
            confirmButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Понятно")
                }
            }
        )
    }
}

@Composable
fun DividerWithText(text: String, modifier: Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.onSurface)
        Text(text = text, style = typography.bodyMedium, fontSize = 16.sp, modifier = Modifier.padding(horizontal = paddingColumn), color = colorScheme.onSurface)
        HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.onSurface)
    }
}


@Composable
fun ShowWarningDialog(showDialog: Boolean, onDismiss: () -> Unit, onAgree: () -> Unit) {
    var agreementText by remember { mutableStateOf("") }
    val isAgreementCorrect = remember(agreementText) {
                agreementText.equals("Согласен", ignoreCase = true) ||
                agreementText.equals("Согласна", ignoreCase = true) ||
                agreementText.equals("Yes", ignoreCase = true) ||
                agreementText.equals("Agree", ignoreCase = true)
    }

    if (showDialog) {
       AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(
                    text = stringResource(id = R.string.attention),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                ) },
            text = {
                Column {
                    Text(
                        text = stringResource(id = R.string.seed_phrase_alert_text),
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface
                        )
                    TextField(
                        value = agreementText,
                        onValueChange = { agreementText = it },
                        label = { Text(stringResource(id = R.string.your_answer)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isAgreementCorrect) {
                            onAgree()
                        } else {
                            onDismiss()
                        }
                    },
                    enabled = isAgreementCorrect
                ) {
                    Text(stringResource(id = R.string.agree))
                }
            },
            shape = newRoundedShape
        )
    }
}





