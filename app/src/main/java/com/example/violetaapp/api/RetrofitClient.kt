package com.example.violetaapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val instance: PlaceApiService by lazy {
        Retrofit.Builder()
                .baseUrl("https://violeta-be.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PlaceApiService::class.java)
    }
}
