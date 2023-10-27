//Mar
package com.example.activityrecognizationapi


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofenceStatusCodes
import kotlin.properties.Delegates

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "GeofenceBroadcastReceiv"
    private val GEOFENCE_ID1 = "Innovation Studio"
    private val GEOFENCE_ID2 = " Salisbury Labs"
    private lateinit var mainActivity: MainActivity
    private var intSend =0
    private fun incrementCounter(context: Context, geofenceId: String) {
        when (geofenceId) {
            GEOFENCE_ID1 -> {
                intSend=100
            }

            GEOFENCE_ID2 -> {
                intSend=200
            }
        }

        // Send a broadcast to indicate that the counters have been updated
        val intent = Intent("UPDATE_UI_ACTION")
        intent.putExtra("GeofenceCounter", intSend)
        context.sendBroadcast(intent)
    }


    override fun onReceive(context: Context?, intent: Intent?) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Toast.makeText(context?.applicationContext, "Error in broadcast receiver", Toast.LENGTH_SHORT).show()
            return
        }
        val geofenceList: List<Geofence> = geofencingEvent.triggeringGeofences
        for (geofence in geofenceList) {
            Log.d(TAG, "onReceive: ${geofence.requestId}")
        }
//        Location location = geofencingEvent.getTriggeringLocation()

        val transitionType = geofencingEvent.geofenceTransition

        for (geofence in geofenceList) {
            when (transitionType) {
                Geofence.GEOFENCE_TRANSITION_DWELL -> {
                    val ID = geofence.requestId

                    // Use the entry count from GeofenceHelper
                    Toast.makeText(
                        context,
                        "You have been inside the $ID geofence for 5 seconds, incrementing counter",
                        Toast.LENGTH_LONG
                    ).show()
                    if (context != null) {
                        incrementCounter(context, ID)
                    }
                }
            }
        }
    }
}



