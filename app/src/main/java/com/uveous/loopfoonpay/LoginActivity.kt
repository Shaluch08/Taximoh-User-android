package com.uveous.loopfoonpay

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.userlogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var email:TextInputEditText
    lateinit var pwd:TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email=findViewById(R.id.email)
        pwd=findViewById(R.id.pwd)

        val butlogin= findViewById<Button>(R.id.login)
        val reg= findViewById<TextView>(R.id.reg)
        val forgotpassword= findViewById<TextView>(R.id.forgotpassword)

        butlogin.setOnClickListener(View.OnClickListener {
            loginfun()

        })

        reg.setOnClickListener(View.OnClickListener {
            val i= Intent(this,SignUp::class.java)
            startActivity(i)
        })
        forgotpassword.setOnClickListener(View.OnClickListener {
            val i= Intent(this,ForgotActivity::class.java)
            startActivity(i)
        })
    }

    fun loginfun(){

        if(email.text.toString().isEmpty()){
            Toast.makeText(this@LoginActivity,"Please enter email", Toast.LENGTH_SHORT).show()
        }else if(pwd.text.toString().isEmpty()){
            Toast.makeText(this@LoginActivity,"Please enter password", Toast.LENGTH_SHORT).show()
        }else {
            val progressDialog = ProgressDialog(this@LoginActivity)
            // progressDialog.setTitle("Kotlin Progress Bar")
            progressDialog.setMessage("Please wait")
            progressDialog.show()
            var mAPIService: ApiService? = null
            mAPIService = ApiClient.apiService
            mAPIService!!.login(email.text.toString(), pwd.text.toString()).enqueue(object :
                Callback<userlogin> {
                override fun onResponse(call: Call<userlogin>, response: Response<userlogin>) {
                    Log.i("", "post submitted to API." + response.body()!!)
                    if (response.isSuccessful()) {
                        var lo:userlogin = response.body()!!
                        progressDialog.dismiss()
                        val mPrefs: SharedPreferences = this@LoginActivity.getSharedPreferences(
                            "TASK_ID",
                            Context.MODE_PRIVATE
                        )
                        val prefsEditor = mPrefs.edit()
                        lo.user_id?.let { prefsEditor.putInt("userid", it) }
                        prefsEditor.commit()
                        Toast.makeText(this@LoginActivity, "Login", Toast.LENGTH_SHORT).show()
                        Log.v("dd", "post registration to API" + response.body()!!.toString())
                        val Intent = Intent(applicationContext, TravelDashboard::class.java)
                        startActivity(Intent)
                    }
                }

                override fun onFailure(call: Call<userlogin>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })

        }
    }
}