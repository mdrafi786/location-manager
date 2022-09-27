package com.mdrafi.locationmanager.model

import android.location.Location
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationUpdate(
    val timestamp: String?,
    val location: Location?
) : Parcelable
