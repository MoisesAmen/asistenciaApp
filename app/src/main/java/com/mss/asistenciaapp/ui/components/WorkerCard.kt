package com.mss.asistenciaapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mss.asistenciaapp.data.models.Trabajador

@Composable
fun WorkerCard(trabajador: Trabajador, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${trabajador.nombres} ${trabajador.apellidos}", style = MaterialTheme.typography.h6)
            Text(text = "DNI: ${trabajador.dni}", style = MaterialTheme.typography.body2)
        }
    }
}