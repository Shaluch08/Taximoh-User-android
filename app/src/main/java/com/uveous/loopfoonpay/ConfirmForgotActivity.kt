package com.uveous.loopfoonpay

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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

class  ConfirmForgotActivity : AppCompatActivity(){
    lateinit var toolbar: Toolbar
    lateinit var logindata: Button
    lateinit var email: EditText
    lateinit var email1: EditText
    var userid:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot1)
        toolbar=findViewById(R.id.toolbar)
        logindata=findViewById(R.id.logindata)

        email=findViewById(R.id.email)
        email1=findViewById(R.id.email1)
        userid=intent.getIntExtra("userid",0)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this,EnterotpForgotActivity::class.java))
        })

        logindata.setOnClickListener(View.OnClickListener {
            if(email.text.toString().contentEquals(email1.text.toString())){
                val progressDialog = ProgressDialog(this@ConfirmForgotActivity)
                progressDialog.setMessage("Please wait")
                progressDialog.show()
                progressDialog.setCanceledOnTouchOutside(false)
                var mAPIService: ApiService? = null
                mAPIService = ApiClient.apiService
                mAPIService!!.forgotconfirmpassword(userid,email.text.toString()).enqueue(object :
                        Callback<backgroundcheck> {
                    override fun onResponse(call: Call<backgroundcheck>, response: Response<backgroundcheck>) {
                        Log.v("", "post submitted to API." + response.body()!!)
                        Log.v("tok", response.toString());
                        if (response.isSuccessful()) {
                            var lo: backgroundcheck = response.body()!!
                            if (lo.status == 200) {
                                progressDialog.dismiss()
                                Toast.makeText(this@ConfirmForgotActivity, "Password changed.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@ConfirmForgotActivity,LoginActivity::class.java)
                                )

                            } else {
                                progressDialog.dismiss()
                                Toast.makeText(this@ConfirmForgotActivity, "error", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }


                    override fun onFailure(call: Call<backgroundcheck>, t: Throwable) {
                        Log.v("tok", t.message.toString());
                        progressDialog.dismiss()
                        Toast.makeText(this@ConfirmForgotActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                Toast.makeText(this@ConfirmForgotActivity, "Both password are not matched.", Toast.LENGTH_SHORT).show()

            }

        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,EnterotpForgotActivity::class.java))

    }

}
