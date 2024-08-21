package com.example.walletapp.AuxiliaryFunctions.Element

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun AnnotatedButton(annotatedText: AnnotatedString, imagePainter: Painter) {
    val context = LocalContext.current
    val urlAnnotation =
        annotatedText.getStringAnnotations(tag = "URL", start = 0, end = annotatedText.length)
            .firstOrNull()?.item

    OutlinedButton(
        onClick = {
            urlAnnotation?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        },
        shape = newRoundedShape,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                androidx.compose.foundation.text.ClickableText(
                    style = TextStyle(
                        color = colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    text = annotatedText,
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(
                            tag = "URL",
                            start = offset,
                            end = offset
                        )
                            .firstOrNull()?.let { annotation ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                context.startActivity(intent)
                            }
                    }
                )
            }
            Icon(
                painter = imagePainter,
                contentDescription = null,
                tint = colorScheme.onSurface,
                modifier = Modifier
                    .size(15.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}