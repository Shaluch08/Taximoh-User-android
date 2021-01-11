package com.uveous.loopfoonpay.Api

import com.uveous.loopfoonpay.Api.Model.userlogin
import com.uveous.loopfoonpay.Api.Model.usersignup
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("signup")
    fun register(@Field("name") name: String,@Field("mobile") mobile:String,@Field("email") email:String,
                 @Field("gender") gender: String, @Field("dob") dob: String,@Field("password") password: String): Call<usersignup>


    @FormUrlEncoded
    @POST("login/user-login")
    fun login(@Field("mobile") email: String,@Field("password") password: String) :Call<userlogin>

}