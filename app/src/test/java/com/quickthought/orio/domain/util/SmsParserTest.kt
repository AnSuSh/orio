package com.quickthought.orio.domain.util

import com.quickthought.orio.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SmsParserTest {

    @Test
    fun `parse expense - HDFC format`() {
        val sms = "Rs 500.00 debited from a/c **1234 on 01-01-24 to VPA merchant@upi"
        val transaction = SmsParser.parse(sms)
        assertNotNull(transaction)
        assertEquals(500.0, transaction?.amount)
        assertEquals(TransactionType.EXPENSE, transaction?.type)
    }

    @Test
    fun `parse expense - ICICI format`() {
        val sms = "Your A/c no. XXXX123 is debited for Rs 1,250.00 on 02-Jan-24. Info: UPI-MERCHANT-PURCHASE."
        val transaction = SmsParser.parse(sms)
        assertNotNull(transaction)
        assertEquals(1250.0, transaction?.amount)
        assertEquals(TransactionType.EXPENSE, transaction?.type)
    }

    @Test
    fun `parse income - SBI format`() {
        val sms = "Your A/c XXX1234 is credited with Rs 50,000.00 on 05/01/24 by NEFT"
        val transaction = SmsParser.parse(sms)
        assertNotNull(transaction)
        assertEquals(50000.0, transaction?.amount)
        assertEquals(TransactionType.INCOME, transaction?.type)
    }

    @Test
    fun `parse income - Generic format`() {
        val sms = "₹ 100 received in your account from Friend"
        val transaction = SmsParser.parse(sms)
        assertNotNull(transaction)
        assertEquals(100.0, transaction?.amount)
        assertEquals(TransactionType.INCOME, transaction?.type)
    }

    @Test
    fun `parse non-transactional SMS`() {
        val sms = "Your OTP for login is 123456. Do not share it with anyone."
        val transaction = SmsParser.parse(sms)
        assertNull(transaction)
    }
}
