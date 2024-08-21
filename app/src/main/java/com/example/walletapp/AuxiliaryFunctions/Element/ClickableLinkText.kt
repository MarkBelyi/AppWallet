package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R

@Composable
fun ClickableLinkText(text: String, url: String, instruction: String) {
    val annotatedText = buildAnnotatedString {
        append(text)
        addStringAnnotation(
            tag = "URL",
            annotation = url,
            start = 0,
            end = text.length
        )
    }

    var expanded by remember { mutableStateOf(false) }

    val imagePainter = painterResource(id = R.drawable.arrow_forward)

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        AnnotatedButton(annotatedText = annotatedText, imagePainter = imagePainter)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = instruction,
            style = TextStyle(color = colorScheme.onSurface, fontSize = 16.sp),
            maxLines = if (expanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        if (instruction.length > 40) {
            Text(
                text = if (expanded) stringResource(id = R.string.show_less) else stringResource(id = R.string.read_more),
                fontWeight = FontWeight.Light,
                color = Color.LightGray,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 8.dp)
            )
        }
    }
}
