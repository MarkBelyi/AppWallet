package com.example.walletapp.mainScreens

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.walletapp.Server.GetAPIString
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun Wallet() {
    var result by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Получаем CoroutineScope связанный с Composable

    Button(
        onClick = {
            coroutineScope.launch {
                result = GetAPIString(con = context, "netlist/1")
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Enter")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(text = result, modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth())
}


/*
@Composable
fun Wallet(viewModel: NetworkViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { viewModel.getNetworksFromApiAndSave(context) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Enter")
        }

        LazyColumn {
            items(viewModel.networks) { network ->
                Text(text = network.network_name, modifier = Modifier.padding(8.dp))
            }
        }
    }
}*/