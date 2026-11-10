package com.personalfinance.cuentas.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity

data class CreditCardWithAccount(
    @Embedded
    val card: CreditCardEntity,

    @Relation(
        parentColumn = "account_id",
        entityColumn = "id"
    )
    val account: AccountEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "card_id"
    )
    val purchases: List<CardPurchaseEntity> = emptyList()
)
