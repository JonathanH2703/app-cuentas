package com.personalfinance.cuentas.domain.capture

import com.personalfinance.cuentas.data.local.entities.PendingSource
import com.personalfinance.cuentas.data.local.entities.TransactionType

enum class BankOrigin(val displayName: String, val brandColorHex: String) {
    BANCOLOMBIA("Bancolombia", "#FFDD00"),
    NEQUI("Nequi", "#FF007A"),
    DAVIPLATA("Daviplata", "#ED1C24"),
    NU_COLOMBIA("Nu Colombia", "#820AD1"),
    RAPPICARD("RappiCard / RappiPay", "#FF441F"),
    SCOTIABANK_COLPATRIA("Scotiabank Colpatria", "#ED1C24"),
    BBVA("BBVA Colombia", "#004481"),
    DALE("Dale!", "#00B4D8"),
    BANCO_DE_BOGOTA("Banco de Bogotá", "#002D72"),
    GENERIC_COLOMBIAN_BANK("Banco Nacional", "#006C4C")
}

data class ParsedBankTransaction(
    val source: PendingSource,
    val bankOrigin: BankOrigin,
    val amountCents: Long,
    val merchant: String,
    val type: TransactionType,
    val rawText: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val accountFilterHint: String? = null,
    // Verdadero cuando el aviso corresponde al pago de una tarjeta de crédito (ej. PSE).
    // En ese caso NO se debita una cuenta ni se crea un gasto: baja el cupo usado de la tarjeta.
    val isCardPayment: Boolean = false,
    // Últimos 4 dígitos de la tarjeta detectados en el texto (si aplica), para mapear a la tarjeta.
    val cardLastFour: String? = null,
    // Verdadero cuando el aviso corresponde al pago de un crédito/préstamo (ej. PSE).
    // En ese caso NO se debita una cuenta ni se crea un gasto: reduce el saldo del crédito.
    val isDebtPayment: Boolean = false,
    // Pista de texto del producto de crédito (ej. "credito de consumo") para mapear al crédito.
    val debtHint: String? = null
)
