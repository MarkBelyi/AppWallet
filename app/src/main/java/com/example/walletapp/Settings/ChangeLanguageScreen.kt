package com.example.walletapp.Settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.DataClass.LanguageOption
import com.example.walletapp.AuxiliaryFunctions.Element.LanguageItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeLanguageScreen(
    viewModel: appViewModel,
    onBackClick: () -> Unit
) {
    val languages = listOf(
        LanguageOption("Русский", "Russian", Locale("ru")),
        LanguageOption("English", "English", Locale("en")),
    )

    val selectedLanguage by viewModel.isEnglishLanguage.observeAsState(
        initial = viewModel.isSystemLanguageEnglish()
    )

    val currentLocale = if (selectedLanguage) Locale("en") else Locale("ru")

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Language", color = colorScheme.onSurface) },
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
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            items(languages) { language ->
                LanguageItem(
                    language = language,
                    isSelected = currentLocale.language == language.locale.language,
                    onSelect = { viewModel.toggleLanguage() }
                )
            }
        }
    }
}

