package com.example.walletapp.elements.checkbox

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun CheckboxWithLabel(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .then(modifier)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .scale(1.5f),
            colors = CheckboxDefaults.colors(
                checkedColor = colorScheme.surface,
                checkmarkColor = colorScheme.primary,
                uncheckedColor = colorScheme.primary
            ),
        )
        ClickableTextWithLink(
            text = text,
            link = ""//нужно поставить сюда ссылочку на нужный документ
        )
    }
}

@Composable
fun ClickableTextWithLink(
    text: String,
    link: String
) {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        append(text.substring(0, 18))  // Добавление текста до 18-го символа без стиля

        withStyle(style = SpanStyle(color = colorScheme.onSurfaceVariant)) { // Применение синего цвета начиная с 18-го символа
            append(text.substring(18))
        }

        addStringAnnotation(
            tag = "URL",
            annotation = link,
            start = 18,
            end = text.length
        )
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(
            color = colorScheme.onSurface,
            fontWeight = FontWeight.Light
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations("URL", offset, offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
        }
    )
}


