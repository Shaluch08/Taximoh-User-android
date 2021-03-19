package com.uveous.loopfoonpay

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.mindorks.example.ubercaranimation.util.MapUtils
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.Driverdetail
import com.uveous.loopfoonpay.Api.Model.cancelride
import com.uveous.loopfoonpay.Api.SessionManager
import com.uveous.taximohdriver.TravelDashboard
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class DriverDetails :AppCompatActivity(), OnMapReadyCallback , LocationListener
{
    private lateinit var sessionManager: SessionManager
    var requestid : Int = 0
    var driverid : Int = 0
    var destinationadd : String = ""
    var a : Int = 0
    lateinit var total:TextView
    lateinit var destination:TextView
    lateinit var originaddress:TextView
    lateinit var simpleRatingBar:RatingBar
    lateinit var drivername:TextView
    lateinit var drivername1:TextView
    lateinit var vechilename:TextView
    lateinit var vechileno:TextView
    lateinit var driverno:TextView
    lateinit var driverno1:TextView
    lateinit var submit:TextView
    lateinit var et_post:EditText
    lateinit var otp:TextView
    lateinit var ok:TextView
    lateinit var profile_image:CircleImageView
    lateinit var profile_image1:CircleImageView
    lateinit var back: ImageView
    lateinit var showdetails: LinearLayout
    lateinit var phone: LinearLayout
    lateinit var showratings: LinearLayout
    lateinit var toolbar: Toolbar
    private var mMap: GoogleMap? = null
    var startLatlng1: LatLng? = null
    var destLatLng1: LatLng? = null
    var distance = 0.0
    val DISTANCE = 100.0
    lateinit var lo: Driverdetail
    private var mFirebaseDatabase: DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driverdetails)

   /*     toolbar=findViewById(R.id.toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this,TravelDashboard::class.java))
        })

*/
        sessionManager = SessionManager(this)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fn_getlocation()
        requestid=intent.getIntExtra("requestid",0);
        destinationadd=intent.getStringExtra("destinationadd").toString()
        total=findViewById(R.id.total)
        showdetails=findViewById(R.id.showdetails)
        phone=findViewById(R.id.phone)
        destination=findViewById(R.id.destination)
        originaddress=findViewById(R.id.originaddress)
        simpleRatingBar=findViewById(R.id.simpleRatingBar)
        drivername=findViewById(R.id.drivername)
        drivername1=findViewById(R.id.drivername1)
        vechilename=findViewById(R.id.vechilename)
        vechileno=findViewById(R.id.vechileno)
        driverno=findViewById(R.id.driverno)
        driverno1=findViewById(R.id.driverno1)
        submit=findViewById(R.id.submit)
        et_post=findViewById(R.id.et_post)
        otp=findViewById(R.id.otp)
        ok=findViewById(R.id.ok)
        profile_image=findViewById(R.id.profile_image)
        profile_image1=findViewById(R.id.profile_image1)
        back=findViewById(R.id.back)
        showratings=findViewById(R.id.showratings)

        back.setOnClickListener(View.OnClickListener {
          startActivity(Intent(this,TravelDashboard::class.java))
        })
        ok.setOnClickListener(View.OnClickListener {
            showratings.visibility= VISIBLE
            showdetails.visibility= GONE
        })
          phone.setOnClickListener(View.OnClickListener {
              val dialIntent = Intent(Intent.ACTION_DIAL)
              dialIntent.data = Uri.parse("tel:" + driverno.text.toString())
              startActivity(dialIntent)

          })

        submit.setOnClickListener(View.OnClickListener {

            try{
            val progressDialog = ProgressDialog(this@DriverDetails)
            // progressDialog.setTitle("Kotlin Progress Bar")
            progressDialog.setMessage("Please wait")
            progressDialog.show()
            progressDialog.setCanceledOnTouchOutside(false)
            var mAPIService: ApiService? = null
            mAPIService = ApiClient.apiService
            mAPIService!!.review("Bearer "+ sessionManager.fetchAuthToken(),requestid, sessionManager.fetchuserid()!!,simpleRatingBar.getRating().toString(),et_post.text.toString()).enqueue(object :
                Callback<cancelride> {
                override fun onResponse(call: Call<cancelride>, response: Response<cancelride>) {
                    Log.i("", "post submitted to API." + response.body()!!)
                    if (response.isSuccessful()) {
                        var lo: cancelride = response.body()!!
                        if(lo.status==200){
                            progressDialog.dismiss()
                            Toast.makeText(this@DriverDetails,"Your Rating is submitted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@DriverDetails,TravelDashboard::class.java))
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this@DriverDetails,"Your Rating is not submitted", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onFailure(call: Call<cancelride>, t: Throwable) {
                    Toast.makeText(this@DriverDetails, t.message, Toast.LENGTH_SHORT).show()
                }
            })
            }catch (e:java.lang.Exception){

            }
        })
        if(a==0) {

            try{
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
                     lo = response.body()!!
                        if (lo.status == 200) {
                            a ++
                            showdetails.visibility = VISIBLE
                            total.setText("Total Fare : " + lo.currency + lo.total_price)
                            destination.setText(lo.destination_address)
                            originaddress.setText(lo.origin_address)
                            drivername.setText(lo.driver_name)
                            drivername1.setText(lo.driver_name)
                            vechilename.setText(lo.vehicle_name)
                            vechileno.setText(lo.vehicle_number)
                            driverno.setText(lo.driver_mobile)
                            driverno1.setText(lo.driver_mobile)
                            otp.setText(lo.valid_otp)
                            driverid= lo.driver_id!!
                            Glide.with(this@DriverDetails).load(lo.profile_photo).into(profile_image)
                            Glide.with(this@DriverDetails).load(lo.profile_photo).into(profile_image1)
                            setMarkerOnMap()
                        } else {
                            showdetails.visibility= GONE
                            /* sorrymessage.visibility=VISIBLE
                        showdetails.visibility= GONE*/
                        }

                    }
                }

                override fun onFailure(call: Call<Driverdetail>, t: Throwable) {
                    Toast.makeText(this@DriverDetails, t.message, Toast.LENGTH_SHORT).show()
                }
            })
            }catch (e:java.lang.Exception){

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@DriverDetails,TravelDashboard::class.java))
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap!!.getUiSettings().setZoomControlsEnabled(true)
    }
    var  originlongitude : Double=0.0
    var  originlatitude : Double=0.0
    var  destlatitude : Double=0.0
    var  destlongitude : Double=0.0
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    fun setMarkerOnMap() {
        try {

            val coder = Geocoder(this)

            val adresses  = coder.getFromLocationName(originaddress.text.toString(), 50)

            val location: Address = adresses.get(0)
            originlatitude =  location.latitude
            originlongitude =  location.longitude

            startLatlng1= LatLng(originlatitude,originlongitude)

            val coder1 = Geocoder(this)
            val adresses1 = coder1.getFromLocationName(destination.text.toString(), 50) as java.util.ArrayList<Address>

            val location1: Address = adresses1.get(0)
            destlatitude =  location1.latitude
            destlongitude =  location1.longitude
            destLatLng1= LatLng(destlatitude,destlongitude)

            //  return distanceInMeters / 1000

            Log.v("originaddress",startLatlng1!!.latitude.toString())
            Log.v("originaddress",startLatlng1!!.longitude.toString())
            Log.v("originaddress",destLatLng1!!.latitude.toString())
            Log.v("originaddress",destLatLng1!!.longitude.toString())



            originMarker = addOriginDestinationMarkerAndGet(startLatlng1!!)
            originMarker?.setAnchor(0.5f, 0.5f)
            destinationMarker = addOriginDestinationMarkerAndGet(destLatLng1!!)
            destinationMarker?.setAnchor(0.5f, 0.5f)

            moveCamera(startLatlng1!!)
            animateCamera(startLatlng1!!)

            // Getting URL to the Google Directions API
            val url: String? = getUrl(startLatlng1!!, destLatLng1!!)
            Log.d("onMapClick", url.toString())
            val FetchUrl: FetchUrl = FetchUrl()

            FetchUrl.execute(url)
            //getDriver()

        } catch (e: Exception) {
            Log.d("MapException", e.message.toString())
        }
    }

    lateinit var Latlng1: LatLng
    private fun getDriver(){
        mFirebaseDatabase = FirebaseDatabase.getInstance()
            .getReference("location/$sessionManager.fetchuserid()-${requestid}-${driverid}")

        mFirebaseDatabase!!.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                //  MessageData messages = (MessageData) dataSnapshot.getValue(MessageData.class);

                val map: MutableMap<*, *>? = dataSnapshot.getValue(MutableMap::class.java)
                val latitude:Double = map!!["latitude"] as Double
                val longitude:Double = map["longitude"] as Double
                Log.v("lat",latitude.toString())
                Log.v("lon",longitude.toString())
                Latlng1=LatLng(latitude,longitude)
                addMarker(Latlng1)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }



        })



    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor =
                BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap())
        return mMap!!.addMarker(
                MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }
    private fun addMarker(latLng: LatLng): Marker? {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(this))
        return mMap!!.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun moveCamera(latLng: LatLng) {
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }



    private fun getUrl(origin: LatLng, dest: LatLng): String? {

        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        val sensor = "sensor=false"

        val parameters = "$str_origin&$str_dest&$sensor"

        val output = "json"

        // Building the url to the web service
        return ("https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters
                + "&key=" + "AIzaSyD8UzxFsaAYwR0ZenE3v_zAg10ZuPov72w")
    }

    private inner class FetchUrl : AsyncTask<String?, Void?, String>() {
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask:ParserTask = ParserTask()

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)
        }

        override fun doInBackground(vararg url: String?): String {
            var data = ""
            try {
                // Fetching the data from web service
                data = url[0]?.let { downloadUrl(it) }!!
                Log.e("Background Task data", data)
            } catch (e: java.lang.Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

    }
    @Throws(IOException::class)
    private  fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream = urlConnection!!.inputStream
            val br =
                    BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            Log.d("downloadUrl", data)
            br.close()
        } catch (e: java.lang.Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    private inner class ParserTask : AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {
        var lineOptions: PolylineOptions? = null
        var startTrack = false
        var points: java.util.ArrayList<LatLng>? = null
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {

            lineOptions = null

            // Traversing through all the routes
            for (i in result!!.indices) {
                points = java.util.ArrayList<LatLng>()
                lineOptions = PolylineOptions()

                // Fetching i-th route
                val path =
                        result[i]

                // Fetching all the points in i-th route
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points!!.add(position)
                }

                // Adding all the points in the route to LineOptions
                lineOptions!!.addAll(points)
                lineOptions!!.width(10f)
                lineOptions!!.color(Color.BLACK)
                Log.d("onPostExecute", "onPostExecute lineoptions decoded")
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap!!.addPolyline(lineOptions)
            } else {
                Log.d("onPostExecute", "without Polylines drawn")
            }
        }

        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? =
                    null
            try {
                jObject = JSONObject(jsonData[0])
                Log.d("ParserTask", jsonData[0].toString())
                val parser = DirectionsJSONParser()
                Log.d("ParserTask", parser.toString())

                // Starts parsing data
                routes = parser.parse(jObject)
                Log.d("ParserTask", "Executing routes")
                Log.d("ParserTask", routes.toString())
            } catch (e: java.lang.Exception) {
                Log.d("ParserTask", e.toString())
                e.printStackTrace()
            }
            return routes
        }
    }

    var isGPSEnable = false
    var isNetworkEnable = false
    var latitude : Double= 0.0
    var longitude:Double = 0.0
    var locationManager: LocationManager? = null
    var location: Location? = null
    var  destlatitude1 : Double=0.0
    var  destlongitude1 : Double=0.0

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

                    }
                }
            }
        }
    }


    override fun onLocationChanged(location: Location) {
        Log.e("latitude", location!!.latitude.toString() + "")
        Log.e("longitude", location!!.longitude.toString() + "")
        latitude = location!!.latitude
        longitude = location!!.longitude

        val coder1 = Geocoder(this)
        val adresses1 = coder1.getFromLocationName(destinationadd, 50) as java.util.ArrayList<Address>

        val location1: Address = adresses1.get(0)
        destlatitude1 =  location1.latitude
        destlongitude1 =  location1.longitude

        val loc3 = Location("one")
        loc3.setLatitude(latitude)
        loc3.setLongitude(longitude)
        val loc4 = Location("two")
        loc4.setLatitude(destlatitude1)
        loc4.setLongitude(destlongitude1)
        distance= loc3.distanceTo(loc4).toDouble()

        if(distance<=DISTANCE){
            showratings.visibility= VISIBLE
            showdetails.visibility= GONE
        }
    }

    override fun onResume() {
        super.onResume()
        //getDriver()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}