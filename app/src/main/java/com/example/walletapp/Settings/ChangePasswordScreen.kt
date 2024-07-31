package com.example.walletapp.Settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.Element.CustomButton
import com.example.walletapp.Element.PasswordAlertDialog
import com.example.walletapp.Element.PasswordFieldWithLabel
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.helper.DESCrypt
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.helper.isBigInteger
import com.example.walletapp.registrationScreens.AuthMethod
import com.example.walletapp.registrationScreens.PinLockScreen
import com.example.walletapp.registrationScreens.checkPasswordsMatch
import com.example.walletapp.registrationScreens.isPasswordValid
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import java.math.BigInteger
import java.nio.charset.StandardCharsets


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChangePasswordScreen(onSuccessClick: () -> Unit, viewModel: appViewModel){
    val context = LocalContext.current
    val ps = PasswordStorageHelper(context)
    val state = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = state,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) {page ->
        when(page){
            0 -> VerifyMnemScreen(ps = ps, onClick = {
                coroutineScope.launch {
                    state.animateScrollToPage(page = 1)
                }
            },
                context = context)
            1 -> ChooseAuthMethod(onPINclick = {
                coroutineScope.launch {
                    state.animateScrollToPage(page = 2)
                }
            }, onPASSclick = {
                coroutineScope.launch {
                    state.animateScrollToPage(page = 3)
                }
            })
            2 -> PINScreen(onClick = {
                    onSuccessClick()
                },
                viewModel = viewModel
            )
            3 -> PASSWORDScreen(onClick = {
                onSuccessClick()
            },
                viewModel = viewModel
            )
        }

    }
}

@Composable
fun VerifyMnemScreen(ps: PasswordStorageHelper, onClick: () -> Unit, context: Context){
    val isContinueEnabled = remember { mutableStateOf(false) }
    val isKeyMatching = remember { mutableStateOf(false) } // Состояние для проверки совпадения ключей

    val bookmarkImportFilePicker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)!!
                val bytes = inputStream.readBytes()
                inputStream.close()

                val decrypt = DESCrypt.decrypt(bytes)
                val text = String(decrypt, StandardCharsets.UTF_8)
                if (!isBigInteger(text)) {
                    Toast.makeText(context, "Неправильный ключ", Toast.LENGTH_SHORT).show()
                    return@let
                }
                val k: ECKeyPair = ECKeyPair.create(BigInteger(text))

                // Проверка схожести ключей
                val currentPrivateKey = ps.getData("MyPrivateKey")
                if (k.privateKey.toByteArray().contentEquals(currentPrivateKey)) {
                    Toast.makeText(context, "Импортируемый ключ совпадает с вашим ключем", Toast.LENGTH_SHORT).show()
                    isKeyMatching.value = true
                    onClick()
                } else {
                    Toast.makeText(context, "Импортируемый ключ не совпадает с вашим ключем", Toast.LENGTH_SHORT).show()
                    isKeyMatching.value = false
                }
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        Spacer(modifier = Modifier.weight(0.3f))

        Text(
            text = "Введите мнемоническую фразу",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(0.1f))

        WriteForCheck(isContinueEnabled = isContinueEnabled, ps = ps)

        Spacer(modifier = Modifier.weight(0.1f))

        Text(
            text = stringResource(id = R.string.seed_phrase_paste),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.weight(0.8f))

        CustomButton(
            text = stringResource(id = R.string.button_continue),
            onClick = onClick,
            enabled = isContinueEnabled.value,
        )

        Spacer(modifier = Modifier.weight(0.05f))

        TextButton(
            onClick = {
                bookmarkImportFilePicker.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                })
            },
            modifier = Modifier.fillMaxWidth(),
            shape = newRoundedShape,
            enabled = true,
        ) {
            Text("Импорт ключей из файла")
        }

        Spacer(modifier = Modifier.weight(0.45f))


    }
}

@Composable
fun WriteForCheck(isContinueEnabled: MutableState<Boolean>, ps: PasswordStorageHelper) {

    val private = ps.getData("MyPrivateKey")
    val public = ps.getData("MyPublicKey")

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

                // Итак, в итоге мы здесь имеем 12 слов.
                if (userPhrases.filter { it.isNotBlank() }.size==12) {
                    // Вот наша мнемоФраза одной строкой
                    val mnemonic = userPhrases.joinToString(" ")
                    // проверка на валидность фразы, ато мош юзер навтыкал слов ваще не отсюда
                    if (!MnemonicUtils.validateMnemonic(mnemonic)) {// проверка на валидность не прошла, всё плохо и ключи из этих слов сгенерить не получится
                        Toast.makeText(con, R.string.toast_write_seed_phrase, Toast.LENGTH_SHORT).show(); isContinueEnabled.value = false; return@TextField
                    }
                    // ну раз мы добрались досюда, значит всё круто. Создаём ключи по фразе:
                    val restoreCredentials: Credentials = WalletUtils.loadBip39Credentials("We are such stuff as dreams are made on", mnemonic)

                    // Сохраняем эти новые ключи в наше шифрохранилище
                    val new_private = restoreCredentials.ecKeyPair.privateKey.toByteArray()
                    val new_public = restoreCredentials.ecKeyPair.publicKey.toByteArray()

                    if (private.contentEquals(new_private) && public.contentEquals(new_public)) {
                        // всё хорошо, активируем кнопку шо мол можно идти дальше
                        isContinueEnabled.value = true
                    }
                }

            },
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

    Column(modifier = Modifier) {
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

@Composable
fun ChooseAuthMethod(onPINclick: () -> Unit, onPASSclick: () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){

        Spacer(modifier = Modifier.weight(0.75f))

        Text(
            text = "Выберите тип авторизации",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            onClick = { onPINclick() },
            modifier = Modifier.fillMaxWidth(),
            shape = newRoundedShape,
            border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)
            ){
                Icon(imageVector = Icons.Rounded.Lock, contentDescription = "password_icon", tint = colorScheme.primary, modifier = Modifier.scale(1.5f))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "PIN-код", color = colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            onClick = { onPASSclick() },
            modifier = Modifier.fillMaxWidth(),
            shape = newRoundedShape,
            border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(16.dp)
            ){
                Icon(painter = painterResource(id = R.drawable.pass), contentDescription = "password_icon", tint = colorScheme.primary, modifier = Modifier.scale(1.5f))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Пароль", color = colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
            }

        }

        Spacer(modifier = Modifier.weight(1f))


    }
}

@Composable
fun PINScreen(onClick: () -> Unit, viewModel: appViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){

        PinLockScreen(
            onAction = {
                viewModel.setAuthMethod(authMethod = AuthMethod.PINCODE)
                onClick()
            }
        )

    }
}

@Composable
fun PASSWORDScreen(onClick: () -> Unit, viewModel: appViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        CreatePasswordScreenWithoutPIN(onNextAction = { onClick() }, viewModel = viewModel)

    }
}

@Composable
fun CreatePasswordScreenWithoutPIN(
    onNextAction: () -> Unit,
    viewModel: appViewModel
) {
    var showPasswordAlert by remember { mutableStateOf(false) }
    var passwordAlertMessage by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var repeatPasswordValue by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val passwordStorageHelper = remember { PasswordStorageHelper(context) }
    val passwordErrorMessage = stringResource(id = R.string.alert_password_message)

    val isPasswordValid = remember(passwordValue, repeatPasswordValue) {
        checkPasswordsMatch(passwordValue, repeatPasswordValue) && isPasswordValid(passwordValue)
    }

    if (showPasswordAlert) {
        PasswordAlertDialog(
            message = passwordAlertMessage,
            onDismiss = { showPasswordAlert = false }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .padding(paddingColumn)
    ) {
        Spacer(modifier = Modifier.weight(0.1f))

        PasswordFieldWithLabel(
            labelText = stringResource(id = R.string.new_password),
            onValueChange = { passwordValue = it },
            passwordValue = passwordValue,
            labelColor = colorScheme.onSurface,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.weight(0.005f))

        PasswordFieldWithLabel(
            labelText = stringResource(id = R.string.repeat_password),
            onValueChange = { repeatPasswordValue = it },
            passwordValue = repeatPasswordValue,
            labelColor = colorScheme.onSurface,
            onImeAction = {}
        )

        Spacer(modifier = Modifier.weight(0.05f))

        CustomButton(
            text = stringResource(id = R.string.button_continue),
            enabled = true,
            onClick = {
                if (isPasswordValid) {
                    viewModel.setAuthMethod(authMethod = AuthMethod.PASSWORD)
                    Toast.makeText(context, R.string.password_saved, Toast.LENGTH_SHORT).show()
                    passwordStorageHelper.setData("MyPassword", passwordValue.toByteArray())
                    onNextAction()
                } else {
                    passwordAlertMessage = passwordErrorMessage
                    showPasswordAlert = true
                }
            }
        )

        Spacer(modifier = Modifier.weight(0.8f))
    }
}