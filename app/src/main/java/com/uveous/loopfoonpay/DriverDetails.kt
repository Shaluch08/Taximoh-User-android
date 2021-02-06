package com.uveous.loopfoonpay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.Driverdetail
import com.uveous.loopfoonpay.Api.Model.GetPrice
import com.uveous.loopfoonpay.Api.SessionManager
import com.uveous.taximohdriver.TravelDashboard
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverDetails :AppCompatActivity()
{
    private lateinit var sessionManager: SessionManager
    var requestid : Int = 0
    var a : Int = 0
    lateinit var total:TextView
    lateinit var destination:TextView
    lateinit var ok:TextView
    lateinit var originaddress:TextView
    lateinit var drivername:TextView
    lateinit var vechilename:TextView
    lateinit var vechileno:TextView
    lateinit var driverno:TextView
    lateinit var profile_image:CircleImageView
    lateinit var back: ImageView
    lateinit var sorrymessage: LinearLayout
    lateinit var showdetails: LinearLayout
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driverdetails)

        toolbar=findViewById(R.id.toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this,TravelDashboard::class.java))
        })


        sessionManager = SessionManager(this)
        requestid=intent.getIntExtra("requestid",0);

        total=findViewById(R.id.total)
        sorrymessage=findViewById(R.id.sorrymessage)
        showdetails=findViewById(R.id.showdetails)
        ok=findViewById(R.id.ok)
        destination=findViewById(R.id.destination)
        originaddress=findViewById(R.id.originaddress)
        drivername=findViewById(R.id.drivername)
        vechilename=findViewById(R.id.vechilename)
        vechileno=findViewById(R.id.vechileno)
        driverno=findViewById(R.id.driverno)
        profile_image=findViewById(R.id.profile_image)
        back=findViewById(R.id.back)

        back.setOnClickListener(View.OnClickListener {
            finish()
        })
        ok.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@DriverDetails,TravelDashboard::class.java))
        })

        if(a==0) {

            var mAPIService: ApiService? = null
            mAPIService = ApiClient.apiService
            mAPIService!!.getdriverdetails(
                    "Bearer " + sessionManager.fetchAuthToken(),
                    requestid, sessionManager.fetchuserid()!!
            ).enqueue(object : Callback<Driverdetail> {
                override fun onResponse(call: Call<Driverdetail>, response: Response<Driverdetail>) {
                    Log.i("", "post submitted to API." + response.body()!!)
                    if (response.isSuccessful()) {
                        Log.v("vvv", response.body().toString()!!)
                        var lo: Driverdetail = response.body()!!
                        if (lo.status == 200) {
                            a ++
                            sorrymessage.visibility = GONE
                            showdetails.visibility = VISIBLE
                            total.setText("Total Fare : " + lo.currency + lo.total_price)
                            destination.setText(lo.destination_address)
                            originaddress.setText(lo.origin_address)
                            drivername.setText(lo.driver_name)
                            vechilename.setText(lo.vehicle_name)
                            vechileno.setText(lo.vehicle_number)
                            driverno.setText(lo.driver_mobile)
                            Glide.with(this@DriverDetails).load(lo.profile_photo).into(profile_image)

                        } else {
                            /* sorrymessage.visibility=VISIBLE
                        showdetails.visibility= GONE*/
                        }

                    }
                }

                override fun onFailure(call: Call<Driverdetail>, t: Throwable) {
                    Toast.makeText(this@DriverDetails, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@DriverDetails,TravelDashboard::class.java))
    }
}