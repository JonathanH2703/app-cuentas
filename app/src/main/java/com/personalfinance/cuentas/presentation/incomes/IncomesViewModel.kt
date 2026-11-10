package com.personalfinance.cuentas.presentation.incomes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.IncomeDao
import com.personalfinance.cuentas.data.local.entities.IncomeEntity
import com.personalfinance.cuentas.data.local.entities.IncomeFrequency
import com.personalfinance.cuentas.domain.usecases.ConfirmIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

import com.personalfinance.cuentas.data.local.dao.DebtDao
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao
import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.entities.BlockType
import com.personalfinance.cuentas.domain.schedule.DueDates
import com.personalfinance.cuentas.presentation.dashboard.PlannedMovementUi
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
class IncomesViewModel @Inject constructor(
    private val incomeDao: IncomeDao,
    private val accountDao: AccountDao,
    private val debtDao: DebtDao,
    private val recurringItemDao: RecurringItemDao,
    private val allocationDao: AllocationDao,
    private val confirmIncomeUseCase: ConfirmIncomeUseCase
) : ViewModel() {

    private val envelopeDateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMM", Locale("es", "CO"))

    private val _selectedHorizonMonths = MutableStateFlow(1)
    val selectedHorizonMonths = _selectedHorizonMonths.asStateFlow()

    val accounts = accountDao.getAllActiveAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingIncomes = incomeDao.getPendingIncomes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receivedIncomes = incomeDao.getReceivedIncomes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPendingCents = combine(pendingIncomes, _selectedHorizonMonths) { incomes, horizon ->
        val today = LocalDate.now()
        val zone = ZoneId.systemDefault()
        // Desde hoy (lo que aún falta por entrar), no desde el inicio del mes.
        val startMillis = today.atStartOfDay(zone).toInstant().toEpochMilli()
        // "Este mes" (horizon=1) termina el último día del mes calendario ACTUAL,
        // no en una ventana de 30 días. "Próximos 5 meses" abarca 5 meses calendario.
        val endMonth = today.plusMonths((horizon - 1).toLong())
        val endMillis = endMonth
            .withDayOfMonth(endMonth.lengthOfMonth())
            .atTime(23, 59, 59)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
        incomes.filter { it.expectedDateMillis in startMillis..endMillis }
            .sumOf { it.amountCents }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val plannedMovements: StateFlow<List<PlannedMovementUi>> = combine(
        accountDao.getTotalConsolidatedBalanceCents(),
        incomeDao.getAllIncomes(),
        debtDao.getActiveDebts(),
        recurringItemDao.getAllActiveRecurringItems(),
        allocationDao.getActiveModelWithBlocks()
    ) { balance, incomes, debts, recurrings, model ->
        val today = LocalDate.now()
        val savingsPct = model?.blocks?.find { it.type == BlockType.SAVINGS }?.percentage ?: 20
        val events = mutableListOf<PlannedEvent>()

        incomes.filter { !it.isReceived }.forEach { inc ->
            val date = Instant.ofEpochMilli(inc.expectedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            if (!date.isBefore(today)) {
                events.add(PlannedEvent(
                    date = date,
                    isIncome = true,
                    title = inc.description,
                    amountCents = inc.amountCents
                ))
            }
        }

        debts.forEach { debt ->
            events.add(
                PlannedEvent(
                    date = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today),
                    isIncome = false,
                    title = debt.name,
                    amountCents = debt.monthlyInstallmentCents
                )
            )
        }

        recurrings.forEach { rec ->
            val recDate = Instant.ofEpochMilli(rec.nextDueDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            events.add(PlannedEvent(if (!recDate.isBefore(today)) recDate else nextOccurrence(recDate.dayOfMonth, today), false, rec.title, rec.amountCents))
        }

        val ordered = events.sortedWith(compareBy({ it.date }, { if (it.isIncome) 0 else 1 }))
        var currentBalance = balance ?: 0L
        
        ordered.map { ev ->
            if (ev.isIncome) {
                val spendableInc = (ev.amountCents * (100 - savingsPct)) / 100L
                currentBalance += spendableInc
                PlannedMovementUi(ev.date, ev.date.format(envelopeDateFormatter), "Entra ${ev.title}", ev.amountCents, true, currentBalance)
            } else {
                currentBalance -= ev.amountCents
                PlannedMovementUi(ev.date, ev.date.format(envelopeDateFormatter), ev.title, ev.amountCents, false, currentBalance)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun setHorizon(months: Int) {
        _selectedHorizonMonths.value = months
    }

    private data class PlannedEvent(val date: LocalDate, val isIncome: Boolean, val title: String, val amountCents: Long)

    private fun nextOccurrence(dayOfMonth: Int, today: LocalDate): LocalDate {
        val d = dayOfMonth.coerceIn(1, 31)
        val thisMonthDay = d.coerceAtMost(today.lengthOfMonth())
        return if (today.dayOfMonth <= thisMonthDay) today.withDayOfMonth(thisMonthDay)
        else today.plusMonths(1).withDayOfMonth(d.coerceAtMost(today.plusMonths(1).lengthOfMonth()))
    }

    fun createIncome(
        description: String,
        amountCents: Long,
        accountId: Long,
        frequency: IncomeFrequency,
        expectedDateMillis: Long
    ) {
        viewModelScope.launch {
            if (description.isBlank() || amountCents <= 0L || accountId == 0L) {
                _userMessage.emit("Por favor completa los datos del ingreso")
                return@launch
            }
            val income = IncomeEntity(
                description = description.trim(),
                amountCents = amountCents,
                accountId = accountId,
                frequency = frequency,
                expectedDateMillis = expectedDateMillis
            )
            incomeDao.insertIncome(income)
            _userMessage.emit("Ingreso proyectado programado")
        }
    }

    /**
     * Registra un ingreso de forma inmediata: crea el ingreso y lo acredita
     * de una vez a la cuenta seleccionada (aumenta su saldo).
     */
    fun registerQuickIncome(
        description: String,
        amountCents: Long,
        accountId: Long
    ) {
        viewModelScope.launch {
            try {
                if (amountCents <= 0L || accountId == 0L) {
                    _userMessage.emit("Por favor completa los datos del ingreso")
                    return@launch
                }
                val income = IncomeEntity(
                    description = description.trim().ifBlank { "Ingreso rápido" },
                    amountCents = amountCents,
                    accountId = accountId,
                    frequency = IncomeFrequency.ONE_TIME,
                    expectedDateMillis = System.currentTimeMillis()
                )
                val incomeId = incomeDao.insertIncome(income)
                confirmIncomeUseCase(incomeId, amountCents, accountId)
                _userMessage.emit("Ingreso registrado en tu cuenta")
            } catch (e: Exception) {
                _userMessage.emit("Error al registrar ingreso: ${e.message}")
            }
        }
    }

    fun updateIncome(
        income: IncomeEntity,
        description: String,
        amountCents: Long,
        accountId: Long,
        frequency: IncomeFrequency,
        expectedDateMillis: Long
    ) {
        viewModelScope.launch {
            try {
                if (description.isBlank() || amountCents <= 0L || accountId == 0L) {
                    _userMessage.emit("Por favor completa los datos válidos del ingreso")
                    return@launch
                }
                val updated = income.copy(
                    description = description.trim(),
                    amountCents = amountCents,
                    accountId = accountId,
                    frequency = frequency,
                    expectedDateMillis = expectedDateMillis
                )
                incomeDao.updateIncome(updated)
                _userMessage.emit("Ingreso actualizado: ${updated.description}")
            } catch (e: Exception) {
                _userMessage.emit("Error al actualizar ingreso: ${e.message}")
            }
        }
    }

    fun confirmIncome(
        incomeId: Long,
        actualAmountCents: Long? = null,
        targetAccountId: Long? = null
    ) {
        viewModelScope.launch {
            try {
                confirmIncomeUseCase(incomeId, actualAmountCents, targetAccountId)
                _userMessage.emit("¡Ingreso acreditado a tu cuenta con éxito!")
            } catch (e: Exception) {
                _userMessage.emit("Error al acreditar ingreso: ${e.message}")
            }
        }
    }

    fun deleteIncome(income: IncomeEntity) {
        viewModelScope.launch {
            incomeDao.deleteIncome(income)
            _userMessage.emit("Ingreso eliminado")
        }
    }
}
