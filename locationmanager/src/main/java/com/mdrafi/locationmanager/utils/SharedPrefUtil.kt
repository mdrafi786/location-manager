package com.mdrafi.locationmanager.utils

import android.content.Context
import android.content.SharedPreferences
import com.mdrafi.locationmanager.constants.PreferenceKeys

object SharedPrefUtil {
    private const val NAME = PreferenceKeys.PREF_SHARED_PREFS_NAME
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val LOCATIONUPDATES =
        Pair(PreferenceKeys.KEY_REQUESTING_LOCATION_UPDATES, false)


    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            NAME,
            MODE
        )
    }

    val instance
        get() = preferences

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var requestLocationUpdates: Boolean
        get() = preferences.getBoolean(LOCATIONUPDATES.first, LOCATIONUPDATES.second)
        set(value) = preferences.edit {
            it.putBoolean(LOCATIONUPDATES.first, value)
            it.commit()
        }

}