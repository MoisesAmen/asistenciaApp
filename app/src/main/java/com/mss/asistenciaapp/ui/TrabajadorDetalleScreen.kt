package com.mss.asistenciaapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mss.asistenciaapp.data.models.Foto
import com.mss.asistenciaapp.data.network.ApiClient
import com.mss.asistenciaapp.ui.components.PhotoCard

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Trabajador") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Color de fondo del top bar
                    titleContentColor = MaterialTheme.colorScheme.onPrimary // Color del texto del título
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Fotos del trabajador: $trabajadorDni",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )
            } else if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
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
}
