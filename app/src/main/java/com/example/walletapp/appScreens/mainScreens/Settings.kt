package com.example.walletapp.appScreens.mainScreens

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.MainActivity
import com.example.walletapp.R
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.helper.DESCrypt
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.helper.isBigInteger
import com.example.walletapp.ui.theme.newRoundedShape
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.web3j.crypto.ECKeyPair
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.util.Locale

data class SettingsBlock(
    val header: String,
    val items: List<Element>
)

data class Element(
    val name: String,
    val description: String,
    val type: ElementType,
    val prefsKey: String
)

enum class ElementType {
    CHECKBOX, SWITCH, RADIOBUTTON, ARROW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: appViewModel,
    onChangePasswordClick: () -> Unit,
    onChangeLanguageClick: () -> Unit,
    onBackClick: () -> Unit,
    navHostController: NavHostController
) {

    val context = LocalContext.current
    val versionName = getAppVersionName(context)
    val sharedPreferences =
        context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
    val locale = Locale.getDefault().language
    val folderName = if (locale == "ru") "ru" else "en"
    val jsonStr =
        context.assets.open("$folderName/settings.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val type = object : TypeToken<List<SettingsBlock>>() {}.type
    val settingsBlocks: List<SettingsBlock> = gson.fromJson(jsonStr, type)
    val ps = PasswordStorageHelper(context)
    var showDialogLogOut by remember { mutableStateOf(false) }
    var showDialogDelete by remember { mutableStateOf(false) }
    var deleteReason by remember { mutableStateOf("") }

    fun logout(){
        viewModel.clearDataBase()
        ps.remove("MyPublicKey");
        ps.remove("MyPrivateKey");
        val sharedPrefs = context.getSharedPreferences("com.example.h2k.PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("VisitedApp", false).apply()
        navHostController.navigate("Registration")
    }

    // Функция для показа уведомления об успешном удалении и закрытия приложения
    fun showDeletionSuccessDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setMessage(context.getString(R.string.user_deleted))
            .setPositiveButton("OK") { _, _ ->
                (context as? Activity)?.finishAffinity()
                val sharedPrefs = context.getSharedPreferences("com.example.h2k.PREFS", Context.MODE_PRIVATE)
                sharedPrefs.edit().putBoolean("VisitedApp", false).apply()
                navHostController.navigate("Registration")
            }
            .setOnDismissListener {
                (context as? Activity)?.finishAffinity()
                val sharedPrefs = context.getSharedPreferences("com.example.h2k.PREFS", Context.MODE_PRIVATE)
                sharedPrefs.edit().putBoolean("VisitedApp", false).apply()
                navHostController.navigate("Registration")
            }
        builder.create().show()

    }

    if(showDialogDelete){
        AlertDialog(
            containerColor = colorScheme.surface,
            tonalElevation = 0.dp,
            onDismissRequest = { showDialogDelete = false },
            title = {
                Text(
                    stringResource(id = R.string.delete_account),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(id = R.string.delete_me_warning),
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deleteReason,
                        onValueChange = { deleteReason = it },
                        singleLine = true,
                        maxLines = 1,
                        shape = newRoundedShape,
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.write_delete_reason),
                                fontWeight = FontWeight.Light
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface,
                            focusedLabelColor = colorScheme.primary,
                            unfocusedContainerColor = colorScheme.surface,
                            unfocusedLabelColor = colorScheme.onBackground,
                            cursorColor = colorScheme.primary
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (deleteReason.isNotEmpty()) {
                        viewModel.deleteMyAccount(deleteReason)
                        showDialogDelete = false // Закрыть диалог после подтверждения
                        showDeletionSuccessDialog()
                    }
                    else{
                        Toast.makeText(context, R.string.write_delete_reason, Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(
                        stringResource(id = R.string.accept),
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialogDelete = false
                }) {
                    Text(
                        stringResource(id = R.string.cancel),
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            shape = newRoundedShape
        )
    }


    if(showDialogLogOut){
        AlertDialog(
            containerColor = colorScheme.surface,
            tonalElevation = 0.dp,
            onDismissRequest = { showDialogLogOut = false },
            title = {
                Text(
                    stringResource(id = R.string.exit_from_account),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    fontSize = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    logout()
                }) {
                    Text(
                        stringResource(id = R.string.accept),
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogLogOut = false }) {
                    Text(
                        stringResource(id = R.string.cancel),
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            shape = newRoundedShape
        )
    }

    //export and import
    val bookmarkExportFilePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val outputStream = context.contentResolver.openOutputStream(uri)!!
                    val privKey = ps.getData("MyPrivateKey") ?: return@let ""
                    val realpriv = BigInteger(1, privKey).toString()
                    val encrypt = DESCrypt.encrypt(realpriv)
                    outputStream.write(encrypt)
                    outputStream.flush()
                    outputStream.close()
                    Toast.makeText(
                        context,
                        androidx.appcompat.R.string.abc_action_mode_done,
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }

    fun showExportBookmarksDialog() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, "SafinaKeys.sfn");
            setType("*/*") // That's needed for some reason, crashes otherwise
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/octet-stream"))
        }
        bookmarkExportFilePicker.launch(intent) // See bookmarkImportFilePicker declaration below for result handler
    }

    val bookmarkImportFilePicker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let{ uri ->
                val inputStream = context.contentResolver.openInputStream(uri)!!
                val bytes = inputStream.readBytes()
                inputStream.close()

                val pk = ps.getData("MyPublicKey")
                println("Public Key: {$pk}")
                println(GetMyAddr(context))

                val decrypt = DESCrypt.decrypt(bytes)
                val text=String(decrypt, StandardCharsets.UTF_8)
                if (!isBigInteger(text)) { Toast.makeText(context, R.string.wrong_key, Toast.LENGTH_SHORT).show(); return@let}
                val k: ECKeyPair = ECKeyPair.create(BigInteger(text))

                ps.setData("MyPrivateKey",k.privateKey.toByteArray())
                ps.setData("MyPublicKey",k.publicKey.toByteArray())

                //Если не предусмотрена мультиюзерность(а её у нас вроде нет..), то нужно очистить таблицы с балансами, кошельками и всем что осталось от предыдущих владельцев, ведь новый ключ - новые данные
                viewModel.clearDataBase()

                //Теперь это наш текущий ключ.
                viewModel.insertSigner(Signer(name = context.getString(R.string.default_name_of_signer), email = "", telephone = "", type = 1, address = GetMyAddr(context), isFavorite = false))

                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.create()
                builder.setTitle(androidx.appcompat.R.string.abc_action_mode_done)
                builder.setMessage(androidx.appcompat.R.string.abc_action_mode_done)
                builder.setPositiveButton("OK") { _, _ ->
                    // Переход на главный экран
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                builder.show()
            }
        }
    }

    fun showImportBookmarksDialog() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("*/*")
        }
        bookmarkImportFilePicker.launch(intent) // See bookmarkImportFilePicker declaration below for result handler
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings), color = colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {

            items(settingsBlocks) { block ->

                Text(
                    text = block.header,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                )

                block.items.forEach { item ->
                    val checkedState = remember {
                        mutableStateOf(
                            sharedPreferences.getBoolean(
                                item.prefsKey,
                                false
                            )
                        )
                    }
                    SettingItem(
                        title = item.name,
                        subtitle = item.description,
                        type = item.type,
                        checkedState = checkedState,
                        onCheckedChange = { newValue ->
                            sharedPreferences.edit().putBoolean(item.prefsKey, newValue).apply()
                            val electronicApprovalEnabled =
                                sharedPreferences.getBoolean("electronic_approval", false)
                            when (item.prefsKey) {
                                "show_test_networks" -> viewModel.updateShowTestNetworks(newValue)
                                "change_theme" -> viewModel.toggleTheme()

                            }
                            if (item.prefsKey == "electronic_approval" && electronicApprovalEnabled) {
                                navHostController.navigate("SignerMode")
                            }
                            if (item.prefsKey == "electronic_approval" && !electronicApprovalEnabled) {
                                navHostController.navigate("App")
                            }
                            if(item.prefsKey == "advanced_user"){
                                sharedPreferences.edit().putBoolean("advanced_user", newValue).apply()
                            }
                        },
                        onClick = {
                            when (item.prefsKey) {
                                "change_password" -> onChangePasswordClick()
                                "change_language" -> onChangeLanguageClick()
                                "import_secret_key" -> showImportBookmarksDialog()
                                "export_secret_key" -> showExportBookmarksDialog()
                                "logout_account" -> showDialogLogOut = true
                                "delete_account" -> showDialogDelete = true
                            }
                        }
                    )
                }
            }
            
            item(1){
                Text(
                    text = "Safina Wallet: $versionName",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = colorScheme.scrim,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

fun getAppVersionName(context: Context): String {
    return try {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        "Unknown"
    }
}



@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    type: ElementType,
    checkedState: MutableState<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
        shape = newRoundedShape,
        onClick = {
            if (type == ElementType.CHECKBOX || type == ElementType.SWITCH) {
                val newCheckedState = !checkedState.value
                checkedState.value = newCheckedState
                onCheckedChange(newCheckedState)
            } else {
                onClick()
            }
        },
        border = BorderStroke(0.5.dp, colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {

                Text(
                    text = title,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )

                if (subtitle.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(4.dp))

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = subtitle,
                        color = colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                }
            }

            when (type) {
                ElementType.CHECKBOX ->
                    Checkbox(
                        checked = checkedState.value,
                        onCheckedChange = { newCheckedState ->
                            checkedState.value = newCheckedState
                            onCheckedChange(newCheckedState)
                        },
                        modifier = Modifier
                            .scale(1.4f)
                            .padding(start = 16.dp)
                    )

                ElementType.SWITCH ->
                    Switch(
                        checked = checkedState.value,
                        onCheckedChange = { newCheckedState ->
                            checkedState.value = newCheckedState
                            onCheckedChange(newCheckedState)
                        },
                        colors = SwitchDefaults.colors(
                            uncheckedBorderColor = colorScheme.primary,
                            uncheckedIconColor = colorScheme.primary,
                            uncheckedTrackColor = colorScheme.surface,
                            uncheckedThumbColor = colorScheme.primary
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )

                ElementType.RADIOBUTTON -> RadioButton(
                    selected = checkedState.value,
                    onClick = { onCheckedChange(!checkedState.value) }
                )

                ElementType.ARROW -> IconButton(
                    onClick = {
                        onClick()
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
    }
}


