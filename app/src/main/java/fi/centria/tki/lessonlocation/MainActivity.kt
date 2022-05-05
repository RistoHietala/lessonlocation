package fi.centria.tki.lessonlocation

import android.Manifest
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.location.*


class MainActivity : AppCompatActivity() {

    private lateinit var no_location_layout: ConstraintLayout
    private lateinit var location_layout: ConstraintLayout
    private lateinit var lati: TextView
    private lateinit var longi: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var requestingLocationUpdates: Boolean = false

    private lateinit var map_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        no_location_layout = findViewById(R.id.no_location_layout)
        location_layout = findViewById(R.id.location_layout)
        lati = findViewById(R.id.latitude_text)
        longi = findViewById(R.id.longitude_text)
        map_button = findViewById(R.id.map_button)
        map_button.setOnClickListener { v->
            intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        askPermission()
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations){
                    lati.text = location.latitude.toString()
                    longi.text = location.longitude.toString()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates){
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun askPermission()
    {
        val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions ->
            when{
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,false) -> {
                    no_location_layout.visibility = View.GONE
                    location_layout.visibility = View.VISIBLE
                    getLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    no_location_layout.visibility = View.GONE
                    location_layout.visibility = View.VISIBLE
                    getLocation()
                }
                else -> {
                    no_location_layout.visibility = View.VISIBLE
                    location_layout.visibility = View.GONE
                }
            }
        }
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun getLocation()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                lati.text = location?.latitude.toString()
                longi.text = location?.longitude.toString()
                createLocationRequest()
                startLocationUpdates()
            }
        }
        catch (e: SecurityException)
        {
            e.printStackTrace()
        }
    }

    private fun createLocationRequest(){
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates(){
        try{
            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
        }
        catch (e: SecurityException)
        {
            e.printStackTrace()
        }
    }

    private fun stopLocationUpdates(){
        if (this::fusedLocationClient.isInitialized && fusedLocationClient != null)
        {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}