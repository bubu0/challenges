package com.mbugaud.didomi.challengelib

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.mbugaud.didomi.challengelib.api.ConsentService
import com.mbugaud.didomi.challengelib.data.ConsentStatus
import com.mbugaud.didomi.challengelib.data.repository.AdvertisingIdDataSource
import com.mbugaud.didomi.challengelib.data.repository.ConsentLocalDataSource
import com.mbugaud.didomi.challengelib.data.repository.ConsentRemoteDataSource
import com.mbugaud.didomi.challengelib.data.repository.ConsentRepository
import kotlinx.coroutines.*

/**
 * Class to manage consent by showing Consent Dialog and save the user's choice.
 * The consent selected by user is sent to a remote server.
 * You must call the [initialize] method with the Application's [Context] in the 'onCreate' method
 * of your Application class.
 * Then you can access to ConsentManager unique instance by calling [ConsentManager.instance].
 */
class ConsentManager private constructor(applicationContext: Context) {

    private val consentRepository = ConsentRepository(
        AdvertisingIdDataSource(applicationContext),
        ConsentLocalDataSource(applicationContext),
        ConsentRemoteDataSource(ConsentService.create())
    )

    /**
     * Check if there is a consent dialog to show and if we need
     * to send data to remote server if it's not synchronized with local consent status.
     */
    fun setUp(context: Context) {
        val consentStatus = consentRepository.getConsentStatus()
        if (consentStatus == ConsentStatus.UNDEFINED) {
            showConsentDialog(context)
        } else if (!consentRepository.isRemotelyUpdated()) {
            // We are not synchronized with remote server, send last consent status.
            GlobalScope.launch {
                runCatching {
                    consentRepository.sendConsentToRemote(consentStatus)
                }.onFailure {
                    it.printStackTrace()
                    // TODO What to do on error?
                }
            }
        }
    }

    /**
     * Show a Dialog to user to accept or deny the consent
     * @param context Context to build the Dialog
     * @param customTitle Replace the default title if not null
     * @param customMessage Replace the default message if not null
     */
    fun showConsentDialog(
        context: Context,
        customTitle: String? = null,
        customMessage: String? = null
    ) {
        val appName = context.applicationInfo.loadLabel(context.packageManager)
        val title = customTitle ?: context.getString(R.string.didomi_consent_dialog_title)
        val message =
            customMessage ?: context.getString(R.string.didomi_consent_dialog_message, appName)

        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.didomi_consent_accept) { _, _ ->
                setConsentStatus(ConsentStatus.ACCEPTED, onError = {
                    it.printStackTrace()
                    // TODO What to do on error?
                })
            }
            setNegativeButton(R.string.didomi_consent_deny) { _, _ ->
                setConsentStatus(ConsentStatus.DENIED, onError = {
                    it.printStackTrace()
                    // TODO What to do on error?
                })
            }
        }.show()
    }

    /**
     * @return The last consent status selected by the user, or [ConsentStatus.UNDEFINED] if no value selected
     */
    fun getConsentStatus() = consentRepository.getConsentStatus()

    /**
     * Save the given consent status locally and send it to remote server.
     * @param consentStatus The consent selected by user
     * @param onSuccess Callback to notify if the data was successfully sent to remote server
     * @param onError Callback to notify if there was an error when sending data
     */
    fun setConsentStatus(
        consentStatus: ConsentStatus,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        GlobalScope.launch {
            runCatching {
                consentRepository.updateConsent(consentStatus)
                withContext(Dispatchers.Main) {
                    onSuccess?.invoke()
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    onError?.invoke(it)
                }
            }
        }
    }

    companion object {
        /**
         * Private unique instance for Singleton pattern
         */
        private lateinit var sInstance: ConsentManager

        /**
         * Initialize the [ConsentManager], must be called only once and before to access to [instance].
         */
        fun initialize(context: Context) {
            sInstance = ConsentManager(context)
        }

        /**
         * The unique instance to access to [ConsentManager] methods.
         */
        val instance: ConsentManager
            get() {
                return sInstance
            }
    }
}