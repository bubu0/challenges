package com.mbugaud.didomi.challengelib

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mbugaud.didomi.challengelib.data.ConsentStatus
import com.mbugaud.didomi.challengelib.data.repository.ConsentLocalDataSource
import com.mbugaud.didomi.challengelib.data.repository.ConsentRemoteDataSource

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Test the [ConsentLocalDataSource]
 */
@RunWith(AndroidJUnit4::class)
class ConsentLocalDataSourceTest {

    private lateinit var consentLocalDataSource: ConsentLocalDataSource

    /**
     * Init the [consentLocalDataSource] and clear all saved data before each test.
     */
    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        consentLocalDataSource = ConsentLocalDataSource(appContext)
        consentLocalDataSource.clearData()
    }

    /**
     * Test the default values returned by the [ConsentLocalDataSource] when there is no saved data.
     */
    @Test
    fun testReadClearedData() {
        assertEquals(
            "Consent status is not UNDEFINED when there is no saved preferences.",
            ConsentStatus.UNDEFINED,
            consentLocalDataSource.getConsentStatus()
        )
        assertEquals(
            "Timestamp is not 0 when there is no saved preferences.",
            0,
            consentLocalDataSource.getConsentUpdateTimestamp()
        )
        assertEquals(
            "isRemotelyUpdated is not false when there is no saved preferences.",
            false,
            consentLocalDataSource.isRemotelyUpdated()
        )
    }

    /**
     * Write data and test if the retrieved data are matching.
     */
    @Test
    fun testWriteAndReadData() {
        consentLocalDataSource.setConsentStatus(ConsentStatus.DENIED)
        consentLocalDataSource.setConsentUpdateTimestamp(1000)
        consentLocalDataSource.setIsRemotelyUpdated(true)
        assertEquals(
            "Consent status is not DENIED.",
            ConsentStatus.DENIED,
            consentLocalDataSource.getConsentStatus()
        )
        assertEquals(
            "Timestamp is not 1000.",
            1000,
            consentLocalDataSource.getConsentUpdateTimestamp()
        )
        assertEquals(
            "isRemotelyUpdated is not true.",
            true,
            consentLocalDataSource.isRemotelyUpdated()
        )

        consentLocalDataSource.setConsentStatus(ConsentStatus.ACCEPTED)
        consentLocalDataSource.setConsentUpdateTimestamp(54326171)
        consentLocalDataSource.setIsRemotelyUpdated(false)
        assertEquals(
            "Consent status is not ACCEPTED.",
            ConsentStatus.ACCEPTED,
            consentLocalDataSource.getConsentStatus()
        )
        assertEquals(
            "Timestamp is not 54326171.",
            54326171,
            consentLocalDataSource.getConsentUpdateTimestamp()
        )
        assertEquals(
            "isRemotelyUpdated is not false.",
            false,
            consentLocalDataSource.isRemotelyUpdated()
        )
    }

}