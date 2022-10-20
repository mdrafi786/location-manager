package com.sample.locationmanager

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mdrafi.locationmanager.service.LocationService
import com.sample.locationmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClickListener()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )

        LocationService.locationResult.observe(this) {
            binding.latLongTv.text =
                String.format(
                    getString(R.string.lat_long),
                    it.latitude,
                    it.longitude
                )
        }
    }

    private fun setClickListener() {
        binding.getCurrentLocationBt.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_ONE_TIME
                startService(this)
            }
        }
        binding.requestLocationUpdatesButton.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
        }

        binding.removeLocationUpdatesButton.setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "OnResume of MainActivity")
    }

    override fun onStop() {
        Log.d(TAG, "OnStop() of MainActivity")
        super.onStop()
    }
}
