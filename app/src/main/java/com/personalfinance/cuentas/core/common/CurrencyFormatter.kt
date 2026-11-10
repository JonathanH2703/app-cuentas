package com.personalfinance.cuentas.core.common

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong

object CurrencyFormatter {

    private val colombianLocale = Locale("es", "CO")

    /**
     * Formatea centavos a representación monetaria en Pesos Colombianos (COP)
     * Ej: 15000000L -> "$ 150.000"
     *     34461732L -> "$ 344.617,32" (muestra centavos automáticamente si existen)
     */
    fun formatCentsToCop(cents: Long, forceDecimals: Boolean? = null): String {
        val amount = cents / 100.0
        val hasCents = (cents % 100L != 0L)
        val showDecimals = forceDecimals ?: hasCents

        val formatter = NumberFormat.getCurrencyInstance(colombianLocale).apply {
            maximumFractionDigits = if (showDecimals) 2 else 0
            minimumFractionDigits = if (showDecimals) 2 else 0
        }
        return formatter.format(amount)
    }

    /**
     * Parsea un texto ingresado por el usuario en Pesos a centavos (Long).
     * Soporta tanto decimales con punto como con coma:
     * "1000" -> 100000L
     * "1000.50" -> 100050L
     * "1000,50" -> 100050L
     * "1.003.689,32" -> 100368932L
     */
    fun parseCopToCents(rawInput: String): Long {
        if (rawInput.isBlank()) return 0L

        // Limpiar símbolos de moneda y espacios
        var cleaned = rawInput.trim()
            .replace("$", "")
            .replace("COP", "", ignoreCase = true)
            .replace(" ", "")

        if (cleaned.isBlank()) return 0L

        val lastDot = cleaned.lastIndexOf('.')
        val lastComma = cleaned.lastIndexOf(',')

        if (lastDot != -1 && lastComma != -1) {
            if (lastComma > lastDot) {
                // Formato colombiano: "1.003.689,32" -> punto es miles, coma es decimal
                cleaned = cleaned.replace(".", "").replace(",", ".")
            } else {
                // Formato internacional: "1,003,689.32" -> coma es miles, punto es decimal
                cleaned = cleaned.replace(",", "")
            }
        } else if (lastComma != -1) {
            // Solo coma: "344617,32" o "1,000,000"
            val parts = cleaned.split(",")
            if (parts.size == 2 && parts[1].length in 1..2) {
                // Es decimal: 344617,32
                cleaned = cleaned.replace(",", ".")
            } else if (parts.size > 1 && parts.last().length == 3) {
                // Es miles: 1,000,000
                cleaned = cleaned.replace(",", "")
            } else {
                cleaned = cleaned.replace(",", ".")
            }
        } else if (lastDot != -1) {
            // Solo punto: "344617.32" o "1.000.000"
            val parts = cleaned.split(".")
            if (parts.size == 2 && parts[1].length in 1..2) {
                // Es decimal: 344617.32
            } else {
                // Es miles: 1.000.000
                cleaned = cleaned.replace(".", "")
            }
        }

        return try {
            val amountDouble = cleaned.toDouble()
            (amountDouble * 100.0).roundToLong()
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Retorna el texto para inicializar campos de edición.
     * Si tiene centavos retorna con coma decimal colombiana ("344617,32"), de lo contrario entero ("45000").
     */
    fun formatCentsForInput(cents: Long): String {
        return if (cents % 100L != 0L) {
            String.format(colombianLocale, "%.2f", cents / 100.0)
        } else {
            (cents / 100L).toString()
        }
    }
}
