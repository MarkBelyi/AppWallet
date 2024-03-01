package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape
import kotlinx.coroutines.launch
import kotlin.reflect.full.memberProperties


val myMod = Modifier // просто для примера использования глобальных модификаторов
    .fillMaxWidth()
    .padding(4.dp)


@Composable
fun Wallet(viewModel: appViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val networks by viewModel.allNetworks.observeAsState(initial = emptyList())

    Button(
        onClick = {
            coroutineScope.launch {
                viewModel.addNetworks(context)
            }
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Enter")
    }

    Spacer(modifier = Modifier.height(16.dp))

    LazyColumn(modifier = Modifier.fillMaxWidth().background(color = colorScheme.background)) {
        items(networks) { network ->
            NetworkItem(network = network)
        }
    }
}

@Composable
fun NetworkItem(network: Networks) {
    Column(modifier = Modifier
        .background(color = colorScheme.surface, shape = roundedShape)
        .padding(8.dp)
    ) {
        // Выведем данные по нашим сетям:
        Card(modifier = myMod) {
            for (net in Networks::class.memberProperties)
                Text(text = "${net.name}: ${net.get(network)}")
        }
    }
}