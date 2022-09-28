package com.sample.locationmanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mdrafi.locationmanager.constants.EXTRA_STARTED_FROM_NOTIFICATION

class LauncherActivity : AppCompatActivity() {

    private var isFromNotification = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.d(MainActivity.TAG, "On Create of Splash")
        isFromNotification = intent?.getBooleanExtra(
            EXTRA_STARTED_FROM_NOTIFICATION,
            false
        ) == true
        Log.d(MainActivity.TAG, "Splash From Notification  : $isFromNotification")
        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_STARTED_FROM_NOTIFICATION, isFromNotification)
                startActivity(this)
                finish()
            }
        }, 3000)
    }

    override fun onResume() {
        Log.d(MainActivity.TAG, "On Resume of Splash")
        super.onResume()
    }

}