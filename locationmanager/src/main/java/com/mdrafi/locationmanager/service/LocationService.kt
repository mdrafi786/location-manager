package com.mdrafi.locationmanager.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.mdrafi.locationmanager.constants.*
import com.mdrafi.locationmanager.manager.LocationUpdateManager
import com.mdrafi.locationmanager.model.CustomNotification
import com.mdrafi.locationmanager.model.LocationUpdate


class LocationService : Service() {
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var mLocation: LocationUpdate? = null
    private var mNotificationManager: NotificationManager? = null
    private var mCustomNotification: CustomNotification? = null


    companion object {
        //region data
        val TAG: String = LocationServices::class.java.simpleName
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 2000

        val instance: LocationService
            get() = LocationService()
        private var mBackgroundLocationList: ArrayList<LocationUpdate> = ArrayList()
    }

    //onCreate
    override fun onCreate() {
        super.onCreate()
        Log.d("CustomTag", "OnCreate() fo Service")
        mBackgroundLocationList.clear()
        initData()
    }

    //Location Callback
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation: Location? = locationResult.lastLocation
            if (currentLocation == null) {
                Log.d(
                    TAG,
                    "Current Location is null"
                )
            }
            mLocation = LocationUpdate(System.currentTimeMillis().toString(), currentLocation)
            // Notify anyone listening for broadcasts about the new location.
            Log.d("MyTag", "Service is in background : ${LocationUpdateManager.isBackground}")
            if (LocationUpdateManager.isBackground) {
                mLocation?.let {
                    mBackgroundLocationList.add(it)
                }
            }
            val intent = Intent(ACTION_BROADCAST)
            intent.putExtra(EXTRA_LOCATION, mLocation)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            updateNotificationContent()
            Log.d(
                TAG,
                currentLocation?.latitude.toString() + "," + currentLocation?.longitude
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            LocationType.OneTime.name -> {
                lastLocation
            }
            LocationType.Continuously.name -> {
                mCustomNotification = intent.getParcelableExtra(IntentKeys.NOTIFICATION)
                lastLocation
                prepareForegroundNotification()
                startLocationUpdates()
            }
            ACTION_STOP_FOREGROUND_SERVICE -> {
                removeLocationUpdates()
            }
        }
        return START_STICKY
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw Exception("Missing Location Permission")
        }
        locationRequest?.let {
            mFusedLocationClient?.requestLocationUpdates(
                it,
                locationCallback, Looper.getMainLooper()
            )
        }
    }

    private fun prepareForegroundNotification() {
        if (mCustomNotification != null) {
            mCustomNotification?.notification?.let {
                startForeground(
                    LocationUpdateManager.NOTIFICATION_ID,
                    it
                )
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                mNotificationManager = getSystemService(NotificationManager::class.java)
                mNotificationManager?.createNotificationChannel(serviceChannel)
            }
            startForeground(LocationUpdateManager.NOTIFICATION_ID, getNotification())
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun initData() {
        locationRequest = LocationRequest.create()
        locationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest?.priority = Priority.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    private val lastLocation: Unit
        get() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                throw Exception("Missing Location Permission")
            }

            mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                Log.d(TAG, "addOnCompleteListener called")
                if (task.isSuccessful && task.result != null) {
                    val currentLocation = task.result
                    if (currentLocation == null) {
                        Log.d(
                            TAG,
                            "Current Location is null"
                        )
                    }
                    Log.d(
                        TAG,
                        currentLocation?.latitude.toString() + "," + currentLocation?.longitude
                    )
                    mLocation =
                        LocationUpdate(System.currentTimeMillis().toString(), currentLocation)
                    val intent = Intent(ACTION_BROADCAST)
                    intent.putExtra(EXTRA_LOCATION, mLocation)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//                    updateNotificationContent()
                } else {
                    Log.d(TAG, "Failed to get location.")
                }
            }
        }

    fun updateNotificationContent() {
        Log.d(TAG, "updateNotificationContent() called")
        // Update notification content if running as a foreground service.
        mCustomNotification?.let {
            it.notificationManager?.notify(LocationUpdateManager.NOTIFICATION_ID, it.notification)
        } ?: run {
            mNotificationManager?.notify(
                LocationUpdateManager.NOTIFICATION_ID,
                getNotification()
            )
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    private fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mBackgroundLocationList.clear()
            mFusedLocationClient?.removeLocationUpdates(locationCallback)
            stopForeground(true)
            stopSelf()
        } catch (unlikely: SecurityException) {
            throw  Exception("Lost location permission. Could not remove updates. $unlikely")
        }
    }

    private fun getNotification(): Notification {

        val pm = packageManager
        val notificationIntent = pm.getLaunchIntentForPackage(applicationContext.packageName)
        notificationIntent?.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)
        var icon: Drawable? = null
        try {
            icon = pm.getApplicationIcon(applicationContext.packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            123,
            notificationIntent, 0
        )
        val builder = NotificationCompat.Builder(this)
            .setContentText("Location Service is running")
            .setSmallIcon(IconCompat.createWithBitmap((icon as BitmapDrawable).bitmap))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())


        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }
        return builder.build()
    }

    fun getBackgroundLocationUpdateList(): ArrayList<LocationUpdate> {
        val list = ArrayList<LocationUpdate>()
        list.addAll(mBackgroundLocationList)
        return list
    }
}