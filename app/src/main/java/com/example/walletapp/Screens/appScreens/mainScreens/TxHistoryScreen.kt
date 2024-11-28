package com.example.walletapp.Screens.appScreens.mainScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.Element.SearchBarTX
import com.example.walletapp.AuxiliaryFunctions.Element.TXItem
import com.example.walletapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TxHistoryScreen(viewModel: appViewModel, onBackClick: () -> Unit) {
    val allTokensString = stringResource(id = R.string.all_tokens)
    val context = LocalContext.current
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isFilterOn by remember { mutableStateOf(false) }
    var selectedToken by remember { mutableStateOf(allTokensString) }
    var completedOnly by remember { mutableStateOf(false) }

    // Наблюдаем за всеми транзакциями и отфильтрованными
    val allTransactions by viewModel.allUserTX.observeAsState(initial = emptyList())
    val filteredTransactions by viewModel.filteredTransactions.observeAsState(initial = emptyList())

    // Используем транзакции для отображения: все или отфильтрованные
    val transactions =
        if (searchText.text.isNotEmpty() || selectedToken != allTokensString || completedOnly) {
            filteredTransactions
        } else {
            allTransactions
        }

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchAndStoreTransactions(context = context) {}
    }

    //TODO(Добавить обновление через свайп, совет используй мою кастомку)
    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.action_tx_history),
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
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            "Back",
                            tint = colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    //TODO(Добавить анимацию)
                    IconButton(onClick = { isFilterOn = !isFilterOn }) {
                        Icon(
                            painterResource(id = if (isFilterOn) R.drawable.filter_off else R.drawable.filter_on),
                            "Filter",
                            tint = colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (isFilterOn) {
                SearchBarTX(
                    searchText = searchText,
                    onTextChange = { newValue ->
                        searchText = newValue
                        viewModel.filterTX(newValue.text, selectedToken, completedOnly)
                    },
                    onTokenSelected = { token ->
                        selectedToken = token
                        viewModel.filterTX(searchText.text, token, completedOnly)
                    },
                    onCompletedOnlyChanged = { completed ->
                        completedOnly = completed
                        viewModel.filterTX(searchText.text, selectedToken, completed)
                    }
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                if (transactions.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.nothing_here),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(padding),
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(transactions) { tx ->
                        TXItem(tx = tx)
                    }
                }
            }
        }
    }
}









