package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class backgroundcheck(
    @SerializedName("status")
    var status:Int,
    @SerializedName("user_id")
    var user_id:Int,
    @SerializedName("forgot_otp")
    var forgot_otp:Int




)