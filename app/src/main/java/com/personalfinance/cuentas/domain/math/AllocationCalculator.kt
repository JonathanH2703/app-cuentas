package com.personalfinance.cuentas.domain.math

import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.BlockType
import java.time.LocalDate

data class AllocatedBlockResult(
    val blockId: Long,
    val blockName: String,
    val blockType: BlockType,
    val percentage: Int,
    val assignedCents: Long,
    val colorHex: String
)

data class SafeToSpendCalculation(
    val consolidatedCashCents: Long,       // Dinero total real en cuentas líquidas (ahorros, billeteras, efectivo)
    val committedNeedsCents: Long,         // Obligaciones fijas / cuotas pendientes este mes
    val safeToSpendTotalCents: Long,       // Saldo libre disponible
    val safeToSpendDailyCents: Long,       // Saldo diario para no romper el mes
    val daysRemainingInMonth: Int,
    val status: SafeToSpendStatus,
    val message: String
)

enum class SafeToSpendStatus {
    GREEN,   // > 20% del presupuesto libre o holgura diaria suficiente
    YELLOW,  // 5% - 20% margen ajustado, precaución
    RED      // < 5% o negativo (déficit / sobregiro técnico)
}

data class CashFlowDayProjection(
    val date: LocalDate,
    val dayOfMonth: Int,
    val startingBalanceCents: Long,
    val projectedIncomeCents: Long,
    val projectedExpenseCents: Long,
    val projectedNetBalanceCents: Long,
    // Ahorro acumulado (desde cero) suponiendo que solo se gasta lo obligatorio.
    // Se calcula como la suma acumulada de (ingreso bruto − obligaciones) del horizonte.
    val projectedSavingsCents: Long = 0L
)

data class CashFlowProjectionSummary(
    val days30NetCents: Long,
    val days60NetCents: Long,
    val days90NetCents: Long,
    val dailyProjections: List<CashFlowDayProjection>
)

object AllocationCalculator {

    /**
     * Reparte un ingreso en centavos entre los bloques de un modelo de asignación (ej. 50/30/20).
     * Garantiza matemáticamente CERO pérdida de centavos por redondeo:
     * Si la suma de las partes truncadas/redondeadas difiere del total,
     * el residuo se absorbe en el bloque marcado con `absorbRemainder = true`
     * (o el bloque con mayor porcentaje si ninguno está marcado).
     */
    fun splitIncomeAcrossBlocks(
        totalIncomeCents: Long,
        blocks: List<AllocationBlockEntity>
    ): List<AllocatedBlockResult> {
        if (totalIncomeCents <= 0L || blocks.isEmpty()) return emptyList()

        val results = mutableListOf<AllocatedBlockResult>()
        var allocatedSum = 0L

        for (block in blocks) {
            val amount = (totalIncomeCents * block.percentage) / 100L
            allocatedSum += amount
            results.add(
                AllocatedBlockResult(
                    blockId = block.id,
                    blockName = block.name,
                    blockType = block.type,
                    percentage = block.percentage,
                    assignedCents = amount,
                    colorHex = block.colorHex
                )
            )
        }

        // Absorción del residuo de centavos
        val remainder = totalIncomeCents - allocatedSum
        if (remainder != 0L) {
            val targetIndex = blocks.indexOfFirst { it.absorbRemainder }.let { index ->
                if (index >= 0) index else blocks.indices.maxByOrNull { blocks[it].percentage } ?: 0
            }
            val target = results[targetIndex]
            results[targetIndex] = target.copy(assignedCents = target.assignedCents + remainder)
        }

        return results
    }

    /**
     * Calcula el semáforo Safe-to-Spend (Disponible para gastar sin culpa).
     * Versión inteligente:
     * Considera el saldo mínimo proyectado en el flujo de caja (bottleneck).
     * Si el saldo proyectado nunca baja de cero, el "Safe-to-Spend" es el excedente
     * que se puede gastar hoy sin que el saldo futuro sea menor a cero en ningún momento.
     */
    fun calculateSafeToSpendIntelligent(
        consolidatedCashCents: Long,
        dailyProjections: List<CashFlowDayProjection>,
        savingsToReserveCents: Long = 0L,
        today: LocalDate = LocalDate.now()
    ): SafeToSpendCalculation {
        val daysInMonth = today.lengthOfMonth()
        val currentDay = today.dayOfMonth
        val daysRemaining = maxOf(1, daysInMonth - currentDay + 1)

        // El "Safe to Spend" real es el saldo mínimo que alcanzará la cuenta en el horizonte visible.
        // Ahora restamos el ahorro que debe quedar intacto a fin de mes.
        val minProjectedBalance = dailyProjections
            .filter { !it.date.isBefore(today) }
            .take(30) // Miramos los próximos 30 días
            .minOfOrNull { it.projectedNetBalanceCents } ?: consolidatedCashCents

        val safeToSpendTotal = minProjectedBalance - savingsToReserveCents
        val safeDaily = if (safeToSpendTotal > 0L) safeToSpendTotal / daysRemaining else 0L

        val status: SafeToSpendStatus
        val message: String

        if (consolidatedCashCents < 0L) {
            status = SafeToSpendStatus.RED
            message = "Tu saldo actual está en negativo (${CurrencyFormatter.formatCentsToCop(consolidatedCashCents)}). Cuentas en déficit o sobregiro."
        } else if (safeToSpendTotal <= 0L) {
            status = SafeToSpendStatus.RED
            message = if (safeToSpendTotal < 0L) {
                "Déficit proyectado: te faltarán ${CurrencyFormatter.formatCentsToCop(-safeToSpendTotal)} para cubrir tus obligaciones y meta de ahorro."
            } else {
                "Tus compromisos y ahorro superan lo disponible. Evita gastos extra."
            }
        } else {
            val ratio = safeToSpendTotal.toDouble() / maxOf(1L, consolidatedCashCents).toDouble()
            if (ratio >= 0.15) { // Bajamos el umbral a 15% para ser más realistas con deudas
                status = SafeToSpendStatus.GREEN
                message = "Al día. Tienes ${CurrencyFormatter.formatCentsToCop(safeToSpendTotal)} libres para gastar este mes."
            } else {
                status = SafeToSpendStatus.YELLOW
                message = "Margen ajustado. Te quedan ${CurrencyFormatter.formatCentsToCop(safeToSpendTotal)} libres. Procura no salirte del plan."
            }
        }

        return SafeToSpendCalculation(
            consolidatedCashCents = consolidatedCashCents,
            committedNeedsCents = 0L, // Ya implícito en la proyección
            safeToSpendTotalCents = safeToSpendTotal,
            safeToSpendDailyCents = safeDaily,
            daysRemainingInMonth = daysRemaining,
            status = status,
            message = message
        )
    }

    /**
     * Calcula el semáforo Safe-to-Spend (Disponible para gastar sin culpa).
     * Fórmula bancaria:
     * Safe-to-Spend = Saldo Líquido Consolidado - Gastos Fijos/Cuotas Pendientes de este Mes.
     * Saldo Diario = Safe-to-Spend / Días restantes del mes.
     */
    fun calculateSafeToSpend(
        consolidatedCashCents: Long,
        committedExpensesCents: Long,
        today: LocalDate = LocalDate.now()
    ): SafeToSpendCalculation {
        val daysInMonth = today.lengthOfMonth()
        val currentDay = today.dayOfMonth
        val daysRemaining = maxOf(1, daysInMonth - currentDay + 1)

        val safeToSpendTotal = consolidatedCashCents - committedExpensesCents
        val safeDaily = if (safeToSpendTotal > 0L) safeToSpendTotal / daysRemaining else 0L

        val status: SafeToSpendStatus
        val message: String

        if (consolidatedCashCents < 0L) {
            status = SafeToSpendStatus.RED
            message = "Tu saldo actual está en negativo (${CurrencyFormatter.formatCentsToCop(consolidatedCashCents)}). Cuentas en déficit o sobregiro."
        } else if (safeToSpendTotal <= 0L) {
            status = SafeToSpendStatus.RED
            message = "Tus compromisos y pagos fijos superan lo que tienes disponible. Es momento de ajustarse y no hacer gastos extra."
        } else {
            val ratio = safeToSpendTotal.toDouble() / maxOf(1L, consolidatedCashCents).toDouble()
            if (ratio >= 0.20) {
                status = SafeToSpendStatus.GREEN
                message = "Vas muy bien. Tienes dinero libre para tus gastos diarios sin descuidar tus compromisos."
            } else {
                status = SafeToSpendStatus.YELLOW
                message = "Ojo: Te queda menos del 20% libre para lo que falta del mes. Modérate con las compras y salidas."
            }
        }

        return SafeToSpendCalculation(
            consolidatedCashCents = consolidatedCashCents,
            committedNeedsCents = committedExpensesCents,
            safeToSpendTotalCents = safeToSpendTotal,
            safeToSpendDailyCents = safeDaily,
            daysRemainingInMonth = daysRemaining,
            status = status,
            message = message
        )
    }

    /**
     * Proyección de Flujo de Caja a 30, 60 y 90 días con ingresos y gastos periódicos.
     */
    fun projectCashFlow(
        initialBalanceCents: Long,
        dailyIncomes: Map<LocalDate, Long>,
        dailyExpenses: Map<LocalDate, Long>,
        startDate: LocalDate = LocalDate.now(),
        horizonDays: Int = 90,
        // Ingresos SIN descontar el ahorro. Se usan para la curva de "ahorro acumulado":
        // si solo se gasta lo obligatorio, todo el ingreso restante se acumula.
        dailyGrossIncomes: Map<LocalDate, Long> = dailyIncomes
    ): CashFlowProjectionSummary {
        var currentBalance = initialBalanceCents
        var accumulatedSavings = 0L
        val dailyList = mutableListOf<CashFlowDayProjection>()

        var balanceAt30 = initialBalanceCents
        var balanceAt60 = initialBalanceCents
        var balanceAt90 = initialBalanceCents

        for (i in 0 until horizonDays) {
            val date = startDate.plusDays(i.toLong())
            val inc = dailyIncomes[date] ?: 0L
            val exp = dailyExpenses[date] ?: 0L
            val grossInc = dailyGrossIncomes[date] ?: 0L
            val net = currentBalance + inc - exp

            // Ahorro acumulado partiendo de cero: solo se gasta lo obligatorio (exp).
            accumulatedSavings += grossInc - exp

            dailyList.add(
                CashFlowDayProjection(
                    date = date,
                    dayOfMonth = date.dayOfMonth,
                    startingBalanceCents = currentBalance,
                    projectedIncomeCents = inc,
                    projectedExpenseCents = exp,
                    projectedNetBalanceCents = net,
                    projectedSavingsCents = accumulatedSavings
                )
            )

            currentBalance = net

            if (i == 29) balanceAt30 = currentBalance
            if (i == 59) balanceAt60 = currentBalance
            if (i == 89) balanceAt90 = currentBalance
        }

        return CashFlowProjectionSummary(
            days30NetCents = balanceAt30,
            days60NetCents = balanceAt60,
            days90NetCents = balanceAt90,
            dailyProjections = dailyList
        )
    }
}
