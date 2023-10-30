package com.example.activityrecognizationapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
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
import java.util.jar.Manifest

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()

        //Mar
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
        val innovation_studio = LatLng(42.27434765610721, -71.80862893926412)
        val salisbury_lab = LatLng(42.2747687040191, -71.80686306135425)
//        val innovation_studio = LatLng(42.275233612801436, -71.80629343277876)
//        val salisbury_lab = LatLng(42.27418447287344, -71.806926668775)
        val zoom0nmap = LatLng(42.2744037458277, -71.8079081146332)
        Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoom0nmap, 17F))
        getmylocation()
        addMarker(innovation_studio)
        addCircle(innovation_studio, GEOFENCE_RADIUS)
        addMarker(salisbury_lab)
        addCircle(salisbury_lab, GEOFENCE_RADIUS)
        addGeofence(innovation_studio, GEOFENCE_RADIUS, GEOFENCE_ID1)
        addGeofence(salisbury_lab, GEOFENCE_RADIUS, GEOFENCE_ID2)
//        Mmap.setOnMapLongClickListener(this)
    }

    private fun getmylocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            Mmap.isMyLocationEnabled = true
        }
        else
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationCode)
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), locationCode)
            }
        }
    }

    //    @SuppressLint("MissingSuperCall")
    private fun onMAPRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

    private var stillStartTime: Long = 0
    private var totalStillDuration: Long = 0

    private fun provideUserStateOutput(type: Int, confidence: Int){
        var label = getString(R.string.activity_still)
        var icon = R.drawable.is_still

        when (type) {
            DetectedActivity.IN_VEHICLE -> {
                label = getString(R.string.activity_in_vehicle)
                icon = R.drawable.ic_driving
            }
            DetectedActivity.RUNNING -> {
                label = getString(R.string.activity_running)
                icon = R.drawable.running
                playAudio();
            }
            DetectedActivity.STILL -> {
                println("still")
                label = getString(R.string.activity_still)
                icon = R.drawable.is_still
                if (isWalking) {
                    // Stop tracking walking
                    isWalking = false
                    val currentTime = System.currentTimeMillis()
                    walkingDuration += currentTime - walkingStartTime
                }

                if (stillStartTime == 0L) {
                    // This is the start of a new "STILL" period
                    stillStartTime = System.currentTimeMillis()
                }
            }
            DetectedActivity.WALKING -> {
                println("walking")
                label = getString(R.string.activity_walking)
                icon = R.drawable.is_walking
                playAudio();

                if (!isWalking) {
                    // Start tracking walking
                    isWalking = true
                    walkingStartTime = System.currentTimeMillis()
                }
            }
        }

        fun formatDuration(durationMillis: Long): String {
            val seconds = (durationMillis / 1000).toInt()
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
        }

        if (isWalking) {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - walkingStartTime
            val formattedDuration = formatDuration(elapsedTime)
            println(formattedDuration)
        }

        Log.e("ActivityDetection", "User activity: $label, Confidence: $confidence")

        if(confidence > Constants.CONFIDENCE){
            runOnUiThread {
                binding.txtActivity.text = label
//            binding.txtConfidence.text ="Confidence : $confidence"
                binding.imgActivity.setImageResource(icon)
            }
        }
    }

    private fun requestActivityRecognitionPermission(){
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 101)
        }
    }

    private fun playAudio() {
        mediaPlayer = MediaPlayer.create(this, R.raw.beat_02)
        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            Log.e("MediaPlayer", "Error occurred: what=$what, extra=$extra")
            true
        }
        mediaPlayer?.start()
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
                            }
                        }
                    }
                }
            }
        }
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

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY))
        val intentFilter = IntentFilter("UPDATE_UI_ACTION")
        registerReceiver(updateUIReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        mediaPlayer?.release()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(updateUIReceiver)
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