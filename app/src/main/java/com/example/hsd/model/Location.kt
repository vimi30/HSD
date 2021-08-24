package com.example.hsd.model


import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("address")
    val address: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)