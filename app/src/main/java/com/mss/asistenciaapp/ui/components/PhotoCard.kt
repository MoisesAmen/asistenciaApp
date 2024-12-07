package com.mss.asistenciaapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.mss.asistenciaapp.data.models.Foto
import java.io.File

/*@Composable
fun PhotoCard(foto: Foto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Fecha: ${foto.fecha}", style = MaterialTheme.typography.body1)
            Text(text = "Ruta: ${foto.ruta}", style = MaterialTheme.typography.body2)
        }
    }
}*/

@Composable
fun PhotoCard(foto: Foto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(text = "Fecha: ${foto.fecha}", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberAsyncImagePainter(File(foto.ruta).toUri()),
            contentDescription = "Foto",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}