package com.personalfinance.cuentas.domain.schedule

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Utilidades para trabajar con fechas de vencimiento de obligaciones recurrentes
 * (cuotas de crédito y gastos fijos) usando siempre la zona horaria local del dispositivo.
 */
object DueDates {

    fun toLocalDate(millis: Long): LocalDate =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()

    fun toMillis(date: LocalDate): Long =
        date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    /**
     * Dado un día del mes, devuelve la próxima fecha en que cae a partir de hoy.
     * Si el día ya pasó este mes, salta al siguiente.
     */
    fun nextOccurrence(dayOfMonth: Int, today: LocalDate): LocalDate {
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
     * Próxima cuota pendiente de una deuda: usa la fecha guardada si sigue vigente,
     * o la deriva del día del mes configurado.
     */
    fun effectiveDebtDueDate(dueDayOfMonth: Int, storedMillis: Long?, today: LocalDate): LocalDate {
        if (storedMillis != null) {
            val stored = toLocalDate(storedMillis)
            if (!stored.isBefore(today)) return stored
            return nextOccurrence(stored.dayOfMonth, today)
        }
        return nextOccurrence(dueDayOfMonth, today)
    }

    /** Desplaza una fecha el número de meses indicado respetando el fin de mes. */
    fun shiftMonths(date: LocalDate, months: Long): LocalDate {
        val target = date.plusMonths(months)
        val day = minOf(date.dayOfMonth, target.lengthOfMonth())
        return target.withDayOfMonth(day)
    }
}
