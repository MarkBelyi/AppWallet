package com.example.walletapp.registrationScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.walletapp.R
import com.example.walletapp.elements.checkbox.CheckboxWithLabel
import com.example.walletapp.ui.theme.paddingColumn
import com.example.walletapp.ui.theme.roundedShape

//Этот скрин дается пользователю когда он впервые заходит в это приложение либо переустанвливает его
//(Но думаю в будущем можно будет сделать функцию переходящую сюда)
@Composable
fun NewUserPage() {
    var termsAccepted by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .padding(paddingColumn)
    ) {

        // Создаем гайдлайны
        val topMarginGuideline = createGuidelineFromTop(0.15f) // Например, заменяем 16.dp на 15% от верха
        val bottomMarginGuideline = createGuidelineFromBottom(0.10f) // Например, заменяем 50.dp на 10% от низа
        val startMarginGuideline = createGuidelineFromStart(0.10f) // Например, заменяем start padding на 10% от начала
        val endMarginGuideline = createGuidelineFromEnd(0.10f) // Например, заменяем end padding на 10% от конца

        // Гайдлайны для отступов между элементами
        val buttonSpacingGuideline = createGuidelineFromBottom(0.25f) // Отступ 0.10f между кнопками
        val checkboxSpacingGuideline = createGuidelineFromBottom(0.40f) // Отступ 0.15f между последней кнопкой и чекбоксом

        val (createButton, addButton, checkboxWithLabel, logoImage) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(fraction = 0.6f)
                .aspectRatio(1f)
                .constrainAs(logoImage) {
                    top.linkTo(topMarginGuideline)
                    start.linkTo(startMarginGuideline)
                    end.linkTo(endMarginGuideline)
                }
        )

        // Function to create a button
        @Composable
        fun createButton(ref: ConstrainedLayoutReference, textResource: Int, enabled: Boolean, linkToBottom: ConstrainedLayoutReference) {
            ElevatedButton(
                onClick = { /* обработчик нажатия */ },

                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .heightIn(min = 45.dp, max = 80.dp)
                    .constrainAs(ref) {
                        start.linkTo(startMarginGuideline)
                        end.linkTo(endMarginGuideline)
                        bottom.linkTo(linkToBottom.top)
                    },
                enabled = enabled,
                shape = roundedShape,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    disabledContainerColor = colorScheme.primaryContainer,
                    disabledContentColor = colorScheme.onPrimaryContainer
                )
            ) {
                Text(text = stringResource(textResource))
            }
        }

        // Checkbox with label
        CheckboxWithLabel(
            text = stringResource(id = R.string.terms_of_use),
            isChecked = termsAccepted,
            onCheckedChange = { isChecked -> termsAccepted = isChecked },
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .constrainAs(checkboxWithLabel) {
                    start.linkTo(startMarginGuideline)
                    end.linkTo(endMarginGuideline)
                    bottom.linkTo(bottomMarginGuideline)
                }
        )

        // AddButton
        createButton(
            ref = addButton,
            textResource = R.string.add_button,
            enabled = termsAccepted,
            linkToBottom = checkboxWithLabel
        )

        // CreateButton
        createButton(
            ref = createButton,
            textResource = R.string.create_button,
            enabled = termsAccepted,
            linkToBottom = addButton,

        )
    }
}

