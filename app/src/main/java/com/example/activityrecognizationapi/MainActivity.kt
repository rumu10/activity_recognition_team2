package com.example.activityrecognizationapi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.activityrecognizationapi.databinding.ActivityMainBinding
import com.example.activityrecognizationapi.service.BackgroundDetectedActivitiesService
import com.example.activityrecognizationapi.utils.Constants
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    lateinit var broadcastReceiver: BroadcastReceiver
    lateinit var binding:ActivityMainBinding
    lateinit var sleepRequestManager: SleepRequestManager
    private var isSleep = false
    private var mediaPlayer: MediaPlayer? = null

    //Mar

    private val TAG = "MainActivity"
    private lateinit var Mmap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var locationManager: LocationManager
    private val locationCode = 2000
    private val locationCode1 = 2001
    private val GEOFENCE_RADIUS = 30
    private lateinit var geofenceHelper: GeofenceHelper
    private val GEOFENCE_ID1 = "Innovation Studio"
    private val GEOFENCE_ID2 = " Salisbury Labs"
    private var innovationCount = 0
    private var salisburyCount = 0

    private var stepCounter = 0
    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null
    private var initialStepCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()

        //Mar
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)


        sleepRequestManager = SleepRequestManager(this)

        requestActivityRecognitionPermission()

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if (intent.action == Constants.BROADCAST_DETECTED_ACTIVITY) {
                    val type = intent.getIntExtra("type", -1)
                    val confidence = intent.getIntExtra("confidence", 0)
                    provideUserStateOutput(type, confidence)
                }
            }
        }


//        binding.btnStartTracking.setOnClickListener {
            startTracking()
//        }

//        binding.btnStopTracking.setOnClickListener {
//            stopTracking()
//        }

//        binding.btnSleep.setOnClickListener{
            if(!isSleep) {
                isSleep = true
                sleepRequestManager.subscribeToSleepUpdates()
            }else {
                isSleep = false
                sleepRequestManager.unsubscribeFromSleepUpdates()
            }
//        }
    }


    //MAR
    override fun onMapReady(googleMap: GoogleMap) {
        Mmap = googleMap
        val innovation_studio = LatLng(42.27439271094537, -71.80876819217796)
        val salisbury_lab = LatLng(42.274569544032445, -71.80702114259171)

        val zoom0nmap = LatLng(42.2744037458277, -71.8079081146332)
        Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoom0nmap, 17F))
        getmylocation()
        addMarker(innovation_studio)
        addCircle(innovation_studio, GEOFENCE_RADIUS)
        addMarker(salisbury_lab)
        addCircle(salisbury_lab, GEOFENCE_RADIUS)
        addGeofence(innovation_studio, GEOFENCE_RADIUS, GEOFENCE_ID1)
        addGeofence(salisbury_lab, GEOFENCE_RADIUS, GEOFENCE_ID2)
    }

    private fun getmylocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Mmap.isMyLocationEnabled = true

            // Request continuous location updates
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                interval = 2000  // Get location update every 2 seconds
                fastestInterval = 2000
                priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationCode)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationCode)
            }
        }
    }

    private fun addGeofence(latLng: LatLng, radius: Int, ID: String) {
        val geofence = geofenceHelper.getGeofence(ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.CreatePendingIntent()
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "onSuccess: Geofence Added...")
            }
            .addOnFailureListener { e ->
                val errorMessage = geofenceHelper.getErrorString(e)
                Log.d(TAG, "onFailure: $errorMessage")
            }
    }


    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        Mmap.addMarker(markerOptions)
    }

    private fun addCircle(latLng: LatLng, radius: Int) {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble()) // Convert the radius to Double
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        Mmap.addCircle(circleOptions)
    }

    private fun handleMapLongClick(latLng: LatLng) {
        Mmap.clear()
        addMarker(latLng)
        addCircle(latLng, GEOFENCE_RADIUS)
        addGeofence(latLng, GEOFENCE_RADIUS, GEOFENCE_ID1)
        addGeofence(latLng, GEOFENCE_RADIUS, GEOFENCE_ID2)
    }


    override fun onMapLongClick(p0: LatLng) {
        if (Build.VERSION.SDK_INT>=29)
        {
            if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                handleMapLongClick(p0)
            }
            else
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationCode)
                    Toast.makeText(this@MainActivity, "For triggering Geofencing we need your background location permission", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationCode)
                }
            }
        }
        else
        {
            handleMapLongClick(p0)
        }

    }

    //Step COunter COde

    private val sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Nothing to do here for step counter
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                // If initialStepCount is zero, set it to the current count
                if (initialStepCount == 0) {
                    initialStepCount = event.values[0].toInt()
                }
                // Subtract the initial count from the current count to get the number of steps since the app started
                stepCounter = event.values[0].toInt() - initialStepCount
                updateStepCounterTextView()
            }
        }
    }

    fun updateStepCounterTextView() {
        val stepCounterTextView = findViewById<TextView>(R.id.stepCounterTextView)
        stepCounterTextView.text = "Steps taken since the app started: $stepCounter"
    }


    //ActivityRecognition

    private fun startTracking(){
        val intent = Intent(this, BackgroundDetectedActivitiesService::class.java)
        startService(intent)
    }

    private fun stopTracking(){
        val intent = Intent(this, BackgroundDetectedActivitiesService::class.java)
        stopService(intent)
    }

    private var isWalking = false
    private var walkingStartTime: Long = 0
    private var walkingDuration: Long = 0

    private var isStill = false
    private var StillStartTime: Long = 0
    private var StillDuration: Long = 0

    private var formattedDurationS = ""
    private var formattedDurationW = ""

    fun formatDuration(durationMillis: Long): String {
        val seconds = (durationMillis / 1000).toInt()
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    }

    private fun provideUserStateOutput(type: Int, confidence: Int) {
        println(type)
        var label = getString(R.string.activity_still)
        var icon = R.drawable.is_still

        when (type) {
            DetectedActivity.IN_VEHICLE -> {
                var label = getString(R.string.activity_in_vehicle)
                var icon = R.drawable.ic_driving
            }

            DetectedActivity.RUNNING -> {

                if (confidence > Constants.CONFIDENCE) {
                    playAudio()
                }
                label = getString(R.string.activity_running)
                icon = R.drawable.running
            }

            DetectedActivity.STILL -> {
                if (confidence > Constants.CONFIDENCE) {
                    mediaPlayer?.stop()
                }
                isStill = true
                isWalking = false
                println("still")
                if (!isWalking && isStill) {
                    StillStartTime = SystemClock.elapsedRealtime()
//                    val currentTime = System.currentTimeMillis()
                    val currentTime = SystemClock.elapsedRealtime()
                    val elapsedTime = currentTime - walkingStartTime
                    formattedDurationS = formatDuration(elapsedTime)
                    println("Formatted suretion" + formattedDurationS)

                    if (formattedDurationW != "") {
                        Toast.makeText(
                            applicationContext,
                            "You walked for $formattedDurationW",
                            Toast.LENGTH_SHORT
                        ).show();
                    }

                }
                label = getString(R.string.activity_still)
                icon = R.drawable.is_still

                if (confidence > Constants.CONFIDENCE) {
                    runOnUiThread {
                        binding.txtActivity.text = label
                        binding.imgActivity.setImageResource(icon)
                    }
                }
            }

            DetectedActivity.WALKING -> {
                if (confidence > Constants.CONFIDENCE) {
                    playAudio()
                }
                isWalking = true
                isStill = false
                println("walking")
                if (!isStill && isWalking) {
                    walkingStartTime = SystemClock.elapsedRealtime()
//                    val currentTime = System.currentTimeMillis()
                    val currentTime = SystemClock.elapsedRealtime()
                    println("current" + currentTime)
                    val elapsedTime = currentTime - StillStartTime
                    println("elapsed" + elapsedTime)
                    formattedDurationW = formatDuration(elapsedTime)
                    println("Formatted suretion" + formattedDurationW)

                    Toast.makeText(
                        applicationContext,
                        "You were still for $formattedDurationS",
                        Toast.LENGTH_SHORT
                    ).show();
                }

                label = getString(R.string.activity_walking)
                icon = R.drawable.is_walking

                if (confidence > Constants.CONFIDENCE) {
                    runOnUiThread {
                        binding.txtActivity.text = label
                        binding.imgActivity.setImageResource(icon)
                    }
                }
            }


//            if(confidence > Constants.CONFIDENCE){
//                runOnUiThread {
//                    binding.txtActivity.text = label
//    //            binding.txtConfidence.text ="Confidence : $confidence"
//                    binding.imgActivity.setImageResource(icon)
//                }
//            }
        }

        Log.e("ActivityDetection", "User activity: $label, Confidence: $confidence")
    }
    private fun playAudio() {

            mediaPlayer = MediaPlayer.create(this, R.raw.beat_02)
            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                Log.e("MediaPlayer", "Error occurred: what=$what, extra=$extra")
                true
            }
            mediaPlayer?.start()
    }

    private fun requestActivityRecognitionPermission(){
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 101)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            101 -> {
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults.indices) {
                        val permission = permissions[i]
                        if (android.Manifest.permission.ACTIVITY_RECOGNITION.equals(permission,
                                ignoreCase = true)) {
                            if (grantResults[i] === PackageManager.PERMISSION_GRANTED) {
                                // you now have permission

                                //MAR
                                if (requestCode == locationCode) {
                                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                        if ((ActivityCompat.checkSelfPermission(
                                                this,
                                                android.Manifest.permission.ACCESS_FINE_LOCATION
                                            ) != PackageManager.PERMISSION_GRANTED) &&
                                            (ActivityCompat.checkSelfPermission(
                                                this,
                                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                                            ) != PackageManager.PERMISSION_GRANTED)
                                        ) {
                                            return
                                        }
                                        Mmap.isMyLocationEnabled = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //MAR
        if (requestCode == locationCode1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if ((ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    return
                }
                Toast.makeText(this@MainActivity, "You can add geofence", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun getAddressFromLatLng(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val address = addresses[0]
                val addressFragments = with(address) {
                    (0..maxAddressLineIndex).map { getAddressLine(it) }
                }
                return addressFragments.joinToString(separator = "\n")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Geocoder Service not available", e)
        }
        return "No Address Found"
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            val location = locationResult.lastLocation
            val address = getAddressFromLatLng(LatLng(location.latitude, location.longitude))
            findViewById<TextView>(R.id.address_text_view).text = "Address: $address"
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY))
        val intentFilter = IntentFilter("UPDATE_UI_ACTION")
        registerReceiver(updateUIReceiver, intentFilter)


        sensorManager?.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        mediaPlayer?.release()
    }

    override fun onPause() {
        super.onPause()
//        unregisterReceiver(updateUIReceiver)
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback)
        unregisterReceiver(updateUIReceiver)

        sensorManager?.unregisterListener(sensorEventListener)

    }

    private val updateUIReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "UPDATE_UI_ACTION") {
                val GeoCounter = intent.getIntExtra("GeofenceCounter", 0)
                updateCountersInTextViews(GeoCounter)
            }
        }
    }
    fun updateCountersInTextViews(GeoCounter: Int) {
        if(GeoCounter==100){
            val innovationStudioCounterTextView = findViewById<TextView>(R.id.innovationStudioCounter)
            innovationCount +=1
            innovationStudioCounterTextView.text = "Visit to innovation studio geofence:: $innovationCount"



        }else if (GeoCounter==200){

            val salisburyLabsCounterTextView = findViewById<TextView>(R.id.salisburyLabsCounter)
            salisburyCount +=1
            salisburyLabsCounterTextView.text = "Visit to salisbury labs geofence:: $salisburyCount"
        }

    }

}