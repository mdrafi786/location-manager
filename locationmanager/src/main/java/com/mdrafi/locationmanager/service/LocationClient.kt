package com.mdrafi.locationmanager.service

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long = 1000L, oneTime: Boolean): Flow<Location>

    class LocationException(message: String) : Exception()
}
