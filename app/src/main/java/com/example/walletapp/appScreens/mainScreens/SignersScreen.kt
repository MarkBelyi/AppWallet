package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape

@Composable
fun SignersScreen(
    viewModel: appViewModel,
    onCurrentSignerClick: (String) -> Unit
) {
    val signers by viewModel.allSigners.observeAsState(initial = emptyList())
    val isAddingSigner by viewModel.isAddingSigner.observeAsState(initial = false)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddSignerDialog() },
                containerColor = Color.White,
                contentColor = colorScheme.onBackground,
                shape = roundedShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add signer"
                )
            }
        }
    ) { padding ->

        if (isAddingSigner) {
            AddSignerDialog(viewModel = viewModel)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
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
            contentColor = colorScheme.onBackground
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 16.dp
        )

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


@Composable
fun AddSignerDialog(
    modifier: Modifier = Modifier,
    viewModel: appViewModel
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }

    fun updateState(updateFunc: (String) -> Unit): (String) -> Unit = { newValue ->
        updateFunc(newValue)
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = { viewModel.hideAddSignerDialog() },
        confirmButton = {
            Button(onClick = {
                viewModel.insertSigner(Signer(name, email, telephone, type = 1, address))
                viewModel.hideAddSignerDialog()
            }) {
                Text(text = "Save")
            }
        },
        title = { Text(text = "Add contact") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomOutlinedTextField(value = name, onValueChange = updateState { name = it }, placeholder = "Name")
                CustomOutlinedTextField(value = address, onValueChange = updateState { address = it }, placeholder = "Address")
                CustomOutlinedTextField(value = email, onValueChange = updateState { email = it }, placeholder = "Email")
                CustomOutlinedTextField(value = telephone, onValueChange = updateState { telephone = it }, placeholder = "Phone number")
            }
        }
    )
}

@Composable
fun CustomOutlinedTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        shape = roundedShape,
        colors = TextFieldDefaults.colors(
            focusedTextColor = colorScheme.onBackground,
    /*unfocusedTextColor: Color = ...,
    disabledTextColor: Color = ...,
    errorTextColor: Color = ...,
    focusedContainerColor: Color = ...,
    unfocusedContainerColor: Color = ...,
    disabledContainerColor: Color = ...,
    errorContainerColor: Color = ...,
    cursorColor: Color = ...,
    errorCursorColor: Color = ...,
    selectionColors: TextSelectionColors = ...,
    focusedIndicatorColor: Color = ...,
    unfocusedIndicatorColor: Color = ...,
    disabledIndicatorColor: Color = ...,
    errorIndicatorColor: Color = ...,
    focusedLeadingIconColor: Color = ...,
    unfocusedLeadingIconColor: Color = ...,
    disabledLeadingIconColor: Color = ...,
    errorLeadingIconColor: Color = ...,
    focusedTrailingIconColor: Color = ...,
    unfocusedTrailingIconColor: Color = ...,
    disabledTrailingIconColor: Color = ...,
    errorTrailingIconColor: Color = ...,
    focusedLabelColor: Color = ...,
    unfocusedLabelColor: Color = ...,
    disabledLabelColor: Color = ...,
    errorLabelColor: Color = ...,
    focusedPlaceholderColor: Color = ...,
    unfocusedPlaceholderColor: Color = ...,
    disabledPlaceholderColor: Color = ...,
    errorPlaceholderColor: Color = ...,
    focusedSupportingTextColor: Color = ...,
    unfocusedSupportingTextColor: Color = ...,
    disabledSupportingTextColor: Color = ...,
    errorSupportingTextColor: Color = ...,
    focusedPrefixColor: Color = ...,
    unfocusedPrefixColor: Color = ...,
    disabledPrefixColor: Color = ...,
    errorPrefixColor: Color = ...,
    focusedSuffixColor: Color = ...,
    unfocusedSuffixColor: Color = ...,
    disabledSuffixColor: Color = ...,
    errorSuffixColor: Color = ...*/


        )
    )
}


