package com.uveous.loopfoonpay

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mindorks.example.ubercaranimation.util.MapUtils
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.*
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
import java.text.SimpleDateFormat
import java.util.*

class RequestRide :AppCompatActivity(), OnMapReadyCallback
{
    private var pDialog: ProgressDialog? = null

    // Progress dialog type (0 - for Horizontal progress bar)
    val progress_bar_type = 0
    var destLatLng1: LatLng? = null

    public lateinit var infocar: ImageView
    public lateinit var infobike: ImageView
    public lateinit var timedate: ImageView
    public lateinit var linearcar: LinearLayout
    public lateinit var linearbike: LinearLayout
    public lateinit var progress: LinearLayout
    public lateinit var carprice: TextView
    public lateinit var bikeprice: TextView
    public lateinit var request: TextView
    public lateinit var textcar: TextView
    public lateinit var originaddress1: TextView
    public lateinit var destination: TextView
    public lateinit var datetime: TextView
    public lateinit var totalFare: AppCompatButton
    public lateinit var cancel: AppCompatButton
    public lateinit var progressbar: ProgressBar
    var distance : String =""
    private var mMap: GoogleMap? = null
    private lateinit var sessionManager: SessionManager
    var PROVIDER = ""
    var type :Int = 0
    lateinit var sheet_request_trip: LinearLayout
    lateinit var lo: GetPrice
    lateinit var lo1: saveride
    lateinit var originaddress : String
    lateinit var destinationaddress : String
    lateinit var back:ImageView
    var startLatlng1: LatLng? = null
    lateinit var datetext : String
    private var a = 0
    lateinit var timetext : String
    var status1 = 0
    var timer = 0
    val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.requestride)

        sessionManager = SessionManager(this)

        originaddress = intent.getStringExtra("origin").toString()
        destinationaddress = intent.getStringExtra("destination").toString()
        distance = intent.getStringExtra("distance").toString()

        back=findViewById(R.id.back)
        initView()

        back.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    private fun initView(){

        sheet_request_trip=findViewById(R.id.sheet_request_trip)
        linearcar=findViewById(R.id.linearcar)
        linearbike=findViewById(R.id.linearbike)
        progress=findViewById(R.id.progress)
        carprice=findViewById(R.id.carprice)
        bikeprice=findViewById(R.id.bikeprice)
        request=findViewById(R.id.request)
        infocar=findViewById(R.id.infocar)
        infobike=findViewById(R.id.infobike)
        timedate=findViewById(R.id.timedate)
        textcar=findViewById(R.id.textcar)
        originaddress1=findViewById(R.id.originaddress)
        destination=findViewById(R.id.destination)
        datetime=findViewById(R.id.datetime)
        totalFare=findViewById(R.id.totalFare)
        cancel=findViewById(R.id.cancel)
        progressbar=findViewById(R.id.progressbar)
         originaddress1.setText(originaddress)
        destination.setText(destinationaddress)

        timedate.setOnClickListener(View.OnClickListener {
            datePicker()
        })

        linearcar.setOnTouchListener(View.OnTouchListener { view, motionEvent -> // Show an alert dialog.
            linearcar.setBackgroundColor(Color.parseColor("#DCDCDC"));
            linearbike.setBackgroundColor(Color.parseColor("#ffffff"));
            type = 1
            false
        })

        linearbike.setOnTouchListener(View.OnTouchListener { view, motionEvent -> // Show an alert dialog.
            linearbike.setBackgroundColor(Color.parseColor("#DCDCDC"));
            linearcar.setBackgroundColor(Color.parseColor("#ffffff"));
            type = 2
            false
        })



        infobike.setOnClickListener(View.OnClickListener {
            val dialog = Dialog(this@RequestRide)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.pricedetails)
            val yesBtn = dialog.findViewById(R.id.tv_ok_thanks) as TextView
            val price = dialog.findViewById(R.id.price) as TextView
            val base = dialog.findViewById(R.id.base) as TextView
            val fare = dialog.findViewById(R.id.fare) as TextView
            val tax = dialog.findViewById(R.id.tax) as TextView
            val distance = dialog.findViewById(R.id.distance) as TextView

            price.setText(lo.currency+lo.bike.totalPrice)
            base.setText(lo.currency+lo.bike.baseFare)
            fare.setText(lo.currency+lo.bike.prKM)
            tax.setText(lo.bike.tax)
            distance.setText(lo.distance)
            yesBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        })

        infocar.setOnClickListener(View.OnClickListener {
            val dialog = Dialog(this@RequestRide)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.pricedetails)
            val yesBtn = dialog.findViewById(R.id.tv_ok_thanks) as TextView
            val price = dialog.findViewById(R.id.price) as TextView
            val base = dialog.findViewById(R.id.base) as TextView
            val fare = dialog.findViewById(R.id.fare) as TextView
            val tax = dialog.findViewById(R.id.tax) as TextView
            val distance = dialog.findViewById(R.id.distance) as TextView

            price.setText(lo.currency+lo.car.totalPrice)
            base.setText(lo.currency+lo.car.baseFare)
            fare.setText(lo.currency+lo.car.prKM)
            tax.setText(lo.car.tax)
            distance.setText(lo.distance)
            yesBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        })



        request.setOnClickListener(View.OnClickListener {
                 if(type==0){
                     Toast.makeText(this,"Please select type",Toast.LENGTH_LONG).show()
                 }else{

                   /*  pDialog =  ProgressDialog(this);
                     pDialog!!.setMessage("Please wait while booking...");
                     pDialog!!.setIndeterminate(false);
                     pDialog!!.setMax(100);
                     pDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                     pDialog!!.setCancelable(true);
                     pDialog!!.show();*/
                     Log.v("dest",distance)


                     /* val progressDialog = ProgressDialog(this)
                      // progressDialog.setTitle("Kotlin Progress Bar")
                      progressDialog.setMessage("Please wait")
                      progressDialog.show()
                      progressDialog.setCanceledOnTouchOutside(false)*/
                     var mAPIService: ApiService? = null
                     mAPIService = ApiClient.apiService
                     sessionManager.fetchuserid()?.let { it1 ->
                         mAPIService!!.saveride(
                                 "Bearer "+ sessionManager.fetchAuthToken(),
                                sessionManager.fetchusername()!!,
                                sessionManager.fetchuserno()!!,
                                 datetext, timetext, type,1,
                                 originlongitude.toString(),
                                 originlatitude.toString(),
                                 destlongitude.toString(),
                                 destlatitude.toString(),
                                 originaddress1.text.toString(),destination.text.toString(),
                                 distance,
                                 it1
                         ).enqueue(object : Callback<saveride> {
                             override fun onResponse(call: Call<saveride>, response: Response<saveride>) {
                                 Log.i("", "post submitted to API." + response.body()!!)
                                 if (response.isSuccessful()) {
                                     Log.v("vvv", response.body().toString()!!)
                                     lo1 = response.body()!!
                                     if (lo1.status == 200) {
                                         if(type==1){
                                             totalFare.setText("Total Fare : "+lo.currency+lo.car.totalPrice)
                                         }else{
                                             totalFare.setText("Total Fare : "+lo.currency+lo.bike.totalPrice)
                                         }
                                         if(lo1.wait_time.contentEquals("current")){
                                             progress.visibility=VISIBLE
                                             sheet_request_trip.visibility=GONE
                                             back.visibility=GONE
                                             //getProgressStatus()

//                                             val millis: Long = lo1.wait_time.toLong() * 60 * 1000
                                             Thread(Runnable {
                                                 while (status1 < 100) {
                                                     status1 += 1
                                                     try {
                                                         Thread.sleep(5000)
                                                     } catch (e: InterruptedException) {
                                                         e.printStackTrace()
                                                     }
                                                     handler.post(Runnable {
                                                           timer= timer + 5
                                                           getProgressStatus()
                                                           progressbar.setProgress(status1)

                                                     })
                                                 }
                                             }).start()
                                         }else{
                                             showDialog("Please check ride details at your schedule time.")
                                         }


                                     } else {
                                        // pDialog!!.dismiss()
                                      //   progressDialog.dismiss()
                                         Toast.makeText(this@RequestRide, "not submiited", Toast.LENGTH_SHORT).show()
                                     }

                                 }
                             }

                             override fun onFailure(call: Call<saveride>, t: Throwable) {
                                 Toast.makeText(this@RequestRide, t.message, Toast.LENGTH_SHORT).show()
                             }
                         })
                     }




                 }
        })
        cancel.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert").setMessage("Are you want to cancel the ride")
            builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }.
            setPositiveButton("Yes") { dialog, which -> // Constants.ACTIVITY_NAME=Constants.HOME_ACTIVITY;

                val progressDialog = ProgressDialog(this@RequestRide)
                // progressDialog.setTitle("Kotlin Progress Bar")
                progressDialog.setMessage("Please wait")
                progressDialog.show()
                progressDialog.setCanceledOnTouchOutside(false)
                var mAPIService: ApiService? = null
                mAPIService = ApiClient.apiService
                mAPIService!!.cancel("Bearer "+ sessionManager.fetchAuthToken(),lo1.request_id, sessionManager.fetchuserid()!!).enqueue(object :
                    Callback<cancelride> {
                    override fun onResponse(call: Call<cancelride>, response: Response<cancelride>) {
                        Log.i("", "post submitted to API." + response.body()!!)
                        if (response.isSuccessful()) {
                            var lo: cancelride = response.body()!!
                            if(lo.status==200){
                                progressDialog.dismiss()
                                Toast.makeText(this@RequestRide,"Your request is cancel.", Toast.LENGTH_SHORT).show()

                                finish()
                            }else{
                                progressDialog.dismiss()
                                Toast.makeText(this@RequestRide,"You can't cancel now.", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }

                    override fun onFailure(call: Call<cancelride>, t: Throwable) {
                        Toast.makeText(this@RequestRide, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }

            val dialog = builder.create()
            dialog.show()

        })

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        senddistance()
        datetext = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        timetext = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        datetime.setText(datetext +" , "+ timetext)
    }

   fun getdriverdetail(){
       var mAPIService: ApiService? = null
       mAPIService = ApiClient.apiService
       mAPIService!!.getdriverdetails(
           "Bearer "+ sessionManager.fetchAuthToken(),
           lo1.request_id,sessionManager.fetchuserid()!!
       ).enqueue(object : Callback<Driverdetail> {
           override fun onResponse(call: Call<Driverdetail>, response: Response<Driverdetail>) {
               Log.i("", "post submitted to API." + response.body()!!)
               if (response.isSuccessful()) {
                   Log.v("vvv", response.body().toString()!!)
                   var lo : Driverdetail = response.body()!!
                   if (lo.status == 200) {
                       val intent = Intent(this@RequestRide,DriverDetails::class.java).putExtra("requestid",lo1.request_id)
                       intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                       startActivity(intent)
                   } else {

                       var mAPIService: ApiService? = null
                       mAPIService = ApiClient.apiService
                       mAPIService!!.cancel("Bearer "+ sessionManager.fetchAuthToken(),lo1.request_id, sessionManager.fetchuserid()!!).enqueue(object :
                               Callback<cancelride> {
                           override fun onResponse(call: Call<cancelride>, response: Response<cancelride>) {
                               Log.i("", "post submitted to API." + response.body()!!)
                               if (response.isSuccessful()) {
                                   var lo: cancelride = response.body()!!

                               }
                           }

                           override fun onFailure(call: Call<cancelride>, t: Throwable) {
                               Toast.makeText(this@RequestRide, t.message, Toast.LENGTH_SHORT).show()
                           }
                       })

                       showDialog("No drivers available in your area.Please check after sometime.")
                   }

               }
           }

           override fun onFailure(call: Call<Driverdetail>, t: Throwable) {
               Toast.makeText(this@RequestRide, t.message, Toast.LENGTH_SHORT).show()
           }
       })

   }


    fun getProgressStatus(){
       var mAPIService: ApiService? = null
       mAPIService = ApiClient.apiService
       mAPIService!!.getstatus(
           "Bearer "+ sessionManager.fetchAuthToken(),
           lo1.request_id,sessionManager.fetchuserid()!!
       ).enqueue(object : Callback<progressstatus> {
           override fun onResponse(call: Call<progressstatus>, response: Response<progressstatus>) {
               Log.i("", "post submitted to API." + response.body()!!)
               if (response.isSuccessful()) {
                   Log.v("vvv", response.body().toString()!!)
                   var lo : progressstatus = response.body()!!
                   if (lo.status == 200) {
                       if(lo.request_status.contentEquals("accepted")){
                           progressbar.setProgress(100)
                           status1=100
                           progress.visibility= GONE
                           if(a==0) {
                               getdriverdetail()
                               a++
                               handler.removeCallbacksAndMessages(null)
                           }
                       }else{

                            if(timer==90){
                                handler.removeCallbacksAndMessages(null)
                                progress.visibility= GONE
                                status1=100

                                var mAPIService: ApiService? = null
                                mAPIService = ApiClient.apiService
                                mAPIService!!.cancel("Bearer "+ sessionManager.fetchAuthToken(),lo1.request_id, sessionManager.fetchuserid()!!).enqueue(object :
                                        Callback<cancelride> {
                                    override fun onResponse(call: Call<cancelride>, response: Response<cancelride>) {
                                        Log.i("", "post submitted to API." + response.body()!!)
                                        if (response.isSuccessful()) {
                                            var lo: cancelride = response.body()!!

                                        }
                                    }

                                    override fun onFailure(call: Call<cancelride>, t: Throwable) {
                                        Toast.makeText(this@RequestRide, t.message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                                showDialog("No drivers available in your area.Please check after sometime.")
                            }
                       }

                   } else {

                   }

               }
           }

           override fun onFailure(call: Call<progressstatus>, t: Throwable) {
               Toast.makeText(this@RequestRide, t.message, Toast.LENGTH_SHORT).show()
           }
       })

   }
    private fun showDialog(text:String) {
        val dialog = Dialog(this@RequestRide)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.place_layout)
        val yesBtn = dialog.findViewById(R.id.tv_ok_thanks) as TextView
        val tv_message_thanks = dialog.findViewById(R.id.tv_message_thanks) as TextView
        tv_message_thanks.setText(text)
        yesBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@RequestRide, TravelDashboard::class.java)
            //    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
         //   finish()

        }

        dialog.show()

    }

    override fun onRestart() {
        super.onRestart()
        sheet_request_trip.visibility= VISIBLE
        progress.visibility= GONE
    }

    private fun datePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val formatDate=(year.toString() + "-" + (month + 1) + "-" + dayOfMonth)
                //tvSelectDate.setText(formatDate)
                timePicker(formatDate)
                datetext=formatDate
            }
        }, year, month, dayOfMonth)
        calendar.add(Calendar.MONTH, 1)
        val now = System.currentTimeMillis() - 1000
        val maxDate = calendar.timeInMillis
        datePickerDialog.getDatePicker().setMinDate(now)
        datePickerDialog.getDatePicker().setMaxDate(maxDate) //After one month from now
        datePickerDialog.show()
    }
    var amPm: String? = null

    private fun timePicker(date: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(this@RequestRide, TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minutes ->
            if (hourOfDay >= 12) {
                amPm = "PM"
            } else {
                amPm = "AM"
            }
            timetext=String.format("%02d:%02d", hourOfDay, minutes) + amPm
            datetime.setText(date +" , "+ String.format("%02d:%02d", hourOfDay, minutes) + amPm)
        }, hour, minute, true)

        timePickerDialog.show()


    }
    private fun senddistance() {

        val progressDialog = ProgressDialog(this)
        // progressDialog.setTitle("Kotlin Progress Bar")
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        progressDialog.setCanceledOnTouchOutside(false)
        var mAPIService: ApiService? = null
        mAPIService = ApiClient.apiService
        mAPIService!!.senddistance(
                "Bearer "+ sessionManager.fetchAuthToken(),
                distance
        ).enqueue(object : Callback<GetPrice> {
            override fun onResponse(call: Call<GetPrice>, response: Response<GetPrice>) {
                Log.i("", "post submitted to API." + response.body()!!)
                if (response.isSuccessful()) {
                    Log.v("vvv", response.body().toString()!!)
                    lo = response.body()!!
                    if (lo.status == 200) {
                        carprice.setText(lo.currency+lo.car.totalPrice)
                        bikeprice.setText(lo.currency+lo.bike.totalPrice)
                        /*     distance1.setText(lo.distance)
                             distance2.setText(lo.distance)*/
                        progressDialog.dismiss()
                        setMarkerOnMap()
                    } else {
                        progressDialog.dismiss()
                        //  Toast.makeText(this@ProfileDetail, "not submiited", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onFailure(call: Call<GetPrice>, t: Throwable) {
                Toast.makeText(this@RequestRide, t.message, Toast.LENGTH_SHORT).show()
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
    fun setMarkerOnMap() {
        try {

              val coder = Geocoder(this)

                        val adresses  = coder.getFromLocationName(originaddress, 50)

                        val location: Address = adresses.get(0)
                        originlatitude =  location.latitude
                        originlongitude =  location.longitude

                        startLatlng1= LatLng(originlatitude,originlongitude)

                        val coder1 = Geocoder(this)
                        val adresses1 = coder1.getFromLocationName(destinationaddress, 50) as java.util.ArrayList<Address>

                        val location1: Address = adresses1.get(0)
                        destlatitude =  location1.latitude
                        destlongitude =  location1.longitude
                        destLatLng1= LatLng(destlatitude,destlongitude)

                        //  return distanceInMeters / 1000

                        Log.v("originaddress",startLatlng1!!.latitude.toString())
                        Log.v("originaddress",startLatlng1!!.longitude.toString())
                        Log.v("originaddress",destLatLng1!!.latitude.toString())
                        Log.v("originaddress",destLatLng1!!.longitude.toString())
                        Log.v("dest",destinationaddress)



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


    override fun onBackPressed() {
     /*   super.onBackPressed()*/
        moveTaskToBack(false);

    }

}