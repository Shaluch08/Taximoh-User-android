package com.uveous.taximohdriver

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.navigation.NavigationView
import com.uveous.loopfoonpay.*
import com.uveous.loopfoonpay.Api.Model.GetPrice
import com.uveous.loopfoonpay.Api.SessionManager
import com.uveous.loopfoonpay.directionhelpers.TaskLoadedCallback
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.util.*

class TravelDashboard : AppCompatActivity()  , LocationListener, OnMapReadyCallback, TaskLoadedCallback {

    var navigationPosition: Int = 0
    lateinit var drawerLayout : DrawerLayout;
    lateinit var toolbar : Toolbar
    lateinit var navigationView: NavigationView
    private lateinit var etpickup : TextView
    public lateinit var destination: TextView
    public lateinit var car: ImageView
    public lateinit var bike: ImageView
    public lateinit var infocar: ImageView
    public lateinit var infobike: ImageView
    public lateinit var cardcar: CardView
    public lateinit var cardbike: CardView
    public lateinit var ridenow: LinearLayout
    public lateinit var linearcar: LinearLayout
    public lateinit var linearbike: LinearLayout
    public lateinit var carprice: TextView
    public lateinit var bikeprice: TextView
    public lateinit var request: TextView
    public lateinit var distance1: TextView
    public lateinit var distance2: TextView
    public lateinit var textcar: TextView
    var distance : String =""
    private var mMap: GoogleMap? = null
    private lateinit var sessionManager: SessionManager
    var PROVIDER = ""
    var type :Int = 0
    lateinit var sheet_request_trip: LinearLayout
    lateinit var lo: GetPrice
    companion object{

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.travel_dashboard)
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key))
        }
        val placesClient: PlacesClient = Places.createClient(this)
        initView()
        sessionManager = SessionManager(this)

    }

    fun statusCheck() {
        val manager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
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
                       PROVIDER = LocationManager.NETWORK_PROVIDER
                        setMarker()
                        startLatlng = LatLng(latitude, longitude)
                        if (marker == null) {
                            place1 = MarkerOptions().position(startLatlng!!).title("Origin") //new LatLng(27.658143, 85.3199503)
                            //new LatLng(27.667491, 85.3208583)
                            marker = mMap!!.addMarker(place1)
                            markers.add(marker!!)
                            marker!!.showInfoWindow()
                            /*  int padding = 50;
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : markers) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);*/
                            // mMap.moveCamera(cu);
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))
                        } else {
                            marker!!.setPosition(startLatlng!!)
                        }

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
                         PROVIDER = LocationManager.GPS_PROVIDER
                        //   fn_update(location);
                        // getNearPlaces();
                        startLatlng = LatLng(latitude, longitude)
                        setMarker()
                        if (marker == null) {
                            place1 = MarkerOptions().position(startLatlng!!).title("Origin") //new LatLng(27.658143, 85.3199503)
                           //new LatLng(27.667491, 85.3208583)
                            marker = mMap!!.addMarker(place1)
                            markers.add(marker!!)
                            marker!!.showInfoWindow()
                            /*  int padding = 50;
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : markers) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);*/
                            // mMap.moveCamera(cu);
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))
                        } else {
                            marker!!.setPosition(startLatlng!!)
                        }

                    }
                }
            }
        }
    }



    @SuppressLint("WrongConstant")
    private fun initView(){
        toolbar=findViewById(R.id.toolbar)
        drawerLayout=findViewById(R.id.drawerLayout)
        navigationView=findViewById(R.id.navigationView1)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
       // getSupportActionBar()!!.setDisplayShowTitleEnabled(false);
        setUpDrawerLayout()

        etpickup=findViewById(R.id.etpickup)
        sheet_request_trip=findViewById(R.id.sheet_request_trip)
        destination=findViewById(R.id.destination)
        car=findViewById(R.id.car)
        bike=findViewById(R.id.bike)
        cardcar=findViewById(R.id.cardcar)
        cardbike=findViewById(R.id.cardbike)
        ridenow=findViewById(R.id.ridenow)
        linearcar=findViewById(R.id.linearcar)
        linearbike=findViewById(R.id.linearbike)
        carprice=findViewById(R.id.carprice)
        bikeprice=findViewById(R.id.bikeprice)
        request=findViewById(R.id.request)
        distance1=findViewById(R.id.distance1)
        distance2=findViewById(R.id.distance)
        infocar=findViewById(R.id.infocar)
        infobike=findViewById(R.id.infobike)
        textcar=findViewById(R.id.textcar)

        etpickup.setOnClickListener(View.OnClickListener {
            val fields: List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent: Intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
            startActivityForResult(intent, 100)



        })
        destination.setOnClickListener(View.OnClickListener {
            val fields: List<Place.Field> = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent: Intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
            startActivityForResult(intent, 200)


        })

     /*   linearcar.setOnClickListener { View.OnClickListener {

            Log.v("Before1", type.toString())
        } }*/

        linearcar.setOnTouchListener(OnTouchListener { view, motionEvent -> // Show an alert dialog.
            linearcar.setBackgroundColor(Color.parseColor("#DCDCDC"));
            linearbike.setBackgroundColor(Color.parseColor("#ffffff"));
            type=1
            false
        })

        linearbike.setOnTouchListener(OnTouchListener { view, motionEvent -> // Show an alert dialog.
            linearbike.setBackgroundColor(Color.parseColor("#DCDCDC"));
            linearcar.setBackgroundColor(Color.parseColor("#ffffff"));
            type=2
            false
        })



        infobike.setOnClickListener(View.OnClickListener {
            val dialog = Dialog(this@TravelDashboard)
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
            val dialog = Dialog(this@TravelDashboard)
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



        cardcar.setOnClickListener(View.OnClickListener {
            car.setColorFilter(ContextCompat.getColor(this, R.color.white));
            bike.setColorFilter(ContextCompat.getColor(this, R.color.black));
            cardcar.setCardBackgroundColor(resources.getColor(R.color.black1))
            cardbike.setCardBackgroundColor(resources.getColor(R.color.white))
            type=1
        })

        cardbike.setOnClickListener(View.OnClickListener {
            bike.setColorFilter(ContextCompat.getColor(this, R.color.white));
            car.setColorFilter(ContextCompat.getColor(this, R.color.black));
            cardcar.setCardBackgroundColor(resources.getColor(R.color.white))
            cardbike.setCardBackgroundColor(resources.getColor(R.color.black1))
            type=2
        })

        request.setOnClickListener(View.OnClickListener {
       /*     if(destination.text.toString().contentEquals("")){
                Toast.makeText(this,"Please enter destination",Toast.LENGTH_LONG).show()
            }else if(type.equals("")){
                Toast.makeText(this,"Please select type",Toast.LENGTH_LONG).show()
            }else{
                startActivity(Intent(this, Ride::class.java).putExtra("destination",destination.text.toString())
                        .putExtra("origin",etpickup.text.toString())
                        .putExtra("type",type)
                        .putExtra("provider",PROVIDER))
            }
*/
        })

   /*     loadData()
        loadData1()
        destination.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
            ) {
                Log.d("Before", "now here")
            }

            override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
            ) {
                Log.d("textch", "before")
            }

            override fun afterTextChanged(s: Editable) {
                val locationAddress = GeocodingLocation()
                locationAddress.getAddressFromLocation(
                        destination.getText().toString(),
                       this@TravelDashboard, GeocoderHandlerfrom1()
                )
                setMarkerOnMap()

                sheet_request_trip.visibility=VISIBLE


            }
        })

*/
/*

        etpickup.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
            ) {
                Log.d("Before", "now here")
            }

            override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
            ) {
                Log.d("textch", "before")
            }

            override fun afterTextChanged(s: Editable) {
                val locationAddress = GeocodingLocation()
                locationAddress.getAddressFromLocation(
                        etpickup.getText().toString(),
                        this@TravelDashboard, GeocoderHandlerfrom1()
                )
                setMarkerOnMap()


            }
        })

*/

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        //Load Inbox fragment first
        navigationPosition = R.id.dashboard
        navigationView.setCheckedItem(navigationPosition)


        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.trip -> {
                          val i=Intent(this, Trip::class.java)
                          startActivity(i)
                }
                R.id.dashboard -> {
                    if (drawerLayout.isDrawerOpen(Gravity.START)) {
                        drawerLayout.closeDrawer(Gravity.START)
                    }
                }
                R.id.logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Alert").setMessage("Are you sure you want to Logout from app")
                    builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }.
                    setPositiveButton("Yes") { dialog, which -> // Constants.ACTIVITY_NAME=Constants.HOME_ACTIVITY;
                        val mSharedPreferences = applicationContext.getSharedPreferences("TASK_ID", 0)
                        mSharedPreferences?.edit()?.remove("userid")?.commit()
                        sessionManager.logoutUser()
                        startActivity(Intent(this, LoginActivity::class.java)) //.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finishAffinity()
                    }

                    val dialog = builder.create()
                    dialog.show()

                   // finish()
                }
              R.id.about -> {
                    startActivity(Intent(this@TravelDashboard, About::class.java))
              }
              R.id.support -> {
                  startActivity(Intent(this@TravelDashboard, Support::class.java))
              }
            }
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            drawerLayout.closeDrawers()
            true
        }

        //Change navigation header information
        changeNavigationHeaderInfo()



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                val place:Place = Autocomplete.getPlaceFromIntent(data!!)
                etpickup.setText(place.address.toString())
                startLatlng=place.latLng
                if(destination.text.toString().equals("Destination")){

                }else{
                    val loc1 = Location("one")
                    loc1.setLatitude(startLatlng!!.latitude)
                    loc1.setLongitude(startLatlng!!.longitude)
                    val loc2 = Location("two")
                    loc2.setLatitude(destLatLng!!.latitude)
                    loc2.setLongitude(destLatLng!!.longitude)
                    distance= loc1.distanceTo(loc2).toString()
                    startActivity(Intent(this, RequestRide::class.java).putExtra("destination",destination.text.toString())
                            .putExtra("origin",etpickup.text.toString())
                            .putExtra("type",type)
                            .putExtra("distance",distance)
                    )
                    setMarkerOnMap()
                }

                Log.v("Place",place.name.toString())
               // Log.i(FragmentActivity.TAG, "Place: " + place.name + ", " + place.id)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status: Status = Autocomplete.getStatusFromIntent(data!!)
                Log.v("status",status.toString())
                //status.getStatusMessage()?.let { Log.i(FragmentActivity.TAG, it) }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }else if (requestCode ==200) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                destination.setText(place.address.toString())
              //  sheet_request_trip.visibility=VISIBLE
                destLatLng=place.latLng
                val loc1 = Location("one")
                loc1.setLatitude(startLatlng!!.latitude)
                loc1.setLongitude(startLatlng!!.longitude)
                val loc2 = Location("two")
                loc2.setLatitude(destLatLng!!.latitude)
                loc2.setLongitude(destLatLng!!.longitude)
                distance= loc1.distanceTo(loc2).toString()
                startActivity(Intent(this, RequestRide::class.java).putExtra("destination",destination.text.toString())
                        .putExtra("origin",etpickup.text.toString())
                        .putExtra("type",type)
                        .putExtra("distance",distance)
                        .putExtra("startlatlng",startLatlng)
                        .putExtra("destlatlng",destLatLng)
                )

                setMarkerOnMap()
                Log.v("Place",place.name.toString())
               // Log.i(FragmentActivity.TAG, "Place: " + place.name + ", " + place.id)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status: Status = Autocomplete.getStatusFromIntent(data!!)
                Log.v("status",status.toString())
                //status.getStatusMessage()?.let { Log.i(FragmentActivity.TAG, it) }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    var parsedDistance: String? = null
    var response: String? = null
    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String? {
        val thread = Thread(Runnable {
            try {
                val url = URL("http://maps.googleapis.com/maps/api/directions/json?origin=$lat1,$lon1&destination=$lat2,$lon2&sensor=false&units=metric&mode=driving")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                val `in`: InputStream = BufferedInputStream(conn.inputStream)
                response = iStreamToString(`in`)
                val jsonObject = JSONObject(response)
                val array: JSONArray = jsonObject.getJSONArray("routes")
                val routes: JSONObject = array.getJSONObject(0)
                val legs: JSONArray = routes.getJSONArray("legs")
                val steps: JSONObject = legs.getJSONObject(0)
                val distance = steps.getJSONObject("distance")
                parsedDistance = distance.getString("text")
                Log.v("Distsnce", "Distance>>$parsedDistance")
            } catch (e: ProtocolException) {
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })
        thread.start()
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return parsedDistance
    }


    fun iStreamToString(is1: InputStream?): String {
        val rd = BufferedReader(InputStreamReader(is1), 4096)
        var line: String?
        val sb = StringBuilder()
        try {
            while (rd.readLine().also { line = it } != null) {
                sb.append(line)
            }
            rd.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return sb.toString()
    }
    class GeocoderHandlerfrom1 : Handler() {
        var destlat: Double? =null
        var destlong: Double? =null
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            locationAddress = when (message.what) {
                1 -> {
                    val bundle = message.data
                    bundle.getString("address")
                }
                else -> null
            }
            Log.d("logi", locationAddress!!)
            //autoCompleteTextViewPlace.setText("");

            //locationAddress.length();
            // oaddress.setText(dfromcity.getText().toString());
            val address =
                    locationAddress.split(",".toRegex()).toTypedArray()

            if (address.size > 3) {
                /*      ocity.setText(address[0]);
                      ostate.setText(address[1]);
                       ocountry.setText(address[2]);
                       opin.setText(address[3]);*/
          /*      var start1 = address[4];
                var start2 = address[5];
                *//*    var startlat : Double = start1
                    var startlong : Double = start2
    *//*

                destlat= start1.toDouble()
                destlong = start2.toDouble()


*/
            }

            //  oaddress.setText(ocity.getText().toString()+" ,"+ostate.getText().toString()+" ,"+ocountry.getText().toString());
        }
    }



 /*   private fun loadData() {
        val predictions: List<Prediction> = ArrayList<Prediction>()
        val placesAutoCompleteAdapter =
                PlacesAutoCompleteAdapter(this, predictions)
        destination.setThreshold(1)
        destination.setAdapter(placesAutoCompleteAdapter)
        placesAutoCompleteAdapter.notifyDataSetChanged()

    }
    private fun loadData1() {
        val predictions: List<Prediction> = ArrayList<Prediction>()
        val placesAutoCompleteAdapter =
                PlacesAutoCompleteAdapter(this, predictions)
        etpickup.setThreshold(1)
        etpickup.setAdapter(placesAutoCompleteAdapter)
        placesAutoCompleteAdapter.notifyDataSetChanged()

    }
*/
    private fun changeNavigationHeaderInfo() {
        val headerView = navigationView.getHeaderView(0)
        headerView.setOnClickListener(View.OnClickListener {
            val i=Intent(this, ProfileDetail::class.java)
            startActivity(i)
        })
        //  headerView.textEmail.text = "lokeshdesai@android4dev.com"
    }

    private fun setUpDrawerLayout() {
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun navigateToFragment(fragmentToNavigate: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        //fragmentTransaction.replace(R.id.frameLayout, fragmentToNavigate)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @SuppressLint("WrongConstant")
    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        }

        if (navigationPosition == R.id.trip) {
            finish()
        } else {
            //Navigate to Inbox Fragment
            navigationPosition = R.id.trip
            //   navigateToFragment(InboxFragment.newInstance())
            //    navigationView.setCheckedItem(navigationPosition)
            toolbar.title = "Travel"
        }
    }

    override fun onLocationChanged(location: Location) {
        if (location != null) {
            Log.e("latitude", location!!.latitude.toString() + "")
            Log.e("longitude", location!!.longitude.toString() + "")
            latitude = location!!.latitude
            longitude = location!!.longitude

        }
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap!!.getUiSettings().setZoomControlsEnabled(true)
        fn_permission()
        statusCheck()

    }

    private fun setMarker() {
        val geocoder: Geocoder
        val yourAddresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())
        yourAddresses = geocoder.getFromLocation(latitude,longitude, 1)
        try {
            etpickup.setText("" + yourAddresses.get(0).getAddressLine(0))
        }catch (e:java.lang.Exception){

        }
    }

    var destLatLng: LatLng? = null
    var startLatlng: LatLng? = null
    private var place1: MarkerOptions? = null
    private  var place2:MarkerOptions? = null
    private var currentPolyline: Polyline? = null
    var marker: Marker? = null
    var marker1: Marker? = null
    var markers: ArrayList<Marker> = java.util.ArrayList()
    var  originlongitude : Double=0.0
    var  originlatitude : Double=0.0
    var  destlatitude : Double=0.0
    var  destlongitude : Double=0.0

    fun setMarkerOnMap() {
        try {
          /*  val coder = Geocoder(this)
            try {
                val adresses  = coder.getFromLocationName(etpickup.text.toString(), 50)

                val location: Address = adresses.get(0)
                originlatitude =  location.latitude
                originlongitude =  location.longitude

                startLatlng= LatLng(originlatitude,originlongitude)

                val coder1 = Geocoder(this)
                val adresses1 = coder1.getFromLocationName(destination.text.toString(), 50) as java.util.ArrayList<Address>

                val location1: Address = adresses1.get(0)
                destlatitude =  location1.latitude
                destlongitude =  location1.longitude
                destLatLng= LatLng(destlatitude,destlongitude)

                //  return distanceInMeters / 1000

                Log.v("distance",distance)
            } catch (e: IOException) {
                e.printStackTrace()
            }

*/




            //destLatLng= LatLng(p.destlat!!,p.destlong!!)
            if (marker == null || marker1 == null) {
                place1 = MarkerOptions().position(startLatlng!!).title("Origin") //new LatLng(27.658143, 85.3199503)
                place2 = MarkerOptions().position(destLatLng!!).title("Destination") //new LatLng(27.667491, 85.3208583)
                marker = mMap!!.addMarker(place1)
                marker1 = mMap!!.addMarker(place2)
                markers.add(marker!!)
                markers.add(marker1!!)
                marker1!!.showInfoWindow()
                marker!!.showInfoWindow()
                val url: String? = getUrl(startLatlng!!, destLatLng!!)
                val FetchUrl: FetchUrl = FetchUrl()
                FetchUrl.execute(url)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))
            } else {
                marker!!.setPosition(startLatlng!!)
            }
            //senddistance()


        } catch (e: Exception) {
            Log.d("MapException", e.toString())
        }
    }

    private fun senddistance() {




/*   *//*     val progressDialog = ProgressDialog(this)
        // progressDialog.setTitle("Kotlin Progress Bar")
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        progressDialog.setCanceledOnTouchOutside(false)*//*
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
                       *//*     distance1.setText(lo.distance)
                            distance2.setText(lo.distance)*//*
                         //   progressDialog.dismiss()

                        } else {
                          //  progressDialog.dismiss()
                            //  Toast.makeText(this@ProfileDetail, "not submiited", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onFailure(call: Call<GetPrice>, t: Throwable) {
                    Toast.makeText(this@TravelDashboard, t.message, Toast.LENGTH_SHORT).show()
                }
            })*/
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


    override fun onTaskDone(vararg values: Any?) {
      /*  if (currentPolyline != null) currentPolyline!!.remove()
        currentPolyline = mMap!!.addPolyline(values[0] as PolylineOptions?)*/
    }

}
