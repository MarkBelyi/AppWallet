package com.example.walletapp.appScreens.mainScreens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    onBackClick: () -> Unit
) {

    val linkItems = listOf(
        LinkItem(
            stringResource(id = R.string.service),
            "https://wallet.tg/",
            stringResource(id = R.string.explain_service)
        ),
        LinkItem(
            stringResource(id = R.string.bot),
            "https://t.me/wallet",
            stringResource(id = R.string.explain_bot)
        ),
        LinkItem(
            stringResource(id = R.string.video_instruction),
            "https://youtu.be/dQw4w9WgXcQ?si=o5l2SJkfWk76o6f3",
            stringResource(id = R.string.explain_video_instruction)
        ),
        LinkItem(
            stringResource(id = R.string.text_instruction),
            "https://wiki.h2k.me/doku.php?id=ru:lite:instr:4",
            stringResource(id = R.string.explain_text_instruction)
        )
    )

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.buy_crypto),
                        color = colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface,
                    scrolledContainerColor = colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(linkItems) { item ->
                ClickableLinkText(text = item.text, url = item.url, instruction = item.instruction)
            }
        }
    }
}

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


@Composable
fun AnnotatedButton(annotatedText: AnnotatedString, imagePainter: Painter) {
    val context = LocalContext.current
    val urlAnnotation = annotatedText.getStringAnnotations(tag = "URL", start = 0, end = annotatedText.length)
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
                ClickableText(
                    style = TextStyle(color = colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    text = annotatedText,
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
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

data class LinkItem(
    val text: String,
    val url: String,
    val instruction: String
)