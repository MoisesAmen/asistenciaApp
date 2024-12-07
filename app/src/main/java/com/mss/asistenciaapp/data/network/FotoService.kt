package com.mss.asistenciaapp.data.network

import com.mss.asistenciaapp.data.models.Foto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FotoService {
    @POST("fotos")
    suspend fun guardarFoto(@Body foto: Foto): Foto

    @GET("fotos/por-trabajador/{dni}")
    suspend fun obtenerFotosPorTrabajador(@Path("dni") dni: String): List<Foto>
}