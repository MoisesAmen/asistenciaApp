package com.mss.asistenciaapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mss.asistenciaapp.data.models.Trabajador
import com.mss.asistenciaapp.data.network.ApiClient

@Composable
fun WorkerDropdown(
    onWorkerSelected: (Trabajador) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var trabajadores by remember { mutableStateOf(listOf<Trabajador>()) }
    var selectedText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar los trabajadores desde el backend
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            trabajadores = ApiClient.apiService.getTrabajadores()
        } catch (e: Exception) {
            errorMessage = "Error al cargar trabajadores: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = modifier) {
        TextField(
            value = selectedText,
            onValueChange = { text ->
                selectedText = text
                expanded = true
            },
            label = { Text("Seleccionar trabajador") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Error desconocido",
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (isLoading) {
            // Indicador de carga mientras se obtienen los datos
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                trabajadores.filter {
                    it.nombres.contains(selectedText, true) ||
                            it.apellidos.contains(selectedText, true)
                }.forEach { trabajador ->
                    DropdownMenuItem(onClick = {
                        selectedText = "${trabajador.nombres} ${trabajador.apellidos}"
                        onWorkerSelected(trabajador)
                        expanded = false
                    }) {
                        Text("${trabajador.nombres} ${trabajador.apellidos}")
                    }
                }
            }
        }
    }
}
