package com.mss.asistenciaapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://192.168.100.51:8080/api/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: TrabajadorService by lazy {
        retrofit.create(TrabajadorService::class.java)
    }
    val fotoService: FotoService by lazy {
        retrofit.create(FotoService::class.java)
    }
}