# **Location Manager**

Location Manager is a library that helps to easily fetch location when your app is in forground, background or in killed state.

## Features

* One time location
* Continuous location with regular interval.

## Installation
1. Add the following to the `settings.gradle` file.
   ```gradle
   dependencyResolutionManagement {
      repositories {
          maven { url 'https://jitpack.io' }
    }}
   ```
2. Add library dependency to the app level `build.gradle` file.
   ```gradle
    dependencies {
	   implementation 'com.github.mdrafi786:location-manager:$latest_stable_version'
	}
   ```
3. Add these permission to `Manifest.xml` like this 
   ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   ```
   > **Note : You have to `ask location permission at runtime` to get location. Without location permission it doesn't work.**

## Usage
* Firstly, you need create notification channel in your `Application`  class. Because in above oreo you have to create notification channel to show notification. We are going to show notification when we fetch location continuously.

  ```kotlin
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
  ```
* To Get `OneTime` location you have to start service with action `ACTION_ONE_TIME`:

  ```kotlin
     Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_ONE_TIME
                startService(this)
            }
  ```
* To Get `Continuous` location you have to start service with action `ACTION_START`:
  ```kotlin
    Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
  ```
  
* To Stop `Continuous` location you have to start service with action `ACTION_STOP`:
  ```kotlin
    Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
  ```
  
 > **Note : You only need to `stop service` if you start service to get location update `continuously`.**

## License
* [BigOhNotation](https://www.bigohtech.com/)
