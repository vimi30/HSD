package com.example.hsd.repository


import com.example.hsd.model.Rides
import com.example.hsd.networking.NetworkService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository
@Inject constructor(private val networkService: NetworkService) {

    suspend fun getRidesFromServer() : Response<Rides> {
        return networkService.getRides()
    }



}