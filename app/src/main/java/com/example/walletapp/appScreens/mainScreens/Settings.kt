package com.example.walletapp.appScreens.mainScreens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken


data class Element(
    val name: String,
    val description: String,
    val type: ElementType,
    val prefsKey: String
)

enum class ElementType {
    @SerializedName("CHECKBOX") CHECKBOX,
    @SerializedName("SWITCH") SWITCH
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {


    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
    val gson = Gson()
    val jsonStr = context.assets.open("settings.json").bufferedReader().use { it.readText() }


    // Десериализация JSON строки в список элементов
    val type = object : TypeToken<List<Element>>() {}.type
    val settingsItems: List<Element> = gson.fromJson(jsonStr, type)



    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            items(settingsItems.size) { index ->
                val item = settingsItems[index]
                var checked by remember { mutableStateOf(sharedPreferences.getBoolean(item.prefsKey, false)) }
                SettingItem(title = item.name, subtitle = item.description, control = {
                    when (item.type) {
                        ElementType.CHECKBOX -> Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                sharedPreferences.edit().putBoolean(item.prefsKey, it).apply()
                            }
                        )
                        ElementType.SWITCH -> Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                sharedPreferences.edit().putBoolean(item.prefsKey, it).apply()
                            }
                        )
                    }
                })
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
        control()
    }
}




