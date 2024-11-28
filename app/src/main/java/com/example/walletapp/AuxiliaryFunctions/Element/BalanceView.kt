package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.R

@Composable
fun BalancesView(viewModel: appViewModel) {
    val combinedBalances by viewModel.getCombinedBalances().observeAsState(initial = emptyMap())

    Column(modifier = Modifier.padding(4.dp)) {
        val nonZeroBalances = combinedBalances.entries.filter { it.value != 0.0 }

        if (nonZeroBalances.isEmpty()) {
            Text(
                text = stringResource(id = R.string.you_have_not_assets),
                maxLines = 1,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
        } else {
            TableHeader()
            if (nonZeroBalances.size > 3) {
                LazyColumn {
                    items(nonZeroBalances) { entry ->
                        BalanceRow(entry.key, entry.value)
                    }
                }
            } else {
                nonZeroBalances.forEach { entry ->
                    BalanceRow(entry.key, entry.value)
                }
            }
        }
    }
}
