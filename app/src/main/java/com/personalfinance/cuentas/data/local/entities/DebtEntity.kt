package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "principal_amount_cents")
    val principalAmountCents: Long,

    @ColumnInfo(name = "current_balance_cents")
    val currentBalanceCents: Long,

    @ColumnInfo(name = "interest_rate_ea")
    val interestRateEa: Double,

    @ColumnInfo(name = "monthly_installment_cents")
    val monthlyInstallmentCents: Long,

    @ColumnInfo(name = "due_day_of_month")
    val dueDayOfMonth: Int,

    @ColumnInfo(name = "start_date_millis")
    val startDateMillis: Long = System.currentTimeMillis(),

    // Próxima cuota pendiente. Si es null se deriva del día del mes (dueDayOfMonth).
    // Se actualiza al registrar un pago para que la cuota ya pagada no se vuelva a proyectar.
    @ColumnInfo(name = "next_due_date_millis")
    val nextDueDateMillis: Long? = null,

    @ColumnInfo(name = "monthly_insurance_cents")
    val monthlyInsuranceCents: Long = 0L,

    @ColumnInfo(name = "is_settled")
    val isSettled: Boolean = false
)
