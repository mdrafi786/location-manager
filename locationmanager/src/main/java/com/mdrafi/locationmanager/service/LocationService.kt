package com.mdrafi.locationmanager.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.mdrafi.locationmanager.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_ONE_TIME = "ACTION_ONE_TIME"

        var locationResult: MutableLiveData<Location> = MutableLiveData()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_ONE_TIME -> start(true)
            ACTION_START -> start(false)
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(oneTime: Boolean) {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        CoroutineScope(Dispatchers.IO).launch {
            locationClient
                .getLocationUpdates(oneTime = oneTime)
                .catch { e ->
                    e.printStackTrace()
                    Log.d("LocationService", e.message.toString())
                }
                .onEach { location ->
                    val lat = location.latitude.toString().takeLast(3)
                    val long = location.longitude.toString().takeLast(3)
                    Log.d("LocationService", "Location: ($lat, $long)")
                    val updatedNotification = notification.setContentText(
                        "Location: ($lat, $long)"
                    )
                    locationResult.postValue(location)
                    notificationManager.notify(1, updatedNotification.build())
                    if (oneTime) {
                        stop()
                    }
                }
                .launchIn(serviceScope)

            startForeground(1, notification.build())
        }
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
