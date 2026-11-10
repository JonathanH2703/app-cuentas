package com.personalfinance.cuentas.presentation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalfinance.cuentas.data.local.dao.EventDao
import com.personalfinance.cuentas.data.local.entities.EventEntity
import com.personalfinance.cuentas.data.local.entities.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Evento enriquecido con datos calculados para la UI.
 *
 * @param daysUntil        dias que faltan hasta la proxima ocurrencia (0 = hoy)
 * @param nextDate         fecha de la proxima ocurrencia
 * @param monthsUntil      meses completos hasta el vencimiento
 * @param targetCents      meta de ahorro = totalAmountCents * coveragePercent / 100
 * @param pendingCents     lo que falta = targetCents - accumulatedCents
 * @param monthlySaving    cuanto apartar por mes = pendingCents / monthsUntil
 *                         Null si el evento no tiene componente financiero.
 * @param progressPct      0..100, progreso del fondo vs la meta
 */
data class EventUi(
    val entity: EventEntity,
    val daysUntil: Int,
    val nextDate: LocalDate,
    val monthsUntil: Int = 0,
    val targetCents: Long? = null,
    val pendingCents: Long? = null,
    val monthlySaving: Long? = null,
    val progressPct: Int = 0
) {
    val isFinancial: Boolean get() = entity.isFinancialType

    /** Bolsillo al que está cargado: "NEEDS", "WANTS", "SAVINGS", o null. */
    val blockTypeName: String? get() = entity.blockType
}

val EventEntity.isFinancialType: Boolean
    get() = type == EventType.OBLIGATION_ANNUAL || type == EventType.OBLIGATION_SPORADIC

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventDao: EventDao
) : ViewModel() {

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    /** Todos los eventos activos: obligaciones primero, luego personales, ambos por dias. */
    val events: StateFlow<List<EventUi>> = eventDao.getAllActiveEvents()
        .map { list -> list.map { toUi(it) }.sortedWith(compareBy({ !it.isFinancial }, { it.daysUntil })) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Todos los eventos (activos e inactivos) para la pantalla de gestion. */
    val allEvents: StateFlow<List<EventUi>> = eventDao.getAllEvents()
        .map { list -> list.map { toUi(it) }.sortedWith(compareBy({ !it.isFinancial }, { it.daysUntil })) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun addEvent(
        title: String,
        type: EventType,
        dayOfMonth: Int,
        monthOfYear: Int,
        year: Int?,
        personName: String?,
        notifyDaysBefore: Int,
        totalAmountCents: Long? = null,
        coveragePercent: Int = 100,
        blockType: String? = null
    ) {
        viewModelScope.launch {
            try {
                require(title.isNotBlank()) { "El titulo no puede estar vacio" }
                require(dayOfMonth in 1..31) { "Dia invalido" }
                require(monthOfYear in 1..12) { "Mes invalido" }
                eventDao.insertEvent(
                    EventEntity(
                        title = title.trim(),
                        type = type,
                        dayOfMonth = dayOfMonth,
                        monthOfYear = monthOfYear,
                        year = year,
                        personName = personName?.trim()?.takeIf { it.isNotBlank() },
                        notifyDaysBefore = notifyDaysBefore.coerceIn(0, 30),
                        isActive = true,
                        totalAmountCents = totalAmountCents,
                        accumulatedCents = 0L,
                        coveragePercent = coveragePercent.coerceIn(1, 100),
                        blockType = blockType
                    )
                )
                _userMessage.emit("Guardado")
            } catch (e: Exception) {
                _userMessage.emit("Error: ${e.message}")
            }
        }
    }

    fun updateEvent(event: EventEntity) {
        viewModelScope.launch {
            try {
                eventDao.updateEvent(event)
                _userMessage.emit("Actualizado")
            } catch (e: Exception) {
                _userMessage.emit("Error al actualizar")
            }
        }
    }

    /** Registra un abono al fondo de esta obligacion. */
    fun addAccumulation(event: EventEntity, amountCents: Long) {
        viewModelScope.launch {
            val target = targetCents(event)
            val newAccumulated = (event.accumulatedCents + amountCents)
                .coerceAtLeast(0L)
                .coerceAtMost(target ?: Long.MAX_VALUE)
            eventDao.updateEvent(event.copy(accumulatedCents = newAccumulated))
            _userMessage.emit("Abono registrado")
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            eventDao.deleteEvent(event)
            _userMessage.emit("Eliminado")
        }
    }

    fun toggleActive(event: EventEntity) {
        viewModelScope.launch {
            eventDao.updateEvent(event.copy(isActive = !event.isActive))
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    companion object {

        /**
         * Meta real de ahorro = totalAmountCents * coveragePercent / 100.
         * Ejemplo: SOAT $800.000, cobertura 20% -> meta $160.000.
         */
        fun targetCents(event: EventEntity): Long? {
            val total = event.totalAmountCents ?: return null
            return (total * event.coveragePercent / 100L).coerceAtLeast(0L)
        }

        /**
         * Calcula cuantos dias y en que fecha cae la proxima ocurrencia del evento.
         */
        fun daysUntilNextOccurrence(dayOfMonth: Int, monthOfYear: Int, year: Int?): Pair<Int, LocalDate> {
            val today = LocalDate.now()
            if (year != null) {
                return try {
                    val d = dayOfMonth.coerceAtMost(LocalDate.of(year, monthOfYear, 1).lengthOfMonth())
                    val date = LocalDate.of(year, monthOfYear, d)
                    ChronoUnit.DAYS.between(today, date).toInt() to date
                } catch (e: Exception) { -999 to today }
            }
            val thisYear = today.year
            val d1 = dayOfMonth.coerceAtMost(LocalDate.of(thisYear, monthOfYear, 1).lengthOfMonth())
            var candidate = LocalDate.of(thisYear, monthOfYear, d1)
            if (candidate.isBefore(today)) {
                val ny = thisYear + 1
                val d2 = dayOfMonth.coerceAtMost(LocalDate.of(ny, monthOfYear, 1).lengthOfMonth())
                candidate = LocalDate.of(ny, monthOfYear, d2)
            }
            return ChronoUnit.DAYS.between(today, candidate).toInt() to candidate
        }

        fun toUi(event: EventEntity): EventUi {
            val (days, date) = daysUntilNextOccurrence(event.dayOfMonth, event.monthOfYear, event.year)
            // Minimo 1 mes para no dividir por cero
            val monthsUntil = maxOf(1, (days / 30.0).toInt())

            if (!event.isFinancialType || event.totalAmountCents == null || event.totalAmountCents <= 0L) {
                return EventUi(entity = event, daysUntil = days, nextDate = date)
            }

            val target   = targetCents(event)!!   // totalAmountCents * coveragePercent / 100
            val pending  = (target - event.accumulatedCents).coerceAtLeast(0L)
            val monthly  = if (monthsUntil > 0) pending / monthsUntil else pending
            val progress = if (target > 0L)
                ((event.accumulatedCents.toFloat() / target.toFloat()) * 100).toInt().coerceIn(0, 100)
            else 0

            return EventUi(
                entity       = event,
                daysUntil    = days,
                nextDate     = date,
                monthsUntil  = monthsUntil,
                targetCents  = target,
                pendingCents = pending,
                monthlySaving = monthly,
                progressPct  = progress
            )
        }
    }

    private fun toUi(event: EventEntity): EventUi = Companion.toUi(event)
}
