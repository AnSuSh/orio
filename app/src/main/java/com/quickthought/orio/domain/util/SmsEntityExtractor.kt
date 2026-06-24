package com.quickthought.orio.domain.util

import com.google.mlkit.nl.entityextraction.DateTimeEntity
import com.google.mlkit.nl.entityextraction.EntityExtractor
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.MoneyEntity
import com.quickthought.orio.data.sms.SmsLog
import com.quickthought.orio.data.sms.SmsLogger
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionType
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsEntityExtractor @Inject constructor(
    private val smsLogger: SmsLogger,
    private val entityExtractor: EntityExtractor
) {

    suspend fun extract(text: String): TransactionDomain? {
        var method = "ML_KIT"
        return try {
            entityExtractor.downloadModelIfNeeded().await()
            
            val params = EntityExtractionParams.Builder(text).build()
            val annotations = entityExtractor.annotate(params).await()
            
            var amount: Double? = null
            var date: Long = System.currentTimeMillis()
            
            for (annotation in annotations) {
                for (entity in annotation.entities) {
                    when (entity) {
                        is MoneyEntity -> {
                            val fraction = entity.fractionalPart.toDouble() / 100.0
                            amount = entity.integerPart.toDouble() + fraction
                        }
                        is DateTimeEntity -> {
                            date = entity.timestampMillis
                        }
                    }
                }
            }
            
            if (amount == null) {
                method = "FALLBACK"
                val fallback = SmsParser.parse(text)
                return fallback?.let {
                    val (category, note) = getCategoryAndNote(text, it.type)
                    val result = it.copy(category = category, note = note)
                    logExtraction(text, result, method)
                    result
                }
            }

            val type = getTransactionType(text)
            val (category, note) = getCategoryAndNote(text, type)

            val result = TransactionDomain(
                amount = amount,
                type = type,
                category = category,
                date = date,
                note = note
            )
            logExtraction(text, result, method)
            result
        } catch (e: Exception) {
            method = "ERROR_FALLBACK"
            val fallback = SmsParser.parse(text)
            fallback?.let {
                val (category, note) = getCategoryAndNote(text, it.type)
                val result = it.copy(category = category, note = note)
                logExtraction(text, result, method)
                result
            }
        }
    }

    private fun logExtraction(text: String, result: TransactionDomain, method: String) {
        smsLogger.log(
            SmsLog(
                rawMessage = text,
                amount = result.amount,
                type = result.type.name,
                category = result.category,
                note = result.note,
                method = method
            )
        )
    }

    private fun getTransactionType(text: String): TransactionType {
        val incomeKeywords = listOf("credited", "received", "deposited", "added")
        return if (incomeKeywords.any { text.contains(it, ignoreCase = true) }) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
        }
    }

    private fun getCategoryAndNote(text: String, type: TransactionType): Pair<String, String> {
        val lowerText = text.lowercase()
        
        // Categorization logic
        val category = when {
            type == TransactionType.INCOME && (lowerText.contains("salary") || lowerText.contains("payroll") || lowerText.contains("hrl")) -> "salary"
            lowerText.contains("zomato") || lowerText.contains("swiggy") || lowerText.contains("restaurant") || 
                lowerText.contains("starbucks") || lowerText.contains("domino") || lowerText.contains("cafe") || 
                lowerText.contains("eat") || lowerText.contains("food") -> "food"
            lowerText.contains("uber") || lowerText.contains("ola") || lowerText.contains("rapido") || 
                lowerText.contains("petrol") || lowerText.contains("shell") || lowerText.contains("fuel") || 
                lowerText.contains("metro") || lowerText.contains("irctc") || lowerText.contains("train") -> "transport"
            lowerText.contains("amazon") || lowerText.contains("flipkart") || lowerText.contains("myntra") || 
                lowerText.contains("dmart") || lowerText.contains("bigbasket") || lowerText.contains("grocery") || 
                lowerText.contains("mall") || lowerText.contains("reliance") -> "shopping"
            lowerText.contains("netflix") || lowerText.contains("hotstar") || lowerText.contains("spotify") || 
                lowerText.contains("pvr") || lowerText.contains("cinema") || lowerText.contains("bookmyshow") -> "entertainment"
            lowerText.contains("pharmacy") || lowerText.contains("apollo") || lowerText.contains("hospital") || 
                lowerText.contains("medplus") || lowerText.contains("doctor") || lowerText.contains("clinic") -> "health"
            lowerText.contains("school") || lowerText.contains("college") || lowerText.contains("university") || 
                lowerText.contains("udemy") || lowerText.contains("coursera") -> "education"
            else -> "other"
        }

        // Note extraction: try to find the merchant name
        val merchant = findMerchant(text)
        val note = merchant ?: "Auto-tracked SMS"

        return Pair(category, note)
    }

    private fun findMerchant(text: String): String? {
        // Common patterns for merchant names in Indian bank SMS
        val patterns = listOf(
            "(?:at|to|towards|on)\\s+([A-Za-z0-9* ]+?)(?:\\s+using|\\s+for|\\s+bal|\\s+ref|\\.)".toRegex(RegexOption.IGNORE_CASE),
            "spent\\s+on\\s+([A-Za-z0-9* ]+?)(?:\\s+using|\\.)".toRegex(RegexOption.IGNORE_CASE),
            "paid\\s+to\\s+([A-Za-z0-9* ]+?)(?:\\s+using|\\.)".toRegex(RegexOption.IGNORE_CASE)
        )

        for (regex in patterns) {
            val match = regex.find(text)
            if (match != null) {
                val name = match.groupValues[1].trim()
                if (name.isNotEmpty() && name.length > 2) {
                    return name
                }
            }
        }
        return null
    }
    
    suspend fun downloadModel() {
        entityExtractor.downloadModelIfNeeded().await()
    }
}
