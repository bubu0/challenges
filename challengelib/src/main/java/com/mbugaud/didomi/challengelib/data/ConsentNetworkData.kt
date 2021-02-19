package com.mbugaud.didomi.challengelib.data

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Data class that represents the Consent for Remote Server communication with JSON attributes.
 */
internal  data class ConsentNetworkData (
    @SerializedName("status") val status: ConsentStatus,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("date") val date: Date
)