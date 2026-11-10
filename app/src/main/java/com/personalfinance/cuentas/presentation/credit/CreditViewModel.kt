package com.personalfinance.cuentas.presentation.credit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.CreditCardDao
import com.personalfinance.cuentas.data.local.dao.DebtDao
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount
import com.personalfinance.cuentas.domain.math.AmortizationRow
import com.personalfinance.cuentas.domain.math.CardRecommendation
import com.personalfinance.cuentas.domain.math.CreditCardBillingSummary
import com.personalfinance.cuentas.domain.math.DebtStrategyComparison
import com.personalfinance.cuentas.domain.math.ExtraPaymentSimulation
import com.personalfinance.cuentas.domain.math.FinancialFormulas
import com.personalfinance.cuentas.domain.usecases.creditcards.CreateCreditCardUseCase
import com.personalfinance.cuentas.domain.usecases.creditcards.PayCreditCardUseCase
import com.personalfinance.cuentas.domain.usecases.creditcards.RecordCardPurchaseUseCase
import com.personalfinance.cuentas.domain.usecases.debts.CreateDebtUseCase
import com.personalfinance.cuentas.domain.usecases.debts.RecordDebtPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreditViewModel @Inject constructor(
    private val creditCardDao: CreditCardDao,
    private val debtDao: DebtDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val createCreditCardUseCase: CreateCreditCardUseCase,
    private val recordCardPurchaseUseCase: RecordCardPurchaseUseCase,
    private val payCreditCardUseCase: PayCreditCardUseCase,
    private val createDebtUseCase: CreateDebtUseCase,
    private val recordDebtPaymentUseCase: RecordDebtPaymentUseCase
) : ViewModel() {

    val cards = creditCardDao.getAllCreditCardsWithAccount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accounts = accountDao.getAllActiveAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debts = debtDao.getAllDebtsWithPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cardRecommendation: StateFlow<CardRecommendation> = cards.map { cardList ->
        FinancialFormulas.recommendBestCardForToday(cardList, LocalDate.now())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CardRecommendation(null, 0, "Calculando recomendación...")
    )

    private val _extraMonthlyBudgetCents = MutableStateFlow(20000000L) // $200.000 COP por defecto
    val extraMonthlyBudgetCents: StateFlow<Long> = _extraMonthlyBudgetCents.asStateFlow()

    // Comparativa en tiempo real de Bola de Nieve vs Avalancha
    val strategyComparison: StateFlow<DebtStrategyComparison> = combine(
        debts,
        _extraMonthlyBudgetCents
    ) { debtList, extraBudget ->
        val activeDebts = debtList.map { it.debt }.filter { !it.isSettled }
        FinancialFormulas.compareSnowballVsAvalanche(activeDebts, extraBudget)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DebtStrategyComparison(
            snowball = com.personalfinance.cuentas.domain.math.StrategyResult("Bola de Nieve", 0, 0L, 0L, emptyList()),
            avalanche = com.personalfinance.cuentas.domain.math.StrategyResult("Avalancha", 0, 0L, 0L, emptyList()),
            interestDifferenceCents = 0L,
            recommendedStrategy = ""
        )
    )

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun setExtraMonthlyBudget(cents: Long) {
        _extraMonthlyBudgetCents.value = cents
    }

    fun getCardBillingSummary(cardWithAccount: CreditCardWithAccount): CreditCardBillingSummary {
        return FinancialFormulas.calculateCardBilling(
            card = cardWithAccount.card,
            purchases = cardWithAccount.purchases,
            today = LocalDate.now()
        )
    }

    fun getAmortizationSchedule(debt: DebtEntity): List<AmortizationRow> {
        val monthlyRate = FinancialFormulas.eaToMonthlyRate(debt.interestRateEa)
        // Estimar meses restantes a partir de saldo y cuota mensual
        val estimatedMonths = if (debt.monthlyInstallmentCents > 0L) {
            val ratio = debt.currentBalanceCents.toDouble() / debt.monthlyInstallmentCents.toDouble()
            maxOf(1, ratio.toInt() + 2)
        } else {
            12
        }
        return FinancialFormulas.generateAmortizationSchedule(
            principalCents = debt.currentBalanceCents,
            monthlyRateDecimal = monthlyRate,
            termMonths = minOf(360, estimatedMonths),
            overrideInstallmentCents = if (debt.monthlyInstallmentCents > 0L) debt.monthlyInstallmentCents else null
        )
    }

    fun simulateExtraPayment(debt: DebtEntity, extraAmountCents: Long): ExtraPaymentSimulation {
        val monthlyRate = FinancialFormulas.eaToMonthlyRate(debt.interestRateEa)
        return FinancialFormulas.simulateExtraPayment(
            currentBalanceCents = debt.currentBalanceCents,
            monthlyInstallmentCents = debt.monthlyInstallmentCents,
            monthlyRateDecimal = monthlyRate,
            extraPaymentCents = extraAmountCents
        )
    }

    fun createCreditCard(
        bankName: String,
        creditLimitCents: Long,
        cutOffDay: Int,
        paymentDueDay: Int,
        interestRateEa: Double,
        handlingFeeCents: Long,
        lastFourDigits: String?,
        revolvingRate: Double = 1.0,
        monthlyInsuranceCents: Long = 0L,
        installmentType: Int = 0
    ) {
        viewModelScope.launch {
            try {
                createCreditCardUseCase(
                    bankName = bankName,
                    creditLimitCents = creditLimitCents,
                    cutOffDay = cutOffDay,
                    paymentDueDay = paymentDueDay,
                    interestRateEa = interestRateEa,
                    handlingFeeCents = handlingFeeCents,
                    lastFourDigits = lastFourDigits,
                    revolvingRate = revolvingRate,
                    monthlyInsuranceCents = monthlyInsuranceCents,
                    installmentType = installmentType
                )
                _userMessage.emit("Tarjeta de crédito agregada")
            } catch (e: Exception) {
                _userMessage.emit("Error: ${e.message}")
            }
        }
    }

    fun recordCardPurchase(
        cardId: Long,
        merchant: String,
        amountCents: Long,
        installmentsTotal: Int,
        categoryId: Long,
        isInterestFree: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                recordCardPurchaseUseCase(
                    cardId = cardId,
                    merchant = merchant,
                    amountCents = amountCents,
                    installmentsTotal = installmentsTotal,
                    categoryId = categoryId,
                    isInterestFree = isInterestFree
                )
                _userMessage.emit("Compra en tarjeta registrada exitosamente")
            } catch (e: Exception) {
                _userMessage.emit("Error: ${e.message}")
            }
        }
    }

    fun payCreditCard(
        cardId: Long,
        accountId: Long,
        amountCents: Long
    ) {
        viewModelScope.launch {
            try {
                payCreditCardUseCase(
                    cardId = cardId,
                    accountId = accountId,
                    amountCents = amountCents
                )
                _userMessage.emit("Pago de tarjeta registrado. Cupo usado actualizado.")
            } catch (e: Exception) {
                _userMessage.emit("Error: ${e.message}")
            }
        }
    }

    fun createDebt(
        name: String,
        principalAmountCents: Long,
        interestRateEa: Double,
        dueDayOfMonth: Int,
        termMonths: Int?,
        customMonthlyInstallmentCents: Long?,
        monthlyInsuranceCents: Long = 0L
    ) {
        viewModelScope.launch {
            try {
                createDebtUseCase(
                    name = name,
                    principalAmountCents = principalAmountCents,
                    interestRateEa = interestRateEa,
                    dueDayOfMonth = dueDayOfMonth,
                    termMonths = termMonths,
                    customMonthlyInstallmentCents = customMonthlyInstallmentCents,
                    monthlyInsuranceCents = monthlyInsuranceCents
                )
                _userMessage.emit("Crédito o deuda agregado")
            } catch (e: Exception) {
                _userMessage.emit("Error: ${e.message}")
            }
        }
    }

    fun recordDebtPayment(debtId: Long, amountCents: Long, isExtra: Boolean) {
        viewModelScope.launch {
            try {
                recordDebtPaymentUseCase(
                    debtId = debtId,
                    amountPaidCents = amountCents,
                    isExtraPayment = isExtra
                )
                val msg = if (isExtra) "¡Abono directo a capital registrado con éxito!" else "Cuota de crédito pagada"
                _userMessage.emit(msg)
            } catch (e: Exception) {
                _userMessage.emit("Error: ${e.message}")
            }
        }
    }

    fun updateDebt(
        id: Long,
        name: String,
        principalAmountCents: Long,
        currentBalanceCents: Long,
        interestRateEa: Double,
        monthlyInstallmentCents: Long,
        dueDayOfMonth: Int,
        isSettled: Boolean,
        monthlyInsuranceCents: Long
    ) {
        viewModelScope.launch {
            try {
                val existing = debts.value.firstOrNull { it.debt.id == id }?.debt
                if (existing != null) {
                    debtDao.updateDebt(
                        existing.copy(
                            name = name,
                            principalAmountCents = principalAmountCents,
                            currentBalanceCents = currentBalanceCents,
                            interestRateEa = interestRateEa,
                            monthlyInstallmentCents = monthlyInstallmentCents,
                            dueDayOfMonth = dueDayOfMonth,
                            isSettled = isSettled,
                            monthlyInsuranceCents = monthlyInsuranceCents
                        )
                    )
                    _userMessage.emit("Préstamo actualizado correctamente")
                }
            } catch (e: Exception) {
                _userMessage.emit("Error al actualizar: ${e.message}")
            }
        }
    }

    fun updateCreditCard(
        cardId: Long,
        creditLimitCents: Long,
        cutOffDay: Int,
        paymentDueDay: Int,
        interestRateEa: Double,
        handlingFeeCents: Long,
        lastFourDigits: String?,
        revolvingRate: Double,
        monthlyInsuranceCents: Long,
        installmentType: Int
    ) {
        viewModelScope.launch {
            try {
                val existing = cards.value.firstOrNull { it.card.id == cardId }?.card
                if (existing != null) {
                    creditCardDao.updateCreditCard(
                        existing.copy(
                            creditLimitCents = creditLimitCents,
                            cutOffDay = cutOffDay,
                            paymentDueDay = paymentDueDay,
                            interestRateEa = interestRateEa,
                            handlingFeeCents = handlingFeeCents,
                            lastFourDigits = lastFourDigits,
                            revolvingRate = revolvingRate,
                            monthlyInsuranceCents = monthlyInsuranceCents,
                            installmentType = installmentType
                        )
                    )
                    _userMessage.emit("Tarjeta actualizada correctamente")
                }
            } catch (e: Exception) {
                _userMessage.emit("Error al actualizar: ${e.message}")
            }
        }
    }
}
