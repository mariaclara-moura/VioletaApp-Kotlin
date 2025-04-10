package com.example.violetaapp.api

import com.example.violetaapp.data.Place
import retrofit2.http.GET

interface PlaceApiService {
    @GET("locais_seguros")
    suspend fun getPlaces(): List<Place>
}
