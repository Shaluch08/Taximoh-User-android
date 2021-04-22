package com.uveous.taximohdriver

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mindorks.example.ubercaranimation.util.MapUtils
import com.uveous.loopfoonpay.*
import com.uveous.loopfoonpay.Api.ApiClient
import com.uveous.loopfoonpay.Api.ApiService
import com.uveous.loopfoonpay.Api.Model.GetPrice
import com.uveous.loopfoonpay.Api.Model.profiledetail
import com.uveous.loopfoonpay.Api.SessionManager
import com.uveous.loopfoonpay.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.util.*

class TravelDashboard : AppCompatActivity()  , OnMapReadyCallback{
    var navigationPosition: Int = 0
    lateinit var drawerLayout : DrawerLayout;
    lateinit var toolbar : Toolbar
    lateinit var navigationView: NavigationView
    private lateinit var etpickup : TextView
    public lateinit var destination: TextView
    public lateinit var car: ImageView
    public lateinit var bike: ImageView
    public lateinit var currentlocation: ImageView
    public lateinit var infocar: ImageView
    public lateinit var back: ImageView
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

    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000

    private val REQUEST_CHECK_SETTINGS = 300
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mCurrentLocation: Location? = null
    lateinit var Latlng1: LatLng
    lateinit var Latlng2: LatLng
    lateinit var Latlng3: LatLng
    lateinit var Latlng4: LatLng
    // boolean flag to toggle the ui
    private var mRequestingLocationUpdates: Boolean? = null
    companion object{

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.travel_dashboard)

        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key))
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // location is received
                mCurrentLocation = locationResult.lastLocation
                if(etpickup.text.toString().contentEquals("Origin")) {
                    updateLocationUI()
                }
            }
        }
        mRequestingLocationUpdates = false;

        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest!!.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()

        sessionManager = SessionManager(this)
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    mRequestingLocationUpdates = true
                    startLocationUpdates()
                }

                override   fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied()) {
                        // open device settings when the permission is
                        // denied permanently
                        openSettings()
                    }
                }

                override  fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
        val placesClient: PlacesClient = Places.createClient(this)
        initView()

    }
    override fun onResume() {
        super.onResume()
        if(etpickup.text.toString().contentEquals("Origin")) {
            if (mRequestingLocationUpdates!! && checkPermissions()) {
                updateLocationUI()
            }
        }

    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }


    override fun onPause() {
        super.onPause()

    }

    private fun updateLocationUI() {
        if (mCurrentLocation != null) {
            val geocoder: Geocoder
            val yourAddresses: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())
            yourAddresses = geocoder.getFromLocation(mCurrentLocation!!.latitude,mCurrentLocation!!.longitude, 1)
            try {
                etpickup.setText("" + yourAddresses.get(0).getAddressLine(0))
            }catch (e:java.lang.Exception){

            }
        /*    if(marker!=null){
                marker!!.remove()
                marker2!!.remove()
                marker3!!.remove()
                marker4!!.remove()
                marker5!!.remove()
                
            }
*/
            startLatlng = LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude)
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
                Latlng1=LatLng(startLatlng!!.latitude+0.001,startLatlng!!.longitude+0.001)
                marker2= addMarker(Latlng1)
                Latlng2=LatLng(startLatlng!!.latitude+0.001,startLatlng!!.longitude-0.001)
                marker3= addMarker(Latlng2)
                Latlng3=LatLng(startLatlng!!.latitude-0.001,startLatlng!!.longitude-0.001)
                marker4=addMarker(Latlng3)
                Latlng4=LatLng(startLatlng!!.latitude+0.002,startLatlng!!.longitude-0.002)
                marker5= addMarker(Latlng4)
            } else {
                marker!!.setPosition(startLatlng!!)
            }

        }

    }


    private fun addMarker(latLng: LatLng): Marker? {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(this))
        return mMap!!.addMarker(
                MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun startLocationUpdates() {
        mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(this, object : OnSuccessListener<LocationSettingsResponse?> {
                @SuppressLint("MissingPermission")
                override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                 //   Log.i(FragmentActivity.TAG, "All location settings are satisfied.")
                 /*   Toast.makeText(
                        applicationContext,
                        "Started location updates!",
                        Toast.LENGTH_SHORT
                    ).show()*/
                    mFusedLocationClient!!.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback, Looper.myLooper()
                    )
                    updateLocationUI()
                }
            })
            .addOnFailureListener(this, object : OnFailureListener {
                override fun onFailure(@NonNull e: java.lang.Exception) {
                    val statusCode: Int = (e as ApiException).getStatusCode()
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {

                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae: ResolvableApiException = e as ResolvableApiException
                                rae.startResolutionForResult(
                                    this@TravelDashboard,
                                    REQUEST_CHECK_SETTINGS
                                )
                            } catch (sie: IntentSender.SendIntentException) {

                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings."

                        }
                    }
                    updateLocationUI()
                }
            })
    }

    private fun openSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts(
            "package",
            BuildConfig.APPLICATION_ID, null
        )
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    @SuppressLint("WrongConstant")
    private fun initView(){
        toolbar=findViewById(R.id.toolbar)
        drawerLayout=findViewById(R.id.drawerLayout)
        navigationView=findViewById(R.id.navigationView1)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
       // getSupportActionBar()!!.setDisplayShowTitleEnabled(false);
        //setUpDrawerLayout()

        etpickup=findViewById(R.id.etpickup)
        sheet_request_trip=findViewById(R.id.sheet_request_trip)
        destination=findViewById(R.id.destination)
        car=findViewById(R.id.car)
        bike=findViewById(R.id.bike)
        currentlocation=findViewById(R.id.currentlocation)
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
        back=findViewById(R.id.back)
        infobike=findViewById(R.id.infobike)
        textcar=findViewById(R.id.textcar)

        currentlocation.setOnClickListener(View.OnClickListener {
            updateLocationUI()
        })

        back.setOnClickListener(View.OnClickListener {
            navigationPosition = R.id.dashboard
            navigationView.setCheckedItem(navigationPosition)

            drawerLayout.openDrawer(Gravity.LEFT)
        })

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

         /*   price.setText(lo.currency+lo.bike.totalPrice)
            base.setText(lo.currency+lo.bike.baseFare)
            fare.setText(lo.currency+lo.bike.prKM)
            tax.setText(lo.bike.tax)*/
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
/*
            price.setText(lo.currency+lo.car.totalPrice)
            base.setText(lo.currency+lo.car.baseFare)
            fare.setText(lo.currency+lo.car.prKM)
            tax.setText(lo.car.tax)*/
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
                R.id.offers -> {
                  startActivity(Intent(this@TravelDashboard, Offers::class.java))
              }   R.id.invitefriend -> {
                  startActivity(Intent(this@TravelDashboard, InviteFriend::class.java))
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
                    marker!!.remove()
                 /*   marker2!!.remove()
                    marker3!!.remove()
                    marker4!!.remove()
                    marker5!!.remove()*/
                    place1 = MarkerOptions().position(startLatlng!!).title("Origin") //new LatLng(27.658143, 85.3199503)
                    marker = mMap!!.addMarker(place1)
                    marker!!.showInfoWindow()
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatlng, 16f))

                    Latlng1=LatLng(startLatlng!!.latitude+0.001,startLatlng!!.longitude+0.001)
                    marker2= addMarker(Latlng1)
                     Latlng2=LatLng(startLatlng!!.latitude+0.001,startLatlng!!.longitude-0.001)
                    marker3= addMarker(Latlng2)
                     Latlng3=LatLng(startLatlng!!.latitude-0.001,startLatlng!!.longitude-0.001)
                    marker4=addMarker(Latlng3)
                     Latlng4=LatLng(startLatlng!!.latitude+0.002,startLatlng!!.longitude-0.002)
                    marker5= addMarker(Latlng4)
                }else{
                    val loc1 = Location("one")
                    loc1.setLatitude(startLatlng!!.latitude)
                    loc1.setLongitude(startLatlng!!.longitude)
                    val loc2 = Location("two")
                    loc2.setLatitude(destLatLng!!.latitude)
                    loc2.setLongitude(destLatLng!!.longitude)
                   // distance= loc1.distanceTo(loc2).toString()

                    val earthRadius = 6371000.0 //meters

                    val dLat = Math.toRadians(destLatLng!!.latitude - startLatlng!!.latitude)
                    val dLng = Math.toRadians(destLatLng!!.longitude - startLatlng!!.longitude)
                    val a =
                        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                Math.cos(Math.toRadians(startLatlng!!.latitude)) * Math.cos(
                            Math.toRadians(destLatLng!!.latitude)
                        ) *
                                Math.sin(dLng / 2) * Math.sin(dLng / 2)
                    val c =
                        2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                    val dist = (earthRadius * c).toFloat()
                    distance=dist.toString()

                    startActivity(Intent(this, RequestRide::class.java)
                            .putExtra("destination",destination.text.toString())
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
            //    distance= loc1.distanceTo(loc2).toString()

                val earthRadius = 6371000.0 //meters

                val dLat = Math.toRadians(destLatLng!!.latitude - startLatlng!!.latitude)
                val dLng = Math.toRadians(destLatLng!!.longitude - startLatlng!!.longitude)
                val a =
                    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(startLatlng!!.latitude)) * Math.cos(
                        Math.toRadians(destLatLng!!.latitude)
                    ) *
                            Math.sin(dLng / 2) * Math.sin(dLng / 2)
                val c =
                    2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                val dist = (earthRadius * c).toFloat()
                distance=dist.toString()

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
        }else if(requestCode==REQUEST_CHECK_SETTINGS){
            if (resultCode == Activity.RESULT_OK) {

            }else if (resultCode == Activity.RESULT_CANCELED) {
                mRequestingLocationUpdates = false;
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
     val name = headerView.findViewById(R.id.txtName) as TextView
     val txtEmail = headerView.findViewById(R.id.txtEmail) as TextView
     try{
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
                         name.text=lo.first_name+lo.last_name
                         txtEmail.text=lo.email


                     } else {

                         //  Toast.makeText(this@ProfileDetail, "not submiited", Toast.LENGTH_SHORT).show()
                     }

                 }
             }

             override fun onFailure(call: Call<profiledetail>, t: Throwable) {
                 Toast.makeText(this@TravelDashboard, t.message, Toast.LENGTH_SHORT).show()
             }
         })
     }

     }catch (e:java.lang.Exception){

     }



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


    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap!!.getUiSettings().setZoomControlsEnabled(true)

    }

    private fun setMarker() {

    }

    var destLatLng: LatLng? = null
    var startLatlng: LatLng? = null
    private var place1: MarkerOptions? = null
    private  var place2:MarkerOptions? = null
    private var currentPolyline: Polyline? = null
    var marker: Marker? = null
    var marker1: Marker? = null
    var marker2: Marker? = null
    var marker3: Marker? = null
    var marker4: Marker? = null
    var marker5: Marker? = null
    var markers: ArrayList<Marker> = java.util.ArrayList()
    var  originlongitude : Double=0.0
    var  originlatitude : Double=0.0
    var  destlatitude : Double=0.0
    var  destlongitude : Double=0.0

    fun setMarkerOnMap() {
        try {

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
