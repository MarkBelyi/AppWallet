package com.example.walletapp.appScreens.mainScreens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.activity.QrActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

@Composable
fun QrScreen(viewModel: appViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasCamPermission = remember {
        mutableStateOf(ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCamPermission.value = granted
    }

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Column(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)) {
        if (hasCamPermission.value) {
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
            AndroidView(factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val qrCodeAnalyzer = QRCodeAnalyzer(context = context) {
                    result ->
                    // Обработайте результат сканирования здесь
                }

                cameraProviderFuture.get().bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    ImageAnalysis.Builder().build().also { it.setAnalyzer(ContextCompat.getMainExecutor(context), qrCodeAnalyzer) }
                )

                previewView
            }, modifier = Modifier.weight(1f).padding(16.dp))

            // Дополнительный UI может быть здесь
        } else {
            Text("Требуется разрешение на использование камеры")
        }
    }
}

class QRCodeAnalyzer(private val context: Context, private val onQrCodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        val hints = mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE))
        setHints(hints)
    }

    override fun analyze(imageProxy: ImageProxy) {
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val data = imageProxy.planes[0].buffer.toByteArray()
        val source = PlanarYUVLuminanceSource(data, imageProxy.width, imageProxy.height, 0, 0, imageProxy.width, imageProxy.height, false)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = reader.decodeWithState(bitmap)
            // Ваш callback теперь может быть вызван на главном потоке UI
            (context as Activity).runOnUiThread {
                onQrCodeDetected(result.text)
            }
        } catch (e: Exception) {
            // Логирование или обработка ошибок декодирования
        } finally {
            imageProxy.close()
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Перемотать буфер к началу
        return ByteArray(remaining()).also { get(it) } // Копировать буфер в массив байтов
    }
}