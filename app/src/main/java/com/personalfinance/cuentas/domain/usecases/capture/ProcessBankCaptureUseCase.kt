package com.personalfinance.cuentas.domain.usecases.capture

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.personalfinance.cuentas.core.common.CurrencyFormatter
import com.personalfinance.cuentas.data.local.dao.AccountDao
import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.dao.DebtDao
import com.personalfinance.cuentas.data.local.dao.PendingTransactionDao
import com.personalfinance.cuentas.data.local.dao.TransactionDao
import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.AccountType
import com.personalfinance.cuentas.data.local.entities.BlockType
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.entities.PendingSource
import com.personalfinance.cuentas.data.local.entities.PendingStatus
import com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import com.personalfinance.cuentas.domain.capture.BankOrigin
import com.personalfinance.cuentas.domain.capture.MerchantCategorizer
import com.personalfinance.cuentas.domain.capture.ParsedBankTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

sealed class CaptureResult {
    data class Success(val transactionId: Long, val pendingId: Long, val summary: String) : CaptureResult()
    data class Duplicate(val hash: String, val summary: String) : CaptureResult()
    data class Ignored(val reason: String) : CaptureResult()
    // Pago de tarjeta detectado: queda pendiente de confirmación del usuario (no se aplica solo).
    data class CardPaymentPending(
        val pendingId: Long,
        val cardId: Long,
        val cardName: String,
        val amountCents: Long,
        val summary: String
    ) : CaptureResult()

    // Pago de crédito/préstamo detectado: queda pendiente de confirmación del usuario.
    data class DebtPaymentPending(
        val pendingId: Long,
        val debtId: Long,
        val debtName: String,
        val amountCents: Long,
        val summary: String
    ) : CaptureResult()
}

@Singleton
class ProcessBankCaptureUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pendingTransactionDao: PendingTransactionDao,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val allocationDao: AllocationDao,
    private val merchantCategorizer: MerchantCategorizer,
    private val creditCardDao: com.personalfinance.cuentas.data.local.dao.CreditCardDao,
    private val debtDao: DebtDao
) {

    companion object {
        private const val CHANNEL_ID = "channel_bank_captures"
        private const val CHANNEL_NAME = "Detección Automática de Bancos"
        
        // Caché en memoria para evitar colisiones concurrentes (ej: Gmail enviando varias veces la misma notificación)
        private val processingHashes = java.util.Collections.synchronizedSet(mutableSetOf<String>())
    }

    /**
     * Procesa una transacción detectada por SMS o Notificación bancaria:
     * 1. Genera hash de deduplicación con ventana de 10 minutos.
     * 2. Descarta duplicados (ej. SMS y Notificación del mismo cobro).
     * 3. Asigna la cuenta más adecuada según el banco detectado.
     * 4. Categoriza inteligentemente con diccionario colombiano y aprendizaje previo.
     * 5. Asigna el sobre/bolsillo correspondiente (Necesidades / Deseos / Ahorro).
     * 6. Registra el movimiento en Room y actualiza el saldo de la cuenta.
     * 7. Notifica silenciosamente al usuario en la barra de estado.
     */
    suspend fun execute(parsed: ParsedBankTransaction, isSimulation: Boolean = false): CaptureResult {
        if (parsed.amountCents <= 0L) {
            return CaptureResult.Ignored("Monto inválido o cero")
        }

        // Pago de tarjeta de crédito (ej. PSE): se maneja aparte. NO debita cuenta ni crea gasto;
        // queda pendiente de confirmación para luego bajar el cupo usado de la tarjeta.
        if (parsed.isCardPayment) {
            return handleCardPayment(parsed, isSimulation)
        }

        // Pago de crédito/préstamo (ej. PSE con Banco de Bogotá): se maneja aparte. NO debita
        // cuenta ni crea gasto; queda pendiente de confirmación para luego reducir el saldo.
        if (parsed.isDebtPayment) {
            return handleDebtPayment(parsed, isSimulation)
        }

        // 1. Calcular hash de deduplicación con cubo temporal de 10 minutos
        val timeBucket10Min = parsed.timestampMillis / (10 * 60 * 1000L)
        val deduplicationHash = generateDeduplicationHash(
            bankOrigin = parsed.bankOrigin,
            amountCents = parsed.amountCents,
            merchant = parsed.merchant,
            timeBucket = timeBucket10Min
        )

        // Verificar si ya se está procesando este hash en este momento
        if (!processingHashes.add(deduplicationHash)) {
            return CaptureResult.Duplicate(deduplicationHash, "Ya se está procesando este movimiento")
        }

        try {
            val fifteenMinutesAgo = parsed.timestampMillis - (15 * 60 * 1000L)

            if (isSimulation) {
                // En modo de prueba / simulación forzada, limpiamos cualquier captura previa idéntica
                // para permitir al usuario probar y re-probar sin bloqueos por hash
                pendingTransactionDao.deleteByDeduplicationHash(deduplicationHash)
                pendingTransactionDao.deleteSimilarCapture(parsed.amountCents, 0)
            } else {
                // 2. Comprobar si ya existe una captura idéntica en los últimos 30 minutos
                val thirtyMinutesAgo = parsed.timestampMillis - (30 * 60 * 1000L)
                val existingRecent = pendingTransactionDao.findRecentByHash(deduplicationHash, thirtyMinutesAgo)
                if (existingRecent != null) {
                    return CaptureResult.Duplicate(
                        hash = deduplicationHash,
                        summary = "Movimiento duplicado ignorado (por hash): ${parsed.merchant} (${CurrencyFormatter.formatCentsToCop(parsed.amountCents)})"
                    )
                }

                // Capa 2: Comprobar captura reciente del mismo banco y monto en los últimos 20 minutos (ej. Notificación de App + Correo simultáneo)
                val twentyMinutesAgo = parsed.timestampMillis - (20 * 60 * 1000L)
                val similarCapture = pendingTransactionDao.findRecentSimilarCapture(
                    amountCents = parsed.amountCents,
                    sender = parsed.bankOrigin.displayName,
                    sinceMillis = twentyMinutesAgo
                )
                if (similarCapture != null) {
                    return CaptureResult.Duplicate(
                        hash = deduplicationHash,
                        summary = "Movimiento duplicado ignorado (mismo banco y monto en últimos 20 min): ${parsed.merchant} (${CurrencyFormatter.formatCentsToCop(parsed.amountCents)})"
                    )
                }
            }

            // 3. Buscar o emparejar la cuenta bancaria de destino
            val activeAccounts = accountDao.getAllActiveAccountsSync()
            if (activeAccounts.isEmpty()) {
                return CaptureResult.Ignored("No existen cuentas registradas en la aplicación")
            }

            val targetAccount = matchAccountForBank(parsed.bankOrigin, activeAccounts)

            // Capa 3: Comprobar si la cuenta de destino ya tiene una transacción idéntica en los últimos 20 minutos (solo en capturas reales)
            if (!isSimulation) {
                val twentyMinutesAgo = parsed.timestampMillis - (20 * 60 * 1000L)
                val recentAccountTx = transactionDao.findRecentSimilarTransaction(
                    accountId = targetAccount.id,
                    amountCents = parsed.amountCents,
                    type = parsed.type,
                    sinceMillis = twentyMinutesAgo
                )
                if (recentAccountTx != null) {
                    return CaptureResult.Duplicate(
                        hash = deduplicationHash,
                        summary = "Movimiento duplicado ignorado (ya registrado en ${targetAccount.name}): ${parsed.merchant} (${CurrencyFormatter.formatCentsToCop(parsed.amountCents)})"
                    )
                }
            }

            // 4. Categorizar inteligentemente
            val matchedCategory = merchantCategorizer.categorizeMerchant(parsed.merchant)
            val categoryId = matchedCategory?.id ?: 1L

            // 5. Determinar bloque/bolsillo del modelo activo (50/30/20)
            val activeModelWithBlocks = allocationDao.getActiveModelWithBlocksSync()
            val targetBlock = if (activeModelWithBlocks != null && activeModelWithBlocks.blocks.isNotEmpty()) {
                val blocks = activeModelWithBlocks.blocks
                val catName = matchedCategory?.name?.lowercase() ?: ""
                when {
                    catName.contains("aliment") || catName.contains("transporte") || catName.contains("servicio") || catName.contains("salud") ->
                        blocks.firstOrNull { it.type == BlockType.NEEDS } ?: blocks.first()
                    catName.contains("entreten") || catName.contains("compra") || catName.contains("viaje") ->
                        blocks.firstOrNull { it.type == BlockType.WANTS } ?: blocks.first()
                    else ->
                        blocks.firstOrNull { it.type == BlockType.WANTS } ?: blocks.first()
                }
            } else null

            // 6. Insertar en base de datos cifrada
            val transactionEntity = TransactionEntity(
                accountId = targetAccount.id,
                categoryId = categoryId,
                blockId = targetBlock?.id,
                amountCents = parsed.amountCents,
                type = parsed.type,
                dateMillis = parsed.timestampMillis,
                merchant = parsed.merchant,
                note = "Detectado: ${parsed.bankOrigin.displayName} (${parsed.source.name})"
            )
            val transactionId = transactionDao.insertTransaction(transactionEntity)

            // Actualizar saldo de la cuenta
            val updatedBalance = if (parsed.type == TransactionType.INCOME) {
                targetAccount.currentBalanceCents + parsed.amountCents
            } else {
                targetAccount.currentBalanceCents - parsed.amountCents
            }
            accountDao.updateBalance(targetAccount.id, updatedBalance)

            // Registrar registro en pending_transactions con estado AUTO_CONFIRMED
            val pendingRecord = PendingTransactionEntity(
                source = parsed.source,
                sourceSender = parsed.bankOrigin.displayName,
                rawText = parsed.rawText,
                deduplicationHash = deduplicationHash,
                suggestedAmountCents = parsed.amountCents,
                suggestedMerchant = parsed.merchant,
                suggestedAccountId = targetAccount.id,
                suggestedCategoryId = categoryId,
                suggestedBlockId = targetBlock?.id,
                suggestedType = parsed.type,
                detectedDateMillis = parsed.timestampMillis,
                status = PendingStatus.AUTO_CONFIRMED
            )
            val pendingId = pendingTransactionDao.insert(pendingRecord)

            // 7. Enviar notificación informativa en el dispositivo
            showLocalNotification(parsed, targetAccount, matchedCategory?.name)

            return CaptureResult.Success(
                transactionId = transactionId,
                pendingId = pendingId,
                summary = "Gasto registrado: ${CurrencyFormatter.formatCentsToCop(parsed.amountCents)} en ${parsed.merchant} (${targetAccount.name})"
            )
        } finally {
            // Eliminar de la caché de procesamiento después de un tiempo o al terminar
            // (En este caso al terminar la ejecución de Room)
            processingHashes.remove(deduplicationHash)
        }
    }

    private fun matchAccountForBank(bank: BankOrigin, accounts: List<AccountEntity>): AccountEntity {
        // Coincidencia por nombre de cuenta
        val byName = accounts.firstOrNull {
            when (bank) {
                BankOrigin.BANCOLOMBIA -> it.name.contains("Bancolombia", ignoreCase = true)
                BankOrigin.NEQUI -> it.name.contains("Nequi", ignoreCase = true)
                BankOrigin.DAVIPLATA -> it.name.contains("Daviplata", ignoreCase = true)
                BankOrigin.NU_COLOMBIA -> it.name.contains("Nu", ignoreCase = true)
                BankOrigin.RAPPICARD -> it.name.contains("Rappi", ignoreCase = true) || it.name.contains("RappiCard", ignoreCase = true) || it.name.contains("RappiPay", ignoreCase = true)
                BankOrigin.SCOTIABANK_COLPATRIA -> it.name.contains("Colpatria", ignoreCase = true) || it.name.contains("Scotiabank", ignoreCase = true)
                BankOrigin.BBVA -> it.name.contains("BBVA", ignoreCase = true)
                BankOrigin.DALE -> it.name.contains("Dale", ignoreCase = true)
                BankOrigin.BANCO_DE_BOGOTA -> it.name.contains("Bogota", ignoreCase = true) || it.name.contains("Bogotá", ignoreCase = true)
                BankOrigin.GENERIC_COLOMBIAN_BANK -> false
            }
        }
        if (byName != null) return byName

        // Coincidencia por tipo de cuenta
        val byType = accounts.firstOrNull {
            when (bank) {
                BankOrigin.NEQUI, BankOrigin.DAVIPLATA, BankOrigin.DALE -> it.type == AccountType.DIGITAL_WALLET
                BankOrigin.RAPPICARD -> it.type == AccountType.DIGITAL_WALLET || it.type == AccountType.CHECKING || it.type == AccountType.SAVINGS
                BankOrigin.BANCOLOMBIA, BankOrigin.NU_COLOMBIA, BankOrigin.BBVA, BankOrigin.SCOTIABANK_COLPATRIA ->
                    it.type == AccountType.SAVINGS || it.type == AccountType.CHECKING
                else -> false
            }
        }
        if (byType != null) return byType

        // Fallback a la primera cuenta disponible
        return accounts.first()
    }

    private fun generateDeduplicationHash(
        bankOrigin: BankOrigin,
        amountCents: Long,
        merchant: String,
        timeBucket: Long
    ): String {
        val normalizedMerchant = merchant.lowercase().replace(Regex("[^a-z0-9]"), "")
        val rawKey = "${bankOrigin.name}_${amountCents}_${normalizedMerchant}_$timeBucket"
        val bytes = MessageDigest.getInstance("MD5").digest(rawKey.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun showLocalNotification(
        parsed: ParsedBankTransaction,
        account: AccountEntity,
        categoryName: String?
    ) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Avisos de gastos e ingresos detectados automáticamente"
                }
                notificationManager.createNotificationChannel(channel)
            }

            val sign = if (parsed.type == TransactionType.INCOME) "+" else "-"
            val sourceLabel = if (parsed.source == PendingSource.EMAIL_NOTIFICATION) " (Correo)" else ""
            val title = if (parsed.type == TransactionType.INCOME) "✅ Ingreso registrado$sourceLabel" else "✅ Gasto registrado$sourceLabel"
            val text = "$sign${CurrencyFormatter.formatCentsToCop(parsed.amountCents)} • ${parsed.merchant} (${account.name}${if (categoryName != null) " - $categoryName" else ""})"

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), notification)
        } catch (_: Exception) {
            // Protección contra fallos de notificación del sistema
        }
    }

    /**
     * Maneja un aviso de pago de tarjeta de crédito: identifica la tarjeta, deduplica los correos
     * repetidos del mismo pago (ej. PSE + banco origen) y deja un registro PENDIENTE de confirmación.
     */
    private suspend fun handleCardPayment(
        parsed: ParsedBankTransaction,
        isSimulation: Boolean
    ): CaptureResult {
        val cards = creditCardDao.getAllCreditCardsWithAccount().first()
        if (cards.isEmpty()) {
            return CaptureResult.Ignored("No hay tarjetas registradas para aplicar el pago")
        }

        val matched = matchCardForPayment(parsed, cards)
            ?: return CaptureResult.Ignored(
                "No se pudo identificar la tarjeta del pago (últimos 4: ${parsed.cardLastFour ?: "N/D"})"
            )

        val cardId = matched.card.id
        val cardName = matched.card.lastFourDigits
            ?.takeIf { it.isNotBlank() }
            ?.let { "${matched.account.name} ••$it" }
            ?: matched.account.name

        // Hash estable por (tarjeta + monto): los correos repetidos del mismo pago colapsan en uno,
        // sin depender del remitente ni del texto exacto (decisión del usuario).
        val paymentHash = generateCardPaymentHash(cardId, parsed.amountCents)

        if (isSimulation) {
            pendingTransactionDao.deleteByDeduplicationHash(paymentHash)
        } else {
            val windowStart = parsed.timestampMillis - (24 * 60 * 60 * 1000L)
            val existing = pendingTransactionDao.findRecentByHash(paymentHash, windowStart)
            if (existing != null && existing.status != PendingStatus.DISCARDED) {
                return CaptureResult.Duplicate(
                    hash = paymentHash,
                    summary = "Pago de tarjeta ya detectado: $cardName (${CurrencyFormatter.formatCentsToCop(parsed.amountCents)})"
                )
            }
        }

        val pendingRecord = PendingTransactionEntity(
            source = parsed.source,
            sourceSender = "Pago tarjeta",
            rawText = parsed.rawText,
            deduplicationHash = paymentHash,
            suggestedAmountCents = parsed.amountCents,
            suggestedMerchant = cardName,
            suggestedAccountId = null,
            suggestedCategoryId = null,
            suggestedBlockId = null,
            suggestedType = TransactionType.INCOME,
            suggestedCardId = cardId,
            detectedDateMillis = parsed.timestampMillis,
            status = PendingStatus.PENDING
        )
        val pendingId = pendingTransactionDao.insert(pendingRecord)

        showCardPaymentNotification(parsed, cardName)

        return CaptureResult.CardPaymentPending(
            pendingId = pendingId,
            cardId = cardId,
            cardName = cardName,
            amountCents = parsed.amountCents,
            summary = "Pago de tarjeta detectado: $cardName (${CurrencyFormatter.formatCentsToCop(parsed.amountCents)}) — confírmalo en Movimientos"
        )
    }

    /** Empareja el pago detectado con una tarjeta por sus últimos 4 dígitos o por el banco emisor. */
    private fun matchCardForPayment(
        parsed: ParsedBankTransaction,
        cards: List<com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount>
    ): com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount? {
        // 1. Coincidencia exacta por últimos 4 dígitos
        val lastFour = parsed.cardLastFour
        if (lastFour != null) {
            cards.firstOrNull { it.card.lastFourDigits == lastFour }?.let { return it }
        }

        // 2. Coincidencia por nombre del banco emisor en la cuenta de la tarjeta
        val byBank = cards.firstOrNull { cardWithAcc ->
            when (parsed.bankOrigin) {
                BankOrigin.RAPPICARD -> cardWithAcc.account.name.contains("Rappi", ignoreCase = true)
                BankOrigin.NU_COLOMBIA -> cardWithAcc.account.name.contains("Nu", ignoreCase = true)
                BankOrigin.BANCO_DE_BOGOTA -> cardWithAcc.account.name.contains("Bogot", ignoreCase = true)
                else -> false
            }
        }
        if (byBank != null) return byBank

        // 2b. Coincidencia por el texto del aviso: en pagos por PSE el aviso llega desde el banco
        // ORIGEN (ej. Bancolombia), no desde el emisor de la tarjeta, por lo que `bankOrigin` no
        // sirve para identificarla. Se buscan alias del emisor dentro del texto.
        val lowerRaw = parsed.rawText.lowercase()
        val byIssuerText = cards.firstOrNull { cardWithAcc ->
            val lastFour = cardWithAcc.card.lastFourDigits
            cardIssuerAliases(cardWithAcc.account.name).any { lowerRaw.contains(it) } ||
                (!lastFour.isNullOrBlank() && lowerRaw.contains(lastFour))
        }
        if (byIssuerText != null) return byIssuerText

        // 3. Si solo hay una tarjeta registrada, es una apuesta razonable.
        return cards.singleOrNull()
    }

    /**
     * Alias que permiten reconocer el emisor de una tarjeta dentro del texto de un aviso bancario
     * (útil cuando el aviso proviene del banco origen y no del emisor, ej. pago por PSE).
     */
    private fun cardIssuerAliases(accountName: String): List<String> {
        val name = accountName.lowercase()
        return when {
            name.contains("nu") -> listOf("nu compan", "nu compañ", "nubank", "nu colombia", " nu ", "nu*", "*nu")
            name.contains("rappi") -> listOf("rappicard", "rappi")
            name.contains("bogot") -> listOf("banco de bogot", "banco de bogotá")
            else -> if (name.length >= 4) listOf(name) else emptyList()
        }
    }

    private fun generateCardPaymentHash(cardId: Long, amountCents: Long): String {
        val rawKey = "CARDPMT_${cardId}_$amountCents"
        val bytes = MessageDigest.getInstance("MD5").digest(rawKey.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Maneja un aviso de pago de crédito/préstamo: identifica el crédito, deduplica los correos
     * repetidos del mismo pago (ej. PSE + banco origen) y deja un registro PENDIENTE de confirmación.
     */
    private suspend fun handleDebtPayment(
        parsed: ParsedBankTransaction,
        isSimulation: Boolean
    ): CaptureResult {
        val debts = debtDao.getActiveDebtsSync()
        if (debts.isEmpty()) {
            return CaptureResult.Ignored("No hay créditos activos para aplicar el pago")
        }

        val matched = matchDebtForPayment(parsed, debts)
            ?: return CaptureResult.Ignored(
                "No se pudo identificar el crédito del pago (pista: ${parsed.debtHint ?: "N/D"})"
            )

        val debtId = matched.id
        val debtName = matched.name

        // Hash estable por (crédito + monto): los correos repetidos del mismo pago colapsan en uno.
        val paymentHash = generateDebtPaymentHash(debtId, parsed.amountCents)

        if (isSimulation) {
            pendingTransactionDao.deleteByDeduplicationHash(paymentHash)
        } else {
            val windowStart = parsed.timestampMillis - (24 * 60 * 60 * 1000L)
            val existing = pendingTransactionDao.findRecentByHash(paymentHash, windowStart)
            if (existing != null && existing.status != PendingStatus.DISCARDED) {
                return CaptureResult.Duplicate(
                    hash = paymentHash,
                    summary = "Pago de crédito ya detectado: $debtName (${CurrencyFormatter.formatCentsToCop(parsed.amountCents)})"
                )
            }
        }

        val pendingRecord = PendingTransactionEntity(
            source = parsed.source,
            sourceSender = "Pago crédito",
            rawText = parsed.rawText,
            deduplicationHash = paymentHash,
            suggestedAmountCents = parsed.amountCents,
            suggestedMerchant = debtName,
            suggestedAccountId = null,
            suggestedCategoryId = null,
            suggestedBlockId = null,
            suggestedType = TransactionType.INCOME,
            suggestedCardId = null,
            suggestedDebtId = debtId,
            detectedDateMillis = parsed.timestampMillis,
            status = PendingStatus.PENDING
        )
        val pendingId = pendingTransactionDao.insert(pendingRecord)

        showDebtPaymentNotification(parsed, debtName)

        return CaptureResult.DebtPaymentPending(
            pendingId = pendingId,
            debtId = debtId,
            debtName = debtName,
            amountCents = parsed.amountCents,
            summary = "Pago de crédito detectado: $debtName (${CurrencyFormatter.formatCentsToCop(parsed.amountCents)}) — confírmalo en Movimientos"
        )
    }

    /** Empareja el pago detectado con un crédito por su nombre o por el banco emisor. */
    private fun matchDebtForPayment(
        parsed: ParsedBankTransaction,
        debts: List<DebtEntity>
    ): DebtEntity? {
        // Tokens significativos del aviso (pista del producto y nombre del banco emisor).
        val tokens = buildList {
            parsed.debtHint?.let { add(it) }
            add(parsed.bankOrigin.displayName)
        }.map { it.lowercase() }
            .flatMap { it.split(Regex("[^a-záéíóúñ0-9]+")) }
            .filter { it.length >= 4 }
            .distinct()

        val byName = debts.firstOrNull { debt ->
            val name = debt.name.lowercase()
            tokens.any { token -> name.contains(token) }
        }
        if (byName != null) return byName

        // Si solo hay un crédito activo, es una apuesta razonable.
        return debts.singleOrNull()
    }

    private fun generateDebtPaymentHash(debtId: Long, amountCents: Long): String {
        val rawKey = "DEBTPMT_${debtId}_$amountCents"
        val bytes = MessageDigest.getInstance("MD5").digest(rawKey.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun showDebtPaymentNotification(parsed: ParsedBankTransaction, debtName: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Avisos de gastos e ingresos detectados automáticamente"
                }
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🏦 Pago de crédito detectado")
                .setContentText("${CurrencyFormatter.formatCentsToCop(parsed.amountCents)} • $debtName — confírmalo en Movimientos")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), notification)
        } catch (_: Exception) {
            // Protección contra fallos de notificación del sistema
        }
    }

    private fun showCardPaymentNotification(parsed: ParsedBankTransaction, cardName: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Avisos de gastos e ingresos detectados automáticamente"
                }
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("💳 Pago de tarjeta detectado")
                .setContentText("${CurrencyFormatter.formatCentsToCop(parsed.amountCents)} • $cardName — confírmalo en Movimientos")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), notification)
        } catch (_: Exception) {
            // Protección contra fallos de notificación del sistema
        }
    }
}
