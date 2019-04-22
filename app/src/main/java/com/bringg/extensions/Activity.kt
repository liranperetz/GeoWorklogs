package com.bringg.extensions

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.core.app.ActivityCompat

fun Activity.requestLocationPermission(rc: Int) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        rc
    )
}

fun Activity.showToast(msg: String){
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}