package com.mss.asistenciaapp.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.mss.asistenciaapp.data.models.Foto
import com.mss.asistenciaapp.data.models.Trabajador
import com.mss.asistenciaapp.data.models.TrabajadorId
import com.mss.asistenciaapp.data.network.ApiClient
import com.mss.asistenciaapp.ui.components.WorkerDropdown
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddAsistenciaScreen(navController: NavController) {
    var selectedWorker by remember { mutableStateOf<Trabajador?>(null) }
    var trabajadores by remember { mutableStateOf(listOf<Trabajador>()) }
    var fechaAsistencia by remember { mutableStateOf(LocalDate.now()) }
    var photoPath by remember { mutableStateOf<String?>(null) }
    var isCameraOpen by remember { mutableStateOf(false) } // Control de la cámara
    var selectedWorkerText by remember { mutableStateOf("") } // Para el texto seleccionado

    // Estado para mostrar el modal de confirmación
    var showSuccessModal by remember { mutableStateOf(false) }

    // Cargar los trabajadores desde el backend
    LaunchedEffect(Unit) {
        try {
            trabajadores = ApiClient.apiService.getTrabajadores()
        } catch (e: Exception) {
            Log.e("AddAsistenciaScreen", "Error al cargar trabajadores: ${e.message}")
        }
    }

    if (isCameraOpen) {
        // Mostrar la cámara
        CameraScreen(
            onPhotoTaken = { path ->
                photoPath = path // Guardar la ruta de la foto
                isCameraOpen = false // Volver al formulario sin usar navegación
            }
        )
    } else {
        // Pantalla principal del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Agregar Asistencia", style = MaterialTheme.typography.titleSmall)

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown para seleccionar trabajador
            WorkerDropdown(
                trabajadores = trabajadores,
                selectedText = selectedWorkerText, // Pasa el texto seleccionado
                onTextChanged = {
                    selectedWorkerText = it // Actualiza el texto seleccionado
                    if (it.isEmpty()) {
                        selectedWorker = null // Si el texto está vacío, resetear el trabajador
                    }
                }, // Actualiza el texto seleccionado
                onWorkerSelected = { worker ->
                    selectedWorker = worker
                    selectedWorkerText = "${worker.nombres} ${worker.apellidos}"
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha de asistencia
            Text("Fecha: ${fechaAsistencia.toString()}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Miniatura de la foto capturada (si existe)
            if (photoPath != null) {
                Image(
                    painter = rememberAsyncImagePainter(File(photoPath!!).toUri()),
                    contentDescription = "Miniatura de la foto",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón para abrir la cámara
            Button(
                onClick = { isCameraOpen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir Cámara")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para guardar la asistencia
            Button(
                onClick = {
                    if (selectedWorker != null && photoPath != null) {
                        guardarAsistencia(
                            trabajador = selectedWorker!!,
                            rutaFoto = photoPath!!,
                            fecha = fechaAsistencia,
                            onSuccess = {
                                // Mostrar el modal de éxito
                                showSuccessModal = true
                            }
                        )
                    } else {
                        Log.e("AddAsistenciaScreen", "Faltan datos para guardar la asistencia.")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedWorker != null && photoPath != null
            ) {
                Text("Guardar Asistencia")
            }
        }
    }

    // Modal de confirmación
    if (showSuccessModal) {
        AlertDialog(
            onDismissRequest = { showSuccessModal = false },
            title = { Text("Asistencia Guardada") },
            text = { Text("La asistencia ha sido guardada correctamente.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessModal = false
                        navController.popBackStack() // Volver a la pantalla anterior
                    }
                ) {
                    Text("OK")
                }
            },
        )
    }
}


/**
 * Función para guardar la asistencia (lógica personalizada).
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun guardarAsistencia(
    trabajador: Trabajador,
    rutaFoto: String,
    fecha: LocalDate,
    onSuccess: () -> Unit // Callback para mostrar el modal de éxito
) {
    // Crea un objeto Foto con los datos necesarios
    val foto = Foto(
        ruta = rutaFoto,
        fecha = fecha.toString(),
        trabajador = TrabajadorId(trabajador.dni)
    )

    // Convertir el objeto Foto a JSON usando Gson
    val json = Gson().toJson(foto)
    Log.d("AddAsistenciaScreen", "JSON enviado: $json")

    // Llamada a la API para guardar la foto
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = ApiClient.fotoService.guardarFoto(foto)
            Log.i("AddAsistenciaScreen", "Foto guardada correctamente: $response")
            onSuccess() // Llamar a onSuccess cuando la foto se guarda correctamente
        } catch (e: HttpException) {
            Log.e("AddAsistenciaScreen", "Error del servidor: ${e.response()?.errorBody()?.string()}")
        } catch (e: Exception) {
            Log.e("AddAsistenciaScreen", "Error al guardar la foto: ${e.message}")
        }
    }
}



