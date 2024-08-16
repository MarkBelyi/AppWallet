package com.example.walletapp.appScreens.mainScreens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
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
                    "Адресная книга экспортирована",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    fun showExportSignersDialog() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, "signers_backup.json")
            setType("*/*") // Необходимый тип, чтобы избежать сбоев
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
                    "Адресная книга импортирована",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun showImportSignersDialog() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("*/*")
        }
        signersImportFilePicker.launch(intent)
    }




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
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = colorScheme.onSurface)
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
                            text = { Text("Экспорт адресной книги", fontWeight = FontWeight.Light) },
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
                            text = { Text("Импорт адресной книги", fontWeight = FontWeight.Light) },
                            leadingIcon = {
                                Icon(painterResource(id = R.drawable.receive), contentDescription = "Import")
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
                    onClick = {
                        onAddSignerClick()
                    }
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


@Composable
fun SignerItem(signer: Signer, viewModel: appViewModel, onClick: (String) -> Unit) {


    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            containerColor = colorScheme.surface,
            tonalElevation = 0.dp,
            onDismissRequest = { showDialog = false },
            title = {
                Text("Подтверждение удаления",
                fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    fontSize = 18.sp
            ) },
            text = { Text("Вы уверены, что хотите удалить этого подписанта?",
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface
            ) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSigner(signer)
                    showDialog = false
                }) {
                    Text("Удалить",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                        )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            },
            shape = newRoundedShape
        )
    }
    Card(
        onClick = { onClick(signer.address) },
        shape = newRoundedShape,
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
                modifier = Modifier.padding(16.dp)
            ) {
                IconButton(
                    onClick = {
                        val updatedSigner = signer.copy(isFavorite = !signer.isFavorite)
                        viewModel.updateSigner(updatedSigner)
                    },
                    modifier = Modifier
                        .scale(1.2f)
                        .alpha(0.9f),
                ) {
                    Icon(
                        imageVector = if (signer.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (signer.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = colorScheme.primary
                    )
                }
            }


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
            ) {
                IconButton(
                    onClick = { showDialog = true },
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
fun AddSignerCard(onClick: () -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize(), // This makes the Box fill the entire screen
        contentAlignment = Alignment.BottomEnd // This aligns the content to the bottom-end corner
    ) {
        Card(
            modifier = Modifier
                .height(48.dp), // Высота карточки
            shape = newRoundedShape,
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
            onClick = onClick
        ) {
            Box(
                contentAlignment = Alignment.Center, // Центрирование содержимого
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // Заполняет весь размер карточки
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





