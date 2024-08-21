package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AuxiliaryFunctions.ENUM.ElementType
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    type: ElementType,
    checkedState: MutableState<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        ),
        shape = newRoundedShape,
        onClick = {
            if (type == ElementType.CHECKBOX || type == ElementType.SWITCH) {
                val newCheckedState = !checkedState.value
                checkedState.value = newCheckedState
                onCheckedChange(newCheckedState)
            } else {
                onClick()
            }
        },
        border = BorderStroke(0.5.dp, colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {

                Text(
                    text = title,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )

                if (subtitle.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(4.dp))

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = subtitle,
                        color = colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                }
            }

            when (type) {
                ElementType.CHECKBOX ->
                    Checkbox(
                        checked = checkedState.value,
                        onCheckedChange = { newCheckedState ->
                            checkedState.value = newCheckedState
                            onCheckedChange(newCheckedState)
                        },
                        modifier = Modifier
                            .scale(1.4f)
                            .padding(start = 16.dp)
                    )

                ElementType.SWITCH ->
                    Switch(
                        checked = checkedState.value,
                        onCheckedChange = { newCheckedState ->
                            checkedState.value = newCheckedState
                            onCheckedChange(newCheckedState)
                        },
                        colors = SwitchDefaults.colors(
                            uncheckedBorderColor = colorScheme.primary,
                            uncheckedIconColor = colorScheme.primary,
                            uncheckedTrackColor = colorScheme.surface,
                            uncheckedThumbColor = colorScheme.primary
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )

                ElementType.RADIOBUTTON -> RadioButton(
                    selected = checkedState.value,
                    onClick = { onCheckedChange(!checkedState.value) }
                )

                ElementType.ARROW -> IconButton(
                    onClick = {
                        onClick()
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
    }
}
