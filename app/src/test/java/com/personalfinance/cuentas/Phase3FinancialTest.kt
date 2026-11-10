package com.personalfinance.cuentas

import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.AccountType
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount
import com.personalfinance.cuentas.domain.math.FinancialFormulas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class Phase3FinancialTest {

    @Test
    fun testRateConversionEaAndMonthly() {
        // Tasa común en Colombia para tarjetas: 28.5% EA
        val ea = 28.5
        val monthlyRate = FinancialFormulas.eaToMonthlyRate(ea)
        // (1 + 0.285)^(1/12) - 1 ≈ 0.02115 (2.115% mes vencido)
        assertEquals(0.02115, monthlyRate, 0.001)

        // Convertir de regreso a EA
        val recoveredEa = FinancialFormulas.monthlyRateToEa(monthlyRate * 100.0)
        assertEquals(28.5, recoveredEa, 0.01)

        // Caso borde 0%
        assertEquals(0.0, FinancialFormulas.eaToMonthlyRate(0.0), 0.00001)
        assertEquals(0.0, FinancialFormulas.monthlyRateToEa(0.0), 0.00001)
    }

    @Test
    fun testInstallmentFrenchSystemCalculation() {
        val principalCents = 100000000L // $1.000.000 COP
        val monthlyRate = 0.02 // 2.0% mensual
        val installments = 12

        val installmentCents = FinancialFormulas.calculateInstallmentAmountCents(
            principalCents = principalCents,
            monthlyRateDecimal = monthlyRate,
            installments = installments
        )

        // Para P = 1.000.000, i = 0.02, n = 12:
        // Cuota teórica = 1.000.000 * (0.02 * (1.02)^12) / ((1.02)^12 - 1) ≈ $94.559,60 COP (9455960 centavos)
        assertTrue("La cuota debe estar alrededor de $94.560 COP", installmentCents in 9450000L..9460000L)

        // 1 sola cuota no debe generar intereses
        val singleInstallment = FinancialFormulas.calculateInstallmentAmountCents(
            principalCents = principalCents,
            monthlyRateDecimal = monthlyRate,
            installments = 1
        )
        assertEquals(principalCents, singleInstallment)
    }

    @Test
    fun testAmortizationScheduleCompleteness() {
        val principalCents = 200000000L // $2.000.000 COP
        val monthlyRate = 0.018 // 1.8% mensual
        val termMonths = 6

        val schedule = FinancialFormulas.generateAmortizationSchedule(
            principalCents = principalCents,
            monthlyRateDecimal = monthlyRate,
            termMonths = termMonths
        )

        assertEquals(termMonths, schedule.size)

        // El saldo final en la última fila debe ser 0
        assertEquals(0L, schedule.last().remainingBalanceCents)

        // La suma de todos los abonos a capital debe ser exactamente igual al principal original
        val totalPrincipalPaid = schedule.sumOf { it.principalPaidCents }
        assertEquals(principalCents, totalPrincipalPaid)

        // En cada fila, cuota = capital + interés
        for (row in schedule) {
            assertEquals(
                "La cuota debe ser igual a principal + interés en el mes ${row.month}",
                row.installmentCents,
                row.principalPaidCents + row.interestCents
            )
        }
    }

    @Test
    fun testExtraPaymentSimulationSavings() {
        val currentBalanceCents = 1000000000L // $10.000.000 COP
        val monthlyInstallmentCents = 40000000L // $400.000 COP
        val monthlyRate = 0.019 // 1.9% mensual
        val extraPaymentCents = 300000000L // Abono extra de $3.000.000 COP

        val simulation = FinancialFormulas.simulateExtraPayment(
            currentBalanceCents = currentBalanceCents,
            monthlyInstallmentCents = monthlyInstallmentCents,
            monthlyRateDecimal = monthlyRate,
            extraPaymentCents = extraPaymentCents
        )

        // Debe haber ahorro de meses y de intereses
        assertTrue("Debe ahorrar meses al reducir plazo", simulation.reduceTermMonthsSaved > 0)
        assertTrue("Debe ahorrar intereses al reducir plazo", simulation.reduceTermInterestSavedCents > 0L)
        assertTrue("La nueva cuota mensual reducida debe ser menor a la original", simulation.reduceInstallmentNewMonthlyCents < monthlyInstallmentCents)
    }

    @Test
    fun testSnowballVsAvalancheComparison() {
        val debtSmallBalance = DebtEntity(
            id = 1,
            name = "Préstamo Pequeño",
            principalAmountCents = 100000000L, // $1.000.000 COP
            currentBalanceCents = 100000000L,
            interestRateEa = 15.0, // Tasa baja
            dueDayOfMonth = 10,
            monthlyInstallmentCents = 10000000L
        )

        val debtHighRate = DebtEntity(
            id = 2,
            name = "Tarjeta Crédito Cara",
            principalAmountCents = 300000000L, // $3.000.000 COP
            currentBalanceCents = 300000000L,
            interestRateEa = 32.0, // Tasa alta
            dueDayOfMonth = 15,
            monthlyInstallmentCents = 20000000L
        )

        val extraBudgetCents = 15000000L // $150.000 COP extra por mes

        val comparison = FinancialFormulas.compareSnowballVsAvalanche(
            debts = listOf(debtSmallBalance, debtHighRate),
            extraMonthlyBudgetCents = extraBudgetCents
        )

        // Bola de Nieve debe priorizar la deuda con menor saldo
        assertEquals("Préstamo Pequeño", comparison.snowball.payoffOrder.firstOrNull())

        // Avalancha debe priorizar la deuda con mayor tasa
        assertEquals("Tarjeta Crédito Cara", comparison.avalanche.payoffOrder.firstOrNull())

        // Avalancha matemáticamente minimiza el costo financiero (paga igual o menos interés)
        assertTrue(
            "Avalancha debe pagar igual o menos intereses totales que Bola de Nieve",
            comparison.avalanche.totalInterestPaidCents <= comparison.snowball.totalInterestPaidCents
        )
    }

    @Test
    fun testCreditCardBillingSummary() {
        val card = CreditCardEntity(
            id = 10,
            accountId = 2,
            creditLimitCents = 500000000L, // Cupo $5.000.000 COP
            cutOffDay = 15,
            paymentDueDay = 30,
            interestRateEa = 26.0,
            handlingFeeCents = 2500000L // Cuota manejo $25.000 COP
        )

        val purchases = listOf(
            CardPurchaseEntity(
                id = 1,
                cardId = 10,
                merchant = "Supermercado Éxito",
                purchaseDateMillis = System.currentTimeMillis(),
                totalAmountCents = 30000000L, // $300.000 COP a 1 cuota
                installmentsTotal = 1
            ),
            CardPurchaseEntity(
                id = 2,
                cardId = 10,
                merchant = "Laptop",
                purchaseDateMillis = System.currentTimeMillis(),
                totalAmountCents = 120000000L, // $1.200.000 COP a 12 cuotas
                installmentsTotal = 12
            )
        )

        val summary = FinancialFormulas.calculateCardBilling(
            card = card,
            purchases = purchases,
            today = LocalDate.of(2026, 10, 20)
        )

        val totalUsedExpected = 30000000L + 120000000L
        assertEquals(totalUsedExpected, summary.totalUsedCents)
        assertEquals(500000000L - totalUsedExpected, summary.availableLimitCents)

        // El pago total incluye las compras más la cuota de manejo
        assertEquals(totalUsedExpected + 2500000L, summary.totalPaymentCents)
    }

    @Test
    fun testRecommendBestCardForToday() {
        val account1 = AccountEntity(id = 1, name = "Bancolombia Oro", type = AccountType.CHECKING, currentBalanceCents = 0L)
        val card1 = CreditCardEntity(
            id = 1,
            accountId = 1,
            creditLimitCents = 300000000L,
            cutOffDay = 5, // Corte el 5 de cada mes
            paymentDueDay = 20,
            interestRateEa = 25.0
        )

        val account2 = AccountEntity(id = 2, name = "Nu Bank", type = AccountType.CHECKING, currentBalanceCents = 0L)
        val card2 = CreditCardEntity(
            id = 2,
            accountId = 2,
            creditLimitCents = 300000000L,
            cutOffDay = 25, // Corte el 25 de cada mes
            paymentDueDay = 10,
            interestRateEa = 24.0
        )

        val cards = listOf(
            CreditCardWithAccount(card = card1, account = account1, purchases = emptyList()),
            CreditCardWithAccount(card = card2, account = account2, purchases = emptyList())
        )

        val recommendation = FinancialFormulas.recommendBestCardForToday(cards, LocalDate.of(2026, 10, 6))
        assertNotNull(recommendation.recommendedCard)
        assertEquals("Bancolombia Oro", recommendation.recommendedCard?.account?.name)
    }

    @Test
    fun testCreditCardCycleCrossMonthNu() {
        // Tarjeta Nu: Corte día 15, Pago día 4 (del mes siguiente)
        val cutOffDay = 15
        val paymentDueDay = 4

        // Caso 1: Hoy es 10 de Octubre (antes del corte del 15 de Octubre)
        // El corte es el 15 de Octubre, y el pago es el 4 de Noviembre
        val cycleBeforeCut = FinancialFormulas.calculateCycleDates(cutOffDay, paymentDueDay, LocalDate.of(2026, 10, 10))
        assertEquals(LocalDate.of(2026, 10, 15), cycleBeforeCut.nextCutOffDate)
        assertEquals(LocalDate.of(2026, 11, 4), cycleBeforeCut.paymentDueDate)
        assertEquals(5, cycleBeforeCut.daysUntilCutOff)
        assertEquals(false, cycleBeforeCut.isAfterCutOffWaitingPayment)

        // Caso 2: Hoy es 20 de Octubre (después del corte del 15 de Octubre)
        // Ya cortó; el pago vence el 4 de Noviembre (15 días restantes)
        val cycleAfterCut = FinancialFormulas.calculateCycleDates(cutOffDay, paymentDueDay, LocalDate.of(2026, 10, 20))
        assertEquals(LocalDate.of(2026, 11, 15), cycleAfterCut.nextCutOffDate)
        assertEquals(LocalDate.of(2026, 11, 4), cycleAfterCut.paymentDueDate)
        assertEquals(true, cycleAfterCut.isAfterCutOffWaitingPayment)
        assertEquals(15, cycleAfterCut.daysUntilPaymentDue)
    }

    @Test
    fun testAccountGmfCalculations() {
        // Cuenta con $1.000.000 COP (100.000.000 centavos)
        val accountNormal = AccountEntity(
            id = 1,
            name = "Bancolombia Ahorros",
            type = AccountType.SAVINGS,
            currentBalanceCents = 100000000L,
            isExemptFromGmf = false
        )

        // 4x1000 sobre $1.000.000 = $4.000 COP (400.000 centavos)
        assertEquals(400000L, accountNormal.gmfTaxEstimatedCents)
        // Neto a retirar = $996.000 COP (99.600.000 centavos)
        assertEquals(99600000L, accountNormal.netBalanceAfterGmfCents)

        // Cuenta exenta legalmente de 4x1000
        val accountExempt = AccountEntity(
            id = 2,
            name = "RappiPay Principal",
            type = AccountType.SAVINGS,
            currentBalanceCents = 100000000L,
            isExemptFromGmf = true
        )
        assertEquals(0L, accountExempt.gmfTaxEstimatedCents)
        assertEquals(100000000L, accountExempt.netBalanceAfterGmfCents)
    }
}
