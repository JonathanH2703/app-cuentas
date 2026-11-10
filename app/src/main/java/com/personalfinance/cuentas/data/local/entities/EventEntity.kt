package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EventType {
    BIRTHDAY,             // Cumpleaños — solo recordatorio
    ANNIVERSARY,          // Aniversario — solo recordatorio
    CUSTOM,               // Fecha personalizada — solo recordatorio
    OBLIGATION_ANNUAL,    // Obligación anual con costo: SOAT, tecnomecánica, impuesto predial
    OBLIGATION_SPORADIC   // Obligación eventual con costo: matrícula, viaje, arreglo grande
}

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "type")
    val type: EventType = EventType.CUSTOM,

    /** Día del mes (1-31) */
    @ColumnInfo(name = "day_of_month")
    val dayOfMonth: Int,

    /** Mes del año (1-12) */
    @ColumnInfo(name = "month_of_year")
    val monthOfYear: Int,

    /**
     * Año del evento. Si es null = repetitivo anual (cumpleaños, aniversario).
     * Si tiene valor = fecha única (ej. evento especial un año puntual).
     */
    @ColumnInfo(name = "year")
    val year: Int? = null,

    /** Nombre o apodo de la persona / descripción corta */
    @ColumnInfo(name = "person_name")
    val personName: String? = null,

    /** Días de antelación con que se envía la notificación */
    @ColumnInfo(name = "notify_days_before")
    val notifyDaysBefore: Int = 1,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    // ── Campos financieros (solo para OBLIGATION_ANNUAL / OBLIGATION_SPORADIC) ──

    /** Costo total estimado de la obligación en centavos. Null = sin implicación financiera. */
    @ColumnInfo(name = "total_amount_cents")
    val totalAmountCents: Long? = null,

    /** Cuánto has acumulado ya para esta obligación. Se incrementa manualmente. */
    @ColumnInfo(name = "accumulated_cents")
    val accumulatedCents: Long = 0L,

    /**
     * Porcentaje del costo total que el usuario quiere cubrir con el ahorro planificado.
     * Rango: 1..100. Por defecto 100 (cubrir todo).
     * Ejemplo: si totalAmountCents=$800.000 y coveragePercent=20,
     * la meta de ahorro es $160.000, y se divide entre los meses disponibles.
     */
    @ColumnInfo(name = "coverage_percent")
    val coveragePercent: Int = 100,

    /**
     * Bolsillo del dashboard al que se carga este evento.
     * "NEEDS" = Necesidades, "WANTS" = Ocio/Deseos, "SAVINGS" = Ahorro.
     * Null = sin asignacion (no aparece en ningun bolsillo del dashboard).
     * Solo relevante para eventos con totalAmountCents != null.
     */
    @ColumnInfo(name = "block_type")
    val blockType: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
