package com.example.walletapp.ui.theme

import androidx.compose.ui.graphics.Color

//Пример записи цветов
//val Purple80 = Color(0xFFD0BCFF)

//Старые цвета

/*val Primary = Color(0xFFB24D4E)
val OnPrimary = Color.White
val PrimaryContainerDark = Color(0xFF805356)
val OnPrimaryContainerDark = Color(0xFFFFDAD4) // Kept the same for contrast
val InversePrimary = Color(0xFFfedbd0) // Kept the same for contrast
val Secondary = Color(0xFFB89C97)
val OnSecondary = Color.Black
val SecondaryContainerDark = Color(0xFF8F756F)
val OnSecondaryContainerDark = Color(0xFFD7C1B8) // Kept the same for contrast
val TertiaryDark = Color(0xFFB0A19A)
val OnTertiaryDark = Color.Black
val TertiaryContainerDark = Color(0xFF857370)
val OnTertiaryContainerDark = Color(0xFFCBB8A2) // Kept the same for contrast
val BackgroundDark = Color(0xFF353535)
val OnBackgroundDark = Color(0xFFE0E0E0)
val SurfaceDark = Color(0xFF4A4A4A)
val OnSurfaceDark = Color(0xFFE0E0E0)
val SurfaceVariantDark = Color(0xFF6C6767)
val OnSurfaceVariantDark = Color(0xFFC4C4C4)
val SurfaceTintDark = Color(0xFFA31621) // Kept the original primary color
val InverseSurfaceDark = Color.White
val InverseOnSurfaceDark = Color(0xFF2C2C2C)
val ErrorDark = Color(0xFFD6858B)
val OnErrorDark = Color.Black
val ErrorContainerDark = Color(0xFFA37075)
val OnErrorContainerDark = Color(0xFFFCD5D7) // Kept the same for contrast
val OutlineDark = Color(0xFFA1A1A1)
val OutlineVariantDark = Color(0xFFBDBDBD)
val ScrimDark = Color(0x80000000) // Kept the same for overlay

val PrimaryContainerLight = Color(0xFFF8D1D3)
val OnPrimaryContainerLight = Color(0xFF410E0B) // Kept the same for contrast
val SecondaryLight = Color(0xFFD1A8A3)
val OnSecondaryLight = Color.White
val SecondaryContainerLight = Color(0xFFE3D0CC)
val OnSecondaryContainerLight = Color(0xFF311B06) // Kept the same for contrast
val TertiaryLight = Color(0xFFD3BEB9)
val OnTertiaryLight = Color.White
val TertiaryContainerLight = Color(0xFFE8DAD6)
val OnTertiaryContainerLight = Color(0xFF2C190B) // Kept the same for contrast
val BackgroundLight = Color(0xFFFFF6F6)
val OnBackgroundLight = Color(0xFF1C1C1C)
val SurfaceLight = Color(0xFFFFFBFB)
val OnSurfaceLight = Color(0xFF1C1C1C)
val SurfaceVariantLight = Color(0xFFF4EDEB)
val OnSurfaceVariantLight = Color(0xFF534340)
val SurfaceTintLight = Color(0xFFA31621) // Kept the original primary color
val InverseSurfaceLight = Color(0xFF2C2C2C)
val InverseOnSurfaceLight = Color.White
val ErrorLight = Color(0xFFF4B3B8)
val OnErrorLight = Color.White
val ErrorContainerLight = Color(0xFFF8D1D6)
val OnErrorContainerLight = Color(0xFF370005) // Kept the same for contrast
val OutlineLight = Color(0xFFD1D1D1)
val OutlineVariantLight = Color(0xFFE0E0E0)
val ScrimLight = Color(0x80000000) // Kept the same for overlay*/

// Основной цвет
val PrimaryBase = Color(0xFFA31621)

// Светлая Тема
val PrimaryLight = PrimaryBase
val OnPrimaryLight = Color.White
val PrimaryContainerLight = PrimaryBase.copy(alpha = 0.4f)
val OnPrimaryContainerLight = Color.White
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








