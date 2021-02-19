package com.mbugaud.didomi.challengelib.data

import com.google.gson.annotations.SerializedName

/**
 * Enum class to represent the status of the consent depending on the user's choice.
 */
enum class ConsentStatus {
    /**
     * User has not yet accepted or denied the consent.
     */
    UNDEFINED,

    /**
     * User has accepted the consent.
     */
    @SerializedName("accept")
    ACCEPTED,

    /**
     * User has denied the consent.
     */
    @SerializedName("deny")
    DENIED
}