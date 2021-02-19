package com.mbugaud.didomi.challengelib

import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.mbugaud.didomi.challengelib.data.ConsentNetworkData
import com.mbugaud.didomi.challengelib.data.ConsentStatus
import com.mbugaud.didomi.challengelib.data.repository.*
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import java.net.UnknownHostException
import java.util.*

/**
 * Test the [ConsentRepository] with mocked DataSources.
 */
@RunWith(MockitoJUnitRunner::class)
class ConsentRepositoryTest {

    @Mock
    private lateinit var mockAdvertisingIdDataSource: AdvertisingIdDataSource

    @Mock
    private lateinit var mockConsentLocalDataSource: ConsentLocalDataSource

    @Mock
    private lateinit var mockConsentRemoteDataSource: ConsentRemoteDataSource

    /**
     * The Repository to Test
     */
    private lateinit var consentRepository: ConsentRepository

    /**
     * An dummy instance of [ConsentNetworkData] used for tests.
     */
    private val testConsentNetworkData = ConsentNetworkData(
        ConsentStatus.ACCEPTED, "test_id", Date(1000)
    )

    /**
     * Init the [consentRepository] and mock data sources with default behavior.
     */
    @Before
    fun setUp() = runBlocking {
        consentRepository = ConsentRepository(
            mockAdvertisingIdDataSource,
            mockConsentLocalDataSource,
            mockConsentRemoteDataSource
        )

        // Default Mock for mockConsentLocalDataSource
        `when`(mockConsentLocalDataSource.getConsentStatus()).thenReturn(ConsentStatus.ACCEPTED)
        `when`(mockConsentLocalDataSource.getConsentUpdateTimestamp()).thenReturn(1000)
        `when`(mockConsentLocalDataSource.isRemotelyUpdated()).thenReturn(true)

        // Default Mock for mockAdvertisingIdDataSource
        `when`(mockAdvertisingIdDataSource.getAdvertisingId()).thenReturn("test_id")

        // Default Mock for mockConsentRemoteDataSource
        `when`(mockConsentRemoteDataSource.sendConsent(testConsentNetworkData))
            .thenReturn(Response.success(204, ""))

        Unit
    }

    /**
     * Test if we retrieve the mocked data from local data source
     */
    @Test
    fun testReadDataFromLocalDataSource() {
        assertEquals(ConsentStatus.ACCEPTED, consentRepository.getConsentStatus())
        assertEquals(true, consentRepository.isRemotelyUpdated())
    }

    /**
     * Test if no exception is thrown when sending consent with default behavior
     */
    @Test(expected = Test.None::class)
    fun testSendConsentWithSuccess() = runBlocking {
        consentRepository.sendConsentToRemote(ConsentStatus.ACCEPTED)
    }

    /**
     * Test if exception is thrown when fail to retrieve Advertising ID
     */
    @Test(expected = GooglePlayServicesNotAvailableException::class)
    fun testSendConsentWhenAdvertisingIdNotAvailable() = runBlocking {
        `when`(mockAdvertisingIdDataSource.getAdvertisingId()).thenThrow(
            GooglePlayServicesNotAvailableException(0)
        )
        consentRepository.sendConsentToRemote(ConsentStatus.ACCEPTED)
    }

    /**
     * Test if exception is thrown when Remote Data Source return an error 400 response
     */
    @Test(expected = ConsentApiException::class)
    fun testSendConsentWhenRemoteReturnError400() = runBlocking {
        `when`(mockConsentRemoteDataSource.sendConsent(testConsentNetworkData))
            .thenReturn(Response.error(400, "".toResponseBody()))
        consentRepository.sendConsentToRemote(ConsentStatus.ACCEPTED)
    }

    /**
     * Test if exception is thrown when Remote Data Source can't access to Internet
     */
    @Test(expected = UnknownHostException::class)
    fun testSendConsentWhenInternetNotAvailable() = runBlocking {
        given(mockConsentRemoteDataSource.sendConsent(testConsentNetworkData)).willAnswer {
            throw UnknownHostException()
        }
        consentRepository.sendConsentToRemote(ConsentStatus.ACCEPTED)
    }
}