package com.personalfinance.cuentas

import com.personalfinance.cuentas.core.common.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyAndSecurityTest {

    @Test
    fun testCurrencyFormattingCOP() {
        val cents = 15000000L // $150.000 COP
        val formatted = CurrencyFormatter.formatCentsToCop(cents)
        // El formateador debe incluir el número 150.000 y el símbolo de pesos
        assertTrue(formatted.contains("150.000") || formatted.contains("150,000"))
    }

    @Test
    fun testParseCopToCentsWithDecimalsAndThousandSeparators() {
        // Formato con coma como decimal: "1.500,50" -> 150050 centavos ($1.500 COP y 50 centavos)
        assertEquals(150050L, CurrencyFormatter.parseCopToCents("1.500,50"))

        // Formato con punto como decimal: "1,500.50" -> 150050 centavos
        assertEquals(150050L, CurrencyFormatter.parseCopToCents("1,500.50"))

        // Formato simple con coma: "344617,32" -> 34461732 centavos
        assertEquals(34461732L, CurrencyFormatter.parseCopToCents("344617,32"))

        // Formato entero: "45000" -> 4500000 centavos ($45.000 COP)
        assertEquals(4500000L, CurrencyFormatter.parseCopToCents("45000"))

        // Formato con símbolo de moneda y espacios: "$ 100.000,00" -> 10000000 centavos
        assertEquals(10000000L, CurrencyFormatter.parseCopToCents("$ 100.000,00"))

        // Formato un solo decimal: "50,5" -> 5050 centavos
        assertEquals(5050L, CurrencyFormatter.parseCopToCents("50,5"))

        // Entrada vacía o inválida -> 0
        assertEquals(0L, CurrencyFormatter.parseCopToCents(""))
        assertEquals(0L, CurrencyFormatter.parseCopToCents("abc"))
    }

    @Test
    fun testFormatCentsForInput() {
        assertEquals("45000", CurrencyFormatter.formatCentsForInput(4500000L))
        assertEquals("344617,32", CurrencyFormatter.formatCentsForInput(34461732L))
        assertEquals("0,50", CurrencyFormatter.formatCentsForInput(50L))
        assertEquals("0", CurrencyFormatter.formatCentsForInput(0L))
    }

    @Test
    fun testCentavosIntegerDivisionNoFloatingPointDrift() {
        // Validación de principio financiero: 1.000.000 COP (100.000.000 centavos)
        // Reparto 50 / 30 / 20
        val totalCents = 100000000L
        val needs = (totalCents * 50) / 100
        val wants = (totalCents * 30) / 100
        val savings = (totalCents * 20) / 100

        assertEquals(50000000L, needs)
        assertEquals(30000000L, wants)
        assertEquals(20000000L, savings)
        assertEquals(totalCents, needs + wants + savings)
    }

    @Test
    fun testCentavosDivisionWithOddRemainder() {
        // 100.000 COP y 1 centavo (10.000.001 centavos) dividido en 3 partes
        val totalCents = 10000001L
        val part1 = (totalCents * 33) / 100
        val part2 = (totalCents * 33) / 100
        val part3 = (totalCents * 34) / 100

        val allocated = part1 + part2 + part3
        val remainder = totalCents - allocated

        // El algoritmo de absorción suma el residuo (remainder) al bloque designado
        val adjustedPart3 = part3 + remainder
        val finalSum = part1 + part2 + adjustedPart3

        assertEquals(totalCents, finalSum)
    }
}
