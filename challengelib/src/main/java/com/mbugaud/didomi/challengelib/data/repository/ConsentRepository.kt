package com.mbugaud.didomi.challengelib.data.repository

import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.mbugaud.didomi.challengelib.data.ConsentNetworkData
import com.mbugaud.didomi.challengelib.data.ConsentStatus
import java.util.*

/**
 * Repository to retrieve and save Consent data from local and remote data sources.
 */
internal class ConsentRepository(private val advertisingIdDataSource: AdvertisingIdDataSource,
                        private val consentLocalDataSource: ConsentLocalDataSource,
                        private val consentRemoteDataSource: ConsentRemoteDataSource) {

    /**
     * Retrieve the last consent status saved locally from SharedPreferences.
     * @return The last saved consent status
     */
    fun getConsentStatus() = consentLocalDataSource.getConsentStatus()

    /**
     * Retrieve if the remote server was updated without any error with the last consent status.
     * This is a local check, we don't ask to the remote server its last saved consent status.
     * @return **true** if the remote server was successfully updated, **false** otherwise.
     */
    fun isRemotelyUpdated() = consentLocalDataSource.isRemotelyUpdated()

    /**
     * Save the last consent status locally with the current date and then send the consent to
     * remote server.
     * @param consentStatus The consent status to send to remote server
     * @throws ConsentApiException
     * @throws IllegalStateException
     * @throws GooglePlayServicesNotAvailableException
     * @throws GooglePlayServicesRepairableException
     */
    suspend fun updateConsent(consentStatus: ConsentStatus) {
        // Save the status and the timestamp of the consent from local repository
        consentLocalDataSource.setConsentStatus(consentStatus)
        val currentTimestamp = System.currentTimeMillis()
        consentLocalDataSource.setConsentUpdateTimestamp(currentTimestamp)

        // Reset the isRemotelyUpdated state from local repository
        consentLocalDataSource.setIsRemotelyUpdated(false)

        // Send data to remote
        sendConsentToRemote(consentStatus)
    }

    /**
     * Retrieve the advertising ID of the device and send the consent with advertising ID and the
     * date of the last consent update to the remote server.
     * @param consentStatus The consent status to send to remote server
     * @throws ConsentApiException
     * @throws IllegalStateException
     * @throws GooglePlayServicesNotAvailableException
     * @throws GooglePlayServicesRepairableException
     */
    suspend fun sendConsentToRemote(consentStatus: ConsentStatus) {
        val adId = advertisingIdDataSource.getAdvertisingId()
        val consent = ConsentNetworkData(
            consentStatus,
            adId,
            Date(consentLocalDataSource.getConsentUpdateTimestamp())
        )
        val response = consentRemoteDataSource.sendConsent(consent)
        if (response.isSuccessful) {
            consentLocalDataSource.setIsRemotelyUpdated(true)
        } else {
            // Fail to send data to remote server
            throw ConsentApiException(
                "Fail to send Consent to remote server, response code: ${response.code()}",
                response.code()
            )
        }
    }
}

/**
 * Exception thrown when remote server return an error response.
 * @param message Description of the exception
 * @param responseCode The HTTP response code returned by the remote server
 */
class ConsentApiException(message: String, val responseCode: Int) : Exception(message)