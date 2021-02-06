package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class progressstatus (

    @SerializedName("status")
    var status:Int,


    @SerializedName("request_status")
    var request_status:String




)