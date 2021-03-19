package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class Driverdetail (

    @SerializedName("status")
    var status:Int,
    @SerializedName("request_log_id")
    val request_log_id: Int? = null,
    @SerializedName("driver_id")
    val driver_id: Int? = null,
    @SerializedName("currency")
    val currency: String? = null,
 @SerializedName("origin_address")
    val origin_address: String? = null,
    @SerializedName("destination_address")
    val destination_address: String? = null,
    @SerializedName("total_price")
    val total_price: String? = null,
      @SerializedName("driver_name")
    val driver_name: String? = null ,
    @SerializedName("driver_mobile")
    val driver_mobile: String? = null,
    @SerializedName("vehicle_name")
    val vehicle_name: String? = null  ,
    @SerializedName("vehicle_number")
    val vehicle_number: String? = null ,
    @SerializedName("profile_photo")
    val profile_photo: String? = null,
    @SerializedName("valid_otp")
    val valid_otp: String? = null
)

