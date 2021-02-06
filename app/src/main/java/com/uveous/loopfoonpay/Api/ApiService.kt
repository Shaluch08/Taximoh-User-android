package com.uveous.loopfoonpay.Api

import com.uveous.loopfoonpay.Api.Model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("signup")
    fun register(@Field("name") name: String,@Field("mobile") mobile:String,@Field("email") email:String,
                 @Field("gender") gender: String, @Field("dob") dob: String,@Field("password") password: String): Call<usersignup>


    @FormUrlEncoded
    @POST("login/user-login")
    fun login(@Field("mobile") email: String,@Field("password") password: String) :Call<userlogin>

    @FormUrlEncoded
    @POST("users/request/cancel-request")
    fun cancel(@Header("Authorization") token :String,@Field("request_id") request_id: Int,@Field("user_id") user_id: Int) :Call<cancelride>

    @GET("user/details/where-clauses")
    fun getprofile(@Header("Authorization") token :String,@Query("user_id") user_id:Int):Call<profiledetail>

    @GET("user/details/get-request-price")
    fun senddistance(@Header("Authorization") token :String,@Query("distance") distance:String):Call<GetPrice>

    @GET("user/details/request-accept-driver-details")
    fun getdriverdetails(@Header("Authorization") token :String,@Query("request_id") request_id:Int,@Query("user_id") user_id:Int):Call<Driverdetail>

  @GET("user/details/user-request-status")
    fun getstatus(@Header("Authorization") token :String,@Query("request_id") request_id:Int,@Query("user_id") user_id:Int):Call<progressstatus>

   @GET("user/trip/get-trip-details")
    fun getTripDetail(@Header("Authorization") token :String,@Query("request_id") request_id:Int,@Query("user_id") user_id:Int):Call<TripDetailModel>

    @GET("user/trip/get-all-trips")
    fun getTripList(@Header("Authorization") token :String, @Query("user_id") user_id:Int):Call<TripList>

    @FormUrlEncoded
    @POST("users/request/create-request")
    fun saveride(@Header("Authorization") token :String, @Field("name") name:String,
                 @Field("mobile") mobile:String,
                 @Field("request_date") request_date:String,
                 @Field("request_time") request_time:String,
                 @Field("type") type:Int,
                 @Field("no_of_passengers") no_of_passengers:Int,
                 @Field("origin_longitude") origin_longitude:String,
                 @Field("origin_latitude") origin_latitude:String,
                 @Field("destination_longitude") destination_longitude:String,
                 @Field("destination_latitude") destination_latitude:String,
                 @Field("origin_address") origin_address:String,
                 @Field("destination_address") destination_address:String,
                 @Field("distance") distance:String,
                 @Field("user_id") user_id:Int  ) :Call<saveride>


}