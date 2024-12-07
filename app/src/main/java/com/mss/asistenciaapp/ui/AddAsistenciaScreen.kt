package com.mss.asistenciaapp.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
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
            Text("Agregar Asistencia", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown para seleccionar trabajador
            WorkerDropdown(
                //trabajadores = trabajadores,
                onWorkerSelected = { worker -> selectedWorker = worker },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha de asistencia
            Text("Fecha: ${fechaAsistencia.toString()}", style = MaterialTheme.typography.body1)

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
                            fecha = fechaAsistencia
                        )
                        navController.popBackStack() // Volver a la pantalla anterior si todo está listo
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
}


/**
 * Función para guardar la asistencia (lógica personalizada).
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun guardarAsistencia(
    trabajador: Trabajador,
    rutaFoto: String,
    fecha: LocalDate
) {
    // Crea un objeto Foto con los datos necesarios
    val foto = Foto(
        ruta = rutaFoto,
        fecha = fecha.toString(),
        trabajador = TrabajadorId(trabajador.dni)
    )

    // Llamada a la API para guardar la foto
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = ApiClient.fotoService.guardarFoto(foto)
            Log.i("AddAsistenciaScreen", "Foto guardada correctamente: $response")
        } catch (e: HttpException) {
            Log.e("AddAsistenciaScreen", "Error del servidor: ${e.response()?.errorBody()?.string()}")
        } catch (e: Exception) {
            Log.e("AddAsistenciaScreen", "Error al guardar la foto: ${e.message}")
        }
    }
}


/*@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddAsistenciaScreen(navController: NavController) {
    var selectedWorker by remember { mutableStateOf<Trabajador?>(null) }
    var trabajadores by remember { mutableStateOf(listOf<Trabajador>()) }
    var fechaAsistencia by remember { mutableStateOf(LocalDate.now()) }
    var photoPath by remember { mutableStateOf<String?>(null) }
    var isCameraOpen by remember { mutableStateOf(false) }

    // Cargar los trabajadores desde el backend
    LaunchedEffect(Unit) {
        try {
            trabajadores = ApiClient.apiService.getTrabajadores()
        } catch (e: Exception) {
            // Manejo de error
        }
    }

    if (isCameraOpen) {
        CameraScreen(
            navController = navController,
            onPhotoTaken = { path ->
                photoPath = path
                isCameraOpen = false
                navController.popBackStack() // Manejamos la navegación aquí
            }
        )
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Agregar Asistencia", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(16.dp))

            WorkerDropdown(
                onWorkerSelected = { worker -> selectedWorker = worker },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha
            Text("Fecha: ${fechaAsistencia.toString()}")

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para abrir la cámara
            Button(
                onClick = { isCameraOpen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir Cámara")
            }

            photoPath?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Foto seleccionada: $it")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para guardar la asistencia
            Button(
                onClick = {
                    /*if (selectedTrabajador != null && photoPath != null) {
                        guardarAsistencia(
                            Trabajador = selectedTrabajador!!,
                            rutaFoto = photoPath!!,
                            fecha = fechaAsistencia
                        )
                        navController.popBackStack()
                    }*/
                },
                modifier = Modifier.fillMaxWidth(),
                //enabled = selectedTrabajador != null && photoPath != null
            ) {
                Text("Guardar Asistencia")
            }
        }
    }
}*/


