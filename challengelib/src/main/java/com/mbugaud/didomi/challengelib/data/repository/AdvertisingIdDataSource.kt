package com.mbugaud.didomi.challengelib.data.repository

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Data Source to retrieve the device Advertising ID.
 */
internal class AdvertisingIdDataSource(private val context: Context) {

    /**
     * @return The Advertising ID of the device or throws an exception on failure.
     * @throws IllegalStateException
     * @throws GooglePlayServicesNotAvailableException
     * @throws GooglePlayServicesRepairableException
     */
    @Throws(
        IllegalStateException::class,
        GooglePlayServicesNotAvailableException::class,
        GooglePlayServicesRepairableException::class
    )
    suspend fun getAdvertisingId(): String = withContext(Dispatchers.IO) {
        // False positive warning with IOException: https://youtrack.jetbrains.com/issue/KTIJ-838
        AdvertisingIdClient.getAdvertisingIdInfo(context).id
    }
}