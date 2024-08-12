package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletapp.Element.ClickableText
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignHistoryScreen(viewModel: appViewModel, onSendingClick: () -> Unit, onBackClick: () -> Unit) {
    val allTX by viewModel.allTX.observeAsState(initial = emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.needSignTX(context = context){}
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.action_sign_history), color = colorScheme.onSurface) },
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
    ){paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(paddingValues)
        ) {
            if(allTX.isNotEmpty()){
                TXScreensHistory(viewModel = viewModel)
            }
            else{
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.no_transactions),
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(16.dp))
                ClickableText(
                    text = stringResource(id = R.string.create_transaction),
                    onClick = onSendingClick
                )
            }
        }

    }
}

@Composable
fun TXScreensHistory(viewModel: appViewModel) {
    val txs by viewModel.allTX.observeAsState(initial = emptyList())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
    ) {
        items(txs) { tx ->
            SignItem(
                tx = tx,
                onSign = { viewModel.signTransaction(tx.unid) },
                onReject = { reason ->
                    viewModel.rejectTransaction(tx.unid, reason = reason)
                },
                viewModel = viewModel
            )
        }
    }
}