package com.withings.sdk.sample

import android.app.Application
import com.withings.library.webble.background.WithingsDeviceIdentity
import com.withings.library.webble.background.WithingsSyncService

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // You should start the syncService at the start of your application with the identities of the devices you want to sync
        // You may find them thanks to public api : /v2/user?action=getdevice
        val deviceIdentity = WithingsDeviceIdentity(
            id = "00:24:e4:00:00:00",
            advertisingKey = "advertisingKeyOfDevice"
        )
        // Know that if you start without (background) location permission, the service will never synchronize your devices
        val syncService = WithingsSyncService.get(this)
        syncService.start(listOf(deviceIdentity))
        syncService.setListener(WithingsSyncListener())
    }
}
