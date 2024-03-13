package com.example.walletapp.appScreens.mainScreens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.util.Pair
import com.example.walletapp.appScreens.Actions
import com.example.walletapp.appScreens.actionItems
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(
    /*onSettingClick: () -> Unit,*/
    onQRClick: () -> Unit,
    onShareClick: () -> Unit,
    onSignersClick: () -> Unit,
    onCreateWalletClick: () -> Unit,
    onMatrixClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(paddingColumn)
    ) {
        val (gridRef) = createRefs()

        ActionGrid(actionItems = actionItems, onItemClick = { itemName ->
            when (itemName) {
                /*"Настройки" -> onSettingClick()*/
                Actions.QR -> onQRClick()
                Actions.shareMyAddr -> onShareClick()
                Actions.signers -> onSignersClick()
                Actions.createWallet -> onCreateWalletClick()
                else -> onMatrixClick()
            }
        }, modifier = Modifier.constrainAs(gridRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        })
    }
}


@Composable
fun ActionGrid(
    actionItems: List<Triple<String, Int, Actions>>,
    onItemClick: (Actions) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = 4

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.background(color = Color.White, shape = roundedShape),
    ) {
        items(actionItems) { actionItem ->
            ActionCell(
                text = actionItem.first,
                imageVector = actionItem.second,
                onClick = { onItemClick(actionItem.third) },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCell(
    text: String,
    imageVector: Int,
    onClick: () -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var textSize by remember { mutableStateOf(IntSize.Zero) }

    // Увеличиваем плавность, настраивая длительность анимации
    val animationDurationMs = 3000 // Длительность анимации в миллисекундах

    LaunchedEffect(key1 = text) {
        coroutineScope.launch {
            while (true) {
                delay(1000) // Начальная задержка перед прокруткой
                // Вычисляем длину прокрутки на основе максимального значения
                if (scrollState.maxValue > 0) {
                    scrollState.animateScrollTo(
                        value = scrollState.maxValue,
                        animationSpec = tween(durationMillis = animationDurationMs)
                    )
                    delay(animationDurationMs.toLong()) // Задержка, чтобы дать время на просмотр
                    scrollState.animateScrollTo(
                        value = 0,
                        animationSpec = tween(durationMillis = animationDurationMs)
                    )
                }
            }
        }
    }



    val interactionSource = remember { MutableInteractionSource() }
    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f),
            //.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        interactionSource = interactionSource
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            Box(modifier = Modifier.padding(8.dp)){
                Icon(
                    painter = painterResource(id = imageVector),
                    contentDescription = text,
                    modifier = Modifier.scale(1.2f),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.horizontalScroll(scrollState).onGloballyPositioned { coordinates ->
                textSize = coordinates.size
            }) {
                Text(
                    text = text,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}