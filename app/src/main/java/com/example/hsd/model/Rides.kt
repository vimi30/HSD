package com.example.hsd.model


import com.google.gson.annotations.SerializedName

data class Rides(
    @SerializedName("rides")
    val rides: List<Ride>
)