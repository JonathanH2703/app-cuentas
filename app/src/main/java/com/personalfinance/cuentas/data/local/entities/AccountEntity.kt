package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.roundToLong

enum class AccountType {
    CASH,             // Efectivo
    SAVINGS,          // Cuenta de Ahorros
    CHECKING,         // Cuenta Corriente
    DIGITAL_WALLET,   // Nequi, Daviplata, Dale, etc.
    INVESTMENT        // Inversión / Fiduciaria
}

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: AccountType,

    @ColumnInfo(name = "current_balance_cents")
    val currentBalanceCents: Long,

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#006C4C",

    @ColumnInfo(name = "icon_name")
    val iconName: String = "account_balance",

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "include_in_total")
    val includeInTotal: Boolean = true,

    @ColumnInfo(name = "is_exempt_from_gmf")
    val isExemptFromGmf: Boolean = false, // Exenta de 4x1000

    @ColumnInfo(name = "annual_yield_rate")
    val annualYieldRate: Double? = null, // Rendimiento / Rentabilidad anual % E.A. (ej. 13.0)

    @ColumnInfo(name = "is_static_saving")
    val isStaticSaving: Boolean = false, // Dinero estático / Ahorro intocable (excluido de Disponible para gastar)

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Monto real neto disponible para retirar/transferir considerando el 4x1000.
     * En Colombia el 4x1000 equivale al 0.4% del valor de la transacción.
     */
    val netBalanceAfterGmfCents: Long
        get() = if (isExemptFromGmf || currentBalanceCents <= 0L) {
            currentBalanceCents
        } else {
            val gmfTax = (currentBalanceCents.toDouble() * 0.004).roundToLong()
            currentBalanceCents - gmfTax
        }

    /**
     * Retención estimada del 4x1000 si se retirara la totalidad del saldo
     */
    val gmfTaxEstimatedCents: Long
        get() = if (isExemptFromGmf || currentBalanceCents <= 0L) {
            0L
        } else {
            (currentBalanceCents.toDouble() * 0.004).roundToLong()
        }
}
