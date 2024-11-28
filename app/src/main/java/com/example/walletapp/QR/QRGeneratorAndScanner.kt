package com.example.walletapp.QR

import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun generateQRCode(
    text: String,
    size: Int = 500,
    foregroundColor: Color = colorScheme.onSurface,
    backgroundColor: Color = colorScheme.surface
): ImageBitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
    val pixels = IntArray(size * size)
    val foregroundArgb = foregroundColor.toArgb()
    val backgroundArgb = backgroundColor.toArgb()
    for (y in 0 until size) {
        for (x in 0 until size) {
            val offset = y * size
            //прозрачный цвет для фона и черный для QR-кода
            pixels[offset + x] = if (bitMatrix.get(
                    x,
                    y
                )
            ) foregroundArgb else backgroundArgb //android.graphics.Color.BLACK else android.graphics.Color.TRANSPARENT
        }
    }
    //ARGB_8888 для поддержки прозрачности
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, size, 0, 0, size, size)
    }
    return bmp.asImageBitmap()
}


