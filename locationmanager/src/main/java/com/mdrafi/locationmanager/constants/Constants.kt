package com.mdrafi.locationmanager.constants


enum class LocationType {
    OneTime, Continuously
}

object PreferenceKeys {
    const val PREF_SHARED_PREFS_NAME = "AppPrefs"
    const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates"
}

const val PACKAGE_NAME = "com.mdrafi.locationmanager"

/**
 * The name of the channel for notifications.
 */
const val CHANNEL_ID = "channel_01"
const val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
const val EXTRA_LOCATION = "$PACKAGE_NAME.location"
const val ACTION_STOP_FOREGROUND_SERVICE = "$PACKAGE_NAME.stopForeground"
const val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"

object IntentKeys {
    const val NOTIFICATION = "notification"
    const val INTERVAL_TIME = "intervalTime"
    const val DISPLACEMENT = "displacement"
}
