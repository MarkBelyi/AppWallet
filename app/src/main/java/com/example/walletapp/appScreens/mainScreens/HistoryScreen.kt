package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.registrationScreens.ClickedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: appViewModel, onSendingClick: () -> Unit, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val allTX by viewModel.allTX.observeAsState(initial = emptyList())

    LaunchedEffect(key1 = allTX) {
        viewModel.fetchAndStoreTransactions(context = context)
    }

    Scaffold(
        containerColor = colorScheme.inverseSurface,
        topBar = {
            TopAppBar(
                title = { Text("Choose", color = colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ){paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.surface)
                .padding(paddingValues)
        ) {
            if(allTX.isNotEmpty()){
                TXScreens(viewModel = viewModel)
            }
            else{
                Text(
                    text = "You don't have any transactions!",
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(16.dp))
                ClickedText(
                    text = "Create transaction",
                    onClick = onSendingClick
                )
            }
        }

    }
}