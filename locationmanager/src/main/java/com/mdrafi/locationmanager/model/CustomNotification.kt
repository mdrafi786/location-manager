package com.mdrafi.locationmanager.model

import android.app.Notification
import android.app.NotificationManager
import android.os.Parcel
import android.os.Parcelable


data class CustomNotification(
    val notificationManager: NotificationManager?,
    val notification: Notification?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        null,
        parcel.readParcelable(Notification::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(notification, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomNotification> {
        override fun createFromParcel(parcel: Parcel): CustomNotification {
            return CustomNotification(parcel)
        }

        override fun newArray(size: Int): Array<CustomNotification?> {
            return arrayOfNulls(size)
        }
    }
}
