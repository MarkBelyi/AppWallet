package com.example.walletapp.AuxiliaryFunctions.Element

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.AuxiliaryFunctions.SealedClass.BottomBarTab

@Composable
fun AppBottomBar(
    bottomBarTabs: List<BottomBarTab>,
    currentRoute: String?,
    navigateToRoute: (String) -> Unit
) {
    NavigationBar(
        containerColor = colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        bottomBarTabs.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = stringResource(id = screen.label),
                        modifier = Modifier.scale(1.4f),
                        tint = if (isSelected) colorScheme.primary else colorScheme.onSurface
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = screen.label),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        ),
                        color = if (isSelected) colorScheme.primary else colorScheme.onSurface
                    )
                },
                selected = currentRoute == screen.route,
                alwaysShowLabel = true,
                onClick = {
                    if (currentRoute != screen.route) {
                        navigateToRoute(screen.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = colorScheme.primary,
                    indicatorColor = colorScheme.surface
                )
            )
        }
    }
}