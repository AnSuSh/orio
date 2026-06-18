package com.quickthought.orio.domain.util

import com.quickthought.orio.domain.model.TransactionDomain
import com.quickthought.orio.domain.model.TransactionType
import java.util.regex.Pattern

object SmsParser {
    private val expenseKeywords = "(?:debited|spent|withdrawn|paid|payment|transfer|sent)"
    private val incomeKeywords = "(?:credited|received|deposited|added)"
    private val currencySymbols = "(?:rs\\.?|inr|₹)"
    private val amountPattern = "([\\d,]+\\.?\\d*)"

    private val expenseRegex1 = Pattern.compile("$expenseKeywords.*$currencySymbols\\s*$amountPattern", Pattern.CASE_INSENSITIVE)
    private val expenseRegex2 = Pattern.compile("$currencySymbols\\s*$amountPattern.*$expenseKeywords", Pattern.CASE_INSENSITIVE)
    
    private val incomeRegex1 = Pattern.compile("$incomeKeywords.*$currencySymbols\\s*$amountPattern", Pattern.CASE_INSENSITIVE)
    private val incomeRegex2 = Pattern.compile("$currencySymbols\\s*$amountPattern.*$incomeKeywords", Pattern.CASE_INSENSITIVE)

    fun parse(message: String): TransactionDomain? {
        val e1 = expenseRegex1.matcher(message)
        val e2 = expenseRegex2.matcher(message)
        
        if (e1.find()) {
            return createTransaction(e1.group(1), TransactionType.EXPENSE)
        } else if (e2.find()) {
            return createTransaction(e2.group(1), TransactionType.EXPENSE)
        }

        val i1 = incomeRegex1.matcher(message)
        val i2 = incomeRegex2.matcher(message)
        
        if (i1.find()) {
            return createTransaction(i1.group(1), TransactionType.INCOME)
        } else if (i2.find()) {
            return createTransaction(i2.group(1), TransactionType.INCOME)
        }

        return null
    }

    private fun createTransaction(amountStr: String?, type: TransactionType): TransactionDomain? {
        val amount = amountStr?.replace(",", "")?.toDoubleOrNull() ?: return null
        return TransactionDomain(
            amount = amount,
            type = type,
            category = "Uncategorized",
            date = System.currentTimeMillis(),
            note = "Auto-tracked from SMS"
        )
    }
}
