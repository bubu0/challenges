/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbugaud.didomi.challengelib.api

import com.google.gson.GsonBuilder
import com.mbugaud.didomi.challengelib.data.ConsentNetworkData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Used to connect to Remote Server to send Consent relative data.
 * All dates are converted to ISO-8601 UTC String format.
 */
internal interface ConsentService {

    @POST("5e14e8122d00002b00167430") // There is no real endpoint here
    suspend fun sendConsent(@Body consentNetworkData: ConsentNetworkData): Response<String>

    companion object {
        private const val BASE_URL = "https://www.mocky.io/v2/"

        fun create(baseUrl: String = BASE_URL): ConsentService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") // Use ISO-8601 UTC date format.
                .create()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ConsentService::class.java)
        }
    }
}
