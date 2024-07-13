package com.example.walletapp.appScreens.mainScreens


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    onBackClick: () -> Unit) {


    val locale = Locale.getDefault().language
    val folderName = if (locale == "ru") "ru" else "en"
    val linkItems = listOf(
        LinkItem("Cервис Wallet", "https://wallet.tg/", "Посетите сервис Wallet для безопасного управления и хранения вашей криптовалюты. Убедитесь, что ваши учетные данные готовы для входа."),
        LinkItem("Перейти на телеграм-бот", "https://t.me/wallet", "Откройте Telegram-бота, чтобы начать управлять своим кошельком напрямую через Telegram. Следуйте инструкциям бота по настройке и доступу к своей учетной записи."),
        LinkItem("Видео инструкция", "https://youtu.be/dQw4w9WgXcQ?si=o5l2SJkfWk76o6f3", "Посмотрите это видеоруководство для пошагового руководства по использованию сервиса Wallet. Обязательно внимательно следуйте каждому шагу для гладкого опыта."),
        LinkItem("Текстовая инструкция", "https://wiki.h2k.me/doku.php?id=ru:lite:instr:4", "Прочитайте текстовые инструкции для получения подробного руководства по настройке и использованию сервиса Wallet. Обратитесь к этому документу, если вам нужны разъяснения по каким-либо шагам.")
    )

    Scaffold(
        containerColor = colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Купить",
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
                        Icon(Icons.Rounded.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxHeight()
                .padding(padding)
                .padding(start = 5.dp, end = 5.dp)
        ) {
            Text(
                "Купить криптовалюту", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                items(linkItems) { item ->
                    ClickableLinkText(text = item.text, url = item.url, instruction = item.instruction)
                }
            }
        }
    }
}
@Composable
fun ClickableLinkText(text: String, url: String, instruction: String) {
    val context = LocalContext.current
    val annotatedText = buildAnnotatedString {
        val str = text
        append(str)
        addStringAnnotation(
            tag = "URL",
            annotation = url,
            start = 0,
            end = str.length
        )
    }

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .background(color = colorScheme.primary, shape = RoundedCornerShape(10.dp))
                .padding(10.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ClickableText(
                        style = TextStyle(color = colorScheme.surface, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        text = annotatedText,
                        modifier = Modifier.align(Alignment.Center),
                        onClick = {
                            annotatedText.getStringAnnotations(tag = "URL", start = it, end = it)
                                .firstOrNull()?.let { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.item))
                                    context.startActivity(intent)
                                }
                        }
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.arrow_forward),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(15.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = instruction,
            style = TextStyle(color = colorScheme.onSurface, fontSize = 16.sp),
            maxLines = if (expanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (instruction.length > 40) {
            Text(
                text = if (expanded) "Show less" else "Read more",
                color = Color.Gray,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}

data class LinkItem(
    val text: String,
    val url: String,
    val instruction: String)

