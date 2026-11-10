package com.personalfinance.cuentas.presentation.receivables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.ContactDao
import com.personalfinance.cuentas.data.local.dao.IncomeDao
import com.personalfinance.cuentas.data.local.dao.ReceivableDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.ContactEntity
import com.personalfinance.cuentas.data.local.entities.ContactKind
import com.personalfinance.cuentas.data.local.entities.IncomeEntity
import com.personalfinance.cuentas.data.local.entities.IncomeFrequency
import com.personalfinance.cuentas.data.local.entities.ReceivableEntity
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.relations.TransactionWithDetails
import com.personalfinance.cuentas.domain.usecases.ConfirmIncomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/** Saldo pendiente agrupado por contraparte. */
data class ContactBalanceUi(
    val contact: ContactEntity,
    val pendingCents: Long,
    val pendingCount: Int
)

/** Fila de una cuenta por cobrar lista para mostrar. */
data class ReceivableRowUi(
    val receivable: ReceivableEntity,
    val contactName: String,
    val sourceLabel: String,
    val isSettled: Boolean,
    val dateLabel: String
)

/** Porción de un gasto o préstamo asignada a una contraparte. */
data class ShareInput(
    val contactId: Long,
    val owedCents: Long
)

@HiltViewModel
class ReceivablesViewModel @Inject constructor(
    private val contactDao: ContactDao,
    private val receivableDao: ReceivableDao,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val incomeDao: IncomeDao,
    private val confirmIncomeUseCase: ConfirmIncomeUseCase
) : ViewModel() {

    private val dateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "CO"))

    val accounts = accountDao.getAllActiveAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts = contactDao.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allReceivables = receivableDao.getAllReceivables()

    val totalPendingCents: StateFlow<Long> = allReceivables
        .map { list -> list.filter { !it.isSettled }.sumOf { it.owedCents } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val contactBalances: StateFlow<List<ContactBalanceUi>> =
        combine(contacts, allReceivables) { cs, rs ->
            cs.map { c ->
                val pending = rs.filter { it.contactId == c.id && !it.isSettled }
                ContactBalanceUi(c, pending.sumOf { it.owedCents }, pending.size)
            }.sortedWith(compareByDescending<ContactBalanceUi> { it.pendingCents }.thenBy { it.contact.name })
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Gastos del histórico disponibles para cargarle una porción a terceros. */
    val expenseTransactions: StateFlow<List<TransactionWithDetails>> =
        transactionDao.getFilteredTransactions(null, null, "EXPENSE")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val transactionLabels: StateFlow<Map<Long, String>> =
        transactionDao.getAllTransactions()
            .map { txs -> txs.associate { it.id to buildTransactionLabel(it) } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    /** Ingresos reales aún no anclados a una deuda por cobrar (candidatos a anclar como pago). */
    val unanchoredIncomeTransactions: StateFlow<List<TransactionEntity>> =
        transactionDao.getUnanchoredIncomeTransactions(50)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingRows: StateFlow<List<ReceivableRowUi>> =
        combine(receivableDao.getPendingReceivables(), contacts, transactionLabels) { rs, cs, labels ->
            val nameById = cs.associate { it.id to it.name }
            rs.map { r -> r.toRow(nameById[r.contactId], labels[r.sourceTransactionId], settled = false) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settledRows: StateFlow<List<ReceivableRowUi>> =
        combine(receivableDao.getSettledReceivables(), contacts, transactionLabels) { rs, cs, labels ->
            val nameById = cs.associate { it.id to it.name }
            rs.map { r -> r.toRow(nameById[r.contactId], labels[r.sourceTransactionId], settled = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    /**
     * Crea una contraparte (persona o sitio). [onCreated] recibe el id generado para
     * seleccionarlo de inmediato en un registro en vivo.
     */
    fun addContact(name: String, kind: ContactKind, onCreated: (Long) -> Unit = {}) {
        val clean = name.trim()
        if (clean.isBlank()) {
            viewModelScope.launch { _userMessage.emit("Escribe un nombre válido") }
            return
        }
        viewModelScope.launch {
            val id = contactDao.insertContact(ContactEntity(name = clean, kind = kind))
            _userMessage.emit("Contacto \"$clean\" creado")
            onCreated(id)
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            contactDao.deleteContact(contact)
            _userMessage.emit("Contacto eliminado")
        }
    }

    /** Registra una o varias cuentas por cobrar sobre un gasto del histórico o un préstamo. */
    fun createReceivables(
        sourceTransactionId: Long?,
        note: String?,
        shares: List<ShareInput>
    ) {
        val valid = shares.filter { it.owedCents > 0L }
        if (valid.isEmpty()) {
            viewModelScope.launch { _userMessage.emit("Asigna un monto mayor a cero a alguien") }
            return
        }
        viewModelScope.launch {
            val entities = valid.map { share ->
                ReceivableEntity(
                    contactId = share.contactId,
                    sourceTransactionId = sourceTransactionId,
                    note = note?.trim()?.ifBlank { null },
                    owedCents = share.owedCents
                )
            }
            receivableDao.insertReceivables(entities)
            _userMessage.emit(
                if (entities.size == 1) "Deuda por cobrar registrada"
                else "${entities.size} deudas por cobrar registradas"
            )
        }
    }

    /**
     * Cobra una cuenta por cobrar dejándola anclada a un movimiento real:
     *  - Si [existingTransactionId] es null, registra un nuevo ingreso en [accountId]
     *    (movimiento real) y lo ancla a la deuda por cobrar.
     *  - Si [existingTransactionId] no es null, ancla ese movimiento existente
     *    (p. ej. un ingreso capturado por notificación/SMS) sin duplicar el saldo.
     */
    fun settle(
        receivable: ReceivableEntity,
        accountId: Long,
        existingTransactionId: Long? = null
    ) {
        viewModelScope.launch {
            try {
                val contact = contactDao.getContactById(receivable.contactId)
                val contactName = contact?.name ?: "contacto"
                val now = System.currentTimeMillis()

                val transactionId: Long?
                val effectiveAccountId: Long

                if (existingTransactionId != null) {
                    val movement = transactionDao.getTransactionById(existingTransactionId)
                    if (movement == null) {
                        _userMessage.emit("El movimiento seleccionado ya no existe")
                        return@launch
                    }
                    transactionDao.updateReceivableAnchor(existingTransactionId, receivable.id)
                    transactionId = existingTransactionId
                    effectiveAccountId = movement.accountId
                } else {
                    if (accountId == 0L) {
                        _userMessage.emit("Selecciona la cuenta que recibe el dinero")
                        return@launch
                    }
                    val income = IncomeEntity(
                        accountId = accountId,
                        description = "Cobro de $contactName",
                        amountCents = receivable.owedCents,
                        frequency = IncomeFrequency.ONE_TIME,
                        expectedDateMillis = now
                    )
                    val incomeId = incomeDao.insertIncome(income)
                    transactionId = confirmIncomeUseCase(
                        incomeId = incomeId,
                        actualReceivedCents = receivable.owedCents,
                        targetAccountId = accountId,
                        receivedMillis = now,
                        linkedReceivableId = receivable.id
                    )
                    effectiveAccountId = accountId
                }

                receivableDao.markSettled(receivable.id, now, effectiveAccountId, transactionId)
                _userMessage.emit("Cobro de $contactName registrado como movimiento")
            } catch (e: Exception) {
                _userMessage.emit("Error al cobrar: ${e.message}")
            }
        }
    }

    fun deleteReceivable(receivable: ReceivableEntity) {
        viewModelScope.launch {
            // Si ya estaba cobrada, solo desancla el movimiento real: no lo borra ni altera saldos,
            // porque ese movimiento bancario es real y debe conservarse para la conciliación 1:1.
            receivable.settledTransactionId?.let { txId ->
                runCatching { transactionDao.updateReceivableAnchor(txId, null) }
            }
            receivableDao.deleteReceivable(receivable)
            _userMessage.emit("Registro eliminado")
        }
    }

    private fun buildTransactionLabel(tx: TransactionEntity): String {
        val title = tx.note ?: tx.merchant ?: "Gasto"
        return "$title · ${CurrencyFormatter.formatCentsToCop(tx.amountCents)}"
    }

    private fun ReceivableEntity.toRow(
        contactName: String?,
        sourceLabel: String?,
        settled: Boolean
    ): ReceivableRowUi {
        val millis = if (settled) (settledAtMillis ?: createdAt) else createdAt
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        return ReceivableRowUi(
            receivable = this,
            contactName = contactName ?: "Sin contacto",
            sourceLabel = sourceLabel ?: (note ?: "Préstamo directo"),
            isSettled = settled,
            dateLabel = date.format(dateFormatter)
        )
    }
}
