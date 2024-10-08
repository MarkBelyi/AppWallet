package com.example.walletapp.AuxiliaryFunctions.Element

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R

@Composable
fun ShareMnemonicPhrase(
    randomSeedPhrases: List<String>,
    onShared: () -> Unit,
    launcher: ActivityResultLauncher<Intent>,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                val phraseText = randomSeedPhrases.joinToString(separator = " ")
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, phraseText)
                    type = "text/plain"
                }
                launcher.launch(Intent.createChooser(sendIntent, null))
                onShared()
            }
    ) {

        Text(
            text = stringResource(id = R.string.send_mnem),
            fontWeight = FontWeight.Light,
            style = TextStyle(fontSize = 16.sp, color = colorScheme.onSurface)
        )

        Spacer(modifier = Modifier.padding(horizontal = 5.dp))

        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = "Share",
            tint = colorScheme.primary
        )

    }
}