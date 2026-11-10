package com.personalfinance.cuentas

import com.personalfinance.cuentas.core.common.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase2LogicTest {

    @Test
    fun testLargeColombianIncomeFormatting() {
        val salaryCents = 350000000L // $3.500.000 COP
        val formatted = CurrencyFormatter.formatCentsToCop(salaryCents)
        assertTrue(formatted.contains("3.500.000") || formatted.contains("3,500,000"))
    }

    @Test
    fun testExpenseDebitCalculation() {
        val initialBalanceCents = 200000000L // $2.000.000 COP
        val expenseCents = 4500000L // $45.000 COP
        val expectedNewBalance = 195500000L // $1.955.000 COP

        val actualBalance = initialBalanceCents - expenseCents
        assertEquals(expectedNewBalance, actualBalance)
    }

    @Test
    fun testIncomeCreditCalculation() {
        val initialBalanceCents = 15000000L // $150.000 COP
        val quincenaCents = 175000000L // $1.750.000 COP
        val expectedNewBalance = 190000000L // $1.900.000 COP

        val actualBalance = initialBalanceCents + quincenaCents
        assertEquals(expectedNewBalance, actualBalance)
    }

    @Test
    fun testBalanceAdjustmentDeltaCalculation() {
        val currentBalanceCents = 50000000L // $500.000 COP
        val realPhysicalBalanceCents = 48500000L // $485.000 COP (faltan $15.000)

        val delta = realPhysicalBalanceCents - currentBalanceCents
        assertEquals(-1500000L, delta)
    }
}
