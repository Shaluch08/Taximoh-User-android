package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class usersignup(

    @SerializedName("name")
    val name: String? = null,
    @SerializedName("mobile")
    val mobile: String? = null,
   @SerializedName("email")
    val email: String? = null,
   @SerializedName("username")
    val username: String? = null,
   @SerializedName("user_type")
    val user_type: String? = null,
   @SerializedName("gender")
    val gender: String? = null,
   @SerializedName("dob")
    val dob: String? = null,
   @SerializedName("status")
    val status: Int? = null,
    @SerializedName("user_id")
    val user_id: Int? = null

)