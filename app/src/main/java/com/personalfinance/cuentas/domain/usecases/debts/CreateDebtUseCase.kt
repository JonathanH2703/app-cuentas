package com.personalfinance.cuentas.domain.usecases.debts

import com.personalfinance.cuentas.data.local.dao.DebtDao
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.domain.math.FinancialFormulas
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateDebtUseCase @Inject constructor(
    private val debtDao: DebtDao
) {
    suspend operator fun invoke(
        name: String,
        principalAmountCents: Long,
        interestRateEa: Double,
        dueDayOfMonth: Int,
        termMonths: Int? = null,
        customMonthlyInstallmentCents: Long? = null,
        monthlyInsuranceCents: Long = 0L
    ): Long {
        require(principalAmountCents > 0) { "El monto del préstamo debe ser mayor a cero" }
        require(dueDayOfMonth in 1..31) { "Día de pago inválido" }

        val monthlyRate = FinancialFormulas.eaToMonthlyRate(interestRateEa)
        val installment = customMonthlyInstallmentCents
            ?: if (termMonths != null && termMonths > 0) {
                FinancialFormulas.calculateInstallmentAmountCents(principalAmountCents, monthlyRate, termMonths)
            } else {
                (principalAmountCents * 0.05).toLong() // 5% por defecto si no se especifica
            }

        val debt = DebtEntity(
            name = name.trim(),
            principalAmountCents = principalAmountCents,
            currentBalanceCents = principalAmountCents,
            interestRateEa = interestRateEa,
            monthlyInstallmentCents = installment,
            dueDayOfMonth = dueDayOfMonth,
            monthlyInsuranceCents = monthlyInsuranceCents
        )
        return debtDao.insertDebt(debt)
    }
}
