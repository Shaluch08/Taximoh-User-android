package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class userlogin (

    @SerializedName("status")
    var status:Int,
    @SerializedName("user_id")
    val user_id: Int? = null,
    @SerializedName("msg")
    val msg: String? = null,
 @SerializedName("user_name")
    val user_name: String? = null,
    @SerializedName("user_mobile")
    val user_mobile: String? = null,
    @SerializedName("user_email")
    val user_email: String? = null,
      @SerializedName("api_token")
    val api_token: String? = null
)