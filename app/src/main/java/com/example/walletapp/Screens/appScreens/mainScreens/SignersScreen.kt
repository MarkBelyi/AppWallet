package com.example.walletapp.Screens.appScreens.mainScreens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.Element.AddSignerCard
import com.example.walletapp.AuxiliaryFunctions.Element.SignerItem
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignersScreen(
    viewModel: appViewModel,
    onCurrentSignerClick: (String) -> Unit,
    onAddSignerClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val signers by viewModel.allSigners.observeAsState(initial = emptyList())
    val sortedSigners =
        signers.sortedWith(compareByDescending<Signer> { it.isFavorite }.thenBy { it.name })
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val signersExportFilePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val outputStream = context.contentResolver.openOutputStream(uri)!!
                val signers = viewModel.allSigners.value ?: emptyList()
                val json = Gson().toJson(signers)

                outputStream.write(json.toByteArray())
                outputStream.flush()
                outputStream.close()
                Toast.makeText(
                    context,
                    R.string.address_book_exported,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun showExportSignersDialog() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, "signers_backup.asfn")
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json"))
        }
        signersExportFilePicker.launch(intent)
    }

    val signersImportFilePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)!!
                val bytes = inputStream.readBytes()
                inputStream.close()

                val json = String(bytes, StandardCharsets.UTF_8)
                val type = object : TypeToken<List<Signer>>() {}.type
                val signers: List<Signer> = Gson().fromJson(json, type)

                signers.forEach { signer ->
                    viewModel.insertSigner(signer)
                }

                Toast.makeText(
                    context,
                    R.string.address_book_imported,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun showImportSignersDialog() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        signersImportFilePicker.launch(intent)
    }



    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.action_signers),
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
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = colorScheme.onSurface
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(color = colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                showExportSignersDialog()
                            },
                            text = {
                                Text(
                                    stringResource(id = R.string.export_address_book),
                                    fontWeight = FontWeight.Light
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.Share, contentDescription = "Export")
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = colorScheme.onSurface,
                                leadingIconColor = colorScheme.onSurface
                            )
                        )
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                showImportSignersDialog()
                            },
                            text = {
                                Text(
                                    stringResource(id = R.string.import_address_book),
                                    fontWeight = FontWeight.Light
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(id = R.drawable.receive),
                                    contentDescription = "Import"
                                )
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = colorScheme.onSurface,
                                leadingIconColor = colorScheme.onSurface
                            )
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                AddSignerCard(
                    onClick = { onAddSignerClick() }
                )
            }
            items(sortedSigners) { signer ->
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







