package com.example.practica03_nu100221.data

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PhpApiService {

    @Multipart
    @POST("api.php")
    suspend fun uploadFoto(
        @Part file: MultipartBody.Part
    ): UploadResponse
}