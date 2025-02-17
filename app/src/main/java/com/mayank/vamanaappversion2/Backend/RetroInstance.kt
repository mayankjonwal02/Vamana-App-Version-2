package com.mayank.vamanaappversion2.Backend

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val host_global = "https://vamana-backend.vercel.app"
    private const val host_local = "http://10.6.0.61:3000"
    private const val BASE_URL = "${host_local}/api/"

    fun getClient(): API_Service {
        var retrofitClient = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(API_Service::class.java)

        return retrofitClient
    }


//    private val loggingInterceptor = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//
//    private val client = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .build()
//
//    val api: API_Service by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()
//            .create(API_Service::class.java)
//    }
}
