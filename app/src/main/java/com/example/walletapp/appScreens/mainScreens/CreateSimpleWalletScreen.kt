package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.walletapp.R
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSimpleWalletScreen(
    viewModel: appViewModel,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val iam = GetMyAddr(context)
    val IamSigner = remember { mutableStateListOf(iam) }
    var walletNameText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.create_wallet_title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->

        ConstraintLayout(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .padding(padding)
                .fillMaxHeight()
        ) {
            val (warning, content, actions) = createRefs()

            Column(
                modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .background(color = MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NameStep(walletNameText) { walletNameText = it }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .constrainAs(warning) {
                        top.linkTo(content.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ){
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(id = R.string.explain_simple_user),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .constrainAs(actions) {
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                val showDialog = remember { mutableStateOf(false) }

                ElevatedButton(
                    onClick = {
                        showDialog.value = true
                    },
                    shape = newRoundedShape,
                    enabled = walletNameText.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text(text = stringResource(id = R.string.create_wallet), color = MaterialTheme.colorScheme.onPrimary)
                    Icon(
                        painter = painterResource(id = R.drawable.done),
                        contentDescription = "Create Wallet",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialog.value = false
                        },
                        title = {
                            Text(text = stringResource(id = R.string.ask_about_creating), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                        },
                        text = {
                            Text(stringResource(id = R.string.explain_ask_about_creating), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Light)
                        },
                        shape = newRoundedShape,
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.createNewWallet(
                                    context = context,
                                    signerKeys = IamSigner,
                                    requiredSigners = 1,
                                    selectedNetworkId = "5000",
                                    walletNameText = walletNameText,
                                    onComplete = onCreateClick
                                )
                                onCreateClick()
                            }) {
                                Text(stringResource(id = R.string.yes), color = MaterialTheme.colorScheme.onSurface)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDialog.value = false
                            }) {
                                Text(stringResource(id = R.string.no), color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    )
                }
            }
        }
    }
}