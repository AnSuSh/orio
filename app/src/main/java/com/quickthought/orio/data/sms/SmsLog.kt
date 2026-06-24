package com.quickthought.orio.data.sms

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmsLog(
    @SerialName("raw_message")
    val rawMessage: String,
    val amount: Double?,
    val type: String?,
    val category: String?,
    val note: String?,
    val method: String, // ML_KIT, FALLBACK, or REGEX
    val timestamp: Long = System.currentTimeMillis(),
    @SerialName("device_model")
    val deviceModel: String = "Unknown",
    @SerialName("android_version")
    val androidVersion: String = "Unknown"
)
