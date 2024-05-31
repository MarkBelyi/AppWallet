package com.example.walletapp.ui.theme

import androidx.compose.ui.graphics.Color

// Все всплывающие окна снизу и необходимые карточки, но не диалоговые окна, их не надо сюда приплетать
// Новые цвета
// Если все же не выбирать градиент то тогда обычный задний фон
//val Background = Color(0xFFFDFDFD)
val Background = Color(0xFF16283E)
val onBackground = Color(0xFFFFFFFF)


val Surface = Color(0xFFFDFDFD) //цвет safina (ой б**)
// Диалоговые окна должны быть синего цвета
//val Surface = Color(0xFF16283e)
//val onSurface = Color(0xFF0E194D)
val onSurface = Color(0xFF16283E)
// Текст на этом Surface
//val onSurface = Color(0xFFFFFFFF)

// Основной цвет
//val Main = Color(0xFF4C44AA)
//val Main = Color(0xFF16283E)
val Main = Color(0xFFBB2649)
// Текст на основном тексте
val onMain = Color(0xFFFFFFFF)
// Выключенная кнопка, то етсть выключенный основной цвет
val disabledMain = Main.copy(alpha = 0.4f)
// Текст на этом выключенном основном цвете
val onDisabledMain = Color(0xFFFFFFFF)

//Текст который будет выделенным по сути он только один хы
//val selectedText = Color(0xFF6A6DD8)
val selectedText = Color(0xFFDF3B62)

val inputText = Color(0xFFA6A6A6)

val secondButton = Color(0xFFFD6E74)

val iconButton = Color(0XFFEC4C7D)

// Основной фон
val DarkBackground = Color(0xFF313234)
val DarkOnBackground = Color(0xFFFDFDFD)
// Основной цвет
val DarkMain = Color(0xFFFD6E74)
// Текст на основном тексте
val DarkOnMain = Color(0xFFFDFDFD)

// Выключенная кнопка, то есть выключенный основной цвет
val DarkDisabledMain = DarkMain.copy(alpha = 0.4f)
// Текст на этом выключенном основном цвете
val DarkOnDisabledMain = Color(0xFFFDFDFD)

// Все всплывающие окна снизу и необходимые карточки
val DarkSurface = Color(0xFF313234)
// Текст на этом Surface
val DarkOnSurface = Color(0xFFE0E0E0)
// Текст который будет выделенным
val DarkSelectedText = Color(0xFFEC4C7D)

// Цвет текста в полях ввода
val DarkInputText = Color(0xFFCCCCCC)

// Второстепенная кнопка
val DarkSecondButton = Color(0xFF4C44AA)

// Кнопка с иконкой
val DarkIconButton = Color(0xFF6854A0)


