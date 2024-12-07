package com.mss.asistenciaapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mss.asistenciaapp.data.models.Trabajador

@Composable
fun WorkerDropdown(
    trabajadores: List<Trabajador>,
    selectedText: String,
    onTextChanged: (String) -> Unit,
    onWorkerSelected: (Trabajador) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    Column(modifier = modifier) {
        // Fila para el TextField y el icono "X"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = selectedText,
                onValueChange = { text ->
                    onTextChanged(text) // Actualiza el texto en el nivel superior
                    expanded = true
                },
                label = { Text("Seleccionar trabajador") },
                modifier = Modifier.weight(1f), // Esto asegura que el TextField ocupe el espacio disponible
                isError = errorMessage != null
            )

            // Botón "X" para vaciar el texto
            if (selectedText.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onTextChanged("") // Vaciar el texto
                        expanded = false // Cerrar el dropdown si está abierto
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar texto",
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Error desconocido",
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (isLoading) {
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
                        onWorkerSelected(trabajador) // Selecciona el trabajador
                        expanded = false
                    }) {
                        Text("${trabajador.nombres} ${trabajador.apellidos}")
                    }
                }
            }
        }
    }
}


