package com.uveous.loopfoonpay

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.TripList
import com.uveous.loopfoonpay.Api.Model.cancelride
import com.uveous.loopfoonpay.Api.Model.profiledetail
import com.uveous.loopfoonpay.Api.Model.progressstatus
import com.uveous.loopfoonpay.Api.SessionManager
import com.uveous.taximohdriver.TravelDashboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Trip : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var recyclerview: RecyclerView
    private lateinit var sessionManager: SessionManager
    private lateinit var tripListAdapter: TripListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trip)

        toolbar = findViewById(R.id.toolbar)
        recyclerview = findViewById(R.id.recyclerview)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this, TravelDashboard::class.java))
        })
        sessionManager = SessionManager(this)

        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerview.layoutManager = layoutManager
        recyclerview.itemAnimator = DefaultItemAnimator()
        getTripDetail()
    }

    private fun getTripDetail() {
        val progressDialog = ProgressDialog(this)
        // progressDialog.setTitle("Kotlin Progress Bar")
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        progressDialog.setCanceledOnTouchOutside(false)
        var mAPIService: ApiService? = null
        mAPIService = ApiClient.apiService
        sessionManager.fetchuserid()?.let {
            mAPIService!!.getTripList(
                    "Bearer "+ sessionManager.fetchAuthToken(),
                    it
            ).enqueue(object : Callback<TripList> {
                override fun onResponse(call: Call<TripList>, response: Response<TripList>) {
                    Log.i("", "post submitted to API." + response.body()!!)
                    if (response.isSuccessful()) {
                        Log.v("vvv", response.body().toString()!!)
                        var lo: TripList = response.body()!!
                        if (lo.status == 200) {
                            progressDialog.dismiss()
                            tripListAdapter = TripListAdapter(lo.result,lo.currency,this@Trip)
                            recyclerview.adapter = tripListAdapter
                        } else {
                            progressDialog.dismiss()
                            //  Toast.makeText(this@ProfileDetail, "not submiited", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onFailure(call: Call<TripList>, t: Throwable) {
                    Toast.makeText(this@Trip, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}