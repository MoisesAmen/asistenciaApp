package com.mss.asistenciaapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mss.asistenciaapp.data.models.Trabajador


import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp

@Composable
fun WorkerCard(trabajador: Trabajador, onClick: () -> Unit) {
    // Usa CardDefaults.elevation para establecer la elevación en Material 3
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${trabajador.nombres} ${trabajador.apellidos}",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "DNI: ${trabajador.dni}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


