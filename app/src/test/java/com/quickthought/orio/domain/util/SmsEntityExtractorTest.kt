package com.quickthought.orio.domain.util

import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.entityextraction.EntityAnnotation
import com.google.mlkit.nl.entityextraction.EntityExtractor
import com.google.mlkit.nl.entityextraction.MoneyEntity
import com.quickthought.orio.data.sms.SmsLogger
import com.quickthought.orio.domain.model.TransactionType
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SmsEntityExtractorTest {

    private lateinit var extractor: SmsEntityExtractor
    private val mockClient = mockk<EntityExtractor>()
    private val mockLogger = mockk<SmsLogger>(relaxed = true)

    @Before
    fun setup() {
        // Mock downloadModelIfNeeded
        val mockDownloadTask = Tasks.forResult<Void>(null)
        every { mockClient.downloadModelIfNeeded() } returns mockDownloadTask

        extractor = SmsEntityExtractor(mockLogger, mockClient)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test categorization - Zomato maps to Food`() = runBlocking {
        val text = "Paid Rs 450 to Zomato for food"

        // Mock ML Kit annotation
        val mockAnnotation = mockk<EntityAnnotation>()
        val mockMoneyEntity = mockk<MoneyEntity>()
        every { mockMoneyEntity.integerPart } returns 450
        every { mockMoneyEntity.fractionalPart } returns 0
        every { mockAnnotation.entities } returns listOf(mockMoneyEntity)

        val mockAnnotateTask = Tasks.forResult(listOf(mockAnnotation))
        every { mockClient.annotate(any<com.google.mlkit.nl.entityextraction.EntityExtractionParams>()) } returns mockAnnotateTask

        val result = extractor.extract(text)

        assertEquals(450.0, result?.amount)
        assertEquals("food", result?.category)
        assertEquals("Zomato", result?.note)
        assertEquals(TransactionType.EXPENSE, result?.type)
    }

    @Test
    fun `test categorization - Amazon maps to Shopping`() = runBlocking {
        val text = "Spent Rs 1200 on Amazon"

        // Mock ML Kit annotation
        val mockAnnotation = mockk<EntityAnnotation>()
        val mockMoneyEntity = mockk<MoneyEntity>()
        every { mockMoneyEntity.integerPart } returns 1200
        every { mockMoneyEntity.fractionalPart } returns 0
        every { mockAnnotation.entities } returns listOf(mockMoneyEntity)

        val mockAnnotateTask = Tasks.forResult(listOf(mockAnnotation))
        every { mockClient.annotate(any<com.google.mlkit.nl.entityextraction.EntityExtractionParams>()) } returns mockAnnotateTask

        val result = extractor.extract(text)

        assertEquals(1200.0, result?.amount ?: 0.0, 0.1)
        assertEquals("shopping", result?.category)
        assertEquals("Amazon", result?.note)
    }

    @Test
    fun `test categorization - Salary maps to Income`() = runBlocking {
        val text = "Your account has been credited with Rs 50000 towards Salary for Dec"

        // Mock ML Kit annotation
        val mockAnnotation = mockk<EntityAnnotation>()
        val mockMoneyEntity = mockk<MoneyEntity>()
        every { mockMoneyEntity.integerPart } returns 50000
        every { mockMoneyEntity.fractionalPart } returns 0
        every { mockAnnotation.entities } returns listOf(mockMoneyEntity)

        val mockAnnotateTask = Tasks.forResult(listOf(mockAnnotation))
        every { mockClient.annotate(any<com.google.mlkit.nl.entityextraction.EntityExtractionParams>()) } returns mockAnnotateTask

        val result = extractor.extract(text)

        assertEquals(50000.0, result?.amount)
        assertEquals("salary", result?.category)
        assertEquals(TransactionType.INCOME, result?.type)
    }

    @Test
    fun `test merchant extraction - Paid to Merchant Name`() = runBlocking {
        val text = "Paid Rs 100 to Starbucks using UPI"

        val mockAnnotation = mockk<EntityAnnotation>()
        val mockMoneyEntity = mockk<MoneyEntity>()
        every { mockMoneyEntity.integerPart } returns 100
        every { mockMoneyEntity.fractionalPart } returns 0
        every { mockAnnotation.entities } returns listOf(mockMoneyEntity)

        val mockAnnotateTask = Tasks.forResult(listOf(mockAnnotation))
        every { mockClient.annotate(any<com.google.mlkit.nl.entityextraction.EntityExtractionParams>()) } returns mockAnnotateTask

        val result = extractor.extract(text)

        assertEquals("Starbucks", result?.note)
        assertEquals("food", result?.category)
    }

    @Test
    fun `test fallback to SmsParser when ML Kit finds no entities`() = runBlocking {
        // SMS that ML Kit might miss but SmsParser handles
        val text = "Your A/c XXX1234 is debited for Rs 500.00 on 01-01-24. Info: UPI-PVR-CINEMAS"

        // ML Kit returns empty list
        val mockAnnotateTask = Tasks.forResult(listOf<EntityAnnotation>())
        every { mockClient.annotate(any<com.google.mlkit.nl.entityextraction.EntityExtractionParams>()) } returns mockAnnotateTask

        val result = extractor.extract(text)

        // Should still find amount 500.0 from SmsParser
        assertEquals(500.0, result?.amount ?: 0.0, 0.1)
        // And should still categorize it as entertainment because of PVR
        assertEquals("entertainment", result?.category)
        assertEquals("UPI-PVR-CINEMAS", result?.note?.uppercase())
    }

    @Test
    fun `test categorization - Transport keywords`() = runBlocking {
        val text = "Paid Rs 150 for Petrol at Shell"

        val mockAnnotation = mockk<EntityAnnotation>()
        val mockMoneyEntity = mockk<MoneyEntity>()
        every { mockMoneyEntity.integerPart } returns 150
        every { mockMoneyEntity.fractionalPart } returns 0
        every { mockAnnotation.entities } returns listOf(mockMoneyEntity)

        val mockAnnotateTask = Tasks.forResult(listOf(mockAnnotation))
        every { mockClient.annotate(any<com.google.mlkit.nl.entityextraction.EntityExtractionParams>()) } returns mockAnnotateTask

        val result = extractor.extract(text)

        assertEquals(150.0, result?.amount ?: 0.0, 0.1)
        assertEquals("transport", result?.category)
        assertEquals("Shell", result?.note)
    }

    @Test
    fun `test raw message storage - rawMessage should match input text`() = runBlocking {
        val text = "Paid Rs 150 for Petrol at Shell"

        val mockAnnotation = mockk<EntityAnnotation>()
        val mockMoneyEntity = mockk<MoneyEntity>()
        every { mockMoneyEntity.integerPart } returns 150
        every { mockMoneyEntity.fractionalPart } returns 0
        every { mockAnnotation.entities } returns listOf(mockMoneyEntity)

        val mockAnnotateTask = Tasks.forResult(listOf(mockAnnotation))
        every { mockClient.annotate(any<com.google.mlkit.nl.entityextraction.EntityExtractionParams>()) } returns mockAnnotateTask

        val result = extractor.extract(text)

        assertEquals(text, result?.rawMessage)
    }
}
