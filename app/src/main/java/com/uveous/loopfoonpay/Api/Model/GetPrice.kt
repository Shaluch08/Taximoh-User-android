package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class GetPrice (

    @SerializedName("status")
    var status:Int,
    @SerializedName("car")
    val car: Bike,
    @SerializedName("bike")
    val bike: Bike,
    @SerializedName( "currency")
    val currency: String,
    @SerializedName( "distance")
    val distance: String

)

data class Bike (
    @SerializedName("base_fare")
    val baseFare: String,
    @SerializedName( "pr_km")
    val prKM: String,
    @SerializedName("tax")
    val tax: String,
    @SerializedName("total_price")
    val totalPrice: String
)
