package com.uveous.loopfoonpay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.backgroundcheck
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotActivity : AppCompatActivity(){
    lateinit var toolbar: Toolbar
    lateinit var logindata: Button
    lateinit var email: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        toolbar=findViewById(R.id.toolbar)
        logindata=findViewById(R.id.logindata)
        email=findViewById(R.id.email)


        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        })


        logindata.setOnClickListener(View.OnClickListener {
            var mAPIService: ApiService? = null
            mAPIService = ApiClient.apiService
            mAPIService!!.forgotemail(email.text.toString()).enqueue(object :
                    Callback<backgroundcheck> {
                override fun onResponse(call: Call<backgroundcheck>, response: Response<backgroundcheck>) {
                    Log.v("", "post submitted to API." + response.body()!!)
                    Log.v("tok", response.toString());
                    if (response.isSuccessful()) {
                        var lo: backgroundcheck = response.body()!!
                        if (lo.status == 200) {

                            startActivity(Intent(this@ForgotActivity,EnterotpForgotActivity::class.java)
                                    .putExtra("otp",lo.forgot_otp)
                                    .putExtra("userid",lo.user_id))
                            logindata.isEnabled=false
                        } else {

                            Toast.makeText(this@ForgotActivity, "error", Toast.LENGTH_SHORT).show()
                        }

                    }
                }


                override fun onFailure(call: Call<backgroundcheck>, t: Throwable) {
                    Log.v("tok", t.message.toString());
                    Toast.makeText(this@ForgotActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        })

    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,LoginActivity::class.java))

    }



}
