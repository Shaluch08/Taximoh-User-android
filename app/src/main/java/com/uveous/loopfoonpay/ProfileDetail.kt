package com.uveous.loopfoonpay

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.profiledetail
import com.uveous.loopfoonpay.Api.Model.saveride
import com.uveous.loopfoonpay.Api.SessionManager
import com.uveous.taximohdriver.TravelDashboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileDetail : AppCompatActivity(){
    lateinit var toolbar: Toolbar
    lateinit var name: TextInputEditText
    lateinit var lname: TextInputEditText
    lateinit var email: TextInputEditText
    lateinit var gender: TextInputEditText
    lateinit var dob: TextInputEditText
    lateinit var contact: TextInputEditText
    lateinit var address: TextInputEditText
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        toolbar=findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this, TravelDashboard::class.java))
        })


        email=findViewById(R.id.email)
        name=findViewById(R.id.name)
        lname=findViewById(R.id.lname)
        gender=findViewById(R.id.gender)
        dob=findViewById(R.id.dob)
        contact=findViewById(R.id.contact)
        address=findViewById(R.id.address)
        sessionManager = SessionManager(this)

try{
        val progressDialog = ProgressDialog(this)
        // progressDialog.setTitle("Kotlin Progress Bar")
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        progressDialog.setCanceledOnTouchOutside(false)
        var mAPIService: ApiService? = null
        mAPIService = ApiClient.apiService
        sessionManager.fetchuserid()?.let {
            mAPIService!!.getprofile(
                    "Bearer "+ sessionManager.fetchAuthToken(),
                    it
            ).enqueue(object : Callback<profiledetail> {
                override fun onResponse(call: Call<profiledetail>, response: Response<profiledetail>) {
                    Log.i("", "post submitted to API." + response.body()!!)
                    if (response.isSuccessful()) {
                        Log.v("vvv", response.body().toString()!!)
                        var lo: profiledetail = response.body()!!
                        if (lo.status == 200) {
                            progressDialog.dismiss()
                            email.setText(lo.email)
                            name.setText(lo.first_name)
                            lname.setText(lo.last_name)
                            dob.setText(lo.dob)
                            contact.setText(lo.mobile)
                            gender.setText(lo.gender)

                        } else {
                            progressDialog.dismiss()
                          //  Toast.makeText(this@ProfileDetail, "not submiited", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onFailure(call: Call<profiledetail>, t: Throwable) {
                    Toast.makeText(this@ProfileDetail, t.message, Toast.LENGTH_SHORT).show()
                }
            })

        }

}catch (e:java.lang.Exception){

}
        }



    }
