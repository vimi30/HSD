package com.example.hsd.model


import com.google.gson.annotations.SerializedName

data class OrderedWaypoint(
    @SerializedName("anchor")
    val anchor: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("location")
    val location: Location,
    @SerializedName("passengers")
    val passengers: List<Passenger>
)