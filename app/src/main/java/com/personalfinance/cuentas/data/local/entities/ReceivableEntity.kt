package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cuenta por cobrar ("me deben"). Cada registro representa la porción de un gasto
 * o un préstamo que una contraparte debe pagarle al usuario.
 *
 * - Si [sourceTransactionId] está presente, esta deuda nace de un gasto ya registrado
 *   en el histórico (p. ej. una comida pagada completa que se dividió).
 * - Si es null, se trata de un préstamo directo sin gasto asociado.
 *
 * El presupuesto solo cuenta "la parte del usuario", por lo que la suma de las
 * porciones pendientes ligadas a una transacción se descuenta del gasto del bloque.
 */
@Entity(
    tableName = "receivables",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contact_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_transaction_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["contact_id"]),
        Index(value = ["source_transaction_id"])
    ]
)
data class ReceivableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "contact_id")
    val contactId: Long,

    @ColumnInfo(name = "source_transaction_id")
    val sourceTransactionId: Long? = null,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "owed_cents")
    val owedCents: Long,

    @ColumnInfo(name = "is_settled")
    val isSettled: Boolean = false,

    @ColumnInfo(name = "settled_at_millis")
    val settledAtMillis: Long? = null,

    @ColumnInfo(name = "settled_account_id")
    val settledAccountId: Long? = null,

    // Movimiento real (ingreso) que saldó esta cuenta por cobrar. Es el ancla del pago.
    @ColumnInfo(name = "settled_transaction_id")
    val settledTransactionId: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
