package com.personalfinance.cuentas.presentation.dashboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalfinance.cuentas.core.security.SecurityLifecycleObserver
import com.personalfinance.cuentas.core.security.SecurityPreferences
import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.DebtDao
import com.personalfinance.cuentas.data.local.dao.IncomeDao
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.BlockType
import com.personalfinance.cuentas.data.local.entities.BudgetEntity
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.entities.IncomeEntity
import com.personalfinance.cuentas.data.local.entities.IncomeFrequency
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity
import com.personalfinance.cuentas.data.local.relations.AllocationModelWithBlocks
import com.personalfinance.cuentas.domain.math.AllocationCalculator
import com.personalfinance.cuentas.domain.math.CashFlowProjectionSummary
import com.personalfinance.cuentas.domain.math.SafeToSpendCalculation
import com.personalfinance.cuentas.domain.math.SafeToSpendStatus
import com.personalfinance.cuentas.domain.schedule.DueDates
import com.personalfinance.cuentas.domain.usecases.AnchorTransactionUseCase
import com.personalfinance.cuentas.domain.usecases.InitializeDefaultAllocationModelUseCase
import com.personalfinance.cuentas.domain.usecases.RecordExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.personalfinance.cuentas.data.local.dao.CreditCardDao
import com.personalfinance.cuentas.domain.math.FinancialFormulas
import com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import java.time.ZoneId
import javax.inject.Inject

data class BlockAllocationUiModel(
    val block: AllocationBlockEntity,
    val allocatedCents: Long,
    val spentCents: Long,
    val budgetLimitCents: Long,
    val color: Color,
    val autoLoadedDetails: String = "",
    val isAutoCalculated: Boolean = false,
    val realSavingsCents: Long = 0L   // Solo bloque SAVINGS: dinero quieto real a fin de mes
)

data class EnvelopeDetailRow(
    val label: String,
    val value: String,
    val highlight: Boolean = false,
    val isNegative: Boolean = false
)

enum class EnvelopeStepStatus { INCOME, OK, SHORT }

data class EnvelopePlanStep(
    val dateLabel: String,
    val title: String,
    val amountLabel: String,
    val note: String,
    val status: EnvelopeStepStatus
)

data class EnvelopeDetailItem(
    val title: String,
    val subtitle: String,
    val amountCents: Long,
    val isSettled: Boolean
)

data class EnvelopeDetailUi(
    val blockId: Long,
    val blockName: String,
    val headline: String,
    val headlinePositive: Boolean,
    val rows: List<EnvelopeDetailRow>,
    val stepsTitle: String,
    val steps: List<EnvelopePlanStep>,
    val itemsTitle: String = "",
    val items: List<EnvelopeDetailItem> = emptyList()
)

data class FinancialCommitmentsSummary(
    val monthlyDebtCommitments: Long,
    val monthlyRecurringCommitments: Long,
    val monthlyCardCommitments: Long,
    val totalNeedsCommitments: Long,
    val monthlyFixedIncome: Long,
    val staticSavings: Long,
    val pendingIncomesCents: Long = 0L
)

data class PlannedMovementUi(
    val date: LocalDate,
    val dateLabel: String,
    val title: String,
    val amountCents: Long,
    val isIncome: Boolean,
    val balanceAfterCents: Long
)

data class ReserveItemUi(
    val title: String,
    val dateLabel: String,
    val amountCents: Long
)

/**
 * Dinero que debe quedar disponible para cubrir las obligaciones que vencen ANTES del próximo
 * ingreso. Todo lo que se cargue a futuro antes de que entre dinero entra en esta reserva.
 */
data class NextIncomeReserveUi(
    val reserveCents: Long,
    val nextIncomeDate: LocalDate,
    val nextIncomeLabel: String,
    val items: List<ReserveItemUi>
)

/**
 * Obligación de una tarjeta de crédito, ya calculada en el ViewModel para que la UI
 * solo la pinte (fuente única de verdad).
 */
data class CreditCardObligationUi(
    val cardId: Long,
    val displayName: String,
    val nextPaymentCents: Long,
    val paymentDueDay: Int,
    val countsInTotal: Boolean,
    val isThisMonth: Boolean = true   // true = vence en el mes actual, false = próximo mes
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val accountDao: AccountDao,
    private val allocationDao: AllocationDao,
    private val transactionDao: TransactionDao,
    private val incomeDao: IncomeDao,
    private val debtDao: DebtDao,
    private val recurringItemDao: RecurringItemDao,
    private val categoryDao: CategoryDao,
    private val creditCardDao: CreditCardDao,
    private val receivableDao: com.personalfinance.cuentas.data.local.dao.ReceivableDao,
    private val eventDao: com.personalfinance.cuentas.data.local.dao.EventDao,
    private val recordExpenseUseCase: RecordExpenseUseCase,
    private val anchorTransactionUseCase: AnchorTransactionUseCase,
    private val securityLifecycleObserver: SecurityLifecycleObserver,
    private val securityPreferences: SecurityPreferences,
    private val initializeDefaultAllocationModelUseCase: InitializeDefaultAllocationModelUseCase
) : ViewModel() {

    val isFlagSecureEnabled: StateFlow<Boolean> = securityPreferences.isFlagSecureEnabled

    init {
        viewModelScope.launch {
            initializeDefaultAllocationModelUseCase()
        }
    }

    // Saldo Total Consolidado (todas las cuentas incluidas)
    val consolidatedBalanceCents: StateFlow<Long?> = accountDao.getTotalConsolidatedBalanceCents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Saldo Disponible Líquido para Gastar (excluye dinero marcado como estático/ahorro protegido)
    val spendableBalanceCents: StateFlow<Long?> = accountDao.getSpendableBalanceCents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Dinero Estático / Ahorro Protegido acumulado
    val staticSavingsBalanceCents: StateFlow<Long?> = accountDao.getStaticSavingsBalanceCents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    // Saldo en tiempo real de Bancolombia (account_id = 1)
    private val bancolombiaBalanceCents: Flow<Long> =
        accountDao.getBalanceCents(accountId = 1L)

    // Cuota mensual de deudas activas (BancoBogota)
    private val bancoBogotaMonthlyCents: Flow<Long> =
        debtDao.getActiveDebts().map { debts ->
            debts.sumOf { it.monthlyInstallmentCents }
        }

    // Saldo pendiente ciclo tarjeta Rappi (card_id = 1, is_fully_paid = false)
    private val rappiCardPendingCents: Flow<Long> =
        creditCardDao.getPendingCycleCents(cardId = 1L)

    // Saldo pendiente ciclo tarjeta Nu (card_id = 2, is_fully_paid = false, incluye intereses diferidos)
    private val nuCardPendingCents: Flow<Long> =
        creditCardDao.getPendingCycleCents(cardId = 2L)

    // Suma de recurrentes activos (parqueadero moto, plan celular, etc.)
    private val activeRecurringTotal: Flow<Long> =
        recurringItemDao.getAllActiveRecurringItems().map { items ->
            items.sumOf { it.amountCents }
        }

    /**
     * Dinero realmente disponible para gastar hoy.
     * Formula: Bancolombia - cuota BancoBogota - saldo pendiente tarjeta Rappi
     *          - saldo pendiente tarjeta Nu - recurrentes activos
     *
     * Puede ser negativo si las obligaciones superan el saldo (se mostrara en rojo en la UI).
     */
    val realAvailableCents: StateFlow<Long> = combine(
        bancolombiaBalanceCents,
        bancoBogotaMonthlyCents,
        rappiCardPendingCents,
        nuCardPendingCents,
        activeRecurringTotal
    ) { bancolombia, bancoBogota, rappiCard, nuCard, recurring ->
        bancolombia - bancoBogota - rappiCard - nuCard - recurring
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0L
    )

    val accounts = accountDao.getAllActiveAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saldo Neto Retirable tras retención de 4x1000 en cuentas no exentas
    val totalNetAfterGmfCents: StateFlow<Long> = accounts.combine(consolidatedBalanceCents) { accountList, _ ->
        accountList.filter { it.includeInTotal }.sumOf { it.netBalanceAfterGmfCents }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Obligaciones fijas mensuales recurrentes activas (plan móvil, parqueadero, etc.)
    val recurringItems: StateFlow<List<RecurringItemEntity>> = recurringItemDao.getAllActiveRecurringItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Créditos y deudas activas
    val activeDebts: StateFlow<List<DebtEntity>> = debtDao.getActiveDebts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Categorías del sistema para registro de obligaciones
    val categories: StateFlow<List<CategoryEntity>> = categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Modelos de Asignación y Modelo Activo
    val allModels: StateFlow<List<AllocationModelWithBlocks>> = allocationDao.getAllModelsWithBlocks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeModel: StateFlow<AllocationModelWithBlocks?> = allocationDao.getActiveModelWithBlocks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val now = LocalDate.now()
    private val startOfMonth = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    private val endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    // Presupuestos fijados para este mes
    private val currentBudgets = allocationDao.getBudgetsForMonth(now.monthValue, now.year)

    // Resumen consolidado de compromisos cargados (deudas, obligaciones fijas, tarjetas e ingresos)
    val commitmentsSummary: StateFlow<FinancialCommitmentsSummary> = combine(
        debtDao.getActiveDebts(),
        recurringItems,
        creditCardDao.getAllCreditCardsWithAccount(),
        incomeDao.getAllIncomes(),
        staticSavingsBalanceCents
    ) { debts, recurrings, cards, incomes, staticSavings ->
        val today = LocalDate.now()
        // Solo obligaciones cuyo próximo vencimiento cae DENTRO DEL MES ACTUAL.
        // Las que vencen en diciembre o más adelante NO se suman aquí — aparecen en la
        // tarjeta "Reserva próximo mes" que está más arriba en el dashboard.
        val endOfThisMonth = today.withDayOfMonth(today.lengthOfMonth())

        val debtMonthly = debts.sumOf { debt ->
            val due = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today)
            if (!due.isAfter(endOfThisMonth)) debt.monthlyInstallmentCents else 0L
        }

        val recurringMonthly = recurrings.sumOf { rec ->
            val recDate = java.time.Instant.ofEpochMilli(rec.nextDueDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val due = if (!recDate.isBefore(today)) recDate
                      else DueDates.nextOccurrence(recDate.dayOfMonth, today)
            if (!due.isAfter(endOfThisMonth)) rec.amountCents else 0L
        }

        val cardMonthly = cards.sumOf { cardWithAcc ->
            val payment = cardNextPaymentCents(cardWithAcc, today)
            if (payment <= 0L) return@sumOf 0L
            val dueDay = cardWithAcc.card.paymentDueDay.coerceIn(1, 28)
            val candidate = today.withDayOfMonth(minOf(dueDay, today.lengthOfMonth()))
            val effectiveDue = if (!candidate.isBefore(today)) candidate
                               else today.plusMonths(1).let { nm ->
                                   nm.withDayOfMonth(minOf(dueDay, nm.lengthOfMonth()))
                               }
            if (!effectiveDue.isAfter(endOfThisMonth)) payment else 0L
        }

        val totalNeeds = debtMonthly + recurringMonthly + cardMonthly

        // "Por entrar": todos los ingresos programados que aún no se han recibido.
        // Se define así para que coincida con el gráfico de Flujo de Caja proyectado.
        val pendingIncomes = incomes.filter { !it.isReceived }.sumOf { it.amountCents }

        // Ingreso esperado del mes: lo ya recibido este mes + todo lo que aún está por entrar.
        // (No se limita al mes calendario para no perder la quincena que cae a fin/inicio de mes.)
        val receivedThisMonth = incomes.filter {
            it.isReceived && (it.receivedDateMillis ?: 0L) in startOfMonth..endOfMonth
        }.sumOf { it.amountCents }
        val fixedIncome = pendingIncomes + receivedThisMonth

        FinancialCommitmentsSummary(
            monthlyDebtCommitments = debtMonthly,
            monthlyRecurringCommitments = recurringMonthly,
            monthlyCardCommitments = cardMonthly,
            totalNeedsCommitments = totalNeeds,
            monthlyFixedIncome = fixedIncome,
            staticSavings = staticSavings ?: 0L,
            pendingIncomesCents = pendingIncomes
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinancialCommitmentsSummary(0L, 0L, 0L, 0L, 0L, 0L, 0L)
    )

    // Fuentes para el detalle ampliado de bolsillos (fechas de pago e ingresos por entrar)
    private data class PlanSources(
        val debts: List<DebtEntity>,
        val recurrings: List<RecurringItemEntity>,
        val incomes: List<IncomeEntity>,
        val cards: List<CreditCardWithAccount>
    )

    private val planSources: StateFlow<PlanSources> = combine(
        debtDao.getActiveDebts(),
        recurringItems,
        incomeDao.getAllIncomes(),
        creditCardDao.getAllCreditCardsWithAccount()
    ) { debts, recurrings, incomes, cards ->
        PlanSources(debts, recurrings, incomes, cards)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlanSources(emptyList(), emptyList(), emptyList(), emptyList())
    )
    /**
     * Saldo proyectado neto para este mes.
     * = saldo actual + ingresos pendientes de este mes (antes de fin de mes)
     *   - obligaciones que vencen este mes
     * 
     * Se usa en la HeroCard como "Libre hoy": lo que te queda
     * después de que entren tus pagos del mes y salgan tus obligaciones.
     */
    val currentMonthNetCents: StateFlow<Long> = combine(
        consolidatedBalanceCents,
        planSources,
        incomeDao.getAllIncomes()
    ) { balanceNullable, sources, allIncomes ->
        val balance = balanceNullable ?: 0L
        val today   = LocalDate.now()
        val endOfThisMonth = today.withDayOfMonth(today.lengthOfMonth())

        // Ingresos que entran antes de que termine el mes (no recibidos aún)
        val pendingIncome = allIncomes.filter { inc ->
            if (inc.isReceived) return@filter false
            val incDate = java.time.Instant.ofEpochMilli(inc.expectedDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val eff = if (inc.frequency != com.personalfinance.cuentas.data.local.entities.IncomeFrequency.ONE_TIME)
                DueDates.nextOccurrence(incDate.dayOfMonth, today)
            else incDate
            !eff.isBefore(today) && !eff.isAfter(endOfThisMonth)
        }.sumOf { it.amountCents }

        // Obligaciones que vencen este mes
        var obligations = 0L
        sources.debts.forEach { debt ->
            val due = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today)
            if (!due.isAfter(endOfThisMonth)) obligations += debt.monthlyInstallmentCents
        }
        sources.recurrings.forEach { rec ->
            val recDate = java.time.Instant.ofEpochMilli(rec.nextDueDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val due = if (!recDate.isBefore(today)) recDate
                      else DueDates.nextOccurrence(recDate.dayOfMonth, today)
            if (!due.isAfter(endOfThisMonth)) obligations += rec.amountCents
        }
        sources.cards.forEach { cwa ->
            val payment = cardNextPaymentCents(cwa, today)
            if (payment > 0L) {
                val dueDay = cwa.card.paymentDueDay.coerceIn(1, 28)
                val candidate = today.withDayOfMonth(minOf(dueDay, today.lengthOfMonth()))
                val effectiveDue = if (!candidate.isBefore(today)) candidate
                                   else today.plusMonths(1).let { nm ->
                                       nm.withDayOfMonth(minOf(dueDay, nm.lengthOfMonth()))
                                   }
                if (!effectiveDue.isAfter(endOfThisMonth)) obligations += payment
            }
        }

        balance + pendingIncome - obligations
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Alias: obligaciones pendientes este mes (para mostrar en la HeroCard como detalle)
    val currentMonthPendingCents: StateFlow<Long> = combine(
        planSources,
        incomeDao.getAllIncomes()
    ) { sources, allIncomes ->
        val today = LocalDate.now()
        val endOfThisMonth = today.withDayOfMonth(today.lengthOfMonth())
        var obligations = 0L
        sources.debts.forEach { debt ->
            val due = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today)
            if (!due.isAfter(endOfThisMonth)) obligations += debt.monthlyInstallmentCents
        }
        sources.recurrings.forEach { rec ->
            val recDate = java.time.Instant.ofEpochMilli(rec.nextDueDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val due = if (!recDate.isBefore(today)) recDate
                      else DueDates.nextOccurrence(recDate.dayOfMonth, today)
            if (!due.isAfter(endOfThisMonth)) obligations += rec.amountCents
        }
        sources.cards.forEach { cwa ->
            val payment = cardNextPaymentCents(cwa, today)
            if (payment > 0L) {
                val dueDay = cwa.card.paymentDueDay.coerceIn(1, 28)
                val candidate = today.withDayOfMonth(minOf(dueDay, today.lengthOfMonth()))
                val effectiveDue = if (!candidate.isBefore(today)) candidate
                                   else today.plusMonths(1).let { nm ->
                                       nm.withDayOfMonth(minOf(dueDay, nm.lengthOfMonth()))
                                   }
                if (!effectiveDue.isAfter(endOfThisMonth)) obligations += payment
            }
        }
        obligations
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Detalle ampliado por bolsillo: cálculos, fechas de pago y plan de apartado
    val envelopeDetails: StateFlow<Map<Long, EnvelopeDetailUi>> = combine(
        activeModel,
        commitmentsSummary,
        planSources,
        spendableBalanceCents,
        transactionDao.getAllTransactions()
    ) { model, commitments, sources, cash, transactions ->
        buildEnvelopeDetails(model, commitments, sources, cash ?: 0L, transactions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Reserva dinámica para el próximo mes: obligaciones que vencen ANTES de que entre el próximo
    // ingreso. Se recalcula con todo lo que se vaya a cargar a futuro (deudas, fijos y tarjetas).
    val nextIncomeReserve: StateFlow<NextIncomeReserveUi?> = planSources.map { sources ->
        val today = LocalDate.now()
        val horizonDate = computeReserveHorizonDate(sources.incomes, today)
        if (horizonDate == null || !horizonDate.isAfter(today)) return@map null

        val dueBeforeIncome = collectPayments(sources, includeCards = true)
            .filter { !it.date.isBefore(today) && it.date.isBefore(horizonDate) }
            .sortedBy { it.date }

        NextIncomeReserveUi(
            reserveCents = dueBeforeIncome.sumOf { it.amountCents },
            nextIncomeDate = horizonDate,
            nextIncomeLabel = horizonDate.format(envelopeDateFormatter),
            items = dueBeforeIncome.map {
                ReserveItemUi(it.title, it.date.format(envelopeDateFormatter), it.amountCents)
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * Dinero quieto real: saldo actual menos lo que falta pagar este mes
     * y menos lo que vence en diciembre ANTES del primer ingreso de diciembre.
     * NO incluye ingresos futuros — solo el saldo real de hoy.
     *
     * Formula:
     *   saldo_actual
     *   - obligaciones_pendientes_noviembre
     *   - obligaciones_1_al_14_dic (antes del primer ingreso de diciembre)
     */
    val realSavingsCents: StateFlow<Long> = combine(
        consolidatedBalanceCents,
        planSources,
        incomeDao.getAllIncomes()
    ) { balanceNullable, sources, allIncomes ->
        val balance = balanceNullable ?: 0L
        // Guarda: si los datos aun no cargaron (planSources vacio y balance==0L),
        // devolvemos 0 para no mostrar un valor incorrecto en la UI durante la inicializacion.
        if (sources.debts.isEmpty() && sources.cards.isEmpty() && sources.recurrings.isEmpty() && allIncomes.isEmpty()) return@combine 0L
        val today = LocalDate.now()
        val endOfThisMonth = today.withDayOfMonth(today.lengthOfMonth())

        // 0. Ingresos del mes actual que aun no han llegado
        // Se incluyen porque forman parte del flujo de noviembre:
        // saldo + estos ingresos = el total con que se van a cubrir las obligaciones.
        val pendingIncomeThisMonth = allIncomes.filter { inc ->
            if (inc.isReceived) return@filter false
            val incDate = java.time.Instant.ofEpochMilli(inc.expectedDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val eff = if (inc.frequency != com.personalfinance.cuentas.data.local.entities.IncomeFrequency.ONE_TIME)
                DueDates.nextOccurrence(incDate.dayOfMonth, today)
            else incDate
            !eff.isBefore(today) && !eff.isAfter(endOfThisMonth)
        }.sumOf { it.amountCents }

        // 1. Obligaciones pendientes de ESTE mes
        var pendingThisMonth = 0L
        sources.debts.forEach { debt ->
            val due = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today)
            if (!due.isBefore(today) && !due.isAfter(endOfThisMonth)) pendingThisMonth += debt.monthlyInstallmentCents
        }
        sources.recurrings.forEach { rec ->
            val recDate = java.time.Instant.ofEpochMilli(rec.nextDueDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val due = if (!recDate.isBefore(today)) recDate
                      else DueDates.nextOccurrence(recDate.dayOfMonth, today)
            if (!due.isBefore(today) && !due.isAfter(endOfThisMonth)) pendingThisMonth += rec.amountCents
        }
        sources.cards.forEach { cwa ->
            val payment = cardNextPaymentCents(cwa, today)
            if (payment > 0L) {
                val dueDay = cwa.card.paymentDueDay.coerceIn(1, 28)
                val candidate = today.withDayOfMonth(minOf(dueDay, today.lengthOfMonth()))
                val effectiveDue = if (!candidate.isBefore(today)) candidate
                                   else today.plusMonths(1).let { nm ->
                                       nm.withDayOfMonth(minOf(dueDay, nm.lengthOfMonth()))
                                   }
                if (!effectiveDue.isBefore(today) && !effectiveDue.isAfter(endOfThisMonth)) pendingThisMonth += payment
            }
        }

        // 2. Primer ingreso de diciembre para saber hasta donde descontar
        val nextMonthStart = today.plusMonths(1).withDayOfMonth(1)
        val firstNextIncome = allIncomes.mapNotNull { inc ->
            val incDate = java.time.Instant.ofEpochMilli(inc.expectedDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val eff = if (inc.frequency != com.personalfinance.cuentas.data.local.entities.IncomeFrequency.ONE_TIME)
                DueDates.nextOccurrence(incDate.dayOfMonth, nextMonthStart)
            else incDate
            if (!eff.isBefore(nextMonthStart)) eff else null
        }.minOrNull()

        // 3. Obligaciones del 1 al dia_antes_del_primer_ingreso de diciembre
        var preIncomeNextMonth = 0L
        if (firstNextIncome != null) {
            sources.debts.forEach { debt ->
                val due = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, nextMonthStart)
                if (!due.isBefore(nextMonthStart) && due.isBefore(firstNextIncome))
                    preIncomeNextMonth += debt.monthlyInstallmentCents
            }
            sources.recurrings.forEach { rec ->
                val recDate = java.time.Instant.ofEpochMilli(rec.nextDueDateMillis)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val due = DueDates.nextOccurrence(recDate.dayOfMonth, nextMonthStart)
                if (!due.isBefore(nextMonthStart) && due.isBefore(firstNextIncome))
                    preIncomeNextMonth += rec.amountCents
            }
            sources.cards.forEach { cwa ->
                val payment = cardNextPaymentCents(cwa, nextMonthStart)
                if (payment > 0L) {
                    val dueDay = cwa.card.paymentDueDay.coerceIn(1, 28)
                    val due = nextMonthStart.withDayOfMonth(minOf(dueDay, nextMonthStart.lengthOfMonth()))
                    if (!due.isBefore(nextMonthStart) && due.isBefore(firstNextIncome))
                        preIncomeNextMonth += payment
                }
            }
        }

        // saldo hoy - obligaciones nov pendientes - obligaciones 1..14 dic
        balance + pendingIncomeThisMonth - pendingThisMonth - preIncomeNextMonth
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Obligaciones de tarjetas de crédito ya calculadas (fuente única para la UI y los totales).
    val creditCardObligations: StateFlow<List<CreditCardObligationUi>> = planSources.map { sources ->
        val today = LocalDate.now()
        sources.cards.map { cardWithAcc ->
            val payment = cardNextPaymentCents(cardWithAcc, today)
            val dueDay = cardWithAcc.card.paymentDueDay.coerceIn(1, 28)
            val endOfThisMonth = today.withDayOfMonth(today.lengthOfMonth())
            val candidateDue = today.withDayOfMonth(minOf(dueDay, today.lengthOfMonth()))
            val effectiveDue = if (!candidateDue.isBefore(today)) candidateDue
                               else today.plusMonths(1).let { nm -> nm.withDayOfMonth(minOf(dueDay, nm.lengthOfMonth())) }
            CreditCardObligationUi(
                cardId = cardWithAcc.card.id,
                displayName = cardWithAcc.card.lastFourDigits
                    ?.takeIf { it.isNotBlank() }
                    ?.let { "${cardWithAcc.account.name} ••$it" }
                    ?: cardWithAcc.account.name,
                nextPaymentCents = payment,
                paymentDueDay = cardWithAcc.card.paymentDueDay,
                countsInTotal = payment > 0L,
                isThisMonth = !effectiveDue.isAfter(endOfThisMonth)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total pendiente por cobrar ("me deben"): suma de porciones no saldadas de terceros.
    val receivablesTotalCents: StateFlow<Long> = receivableDao.getPendingReceivables()
        .map { list -> list.sumOf { it.owedCents } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Deuda total de tarjetas (cupo usado = capital pendiente). Base para la deuda neta.
    val totalCardDebtCents: StateFlow<Long> = planSources.map { sources ->
        val today = LocalDate.now()
        sources.cards.sumOf { cwa ->
            FinancialFormulas.calculateCardBilling(cwa.card, cwa.purchases, today).totalUsedCents
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    /**
     * Eventos activos con ahorro mensual, agrupados por blockType para mostrar
     * justo debajo del bolsillo correspondiente en el dashboard.
     * Clave = "NEEDS" | "WANTS" | "SAVINGS" | null
     */
    val eventsByBlock: StateFlow<Map<String, List<com.personalfinance.cuentas.presentation.events.EventUi>>> =
        eventDao.getAllActiveEvents()
            .map { list ->
                list
                    .filter { ev -> ev.blockType != null && ev.totalAmountCents != null && ev.totalAmountCents > 0L }
                    .map { ev -> com.personalfinance.cuentas.presentation.events.EventsViewModel.toUi(ev) }
                    .filter { ui -> (ui.monthlySaving ?: 0L) > 0L }
                    .groupBy { ui -> ui.entity.blockType ?: "NEEDS" }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private data class PlannedPayment(
        val date: LocalDate,
        val title: String,
        val amountCents: Long
    )

    private data class EnvelopeEvent(
        val date: LocalDate,
        val isIncome: Boolean,
        val title: String,
        val amountCents: Long
    )

    private val envelopeDateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMM", Locale("es", "CO"))

    /**
     * Monto que se pagará en el próximo vencimiento de la tarjeta.
     * Delega en [FinancialFormulas.calculateCardNextPaymentCents] para tener UNA sola fuente
     * de verdad compartida con la reserva, la proyección de caja y la línea de tiempo.
     */
    private fun cardNextPaymentCents(cardWithAcc: CreditCardWithAccount, today: LocalDate): Long {
        return FinancialFormulas.calculateCardNextPaymentCents(
            card = cardWithAcc.card,
            purchases = cardWithAcc.purchases,
            today = today
        )
    }

    /**
     * Pagos de tarjeta proyectados en los próximos [occurrences] vencimientos mensuales.
     * Se usa para que la proyección de caja y "Libre hoy" NO ignoren los pagos de tarjeta.
     *
     * Cada mes se calcula con el cronograma real de la tarjeta (cuotas de diferidos que aún
     * quedan + cuota de manejo), de modo que el monto varía mes a mes y no se repite igual.
     */
    private fun cardPaymentsForHorizon(
        cards: List<CreditCardWithAccount>,
        today: LocalDate,
        occurrences: Int
    ): List<PlannedPayment> {
        val payments = mutableListOf<PlannedPayment>()
        cards.forEach { cardWithAcc ->
            val schedule = FinancialFormulas.calculateCardPaymentsSchedule(
                card = cardWithAcc.card,
                purchases = cardWithAcc.purchases,
                months = occurrences,
                today = today
            )
            if (schedule.all { it <= 0L }) return@forEach

            val digits = cardWithAcc.card.lastFourDigits
            val title = if (digits.isNullOrBlank()) "Tarjeta de crédito"
            else "Tarjeta ••$digits"
            val firstDue = nextOccurrence(cardWithAcc.card.paymentDueDay, today)
            schedule.forEachIndexed { index, amount ->
                if (amount > 0L) {
                    val date = DueDates.shiftMonths(firstDue, index.toLong())
                    if (!date.isBefore(today)) {
                        payments.add(PlannedPayment(date, title, amount))
                    }
                }
            }
        }
        return payments
    }

    private fun buildEnvelopeDetails(
        model: AllocationModelWithBlocks?,
        commitments: FinancialCommitmentsSummary,
        sources: PlanSources,
        currentCashCents: Long,
        monthTransactions: List<com.personalfinance.cuentas.data.local.entities.TransactionEntity>
    ): Map<Long, EnvelopeDetailUi> {
        if (model == null) return emptyMap()

        val pendingIncomes = sources.incomes
            .filter { !it.isReceived }
            .sortedBy { it.expectedDateMillis }

        // Montos ya movidos este mes (para marcar obligaciones ya saldadas en el desglose).
        val settledAmounts = monthTransactions
            .filter { it.dateMillis in startOfMonth..endOfMonth }
            .map { kotlin.math.abs(it.amountCents) }
            .toSet()

        // Solo obligaciones de este mes para el detalle del bolsillo.
        // Las de diciembre o más adelante aparecen en la tarjeta de reserva, no aquí.
        val thisMonthEnd = LocalDate.now().let { it.withDayOfMonth(it.lengthOfMonth()) }
        val allPaymentsWithCards    = collectPayments(sources, includeCards = true).filter  { !it.date.isAfter(thisMonthEnd) }
        val allPaymentsWithoutCards = collectPayments(sources, includeCards = false).filter { !it.date.isAfter(thisMonthEnd) }

        return model.blocks.associate { block ->
            block.id to when (block.type) {
                BlockType.NEEDS -> buildCommitmentsDetail(
                    block = block,
                    payments = allPaymentsWithCards,
                    currentCashCents = currentCashCents,
                    pendingIncomes = pendingIncomes,
                    fallbackTotalCents = commitments.totalNeedsCommitments,
                    settledAmounts = settledAmounts
                )
                BlockType.DEBTS -> buildCommitmentsDetail(
                    block = block,
                    payments = allPaymentsWithoutCards,
                    currentCashCents = currentCashCents,
                    pendingIncomes = pendingIncomes,
                    fallbackTotalCents = commitments.monthlyDebtCommitments,
                    settledAmounts = settledAmounts
                )
                else -> buildPercentageDetail(block, commitments, pendingIncomes)
            }
        }
    }

    private fun collectPayments(
        sources: PlanSources,
        includeCards: Boolean
    ): List<PlannedPayment> {
        val today = LocalDate.now()
        val payments = mutableListOf<PlannedPayment>()

        sources.debts.forEach { debt ->
            payments.add(
                PlannedPayment(
                    date = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today),
                    title = debt.name,
                    amountCents = debt.monthlyInstallmentCents
                )
            )
        }

        sources.recurrings.forEach { rec ->
            val recDate = Instant.ofEpochMilli(rec.nextDueDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val date = if (!recDate.isBefore(today)) recDate
            else nextOccurrence(recDate.dayOfMonth, today)
            payments.add(
                PlannedPayment(
                    date = date,
                    title = rec.title,
                    amountCents = rec.amountCents
                )
            )
        }

        if (includeCards) {
            sources.cards.forEach { cardWithAcc ->
                val monthly = cardNextPaymentCents(cardWithAcc, today)
                if (monthly > 0L) {
                    val digits = cardWithAcc.card.lastFourDigits
                    payments.add(
                        PlannedPayment(
                            date = nextOccurrence(cardWithAcc.card.paymentDueDay, today),
                            title = if (digits.isNullOrBlank()) "Tarjeta de crédito" else "Tarjeta ••$digits",
                            amountCents = monthly
                        )
                    )
                }
            }
        }

        return payments.sortedBy { it.date }
    }

    /**
     * Dado un día del mes, devuelve la próxima fecha en que cae a partir de hoy.
     * Si el día ya pasó este mes, salta al siguiente (respeta el día actual).
     */
    private fun nextOccurrence(dayOfMonth: Int, today: LocalDate): LocalDate {
        val d = dayOfMonth.coerceIn(1, 31)
        val thisMonthDay = d.coerceAtMost(today.lengthOfMonth())
        return if (today.dayOfMonth <= thisMonthDay) {
            today.withDayOfMonth(thisMonthDay)
        } else {
            val next = today.plusMonths(1)
            next.withDayOfMonth(d.coerceAtMost(next.lengthOfMonth()))
        }
    }

    /**
     * Calcula la fecha horizonte para la reserva.
     * Si el próximo ingreso es muy pronto (dentro de 2 días), busca el siguiente ingreso
     * para que la reserva cubra el ciclo que está a punto de empezar.
     */
    private fun computeReserveHorizonDate(incomes: List<IncomeEntity>, today: LocalDate): LocalDate? {
        val upcomingDates = incomes.mapNotNull { inc ->
            val incDate = DueDates.toLocalDate(inc.expectedDateMillis)
            when {
                !inc.isReceived ->
                    if (!incDate.isBefore(today)) incDate
                    else nextOccurrence(incDate.dayOfMonth, today.plusDays(1))
                inc.frequency != IncomeFrequency.ONE_TIME ->
                    nextOccurrence(incDate.dayOfMonth, today.plusDays(1))
                else -> null
            }
        }.distinct().sorted()

        if (upcomingDates.isEmpty()) return null

        val firstIncome = upcomingDates.first()

        // Si el ingreso es hoy, mañana o pasado mañana, y hay otro ingreso después,
        // saltamos al siguiente para cubrir las obligaciones del ciclo que entra.
        return if (firstIncome.isBefore(today.plusDays(3)) && upcomingDates.size > 1) {
            upcomingDates[1]
        } else {
            firstIncome
        }
    }

    private fun buildCommitmentsDetail(
        block: AllocationBlockEntity,
        payments: List<PlannedPayment>,
        currentCashCents: Long,
        pendingIncomes: List<IncomeEntity>,
        fallbackTotalCents: Long,
        settledAmounts: Set<Long>
    ): EnvelopeDetailUi {
        val today = LocalDate.now()

        val paymentsTotalRaw = payments.sumOf { it.amountCents }
        val paymentsTotal = if (paymentsTotalRaw > 0L) paymentsTotalRaw else fallbackTotalCents

        // Horizonte: hasta el último pago programado a partir de hoy
        val horizonEnd = payments.maxOfOrNull { it.date }
            ?: today.withDayOfMonth(today.lengthOfMonth())

        // Ingresos por entrar que llegan a tiempo (antes del último pago programado)
        val relevantIncomes = pendingIncomes.filter {
            !Instant.ofEpochMilli(it.expectedDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate().isAfter(horizonEnd)
        }
        val pendingInTime = relevantIncomes.sumOf { it.amountCents }
        val availableTotal = currentCashCents + pendingInTime
        val surplus = availableTotal - paymentsTotal

        // Orden cronológico: primero ingresos, luego pagos del mismo día
        val events = mutableListOf<EnvelopeEvent>()
        relevantIncomes.forEach { inc ->
            events.add(
                EnvelopeEvent(
                    date = Instant.ofEpochMilli(inc.expectedDateMillis)
                        .atZone(ZoneId.systemDefault()).toLocalDate(),
                    isIncome = true,
                    title = inc.description,
                    amountCents = inc.amountCents
                )
            )
        }
        payments.forEach { p ->
            events.add(EnvelopeEvent(p.date, isIncome = false, title = p.title, amountCents = p.amountCents))
        }
        val ordered = events.sortedWith(compareBy({ it.date }, { if (it.isIncome) 0 else 1 }))

        var running = currentCashCents
        var worstRunning = currentCashCents
        var firstShortDate: LocalDate? = null
        val steps = mutableListOf<EnvelopePlanStep>()

        ordered.forEach { ev ->
            if (ev.isIncome) {
                running += ev.amountCents
                steps.add(
                    EnvelopePlanStep(
                        dateLabel = ev.date.format(envelopeDateFormatter),
                        title = "Entra ${ev.title}",
                        amountLabel = "+${CurrencyFormatter.formatCentsToCop(ev.amountCents)}",
                        note = "",
                        status = EnvelopeStepStatus.INCOME
                    )
                )
            } else {
                running -= ev.amountCents
                val covered = running >= 0L
                if (!covered && firstShortDate == null) firstShortDate = ev.date
                steps.add(
                    EnvelopePlanStep(
                        dateLabel = ev.date.format(envelopeDateFormatter),
                        title = ev.title,
                        amountLabel = "-${CurrencyFormatter.formatCentsToCop(ev.amountCents)}",
                        note = if (covered) "Cubierto" else "No alcanza",
                        status = if (covered) EnvelopeStepStatus.OK else EnvelopeStepStatus.SHORT
                    )
                )
            }
            if (running < worstRunning) worstRunning = running
        }

        val headline: String
        val headlinePositive: Boolean
        when {
            paymentsTotal <= 0L -> {
                headline = "Sin pagos programados"
                headlinePositive = true
            }
            firstShortDate != null -> {
                headline = "No alcanza desde el ${firstShortDate.format(envelopeDateFormatter)}: faltan ${CurrencyFormatter.formatCentsToCop(-worstRunning)}"
                headlinePositive = false
            }
            else -> {
                headline = "Alcanza para cubrir tus próximas obligaciones"
                headlinePositive = true
            }
        }

        // Desglose itemizado: cada obligación que compone el total, marcando las ya saldadas.
        val items = payments.map { p ->
            EnvelopeDetailItem(
                title = p.title,
                subtitle = p.date.format(envelopeDateFormatter),
                amountCents = p.amountCents,
                isSettled = settledAmounts.contains(p.amountCents)
            )
        }
        val settledTotal = items.filter { it.isSettled }.sumOf { it.amountCents }
        val pendingTotal = items.filter { !it.isSettled }.sumOf { it.amountCents }

        val rows = listOf(
            EnvelopeDetailRow("Total obligaciones próximas", CurrencyFormatter.formatCentsToCop(paymentsTotal), highlight = true),
            EnvelopeDetailRow("Ya pagado este mes", CurrencyFormatter.formatCentsToCop(settledTotal)),
            EnvelopeDetailRow("Pendiente por cubrir", CurrencyFormatter.formatCentsToCop(pendingTotal), highlight = true),
            EnvelopeDetailRow("Disponible hoy", CurrencyFormatter.formatCentsToCop(currentCashCents)),
            EnvelopeDetailRow("Ingresos que llegan a tiempo", CurrencyFormatter.formatCentsToCop(pendingInTime)),
            EnvelopeDetailRow("Total disponible", CurrencyFormatter.formatCentsToCop(availableTotal)),
            EnvelopeDetailRow(
                label = "Sobrante tras pagar",
                value = CurrencyFormatter.formatCentsToCop(surplus),
                highlight = true,
                isNegative = surplus < 0L
            )
        )

        return EnvelopeDetailUi(
            blockId = block.id,
            blockName = block.name,
            headline = headline,
            headlinePositive = headlinePositive,
            rows = rows,
            stepsTitle = if (payments.isEmpty()) "Movimientos" else "Pagos próximos y fechas",
            steps = steps,
            itemsTitle = "Composición de las obligaciones",
            items = items
        )
    }

    private fun buildPercentageDetail(
        block: AllocationBlockEntity,
        commitments: FinancialCommitmentsSummary,
        pendingIncomes: List<IncomeEntity>
    ): EnvelopeDetailUi {
        val pct = block.percentage.coerceAtLeast(0)
        val pendingTotal = pendingIncomes.sumOf { it.amountCents }
        val monthTotal = commitments.monthlyFixedIncome
        val received = (monthTotal - pendingTotal).coerceAtLeast(0L)

        val fixedToSetAside = received * pct / 100L
        val projectedTotal = monthTotal * pct / 100L

        val steps = pendingIncomes.sortedBy { it.expectedDateMillis }.map { inc ->
            val date = Instant.ofEpochMilli(inc.expectedDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            EnvelopePlanStep(
                dateLabel = date.format(envelopeDateFormatter),
                title = "Entra ${inc.description}",
                amountLabel = "+${CurrencyFormatter.formatCentsToCop(inc.amountCents)}",
                note = "Aparta ${CurrencyFormatter.formatCentsToCop(inc.amountCents * pct / 100L)}",
                status = EnvelopeStepStatus.INCOME
            )
        }

        return EnvelopeDetailUi(
            blockId = block.id,
            blockName = block.name,
            headline = if (received > 0L) {
                "Aparta ${CurrencyFormatter.formatCentsToCop(fixedToSetAside)} ($pct% de lo recibido)"
            } else {
                "Aún no registras ingresos recibidos este mes"
            },
            headlinePositive = received > 0L,
            rows = listOf(
                EnvelopeDetailRow("Ingresos recibidos este mes", CurrencyFormatter.formatCentsToCop(received)),
                EnvelopeDetailRow("Ingresos por entrar", CurrencyFormatter.formatCentsToCop(pendingTotal)),
                EnvelopeDetailRow("Total previsto del mes", CurrencyFormatter.formatCentsToCop(monthTotal)),
                EnvelopeDetailRow("Porcentaje del modelo", "$pct%"),
                EnvelopeDetailRow("Monto fijo a apartar", CurrencyFormatter.formatCentsToCop(fixedToSetAside), highlight = true),
                EnvelopeDetailRow("Si entra todo el mes", CurrencyFormatter.formatCentsToCop(projectedTotal))
            ),
            stepsTitle = "Cuándo y cuánto apartar",
            steps = steps
        )
    }


    // Bloques y Sobres UI (Proyectado: vs presupuesto total del mes)
    val projectedBlockEnvelopes: StateFlow<List<BlockAllocationUiModel>> = combine(
        activeModel,
        currentBudgets,
        transactionDao.getAllTransactions(),
        commitmentsSummary,
        receivableDao.getAllReceivables()
    ) { modelWithBlocks, budgets, transactions, commitments, receivables ->
        val envelopes = calculateEnvelopes(modelWithBlocks, budgets, transactions, commitments, receivables, isProjected = true)
        val realSav = realSavingsCents.value
        envelopes.map { env ->
            if (env.block.type == BlockType.SAVINGS) env.copy(realSavingsCents = realSav) else env
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bloques y Sobres UI (Actual: vs lo que se ha recibido hasta hoy)
    val actualBlockEnvelopes: StateFlow<List<BlockAllocationUiModel>> = combine(
        activeModel,
        currentBudgets,
        transactionDao.getAllTransactions(),
        commitmentsSummary,
        receivableDao.getAllReceivables()
    ) { modelWithBlocks, budgets, transactions, commitments, receivables ->
        val envelopes = calculateEnvelopes(modelWithBlocks, budgets, transactions, commitments, receivables, isProjected = false)
        val realSav = realSavingsCents.value
        envelopes.map { env ->
            if (env.block.type == BlockType.SAVINGS) env.copy(realSavingsCents = realSav) else env
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Alias para mantener compatibilidad si es necesario, aunque lo ideal es usar los nuevos
    val blockEnvelopes: StateFlow<List<BlockAllocationUiModel>> = actualBlockEnvelopes

    private fun calculateEnvelopes(
        modelWithBlocks: AllocationModelWithBlocks?,
        budgets: List<BudgetEntity>,
        transactions: List<com.personalfinance.cuentas.data.local.entities.TransactionEntity>,
        commitments: FinancialCommitmentsSummary,
        receivables: List<com.personalfinance.cuentas.data.local.entities.ReceivableEntity>,
        isProjected: Boolean
    ): List<BlockAllocationUiModel> {
        if (modelWithBlocks == null) return emptyList()

        // El ingreso base para sugerencias de Ocio y Ahorro siempre debe ser el proyectado del mes
        // para que el usuario sepa cuánto debe apartar/gastar en total, incluso si aún no recibe todo.
        val baseIncome = commitments.monthlyFixedIncome
        val receivedIncome = (commitments.monthlyFixedIncome - commitments.pendingIncomesCents).coerceAtLeast(0L)

        // Lógica dinámica: Ahorro y Ocio se reparten lo que sobra tras cubrir Obligaciones (Needs)
        // mediante un reparto flexible 60/40 del sobrante (60% Ocio, 40% Ahorro).
        // NOTA: esto NO equivale a 30/20 del total salvo que Obligaciones sea exactamente el 50%
        // del ingreso; por eso las etiquetas de la UI hablan de "residual", no de "30/20".
        val totalProjectedNeeds = commitments.totalNeedsCommitments
        val remainingIncome = (baseIncome - totalProjectedNeeds).coerceAtLeast(0L)
        
        val dynamicWantsLimit = (remainingIncome * 0.6).toLong()
        val dynamicSavingsLimit = (remainingIncome * 0.4).toLong()

        // Índice de porciones gastadas por terceros ("me deben")
        val sharedByTransaction: Map<Long, Long> = receivables
            .filter { it.sourceTransactionId != null }
            .groupBy { it.sourceTransactionId!! }
            .mapValues { (_, list) -> list.sumOf { it.owedCents } }

        return modelWithBlocks.blocks.map { block ->
            val budget = budgets.firstOrNull { it.blockId == block.id }
            
            // Filtramos transacciones del bloque en el mes actual
            val blockTransactions = transactions
                .filter { it.blockId == block.id && it.dateMillis in startOfMonth..endOfMonth }
            
            // Si el residual de ahorro es negativo, se informa como déficit (no se oculta).
            var savingsDeficit = 0L

            // Si una transacción de cualquier bloque está marcada como 'isLeisure', 
            // se descuenta de su bloque original y se suma al gasto de 'WANTS' (Ocio).
            // Para NEEDS: "pagado" = gastos EXPENSE del bloque en el mes actual
            val needsTransactionsThisMonth = transactions.filter {
                it.blockId == block.id &&
                it.dateMillis in startOfMonth..endOfMonth &&
                it.type == com.personalfinance.cuentas.data.local.entities.TransactionType.EXPENSE
            }.sumOf { tx -> tx.amountCents - (sharedByTransaction[tx.id] ?: 0L) }
                .coerceAtLeast(0L)

            val spent = when (block.type) {
                BlockType.NEEDS, BlockType.DEBTS -> {
                    // "Pagado" = total de gastos de necesidades reales del mes (solo EXPENSE)
                    needsTransactionsThisMonth
                }
                BlockType.WANTS -> {
                    // Todo lo del bloque WANTS + lo marcado como leisure en otros bloques
                    val inBlock = blockTransactions.sumOf { tx -> tx.amountCents - (sharedByTransaction[tx.id] ?: 0L) }
                    val extraLeisure = transactions.filter { 
                        it.blockId != block.id && it.isLeisure && it.dateMillis in startOfMonth..endOfMonth 
                    }.sumOf { tx -> tx.amountCents - (sharedByTransaction[tx.id] ?: 0L) }
                    (inBlock + extraLeisure).coerceAtLeast(0L)
                }
                BlockType.SAVINGS -> {
                    // Ahorro residual: Ingresos recibidos - (Gastos Obligatorios + Gastos Ocio)
                    val needsBlock = modelWithBlocks.blocks.find { it.type == BlockType.NEEDS }
                    val wantsBlock = modelWithBlocks.blocks.find { it.type == BlockType.WANTS }
                    
                    val needsSpent = transactions.filter { 
                        it.blockId == needsBlock?.id && it.dateMillis in startOfMonth..endOfMonth && !it.isLeisure 
                    }.sumOf { tx -> tx.amountCents - (sharedByTransaction[tx.id] ?: 0L) }
                    
                    val wantsSpent = transactions.filter { 
                        (it.blockId == wantsBlock?.id || it.isLeisure) && it.dateMillis in startOfMonth..endOfMonth 
                    }.sumOf { tx -> tx.amountCents - (sharedByTransaction[tx.id] ?: 0L) }

                    // El ahorro real es lo que queda de lo recibido tras pagar obligaciones y ocio.
                    val residual = receivedIncome - needsSpent - wantsSpent
                    if (residual < 0L) savingsDeficit = -residual
                    residual.coerceAtLeast(0L)
                }
                else -> {
                    // Todo lo del bloque que NO esté marcado como leisure
                    blockTransactions.filter { !it.isLeisure }
                        .sumOf { tx -> tx.amountCents - (sharedByTransaction[tx.id] ?: 0L) }
                        .coerceAtLeast(0L)
                }
            }

            val parsedColor = try {
                Color(android.graphics.Color.parseColor(block.colorHex))
            } catch (e: Exception) {
                Color(0xFF2E7D32)
            }

            var autoAmount = 0L
            var autoDetails = ""
            var isAuto = false

            when (block.type) {
                BlockType.NEEDS, BlockType.DEBTS -> {
                    // allocatedCents = total de obligaciones del mes (pagadas + pendientes).
                    // Se calcula sumando los gastos reales de NEEDS del mes (lo que ya salió)
                    // más las obligaciones que aún no tienen transacción (pendientes).
                    // Esto evita que "Total mes" sea menor que "Pagado este mes".
                    // Simple: usamos directamente lo que el usuario ha gastado en NEEDS este mes
                    // más las obligaciones aún pendientes.
                    val needsPaidThisMonth = transactions
                        .filter {
                            it.blockId == block.id &&
                            it.dateMillis in startOfMonth..endOfMonth &&
                            it.type == com.personalfinance.cuentas.data.local.entities.TransactionType.EXPENSE
                        }
                        .sumOf { tx -> tx.amountCents - (sharedByTransaction[tx.id] ?: 0L) }
                        .coerceAtLeast(0L)
                    // Total = lo ya pagado + lo que queda pendiente del mes actual
                    autoAmount = needsPaidThisMonth + commitments.totalNeedsCommitments
                    isAuto = true
                    autoDetails = "Pagado: ${CurrencyFormatter.formatCentsToCop(needsPaidThisMonth)} | Pendiente: ${CurrencyFormatter.formatCentsToCop(commitments.totalNeedsCommitments)}"
                }
                BlockType.SAVINGS -> {
                    autoAmount = dynamicSavingsLimit
                    isAuto = true
                    autoDetails = if (savingsDeficit > 0L)
                        "Déficit del mes: -${CurrencyFormatter.formatCentsToCop(savingsDeficit)} (gastaste más de lo recibido)"
                    else "Ahorro total esperado: ${CurrencyFormatter.formatCentsToCop(autoAmount)} (40% residual)"
                }
                BlockType.WANTS -> {
                    autoAmount = dynamicWantsLimit
                    isAuto = true
                    autoDetails = "Tope dinámico Ocio: ${CurrencyFormatter.formatCentsToCop(autoAmount)} (60% residual)"
                }
                else -> {
                    val pct = if (block.percentage > 0) block.percentage else 10
                    autoAmount = (remainingIncome * (pct / 100.0)).toLong()
                    autoDetails = "Sugerido: $pct% del sobrante"
                }
            }

            val finalBudgetLimit = when (block.type) {
                BlockType.NEEDS, BlockType.SAVINGS -> 0L // Sin tope/barra de control para Necesidades y Ahorro
                else -> autoAmount // Forzamos el tope dinámico, eliminando ajuste manual
            }

            BlockAllocationUiModel(
                block = block,
                allocatedCents = autoAmount,
                spentCents = spent,
                budgetLimitCents = finalBudgetLimit,
                color = parsedColor,
                autoLoadedDetails = autoDetails,
                isAutoCalculated = isAuto
            )
        }
    }

    // Lista cronológica de movimientos planeados (Ingresos y Gastos fijados)
    val plannedMovements: StateFlow<List<PlannedMovementUi>> = combine(
        consolidatedBalanceCents,
        planSources,
        activeModel
    ) { balance, sources, model ->
        val today = LocalDate.now()
        val savingsPct = model?.blocks?.find { it.type == BlockType.SAVINGS }?.percentage ?: 20
        val events = mutableListOf<EnvelopeEvent>()

        // 1. Incomes
        sources.incomes.filter { !it.isReceived }.forEach { inc ->
            events.add(EnvelopeEvent(
                date = java.time.Instant.ofEpochMilli(inc.expectedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate(),
                isIncome = true,
                title = inc.description,
                amountCents = inc.amountCents
            ))
        }

        // 2. Debts (solo la próxima cuota pendiente; si ya se pagó, salta al mes siguiente)
        sources.debts.forEach { debt ->
            val date = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today)
            events.add(EnvelopeEvent(date, false, debt.name, debt.monthlyInstallmentCents))
        }

        // 3. Recurrings
        sources.recurrings.forEach { rec ->
            val date = java.time.Instant.ofEpochMilli(rec.nextDueDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            events.add(EnvelopeEvent(if (!date.isBefore(today)) date else nextOccurrence(date.dayOfMonth, today), false, rec.title, rec.amountCents))
        }

        // 4. Tarjetas de crédito: el pago del próximo vencimiento de cada tarjeta.
        cardPaymentsForHorizon(sources.cards, today, occurrences = 1).forEach { p ->
            events.add(EnvelopeEvent(p.date, false, p.title, p.amountCents))
        }

        val ordered = events.sortedWith(compareBy({ it.date }, { if (it.isIncome) 0 else 1 }))
        var currentBalance = balance ?: 0L
        
        ordered.map { ev ->
            if (ev.isIncome) {
                // Descontamos ahorro del ingreso para mostrar el impacto real en disponible
                val spendableInc = (ev.amountCents * (100 - savingsPct)) / 100L
                currentBalance += spendableInc
                PlannedMovementUi(ev.date, ev.date.format(envelopeDateFormatter), "Entra ${ev.title}", ev.amountCents, true, currentBalance)
            } else {
                currentBalance -= ev.amountCents
                PlannedMovementUi(ev.date, ev.date.format(envelopeDateFormatter), ev.title, ev.amountCents, false, currentBalance)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Proyecciones de Flujo de Caja Proyectadas (con ingresos programados futuros y ahorro descontado)
    val cashFlowProjection: StateFlow<CashFlowProjectionSummary> = combine(
        consolidatedBalanceCents,
        planSources,
        activeModel
    ) { balance: Long?, sources, model ->
        val incomeMap = mutableMapOf<LocalDate, Long>()
        val grossIncomeMap = mutableMapOf<LocalDate, Long>()
        val expenseMap = mutableMapOf<LocalDate, Long>()

        val today = LocalDate.now()
        val savingsPct = model?.blocks?.find { it.type == BlockType.SAVINGS }?.percentage ?: 20

        // Mapear ingresos en el horizonte, descontando el ahorro de una vez.
        // Un ingreso RECURRENTE se proyecta en TODAS sus ocurrencias dentro del horizonte
        // (aunque la de este mes ya se haya recibido); si no, la curva caía siempre como si
        // nunca fuera a entrar dinero.
        // También se guarda el monto BRUTO para la curva de ahorro acumulado.
        val horizonEnd = today.plusDays(90)

        fun registerIncome(date: LocalDate, amountCents: Long) {
            val spendableInc = (amountCents * (100 - savingsPct)) / 100L
            incomeMap[date] = (incomeMap[date] ?: 0L) + spendableInc
            grossIncomeMap[date] = (grossIncomeMap[date] ?: 0L) + amountCents
        }

        sources.incomes.forEach { inc ->
            val anchor = Instant.ofEpochMilli(inc.expectedDateMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()

            // Ingresos únicos: solo cuentan si aún no se han recibido.
            if (inc.frequency == IncomeFrequency.ONE_TIME || inc.frequency == IncomeFrequency.CUSTOM) {
                if (!inc.isReceived) {
                    registerIncome(if (anchor.isBefore(today)) today else anchor, inc.amountCents)
                }
                return@forEach
            }

            // Ingresos recurrentes: se expanden por frecuencia.
            // - Si ya se recibió el de este ciclo, solo se proyectan los siguientes (evita duplicar
            //   el dinero que ya está en el saldo).
            // - Si está pendiente y ya venció, la ocurrencia vencida sigue siendo dinero disponible HOY.
            val minDate = if (inc.isReceived) today.plusDays(1) else today
            var overdueRegistered = false
            var k = 0
            while (k <= 40) {
                val date = when (inc.frequency) {
                    IncomeFrequency.WEEKLY -> anchor.plusDays(7L * k)
                    IncomeFrequency.BIWEEKLY -> anchor.plusDays(15L * k)
                    else -> DueDates.shiftMonths(anchor, k.toLong())
                }
                if (!date.isBefore(horizonEnd)) break

                if (date.isBefore(minDate)) {
                    if (!inc.isReceived && !overdueRegistered) {
                        registerIncome(today, inc.amountCents)
                        overdueRegistered = true
                    }
                } else {
                    registerIncome(date, inc.amountCents)
                }
                k++
            }
        }

        // Mapear pagos de cuotas mensuales de deudas desde la próxima cuota pendiente.
        // Si la cuota del periodo ya se pagó (movimiento anclado), el vencimiento ya avanzó un mes.
        sources.debts.forEach { debt ->
            val firstDue = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today)
            for (monthOffset in 0..3) {
                val date = DueDates.shiftMonths(firstDue, monthOffset.toLong())
                if (!date.isBefore(today)) {
                    expenseMap[date] = (expenseMap[date] ?: 0L) + debt.monthlyInstallmentCents
                }
            }
        }

        // Mapear pagos de obligaciones fijas mensuales desde su próximo vencimiento real.
        // Si el pago del periodo ya se registró, el vencimiento ya avanzó un mes.
        sources.recurrings.forEach { rec ->
            val firstDue = DueDates.toLocalDate(rec.nextDueDateMillis)
            for (monthOffset in 0..3) {
                val date = DueDates.shiftMonths(firstDue, monthOffset.toLong())
                if (!date.isBefore(today)) {
                    expenseMap[date] = (expenseMap[date] ?: 0L) + rec.amountCents
                }
            }
        }

        // Mapear pagos de tarjetas de crédito en el mismo horizonte de 4 meses.
        // Sin esto, "Libre hoy" y la proyección ignoraban las tarjetas.
        cardPaymentsForHorizon(sources.cards, today, occurrences = 4).forEach { p ->
            expenseMap[p.date] = (expenseMap[p.date] ?: 0L) + p.amountCents
        }

        // Para la proyección, el balance inicial también debe descontar el ahorro de lo ya recibido
        val receivedThisMonth = sources.incomes.filter {
            it.isReceived && (it.receivedDateMillis ?: 0L) in startOfMonth..endOfMonth
        }.sumOf { it.amountCents }
        val savingsAlreadyReserved = (receivedThisMonth * savingsPct) / 100L
        val spendableInitialBalance = (balance ?: 0L) - savingsAlreadyReserved

        AllocationCalculator.projectCashFlow(
            initialBalanceCents = spendableInitialBalance,
            dailyIncomes = incomeMap,
            dailyExpenses = expenseMap,
            startDate = today,
            horizonDays = 90,
            dailyGrossIncomes = grossIncomeMap
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CashFlowProjectionSummary(0L, 0L, 0L, emptyList())
    )

    // Proyecciones de Flujo de Caja Actual (ahora inteligente: incluye ingresos pero descuenta ahorros)
    val actualCashFlowProjection: StateFlow<CashFlowProjectionSummary> = cashFlowProjection

    // Semáforo Safe-to-Spend Inteligente
    // Considera tanto el saldo actual como los ingresos que van a entrar,
    // asegurando que nunca se caiga en negativo y descontando la meta de ahorro.
    private val intelligentSafeToSpendCalculation: StateFlow<SafeToSpendCalculation> = combine(
        spendableBalanceCents,
        cashFlowProjection,
        commitmentsSummary,
        activeModel
    ) { spendableCash: Long?, projection: CashFlowProjectionSummary, commitments, model ->
        val savingsPct = model?.blocks?.find { it.type == BlockType.SAVINGS }?.percentage ?: 20
        val receivedIncome = (commitments.monthlyFixedIncome - commitments.pendingIncomesCents).coerceAtLeast(0L)
        val savingsFromReceived = (receivedIncome * savingsPct) / 100L
        
        // El disponible real hoy es lo que hay en cuentas menos lo que ya debería estar ahorrado
        val currentSpendable = (spendableCash ?: 0L) - savingsFromReceived

        AllocationCalculator.calculateSafeToSpendIntelligent(
            consolidatedCashCents = currentSpendable,
            dailyProjections = projection.dailyProjections,
            savingsToReserveCents = 0L, // Ya descontado en la proyección y en currentSpendable
            today = LocalDate.now()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SafeToSpendCalculation(0L, 0L, 0L, 0L, 30, SafeToSpendStatus.GREEN, "Calculando...")
    )

    val actualSafeToSpend: StateFlow<SafeToSpendCalculation> = intelligentSafeToSpendCalculation
    val projectedSafeToSpend: StateFlow<SafeToSpendCalculation> = intelligentSafeToSpendCalculation

    // Alias retrocompatible
    val safeToSpend: StateFlow<SafeToSpendCalculation> = actualSafeToSpend

    // Saldo Proyectado Consolidado (Saldo real actual + Ingresos programados pendientes)
    val projectedConsolidatedBalanceCents: StateFlow<Long> = combine(
        consolidatedBalanceCents,
        commitmentsSummary
    ) { balance, commitments ->
        (balance ?: 0L) + commitments.pendingIncomesCents
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun switchActiveModel(modelId: Long) {
        viewModelScope.launch {
            try {
                allocationDao.setActiveModel(modelId)
                _userMessage.emit("Modelo de distribución actualizado")
            } catch (e: Exception) {
                _userMessage.emit("Error al cambiar modelo: ${e.message}")
            }
        }
    }

    fun setBudgetLimit(blockId: Long, limitCents: Long) {
        viewModelScope.launch {
            try {
                val budget = BudgetEntity(
                    blockId = blockId,
                    month = now.monthValue,
                    year = now.year,
                    limitAmountCents = limitCents
                )
                allocationDao.insertBudget(budget)
                _userMessage.emit("Tope de sobre mensual actualizado")
            } catch (e: Exception) {
                _userMessage.emit("Error al fijar presupuesto: ${e.message}")
            }
        }
    }

    fun addRecurringItem(
        title: String,
        amountCents: Long,
        accountId: Long,
        categoryId: Long,
        dueDayOfMonth: Int
    ) {
        viewModelScope.launch {
            try {
                require(title.isNotBlank()) { "Ingresa una descripción para la obligación" }
                require(amountCents > 0L) { "El monto debe ser mayor a 0" }

                val today = LocalDate.now()
                val targetMonth = if (dueDayOfMonth >= today.dayOfMonth) today else today.plusMonths(1)
                val validDay = minOf(dueDayOfMonth, targetMonth.lengthOfMonth())
                val dueDateMillis = targetMonth.withDayOfMonth(validDay)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                val activeModelWithBlocks = allocationDao.getActiveModelWithBlocks().firstOrNull()
                val blockId = activeModelWithBlocks?.blocks?.firstOrNull { it.type == BlockType.NEEDS }?.id
                    ?: activeModelWithBlocks?.blocks?.firstOrNull()?.id ?: 1L

                val item = RecurringItemEntity(
                    accountId = accountId,
                    categoryId = categoryId,
                    blockId = blockId,
                    title = title.trim(),
                    amountCents = amountCents,
                    frequency = IncomeFrequency.MONTHLY,
                    nextDueDateMillis = dueDateMillis,
                    notifyDaysBefore = 2,
                    isActive = true
                )
                recurringItemDao.insertRecurringItem(item)
                _userMessage.emit("Obligación fija agregada: $title")
            } catch (e: Exception) {
                _userMessage.emit("Error al crear obligación: ${e.message}")
            }
        }
    }

    fun payRecurringItem(item: RecurringItemEntity, paymentAccountId: Long? = null) {
        viewModelScope.launch {
            try {
                val targetAccountId = paymentAccountId ?: item.accountId
                val transactionId = recordExpenseUseCase(
                    accountId = targetAccountId,
                    categoryId = item.categoryId,
                    amountCents = item.amountCents,
                    note = "Pago de obligación fija: ${item.title}"
                )

                // Ancla el gasto a la obligación: la marca como cumplida y avanza el vencimiento.
                anchorTransactionUseCase(transactionId, linkedDebtId = null, linkedRecurringItemId = item.id)

                val nextDue = DueDates.shiftMonths(DueDates.toLocalDate(item.nextDueDateMillis), 1)
                _userMessage.emit("Pago registrado para ${item.title}. Próximo cobro: ${nextDue.dayOfMonth} ${nextDue.month.name.lowercase().take(3)}")
            } catch (e: Exception) {
                _userMessage.emit("Error al pagar obligación: ${e.message}")
            }
        }
    }

    fun updateRecurringItem(
        item: RecurringItemEntity,
        title: String,
        amountCents: Long,
        accountId: Long,
        categoryId: Long,
        dueDayOfMonth: Int
    ) {
        viewModelScope.launch {
            try {
                require(title.isNotBlank()) { "Ingresa una descripción para la obligación" }
                require(amountCents > 0L) { "El monto debe ser mayor a 0" }

                val currentDue = java.time.Instant.ofEpochMilli(item.nextDueDateMillis)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val validDay = minOf(dueDayOfMonth, currentDue.lengthOfMonth())
                val nextDueDateMillis = currentDue.withDayOfMonth(validDay)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                val updated = item.copy(
                    title = title.trim(),
                    amountCents = amountCents,
                    accountId = accountId,
                    categoryId = categoryId,
                    nextDueDateMillis = nextDueDateMillis
                )
                recurringItemDao.updateRecurringItem(updated)
                _userMessage.emit("Obligación actualizada: ${updated.title}")
            } catch (e: Exception) {
                _userMessage.emit("Error al actualizar obligación: ${e.message}")
            }
        }
    }

    fun deleteRecurringItem(item: RecurringItemEntity) {
        viewModelScope.launch {
            try {
                recurringItemDao.deleteRecurringItem(item)
                _userMessage.emit("Obligación eliminada: ${item.title}")
            } catch (e: Exception) {
                _userMessage.emit("Error al eliminar obligación: ${e.message}")
            }
        }
    }

    fun lockApp() {
        securityLifecycleObserver.lock()
    }
}
