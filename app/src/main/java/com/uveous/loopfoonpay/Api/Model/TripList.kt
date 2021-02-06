package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class TripList(
        @SerializedName("status")
        var status:Int,
        @SerializedName("currency")
        var currency: String,
        @SerializedName("result")
        var result:ArrayList<tripresult>
)

class tripresult {
    @SerializedName("request_id")
    var request_id:Int ?=0
    @SerializedName("request_date")
    var request_date:String ?=""
    @SerializedName("request_time")
    var request_time:String ?=""
   @SerializedName("origin_address")
    var origin_address:String ?=""
   @SerializedName("destination_address")
    var destination_address:String ?=""
   @SerializedName("category_name")
    var category_name:String ?=""
    @SerializedName("total_trip_price")
    var total_trip_price:String ?=""
    @SerializedName("status")
    var status:String ?=""
       @SerializedName("vehicle_name")
    var vehicle_name:String ?=""
    @SerializedName("vehicle_number")
    var vehicle_number:String ?=""

}