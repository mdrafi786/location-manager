package com.mdrafi.locationmanager.manager

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.mdrafi.locationmanager.constants.*
import com.mdrafi.locationmanager.model.CustomNotification
import com.mdrafi.locationmanager.model.LocationUpdate
import com.mdrafi.locationmanager.service.LocationService
import com.mdrafi.locationmanager.utils.SharedPrefUtil
import com.mdrafi.locationmanager.utils.showPermanentlyDeniedDialog
import com.mdrafi.locationmanager.utils.showRationaleDialog


class LocationUpdateManager(
    private val context: Context?,
    private val fetchLocationType: LocationType?,
    private val customNotification: CustomNotification?,
    val setOnLocationChangeListener: (LocationUpdate?) -> Unit = {}
) : PermissionRequest.Listener {

    companion object {
        /**
         * The identifier for the notification displayed for the foreground service.
         */
        const val NOTIFICATION_ID = 12345678
        var isBackground = false
    }

    private val myReceiver = MyReceiver()

    /**
     * The current location.
     */
    private var mLocation: LocationUpdate? = null

    /*
    * runtime permission request
    * */
    private val request by lazy {
        (context as FragmentActivity).permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).build()
    }


    /*
    * Builder class to create object of Location Update Manager
    * */
    data class Builder(
        private var activity: Activity?,
        private var fetchLocationType: LocationType? = null,
        private var setOnLocationChangeListener: (LocationUpdate?) -> Unit = {},
        private var customNotification: CustomNotification? = null
    ) {

        fun build() =
            LocationUpdateManager(
                activity,
                fetchLocationType,
                customNotification,
                setOnLocationChangeListener
            )
    }

    init {
        isBackground = false
        context?.let { SharedPrefUtil.init(it) }
    }

    /*
    * This method request for runtime location permission
    * */
    fun startLocationUpdates() {
        request.addListener(this)
        request.send()
    }

    /*
    * This method start the location service based on LocationType like Onetime and Continuously
    * */
    private fun startService() {
        if (fetchLocationType == LocationType.OneTime) {
            context?.startService(
                Intent(
                    context,
                    LocationService::class.java
                ).setAction(LocationType.OneTime.name)
            )
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(
                    Intent(
                        context,
                        LocationService::class.java
                    ).setAction(LocationType.Continuously.name)
                        .putExtra(IntentKeys.NOTIFICATION, customNotification)
                )
            } else {
                context?.startService(
                    Intent(
                        context,
                        LocationService::class.java
                    ).setAction(LocationType.Continuously.name)
                        .putExtra(IntentKeys.NOTIFICATION, customNotification)
                )
            }
        }
    }

    /*
    * Stop getting location
    * */
    fun stopLocationUpdates() {
        context?.startService(
            Intent(context, LocationService::class.java).setAction(
                ACTION_STOP_FOREGROUND_SERVICE
            )
        )
    }

    /*
    * This method register Local broadcast to getting callbacks
    * */
    fun registerReceiver() {
        isBackground = false
        context?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(
                myReceiver,
                IntentFilter(ACTION_BROADCAST)
            )
        }
    }

    /*
    * This method unregister Local broadcast to prevent memory leaks
    * */
    fun unregisterReceiver() {
        isBackground = true
        request.removeListener(this)
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(myReceiver)
        }
    }

    /**
     * Receiver for broadcasts sent by [LocationService].
     */
    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(LocationService.TAG, "Receiver")
            when (intent.action) {
                ACTION_BROADCAST -> {
                    Log.d(LocationService.TAG, "Action Broadcast")
                    val location =
                        intent.getParcelableExtra<LocationUpdate>(EXTRA_LOCATION)
                    mLocation = location
                    setOnLocationChangeListener(location)
                }
            }
        }
    }

    /*
     * Permission result callback method
     * */
    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> context?.showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> context?.showRationaleDialog(result, request)
            result.allGranted() -> startService()
        }
    }

    /*
    * This method gives list of location with timestamp when app is in background or killed state
    * */
    fun getBackgroundLocationUpdates(): ArrayList<LocationUpdate> {
        return LocationService.instance.getBackgroundLocationUpdateList()
    }

    fun isServiceRunning(): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        manager?.let {
            for (service in it.getRunningServices(Int.MAX_VALUE)) {
                if (LocationService::class.java.name == service.service.className) {
                    return true
                }
            }
        }
        return false
    }

}