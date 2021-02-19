package com.mbugaud.didomi.challengelib

import com.mbugaud.didomi.challengelib.api.ConsentService
import com.mbugaud.didomi.challengelib.data.ConsentNetworkData
import com.mbugaud.didomi.challengelib.data.ConsentStatus
import com.mbugaud.didomi.challengelib.data.repository.ConsentRemoteDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.util.*

/**
 * Test the [ConsentRemoteDataSource].
 */
class ConsentRemoteDataSourceTest {

    /**
     * The Data Source to Test
     */
    private lateinit var consentRemoteDataSource: ConsentRemoteDataSource

    /**
     * Used to mock HTTP requests
     */
    private val mockServer = MockWebServer()

    /**
     * An dummy instance of [ConsentNetworkData] used for tests.
     */
    private val testConsentNetworkData = ConsentNetworkData(
        ConsentStatus.ACCEPTED, "test", Date(1000)
    )

    /**
     * The expected JSON body matching with the [testConsentNetworkData] object.
     * You must update this String when you modify the [testConsentNetworkData] variables.
     */
    private val testRequestBody = "{\"status\":\"accept\",\"device_id\":\"test\",\"date\":\"1970-01-01T01:00:01+0100\"}"

    @Before
    fun setup() {
        mockServer.start()

        val baseUrl = mockServer.url("/")
        consentRemoteDataSource = ConsentRemoteDataSource(ConsentService.create(baseUrl.toString()))
    }

    @After
    fun shutDown() {
        mockServer.shutdown()
    }

    /**
     * Check the HTTP Method and the JSON Body sent by the DataSource.
     */
    @Test
    fun testRequestMethodAndBody() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(204))
        consentRemoteDataSource.sendConsent(testConsentNetworkData)
        val request: RecordedRequest = mockServer.takeRequest()
        assertEquals("The request method is not POST", "POST", request.method)
        assertEquals("The request body doesn't match with expected body", testRequestBody, request.body.readUtf8())
    }

    /**
     * Check the response Code when server return 204 (success).
     */
    @Test
    fun testResponseWithResponseCode204() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(204))
        val response = consentRemoteDataSource.sendConsent(testConsentNetworkData)
        assertEquals("The response code is not 204", 204, response.code())
    }

    /**
     * Check the response Code when server return 400 (error).
     */
    @Test
    fun testResponseWithResponseCode400() = runBlocking {
        mockServer.enqueue(MockResponse().setResponseCode(400))
        val response = consentRemoteDataSource.sendConsent(testConsentNetworkData)
        assertEquals("The response code is not 400", 400, response.code())
    }
}