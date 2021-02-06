package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class TripDetailModel(
@SerializedName("status")
var status:Int,
 @SerializedName("currency")
var currency: String,
@SerializedName("request_id")
 var request_id:Int,
@SerializedName("request_date")
var request_date:String,
@SerializedName("request_time")
var request_time:String,
@SerializedName("origin_address")
var origin_address:String,
@SerializedName("destination_address")
var destination_address:String,
@SerializedName("category_name")
var category_name:String,
@SerializedName("base_fare_price")
var base_fare_price:String,
@SerializedName("total_trip_price")
var total_trip_price:String,
 @SerializedName("distance")
var distance:String ,
@SerializedName("vehicle_name")
var vehicle_name:String,
@SerializedName("vehicle_number")
var vehicle_number:String ,
@SerializedName("request_status")
var request_status:String,
@SerializedName("tax")
var tax:String,
@SerializedName("km_price")
var km_price:String

)
