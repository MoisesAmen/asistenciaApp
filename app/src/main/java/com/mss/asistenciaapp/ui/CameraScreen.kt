package com.mss.asistenciaapp.ui

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberImagePainter
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCaptureException


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraScreen(
    onPhotoTaken: (String) -> Unit
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }
    var cameraProvider: ProcessCameraProvider? = null
    var photoPath by remember { mutableStateOf<String?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> permissionGranted = isGranted }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    if (permissionGranted) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Vista de la cámara
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Botón para capturar foto
            Button(
                onClick = {
                    capturePhoto(context, imageCapture.value) { filePath ->
                        photoPath = filePath
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Capturar Foto")
            }

            // Mostrar foto capturada
            photoPath?.let {
                PhotoCapturedDialog(
                    photoPath = it,
                    onSave = { path ->
                        onPhotoTaken(path)
                        photoPath = null
                    },
                    onDiscard = {
                        photoPath = null
                        restartCamera(context, previewView, lifecycleOwner, imageCapture)
                    }
                )
            }
        }

        // Configurar CameraX
        LaunchedEffect(Unit) {
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val imageCaptureBuilder = ImageCapture.Builder()
            imageCapture.value = imageCaptureBuilder.build()

            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture.value
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Se necesita permiso para usar la cámara.")
        }
    }
}

/**
 * Reinicia la cámara después de descartar la foto.
 */
private fun restartCamera(
    context: Context,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    imageCaptureState: MutableState<ImageCapture?>
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val newImageCapture = ImageCapture.Builder().build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                newImageCapture
            )
            imageCaptureState.value = newImageCapture
        } catch (e: Exception) {
            Log.e("CameraScreen", "Error al reiniciar la cámara: ${e.message}", e)
        }
    }, ContextCompat.getMainExecutor(context))
}

/**
 * Captura una foto usando CameraX y la guarda en almacenamiento interno.
 */
private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    onPhotoSaved: (String?) -> Unit
) {
    if (imageCapture == null) {
        onPhotoSaved(null)
        return
    }

    val photoFile = File(context.filesDir, "photo_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onPhotoSaved(photoFile.absolutePath)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Error al capturar la foto: ${exception.message}", exception)
                onPhotoSaved(null)
            }
        }
    )
}

/**
 * Diálogo para manejar la foto capturada.
 */
@Composable
fun PhotoCapturedDialog(
    photoPath: String,
    onSave: (String) -> Unit,
    onDiscard: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { },
        title = { Text("Foto tomada") },
        text = {
            Image(
                painter = rememberImagePainter(photoPath),
                contentDescription = "Vista previa de la foto",
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            androidx.compose.material3.Button(onClick = { onSave(photoPath) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            androidx.compose.material3.Button(onClick = onDiscard) {
                Text("Descartar")
            }
        }
    )
}













