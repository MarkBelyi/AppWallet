package com.example.walletapp.appScreens.mainScreens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

@Composable
fun SettingsScreen(viewModel: appViewModel) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
    val locale = Locale.getDefault().language
    val folderName = if (locale == "ru") "ru" else "en"
    val jsonStr = context.assets.open("$folderName/settings.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val type = object : TypeToken<List<SettingsBlock>>() {}.type
    val settingsBlocks: List<SettingsBlock> = gson.fromJson(jsonStr, type)

    val exportKeyLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.e("exportKeyLauncher", "Result received")
        Toast.makeText(context, "Export result received", Toast.LENGTH_SHORT).show()
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                Log.e("exportKeyLauncher", "Uri received: $uri")
                Toast.makeText(context, "Export URI received: $uri", Toast.LENGTH_SHORT).show()
                viewModel.exportKey(context, uri)
            }
        }
    }

    val importKeyLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.e("importKeyLauncher", "Result received")
        Toast.makeText(context, "Import result received", Toast.LENGTH_SHORT).show()
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                Log.e("importKeyLauncher", "Uri received: $uri")
                Toast.makeText(context, "Import URI received: $uri", Toast.LENGTH_SHORT).show()
                viewModel.importKey(context, uri)
            }
        }
    }

    fun showExportKeyDialog() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, "Keys.h2k")
            setType("*/*")
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/octet-stream"))
        }
        Log.e("showExportKeyDialog", "Launching export key dialog")
        Toast.makeText(context, "Launching export key dialog", Toast.LENGTH_SHORT).show()
        exportKeyLauncher.launch(intent)
    }

    fun showImportKeyDialog() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("*/*")
        }
        Log.e("showImportKeyDialog", "Launching import key dialog")
        Toast.makeText(context, "Launching import key dialog", Toast.LENGTH_SHORT).show()
        importKeyLauncher.launch(intent)
    }

    Scaffold(
        containerColor = colorScheme.background,
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
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
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
                            if (item.prefsKey == "show_test_networks") {
                                viewModel.updateShowTestNetworks(newValue)
                            }
                        },
                        onArrowClick = {
                            when(item.prefsKey){
                                "import_secret_key" -> showImportKeyDialog()
                                "export_secret_key" -> showExportKeyDialog()
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
    onCheckedChange: (Boolean) -> Unit
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
                }
            }
            .border(0.75.dp, colorScheme.primary, roundedShape)
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

                if(subtitle.isNotEmpty()){

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
                        onCheckedChange = null, // Обработчик тут убираем
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                ElementType.RADIOBUTTON -> RadioButton(
                    selected = checkedState.value,
                    onClick = { onCheckedChange(!checkedState.value) } // Обработчик оставляем здесь
                )

                ElementType.ARROW -> IconButton(
                    onClick = { /* Обработчик нажатия  */ }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
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
    prefsKey: String,
    sharedPreferences: SharedPreferences,
    onSettingChange: (Boolean) -> Unit
) {
    var checkedState by remember { mutableStateOf(sharedPreferences.getBoolean(prefsKey, false)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable {
                if (type == ElementType.CHECKBOX || type == ElementType.SWITCH) {
                    val newCheckedState = !checkedState
                    checkedState = newCheckedState
                    sharedPreferences
                        .edit()
                        .putBoolean(prefsKey, newCheckedState)
                        .apply()
                    onSettingChange(newCheckedState)
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
            }

            when (type) {
                ElementType.CHECKBOX -> Checkbox(
                    checked = checkedState,
                    onCheckedChange = { newValue ->
                        checkedState = newValue
                        sharedPreferences.edit().putBoolean(prefsKey, newValue).apply()
                        onSettingChange(newValue)
                    }
                )
                ElementType.SWITCH -> Switch(
                    checked = checkedState,
                    onCheckedChange = { newValue ->
                        checkedState = newValue
                        sharedPreferences.edit().putBoolean(prefsKey, newValue).apply()
                        onSettingChange(newValue)
                    }
                )
                ElementType.RADIOBUTTON -> RadioButton(
                    selected = checkedState,
                    onClick = {
                        val newValue = !checkedState
                        checkedState = newValue
                        sharedPreferences.edit().putBoolean(prefsKey, newValue).apply()
                        onSettingChange(newValue)
                    }
                )
                ElementType.ARROW -> IconButton(
                    onClick = { /* Обработчик нажатия  */ }
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
    }
}


