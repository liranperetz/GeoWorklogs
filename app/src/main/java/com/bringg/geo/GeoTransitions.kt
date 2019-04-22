package com.bringg.geo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bringg.data.local.RoomDB
import com.bringg.data.local.dao.WorkLogDao
import com.bringg.data.local.entity.WorkLog
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GeoTransitions : BroadcastReceiver() {

    private lateinit var workLogDao: WorkLogDao

    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            // convert intent to Geo fencing event
            val geoFencingEvent = GeofencingEvent.fromIntent(intent)

            // check if we got an error
            if (geoFencingEvent.hasError()) {
                Log.e(TAG, "geo fence triggered with error")
            } else {
                workLogDao = RoomDB.get(context).workLogDao

                // Get the transition type.
                val transition = geoFencingEvent.geofenceTransition

                when (transition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d(TAG, "geo fence triggered, user entered to geo fence")

                        geoFencingEvent.triggeringGeofences.forEach {
                            saveUserArrived(it.requestId.toInt())
                        }

                    }
                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d(TAG, "geo fence triggered, user exit from geo fence")

                        geoFencingEvent.triggeringGeofences.forEach {
                            saveUserLeave(it.requestId.toInt())
                        }
                    }
                    else -> {
                        Log.w(TAG, "geo fence triggered, but transition not supported")
                    }
                }
            }
        }
    }

    /**
     * Insert user arrived to geo fence work log record
     * @param workAddressId
     */
    private fun saveUserArrived(workAddressId: Int) = runBlocking {
        workLogDao.insert(
            WorkLog(
                arrivedAt = System.currentTimeMillis(),
                workAddress = workAddressId
            )
        )
    }

    /**
     * Update the last opened work log in the given [workAddressId]
     * @param workAddressId
     */
    private fun saveUserLeave(workAddressId: Int) = runBlocking {
        val lastOpened = workLogDao.getLastOpened(workAddressId)
        if (lastOpened != null) {
            lastOpened.leavedAt = System.currentTimeMillis()
            lastOpened.dwellTime = lastOpened.leavedAt - lastOpened.arrivedAt
            workLogDao.insert(lastOpened)
        } else {
            Log.e(TAG, "User leave geo fence but no open log found")
        }
    }

    companion object {
        const val TAG = "GeoTransitions"
    }
}