package com.example.toastlibrary

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import java.util.*


class CameraActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    lateinit var image: ImageView
    private val pic_id = 123
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    protected val REQUEST_CHECK_SETTINGS = 0x1
    var currlat = 0.0
    var currlog:Double = 0.0
    private var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val btnPhoto = findViewById<TextView>(R.id.btnPhoto)
        image = findViewById<ImageView>(R.id.image)
        settingsrequest()
        btnPhoto.setOnClickListener {
           // locationPermission()

            startCamera()
        }


    }

    fun locationPermission() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA
                ), 1
            )
            return;
        } else {
           startCamera()
            //settingsrequest()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                   locationPermission()
                } else {
                    locationPermission()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === pic_id) {
            image.setImageBitmap(data!!.getExtras()!!.get("data") as Bitmap?)
        }
    }
    fun startCamera(){
        val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        startActivityForResult(camera_intent, pic_id)
    }
    override fun onConnected(bundle: Bundle?) {}

    override fun onConnectionSuspended(i: Int) {}

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    fun settingsrequest() {
        val googleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true) //this is the key ingredient
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            val state = result.locationSettingsStates
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    //  setLoginScreen();
                    getLocation()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {}
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
            }
        }
    }

    fun getLocation() {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
            fusedLocationClient?.getLastLocation()?.addOnSuccessListener(object : OnSuccessListener<Location?> {

                    override fun onSuccess(location: Location?) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currlat = location.latitude
                            currlog = location.longitude
                            Log.d("currlatservice", "$currlat...")
                            Log.d("currlogservice", currlog.toString() + "...")
                            getCurrentAddress(currlat, currlog)
                        } else {
                            getLocationfromClient()
                        }
                    }
                })
        } catch (e: Exception) {
        }
    }

    fun getLocationfromClient() {
        try {
             mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = LocationRequest.create()
            locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest?.setInterval((20 * 1000).toLong())
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    try {
                        if (locationResult == null) {
                            // return;
                        }
                        for (location in locationResult!!.getLocations()) {
                            if (location != null) {
                                mFusedLocationClient?.removeLocationUpdates(locationCallback)
                                currlat = location.latitude
                                currlog = location.longitude

                                Log.d("currlatservice", "$currlat...")
                                Log.d("currlogservice", currlog.toString() + "...")
                                getCurrentAddress(currlat, currlog)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
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
            mFusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: Exception) {
        }
    }

    fun getCurrentAddress(latitude: Double, longitude: Double) {
        val gc = Geocoder(this, Locale.getDefault())
        try {
            val addresses = gc.getFromLocation(latitude, longitude, 1)
            val sb = StringBuilder()
            if (addresses.size > 0) {
                val address = addresses[0]
                // for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                //    sb.append(address.getAddressLine(i)).append("\n");
                sb.append(address.subLocality).append(", ")
                sb.append(address.locality).append(", ")
                sb.append(address.adminArea).append(", ")
                sb.append(address.countryName).append(", ")
                sb.append(address.postalCode)
            }
            Log.d("currentAddress", sb.toString())

            // PreferenceUtil.setStringPrefs(this, PreferenceUtil.CURRENT_ADDRESS, sb.toString());
        } catch (e: Exception) {
        }
    }


}