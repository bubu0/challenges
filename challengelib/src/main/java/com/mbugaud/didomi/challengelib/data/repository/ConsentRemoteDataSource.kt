package com.mbugaud.didomi.challengelib.data.repository

import com.mbugaud.didomi.challengelib.api.ConsentService
import com.mbugaud.didomi.challengelib.data.ConsentNetworkData

/**
 * Data Source to send Consent data to Remote Server
 */
class ConsentRemoteDataSource(private val api: ConsentService) {

    /**
     * Send the Consent data to Remote Server.
     * @param consentNetworkData The consent data to send to the remote server.
     * @return The response in String format.
     */
    suspend fun sendConsent(consentNetworkData: ConsentNetworkData) = api.sendConsent(consentNetworkData)
}