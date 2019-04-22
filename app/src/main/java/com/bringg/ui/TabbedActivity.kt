package com.bringg.ui

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bringg.R
import com.bringg.data.local.entity.WorkAddress
import com.bringg.extensions.hasLocationPermission
import com.bringg.extensions.requestLocationPermission
import com.bringg.extensions.showToast
import com.bringg.geo.GeoTransitions
import com.bringg.geo.Utils
import com.bringg.vm.WorkAddressVM
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.activity_tabbed.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TabbedActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private lateinit var geoFencingClient: GeofencingClient
    private lateinit var workAddressVM: WorkAddressVM

    // create geo fencing pending intent (in lazy way)
    private val geoFencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeoTransitions::class.java)
        // create a broad cast and not intent service to support background triggering
        PendingIntent.getBroadcast(this, RC_GEO_PENDING_INTENT, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tabbed)

        geoFencingClient = LocationServices.getGeofencingClient(this)

        workAddressVM = ViewModelProviders.of(this).get(WorkAddressVM::class.java)

        setSupportActionBar(toolbar)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        // Check if the app has permission to access location info
        if (applicationContext.hasLocationPermission()) {
            // Permission granted, invoke init geo fences
            monitorGeoFences()
        } else {
            // Permission is not granted, ask for it
            requestLocationPermission(RC_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (RC_LOCATION == requestCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission granted
                monitorGeoFences()
            } else {
                // permission denied
                showToast("Permission denied...")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Create and monitor geo fence for the last saved work address
     *
     */
    @SuppressLint("MissingPermission")
    private fun monitorGeoFences() {
        // get work address data, and listen for updates
        workAddressVM.getWorkAddress().observe(this, Observer<List<WorkAddress>> { workAdresses ->

            GlobalScope.launch {
                // stop and remove current geo fences
                geoFencingClient.removeGeofences(geoFencePendingIntent)

                if (workAdresses.isNotEmpty()) {
                    // create geo fences list with capacity of 1 object
                    val geoFences = ArrayList<Geofence>(1)

                    // get the first work address in list
                    val entry = workAdresses[0]
                    // create an api geo fence object and add it to the list
                    geoFences.add(Utils.createGeofence(entry.id, entry.lat, entry.lon))

                    // make sure the app has location access permission
                    if (applicationContext.hasLocationPermission()) {
                        geoFencingClient.addGeofences(getGeoFencingRequest(geoFences), geoFencePendingIntent)
                            .addOnSuccessListener {
                                Log.d(TAG, "Geo fences monitoring started")
                            }
                            .addOnFailureListener {
                                Log.e(TAG, "add geo fences failed: $it")
                                showToast("add geo fences failed: $it")
                            }

                    } else {
                        Log.w(TAG, "monitorGeoFences called but location access not granted")
                    }
                }
            }
        })
    }

    /**
     * Create geo fencing request with the give [geoFences]
     * @param geoFences api geo fences objects
     * @return Geo fencing request instance
     */
    private fun getGeoFencingRequest(geoFences: ArrayList<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            addGeofences(geoFences)
            setInitialTrigger(0)
        }.build()
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> FragmentConfiguration()
            else -> FragmentStatistics()
        }

        override fun getCount(): Int = 2
    }

    companion object {
        const val TAG = "TabbedActivity"
        const val RC_LOCATION = 1
        const val RC_GEO_PENDING_INTENT = 2
    }
}
