package com.uveous.loopfoonpay

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.media.Image
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mindorks.example.ubercaranimation.util.MapUtils
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.TripDetailModel
import com.uveous.loopfoonpay.Api.Model.cancelride
import com.uveous.loopfoonpay.Api.Model.progressstatus
import com.uveous.loopfoonpay.Api.SessionManager
import com.uveous.taximohdriver.TravelDashboard
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
import java.util.HashMap

class TripDetail : AppCompatActivity(),OnMapReadyCallback{
    lateinit var toolbar: Toolbar
    lateinit var status: TextView
    lateinit var origin: TextView
    lateinit var destination: TextView
    lateinit var fare: TextView
    lateinit var tfare: TextView
    lateinit var faret: TextView
    lateinit var image: ImageView
    lateinit var tax: TextView
    lateinit var permile: TextView
    lateinit var basefare: TextView
    lateinit var vechileno: TextView
     var requestid: Int?=0
    private var mMap: GoogleMap? = null
    private lateinit var sessionManager: SessionManager
    var destLatLng1: LatLng? = null
    lateinit var lo : TripDetailModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tripdetail)

        toolbar=findViewById(R.id.toolbar)
        status=findViewById(R.id.status)
        origin=findViewById(R.id.origin)
        tax=findViewById(R.id.tax)
        image=findViewById(R.id.image)
        basefare=findViewById(R.id.basefare)
        permile=findViewById(R.id.permile)
        destination=findViewById(R.id.destination)
        vechileno=findViewById(R.id.vechileno)
        fare=findViewById(R.id.fare)
        tfare=findViewById(R.id.tfare)
        faret=findViewById(R.id.faret)
        requestid=intent.getIntExtra("requestid",0)
        sessionManager = SessionManager(this)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Trip::class.java))
        })

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        getTripDetail()
    }
    fun getTripDetail(){
        val progressDialog = ProgressDialog(this)
        // progressDialog.setTitle("Kotlin Progress Bar")
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        progressDialog.setCanceledOnTouchOutside(false)
        var mAPIService: ApiService? = null
        mAPIService = ApiClient.apiService
        mAPIService!!.getTripDetail(
                "Bearer "+ sessionManager.fetchAuthToken(),
                requestid!!,sessionManager.fetchuserid()!!
        ).enqueue(object : Callback<TripDetailModel> {
            override fun onResponse(call: Call<TripDetailModel>, response: Response<TripDetailModel>) {
                Log.i("", "post submitted to API." + response.body()!!)
                if (response.isSuccessful()) {
                    Log.v("vvv", response.body().toString()!!)
                   lo= response.body()!!
                    if (lo.status == 200) {
                        setMarkerOnMap()

                        progressDialog.dismiss()
                        status.text=lo.request_status
                        origin.text=lo.origin_address
                        destination.text=lo.destination_address
                        tax.text=lo.tax
                        basefare.text=lo.currency+lo.base_fare_price
                        permile.text=lo.currency+lo.km_price
                        faret.text=lo.currency+lo.total_trip_price

                        fare.text=lo.currency+lo.total_trip_price
                        tfare.text=lo.currency+lo.total_trip_price
                        if(lo.request_status!!.contentEquals("processing") || lo.request_status!!.contentEquals("completed")){
                            vechileno.text= lo.vehicle_name+","+lo.vehicle_number
                        }else{
                            vechileno.text=""
                        }
                        if(lo.category_name.equals("Car")){
                            image.setImageResource(R.drawable.car1)
                        }else{
                           image.setImageResource(R.drawable.motorbike)
                        }

                    } else {

                        progressDialog.dismiss()
                    }

                }
            }

            override fun onFailure(call: Call<TripDetailModel>, t: Throwable) {
                Toast.makeText(this@TripDetail, t.message, Toast.LENGTH_SHORT).show()
            }
        })

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
    var startLatlng1: LatLng? = null

    fun setMarkerOnMap() {
        try {

            val coder = Geocoder(this)

            val adresses  = coder.getFromLocationName(lo.origin_address, 50)

            val location: Address = adresses.get(0)
            originlatitude =  location.latitude
            originlongitude =  location.longitude

            startLatlng1= LatLng(originlatitude,originlongitude)

            val coder1 = Geocoder(this)
            val adresses1 = coder1.getFromLocationName(lo.destination_address, 50) as java.util.ArrayList<Address>

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

        } catch (e: Exception) {
            Log.d("MapException", e.message.toString())
        }
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor =
                BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap())
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


}
