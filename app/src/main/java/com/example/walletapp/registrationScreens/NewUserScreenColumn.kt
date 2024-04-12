package com.example.walletapp.registrationScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R
import com.example.walletapp.elements.checkbox.CheckboxWithLabel
import com.example.walletapp.ui.theme.gradient
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.delay

@Composable
fun NewUserScreenColumn(onCreateClick: () -> Unit, onAddClick: () -> Unit){
    //Настройки анимации
    // Состояние для управления видимостью
    var isVisible by remember { mutableStateOf(false) }
    // Задержка перед появлением
    LaunchedEffect(Unit) {
        delay(500) // Задержка 500 мс перед началом анимации
        isVisible = true
    }



    var termsAccepted by remember { mutableStateOf(false) }

    Column(

        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = gradient

    ) {

        Spacer(modifier = Modifier.weight(0.3f))


        // Это будет логотипом
        Box(

            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = colorScheme.surface, shape = CircleShape)
                .fillMaxWidth(fraction = 0.5f)
                .aspectRatio(1f)

        ) {

            // Logo
            // Потом сюда свое лого поставить нужно, у меня мало времени
            Image(
                painter = painterResource(id = R.drawable.newlogo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.5f)
                    .aspectRatio(1f)
            )

        }

        Spacer(modifier = Modifier.weight(0.2f))


        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                // Откуда начинается анимация
                initialOffsetY = { fullHeight -> fullHeight / 2 },
                // Более плавная анимация с помощью tween
                animationSpec = spring()

            ),
            exit = slideOutVertically(
                // Куда заканчивается анимация
                targetOffsetY = { fullHeight -> fullHeight / 2 },
                // Более плавная анимация с помощью tween
                animationSpec = spring()
            )
        ) {
            // Нижняя белая панель, самым оптимизированный способом
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.6f),
                color = colorScheme.surface,
                shape = topRoundedShape
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.weight(0.2f))

                    Text(
                        text = stringResource(id = R.string.hello),
                        color = colorScheme.onSurface,
                        maxLines = 1,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(0.15f))

                    // CreateButton
                    CustomButton(
                        textResource = R.string.create_button,
                        enabled = termsAccepted,
                        onClick = {
                            onCreateClick()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // AddButton
                    CustomButton(
                        textResource = R.string.add_button,
                        enabled = termsAccepted,
                        onClick = {
                            onAddClick()
                        }
                    )

                    Spacer(modifier = Modifier.weight(0.1f))

                    // Checkbox with label
                    CheckboxWithLabel(
                        text = stringResource(id = R.string.terms_of_use),
                        isChecked = termsAccepted,
                        onCheckedChange = { isChecked -> termsAccepted = isChecked },
                        modifier = Modifier
                            .fillMaxWidth(0.75f)

                    )

                    Spacer(modifier = Modifier.weight(0.4f))
                }
            }
        }
    }

}





@Composable
fun CustomButton(textResource: Int, onClick: () -> Unit, enabled: Boolean) {

    ElevatedButton(

        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .heightIn(min = 48.dp, max = 64.dp)
            .shadow(
                elevation = 16.dp,
                shape = newRoundedShape,
                clip = true
            ),
        shape = newRoundedShape,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            disabledContainerColor = colorScheme.primaryContainer,
            disabledContentColor = colorScheme.onPrimaryContainer
        )

    ) {

        Text(
            text = stringResource(textResource),
            fontWeight = FontWeight.Bold
        )

    }
}
