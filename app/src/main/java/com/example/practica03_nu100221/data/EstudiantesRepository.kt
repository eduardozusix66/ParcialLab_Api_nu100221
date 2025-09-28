package com.example.practica03_nu100221.data

import android.R

class EstudiantesRepository(
    private val api: ApiService = RetrofitClient.api
) {
    suspend fun list(): List<EstudianteResponse> =
        api.getEstudiantes()

    suspend fun add(nombre: String, edad: Int , foto_url: String): Int {
        val res = api.addEstudiante(EstudiantePayload(nombre, edad, foto_url ))
        return res.data ?: -1
    }

    suspend fun update(id: Int, nombre: String, edad: Int , foto_url: String): EstudianteResponse? {
        val res = api.updateEstudiante(id, EstudiantePayload(nombre, edad, foto_url ))
        return res.data
    }

    suspend fun delete(id: Int) {
        api.deleteEstudiante(id)
    }
}
