package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.roundedShape
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarTX(
    searchText: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onTokenSelected: (String) -> Unit,
    onCompletedOnlyChanged: (Boolean) -> Unit,
) {
    val allTokensString = stringResource(id = R.string.all_tokens)
    var selectedToken by remember { mutableStateOf(allTokensString) }
    var completedOnly by remember { mutableStateOf(false) }
    var openTokenBottomSheet by remember { mutableStateOf(false) }
    val tokenBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    //TODO(Добавить рабочий фильтр для USDT)
    val tokens = listOf(
        allTokensString, "BTC", "ETH", "TRX"
    )

    if (openTokenBottomSheet) {
        ModalBottomSheet(
            shape = topRoundedShape,
            containerColor = colorScheme.surface,
            tonalElevation = 0.dp,
            sheetState = tokenBottomSheetState,
            onDismissRequest = { openTokenBottomSheet = false },
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.choose_token),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    HorizontalDivider(thickness = 0.5.dp, color = colorScheme.primary)
                }

                items(tokens) { token ->
                    Text(
                        text = token,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        color = colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedToken = token
                                onTokenSelected(token)
                                coroutineScope.launch {
                                    tokenBottomSheetState.hide()
                                    openTokenBottomSheet = false
                                }
                            }
                            .padding(vertical = 12.dp)
                    )

                    HorizontalDivider(thickness = 0.5.dp, color = colorScheme.scrim)
                }
            }
        }
    }

    Column(
        modifier = Modifier.background(color = colorScheme.surface)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = colorScheme.onSurface),
            singleLine = true,
            maxLines = 1,
            shape = newRoundedShape,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "search",
                    tint = colorScheme.primary
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surface,
                focusedLabelColor = colorScheme.primary,
                unfocusedContainerColor = colorScheme.surface,
                unfocusedLabelColor = colorScheme.onBackground,
                cursorColor = colorScheme.primary
            )
        )

        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            item {
                Card(
                    onClick = {
                        openTokenBottomSheet = true
                        coroutineScope.launch {
                            tokenBottomSheetState.show()
                        }
                    },
                    shape = roundedShape,
                    border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .padding(end = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = selectedToken,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Light,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.onSurface,
                        )
                    }
                }
            }

            item {
                Card(
                    onClick = {
                        completedOnly = !completedOnly
                        onCompletedOnlyChanged(completedOnly)
                    },
                    shape = roundedShape,
                    border = BorderStroke(width = 0.5.dp, color = colorScheme.primary),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .padding(end = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.completed),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Light,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = colorScheme.onSurface,
                            modifier = Modifier
                                .padding(start = 8.dp)
                        )
                        Checkbox(
                            checked = completedOnly,
                            onCheckedChange = {
                                completedOnly = it
                                onCompletedOnlyChanged(it)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = colorScheme.surface,
                                uncheckedColor = colorScheme.primaryContainer,
                                checkmarkColor = colorScheme.primary
                            ),
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                }
            }
        }
    }
}