package com.example.walletapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


// Здесь можно менять цвета для того, чтобы менять гредиент, почему именно градиент,
// по моему мнению так приложение перестанет быть скучным
// но это только мое мнение
// возможно старым пользователям это не нужно

val gradientColors = listOf(
    Color(0xFF6A85B6), // Фиолетовый
    Color(0xFFF67280), // Розовый
    Color(0xFFFFC6A9), // Оранжевый
    Color(0xFFFFE6E6)  // Белый для затухания к краям
)
// Задний фон (Градиентный, хехе) Меняемся в красоту а то красный мне уже глаза выжигает
// хоть Modifier и определен по другому
// но все же дополнительные настройки ему можно давать
// надеюсь сложности это не вызовет
val gradient = Modifier
    .fillMaxSize()
    .background(Brush.linearGradient(gradientColors))


// Новые цвета
// Если все же не выбирать градиент то тогда обычный задний фон
val Background = Color(0xFFFDFDFD)
val onBackground = Color(0xFF000000)

// Основной цвет
val Main = Color(0xFF4C44AA)
// Текст на основном тексте
val onMain = Color(0xFFFDFDFD)

// Выключенная кнопка, то етсть выключенный основной цвет
val disabledMain = Main.copy(alpha = 0.4f)
// Текст на этом выключенном основном цвете
val onDisabledMain = Color(0xFFFDFDFD)

// Все всплывающие окна снизу и необходимые карточки, но не диалоговые окна их не надо сюда приплетать
// Диалоговые окна должны быть синего цвета
val Surface = Color(0xFFFDFDFD)
// Текст на этом Surface
val onSurface = Color(0xFF0E194D)
//Текст который будет выделенным по сути он только один хы
val selectedText = Color(0xFF6A6DD8)

val inputText = Color(0xFFA6A6A6)

val secondButton = Color(0xFFFD6E74)














// Старые цвета
// Основной цвет
val PrimaryBase = Color(0xFFA31621)

// Светлая Тема
val PrimaryLight = PrimaryBase //+
val OnPrimaryLight = Color.White //+
val PrimaryContainerLight = PrimaryBase.copy(alpha = 0.4f) //+
val OnPrimaryContainerLight = Color.White //+
val SecondaryLight = PrimaryBase.copy(alpha = 0.6f)
val OnSecondaryLight = Color.White
val SecondaryContainerLight = PrimaryBase.copy(alpha = 0.4f)
val OnSecondaryContainerLight = Color.Black
val TertiaryLight = PrimaryBase.copy(alpha = 0.2f)
val OnTertiaryLight = Color.Black
val TertiaryContainerLight = PrimaryBase.copy(alpha = 0.1f)
val OnTertiaryContainerLight = Color.Black
val BackgroundLight = Color(0xFFF0EAE2)
val OnBackgroundLight = Color.Black
val SurfaceLight = Color(0xFFFFFBFB)
val OnSurfaceLight = Color.Black
val ErrorLight = Color(0xFFB00020)
val OnErrorLight = Color.White
val ErrorContainerLight = ErrorLight.copy(alpha = 0.8f)
val OnErrorContainerLight = Color.White
val OutlineLight = PrimaryBase.copy(alpha = 0.2f)
val ScrimLight = Color.Black.copy(alpha = 0.32f)
val InversePrimaryLight = Color.White
val SurfaceVariantLight = PrimaryBase.copy(alpha = 0.5f)
val OnSurfaceVariantLight = Color.Black
val SurfaceTintLight = PrimaryBase
val InverseSurfaceLight = Color.Black
val InverseOnSurfaceLight = Color.White
val OutlineVariantLight = PrimaryBase.copy(alpha = 0.1f)

// Темная Тема
val PrimaryDark = PrimaryBase
val OnPrimaryDark = Color.White
val PrimaryContainerDark = PrimaryBase.copy(alpha = 0.7f)
val OnPrimaryContainerDark = Color.White
val SecondaryDark = PrimaryBase.copy(alpha = 0.5f)
val OnSecondaryDark = Color.White
val SecondaryContainerDark = PrimaryBase.copy(alpha = 0.3f)
val OnSecondaryContainerDark = Color.White
val TertiaryDark = PrimaryBase.copy(alpha = 0.1f)
val OnTertiaryDark = Color.White
val TertiaryContainerDark = PrimaryBase.copy(alpha = 0.05f)
val OnTertiaryContainerDark = Color.White
val BackgroundDark = Color(0xFF121212)
val OnBackgroundDark = Color.White
val SurfaceDark = Color(0xFF121212)
val OnSurfaceDark = Color.White
val ErrorDark = Color(0xFFCF6679)
val OnErrorDark = Color.Black
val ErrorContainerDark = ErrorDark.copy(alpha = 0.8f)
val OnErrorContainerDark = Color.Black
val OutlineDark = PrimaryBase.copy(alpha = 0.2f)
val ScrimDark = Color.Black.copy(alpha = 0.32f)
val InversePrimaryDark = Color.Black
val SurfaceVariantDark = PrimaryBase.copy(alpha = 0.7f)
val OnSurfaceVariantDark = Color.White
val SurfaceTintDark = PrimaryBase
val InverseSurfaceDark = Color.White
val InverseOnSurfaceDark = Color.Black
val OutlineVariantDark = PrimaryBase.copy(alpha = 0.1f)








