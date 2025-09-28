package com.example.practica03_nu100221.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitPhpClient {

    // 👇 Si tu upload.php está en el root del dominio
    private const val BASE_URL = "https://nu100221.enastudios.com/"

    // 👇 Si lo pusiste en /apiform/
    // private const val BASE_URL = "https://enastudios.com/apiform/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: PhpApiService = retrofit.create(PhpApiService::class.java)
}