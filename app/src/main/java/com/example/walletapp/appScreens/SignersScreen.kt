package com.example.walletapp.appScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.Event.SignerEvent
import com.example.walletapp.DataBase.State.SignerState
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun SignersScreen(
    viewModel: appViewModel
){
    val signers by viewModel.allSigners.observeAsState(initial = emptyList())
    val isAddingSigner by viewModel.isAddingSigner.observeAsState(initial = false)


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddSignerDialog() },
                containerColor = colorScheme.background,
                contentColor = colorScheme.onBackground,
                shape = roundedShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add signer"
                )
            }
        }
    ) {padding ->

        if (isAddingSigner) {
            AddSignerDialog(viewModel = viewModel)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            items(signers){signer ->
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorScheme.surface, shape = roundedShape)
                ){
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Text(
                                text = signer.name,
                                fontSize = 24.sp,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = signer.address,
                                fontSize = 16.sp,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = signer.email,
                                fontSize = 16.sp,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = signer.telephone,
                                fontSize = 16.sp,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.deleteSigner(signer) }
                    ) {
                       Icon(
                           imageVector = Icons.Rounded.Delete,
                           contentDescription = "Delete signer"
                       )
                    }
                }
            }
        }
    }
}

@Composable
fun AddSignerDialog(
    modifier: Modifier = Modifier,
    viewModel: appViewModel
){

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = { viewModel.hideAddSignerDialog() },
        confirmButton = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd){
                            Button(onClick = {
                                viewModel.insertSigner(Signer(name, email, telephone, type = 1, address))
                                viewModel.hideAddSignerDialog()
                            }) {
                                Text(text = "Save")
                            }
                        }
        },
        title = { Text(text = "Add contact")},
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    placeholder = {
                        Text(text = "Name")
                    }
                )
                TextField(
                    value = address,
                    onValueChange = {
                        address = it
                    },
                    placeholder = {
                        Text(text = "Address")
                    }
                )
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    placeholder = {
                        Text(text = "Email")
                    }
                )
                TextField(
                    value = telephone,
                    onValueChange = {
                        telephone = it
                    },
                    placeholder = {
                        Text(text = "Phone number")
                    }
                )
            }
        }
    )
}















