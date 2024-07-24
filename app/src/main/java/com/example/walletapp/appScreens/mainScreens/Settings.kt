package com.example.walletapp.appScreens.mainScreens

import android.content.Context
import androidx.compose.foundation.border
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: appViewModel, onChangePasswordClick: () -> Unit, onChangeLanguageClick: () -> Unit, onBackClick: () -> Unit, navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
    val locale = Locale.getDefault().language
    val folderName = if (locale == "ru") "ru" else "en"
    val jsonStr = context.assets.open("$folderName/settings.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val type = object : TypeToken<List<SettingsBlock>>() {}.type
    val settingsBlocks: List<SettingsBlock> = gson.fromJson(jsonStr, type)

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
                            val electronicApprovalEnabled = sharedPreferences.getBoolean("electronic_approval", false)
                            when(item.prefsKey){
                                "show_test_networks" -> viewModel.updateShowTestNetworks(newValue)
                                "change_theme" -> viewModel.toggleTheme()

                            }
                            if (item.prefsKey == "electronic_approval" && electronicApprovalEnabled){
                                navHostController.navigate("SignerMode")
                            }
                            if (item.prefsKey == "electronic_approval" && !electronicApprovalEnabled){
                                navHostController.navigate("App")
                            }
                        },
                        onClick = {
                            when(item.prefsKey){
                                "change_password" -> onChangePasswordClick()
                                "change_language" -> onChangeLanguageClick()
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
        ),
        onClick = {
            if (type == ElementType.CHECKBOX || type == ElementType.SWITCH) {
                val newCheckedState = !checkedState.value
                checkedState.value = newCheckedState
                onCheckedChange(newCheckedState)
            }
            else{
                onClick()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
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


