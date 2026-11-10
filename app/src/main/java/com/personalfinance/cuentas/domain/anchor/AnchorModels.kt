package com.personalfinance.cuentas.domain.anchor

import kotlin.math.abs

/** Tipo de obligación a la que se puede anclar una transacción. */
enum class AnchorType { DEBT, RECURRING }

/**
 * Obligación disponible para anclar un movimiento.
 * [expectedCents] es el monto esperado (cuota mensual del crédito o valor del gasto fijo).
 */
data class AnchorOption(
    val type: AnchorType,
    val id: Long,
    val title: String,
    val expectedCents: Long,
    val subtitle: String
)

/**
 * Recomienda a qué obligación corresponde un movimiento comparando el monto
 * registrado con el monto esperado de cada obligación.
 */
object AnchorRecommender {

    /** Puntaje mínimo (0..100) para considerar que un movimiento "encaja" con la obligación. */
    const val MIN_RECOMMEND_SCORE = 80

    /** 0..100 según qué tan parecido es el monto al esperado de la obligación. */
    fun score(option: AnchorOption, amountCents: Long): Int {
        if (amountCents <= 0L || option.expectedCents <= 0L) return 0
        val diff = abs(amountCents - option.expectedCents)
        val ratio = diff.toDouble() / option.expectedCents.toDouble()
        return ((1.0 - ratio) * 100).toInt().coerceIn(0, 100)
    }

    /** Opciones ordenadas por parecido al monto; las más probables primero. */
    fun rank(options: List<AnchorOption>, amountCents: Long): List<AnchorOption> =
        if (amountCents <= 0L) options else options.sortedByDescending { score(it, amountCents) }

    fun isRecommended(option: AnchorOption, amountCents: Long): Boolean =
        score(option, amountCents) >= MIN_RECOMMEND_SCORE
}
