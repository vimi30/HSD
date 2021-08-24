package com.example.hsd.networking


import com.example.hsd.model.Rides
import retrofit2.Response
import retrofit2.http.GET

interface NetworkService {

    @GET("hsd-interview-resources/simplified_my_rides_response.json")
    suspend fun getRides() : Response<Rides>
}