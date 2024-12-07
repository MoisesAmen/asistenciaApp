package com.mss.asistenciaapp.ui

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.io.File
import android.view.Surface
import androidx.compose.foundation.Image
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberImagePainter

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

    // Estado para controlar si la foto fue tomada
    var photoPath by remember { mutableStateOf<String?>(null) }

    // Estado de permisos
    var permissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> permissionGranted = isGranted }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    if (permissionGranted) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Mostrar la cámara
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Botón para capturar la foto
            Button(
                onClick = {
                    if (photoPath == null) { // Solo permitir si no hay una foto activa
                        Log.d("CameraScreen", "Iniciando captura de foto")
                        capturePhoto(context, imageCapture.value) { filePath ->  // Usar imageCapture.value
                            if (filePath != null) {
                                Log.d("CameraScreen", "Foto capturada correctamente: $filePath")
                                photoPath = filePath // Actualiza la ruta de la foto
                            } else {
                                Log.e("CameraScreen", "Error al capturar la foto")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Capturar Foto")
            }

            // Mostrar el modal si hay una foto capturada
            photoPath?.let {
                PhotoCapturedDialog(
                    photoPath = it,
                    onSave = { path ->
                        onPhotoTaken(path) // Guarda la foto
                        photoPath = null // Limpia la ruta
                        Log.d("CameraScreen", "Foto guardada y estado reiniciado")
                    },
                    onDiscard = {
                        photoPath = null // Descarta la foto y limpia el estado
                        restartCamera(context, previewView, lifecycleOwner, imageCapture)
                        Log.d("CameraScreen", "Foto descartada y estado reiniciado")
                    }
                )
            }
        }

        // Configurar CameraX
        LaunchedEffect(Unit) {
            try {
                cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(previewView.surfaceProvider)

                // Manejar la rotación de forma segura
                val rotation = try {
                    previewView.display?.rotation ?: Surface.ROTATION_0
                } catch (e: Exception) {
                    Log.e("CameraScreen", "Error obteniendo rotación de pantalla: ${e.message}")
                    Surface.ROTATION_0
                }

                // Inicializa imageCapture
                val newImageCapture = ImageCapture.Builder()
                    .setTargetRotation(rotation) // Establecer rotación segura
                    .build()

                imageCapture.value = newImageCapture  // Asignar a la variable mutable

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                // Primero, liberar la cámara anterior
                cameraProvider?.unbindAll()

                // Ahora vincular los nuevos casos de uso
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture.value  // Usar imageCapture.value
                )
            } catch (e: Exception) {
                Log.e("CameraScreen", "Error al configurar la cámara: ${e.message}")
            }
        }
    } else {
        Text("Se necesita permiso para usar la cámara.", modifier = Modifier.fillMaxSize())
    }
}

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

        val newImageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                newImageCapture
            )
            imageCaptureState.value = newImageCapture  // Asignar el nuevo valor a imageCaptureState
        } catch (e: Exception) {
            Log.e("CameraScreen", "Error al reiniciar la cámara: ${e.message}", e)
        }
    }, ContextCompat.getMainExecutor(context))
}


/**
 * Captura una foto usando CameraX y la guarda en el almacenamiento interno.
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

    // Crear un archivo en el almacenamiento interno
    val photoFile = File(
        context.filesDir,
        "photo_${System.currentTimeMillis()}.jpg" // Nombre único
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // Capturar la foto
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d("CameraScreen", "Foto guardada en: ${photoFile.absolutePath}")
                onPhotoSaved(photoFile.absolutePath) // Retorna la ruta del archivo guardado
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Error al capturar la foto: ${exception.message}", exception)
                onPhotoSaved(null) // Informa de un error
            }
        }
    )
}

/**
 * Dialog que muestra la foto tomada y permite al usuario elegir si la guarda o la descarta.
 */
@Composable
fun PhotoCapturedDialog(
    photoPath: String,
    onSave: (String) -> Unit,
    onDiscard: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* No hacer nada al tocar fuera del cuadro de diálogo */ },
        title = { Text("Foto tomada") },
        text = {
            Image(
                painter = rememberImagePainter(photoPath),
                contentDescription = "Vista previa de la foto",
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(photoPath) }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDiscard
            ) {
                Text("Descartar")
            }
        }
    )
}












