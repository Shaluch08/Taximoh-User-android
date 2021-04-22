package com.uveous.loopfoonpay

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.saveride
import com.uveous.loopfoonpay.Api.SessionManager
import com.uveous.taximohdriver.TravelDashboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Ride :AppCompatActivity(){

    lateinit var name: TextInputEditText
    lateinit var phone: TextInputEditText
    lateinit var date: TextInputEditText
    lateinit var time: TextInputEditText
    lateinit var origin: TextInputEditText
    lateinit var destination: TextInputEditText
    lateinit var noofpassengers: TextInputEditText
    lateinit var selecttime: TextInputLayout
    lateinit var selectdate: TextInputLayout
    lateinit var submit: Button
    lateinit var originaddress : String
    lateinit var destinationaddress : String
    lateinit var provider : String
    lateinit var datetext : String
    lateinit var timetext : String
    lateinit var dist : String
     var passengerno : Int =0
     var type : Int=0
    var payment_id :Int = 0;
     var  originlongitude : Double=0.0
     var  originlatitude : Double=0.0
     var  destlatitude : Double=0.0
     var  destlongitude : Double=0.0
    private var mYear = 0
    private  var mMonth:Int = 0
    private  var mDay:Int = 0
    private lateinit var sessionManager: SessionManager
    var amPm: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ride)

        name = findViewById(R.id.name)
        phone = findViewById(R.id.phone)
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)
        selectdate = findViewById(R.id.selectdate)
        selecttime = findViewById(R.id.selecttime)
        origin = findViewById(R.id.origin)
        destination = findViewById(R.id.destination)
        noofpassengers = findViewById(R.id.noofpassengers)
        submit = findViewById(R.id.submit)
        originaddress = intent.getStringExtra("origin").toString()
        destinationaddress = intent.getStringExtra("destination").toString()
        provider = intent.getStringExtra("provider").toString()
        type = intent.getIntExtra("type", 0)

        if (type == 1) {
            date.visibility = VISIBLE
            selectdate.visibility = VISIBLE
            time.visibility = VISIBLE
            selecttime.visibility = VISIBLE
            noofpassengers.visibility = VISIBLE
        } else {
            date.visibility = GONE
            selecttime.visibility = GONE
            time.visibility = GONE
            selectdate.visibility = GONE
            noofpassengers.visibility = GONE
        }
        sessionManager = SessionManager(this)

        origin.setText(originaddress)
        destination.setText(destinationaddress)

        val coder = Geocoder(this)
        try {
            val adresses  = coder.getFromLocationName(originaddress, 50)

            val location: Address = adresses.get(0)
            originlatitude =  location.latitude
            originlongitude =  location.longitude

            val coder1 = Geocoder(this)
            val adresses1 = coder1.getFromLocationName(destinationaddress, 50) as java.util.ArrayList<Address>

            val location1: Address = adresses1.get(0)
            destlatitude =  location1.latitude
            destlongitude =  location1.longitude

            val loc1 = Location("one")
            loc1.setLatitude(originlatitude)
            loc1.setLongitude(originlongitude)
            val loc2 = Location("two")
            loc2.setLatitude(destlatitude)
            loc2.setLongitude(destlongitude)
            dist= loc1.distanceTo(loc2).toString()
          //  return distanceInMeters / 1000

            Log.v("distance",dist)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        name.setText(sessionManager.fetchusername())
        phone.setText(sessionManager.fetchuserno())
        date.setOnClickListener(View.OnClickListener {
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> date.setText(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth) }, mYear, mMonth, mDay)
            datePickerDialog.show()

        })


        time.setOnClickListener(View.OnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this@Ride, OnTimeSetListener { timePicker, hourOfDay, minutes ->
                if (hourOfDay >= 12) {
                    amPm = "PM"
                } else {
                    amPm = "AM"
                }
                time.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm)
            }, hour, minute, false)

            timePickerDialog.show()

        })

        submit.setOnClickListener(View.OnClickListener {

                    if (type == 1) {
                        datetext = date.text.toString()
                        timetext = time.text.toString()
                        val myStringNumber: String = noofpassengers.text.toString()
                        passengerno = myStringNumber.toInt()
                    } else {
                        val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        datetext = currentDate
                        timetext = currentTime
                        passengerno = 1
                    }

                Log.v("dd", sessionManager.fetchAuthToken().toString())
                Log.v("dd", type.toString())
                Log.v("dd",  originlongitude.toString())
                Log.v("dd",  originlatitude.toString())
                Log.v("dd", destlongitude.toString())
                Log.v("dd", destlatitude.toString())
                Log.v("dd", originaddress)
                Log.v("dd",destinationaddress)
                Log.v("dd", datetext)
                Log.v("dd", timetext)


                val progressDialog = ProgressDialog(this)
                    // progressDialog.setTitle("Kotlin Progress Bar")
                    progressDialog.setMessage("Please wait")
                    progressDialog.show()
                    progressDialog.setCanceledOnTouchOutside(false)
                    var mAPIService: ApiService? = null
                    mAPIService = ApiClient.apiService
                    sessionManager.fetchuserid()?.let { it1 ->
                    mAPIService!!.saveride(
                            "Bearer "+ sessionManager.fetchAuthToken(),
                            name.text.toString(),
                            phone.text.toString(),
                            datetext, timetext, type,passengerno,2,
                            originlongitude.toString(),
                            originlatitude.toString(),
                            destlongitude.toString(),
                            destlatitude.toString(),
                            origin.text.toString(),destination.text.toString(),
                            dist,payment_id.toString(),
                            it1
                    ).enqueue(object : Callback<saveride> {
                        override fun onResponse(call: Call<saveride>, response: Response<saveride>) {
                            Log.d("", "post submitted to API." + response.body()!!)
                            if (response.isSuccessful()) {
                                Log.d("vvv", response.body().toString()!!)
                                var lo: saveride = response.body()!!
                                if (lo.status == 200) {
                                    progressDialog.dismiss()
                                    showDialog()
                                   // Toast.makeText(this@Ride, "Save", Toast.LENGTH_SHORT).show()

                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(this@Ride, "not submiited", Toast.LENGTH_SHORT).show()
                                }

                            }
                        }

                        override fun onFailure(call: Call<saveride>, t: Throwable) {
                            Toast.makeText(this@Ride, t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }




        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@Ride,TravelDashboard::class.java))
    }

    private fun showDialog() {
        val dialog = Dialog(this@Ride)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.place_layout)
        val yesBtn = dialog.findViewById(R.id.tv_ok_thanks) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this,TravelDashboard::class.java))
        }

        dialog.show()

    }
}