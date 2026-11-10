package com.personalfinance.cuentas.domain.usecases.creditcards

import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.CreditCardDao
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.AccountType
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateCreditCardUseCase @Inject constructor(
    private val accountDao: AccountDao,
    private val creditCardDao: CreditCardDao
) {
    suspend operator fun invoke(
        bankName: String,
        creditLimitCents: Long,
        cutOffDay: Int,
        paymentDueDay: Int,
        interestRateEa: Double,
        handlingFeeCents: Long = 0L,
        monthlyInsuranceCents: Long = 0L,
        installmentType: Int = 0,
        lastFourDigits: String? = null,
        revolvingRate: Double = 1.0,
        colorHex: String = "#1565C0"
    ): Long {
        require(creditLimitCents > 0) { "El cupo de crédito debe ser mayor a cero" }
        require(cutOffDay in 1..31) { "Día de corte inválido" }
        require(paymentDueDay in 1..31) { "Día límite de pago inválido" }
        require(revolvingRate in 0.0..1.0) { "La tasa de revolvente debe estar entre 0 y 1" }

        // 1. Crear la cuenta asociada de tipo CHECKING/Tarjeta
        val account = AccountEntity(
            name = bankName.trim(),
            type = AccountType.CHECKING,
            currentBalanceCents = 0L, // Saldo inicial usado = 0
            colorHex = colorHex,
            iconName = "credit_card",
            includeInTotal = false // No suma como dinero líquido propio en patrimonio
        )
        val accountId = accountDao.insertAccount(account)

        // 2. Crear la tarjeta de crédito
        val creditCard = CreditCardEntity(
            accountId = accountId,
            creditLimitCents = creditLimitCents,
            cutOffDay = cutOffDay,
            paymentDueDay = paymentDueDay,
            interestRateEa = interestRateEa,
            handlingFeeCents = handlingFeeCents,
            monthlyInsuranceCents = monthlyInsuranceCents,
            installmentType = installmentType,
            lastFourDigits = lastFourDigits,
            revolvingRate = revolvingRate
        )
        return creditCardDao.insertCreditCard(creditCard)
    }
}
