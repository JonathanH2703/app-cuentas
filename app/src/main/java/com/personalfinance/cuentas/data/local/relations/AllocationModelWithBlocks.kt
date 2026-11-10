package com.personalfinance.cuentas.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.AllocationModelEntity

data class AllocationModelWithBlocks(
    @Embedded
    val model: AllocationModelEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "model_id"
    )
    val blocks: List<AllocationBlockEntity> = emptyList()
)
