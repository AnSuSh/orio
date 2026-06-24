package com.quickthought.orio.domain.util

import com.quickthought.orio.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SmsParserTest {

    @Test
    fun `parse expense message with keyword first`() {
        val message = "Debited Rs. 500.00 from your account"
        val result = SmsParser.parse(message)
        assertNotNull(result)
        assertEquals(500.0, result?.amount!!, 0.01)
        assertEquals(TransactionType.EXPENSE, result.type)
    }

    @Test
    fun `parse expense message with amount first`() {
        val message = "Rs. 250 spent on Starbucks"
        val result = SmsParser.parse(message)
        assertNotNull(result)
        assertEquals(250.0, result?.amount!!, 0.01)
        assertEquals(TransactionType.EXPENSE, result.type)
    }

    @Test
    fun `parse income message`() {
        val message = "Your account has been credited with INR 25,000"
        val result = SmsParser.parse(message)
        assertNotNull(result)
        assertEquals(25000.0, result?.amount!!, 0.01)
        assertEquals(TransactionType.INCOME, result.type)
    }

    @Test
    fun `return null for non-financial message`() {
        val message = "Hi, how are you?"
        val result = SmsParser.parse(message)
        assertNull(result)
    }
}
