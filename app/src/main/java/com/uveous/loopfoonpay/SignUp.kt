package com.uveous.loopfoonpay

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.userlogin
import com.uveous.loopfoonpay.Api.Model.usersignup
import com.uveous.loopfoonpay.Api.SessionManager
import com.uveous.taximohdriver.TravelDashboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SignUp : AppCompatActivity() ,LocationListener{
    lateinit var dob: TextInputEditText
    lateinit var name: TextInputEditText
    lateinit var lname: TextInputEditText
    lateinit var email: TextInputEditText
    lateinit var phone: TextInputEditText
    lateinit var pwd: TextInputEditText
    lateinit var cpwd: TextInputEditText
    lateinit var radioGroup: RadioGroup
    private var mYear = 0
    private  var mMonth:Int = 0
    private  var mDay:Int = 0
    lateinit var gender :String
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        dob=findViewById(R.id.dob)
        name=findViewById(R.id.name)
        lname=findViewById(R.id.lname)
        email=findViewById(R.id.email)
        phone=findViewById(R.id.phone)
        pwd=findViewById(R.id.pwd)
        cpwd=findViewById(R.id.cpwd)
        radioGroup=findViewById(R.id.radioGroup)
        sessionManager = SessionManager(this)
        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    val radio: RadioButton = findViewById(checkedId)
                      gender=radio.text.toString()
                    Toast.makeText(applicationContext," On checked change :"+
                            " ${radio.text}",
                            Toast.LENGTH_SHORT).show()
                })


        dob.setOnClickListener(View.OnClickListener {
        val c: Calendar = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth -> dob.setText(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth) }, mYear, mMonth, mDay)
        datePickerDialog.show()

        })

        val signup=findViewById<Button>(R.id.signup)
        val login=findViewById<TextView>(R.id.login)

        signup.setOnClickListener(View.OnClickListener {
            signupfun()
           /* val Intent= Intent(this,TravelDashboard::class.java)
            startActivity(Intent)*/
        })
        login.setOnClickListener(View.OnClickListener {
            val Intent= Intent(this,LoginActivity::class.java)
            startActivity(Intent)
        })
    }

    fun signupfun(){
        if(email.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter email", Toast.LENGTH_SHORT).show()
        }else if(pwd.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter password", Toast.LENGTH_SHORT).show()
        }else if(phone.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter phone number", Toast.LENGTH_SHORT).show()
        }else if(name.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter first name", Toast.LENGTH_SHORT).show()
        }else if(lname.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter last name", Toast.LENGTH_SHORT).show()
        }else if(dob.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please select dob", Toast.LENGTH_SHORT).show()
        }else if(gender.isEmpty()){
            Toast.makeText(this@SignUp,"Please select gender", Toast.LENGTH_SHORT).show()
        }else if(cpwd.text.toString().isEmpty()){
            Toast.makeText(this@SignUp,"Please enter confirm password", Toast.LENGTH_SHORT).show()
        }else if(!cpwd.text.toString().equals(pwd.text.toString())){
            Toast.makeText(this@SignUp,"Please enter correct confirm password", Toast.LENGTH_SHORT).show()
        }else {
            try{
            val progressDialog = ProgressDialog(this@SignUp)
            // progressDialog.setTitle("Kotlin Progress Bar")
            progressDialog.setMessage("Please wait")
            progressDialog.show()
            progressDialog.setCanceledOnTouchOutside(false)
            var mAPIService: ApiService? = null
            mAPIService = ApiClient.apiService
            mAPIService!!.register(
                name.text.toString(), lname.text.toString(),
                phone.text.toString(),
                email.text.toString(),
                gender,
                dob.text.toString(),
                pwd.text.toString()
            ).enqueue(object : Callback<usersignup> {
                override fun onResponse(call: Call<usersignup>, response: Response<usersignup>) {
                    Log.i("", "post submitted to API." + response.body()!!)
                    if (response.isSuccessful()) {
                        var lo: usersignup = response.body()!!
                        if(lo.status==200){
                            lo.api_token?.let { sessionManager.saveAuthToken(it) }
                            lo.user_id?.let { sessionManager.saveuserid(it) }
                            lo.username?.let { sessionManager.savename(it) }
                            lo.mobile?.let { sessionManager.savenumber(it) }
                            Log.v("dd", sessionManager.fetchusername().toString())
                            progressDialog.dismiss()
                            val mPrefs: SharedPreferences = this@SignUp.getSharedPreferences(
                                    "TASK_ID",
                                    Context.MODE_PRIVATE
                            )
                            val prefsEditor = mPrefs.edit()
                            lo.user_id?.let { prefsEditor.putInt("userid", it) }
                            prefsEditor.commit()
                            Toast.makeText(this@SignUp, "Register", Toast.LENGTH_SHORT).show()
                            Log.v("dd", "post registration to API" + response.body()!!.toString())
                            val Intent = Intent(applicationContext, TravelDashboard::class.java)
                            startActivity(Intent)
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this@SignUp,lo.msg, Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onFailure(call: Call<usersignup>, t: Throwable) {
                    Toast.makeText(this@SignUp, t.message, Toast.LENGTH_SHORT).show()
                }
            })
            }catch (e:java.lang.Exception){

            }
        }

    }

    fun statusCheck() {
        val manager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }else{

        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        DialogInterface.OnClickListener { dialog, id ->
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        })
                .setNegativeButton("No",
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: android.app.AlertDialog? = builder.create()
        alert!!.show()
    }
    private fun fn_permission() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                        this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                ),
                        REQUEST_PERMISSIONS
                )
            }
        } else {
            boolean_permission = true
            fn_getlocation()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true
                    fn_getlocation()
                    statusCheck()
                } else {
                    statusCheck()
                    //Toast.makeText(HomeActivity.this, "Please allow the permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    var isGPSEnable = false
    var isNetworkEnable = false
    var latitude : Double= 0.0
    var longitude:Double = 0.0
    var locationManager: LocationManager? = null
    var location: Location? = null
    private val REQUEST_PERMISSIONS = 100

    private val MY_REQUEST = 1001
    var boolean_permission = false

    private fun fn_getlocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        isGPSEnable = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnable = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGPSEnable && !isNetworkEnable) {
        } else {
            if (isNetworkEnable) {
                location = null
                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,);

                if (locationManager != null) {
                    if (ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    location =
                            locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (location != null) {
                        Log.e("latitude", location!!.latitude.toString() + "")
                        Log.e("longitude", location!!.longitude.toString() + "")
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                        /*     setMarker()
                             startLatlng = LatLng(latitude, longitude)
                             if (marker == null) {
                                 place1 = MarkerOptions().position(startLatlng!!).title("Origin") //new LatLng(27.658143, 85.3199503)
                                 //new LatLng(27.667491, 85.3208583)
                                 marker = mMap!!.addMarker(place1)
                                 markers.add(marker!!)
                                 marker!!.showInfoWindow()
                                 *//*  int padding = 50;
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : markers) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);*//*
                            // mMap.moveCamera(cu);
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))
                        } else {
                            marker!!.setPosition(startLatlng!!)
                        }
*/
                    }
                }
            } else if (isGPSEnable) {
                location = null
                if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
                if (locationManager != null) {
                    location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location != null) {
                        Log.e("latitude", location!!.latitude.toString() + "")
                        Log.e("longitude", location!!.longitude.toString() + "")
                        latitude = location!!.latitude
                        longitude = location!!.longitude
                        // String distance=  UtilityFunction.calculateDistance(latitude,longitude,Double.parseDouble(locationPOJO.getLat()),Double.parseDouble(locationPOJO.getLong()),LocationManager.GPS_PROVIDER);

                        //   fn_update(location);
                        // getNearPlaces();
                        /*  startLatlng = LatLng(latitude, longitude)
                          setMarker()
                          if (marker == null) {
                              place1 = MarkerOptions().position(startLatlng!!).title("Origin") //new LatLng(27.658143, 85.3199503)
                             //new LatLng(27.667491, 85.3208583)
                              marker = mMap!!.addMarker(place1)
                              markers.add(marker!!)
                              marker!!.showInfoWindow()
                              *//*  int padding = 50;
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : markers) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);*//*
                            // mMap.moveCamera(cu);
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))
                        } else {
                            marker!!.setPosition(startLatlng!!)
                        }
*/
                    }
                }
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        if (location != null) {
            Log.e("latitude", location!!.latitude.toString() + "")
            Log.e("longitude", location!!.longitude.toString() + "")
            latitude = location!!.latitude
            longitude = location!!.longitude

        }
    }

}