package com.bringg.extensions

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

fun AndroidViewModel.getContext() : Context {
    return (getApplication() as Application).applicationContext
}