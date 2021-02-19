package com.mbugaud.didomi.challengelib.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.mbugaud.didomi.challengelib.data.ConsentStatus

/**
 * Data source class to save user's consents state persistently in SharedPreferences:
 * * Status: The ordinal of the [ConsentStatus] enum that represent user's choice.
 * * Timestamp: The date in timestamp format of the last user's choice.
 * * Is remotely updated: A boolean to know if the consent was successfully sent to the remote server.
 */
internal class ConsentLocalDataSource(private val context: Context) {

    private val sharedPreferencesName = "ConsentPreferences"
    private val keyConsentStatus = "KEY_CONSENT_STATUS"
    private val keyConsentUpdateTimestamp = "KEY_CONSENT_UPDATE_TIMESTAMP"
    private val keyConsentIsRemotelyUpdated = "KEY_CONSENT_IS_REMOTELY_UPDATED"

    /**
     * @return The [SharedPreferences] in Private Mode to save Consent relative data for the application.
     */
    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    }

    /**
     * @return The last saved consent status, or [ConsentStatus.UNDEFINED] if no value saved
     */
    internal fun getConsentStatus(): ConsentStatus {
        // Retrieve saved ConsentStatus ordinal, use UNDEFINED by default if no value saved.
        val consentStatusOrdinal = getSharedPreferences().getInt(keyConsentStatus, ConsentStatus.UNDEFINED.ordinal)
        return if (consentStatusOrdinal >= 0 && consentStatusOrdinal < ConsentStatus.values().size) {
            ConsentStatus.values()[consentStatusOrdinal]
        } else {
            // If the ordinal doesn't match with the enum, return UNDEFINED
            ConsentStatus.UNDEFINED
        }
    }

    /**
     * @return The timestamp (in ms) of last consent update (0 if the consent was never collected)
     */
    internal fun getConsentUpdateTimestamp(): Long {
        return getSharedPreferences().getLong(keyConsentUpdateTimestamp, 0)
    }

    /**
     * @return **true** if the last consent was successfully sent to the remote server, **false** otherwise.
     */
    internal fun isRemotelyUpdated(): Boolean {
        return getSharedPreferences().getBoolean(keyConsentIsRemotelyUpdated, false)
    }

    /**
     * Save the consent status persistently in shared preferences (by saving [ConsentStatus] ordinal)
     * @param consentStatus The last consent status chosen by the user
     */
    internal fun setConsentStatus(consentStatus: ConsentStatus) {
        getSharedPreferences().edit().putInt(keyConsentStatus, consentStatus.ordinal).apply()
    }

    /**
     * Save the date when consent was updated persistently in shared preferences
     * @param timestamp The timestamp in ms
     */
    internal fun setConsentUpdateTimestamp(timestamp: Long) {
        getSharedPreferences().edit().putLong(keyConsentUpdateTimestamp, timestamp).apply()
    }

    /**
     * Save the remote update state persistently in shared preferences
     * @param isRemotelyUpdated **true** if the last consent was successfully sent to the remote server, **false** otherwise.
     */
    internal fun setIsRemotelyUpdated(isRemotelyUpdated: Boolean) {
        getSharedPreferences().edit().putBoolean(keyConsentIsRemotelyUpdated, isRemotelyUpdated).apply()
    }

    /**
     * Clear all data from SharedPreferences
     */
    internal fun clearData() {
        getSharedPreferences().edit().clear().apply()
    }
}