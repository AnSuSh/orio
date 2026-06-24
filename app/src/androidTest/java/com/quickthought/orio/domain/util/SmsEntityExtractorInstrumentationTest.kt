package com.quickthought.orio.domain.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.quickthought.orio.data.sms.SmsLogger
import com.quickthought.orio.domain.model.TransactionType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(com.quickthought.orio.core.di.SmsModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SmsEntityExtractorInstrumentationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var extractor: SmsEntityExtractor

    @Module
    @InstallIn(SingletonComponent::class)
    object TestModule {
        @Provides
        @Singleton
        fun provideSmsLogger(): SmsLogger = mockk(relaxed = true)
    }

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun whenReadExpenseMessage_shouldGetTransactionDomainWithExpenseAsType() = runBlocking {
        // This test requires internet for the first run to download the ML model
        // or it will use the pre-downloaded model if available.
//        val text = "Spent Rs 500.00 at Starbucks" // Failing
        val text = "Paid Rs 500 to Starbucks using UPI"
        val result = extractor.extract(text)

        assertNotNull("Extraction should not be null", result)
        assertEquals(500.0, result?.amount!!, 0.1)
        assertEquals("food", result.category)
        assertEquals("Starbucks", result.note)
        assertEquals(TransactionType.EXPENSE, result.type)
    }

    @Test
    fun whenReadIncomeMessage_shouldGetTransactionDomainWithIncomeAsType() = runBlocking {
        val text = "Your account has been credited with Rs 25,000.00 towards Salary"
        val result = extractor.extract(text)

        assertNotNull(result)
        assertEquals(25000.0, result?.amount!!, 0.1)
        assertEquals("salary", result.category)
        assertEquals(TransactionType.INCOME, result.type)
    }
}
