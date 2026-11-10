package com.personalfinance.cuentas

import com.personalfinance.cuentas.data.local.entities.PendingSource
import com.personalfinance.cuentas.data.local.entities.TransactionType
import com.personalfinance.cuentas.domain.capture.BankOrigin
import com.personalfinance.cuentas.domain.capture.ColombianBankParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase5BankCaptureAndDeduplicationTest {

    @Test
    fun testBancolombiaSmsPurchaseParsing() {
        val rawSms = "Bancolombia le informa compra por $45.000,00 en D1 CALLE 50 14:20. Inquietudes al 018000912345"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "890001",
            rawText = rawSms,
            source = PendingSource.SMS
        )

        assertNotNull(parsed)
        assertEquals(BankOrigin.BANCOLOMBIA, parsed?.bankOrigin)
        assertEquals(4500000L, parsed?.amountCents) // $45.000 COP en centavos
        assertTrue(parsed?.merchant?.contains("D1 CALLE 50", ignoreCase = true) == true)
        assertEquals(TransactionType.EXPENSE, parsed?.type)
    }

    @Test
    fun testBancolombiaIncomeParsing() {
        val rawNotification = "Bancolombia: Recibio transferencia de JUAN PEREZ por $200.000"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "com.bancolombia.personas",
            rawText = rawNotification,
            source = PendingSource.NOTIFICATION
        )

        assertNotNull(parsed)
        assertEquals(BankOrigin.BANCOLOMBIA, parsed?.bankOrigin)
        assertEquals(20000000L, parsed?.amountCents) // $200.000 COP en centavos
        assertEquals(TransactionType.INCOME, parsed?.type)
    }

    @Test
    fun testNequiPaymentParsing() {
        val rawNotification = "¡Pagaste $25.000 en TIENDA ARA con tu Nequi! Tu nuevo saldo es $150.000"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "com.nequi.MobileApp",
            rawText = rawNotification,
            source = PendingSource.NOTIFICATION
        )

        assertNotNull(parsed)
        assertEquals(BankOrigin.NEQUI, parsed?.bankOrigin)
        assertEquals(2500000L, parsed?.amountCents) // $25.000 COP en centavos
        assertTrue(parsed?.merchant?.contains("TIENDA ARA", ignoreCase = true) == true)
        assertEquals(TransactionType.EXPENSE, parsed?.type)
    }

    @Test
    fun testNequiIncomeParsing() {
        val rawSms = "¡Te transfirieron $180.000! Maria te envio plata"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "85954",
            rawText = rawSms,
            source = PendingSource.SMS
        )

        assertNotNull(parsed)
        assertEquals(BankOrigin.NEQUI, parsed?.bankOrigin)
        assertEquals(18000000L, parsed?.amountCents) // $180.000 COP en centavos
        assertEquals(TransactionType.INCOME, parsed?.type)
    }

    @Test
    fun testDaviplataPurchaseParsing() {
        val rawSms = "DaviPlata: Compra por $48.900 en DROGAS LA REBAJA"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "85888",
            rawText = rawSms,
            source = PendingSource.SMS
        )

        assertNotNull(parsed)
        assertEquals(BankOrigin.DAVIPLATA, parsed?.bankOrigin)
        assertEquals(4890000L, parsed?.amountCents) // $48.900 COP en centavos
        assertTrue(parsed?.merchant?.contains("DROGAS LA REBAJA", ignoreCase = true) == true)
        assertEquals(TransactionType.EXPENSE, parsed?.type)
    }

    @Test
    fun testNuColombiaPurchaseParsing() {
        val rawNotification = "Compra aprobada en UBER por $18.500"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "com.nu.production",
            rawText = rawNotification,
            source = PendingSource.NOTIFICATION
        )

        assertNotNull(parsed)
        assertEquals(BankOrigin.NU_COLOMBIA, parsed?.bankOrigin)
        assertEquals(1850000L, parsed?.amountCents) // $18.500 COP en centavos
        assertTrue(parsed?.merchant?.contains("UBER", ignoreCase = true) == true)
        assertEquals(TransactionType.EXPENSE, parsed?.type)
    }

    @Test
    fun testRappiCardPurchaseParsing() {
        val rawNotification = "Tu compra con RappiCard en NETFLIX por $32.900 fue aprobada"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "com.rappi.store",
            rawText = rawNotification,
            source = PendingSource.NOTIFICATION
        )

        assertNotNull(parsed)
        assertEquals(BankOrigin.RAPPICARD, parsed?.bankOrigin)
        assertEquals(3290000L, parsed?.amountCents) // $32.900 COP en centavos
        assertTrue(parsed?.merchant?.contains("NETFLIX", ignoreCase = true) == true)
        assertEquals(TransactionType.EXPENSE, parsed?.type)
    }

    @Test
    fun testRappiCardSmsParsing() {
        val rawSms = "RappiCard: Compra por $45.000 en D1 CALLE 50"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "RappiCard",
            rawText = rawSms,
            source = PendingSource.SMS
        )

        assertNotNull(parsed)
        assertEquals(BankOrigin.RAPPICARD, parsed?.bankOrigin)
        assertEquals(4500000L, parsed?.amountCents) // $45.000 COP en centavos
        assertTrue(parsed?.merchant?.contains("D1 CALLE 50", ignoreCase = true) == true)
        assertEquals(TransactionType.EXPENSE, parsed?.type)
    }

    @Test
    fun testEmailNotificationParsing() {
        // Alerta bancaria que llega por correo (Gmail o Outlook)
        val rawGmailNotification = "Bancolombia - Notificación de compra: Compra por $60.000 en EXITO con su T.Cred *9876"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "com.google.android.gm",
            rawText = rawGmailNotification,
            source = PendingSource.NOTIFICATION
        )

        assertNotNull(parsed)
        assertEquals(PendingSource.EMAIL_NOTIFICATION, parsed?.source)
        assertEquals(BankOrigin.BANCOLOMBIA, parsed?.bankOrigin)
        assertEquals(6000000L, parsed?.amountCents)
        assertTrue(parsed?.merchant?.contains("EXITO", ignoreCase = true) == true)
        assertEquals(TransactionType.EXPENSE, parsed?.type)
    }

    @Test
    fun testRappiCardEmailNotificationParsing() {
        // Alerta de RappiCard que llega por correo en Outlook
        val rawOutlookNotification = "RappiCard: Tu compra en UBER por $22.500 fue aprobada"
        val parsed = ColombianBankParser.parse(
            senderOrPackage = "com.microsoft.office.outlook",
            rawText = rawOutlookNotification,
            source = PendingSource.NOTIFICATION
        )

        assertNotNull(parsed)
        assertEquals(PendingSource.EMAIL_NOTIFICATION, parsed?.source)
        assertEquals(BankOrigin.RAPPICARD, parsed?.bankOrigin)
        assertEquals(2250000L, parsed?.amountCents)
        assertTrue(parsed?.merchant?.contains("UBER", ignoreCase = true) == true)
        assertEquals(TransactionType.EXPENSE, parsed?.type)
    }

    @Test
    fun testRappiCardMultiChannelParsingGeneratesIdenticalKey() {
        val now = System.currentTimeMillis()

        // 1. Notificación de la app de Rappi
        val fromApp = ColombianBankParser.parse(
            senderOrPackage = "com.rappi.store",
            rawText = "Tu compra con RappiCard en UBER por $22.500 fue aprobada",
            source = PendingSource.NOTIFICATION,
            timestampMillis = now
        )

        // 2. Alerta de correo en Gmail o Outlook
        val fromEmail = ColombianBankParser.parse(
            senderOrPackage = "com.microsoft.office.outlook",
            rawText = "RappiCard: Tu compra en UBER por $22.500 fue aprobada",
            source = PendingSource.NOTIFICATION,
            timestampMillis = now + 15000 // 15 segundos después
        )

        // 3. SMS de confirmación
        val fromSms = ColombianBankParser.parse(
            senderOrPackage = "RappiCard",
            rawText = "RappiCard: Compra por $22.500 en UBER",
            source = PendingSource.SMS,
            timestampMillis = now + 25000 // 25 segundos después
        )

        assertNotNull(fromApp)
        assertNotNull(fromEmail)
        assertNotNull(fromSms)

        // Verificamos que los 3 canales extraen los mismos datos financieros exactos
        assertEquals(fromApp?.bankOrigin, fromEmail?.bankOrigin)
        assertEquals(fromApp?.bankOrigin, fromSms?.bankOrigin)

        assertEquals(fromApp?.amountCents, fromEmail?.amountCents)
        assertEquals(fromApp?.amountCents, fromSms?.amountCents)

        assertEquals(fromApp?.merchant, fromEmail?.merchant)
        assertEquals(fromApp?.merchant, fromSms?.merchant)

        assertEquals(fromApp?.type, fromEmail?.type)
        assertEquals(fromApp?.type, fromSms?.type)
    }

    @Test
    fun testCopAmountPunctuationVariations() {
        assertEquals(1500000L, ColombianBankParser.parseCopAmountToCents("$ 15.000"))
        assertEquals(4500000L, ColombianBankParser.parseCopAmountToCents("$45.000,00"))
        assertEquals(125000050L, ColombianBankParser.parseCopAmountToCents("1.250.000,50"))
        assertEquals(15000000L, ColombianBankParser.parseCopAmountToCents("150000"))
        assertEquals(3490000L, ColombianBankParser.parseCopAmountToCents("34.900"))
    }
}
