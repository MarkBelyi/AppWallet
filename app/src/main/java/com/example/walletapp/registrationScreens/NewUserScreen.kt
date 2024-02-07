package com.example.walletapp.registrationScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
fun NewUserScreen() {
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

        val (createButton, addButton, checkboxWithLabel, logoImage) = createRefs()



        // Function to create a button
        @Composable
        fun CustomButton(ref: ConstrainedLayoutReference, textResource: Int, enabled: Boolean, linkToBottom: ConstrainedLayoutReference, bottomMargin: Dp = 0.dp) {
            ElevatedButton(
                onClick = {
                    /* обработчик нажатия */
                },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .heightIn(min = 40.dp, max = 64.dp)
                    .constrainAs(ref) {
                        start.linkTo(startMarginGuideline)
                        end.linkTo(endMarginGuideline)
                        bottom.linkTo(linkToBottom.top, margin = bottomMargin)
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

        //Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(fraction = 0.6f)
                .heightIn(min = 75.dp, max = 202.dp)
                .widthIn(min = 108.dp, max = 232.dp)
                .aspectRatio(1f)
                .constrainAs(logoImage) {
                    top.linkTo(topMarginGuideline)
                    start.linkTo(startMarginGuideline)
                    end.linkTo(endMarginGuideline)
                }
        )

        // AddButton
        CustomButton(
            ref = addButton,
            textResource = R.string.add_button,
            enabled = termsAccepted,
            linkToBottom = checkboxWithLabel,
            bottomMargin = 32.dp
        )

        // CreateButton
        CustomButton(
            ref = createButton,
            textResource = R.string.create_button,
            enabled = termsAccepted,
            linkToBottom = addButton,
            bottomMargin = 8.dp
        )

        // Checkbox with label
        CheckboxWithLabel(
            text = stringResource(id = R.string.terms_of_use),
            isChecked = termsAccepted,
            onCheckedChange = { isChecked -> termsAccepted = isChecked },
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .constrainAs(checkboxWithLabel) {
                    //top.linkTo(bottomMarginGuideline)
                    start.linkTo(startMarginGuideline)
                    end.linkTo(endMarginGuideline)
                    bottom.linkTo(bottomMarginGuideline)
                }
        )
    }
}