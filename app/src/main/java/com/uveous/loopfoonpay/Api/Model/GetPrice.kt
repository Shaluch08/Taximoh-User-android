package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class GetPrice (
    @SerializedName("status")
    var status:Int,
    @SerializedName( "currency")
    val currency: String,
    @SerializedName( "distance")
    val distance: String,
    @SerializedName( "wallet_amount")
    val walletAmount: String,
    @SerializedName( "image_url")
    val imageUrl: String,
    @SerializedName(   "vehicle_category")
    val vehicleCategory: List<VehicleCategory>,
    @SerializedName(   "payment_methods")
    val paymentMethods: List<PaymentMethods>


)
data class VehicleCategory (
        @SerializedName(  "vehicle_type")
        val vehicleType: String,

        @SerializedName( "vehicle_photo")
        val vehiclePhoto: String,

        val name: String,

        @SerializedName(  "vehicle_category_id")
        val vehicleCategoryID: Int,

        @SerializedName(  "vehicle_id")
        val vehicle_id: Int,

        @SerializedName(  "base_fare")
        val baseFare: String,

        @SerializedName(  "pr_km")
        val prKM: String,

        val tax: String,
        @SerializedName(  "tax_rate")
        val taxrate: String,

        @SerializedName( "total_price")
        val totalPrice: String
)

data class PaymentMethods (
        @SerializedName(  "id")
        val paymentId: String,

        @SerializedName( "name")
        val paymentName: String,


        @SerializedName(  "slug")
        val paymentSlug: String,

        @SerializedName(  "icon")
        val paymentIcon: String


)
