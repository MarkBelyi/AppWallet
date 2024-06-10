package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignersScreen(
    viewModel: appViewModel,
    onCurrentSignerClick: (String) -> Unit,
    onAddSignerClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val signers by viewModel.allSigners.observeAsState(initial = emptyList())

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = "Signers", color = colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    scrolledContainerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    )  { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(signers) { signer ->
                SignerItem(
                    signer = signer,
                    viewModel = viewModel,
                    onClick = {
                        onCurrentSignerClick(signer.address)
                    }
                )
            }
            item {
                AddSignerCard(
                    onClick = { onAddSignerClick() }
                )
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignerItem(signer: Signer, viewModel: appViewModel, onClick: (String) -> Unit) {
    Card(
        onClick = {onClick(signer.address)},
        shape = roundedShape,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
        border = BorderStroke(width = 0.75.dp, color = colorScheme.primary),

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = signer.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    minLines = 1,
                )
                Text(
                    text = signer.address,
                    fontSize = 14.sp,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    minLines = 1,

                    )
                Text(
                    text = signer.email,
                    fontSize = 14.sp,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    minLines = 1,

                    )
                Text(
                    text = signer.telephone,
                    fontSize = 14.sp,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Light,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    minLines = 1,
                )

            }
            Column(
                modifier = Modifier.padding(16.dp)
            ){
                IconButton(
                    onClick = { viewModel.deleteSigner(signer) },
                    modifier = Modifier
                        .scale(1.2f)
                        .alpha(0.9f),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete signer",
                        tint = colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSignerCard(onClick: () -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize(), // This makes the Box fill the entire screen
        contentAlignment = Alignment.BottomEnd // This aligns the content to the bottom-end corner
    ){
        Card(
            modifier = Modifier
                .width(48.dp)
                .height(48.dp), // Высота карточки
            shape = roundedShape,
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            onClick = onClick
        ) {
            Box(
                contentAlignment = Alignment.Center, // Центрирование содержимого
                modifier = Modifier.fillMaxSize().weight(1f) // Заполняет весь размер карточки
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add signer",
                    modifier = Modifier.size(24.dp), // Размер иконки
                    tint = colorScheme.primary
                )
            }
        }
    }

}





