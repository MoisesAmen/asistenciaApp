package com.mss.asistenciaapp.data.models

import java.time.LocalDate

data class Foto(
    val id: Long? = null,
    val ruta: String,
    val fecha: String,
    val trabajador: TrabajadorId
)

data class TrabajadorId(
    val dni: String
)
