package com.bringg.extensions

import java.util.*
import java.util.concurrent.TimeUnit

fun Calendar.asStartOfDay() : Long{
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return timeInMillis
}

fun Calendar.asEndOfDay() : Long {
    return asStartOfDay() + TimeUnit.DAYS.toMillis(1) - 1
}