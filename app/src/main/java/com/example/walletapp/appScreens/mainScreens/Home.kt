package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.util.Pair
import com.example.walletapp.appScreens.actionItems
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape
import java.math.BigInteger

@Composable
fun Home(
    /*onSettingClick: () -> Unit,
    onShareClick: () -> Unit,*/
    onSignersClick: () -> Unit
) {
    val ps = PasswordStorageHelper(LocalContext.current)
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(paddingColumn)
    ) {
        val (gridRef, button, text) = createRefs()

        ActionGrid(actionItems = actionItems, onItemClick = { itemName ->
            when (itemName) {
                /*"Настройки" -> onSettingClick()
                "Поделиться публичным ключем" -> onShareClick()*/
                "Подписанты" -> onSignersClick()
                else -> Unit
            }
        }, modifier = Modifier.constrainAs(gridRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        })

        var outputText by remember { mutableStateOf("") }

        Button(onClick = {
            val key = ps.getData("MyPrivateKey")
            val n = BigInteger(key)
            outputText = n.toString(16)
        },
            modifier = Modifier.constrainAs(button){
                top.linkTo(gridRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }

        ) {
            Text(text = "Enter")
        }

        Text(
            text = outputText, // Использование сохранённого значения для отображения
            modifier = Modifier.constrainAs(text) { // Замените yourTextRef на ваше собственное определение ссылки для размещения текста
                top.linkTo(button.bottom, margin = 8.dp) // Размещение под кнопкой с небольшим отступом
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}


@Composable
fun ActionGrid(
    actionItems: MutableList<Pair<String, Int>>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val columns = 4

    val cellWidth = (screenWidth - (10.dp * (columns + 1))) / columns
    val cellHeight = cellWidth * (screenHeight / screenWidth)

    LazyVerticalGrid(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = roundedShape
            ),
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(paddingColumn),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false,

        ) {
        items(actionItems) { actionItem ->
            ActionCell(
                text = actionItem.first,
                imageVector = actionItem.second,
                onClick = { onItemClick(actionItem.first) },
                cellWidth = cellWidth,
                cellHeight = cellHeight
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCell(
    text: String,
    imageVector: Int,
    onClick: () -> Unit,
    cellWidth: Dp,
    cellHeight: Dp
){
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f)
            .padding(5.dp)
            .width(cellWidth)
            .height(cellHeight),
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
            Box(
                modifier = Modifier
                    .border(
                        1.dp,
                        shape = RoundedCornerShape(10.dp),
                        color = Color.LightGray
                    )
                    .padding(8.dp)
            ){
                Icon(
                    painter = painterResource(id = imageVector),
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
    }
}