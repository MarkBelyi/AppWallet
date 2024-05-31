package com.example.walletapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    // background параметр определять здесь бессмысленно так как нельзя вставить градиент
    // оставьте его пустым пока такая возможность не появится

    background = DarkBackground, // это не задний фон
    onBackground = onBackground,

    primary = DarkMain,
    onPrimary = DarkOnMain,
    primaryContainer = DarkDisabledMain,
    onPrimaryContainer = DarkOnDisabledMain,

    //Второй главный цвет
    secondary = DarkSecondButton,

    //Третий главный цвет
    tertiary = DarkIconButton,

    surface = DarkSurface, // вот задний фон
    onSurface = DarkOnSurface,

    inverseSurface = Color.LightGray.copy(alpha = 0.1f),

    //Это когда на текст можно нажать
    onSurfaceVariant = DarkSelectedText,

    //используется если текст в поле нужно ввести
    scrim = DarkInputText

)

private val LightColorScheme = lightColorScheme(
    // background параметр определять здесь бессмысленно так как нельзя вставить градиент
    // оставьте его пустым пока такая возможность не появится

    background = Background, // это не задний фон
    onBackground = onBackground,

    primary = Main,
    onPrimary = onMain,
    primaryContainer = disabledMain,
    onPrimaryContainer = onDisabledMain,

    //Второй главный цвет
    secondary = secondButton,

    //Третий главный цвет
    tertiary = iconButton,

    surface = Surface, // вот задний фон
    onSurface = onSurface,

    //Цвет на заднем фоне экрана
    inverseSurface = Color.LightGray.copy(alpha = 0.1f),

    //Это когда на текст можно нажать
    onSurfaceVariant = selectedText,

    //используется если текст в поле нужно ввести
    scrim = inputText

)


@Composable
fun WalletAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {

            val window = (view.context as Activity).window
            // Устанавливаем флаги для прозрачности статус-бара и навигационного бара
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme

        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )

}