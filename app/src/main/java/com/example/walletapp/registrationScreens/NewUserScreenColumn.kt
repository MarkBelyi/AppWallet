package com.example.walletapp.registrationScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.Element.CheckboxWithLabel
import com.example.walletapp.Element.CustomButton
import com.example.walletapp.R
import com.example.walletapp.ui.theme.topRoundedShape
import kotlinx.coroutines.delay


@Composable
fun NewUserScreenColumn(onCreateClick: () -> Unit, onAddClick: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }
    val logoRes = if (isSystemInDarkTheme()) {
        R.drawable.safina_rgb_dark
    } else {
        R.drawable.safina_rgb
    }


    LaunchedEffect(Unit) {
        delay(500)
        isVisible = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        Spacer(modifier = Modifier.weight(0.3f))

        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(fraction = 0.5f)
                .scale(1.5f)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.weight(0.2f))

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight / 2 },
                animationSpec = spring()
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight / 2 },
                animationSpec = spring()
            )
        ) {
            AnimatedContent(
                termsAccepted = termsAccepted,
                onCreateClick = onCreateClick,
                onAddClick = onAddClick,
                onTermsAcceptedChange = { termsAccepted = it }
            )
        }
    }
}

@Composable
fun AnimatedContent(
    termsAccepted: Boolean,
    onCreateClick: () -> Unit,
    onAddClick: () -> Unit,
    onTermsAcceptedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.7f),
        color = MaterialTheme.colorScheme.surface,
        shape = topRoundedShape
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.2f))

            Text(
                text = stringResource(id = R.string.hello),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(0.15f))

            // Create Button
            CustomButton(
                textResource = R.string.create_button,
                enabled = termsAccepted,
                onClick = onCreateClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Button
            CustomButton(
                textResource = R.string.add_button,
                enabled = termsAccepted,
                onClick = onAddClick
            )

            Spacer(modifier = Modifier.weight(0.1f))

            CheckboxWithLabel(
                text = stringResource(id = R.string.terms_of_use),
                isChecked = termsAccepted,
                onCheckedChange = onTermsAcceptedChange,
                modifier = Modifier.fillMaxWidth(0.75f)
            )

            Spacer(modifier = Modifier.weight(0.4f))
        }
    }
}


