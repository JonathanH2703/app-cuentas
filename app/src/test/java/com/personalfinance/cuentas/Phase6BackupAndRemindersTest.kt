package com.personalfinance.cuentas

import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.AccountType
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.AllocationModelEntity
import com.personalfinance.cuentas.data.local.entities.BlockType
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import com.personalfinance.cuentas.domain.backup.BackupCryptoEngine
import com.personalfinance.cuentas.domain.backup.BackupJsonSerializer
import com.personalfinance.cuentas.domain.backup.BackupPayload
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.nio.charset.StandardCharsets

class Phase6BackupAndRemindersTest {

    @Test
    fun testBackupPayloadJsonSerializationRoundTrip() {
        val sampleAccount1 = AccountEntity(
            id = 1L,
            name = "Bancolombia Ahorros",
            type = AccountType.SAVINGS,
            currentBalanceCents = 2_500_000_00L, // $2.500.000 COP
            colorHex = "#00C853",
            iconName = "account_balance"
        )
        val sampleAccount2 = AccountEntity(
            id = 2L,
            name = "RappiCard Crédito",
            type = AccountType.CHECKING,
            currentBalanceCents = 0L,
            colorHex = "#FF441F",
            iconName = "credit_card"
        )

        val sampleCategory = CategoryEntity(
            id = 10L,
            name = "Supermercado",
            iconName = "shopping_cart",
            colorHex = "#4CAF50",
            defaultBlockType = BlockType.NEEDS,
            isSystem = true
        )

        val sampleModel = AllocationModelEntity(
            id = 1L,
            name = "Regla 50/30/20",
            isActive = true
        )

        val sampleBlock = AllocationBlockEntity(
            id = 100L,
            modelId = 1L,
            name = "Necesidades Básicas",
            percentage = 50,
            type = BlockType.NEEDS,
            colorHex = "#2E7D32"
        )

        val sampleTx = TransactionEntity(
            id = 500L,
            accountId = 1L,
            categoryId = 10L,
            blockId = 100L,
            amountCents = 145_000_00L, // $145.000 COP
            type = TransactionType.EXPENSE,
            dateMillis = 1729800000000L,
            note = "Mercado quincenal en Éxito",
            merchant = "Almacenes Éxito"
        )

        val sampleCreditCard = CreditCardEntity(
            id = 1L,
            accountId = 2L,
            creditLimitCents = 5_000_000_00L, // $5.000.000 COP
            cutOffDay = 15,
            paymentDueDay = 5,
            interestRateEa = 27.9,
            lastFourDigits = "4321"
        )

        val samplePurchase = CardPurchaseEntity(
            id = 10L,
            cardId = 1L,
            merchant = "Apple Store Colombia",
            purchaseDateMillis = 1729800000000L,
            totalAmountCents = 3_200_000_00L,
            installmentsTotal = 6,
            installmentsPaid = 1,
            interestRateNm = 2.1,
            isFullyPaid = false
        )

        val sampleDebt = DebtEntity(
            id = 20L,
            name = "Crédito Vehículo",
            principalAmountCents = 25_000_000_00L,
            currentBalanceCents = 18_450_000_00L,
            interestRateEa = 16.5,
            monthlyInstallmentCents = 820_000_00L,
            dueDayOfMonth = 18,
            isSettled = false
        )

        val originalPayload = BackupPayload(
            schemaVersion = 1,
            exportedAtMillis = 1729850000000L,
            appVersion = "1.0.0",
            accounts = listOf(sampleAccount1, sampleAccount2),
            categories = listOf(sampleCategory),
            allocationModels = listOf(sampleModel),
            allocationBlocks = listOf(sampleBlock),
            transactions = listOf(sampleTx),
            creditCards = listOf(sampleCreditCard),
            cardPurchases = listOf(samplePurchase),
            debts = listOf(sampleDebt)
        )

        // Serializar
        val jsonString = BackupJsonSerializer.serialize(originalPayload)
        assertNotNull(jsonString)
        assertTrue(jsonString.contains("Bancolombia Ahorros"))
        assertTrue(jsonString.contains("RappiCard Crédito"))
        assertTrue(jsonString.contains("Apple Store Colombia"))
        assertTrue(jsonString.contains("500000000"))

        // Deserializar
        val restoredPayload = BackupJsonSerializer.deserialize(jsonString)
        assertEquals(1, restoredPayload.schemaVersion)
        assertEquals(1729850000000L, restoredPayload.exportedAtMillis)
        assertEquals(2, restoredPayload.accounts.size)
        assertEquals("Bancolombia Ahorros", restoredPayload.accounts[0].name)
        assertEquals(2_500_000_00L, restoredPayload.accounts[0].currentBalanceCents)
        assertEquals("RappiCard Crédito", restoredPayload.accounts[1].name)

        assertEquals(1, restoredPayload.categories.size)
        assertEquals("Supermercado", restoredPayload.categories[0].name)

        assertEquals(1, restoredPayload.transactions.size)
        assertEquals(145_000_00L, restoredPayload.transactions[0].amountCents)
        assertEquals("Almacenes Éxito", restoredPayload.transactions[0].merchant)

        assertEquals(1, restoredPayload.creditCards.size)
        assertEquals(15, restoredPayload.creditCards[0].cutOffDay)
        assertEquals(5, restoredPayload.creditCards[0].paymentDueDay)

        assertEquals(1, restoredPayload.cardPurchases.size)
        assertEquals(6, restoredPayload.cardPurchases[0].installmentsTotal)

        assertEquals(1, restoredPayload.debts.size)
        assertEquals(18_450_000_00L, restoredPayload.debts[0].currentBalanceCents)
    }

    @Test
    fun testAes256GcmEncryptionAndDecryptionRoundTrip() {
        val sensitiveData = "{\"cuentas\":[{\"nombre\":\"RappiCard\",\"cupo\":500000000}]}".toByteArray(StandardCharsets.UTF_8)
        val password = "MiClaveSeguraBancaria2026*#".toCharArray()

        // 1. Cifrar
        val encryptedBytes = BackupCryptoEngine.encrypt(sensitiveData, password)
        assertNotNull(encryptedBytes)
        assertTrue(encryptedBytes.size > sensitiveData.size)
        assertTrue(BackupCryptoEngine.isEncrypted(encryptedBytes))

        // 2. Descifrar con la contraseña correcta
        val decryptedBytes = BackupCryptoEngine.decrypt(encryptedBytes, password)
        assertArrayEquals(sensitiveData, decryptedBytes)
    }

    @Test
    fun testAes256GcmRejectsIncorrectPassword() {
        val sensitiveData = "DATOS_FINANCIEROS_SECRETOS".toByteArray(StandardCharsets.UTF_8)
        val correctPassword = "PasswordCorrecta123".toCharArray()
        val wrongPassword = "PasswordErronea999".toCharArray()

        val encryptedBytes = BackupCryptoEngine.encrypt(sensitiveData, correctPassword)

        try {
            BackupCryptoEngine.decrypt(encryptedBytes, wrongPassword)
            fail("Debió lanzar excepción por clave errónea o autenticación fallida")
        } catch (e: Exception) {
            // Éxito: la autenticación criptográfica GCM rechazó la clave equivocada
            assertNotNull(e)
        }
    }

    @Test
    fun testAes256GcmRejectsTamperedData() {
        val sensitiveData = "INFORMACION_INTEGRA".toByteArray(StandardCharsets.UTF_8)
        val password = "Password123".toCharArray()

        val encryptedBytes = BackupCryptoEngine.encrypt(sensitiveData, password)

        // Alterar un byte del cuerpo cifrado
        encryptedBytes[encryptedBytes.size - 5] = (encryptedBytes[encryptedBytes.size - 5].toInt() xor 0xFF).toByte()

        try {
            BackupCryptoEngine.decrypt(encryptedBytes, password)
            fail("Debió fallar la verificación de autenticación de tag GCM")
        } catch (e: Exception) {
            // Éxito: GCM detectó alteración en el mensaje
            assertNotNull(e)
        }
    }

    @Test
    fun testIsEncryptedHeaderValidation() {
        val fakePlainJson = "{\"schema_version\":1}".toByteArray(StandardCharsets.UTF_8)
        assertFalse(BackupCryptoEngine.isEncrypted(fakePlainJson))

        val randomGarbage = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        assertFalse(BackupCryptoEngine.isEncrypted(randomGarbage))
    }

    @Test
    fun testReminderDaysUntilCalculation() {
        fun calculateDaysUntil(currentDay: Int, targetDay: Int, maxDaysInMonth: Int): Int {
            val normalizedTarget = targetDay.coerceAtMost(maxDaysInMonth)
            return if (normalizedTarget >= currentDay) {
                normalizedTarget - currentDay
            } else {
                (maxDaysInMonth - currentDay) + normalizedTarget
            }
        }

        // Mismo día
        assertEquals(0, calculateDaysUntil(currentDay = 15, targetDay = 15, maxDaysInMonth = 31))

        // Corte en 2 días (ej: hoy es 13, corte es 15)
        assertEquals(2, calculateDaysUntil(currentDay = 13, targetDay = 15, maxDaysInMonth = 31))

        // Pago en 5 días
        assertEquals(5, calculateDaysUntil(currentDay = 10, targetDay = 15, maxDaysInMonth = 30))

        // Salto de fin de mes: hoy es 29 de octubre, corte es el 3 de noviembre (31 días en octubre)
        // Días restantes en octubre (31 - 29 = 2) + 3 = 5 días
        assertEquals(5, calculateDaysUntil(currentDay = 29, targetDay = 3, maxDaysInMonth = 31))

        // Normalización si el mes tiene 28 días y el día configurado es 31
        assertEquals(0, calculateDaysUntil(currentDay = 28, targetDay = 31, maxDaysInMonth = 28))
    }
}
