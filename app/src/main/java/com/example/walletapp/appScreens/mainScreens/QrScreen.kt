package com.example.walletapp.appScreens.mainScreens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.roundedShape
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun QrScreen(onScanResult: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current


    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(key1 = Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    var scanResult by remember { mutableStateOf("") }
    //val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (hasCameraPermission) {
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
            AndroidView(factory = { context ->
                val previewView = androidx.camera.view.PreviewView(context)
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val qrCodeAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(context), QRCodeAnalyzer { result ->
                            scanResult = result
                            onScanResult(result)
                        })
                    }

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            qrCodeAnalyzer
                        )
                    } catch (exc: Exception) {
                        exc.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))

                previewView
            }, modifier = Modifier.fillMaxSize())

            // Визуальный квадрат для области сканирования
            // Оверлей со сканированием QR
            QrScannerOverlay(
                modifier = Modifier
                    .fillMaxWidth(0.75f) // Используйте 75% от ширины экрана
                    .aspectRatio(1f) // Сохраняет соотношение сторон квадрата 1:1
            )
        } else {
            Text("Требуется разрешение на использование камеры")
        }
    }
}


class QRCodeAnalyzer(private val onQrCodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @OptIn(ExperimentalGetImage::class) override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue ?: continue
                        onQrCodeDetected(rawValue)
                        break // Stop after the first QR Code is detected
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close() // It's important to close the imageProxy
                }
        }
    }
}


//Визуальная часть
@Composable
fun QrScannerOverlay(modifier: Modifier = Modifier) {
    val color = colorScheme.primary // Use color from MaterialTheme or specify directly
    val strokeWidth = with(LocalDensity.current) { 5.dp.toPx() } // Stroke width for lines

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Function to draw a single corner line
            fun drawCornerLine(startX: Float, startY: Float, endX: Float, endY: Float) {
                drawLine(
                    color = color,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round // This makes the line ends rounded
                )
            }

            val cornerLength = 50.dp.toPx() // Length of the corner lines
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Top-left corner
            drawCornerLine(0f, 0f, cornerLength, 0f)
            drawCornerLine(0f, 0f, 0f, cornerLength)

            // Top-right corner
            drawCornerLine(canvasWidth, 0f, canvasWidth - cornerLength, 0f)
            drawCornerLine(canvasWidth, 0f, canvasWidth, cornerLength)

            // Bottom-left corner
            drawCornerLine(0f, canvasHeight, cornerLength, canvasHeight)
            drawCornerLine(0f, canvasHeight, 0f, canvasHeight - cornerLength)

            // Bottom-right corner
            drawCornerLine(canvasWidth, canvasHeight, canvasWidth - cornerLength, canvasHeight)
            drawCornerLine(canvasWidth, canvasHeight, canvasWidth, canvasHeight - cornerLength)
        }
    }
}
