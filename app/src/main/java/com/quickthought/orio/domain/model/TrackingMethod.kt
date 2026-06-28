package com.quickthought.orio.domain.model

/**
 * Indicates how the transaction was recorded in the system.
 */
enum class TrackingMethod {
    /** Manually entered by the user. */
    MANUAL,

    /** Automatically tracked from an incoming SMS message. */
    AUTO_SMS
}
