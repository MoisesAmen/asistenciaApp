package com.mss.asistenciaapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mss.asistenciaapp.data.models.Trabajador
import com.mss.asistenciaapp.data.network.ApiClient
import com.mss.asistenciaapp.ui.components.WorkerCard

@Composable
fun VerTrabajadoresScreen(navController: NavController) {
    var trabajadores by remember { mutableStateOf(listOf<Trabajador>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar los trabajadores desde el backend
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val response = ApiClient.apiService.getTrabajadores()
            trabajadores = response
        } catch (e: Exception) {
            errorMessage = "Error al cargar los trabajadores: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { navController.navigate("agregarTrabajador") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Trabajador")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                // Mostrar un indicador de carga mientras los datos se obtienen
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                // Mostrar mensaje de error si ocurre algún problema
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else -> {
                // Mostrar la lista de trabajadores
                LazyColumn {
                    items(trabajadores) { trabajador ->
                        WorkerCard(trabajador = trabajador, onClick = {
                            navController.navigate("trabajadorDetalle/${trabajador.dni}")
                        })
                    }
                }
            }
        }
    }
}
