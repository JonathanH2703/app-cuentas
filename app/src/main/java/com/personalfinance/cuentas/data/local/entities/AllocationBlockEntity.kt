package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class BlockType {
    NEEDS,    // Necesidades básicas / Gastos fijos esenciales
    DEBTS,    // Cuotas de créditos y tarjetas
    SAVINGS,  // Metas de ahorro y fondo de emergencia
    WANTS,    // Ocio, deseos, salidas, compras personales
    CUSTOM    // Bloque libre configurado por el usuario
}

@Entity(
    tableName = "allocation_blocks",
    foreignKeys = [
        ForeignKey(
            entity = AllocationModelEntity::class,
            parentColumns = ["id"],
            childColumns = ["model_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["model_id"])
    ]
)
data class AllocationBlockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "model_id")
    val modelId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "percentage")
    val percentage: Int, // e.g. 50, 30, 20

    @ColumnInfo(name = "type")
    val type: BlockType,

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#2E7D32",

    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0,

    @ColumnInfo(name = "absorb_remainder")
    val absorbRemainder: Boolean = false // Si recibe el centavo sobrante de redondeo
)
