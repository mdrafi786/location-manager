package com.sample.locationmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.view.ContentInfoCompat
import com.mdrafi.locationmanager.constants.EXTRA_STARTED_FROM_NOTIFICATION

class LauncherActivity : AppCompatActivity() {

    private var isFromNotification = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.d(MainActivity.TAG, "On Create of Splash")
        handleIntent(intent)
        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }, 3000)
    }

    override fun onResume() {
        Log.d(MainActivity.TAG, "On Resume of Splash")
        super.onResume()
    }

    private fun handleIntent(intent: Intent?) {
        isFromNotification = intent?.getBooleanExtra(
            EXTRA_STARTED_FROM_NOTIFICATION,
            false
        ) == true
        Log.d(MainActivity.TAG, "Splash From Notification  : $isFromNotification")
    }
}