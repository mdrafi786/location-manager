package com.mdrafi.locationmanager.constants

import android.location.Location
import java.util.*


enum class LocationType {
    OneTime, Continuously
}

object PreferenceKeys {
    const val PREF_SHARED_PREFS_NAME = "AppPrefs"
    const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates"
}

/**
 * Returns the `location` object as a human readable string.
 * @param location  The [Location].
 */

const val PACKAGE_NAME = "com.mdrafi.locationmanager"

/**
 * The name of the channel for notifications.
 */
const val CHANNEL_ID = "channel_01"
const val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
const val EXTRA_LOCATION = "$PACKAGE_NAME.location"
const val ACTION_START_SERVICE = "$PACKAGE_NAME.startService"
const val ACTION_STOP_FOREGROUND_SERVICE = "$PACKAGE_NAME.stopForground"
const val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"

object IntentKeys {
    const val NOTIFICATION = "notification"
}
