package com.sample.locationmanager

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.mdrafi.locationmanager.constants.EXTRA_STARTED_FROM_NOTIFICATION
import com.mdrafi.locationmanager.constants.LocationType
import com.mdrafi.locationmanager.manager.LocationUpdateManager
import com.mdrafi.locationmanager.model.CustomNotification
import com.mdrafi.locationmanager.model.LocationUpdate
import com.sample.locationmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mLocationManager: LocationUpdateManager? = null
    private var backgroundPointList: ArrayList<LocationUpdate>? = ArrayList()

    companion object {
        // Used in checking for runtime permissions.
        const val TAG = "CustomTag"
        private const val CHANNEL_ID = "channel_123"
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d(TAG, "On new intent called")
        handleIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleIntent(intent)
        setClickListener()
        initLocationManager()
    }

    private fun initLocationManager() {
        mLocationManager =
            LocationUpdateManager.Builder(
                activity = this,
                fetchLocationType = LocationType.Continuously,
                customNotification = createCustomNotification(),
                setOnLocationChangeListener = {
                    it?.let {
                        binding.latLongTv.text =
                            String.format(
                                getString(R.string.lat_long),
                                it.location?.latitude,
                                it.location?.longitude
                            )
                    }
                }
            ).build()
    }

    private fun setClickListener() {
        binding.requestLocationUpdatesButton.setOnClickListener {
            if (mLocationManager?.isServiceRunning() == false)
                mLocationManager?.startLocationUpdates()
        }

        binding.removeLocationUpdatesButton.setOnClickListener {
            binding.latLongTv.text = ""
            mLocationManager?.stopLocationUpdates()
        }

        binding.showBackgroundPointsButton.setOnClickListener {
            showBackgroundLocationUpdates()
            Intent(this, BackgroundPointsActivity::class.java).apply {
                putParcelableArrayListExtra("BackgroundPoints", backgroundPointList)
                startActivity(this)
            }
        }


    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "OnResume of MainActivity")
        mLocationManager?.registerReceiver()
    }

    override fun onStop() {
        Log.d(TAG, "OnStop() of MainActivity")
        mLocationManager?.unregisterReceiver()
        super.onStop()
    }

    private val notification: Notification
        get() {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

            // The PendingIntent to launch activity.
            val activityPendingIntent = PendingIntent.getActivity(
                this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder =
                NotificationCompat.Builder(this)
                    .setContentText("Custom Location Service running")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentIntent(activityPendingIntent)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())

            // Set the Channel ID for Android O.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID) // Channel ID
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.mipmap.ic_launcher_adaptive_fore)
            }
            return builder.build()
        }

    private fun createCustomNotification(): CustomNotification {
        val notificationManager =
            applicationContext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel =
                NotificationChannel(
                    CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT
                )

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel)
        }
        return CustomNotification(
            notificationManager = notificationManager,
            notification = notification
        )
    }

    private fun showBackgroundLocationUpdates() {
        mLocationManager?.getBackgroundLocationUpdates()?.let {
            backgroundPointList?.clear()
            backgroundPointList?.addAll(it)
        }
        Log.d(TAG, "Background Points size ${backgroundPointList?.size}")
        /* mLocationManager?.getBackgroundLocationUpdates()?.forEach {
             Log.d(
                 TAG,
                 "Lat Long : ${it.location?.latitude}  ${it.location?.longitude} and TimeStamp : ${it.timestamp}"
             )
         }*/
    }

    private fun handleIntent(intent: Intent?) {
        val fromNotification = intent?.getBooleanExtra(
            EXTRA_STARTED_FROM_NOTIFICATION,
            false
        )
        binding.fromNotification.text =
            String.format(getString(R.string.from_notification), fromNotification)
        Log.d(TAG, "From Notification : $fromNotification")
    }
}