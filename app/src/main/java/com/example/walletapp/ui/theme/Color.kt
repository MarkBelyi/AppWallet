package com.example.walletapp.ui.theme

import androidx.compose.ui.graphics.Color

// Все всплывающие окна снизу и необходимые карточки, но не диалоговые окна, их не надо сюда приплетать
//Задний фон
val Background = Color(0xFFF1F1F1)

//То что на заднем фоне
val onBackground = Color(0xFFFFFFFF)

//Цвет фона
val Surface = Color(0xFFFDFDFD)
val onSurface = Color(0xFF16283E)
val Main = Color(0xFFBB2649)//цвет safina (ой б**)
val onMain = Color(0xFFFFFFFF)

// Выключенная кнопка, то етсть выключенный основной цвет
val disabledMain = Main.copy(alpha = 0.4f)

// Текст на этом выключенном основном цвете
val onDisabledMain = Color(0xFFFFFFFF)

//Текст который будет выделенным по сути он только один хы
val selectedText = Color(0xFFDF3B62)
val inputText = Color(0xFFA6A6A6)
val secondButton = Color(0xFFFD6E74)
val iconButton = Color(0XFFEC4C7D)
val inverseSurface = Color.LightGray.copy(alpha = 0.1f)

// Основной фон
val DarkBackground = Color(0xFF1E1E1E)
val DarkOnBackground = Color(0xFFFDFDFD)

// Основной цвет
val DarkMain = Color(0xFF9B2C3F)

// Текст на основном тексте
val DarkOnMain = Color(0xFFFDFDFD)

// Выключенная кнопка, то есть выключенный основной цвет
val DarkDisabledMain = DarkMain.copy(alpha = 0.4f)

// Текст на этом выключенном основном цвете
val DarkOnDisabledMain = Color(0xFFFDFDFD)

// Все всплывающие окна снизу и необходимые карточки
val DarkSurface = Color(0xFF121212)

// Текст на этом Surface
val DarkOnSurface = Color(0xFFE0E0E0)

// Текст который будет выделенным
val DarkSelectedText = Color(0xFF7C1F32)

// Цвет текста в полях ввода
val DarkInputText = Color(0xFFCCCCCC)

// Второстепенная кнопка
val DarkSecondButton = Color(0xFF4C44AA)

// Кнопка с иконкой
val DarkIconButton = Color(0xFF6854A0)


