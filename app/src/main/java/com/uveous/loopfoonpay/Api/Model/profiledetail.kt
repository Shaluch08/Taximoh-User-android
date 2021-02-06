package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class profiledetail (

    @SerializedName("status")
    var status:Int,
    @SerializedName("user_id")
    val user_id: Int? = null,
    @SerializedName("msg")
    val msg: String? = null,
 @SerializedName("name")
    val name: String? = null,
    @SerializedName("mobile")
    val mobile: String? = null,
    @SerializedName("email")
    val email: String? = null,
      @SerializedName("dob")
    val dob: String? = null ,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("pincode")
    val pincode: String? = null ,
    @SerializedName("gender")
    val gender: String? = null
)

