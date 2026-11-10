package com.personalfinance.cuentas.domain.math

import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToLong

data class AmortizationRow(
    val month: Int,
    val installmentCents: Long,
    val interestCents: Long,
    val principalPaidCents: Long,
    val remainingBalanceCents: Long
)

data class ExtraPaymentSimulation(
    val originalMonthsRemaining: Int,
    val originalTotalInterestCents: Long,
    val reduceTermNewMonths: Int,
    val reduceTermMonthsSaved: Int,
    val reduceTermInterestSavedCents: Long,
    val reduceInstallmentNewMonthlyCents: Long,
    val reduceInstallmentInterestSavedCents: Long
)

data class StrategyResult(
    val name: String,
    val monthsToFree: Int,
    val totalInterestPaidCents: Long,
    val totalPaidCents: Long,
    val payoffOrder: List<String>
)

data class DebtStrategyComparison(
    val snowball: StrategyResult,
    val avalanche: StrategyResult,
    val interestDifferenceCents: Long, // Ahorro de usar Avalancha vs Bola de Nieve
    val recommendedStrategy: String
)

data class CreditCardCycleInfo(
    val nextCutOffDate: LocalDate,
    val paymentDueDate: LocalDate,
    val daysUntilCutOff: Int,
    val daysUntilPaymentDue: Int,
    val nextCutOffFormatted: String,
    val paymentDueFormatted: String,
    val isAfterCutOffWaitingPayment: Boolean,
    val cycleStatusMessage: String
)

data class CreditCardBillingSummary(
    val cardId: Long,
    val creditLimitCents: Long,
    val totalUsedCents: Long,
    val availableLimitCents: Long,
    val cutOffBalanceCents: Long,
    val minimumPaymentCents: Long,
    val totalPaymentCents: Long,
    val interestIfMinimumPaidCents: Long,
    val daysUntilCutOff: Int,
    val daysUntilPaymentDue: Int,
    val nextCutOffFormatted: String = "",
    val paymentDueFormatted: String = "",
    val cycleStatusMessage: String = "",
    val isAfterCutOffWaitingPayment: Boolean = false
)

data class CardRecommendation(
    val recommendedCard: CreditCardWithAccount?,
    val graceDays: Int,
    val reason: String
)

object FinancialFormulas {



    /**
     * Convierte una Tasa Efectiva Anual (EA) a Tasa Nominal/Efectiva Mensual decimal.
     * Fórmula colombiana: i_mes = (1 + EA)^(1/12) - 1
     * Ej: 28.5% EA -> ~0.0211 mensual (2.11% mes vencido)
     */
    fun eaToMonthlyRate(eaPercentage: Double): Double {
        if (eaPercentage <= 0.0) return 0.0
        val eaDecimal = eaPercentage / 100.0
        return (1.0 + eaDecimal).pow(1.0 / 12.0) - 1.0
    }

    /**
     * Convierte una Tasa Mensual porcentual a Efectiva Anual (EA).
     * Fórmula: EA% = ((1 + i_mes)^12 - 1) * 100
     */
    fun monthlyRateToEa(monthlyPercentage: Double): Double {
        if (monthlyPercentage <= 0.0) return 0.0
        val mDecimal = monthlyPercentage / 100.0
        return ((1.0 + mDecimal).pow(12.0) - 1.0) * 100.0
    }

    /**
     * Calcula la cuota mensual fija (Sistema Francés) para diferidos o créditos.
     * Cuota = P * [ i * (1+i)^n ] / [ (1+i)^n - 1 ]
     * Si installments == 1 o tasa == 0, no hay interés.
     */
    fun calculateInstallmentAmountCents(
        principalCents: Long,
        monthlyRateDecimal: Double,
        installments: Int
    ): Long {
        if (principalCents <= 0L || installments <= 0) return 0L
        if (installments == 1 || monthlyRateDecimal <= 0.000001) {
            return (principalCents.toDouble() / installments).roundToLong()
        }

        val factor = (1.0 + monthlyRateDecimal).pow(installments.toDouble())
        val installmentDouble = principalCents.toDouble() * (monthlyRateDecimal * factor) / (factor - 1.0)
        return installmentDouble.roundToLong()
    }

    /**
     * Calcula el capital pendiente de una deuda diferida (Sistema Francés o Sin Interés).
     * @param paidInstallments Número de cuotas YA PAGADAS en periodos anteriores.
     */
    fun calculateRemainingPrincipalCents(
        principalCents: Long,
        monthlyRateDecimal: Double,
        totalInstallments: Int,
        paidInstallments: Int
    ): Long {
        if (principalCents <= 0L || totalInstallments <= 0) return 0L
        val remaining = (totalInstallments - paidInstallments).coerceAtLeast(0)
        if (remaining == 0) return 0L

        // Caso Sin Interés: Proporcional
        if (monthlyRateDecimal <= 0.000001) {
            return (principalCents.toDouble() * remaining / totalInstallments).roundToLong()
        }

        // Caso Con Interés (Sistema Francés):
        // Capital Pendiente = Cuota * [ 1 - (1+r)^-remaining ] / r
        val cuota = calculateInstallmentAmountCents(principalCents, monthlyRateDecimal, totalInstallments)
        val factor = (1.0 + monthlyRateDecimal).pow(-remaining.toDouble())
        val balance = cuota.toDouble() * (1.0 - factor) / monthlyRateDecimal
        return balance.roundToLong()
    }

    /**
     * Calcula el interés de mora diario sobre un saldo vencido no pagado.
     * Interés mora = Saldo * i_mora_mensual * (días / 30)
     */
    fun calculateLateInterestCents(
        overdueBalanceCents: Long,
        overdueDays: Int,
        annualLateRateEa: Double = 32.0
    ): Long {
        if (overdueBalanceCents <= 0L || overdueDays <= 0) return 0L
        val monthlyRate = eaToMonthlyRate(annualLateRateEa)
        val dailyRate = monthlyRate / 30.0
        return (overdueBalanceCents.toDouble() * dailyRate * overdueDays).roundToLong()
    }

    /**
     * Genera la tabla completa de amortización (cuota fija mensual).
     */
    fun generateAmortizationSchedule(
        principalCents: Long,
        monthlyRateDecimal: Double,
        termMonths: Int,
        overrideInstallmentCents: Long? = null
    ): List<AmortizationRow> {
        if (principalCents <= 0L || termMonths <= 0) return emptyList()

        val monthlyInstallment = overrideInstallmentCents ?: calculateInstallmentAmountCents(principalCents, monthlyRateDecimal, termMonths)
        var balance = principalCents
        val schedule = mutableListOf<AmortizationRow>()

        for (month in 1..termMonths) {
            val interest = (balance.toDouble() * monthlyRateDecimal).roundToLong()
            val principalPaid = if (month == termMonths) balance else min(monthlyInstallment - interest, balance)
            val finalInstallment = if (month == termMonths) principalPaid + interest else monthlyInstallment
            balance = max(0L, balance - principalPaid)

            schedule.add(
                AmortizationRow(
                    month = month,
                    installmentCents = finalInstallment,
                    interestCents = interest,
                    principalPaidCents = principalPaid,
                    remainingBalanceCents = balance
                )
            )
            if (balance <= 0L) break
        }
        return schedule
    }

    /**
     * Simula el impacto de un abono extraordinario a capital.
     */
    fun simulateExtraPayment(
        currentBalanceCents: Long,
        monthlyInstallmentCents: Long,
        monthlyRateDecimal: Double,
        extraPaymentCents: Long
    ): ExtraPaymentSimulation {
        val effectiveExtra = min(extraPaymentCents, currentBalanceCents)

        // 1. Simulación base sin abono
        val baseline = simulatePayoff(currentBalanceCents, monthlyInstallmentCents, monthlyRateDecimal)

        // 2. Escenario A: Reducción de Plazo (abono a capital manteniendo la misma cuota)
        val newBalance = currentBalanceCents - effectiveExtra
        val termReduced = simulatePayoff(newBalance, monthlyInstallmentCents, monthlyRateDecimal)

        val monthsSaved = max(0, baseline.first - termReduced.first)
        val interestSavedTerm = max(0L, baseline.second - termReduced.second)

        // 3. Escenario B: Reducción de Cuota (abono a capital manteniendo los meses originales)
        val newMonthlyInstallment = calculateInstallmentAmountCents(newBalance, monthlyRateDecimal, max(1, baseline.first))
        val installmentReduced = simulatePayoff(newBalance, newMonthlyInstallment, monthlyRateDecimal)
        val interestSavedInstallment = max(0L, baseline.second - installmentReduced.second)

        return ExtraPaymentSimulation(
            originalMonthsRemaining = baseline.first,
            originalTotalInterestCents = baseline.second,
            reduceTermNewMonths = termReduced.first,
            reduceTermMonthsSaved = monthsSaved,
            reduceTermInterestSavedCents = interestSavedTerm,
            reduceInstallmentNewMonthlyCents = newMonthlyInstallment,
            reduceInstallmentInterestSavedCents = interestSavedInstallment
        )
    }

    private fun simulatePayoff(
        balanceCents: Long,
        monthlyInstallmentCents: Long,
        monthlyRateDecimal: Double
    ): Pair<Int, Long> {
        var bal = balanceCents
        var months = 0
        var totalInterest = 0L

        while (bal > 0L && months < 360) {
            months++
            val interest = (bal.toDouble() * monthlyRateDecimal).roundToLong()
            totalInterest += interest
            val principal = min(bal, monthlyInstallmentCents - interest)
            if (principal <= 0L) break // Cuota no cubre intereses
            bal -= principal
        }
        return Pair(months, totalInterest)
    }

    /**
     * Compara las estrategias de desendeudamiento: Bola de Nieve vs Avalancha.
     */
    fun compareSnowballVsAvalanche(
        debts: List<DebtEntity>,
        extraMonthlyBudgetCents: Long
    ): DebtStrategyComparison {
        val activeDebts = debts.filter { !it.isSettled && it.currentBalanceCents > 0L }
        if (activeDebts.isEmpty()) {
            val emptyResult = StrategyResult("Sin deudas", 0, 0L, 0L, emptyList())
            return DebtStrategyComparison(emptyResult, emptyResult, 0L, "Al día")
        }

        // Bola de Nieve: menor saldo primero
        val snowballDebts = activeDebts.sortedBy { it.currentBalanceCents }
        val snowballResult = simulateDebtStrategy("Bola de Nieve", snowballDebts, extraMonthlyBudgetCents)

        // Avalancha: mayor tasa de interés primero
        val avalancheDebts = activeDebts.sortedByDescending { it.interestRateEa }
        val avalancheResult = simulateDebtStrategy("Avalancha", avalancheDebts, extraMonthlyBudgetCents)

        val diff = snowballResult.totalInterestPaidCents - avalancheResult.totalInterestPaidCents
        val recommended = if (diff > 0) "Avalancha (Ahorras ${diff / 100} COP en intereses)" else "Bola de Nieve"

        return DebtStrategyComparison(
            snowball = snowballResult,
            avalanche = avalancheResult,
            interestDifferenceCents = max(0L, diff),
            recommendedStrategy = recommended
        )
    }

    private fun simulateDebtStrategy(
        strategyName: String,
        debtOrder: List<DebtEntity>,
        extraMonthlyBudgetCents: Long
    ): StrategyResult {
        data class DebtState(
            val name: String,
            var balance: Long,
            val monthlyRate: Double,
            val minInstallment: Long
        )

        val states = debtOrder.map {
            DebtState(
                name = it.name,
                balance = it.currentBalanceCents,
                monthlyRate = eaToMonthlyRate(it.interestRateEa),
                minInstallment = it.monthlyInstallmentCents
            )
        }

        var months = 0
        var totalInterest = 0L
        var totalPaid = 0L
        var rolloverBudget = extraMonthlyBudgetCents
        val payoffOrder = mutableListOf<String>()

        while (states.any { it.balance > 0L } && months < 360) {
            months++
            var availableSnowball = rolloverBudget

            // 1. Pagar intereses y cuota mínima a todas las deudas activas
            for (debt in states.filter { it.balance > 0L }) {
                val interest = (debt.balance.toDouble() * debt.monthlyRate).roundToLong()
                totalInterest += interest
                val normalPrincipal = min(debt.balance, max(0L, debt.minInstallment - interest))
                debt.balance -= normalPrincipal
                totalPaid += (normalPrincipal + interest)

                if (debt.balance <= 0L && !payoffOrder.contains(debt.name)) {
                    payoffOrder.add(debt.name)
                    // Liberar su cuota mínima para la siguiente deuda en la lista
                    rolloverBudget += debt.minInstallment
                }
            }

            // 2. Todo el dinero extra se abona a la primera deuda prioritaria activa
            val target = states.firstOrNull { it.balance > 0L }
            if (target != null && availableSnowball > 0L) {
                val extraPayment = min(target.balance, availableSnowball)
                target.balance -= extraPayment
                totalPaid += extraPayment

                if (target.balance <= 0L && !payoffOrder.contains(target.name)) {
                    payoffOrder.add(target.name)
                    rolloverBudget += target.minInstallment
                }
            }
        }

        return StrategyResult(
            name = strategyName,
            monthsToFree = months,
            totalInterestPaidCents = totalInterest,
            totalPaidCents = totalPaid,
            payoffOrder = payoffOrder
        )
    }

    /**
     * Calcula el estado de facturación, cupo usado, pago mínimo y fecha de corte.
     */
    fun calculateCardBilling(
        card: CreditCardEntity,
        purchases: List<CardPurchaseEntity>,
        today: LocalDate = LocalDate.now()
    ): CreditCardBillingSummary {
        val activePurchases = purchases.filter { !it.isFullyPaid }
        
        // El saldo utilizado real es la suma del capital pendiente de cada compra
        val totalUsedCents = activePurchases.sumOf { p ->
            val purchaseRate = p.interestRateNm / 100.0
            if (p.installmentsTotal <= 1) {
                p.totalAmountCents
            } else {
                if (card.installmentType == 1) {
                    // Fintech: Capital constante
                    val capitalPerInstallment = p.totalAmountCents / p.installmentsTotal
                    val remainingCapital = p.totalAmountCents - (capitalPerInstallment * p.installmentsPaid)
                    max(0L, remainingCapital)
                } else {
                    // Tradicional: Francés
                    calculateRemainingPrincipalCents(
                        p.totalAmountCents,
                        purchaseRate,
                        p.installmentsTotal,
                        p.installmentsPaid
                    )
                }
            }
        }
        
        val availableLimit = max(0L, card.creditLimitCents - totalUsedCents)

        val monthlyRate = eaToMonthlyRate(card.interestRateEa)

        // Saldo del corte = cuota de manejo + seguro + compras a 1 cuota (del periodo) + cuotas de diferidos del mes
        var cutOffBalance = card.handlingFeeCents + card.monthlyInsuranceCents
        var installmentsDue = 0L

        val cycle = calculateCycleDates(card.cutOffDay, card.paymentDueDay, today)

        for (p in activePurchases) {
            val purchaseRate = p.interestRateNm / 100.0
            if (p.installmentsTotal <= 1) {
                // Solo incluimos compras a 1 cuota que entraron en este ciclo o están pendientes de ciclos anteriores
                // Para simplificar, incluimos todas las compras a 1 cuota no pagadas
                cutOffBalance += p.totalAmountCents
            } else {
                val installmentAmount = if (card.installmentType == 1) {
                    // Fintech: Capital/N + Interés sobre saldo actual
                    val capitalPerInstallment = p.totalAmountCents / p.installmentsTotal
                    val remainingPrincipalAtStartOfCycle = p.totalAmountCents - (capitalPerInstallment * p.installmentsPaid)
                    val interestThisMonth = (remainingPrincipalAtStartOfCycle.toDouble() * purchaseRate).roundToLong()
                    capitalPerInstallment + interestThisMonth
                } else {
                    calculateInstallmentAmountCents(p.totalAmountCents, purchaseRate, p.installmentsTotal)
                }
                
                // Una compra diferida solo suma al corte si ya pasó su primer mes
                 val billed = hasCutOffPassed(p.purchaseDateMillis, card.cutOffDay, today)
                 if (billed || p.installmentsPaid > 0) {
                     cutOffBalance += installmentAmount
                     installmentsDue += installmentAmount
                 }
            }
        }

        // Pago mínimo REAL: lo obligatorio (cuotas de diferidos + cuota de manejo + seguro) más un abono
        // mínimo a capital sobre el saldo revolvente (compras a 1 cuota).
        // Usamos la tasa de revolvente configurada para la tarjeta (ej: 5% tradicional vs 100% Nu/Rappi).
        val mandatoryPayment = installmentsDue + card.handlingFeeCents + card.monthlyInsuranceCents
        val revolvingBalanceAtCut = max(0L, cutOffBalance - mandatoryPayment)
        val minimumPayment = mandatoryPayment + (revolvingBalanceAtCut * card.revolvingRate).roundToLong()

        // Si solo se paga el mínimo, el resto rota generando intereses el próximo mes
        val revolvingBalance = max(0L, totalUsedCents - minimumPayment)
        val interestIfMinimumPaid = (revolvingBalance.toDouble() * monthlyRate).roundToLong()

        return CreditCardBillingSummary(
            cardId = card.id,
            creditLimitCents = card.creditLimitCents,
            totalUsedCents = totalUsedCents,
            availableLimitCents = availableLimit,
            cutOffBalanceCents = cutOffBalance,
            minimumPaymentCents = minimumPayment,
            totalPaymentCents = cutOffBalance, // El pago total del periodo es el saldo del corte
            interestIfMinimumPaidCents = interestIfMinimumPaid,
            daysUntilCutOff = cycle.daysUntilCutOff,
            daysUntilPaymentDue = cycle.daysUntilPaymentDue,
            nextCutOffFormatted = cycle.nextCutOffFormatted,
            paymentDueFormatted = cycle.paymentDueFormatted,
            cycleStatusMessage = cycle.cycleStatusMessage,
            isAfterCutOffWaitingPayment = cycle.isAfterCutOffWaitingPayment
        )
    }

    /**
     * Cuota que se pagará en el PRÓXIMO vencimiento de una tarjeta de crédito.
     *
     * Fuente única de verdad para el compromiso mensual de una tarjeta (la usan el dashboard,
     * la reserva para el próximo ingreso, la proyección de caja y la línea de tiempo).
     *
     * Reglas:
     * - La cuota de manejo se cobra siempre.
     * - Una compra a 1 cuota paga el porcentaje definido por `revolvingRate` (ej. 100% o 5%).
     * - Una compra diferida paga su cuota fija calculada.
     * - Solo entran compras que ya pasaron su primer corte de facturación (o que ya llevan
     *   cuotas pagadas).
     */
    fun calculateCardNextPaymentCents(
        card: CreditCardEntity,
        purchases: List<CardPurchaseEntity>,
        today: LocalDate = LocalDate.now()
    ): Long {
        var mandatoryTotal = card.handlingFeeCents + card.monthlyInsuranceCents
        var revolvingTotal = 0L

        purchases.forEach { p ->
            if (p.isFullyPaid) return@forEach
            if (p.installmentsPaid >= p.installmentsTotal) return@forEach

            val started = p.installmentsPaid >= 1
            val billed = hasCutOffPassed(p.purchaseDateMillis, card.cutOffDay, today)
            if (!started && !billed) return@forEach

            if (p.installmentsTotal <= 1) {
                revolvingTotal += p.totalAmountCents
            } else {
                val monthlyRate = (p.interestRateNm / 100.0).coerceAtLeast(0.0)
                val installmentCents = if (card.installmentType == 1) {
                    val capitalPerInstallment = p.totalAmountCents / p.installmentsTotal
                    val remainingPrincipalAtStart = p.totalAmountCents - (capitalPerInstallment * p.installmentsPaid)
                    val interestThisMonth = (remainingPrincipalAtStart.toDouble() * monthlyRate).roundToLong()
                    capitalPerInstallment + interestThisMonth
                } else {
                    calculateInstallmentAmountCents(p.totalAmountCents, monthlyRate, p.installmentsTotal)
                }
                mandatoryTotal += installmentCents
            }
        }

        return mandatoryTotal + (revolvingTotal * card.revolvingRate).roundToLong()
    }

    /**
     * Cronograma de lo que se pagará en los PRÓXIMOS [months] vencimientos de una tarjeta.
     */
    fun calculateCardPaymentsSchedule(
        card: CreditCardEntity,
        purchases: List<CardPurchaseEntity>,
        months: Int,
        today: LocalDate = LocalDate.now()
    ): List<Long> {
        if (months <= 0) return emptyList()
        val mandatorySchedule = LongArray(months)
        val revolvingSchedule = LongArray(months)

        purchases.forEach { p ->
            if (p.isFullyPaid) return@forEach
            if (p.installmentsPaid >= p.installmentsTotal) return@forEach

            val alreadyStarted = p.installmentsPaid >= 1
            val alreadyBilled = hasCutOffPassed(p.purchaseDateMillis, card.cutOffDay, today)
            val firstBillableIndex = if (alreadyStarted || alreadyBilled) 0 else 1

            if (p.installmentsTotal <= 1) {
                if (firstBillableIndex < months) {
                    revolvingSchedule[firstBillableIndex] += p.totalAmountCents
                }
            } else {
                val monthlyRate = (p.interestRateNm / 100.0).coerceAtLeast(0.0)
                val remainingInstallments = (p.installmentsTotal - p.installmentsPaid).coerceAtLeast(0)
                
                for (k in 0 until remainingInstallments) {
                    val index = firstBillableIndex + k
                    if (index >= months) break

                    val installmentCents = if (card.installmentType == 1) {
                        val capitalPerInstallment = p.totalAmountCents / p.installmentsTotal
                        // El capital pendiente baja mes a mes
                        val remainingPrincipal = p.totalAmountCents - (capitalPerInstallment * (p.installmentsPaid + k))
                        val interestThisMonth = (remainingPrincipal.toDouble() * monthlyRate).roundToLong()
                        capitalPerInstallment + interestThisMonth
                    } else {
                        calculateInstallmentAmountCents(p.totalAmountCents, monthlyRate, p.installmentsTotal)
                    }
                    
                    mandatorySchedule[index] += installmentCents
                }
            }
        }

        val finalSchedule = mutableListOf<Long>()
        for (i in 0 until months) {
            val amount = card.handlingFeeCents + card.monthlyInsuranceCents + mandatorySchedule[i] + (revolvingSchedule[i] * card.revolvingRate).roundToLong()
            finalSchedule.add(amount)
        }

        return finalSchedule
    }

    /**
     * Indica si ya se cerró al menos un corte de facturación desde la fecha de compra.
     * El corte del mismo mes se alcanza si el día de compra no supera el día de corte.
     */
    fun hasCutOffPassed(purchaseMillis: Long, cutOffDay: Int, today: LocalDate): Boolean {
        val purchase = java.time.Instant.ofEpochMilli(purchaseMillis)
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        val cutOff = cutOffDay.coerceIn(1, 31)
        val firstCutOff = if (purchase.dayOfMonth <= cutOff) {
            purchase.withDayOfMonth(minOf(cutOff, purchase.lengthOfMonth()))
        } else {
            val next = purchase.plusMonths(1)
            next.withDayOfMonth(minOf(cutOff, next.lengthOfMonth()))
        }
        return !firstCutOff.isAfter(today)
    }

    /**
     * Calcula con exactitud las fechas del calendario para cortes y pagos de tarjetas,
     * contemplando casos como Nu donde el corte es en un mes y el límite de pago en el mes siguiente.
     */
    fun calculateCycleDates(
        cutOffDay: Int,
        paymentDueDay: Int,
        today: LocalDate = LocalDate.now()
    ): CreditCardCycleInfo {
        val currentDay = today.dayOfMonth
        val formatter = DateTimeFormatter.ofPattern("d MMM", Locale("es", "CO"))

        // 1. Determinar el próximo corte de facturación
        val nextCutOffDate = if (cutOffDay > currentDay) {
            today.withDayOfMonth(minOf(cutOffDay, today.lengthOfMonth()))
        } else {
            val nextMonth = today.plusMonths(1)
            nextMonth.withDayOfMonth(minOf(cutOffDay, nextMonth.lengthOfMonth()))
        }

        // 2. Determinar la fecha límite de pago del ciclo relevante
        val paymentDueDate: LocalDate
        val isAfterCutOffWaitingPayment: Boolean
        val statusMessage: String

        if (paymentDueDay <= cutOffDay) {
            // Caso Nu / Bancolombia: el pago vence el mes siguiente al corte (ej: corte 15, pago 4)
            if (currentDay > cutOffDay) {
                // Corte de este mes ya ocurrió. Vence el mes siguiente.
                val nextMonth = today.plusMonths(1)
                paymentDueDate = nextMonth.withDayOfMonth(minOf(paymentDueDay, nextMonth.lengthOfMonth()))
                isAfterCutOffWaitingPayment = true
                statusMessage = "Corte cerrado el día $cutOffDay. Pagar antes del ${paymentDueDate.format(formatter)}."
            } else if (currentDay <= paymentDueDay) {
                // Primeros días del mes, antes del vencimiento del ciclo anterior
                paymentDueDate = today.withDayOfMonth(minOf(paymentDueDay, today.lengthOfMonth()))
                isAfterCutOffWaitingPayment = true
                statusMessage = "Pago pendiente del ciclo anterior. Vence el ${paymentDueDate.format(formatter)}."
            } else {
                // Entre el día de pago y el día de corte (ej. 5 al 15)
                val nextMonth = today.plusMonths(1)
                paymentDueDate = nextMonth.withDayOfMonth(minOf(paymentDueDay, nextMonth.lengthOfMonth()))
                isAfterCutOffWaitingPayment = false
                statusMessage = "Compras acumulándose hasta el corte del día $cutOffDay. Se pagarán el ${paymentDueDate.format(formatter)}."
            }
        } else {
            // Corte y pago en el mismo mes calendario (ej: corte 10, pago 28)
            if (currentDay > paymentDueDay) {
                val nextMonth = today.plusMonths(1)
                paymentDueDate = nextMonth.withDayOfMonth(minOf(paymentDueDay, nextMonth.lengthOfMonth()))
                isAfterCutOffWaitingPayment = false
                statusMessage = "Compras en curso. Próximo corte el ${nextCutOffDate.format(formatter)}."
            } else if (currentDay > cutOffDay) {
                paymentDueDate = today.withDayOfMonth(minOf(paymentDueDay, today.lengthOfMonth()))
                isAfterCutOffWaitingPayment = true
                statusMessage = "Corte cerrado el día $cutOffDay. Pagar antes del ${paymentDueDate.format(formatter)}."
            } else {
                paymentDueDate = today.withDayOfMonth(minOf(paymentDueDay, today.lengthOfMonth()))
                isAfterCutOffWaitingPayment = false
                statusMessage = "Compras acumulándose hasta el día $cutOffDay. Vence el ${paymentDueDate.format(formatter)}."
            }
        }

        val daysUntilCut = ChronoUnit.DAYS.between(today, nextCutOffDate).toInt().coerceAtLeast(0)
        val daysUntilDue = ChronoUnit.DAYS.between(today, paymentDueDate).toInt().coerceAtLeast(0)

        return CreditCardCycleInfo(
            nextCutOffDate = nextCutOffDate,
            paymentDueDate = paymentDueDate,
            daysUntilCutOff = daysUntilCut,
            daysUntilPaymentDue = daysUntilDue,
            nextCutOffFormatted = nextCutOffDate.format(formatter),
            paymentDueFormatted = paymentDueDate.format(formatter),
            isAfterCutOffWaitingPayment = isAfterCutOffWaitingPayment,
            cycleStatusMessage = statusMessage
        )
    }

    /**
     * Recomienda cuál tarjeta de crédito conviene usar hoy según su fecha de corte.
     * La tarjeta ideal es aquella cuyo corte acaba de pasar, porque la compra entra en el siguiente ciclo,
     * otorgando el máximo plazo de gracia sin intereses (hasta 45 a 55 días de plazo).
     */
    fun recommendBestCardForToday(
        cards: List<CreditCardWithAccount>,
        today: LocalDate = LocalDate.now()
    ): CardRecommendation {
        if (cards.isEmpty()) {
            return CardRecommendation(null, 0, "No tienes tarjetas de crédito registradas.")
        }

        val currentDay = today.dayOfMonth
        var bestCard: CreditCardWithAccount? = null
        var maxGraceDays = -1

        for (cardWithAccount in cards) {
            val card = cardWithAccount.card
            // Días desde hoy hasta el corte:
            // Si la compra se hace 1 o 2 días después del corte, financia todo el mes siguiente + días hasta la fecha de pago
            val daysToCut = if (card.cutOffDay > currentDay) {
                card.cutOffDay - currentDay
            } else {
                (today.lengthOfMonth() - currentDay) + card.cutOffDay
            }

            // Días de gracia totales para compras a 1 cuota:
            // días hasta el próximo corte + días entre corte y fecha de pago (aprox 15-20 días)
            val paymentGrace = if (card.paymentDueDay > card.cutOffDay) {
                card.paymentDueDay - card.cutOffDay
            } else {
                (today.lengthOfMonth() - card.cutOffDay) + card.paymentDueDay
            }

            val totalGrace = daysToCut + paymentGrace

            if (totalGrace > maxGraceDays) {
                maxGraceDays = totalGrace
                bestCard = cardWithAccount
            }
        }

        val cardName = bestCard?.account?.name ?: "Tarjeta"
        return CardRecommendation(
            recommendedCard = bestCard,
            graceDays = maxGraceDays,
            reason = "Usa $cardName hoy: su corte está a $maxGraceDays días de gracia total antes de pagar a 1 cuota sin intereses."
        )
    }
}
