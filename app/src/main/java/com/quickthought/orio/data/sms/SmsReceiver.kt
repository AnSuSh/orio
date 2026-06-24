package com.quickthought.orio.data.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.quickthought.orio.domain.repository.TransactionsRepository
import com.quickthought.orio.domain.util.SmsEntityExtractor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: TransactionsRepository

    @Inject
    lateinit var smsEntityExtractor: SmsEntityExtractor

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val pendingResult = goAsync()

            scope.launch {
                try {
                    for (message in messages) {
                        val body = message.messageBody
                        Log.d("SmsReceiver", "Received SMS: $body")

                        val transaction = smsEntityExtractor.extract(body)
                        if (transaction != null) {
                            Log.d("SmsReceiver", "Parsed transaction: $transaction")
                            repository.insertTransaction(transaction)
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
