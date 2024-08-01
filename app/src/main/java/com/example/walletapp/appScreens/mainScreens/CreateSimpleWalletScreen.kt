package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.R
import com.example.walletapp.Server.GetMyAddr
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateSimpleWalletScreen(
    viewModel: appViewModel,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit
) {

    val context = LocalContext.current
    val iam = GetMyAddr(context)
    val coroutineScope = rememberCoroutineScope()
    val signerKeys = remember { mutableStateListOf("") }
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

            val (content, actions) = createRefs()

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
                        .height(48.dp)
                        .padding(start = 12.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.done),
                        contentDescription = "Create Wallet",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(text = "Create", color = MaterialTheme.colorScheme.onPrimary)
                }

                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialog.value = false
                        },
                        title = {
                            Text(text = "Вы готовы создать кошелек?")
                        },
                        text = {
                            Text("Далее вы не сможете изменить данные, касающиеся вашего кошелька. Если вы не уверены в введенной информации, пожалуйста, перепроверьте ее.")
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
                                Text("Да", color = MaterialTheme.colorScheme.onSurface)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDialog.value = false
                            }) {
                                Text("Нет", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    )
                }
            }
        }
    }
}