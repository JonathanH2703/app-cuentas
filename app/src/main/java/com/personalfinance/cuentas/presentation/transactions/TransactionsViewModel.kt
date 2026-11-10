package com.personalfinance.cuentas.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.DebtDao
import com.personalfinance.cuentas.data.local.dao.PendingTransactionDao
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import com.personalfinance.cuentas.data.local.entities.PendingStatus
import com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity
import com.personalfinance.cuentas.data.local.relations.TransactionWithDetails
import com.personalfinance.cuentas.domain.anchor.AnchorOption
import com.personalfinance.cuentas.domain.anchor.AnchorType
import com.personalfinance.cuentas.domain.schedule.DueDates
import com.personalfinance.cuentas.domain.usecases.AnchorTransactionUseCase
import com.personalfinance.cuentas.domain.usecases.PrepopulateCategoriesUseCase
import com.personalfinance.cuentas.domain.usecases.RecordExpenseUseCase
import com.personalfinance.cuentas.domain.usecases.creditcards.PayCreditCardUseCase
import com.personalfinance.cuentas.domain.usecases.debts.RecordDebtPaymentUseCase
import com.personalfinance.cuentas.data.local.entities.PendingSource
import com.personalfinance.cuentas.domain.capture.ColombianBankParser
import com.personalfinance.cuentas.domain.usecases.capture.CaptureResult
import com.personalfinance.cuentas.domain.usecases.capture.ProcessBankCaptureUseCase
import com.personalfinance.cuentas.service.FinanceNotificationListenerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val allocationDao: AllocationDao,
    private val debtDao: DebtDao,
    private val recurringItemDao: RecurringItemDao,
    private val pendingTransactionDao: PendingTransactionDao,
    private val creditCardDao: com.personalfinance.cuentas.data.local.dao.CreditCardDao,
    private val recordExpenseUseCase: RecordExpenseUseCase,
    private val anchorTransactionUseCase: AnchorTransactionUseCase,
    private val prepopulateCategoriesUseCase: PrepopulateCategoriesUseCase,
    private val processBankCaptureUseCase: ProcessBankCaptureUseCase,
    private val payCreditCardUseCase: PayCreditCardUseCase,
    private val recordDebtPaymentUseCase: RecordDebtPaymentUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            prepopulateCategoriesUseCase()
        }
    }

    val accounts = accountDao.getAllActiveAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTypeFilter = MutableStateFlow<TransactionType?>(null)
    val selectedTypeFilter: StateFlow<TransactionType?> = _selectedTypeFilter.asStateFlow()

    private val _selectedAccountId = MutableStateFlow<Long?>(null)
    val selectedAccountId: StateFlow<Long?> = _selectedAccountId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Obligaciones disponibles para anclar un movimiento: créditos activos y gastos fijos mensuales
    val anchorOptions: StateFlow<List<AnchorOption>> = combine(
        debtDao.getActiveDebts(),
        recurringItemDao.getAllActiveRecurringItems()
    ) { debts, recurrings ->
        val today = LocalDate.now()
        val dateFormat = DateTimeFormatter.ofPattern("d MMM", Locale("es", "CO"))

        val debtOptions = debts.map { debt ->
            val due = DueDates.effectiveDebtDueDate(debt.dueDayOfMonth, debt.nextDueDateMillis, today)
            AnchorOption(
                type = AnchorType.DEBT,
                id = debt.id,
                title = debt.name,
                expectedCents = debt.monthlyInstallmentCents,
                subtitle = "Crédito · Cuota ${CurrencyFormatter.formatCentsToCop(debt.monthlyInstallmentCents)} · vence ${due.format(dateFormat)}"
            )
        }

        val recurringOptions = recurrings.map { item ->
            val due = DueDates.toLocalDate(item.nextDueDateMillis)
            AnchorOption(
                type = AnchorType.RECURRING,
                id = item.id,
                title = item.title,
                expectedCents = item.amountCents,
                subtitle = "Gasto fijo · ${CurrencyFormatter.formatCentsToCop(item.amountCents)} · vence ${due.format(dateFormat)}"
            )
        }

        debtOptions + recurringOptions
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flujo filtrado y combinado de transacciones
    val transactions: StateFlow<List<TransactionWithDetails>> = combine(
        transactionDao.getAllTransactionsWithDetails(),
        _selectedTypeFilter,
        _selectedAccountId,
        _searchQuery
    ) { all, type, accountId, query ->
        all.filter { item ->
            val matchesType = type == null || item.transaction.type == type
            val matchesAccount = accountId == null || item.transaction.accountId == accountId
            val matchesQuery = query.isBlank() ||
                    (item.transaction.merchant?.contains(query, ignoreCase = true) == true) ||
                    (item.transaction.note?.contains(query, ignoreCase = true) == true) ||
                    (item.category.name.contains(query, ignoreCase = true))

            matchesType && matchesAccount && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Compras de tarjetas de credito: se muestran en el historial junto con las transacciones normales.
    // Incluye la cuenta de la tarjeta para mostrar el nombre en la lista.
    val cardPurchases: StateFlow<List<com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount>> =
        creditCardDao.getAllCreditCardsWithAccount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pagos de obligación (tarjeta de crédito o crédito/préstamo) detectados automáticamente
    // (correos PSE/banco) y pendientes de confirmación.
    val pendingObligationPayments: StateFlow<List<PendingTransactionEntity>> =
        pendingTransactionDao.getPendingObligationPayments()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTypeFilter(type: TransactionType?) {
        _selectedTypeFilter.value = type
    }

    fun setAccountFilter(accountId: Long?) {
        _selectedAccountId.value = accountId
    }

    /**
     * Confirma un pago de obligación detectado:
     * - Tarjeta de crédito: baja el cupo usado sin debitar ninguna cuenta.
     * - Crédito/préstamo: reduce el saldo del crédito sin debitar ninguna cuenta.
     * En ambos casos el egreso real ya lo registra el correo del banco origen.
     */
    fun confirmObligationPayment(pending: PendingTransactionEntity) {
        viewModelScope.launch {
            try {
                when {
                    pending.suggestedCardId != null -> {
                        payCreditCardUseCase.reduceCardDebt(
                            cardId = pending.suggestedCardId,
                            amountCents = pending.suggestedAmountCents,
                            paymentDateMillis = pending.detectedDateMillis
                        )
                        pendingTransactionDao.updateStatus(pending.id, PendingStatus.CONFIRMED)
                        _userMessage.emit("Pago aplicado a ${pending.suggestedMerchant ?: "la tarjeta"}. Cupo usado actualizado.")
                    }
                    pending.suggestedDebtId != null -> {
                        recordDebtPaymentUseCase(
                            debtId = pending.suggestedDebtId,
                            amountPaidCents = pending.suggestedAmountCents,
                            isExtraPayment = false,
                            paymentDateMillis = pending.detectedDateMillis
                        )
                        pendingTransactionDao.updateStatus(pending.id, PendingStatus.CONFIRMED)
                        _userMessage.emit("Pago aplicado a ${pending.suggestedMerchant ?: "el crédito"}. Saldo actualizado.")
                    }
                    else -> {
                        _userMessage.emit("Este registro no corresponde a una obligación")
                    }
                }
            } catch (e: Exception) {
                _userMessage.emit("Error al aplicar el pago: ${e.message}")
            }
        }
    }

    /** Descarta un pago de obligación detectado que en realidad no corresponde. */
    fun discardObligationPayment(pending: PendingTransactionEntity) {
        viewModelScope.launch {
            pendingTransactionDao.updateStatus(pending.id, PendingStatus.DISCARDED)
            _userMessage.emit("Pago detectado descartado")
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun recordQuickExpense(
        accountId: Long,
        categoryId: Long,
        amountCents: Long,
        note: String?,
        linkedDebtId: Long? = null,
        linkedRecurringItemId: Long? = null
    ) {
        viewModelScope.launch {
            try {
                val transactionId = recordExpenseUseCase(
                    accountId = accountId,
                    categoryId = categoryId,
                    amountCents = amountCents,
                    note = note,
                    merchant = note
                )
                if (linkedDebtId != null || linkedRecurringItemId != null) {
                    anchorTransactionUseCase(transactionId, linkedDebtId, linkedRecurringItemId)
                }
                _userMessage.emit("Gasto registrado exitosamente")
            } catch (e: Exception) {
                _userMessage.emit("Error al guardar: ${e.message}")
            }
        }
    }

    /**
     * Ancla o desancla un movimiento ya registrado a una obligación (crédito o gasto fijo).
     * Pasar ambos ids en null elimina el ancla.
     */
    fun anchorTransaction(
        transactionId: Long,
        linkedDebtId: Long?,
        linkedRecurringItemId: Long?
    ) {
        viewModelScope.launch {
            try {
                anchorTransactionUseCase(transactionId, linkedDebtId, linkedRecurringItemId)
                _userMessage.emit(
                    if (linkedDebtId == null && linkedRecurringItemId == null) {
                        "Ancla eliminada del movimiento"
                    } else {
                        "Movimiento anclado a la obligación"
                    }
                )
            } catch (e: Exception) {
                _userMessage.emit("Error al anclar: ${e.message}")
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            try {
                // Si el movimiento cumplía una obligación, revertimos su efecto antes de eliminarlo
                if (transaction.linkedDebtId != null || transaction.linkedRecurringItemId != null) {
                    anchorTransactionUseCase(transaction.id, null, null)
                }

                val account = accountDao.getAccountByIdSync(transaction.accountId)
                if (account != null) {
                    val restoredBalance = if (transaction.type == TransactionType.EXPENSE) {
                        account.currentBalanceCents + transaction.amountCents
                    } else if (transaction.type == TransactionType.INCOME) {
                        account.currentBalanceCents - transaction.amountCents
                    } else {
                        account.currentBalanceCents
                    }
                    accountDao.updateBalance(account.id, restoredBalance)
                }
                // Limpiar registro de captura pendiente para permitir re-probar notificaciones sin duplicados
                pendingTransactionDao.deleteSimilarCapture(transaction.amountCents, transaction.accountId)
                transactionDao.deleteTransaction(transaction)
                _userMessage.emit("Movimiento eliminado y saldo de cuenta restaurado")
            } catch (e: Exception) {
                _userMessage.emit("Error al eliminar: ${e.message}")
            }
        }
    }

    /**
     * Edita un movimiento existente: categoría (que determina el sobre/bloque presupuestario),
     * monto y nota. Reajusta el saldo de la cuenta por la diferencia de monto y, si el movimiento
     * estaba anclado a una obligación, reaplica su efecto con el nuevo valor.
     */
    fun updateTransaction(
        transaction: TransactionEntity,
        categoryId: Long,
        amountCents: Long,
        note: String?,
        isLeisure: Boolean,
        type: TransactionType
    ) {
        if (amountCents <= 0L) {
            viewModelScope.launch { _userMessage.emit("El monto debe ser mayor a cero") }
            return
        }
        viewModelScope.launch {
            try {
                val amountDelta = amountCents - transaction.amountCents
                val typeChanged = transaction.type != type
                val wasAnchored = transaction.linkedDebtId != null || transaction.linkedRecurringItemId != null

                // Si el monto cambió y el movimiento cumplía una obligación, revertimos el pago
                // registrado para volver a aplicarlo con el nuevo valor.
                if (wasAnchored && (amountDelta != 0L || typeChanged)) {
                    anchorTransactionUseCase(transaction.id, null, null)
                }

                // Ajustar el saldo de la cuenta
                val account = accountDao.getAccountByIdSync(transaction.accountId)
                if (account != null) {
                    // 1. Revertir el efecto del movimiento original
                    val balanceWithoutOriginal = when (transaction.type) {
                        TransactionType.EXPENSE -> account.currentBalanceCents + transaction.amountCents
                        TransactionType.INCOME -> account.currentBalanceCents - transaction.amountCents
                        else -> account.currentBalanceCents
                    }
                    
                    // 2. Aplicar el efecto del nuevo movimiento
                    val newBalance = when (type) {
                        TransactionType.EXPENSE -> balanceWithoutOriginal - amountCents
                        TransactionType.INCOME -> balanceWithoutOriginal + amountCents
                        else -> balanceWithoutOriginal
                    }
                    
                    accountDao.updateBalance(account.id, newBalance)
                }

                // Recalcular el bloque presupuestario según el tipo por defecto de la nueva categoría
                val newBlockId = resolveBlockIdForCategory(categoryId)

                transactionDao.updateTransactionDetails(
                    transactionId = transaction.id,
                    categoryId = categoryId,
                    blockId = newBlockId,
                    amountCents = amountCents,
                    note = note,
                    isLeisure = isLeisure
                )
                
                // Actualizar el tipo de transacción (esto no estaba en updateTransactionDetails antes)
                transactionDao.updateTransactionType(transaction.id, type)

                if (wasAnchored && (amountDelta != 0L || typeChanged)) {
                    anchorTransactionUseCase(
                        transaction.id,
                        transaction.linkedDebtId,
                        transaction.linkedRecurringItemId
                    )
                }

                _userMessage.emit("Movimiento actualizado")
            } catch (e: Exception) {
                _userMessage.emit("Error al actualizar: ${e.message}")
            }
        }
    }

    /** Resuelve el bloque del modelo de presupuesto activo que corresponde a una categoría. */
    private suspend fun resolveBlockIdForCategory(categoryId: Long): Long? {
        val category = categoryDao.getCategoryById(categoryId) ?: return null
        val model = allocationDao.getActiveModelWithBlocksSync() ?: return null
        val block = model.blocks.firstOrNull { it.type == category.defaultBlockType }
            ?: model.blocks.firstOrNull()
        return block?.id
    }

    fun scanActiveNotifications() {
        viewModelScope.launch {
            FinanceNotificationListenerService.scanActiveNotifications()
            _userMessage.emit("Escaneando notificaciones en barra de estado...")
        }
    }

    fun simulateTestNotification(sender: String, message: String) {
        viewModelScope.launch {
            val parsed = ColombianBankParser.parse(
                senderOrPackage = sender,
                rawText = message,
                source = PendingSource.NOTIFICATION,
                timestampMillis = System.currentTimeMillis()
            )
            if (parsed != null) {
                val result = processBankCaptureUseCase.execute(parsed, isSimulation = true)
                when (result) {
                    is CaptureResult.Success -> {
                        _userMessage.emit("✅ Prueba detectada: ${result.summary}")
                    }
                    is CaptureResult.Duplicate -> {
                        _userMessage.emit("⚠️ ${result.summary}")
                    }
                    is CaptureResult.Ignored -> {
                        _userMessage.emit("ℹ️ Ignorado: ${result.reason}")
                    }
                    is CaptureResult.CardPaymentPending -> {
                        _userMessage.emit("💳 ${result.summary}")
                    }
                    is CaptureResult.DebtPaymentPending -> {
                        _userMessage.emit("🏦 ${result.summary}")
                    }
                }
            } else {
                _userMessage.emit("❌ No se reconoció formato bancario en el mensaje de prueba")
            }
        }
    }
}
