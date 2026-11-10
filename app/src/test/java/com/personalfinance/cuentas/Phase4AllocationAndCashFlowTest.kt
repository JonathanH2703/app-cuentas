package com.personalfinance.cuentas

import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.BlockType
import com.personalfinance.cuentas.domain.math.AllocationCalculator
import com.personalfinance.cuentas.domain.math.SafeToSpendStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class Phase4AllocationAndCashFlowTest {

    @Test
    fun test503020SplitExactSumNoCentDrift() {
        val totalIncomeCents = 250000000L // $2.500.000 COP

        val blocks = listOf(
            AllocationBlockEntity(modelId = 1, name = "Necesidades", percentage = 50, type = BlockType.NEEDS, absorbRemainder = true),
            AllocationBlockEntity(modelId = 1, name = "Deseos", percentage = 30, type = BlockType.WANTS, absorbRemainder = false),
            AllocationBlockEntity(modelId = 1, name = "Ahorro", percentage = 20, type = BlockType.SAVINGS, absorbRemainder = false)
        )

        val split = AllocationCalculator.splitIncomeAcrossBlocks(totalIncomeCents, blocks)

        assertEquals(3, split.size)
        assertEquals(125000000L, split[0].assignedCents) // 50% = $1.250.000
        assertEquals(75000000L, split[1].assignedCents)  // 30% = $750.000
        assertEquals(50000000L, split[2].assignedCents)  // 20% = $500.000

        // La suma de centavos debe ser idéntica al total
        assertEquals(totalIncomeCents, split.sumOf { it.assignedCents })
    }

    @Test
    fun testOddCentRemainderAbsorption() {
        // Ingreso con centavos impares no divisibles exactamente: $1.000.001 COP y 3 centavos (100.000.103 centavos)
        // 33% / 33% / 34%
        val totalIncomeCents = 100000103L

        val blocks = listOf(
            AllocationBlockEntity(modelId = 1, name = "Bloque A", percentage = 33, type = BlockType.NEEDS, absorbRemainder = true),
            AllocationBlockEntity(modelId = 1, name = "Bloque B", percentage = 33, type = BlockType.WANTS, absorbRemainder = false),
            AllocationBlockEntity(modelId = 1, name = "Bloque C", percentage = 34, type = BlockType.SAVINGS, absorbRemainder = false)
        )

        val split = AllocationCalculator.splitIncomeAcrossBlocks(totalIncomeCents, blocks)

        // El algoritmo de absorción de residuo debe asegurar cero pérdida de centavos
        assertEquals(totalIncomeCents, split.sumOf { it.assignedCents })
    }

    @Test
    fun testSafeToSpendGreenStatus() {
        val consolidatedCash = 500000000L // $5.000.000 COP líquidos
        val committedExpenses = 150000000L // $1.500.000 COP cuotas y arriendo
        val today = LocalDate.of(2026, 10, 15) // día 15 de un mes de 31 días (quedan 17 días)

        val safeToSpend = AllocationCalculator.calculateSafeToSpend(
            consolidatedCashCents = consolidatedCash,
            committedExpensesCents = committedExpenses,
            today = today
        )

        assertEquals(SafeToSpendStatus.GREEN, safeToSpend.status)
        assertEquals(350000000L, safeToSpend.safeToSpendTotalCents) // $3.500.000 COP libres
        assertTrue("El saldo diario debe ser mayor a cero", safeToSpend.safeToSpendDailyCents > 0L)
        assertEquals(17, safeToSpend.daysRemainingInMonth)
    }

    @Test
    fun testSafeToSpendRedStatusOnDeficit() {
        val consolidatedCash = 100000000L // $1.000.000 COP líquidos
        val committedExpenses = 180000000L // $1.800.000 COP obligaciones fijas pendientes

        val safeToSpend = AllocationCalculator.calculateSafeToSpend(
            consolidatedCashCents = consolidatedCash,
            committedExpensesCents = committedExpenses,
            today = LocalDate.of(2026, 10, 20)
        )

        assertEquals(SafeToSpendStatus.RED, safeToSpend.status)
        assertTrue("El saldo debe ser negativo", safeToSpend.safeToSpendTotalCents < 0L)
        assertEquals(0L, safeToSpend.safeToSpendDailyCents)
    }

    @Test
    fun testCashFlowProjection306090Days() {
        val initialCash = 200000000L // $2.000.000 COP
        val today = LocalDate.of(2026, 10, 1)

        val incomes = mapOf(
            today.plusDays(14) to 300000000L, // Quincena 1 (+3M)
            today.plusDays(29) to 300000000L  // Quincena 2 (+3M)
        )

        val expenses = mapOf(
            today.plusDays(5) to 150000000L // Pago arriendo (-1.5M)
        )

        val projection = AllocationCalculator.projectCashFlow(
            initialBalanceCents = initialCash,
            dailyIncomes = incomes,
            dailyExpenses = expenses,
            startDate = today,
            horizonDays = 90
        )

        assertEquals(90, projection.dailyProjections.size)

        // Al día 30: 2M - 1.5M + 3M + 3M = 6.5M COP (650000000L)
        assertEquals(650000000L, projection.days30NetCents)
        assertEquals(650000000L, projection.days60NetCents)
        assertEquals(650000000L, projection.days90NetCents)
    }
}
