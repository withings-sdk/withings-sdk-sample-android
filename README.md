# Withings Android SDK Sample

> The snippets on this page show a simple, light-touch integration of the Withings SDK to your Android App

# Android Withings SDK 

## Minimum requirements

- Android 7
- Androidx

## Add the Withings SDK dependencies

Add the dependency to your **build.gradle**:

```grouvy
repositories {
    ...
    maven { url = "https://withings.jfrog.io/artifactory/androidsdk-mvn-prod/" }
}

dependencies {
    ...
    implementation "com.withings.library:webble:6.1"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_version"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinx_version"
}
```

## Add the library permissions

You will need some application permissions for the library to work.

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

?> **Important**: The Withings SDK will not require the user to turn ON the Bluetooth. It is recommended that your app ask the user to turn on Bluetooth.

?> **Important**: The Withings SDK will ask the user the location permission to enable BLE scan if the app has not. It is recommended that your app ask the user to enable location permission to provide the right level of information. For more information, please [refer here](http://developer.withings.com/sdk/#/?id=connectivity-and-permissions).

## Initialize the Withings SDK

Please follow the following steps to implement the Withings SDK:

1. Add a [WithingsFragment](https://developer.withings.com/sdk/#/android/WithingsFragment) to your `Activity` and load your redirection URL for install or settings.

```kotlin
WithingsFragment.newInstance(myInstallUrl)
WithingsFragment.newInstance(myDevicesUrl, accessToken)
```

2. Add the two callbacks :

- `setCloseIntentListener(...)`
- `setNotificationListener(...)`

3. (Optional) If you want the SDK to look for device to synchronize, you can use the [WithingsSyncService](https://developer.withings.com/sdk/#/android/WithingsSyncService).

In your own implementation of the `android.app.Application` you con call : 

`WithingsSyncService.get(context).start(...)` with the information of the specific devices you want to synchronize. You'll need to have the location permission for ir to work.

This service requires you to provide one or more Withings's devices tokens. There is two ways to get a device token:

- After installing a Withings device with the SDK, you will receive a notification from your `NotificationListener` "Installation Success" with a parameter `advertise_key` which contains your device token.
- Through the Withings API [User v2 - Getdevice](https://developer.withings.com/oauth2/#operation/userv2-activate). This API lists your devices, the device token is stored with the advertise_key key.
