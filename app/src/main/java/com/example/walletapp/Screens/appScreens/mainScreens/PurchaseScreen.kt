package com.example.walletapp.Screens.appScreens.mainScreens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.walletapp.AuxiliaryFunctions.DataClass.LinkItem
import com.example.walletapp.AuxiliaryFunctions.Element.ClickableLinkText
import com.example.walletapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    onBackClick: () -> Unit
) {

    val linkItems = listOf(
        LinkItem(
            stringResource(id = R.string.service),
            "https://wallet.tg/",
            stringResource(id = R.string.explain_service)
        ),
        LinkItem(
            stringResource(id = R.string.bot),
            "https://t.me/wallet",
            stringResource(id = R.string.explain_bot)
        ),
        LinkItem(
            stringResource(id = R.string.video_instruction),
            "https://youtu.be/dQw4w9WgXcQ?si=o5l2SJkfWk76o6f3",
            stringResource(id = R.string.explain_video_instruction)
        ),
        LinkItem(
            stringResource(id = R.string.text_instruction),
            "https://wiki.h2k.me/doku.php?id=ru:lite:instr:4",
            stringResource(id = R.string.explain_text_instruction)
        )
    )

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.buy_crypto),
                        color = colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    scrolledContainerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(linkItems) { item ->
                ClickableLinkText(text = item.text, url = item.url, instruction = item.instruction)
            }
        }
    }
}




