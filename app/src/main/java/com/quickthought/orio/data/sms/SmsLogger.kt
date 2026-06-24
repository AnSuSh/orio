package com.quickthought.orio.data.sms

import android.util.Log
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsLogger(
    private val postgrest: Postgrest
) {
    private val scope = CoroutineScope(Dispatchers.IO)

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
