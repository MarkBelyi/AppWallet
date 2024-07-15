package com.example.walletapp.appScreens.mainScreens

import android.content.Context
import android.util.Log
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.helper.PasswordStorageHelper
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
    val locale = Locale.getDefault().language // Получаем текущий язык
    val folderName = if (locale == "ru") "ru" else "en" // Выбираем папку на основе языка
    val jsonStr = context.assets.open("$folderName/settings.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val type = object : TypeToken<List<SettingsBlock>>() {}.type
    val settingsBlocks: List<SettingsBlock> = gson.fromJson(jsonStr, type)
    val ps = PasswordStorageHelper(context = context)
    val secretKey = ps.getData("MyPrivateKey")


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
                        secretKey = secretKey.toString()
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
    secretKey: String
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                    onClick = {
                        Log.d("SettingsScreen", "Secret key: $secretKey")
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
    }
}


