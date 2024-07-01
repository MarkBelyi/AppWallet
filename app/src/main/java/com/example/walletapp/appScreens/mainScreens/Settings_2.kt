package com.example.walletapp.appScreens.mainScreens

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Locale
import javax.crypto.Cipher


@Composable
    fun SettingsScreen_2() {
        val context = LocalContext.current
        val sharedPreferences = context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)

        // Сохраните тестовый секретный ключ при загрузке экрана настроек
        saveTestSecretKey(sharedPreferences)

        val locale = Locale.getDefault().language
        val folderName = if (locale == "ru") "ru" else "en"

        val jsonStr = context.assets.open("$folderName/settings.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<SettingsBlock>>() {}.type
        val settingsBlocks: List<SettingsBlock> = gson.fromJson(jsonStr, type)

        var secretKey by remember { mutableStateOf("") }
        var encryptedKey by remember { mutableStateOf("") }
        var importStatus by remember { mutableStateOf("") }
        var exportStatus by remember { mutableStateOf("") }

        Scaffold(
            containerColor = colorScheme.inverseSurface,
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(settingsBlocks) { block ->
                        Text(
                            text = block.header,
                            color = colorScheme.onSurface,
                            maxLines = 1,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        )
                        block.items.forEach { item ->
                            val checkedState = remember {
                                mutableStateOf(sharedPreferences.getBoolean(item.prefsKey, false))
                            }
                            SettingItem(
                                title = item.name,
                                subtitle = item.description,
                                type = item.type,
                                checkedState = checkedState,
                                onCheckedChange = { newValue ->
                                    sharedPreferences.edit().putBoolean(item.prefsKey, newValue).apply()
                                },
                                onArrowClick = {
                                    when (item.prefsKey) {
                                        "import_secret_key" -> {
                                            Log.d("SettingsScreen", "Importing secret key")
                                            importSecretKey(context, sharedPreferences)?.let {
                                                secretKey = it
                                                importStatus = "Импорт успешен"
                                            } ?: run {
                                                importStatus = "Импорт не удался"
                                            }
                                        }
                                        "export_secret_key" -> {
                                            Log.d("SettingsScreen", "Exporting secret key")
                                            exportSecretKey(context, sharedPreferences)?.let {
                                                encryptedKey = it
                                                exportStatus = "Экспорт успешен"
                                            } ?: run {
                                                exportStatus = "Экспорт не удался"
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                // Поля для проверки
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Секретный ключ:")
                    TextField(value = secretKey, onValueChange = { secretKey = it })

                    Text("Зашифрованный ключ:")
                    TextField(value = encryptedKey, onValueChange = { encryptedKey = it })

                    Button(onClick = {
                        exportSecretKey(context, sharedPreferences)?.let {
                            encryptedKey = it
                            exportStatus = "Экспорт успешен"
                        } ?: run {
                            exportStatus = "Экспорт не удался"
                        }
                    }) {
                        Text("Экспортировать секретный ключ")
                    }
                    Text(exportStatus)

                    Button(onClick = {
                        importSecretKey(context, sharedPreferences)?.let {
                            secretKey = it
                            importStatus = "Импорт успешен"
                        } ?: run {
                            importStatus = "Импорт не удался"
                        }
                    }) {
                        Text("Импортировать секретный ключ")
                    }
                    Text(importStatus)
                }
            }
        }
    }

    fun saveTestSecretKey(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit().putString("secret_key", "my_secret_key").apply()
    }

    fun exportSecretKey(context: Context, sharedPreferences: SharedPreferences): String? {
        val secretKey = sharedPreferences.getString("secret_key", null)
        return if (secretKey != null) {
            val keyPair = generateKeyPair()
            val publicKey = keyPair.public
            val privateKey = keyPair.private

            val encryptedKey = encryptData(secretKey, publicKey)
            sharedPreferences.edit().putString("encrypted_secret_key", encryptedKey).apply()
            sharedPreferences.edit().putString("private_key", Base64.encodeToString(privateKey.encoded, Base64.DEFAULT)).apply()

            Log.d("SettingsScreen", "Экспортированный зашифрованный ключ: $encryptedKey")
            encryptedKey
        } else {
            Log.d("SettingsScreen", "Secret key not found.")
            null
        }
    }

    fun importSecretKey(context: Context, sharedPreferences: SharedPreferences): String? {
        val encryptedKey = sharedPreferences.getString("encrypted_secret_key", null)
        val privateKeyString = sharedPreferences.getString("private_key", null)

        return if (encryptedKey != null && privateKeyString != null) {
            val privateKeyBytes = Base64.decode(privateKeyString, Base64.DEFAULT)
            val privateKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes))

            val secretKey = decryptData(encryptedKey, privateKey)
            sharedPreferences.edit().putString("secret_key", secretKey).apply()
            Log.d("SettingsScreen", "Импортированный секретный ключ: $secretKey")
            secretKey
        } else {
            Log.d("SettingsScreen", "Encrypted secret key or private key not found.")
            null
        }
    }

    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }

    fun encryptData(data: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decryptData(data: String, privateKey: PrivateKey): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decodedBytes = Base64.decode(data, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }

    @Composable
    fun SettingItem(
        title: String,
        subtitle: String,
        type: ElementType,
        checkedState: MutableState<Boolean>,
        onCheckedChange: (Boolean) -> Unit,
        onArrowClick: () -> Unit = {}
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable {
                    if (type == ElementType.CHECKBOX || type == ElementType.SWITCH) {
                        val newCheckedState = !checkedState.value
                        checkedState.value = newCheckedState
                        onCheckedChange(newCheckedState)
                    } else if (type == ElementType.ARROW) {
                        onArrowClick()
                    }
                }
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
                        .padding(16.dp)
                ) {
                    Text(
                        text = title,
                        color = colorScheme.onSurface,
                        maxLines = 1,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W400,
                    )

                    if (subtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(thickness = 0.5.dp, color = colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = subtitle,
                            color = colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }
                when (type) {
                    ElementType.CHECKBOX -> Box(
                        Modifier.clickable {
                            if (type == ElementType.CHECKBOX) {
                                val newCheckedState = !checkedState.value
                                checkedState.value = newCheckedState
                                onCheckedChange(newCheckedState)
                            }
                        }
                    ) {
                        Checkbox(
                            checked = checkedState.value,
                            onCheckedChange = null,
                            modifier = Modifier.scale(1.4f).padding(start = 16.dp)
                        )
                    }

                    ElementType.SWITCH -> Box(
                        Modifier.clickable {
                            if (type == ElementType.SWITCH) {
                                val newCheckedState = !checkedState.value
                                checkedState.value = newCheckedState
                                onCheckedChange(newCheckedState)
                            }
                        }
                    ) {
                        Switch(
                            checked = checkedState.value,
                            colors = SwitchDefaults.colors(
                                uncheckedBorderColor = colorScheme.primary,
                                uncheckedIconColor = colorScheme.primary,
                                uncheckedTrackColor = colorScheme.surface,
                                uncheckedThumbColor = colorScheme.primary
                            ),
                            onCheckedChange = null,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    ElementType.RADIOBUTTON -> RadioButton(
                        selected = checkedState.value,
                        onClick = { onCheckedChange(!checkedState.value) }
                    )

                    ElementType.ARROW -> IconButton(
                        onClick = onArrowClick
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }
        }
    }
