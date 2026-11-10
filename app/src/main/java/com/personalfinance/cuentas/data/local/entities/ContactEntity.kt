package com.personalfinance.cuentas.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Tipo de contraparte a la que el usuario le carga una deuda por cobrar. */
enum class ContactKind {
    PERSON, // Persona natural (amigo, familiar, colega)
    PLACE   // Sitio o comercio (negocio, tienda, entidad)
}

/**
 * Contraparte del módulo "Me deben". Representa a quien le prestamos dinero
 * o a quien le cargamos una fracción de un gasto que nosotros pagamos.
 */
@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "kind")
    val kind: ContactKind = ContactKind.PERSON,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
