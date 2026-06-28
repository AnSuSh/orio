package com.quickthought.orio.domain.util

import com.google.mlkit.nl.entityextraction.DateTimeEntity
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractor
import com.google.mlkit.nl.entityextraction.MoneyEntity
import com.quickthought.orio.data.sms.SmsLog
import com.quickthought.orio.data.sms.SmsLogger
import com.quickthought.orio.domain.model.TrackingMethod
import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionType
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core utility for extracting financial transaction data from SMS messages.
 * 
 * This class uses a multi-layered approach:
 * 1. **ML Kit Entity Extraction**: Primary method to identify amounts and dates.
 * 2. **Keyword Mapping**: Custom logic to categorize transactions (e.g., Food, Transport) based on merchant names.
 * 3. **Regex Fallback**: Uses [SmsParser] if ML Kit fails to identify essential fields.
 * 
 * Extracted logs are automatically sent to Supabase via [SmsLogger] for analytical improvements.
 */
@Singleton
class SmsEntityExtractor @Inject constructor(
    private val smsLogger: SmsLogger,
    private val entityExtractor: EntityExtractor
) {

    /**
     * Extracts a [TransactionDomain] from the given SMS [text].
     * 
     * @param text The raw SMS message body.
     * @return A valid transaction object if extraction succeeds, or null if the message 
     *         is not recognized as a financial transaction.
     */
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
                            val currentAmount = entity.integerPart.toDouble() + (entity.fractionalPart.toDouble() / 100.0)
                            // Heuristic: If we find multiple amounts, and one looks like a balance or limit,
                            // we try to pick the transaction one.
                            val balanceKeywords = listOf("Avl Bal", "Available Balance", "Avl Limit", "Available Limit")
                            val isBalanceContext = balanceKeywords.any { text.contains(it, ignoreCase = true) }
                            
                            if (amount == null || !isBalanceContext) {
                                amount = currentAmount
                            }
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
                    if (isLikelyMarketingOffer(text)) return null
                    val (category, note) = getCategoryAndNote(text, it.type)
                    val result = it.copy(
                        category = category, 
                        note = note,
                        trackingMethod = TrackingMethod.AUTO_SMS,
                        rawMessage = text
                    )
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
                note = note,
                trackingMethod = TrackingMethod.AUTO_SMS,
                rawMessage = text
            )
            
            // Check for marketing offers/ads
            if (isLikelyMarketingOffer(text)) {
                return null
            }

            logExtraction(text, result, method)
            result
        } catch (e: Exception) {
            method = "ERROR_FALLBACK"
            val fallback = SmsParser.parse(text)
            fallback?.let {
                if (isLikelyMarketingOffer(text)) return null
                val (category, note) = getCategoryAndNote(text, it.type)
                val result = it.copy(
                    category = category, 
                    note = note,
                    trackingMethod = TrackingMethod.AUTO_SMS,
                    rawMessage = text
                )
                logExtraction(text, result, method)
                result
            }
        }
    }

    private fun isLikelyMarketingOffer(text: String): Boolean {
        val lowerText = text.lowercase()
        
        // Specific patterns from the failed cases
        val isAirtelRecharge = lowerText.contains("recharge now with rs") && 
                              (lowerText.contains("airtel") || lowerText.contains("unlimited calls"))
        
        val isSbiCardDiscount = lowerText.contains("instant discount") && 
                                lowerText.contains("sbi credit card") && 
                                lowerText.contains("valid till")
        
        val isPersonalOffer = lowerText.contains("offer for") && lowerText.contains("!")

        return isAirtelRecharge || isSbiCardDiscount || isPersonalOffer
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
        val lowerText = text.lowercase()
        val incomeKeywords = listOf("credited", "received", "deposited", "added", "credit by")
        
        if (incomeKeywords.any { lowerText.contains(it) }) {
            return TransactionType.INCOME
        }
        return TransactionType.EXPENSE
    }

    private fun getCategoryAndNote(text: String, type: TransactionType): Pair<String, String> {
        val merchant = findMerchant(text)
        val lowerText = text.lowercase()
        val categoryContext = (merchant ?: "") + " " + lowerText

        // Categorization logic
        val category = when {
            type == TransactionType.INCOME && (lowerText.contains("salary") || lowerText.contains("payroll") || lowerText.contains(
                "hrl"
            )) -> "salary"

            categoryContext.contains("zomato") || categoryContext.contains("swiggy") || categoryContext.contains("restaurant") ||
                    categoryContext.contains("starbucks") || categoryContext.contains("domino") || categoryContext.contains(
                "cafe"
            ) ||
                    categoryContext.contains("eat") || categoryContext.contains("food") -> "food"

            categoryContext.contains("uber") || categoryContext.contains("ola") || categoryContext.contains("rapido") ||
                    categoryContext.contains("petrol") || categoryContext.contains("shell") || categoryContext.contains(
                "fuel"
            ) ||
                    categoryContext.contains("metro") || categoryContext.contains("irctc") || categoryContext.contains(
                "train"
            ) || categoryContext.contains("transport") -> "transport"

            categoryContext.contains("amazon") || categoryContext.contains("flipkart") || categoryContext.contains("myntra") ||
                    categoryContext.contains("dmart") || categoryContext.contains("bigbasket") || categoryContext.contains(
                "grocery"
            ) ||
                    categoryContext.contains("mall") || categoryContext.contains("reliance") || categoryContext.contains("shopping") -> "shopping"

            categoryContext.contains("netflix") || categoryContext.contains("hotstar") || categoryContext.contains("spotify") ||
                    categoryContext.contains("pvr") || categoryContext.contains("cinema") || categoryContext.contains(
                "bookmyshow"
            ) -> "entertainment"

            categoryContext.contains("pharmacy") || categoryContext.contains("apollo") || categoryContext.contains("hospital") ||
                    categoryContext.contains("medplus") || categoryContext.contains("doctor") || categoryContext.contains(
                "clinic"
            ) -> "health"

            categoryContext.contains("school") || categoryContext.contains("college") || categoryContext.contains("university") ||
                    categoryContext.contains("udemy") || categoryContext.contains("coursera") -> "education"

            else -> "other"
        }

        val note = merchant ?: "Auto-tracked SMS"
        return Pair(category, note)
    }

    private fun findMerchant(text: String): String? {
        // Common patterns for merchant names in Indian bank SMS
        val patterns = listOf(
            "(?:at|to|towards|on|trf to|spent on|paid to)\\s+([A-Za-z0-9* ]+?)(?:\\s+using|\\s+for|\\s+bal|\\s+ref|\\.|$)".toRegex(
                RegexOption.IGNORE_CASE
            ),
            "Info:\\s*([A-Za-z0-9* -]+)".toRegex(RegexOption.IGNORE_CASE)
        )

        for (regex in patterns) {
            val match = regex.find(text)
            if (match != null) {
                var name = match.groupValues[1].trim()
                // Remove trailing dashes or dots
                name = name.removeSuffix("-").removeSuffix(".").trim()
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
