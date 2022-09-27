package com.sample.locationmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mdrafi.locationmanager.model.LocationUpdate
import com.sample.locationmanager.databinding.ActivityBackgroundPointsBinding

class BackgroundPointsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackgroundPointsBinding
    private var backgroundPointList: ArrayList<LocationUpdate>? = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackgroundPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backgroundLocationTv.text = getString(R.string.no_points)
        val intentData =
            intent.getParcelableArrayListExtra<LocationUpdate>("BackgroundPoints") as ArrayList
        backgroundPointList?.addAll(intentData)
        setClickListeners()
        if (backgroundPointList?.isNotEmpty() == true)
            showPoints()
    }

    private fun setClickListeners() {
        binding.backIv.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showPoints() {
        val builder = StringBuilder("")
        backgroundPointList?.forEach {
            builder.append("Lat Long : ${it.location?.latitude}  ${it.location?.longitude} and TimeStamp : ${it.timestamp}\n")
        }
        binding.backgroundLocationTv.text = builder
    }
}