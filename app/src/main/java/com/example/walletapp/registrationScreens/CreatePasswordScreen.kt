package com.example.walletapp.registrationScreens

import android.graphics.Paint.Align
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.walletapp.R
import com.example.walletapp.ui.theme.paddingColumn

@Composable
fun CreatePasswordScreen(){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.background)
            .padding(paddingColumn)
    ) {

        val (title, pagination, passwordField, repeatPasswordField, continueButton) = createRefs()
        val guideline = createGuidelineFromTop(0.1f)

        /*Text(
            text = stringResource(id = R.string.create_password_screen_title),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = colorScheme.onBackground,
            maxLines = 1,
            textAlign = TextAlign.Center
        )*/



    }
}