package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.walletapp.appViewModel.appViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TxHistoryScreen(viewModel: appViewModel, onBackClick: () -> Unit) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.needSignTX(context){}
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "История транзакций",
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
    ) {padding ->
        Text(
            text = "Здесь пока ничего нет! " +
                    "Но будут транзакции, для отслеживания их состояния, " +
                    "они отличатся от Истории Подписаний, " +
                    "ведь там только для отслеживания своих подписей, " +
                    "а здесь уже сами транзакции и отслеживание подписей других людей",
            fontSize = 16.sp,
            modifier = Modifier.padding(padding),
            color = colorScheme.onSurface,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
    }



}