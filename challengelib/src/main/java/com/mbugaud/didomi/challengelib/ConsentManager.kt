package com.mbugaud.didomi.challengelib

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.mbugaud.didomi.challengelib.api.ConsentService
import com.mbugaud.didomi.challengelib.data.ConsentStatus
import com.mbugaud.didomi.challengelib.data.repository.AdvertisingIdDataSource
import com.mbugaud.didomi.challengelib.data.repository.ConsentLocalDataSource
import com.mbugaud.didomi.challengelib.data.repository.ConsentRemoteDataSource
import com.mbugaud.didomi.challengelib.data.repository.ConsentRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ConsentManager private constructor(applicationContext: Context) {

    private val consentRepository = ConsentRepository(
        AdvertisingIdDataSource(applicationContext),
        ConsentLocalDataSource(applicationContext),
        ConsentRemoteDataSource(ConsentService.create())
    )

    /**
     * Initialize the [ConsentManager] to check if there is a consent dialog to show and if we need
     * to send data to remote server if it's not synchronized with local consent status.
     */
    fun setUp(context: Context) {
        val consentStatus = consentRepository.getConsentStatus()
        if (consentStatus == ConsentStatus.UNDEFINED) {
            showConsentDialog(context)
        } else if (!consentRepository.isRemotelyUpdated()) {
            // We are not synchronized with remote server, send last consent state.
            GlobalScope.launch {
                runCatching {
                    consentRepository.sendConsentToRemote(consentStatus)
                }.onFailure {
                    it.printStackTrace()
                    // TODO Do nothing ?
                }
            }
        }
    }

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
                })
            }
            setNegativeButton(R.string.didomi_consent_deny) { _, _ ->
                setConsentStatus(ConsentStatus.DENIED, onError = {
                    it.printStackTrace()
                })
            }
        }.show()
    }

    fun getConsentStatus() = consentRepository.getConsentStatus()

    fun setConsentStatus(
        consentStatus: ConsentStatus,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        GlobalScope.launch {
            runCatching {
                consentRepository.updateConsent(consentStatus)
                onSuccess?.invoke()
            }.onFailure {
                onError?.invoke(it)
            }
        }
    }

    companion object {
        /**
         * Unique instance
         */
        private lateinit var sInstance: ConsentManager

        fun initialize(context: Context) {
            sInstance = ConsentManager(context)
        }

        val instance: ConsentManager
            get() {
                return sInstance
            }
    }
}