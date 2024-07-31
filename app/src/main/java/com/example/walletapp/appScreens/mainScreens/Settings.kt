package com.example.walletapp.appScreens.mainScreens

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val sharedPreferences =
        context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
    val locale = Locale.getDefault().language
    val folderName = if (locale == "ru") "ru" else "en"
    val jsonStr =
        context.assets.open("$folderName/settings.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val type = object : TypeToken<List<SettingsBlock>>() {}.type
    val settingsBlocks: List<SettingsBlock> = gson.fromJson(jsonStr, type)

    //export and import
    val bookmarkExportFilePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //val ddff: String? = result.data?.data?.path
                result.data?.data?.let { uri ->
                    val outputStream = context.contentResolver.openOutputStream(uri)!!
                    val ps = PasswordStorageHelper(context)
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
                val ps = PasswordStorageHelper(context)

                val pk = ps.getData("MyPublicKey")
                println("Public Key: {$pk}")
                println(GetMyAddr(context))

                val decrypt = DESCrypt.decrypt(bytes)
                val text=String(decrypt, StandardCharsets.UTF_8)
                if (!isBigInteger(text)) { Toast.makeText(context, "Неправильный ключ", Toast.LENGTH_SHORT).show(); return@let}
                val k: ECKeyPair = ECKeyPair.create(BigInteger(text))

                ps.setData("MyPrivateKey",k.privateKey.toByteArray())
                ps.setData("MyPublicKey",k.publicKey.toByteArray())

                //Если не предусмотрена мультиюзерность(а её у нас вроде нет..), то нужно очистить таблицы с балансами, кошельками и всем что осталось от предыдущих владельцев, ведь новый ключ - новые данные
                viewModel.clearDataBase()

                //Теперь это наш текущий ключ.
                val pk_new = ps.getData("MyPublicKey")
                println("Public Key: {$pk_new}")
                println(GetMyAddr(context))
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
                title = { Text("Settings", color = colorScheme.onSurface) },
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
                        },
                        onClick = {
                            when (item.prefsKey) {
                                "change_password" -> onChangePasswordClick()
                                "change_language" -> onChangeLanguageClick()
                                "import_secret_key" -> showImportBookmarksDialog()
                                "export_secret_key" -> showExportBookmarksDialog()
                                "advanced_user" -> {}
                            }
                        }
                    )
                }
            }
        }
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


