package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    EXPENSE,        // Gasto normal
    INCOME,         // Ingreso directo
    TRANSFER_OUT,   // Salida por transferencia entre cuentas
    TRANSFER_IN,    // Entrada por transferencia entre cuentas
    ADJUSTMENT      // Ajuste manual de saldo
}

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AllocationBlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["block_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["date_millis"]),
        Index(value = ["account_id", "date_millis"]),
        Index(value = ["category_id"]),
        Index(value = ["block_id", "date_millis"]),
        Index(value = ["linked_debt_id"]),
        Index(value = ["linked_recurring_item_id"]),
        Index(value = ["linked_receivable_id"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "account_id")
    val accountId: Long,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "block_id")
    val blockId: Long? = null,

    @ColumnInfo(name = "amount_cents")
    val amountCents: Long,

    @ColumnInfo(name = "type")
    val type: TransactionType = TransactionType.EXPENSE,

    @ColumnInfo(name = "date_millis")
    val dateMillis: Long,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "merchant")
    val merchant: String? = null,

    // Vínculo con la obligación que esta transacción cumple (deuda o gasto fijo mensual)
    @ColumnInfo(name = "linked_debt_id")
    val linkedDebtId: Long? = null,

    @ColumnInfo(name = "linked_recurring_item_id")
    val linkedRecurringItemId: Long? = null,

    // Vínculo con la cuenta por cobrar ("Me deben") que este movimiento salda.
    // Permite que un ingreso real (capturado por notificación/SMS o registrado a mano)
    // quede anclado a la deuda por cobrar correspondiente.
    @ColumnInfo(name = "linked_receivable_id")
    val linkedReceivableId: Long? = null,

    @ColumnInfo(name = "is_leisure")
    val isLeisure: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
