package com.example.walletapp.Screens.registrationScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.example.walletapp.AppViewModel.appViewModel
import com.example.walletapp.AuxiliaryFunctions.Element.Write
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.paddingColumn

@Composable
fun WriteSeedPhraseScreen(navHostController: NavHostController, viewModel: appViewModel) {
    val isContinueEnabled = remember { mutableStateOf(false) }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .padding(paddingColumn)
    ) {
        val (textHeader, writeComponent, instructionText, continueButton) = createRefs()

        @Composable
        fun CustomButton(
            text: String,
            enabled: Boolean,
            onClick: () -> Unit
        ) {
            ElevatedButton(
                onClick = onClick,
                modifier = Modifier
                    .constrainAs(continueButton) {
                        bottom.linkTo(parent.bottom, margin = 32.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .heightIn(min = 56.dp, max = 64.dp)
                    .padding(top = 5.dp, bottom = 5.dp),
                enabled = enabled,
                shape = newRoundedShape,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    disabledContainerColor = colorScheme.primaryContainer,
                    disabledContentColor = colorScheme.onPrimaryContainer
                )
            ) {
                Text(text = text)
            }
        }

        Text(
            text = stringResource(id = R.string.enter_mnem),
            style = TextStyle(
                fontSize = typography.titleLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(textHeader) {
                    top.linkTo(parent.top, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        Write(
            isContinueEnabled = isContinueEnabled,
            modifier = Modifier.constrainAs(writeComponent) {
                top.linkTo(textHeader.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            viewModel = viewModel
        )

        Text(
            text = stringResource(id = R.string.seed_phrase_paste),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = colorScheme.onSurface
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(instructionText) {
                    top.linkTo(writeComponent.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        CustomButton(
            text = stringResource(id = R.string.button_continue),
            onClick = { navHostController.navigate("App") },
            enabled = isContinueEnabled.value,
        )
    }
}