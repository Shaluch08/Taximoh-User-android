package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class saveride (

    @SerializedName("status")
    var status:Int,

  @SerializedName("request_id")
    var request_id:Int,

    @SerializedName("wait_time")
    var wait_time:String




)