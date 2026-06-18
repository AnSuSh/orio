package com.quickthought.orio.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeOverviewTest {

    @Test
    fun `calculate totalBalance correctly`() {
        val overview = HomeOverview(
            transactions = emptyList(),
            totalIncome = 1000.0,
            totalExpense = 400.0,
            monthlyBudget = 1000.0,
            daysLeftInMonth = 10
        )
        assertEquals(600.0, overview.totalBalance, 0.0)
    }

    @Test
    fun `calculate remainingBudget correctly`() {
        val overview = HomeOverview(
            transactions = emptyList(),
            totalIncome = 1000.0,
            totalExpense = 400.0,
            monthlyBudget = 1000.0,
            daysLeftInMonth = 10
        )
        assertEquals(600.0, overview.remainingBudget, 0.0)
    }

    @Test
    fun `calculate dailyRemaining correctly`() {
        val overview = HomeOverview(
            transactions = emptyList(),
            totalIncome = 1000.0,
            totalExpense = 400.0,
            monthlyBudget = 1000.0,
            daysLeftInMonth = 10
        )
        assertEquals(60.0, overview.dailyRemaining, 0.0)
    }

    @Test
    fun `calculate dailyRemaining when budget exceeded`() {
        val overview = HomeOverview(
            transactions = emptyList(),
            totalIncome = 1000.0,
            totalExpense = 1200.0,
            monthlyBudget = 1000.0,
            daysLeftInMonth = 10
        )
        assertEquals(0.0, overview.remainingBudget, 0.0)
        assertEquals(0.0, overview.dailyRemaining, 0.0)
    }
}
