package com.mss.asistenciaapp.data.network

import com.mss.asistenciaapp.data.models.Trabajador
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrabajadorService {
    @GET("trabajadores")
    suspend fun getTrabajadores(): List<Trabajador>

    @POST("trabajadores")
    suspend fun addTrabajador(@Body trabajador: Trabajador): Response<Trabajador>

    @GET("trabajadores/existe/{dni}")
    suspend fun getTrabajadorExiste(@Path("dni") dni: String): Boolean
}