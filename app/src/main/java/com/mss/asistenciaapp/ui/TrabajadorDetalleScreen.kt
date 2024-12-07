package com.mss.asistenciaapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mss.asistenciaapp.data.models.Foto
import com.mss.asistenciaapp.data.network.ApiClient
import com.mss.asistenciaapp.ui.components.PhotoCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrabajadorDetalleScreen(navController: NavController, trabajadorDni: String) {
    var fotos by remember { mutableStateOf<List<Foto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Carga las fotos desde el backend
    LaunchedEffect(trabajadorDni) {
        try {
            isLoading = true
            errorMessage = null
            fotos = ApiClient.fotoService.obtenerFotosPorTrabajador(trabajadorDni)
        } catch (e: Exception) {
            errorMessage = "Error al cargar fotos: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Fotos del trabajador: $trabajadorDni",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = Color.Red,
                style = MaterialTheme.typography.body1
            )
        } else {
            LazyColumn {
                items(fotos) { foto ->
                    PhotoCard(foto = foto)
                }
            }
        }
    }
}

