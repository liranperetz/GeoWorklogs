package com.bringg.geo

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.Geofence
import java.lang.Exception
import java.util.concurrent.TimeUnit

class Utils {


    companion object {

        private const val TAG = "GeoUtils"

        private const val RADIUS = 50f

        private val EXPIRATION = TimeUnit.DAYS.toMillis(1)

        /**
         * get addresses objects by address string
         *
         * @param ctx
         * @param address
         * @return Address object
         */
        fun toAddressObject(ctx: Context, address: String): Address? =
            try {
                Geocoder(ctx).getFromLocationName(address, 1).let {
                    return if (it.size > 0) {
                        it[0]
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get address object by location name failed: $e")
                null
            }


        /**
         * Create geo fence object for google GeofenceApi
         * @param id geo fence id
         * @param lat geo fence lat
         * @param lon geo fence lon
         * @return geo fence object
         */
        fun createGeofence(id: Int, lat: Double, lon: Double): Geofence = Geofence.Builder()
            // Geo fence id
            .setRequestId(id.toString())

            // Set the circular region
            .setCircularRegion(lat, lon, RADIUS)

            // Set the expiration duration of the geofence
            .setExpirationDuration(EXPIRATION)

            // Set the transition types of interest
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

            // Build the object
            .build()

    }

}