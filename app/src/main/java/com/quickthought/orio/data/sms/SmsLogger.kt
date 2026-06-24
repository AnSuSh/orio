package com.quickthought.orio.data.sms

import android.util.Log
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles remote logging of SMS extraction results to Supabase.
 * 
 * This is used to collect raw messages and their extraction results (anonymously)
 * to monitor ML model performance and improve categorization logic over time.
 */
class SmsLogger(
    private val postgrest: Postgrest
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Enriches the [log] with device metadata and inserts it into the 'sms_logs' table.
     * Execution happens on a background IO scope to avoid blocking the main thread 
     * or the SMS receiver.
     */
    fun log(log: SmsLog) {
        val enrichedLog = log.copy(
            deviceModel = android.os.Build.MODEL,
            androidVersion = android.os.Build.VERSION.RELEASE
        )
        scope.launch {
            try {
                Log.d("SmsLogger", "Logging SMS: $enrichedLog")
                postgrest["sms_logs"].insert(enrichedLog)
            } catch (e: Exception) {
                Log.e("SmsLogger", "Error logging SMS to Supabase", e)
            }
        }
    }
}
