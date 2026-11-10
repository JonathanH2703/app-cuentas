package com.personalfinance.cuentas.domain.capture

import com.personalfinance.cuentas.data.local.entities.PendingSource
import com.personalfinance.cuentas.data.local.entities.TransactionType
import java.util.regex.Pattern

object ColombianBankParser {

    /**
     * Convierte una cadena de monto colombiana en centavos de COP (Long * 100).
     * Soporta:
     * - "$ 45.000" -> 4500000L
     * - "$45.000,00" -> 4500000L
     * - "1.250.000,50" -> 125000050L
     * - "120000" -> 12000000L
     */
    fun parseCopAmountToCents(amountRaw: String): Long {
        val sanitized = amountRaw.replace("$", "").replace("-", "").replace(" ", "").trim()
        if (sanitized.isEmpty()) return 0L

        return try {
            val hasComma = sanitized.contains(",")
            val hasDot = sanitized.contains(".")

            if (hasComma && hasDot) {
                // Caso mixto: 1.234,56 o 1,234.56
                val commaIndex = sanitized.lastIndexOf(",")
                val dotIndex = sanitized.lastIndexOf(".")
                
                if (commaIndex > dotIndex) {
                    // Formato 1.234,56 (Colombiano estándar)
                    val pesos = sanitized.substring(0, commaIndex).replace(".", "").toLongOrNull() ?: 0L
                    val cents = sanitized.substring(commaIndex + 1).filter { it.isDigit() }.take(2).padEnd(2, '0').toLongOrNull() ?: 0L
                    (pesos * 100L) + cents
                } else {
                    // Formato 1,234.56 (US/Bancolombia Email)
                    val pesos = sanitized.substring(0, dotIndex).replace(",", "").toLongOrNull() ?: 0L
                    val cents = sanitized.substring(dotIndex + 1).filter { it.isDigit() }.take(2).padEnd(2, '0').toLongOrNull() ?: 0L
                    (pesos * 100L) + cents
                }
            } else if (hasComma) {
                // Solo coma: puede ser 45.000 (erróneo) o 45,00
                val index = sanitized.lastIndexOf(",")
                val decimalPart = sanitized.substring(index + 1)
                if (decimalPart.length == 2 && decimalPart.all { it.isDigit() }) {
                    val pesos = sanitized.substring(0, index).replace(".", "").toLongOrNull() ?: 0L
                    (pesos * 100L) + decimalPart.toLong()
                } else {
                    // Tratar coma como separador de miles si no parece decimal
                    val pesos = sanitized.replace(",", "").toLongOrNull() ?: 0L
                    pesos * 100L
                }
            } else if (hasDot) {
                // Solo punto: puede ser 45.000 o 45.00
                val index = sanitized.lastIndexOf(".")
                val decimalPart = sanitized.substring(index + 1)
                if (decimalPart.length == 2 && decimalPart.all { it.isDigit() } && sanitized.length > 3 && sanitized[sanitized.length - 4] != '.') {
                    // Si parece decimal (ej: 45.00) y no es parte de miles (ej: 1.000.000)
                    val pesos = sanitized.substring(0, index).replace(".", "").toLongOrNull() ?: 0L
                    (pesos * 100L) + decimalPart.toLong()
                } else {
                    // Tratar punto como separador de miles (ej: 45.000)
                    val pesos = sanitized.replace(".", "").toLongOrNull() ?: 0L
                    pesos * 100L
                }
            } else {
                // Sin separadores: 120000
                val pesos = sanitized.toLongOrNull() ?: 0L
                pesos * 100L
            }
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Determina el origen bancario basado en el remitente SMS o el paquete de la aplicación.
     */
    fun identifyBankOrigin(senderOrPackage: String, text: String): BankOrigin? {
        val lowerSender = senderOrPackage.lowercase()
        val lowerText = text.lowercase()

        return when {
            lowerSender.contains("890001") || lowerSender.contains("85540") || lowerSender.contains("bancolombia") ||
                    lowerSender.contains("com.todo1.mobile") || lowerSender.contains("com.bancolombia") ||
                    lowerText.contains("bancolombia") || lowerText.contains("alertas y notificaciones") -> BankOrigin.BANCOLOMBIA

            lowerSender.contains("85954") || lowerSender.contains("nequi") ||
                    lowerSender.contains("com.nequi") -> BankOrigin.NEQUI

            // RappiCard ANTES que Davivienda: el banco emisor de RappiCard es Davivienda,
            // por lo que correos de "@davivienda.com" podrian matchear Daviplata incorrectamente
            // si no priorizamos la deteccion de Rappi por sender/texto.
            lowerSender.contains("rappi") || lowerSender.contains("rappipay") ||
                    lowerSender.contains("rappicard") || lowerSender.contains("890099") ||
                    lowerSender.contains("85040") || lowerSender.contains("com.rappi") ||
                    lowerSender.contains("com.grability.rappi") -> BankOrigin.RAPPICARD

            lowerSender.contains("85888") || lowerSender.contains("daviplata") ||
                    lowerSender.contains("com.davivienda") -> BankOrigin.DAVIPLATA

            lowerSender.contains("nu") || lowerSender.contains("nubank") ||
                    lowerSender.contains("com.nu.production") -> BankOrigin.NU_COLOMBIA

            lowerSender.contains("colpatria") || lowerSender.contains("scotiabank") ||
                    lowerSender.contains("com.scotiabank") -> BankOrigin.SCOTIABANK_COLPATRIA

            lowerSender.contains("bbva") || lowerSender.contains("com.bbva") -> BankOrigin.BBVA

            lowerSender.contains("dale") || lowerSender.contains("co.com.dale") -> BankOrigin.DALE

            lowerSender.contains("bancodebogota") || lowerSender.contains("banco de bogota") -> BankOrigin.BANCO_DE_BOGOTA

            // Búsqueda en el cuerpo del mensaje si el remitente fue genérico o una app de correo (Gmail, Outlook)
            lowerText.contains("bancolombia") -> BankOrigin.BANCOLOMBIA
            lowerText.contains("nequi") -> BankOrigin.NEQUI
            lowerText.contains("daviplata") || lowerText.contains("davivienda") -> BankOrigin.DAVIPLATA
            lowerText.contains("cuenta nu") || lowerText.contains("tarjeta nu") || lowerText.contains("nubank") || lowerText.contains("nu ") || lowerText.contains("nu:") -> BankOrigin.NU_COLOMBIA
            // Davivienda + Rappi: correo de RappiCard emitida por Davivienda
            (lowerText.contains("davivienda") || lowerText.contains("davivienda.com")) &&
                    (lowerText.contains("rappi") || lowerText.contains("rappicard")) -> BankOrigin.RAPPICARD
            lowerText.contains("rappi") || lowerText.contains("rappicard") || lowerText.contains("rappipay") || lowerText.contains("rappi cuenta") -> BankOrigin.RAPPICARD
            lowerText.contains("scotiabank") || lowerText.contains("colpatria") -> BankOrigin.SCOTIABANK_COLPATRIA
            lowerText.contains("bbva") -> BankOrigin.BBVA
            lowerText.contains("dale!") || lowerText.contains("dale") -> BankOrigin.DALE
            lowerText.contains("banco de bogota") || lowerText.contains("bancodebogota") -> BankOrigin.BANCO_DE_BOGOTA

            // Si contiene términos inequívocos de un movimiento financiero en Colombia
            // (compra, pago, transferencia, abono, retiro, débito, etc.)
            lowerText.contains("compra por") || lowerText.contains("pago por") ||
                    lowerText.contains("transferencia por") || lowerText.contains("transferiste") ||
                    lowerText.contains("pagaste") || lowerText.contains("compraste") ||
                    lowerText.contains("recibiste") || lowerText.contains("recibió") ||
                    lowerText.contains("recibio") || lowerText.contains("abono") ||
                    lowerText.contains("enviaste") || lowerText.contains("retiraste") ||
                    lowerText.contains("debito") || lowerText.contains("fue aprobada") ||
                    lowerText.contains("aprobada") || lowerText.contains("pago exitoso") ||
                    lowerText.contains("compra exitosa") || lowerText.contains("consignación") ||
                    lowerText.contains("consignacion") ||
                    lowerText.contains("tarjeta de credito") ||
                    lowerText.contains("tarjeta de crédito") ||
                    lowerText.contains("pse") -> BankOrigin.GENERIC_COLOMBIAN_BANK

            else -> null
        }
    }

    /**
     * Analiza el texto de la notificación o SMS y extrae monto, comercio y tipo.
     * Soporta tanto SMS de operadoras como notificaciones de apps bancarias y correos (Gmail, Outlook).
     */
    fun parse(
        senderOrPackage: String,
        rawText: String,
        source: PendingSource,
        timestampMillis: Long = System.currentTimeMillis()
    ): ParsedBankTransaction? {
        if (rawText.isBlank()) return null

        // Si no se identifica un banco conocido ni un patrón financiero claro, se descarta la
        // notificación para no registrar movimientos de apps no financieras (chats, promos, etc.).
        val bank = identifyBankOrigin(senderOrPackage, rawText) ?: return null

        // Detectar si proviene de una app de correo electrónico
        val isEmailApp = senderOrPackage.lowercase().let {
            it.contains("com.google.android.gm") ||
            it.contains("com.microsoft.office.outlook") ||
            it.contains("com.yahoo.mobile.client.android.mail") ||
            it.contains("com.samsung.android.email") ||
            it.contains("mail") || it.contains("correo")
        }
        val effectiveSource = if (isEmailApp && source == PendingSource.NOTIFICATION) {
            PendingSource.EMAIL_NOTIFICATION
        } else source

        // Pago de tarjeta de crédito (ej. PSE): NO es un gasto ni un ingreso a una cuenta.
        // Se marca como TRANSFER_OUT para que el flujo de captura pida confirmación y luego baje el cupo usado.
        if (looksLikeCardPayment(rawText)) {
            val paymentCents = extractCardPaymentAmountCents(rawText)
            if (paymentCents > 0L) {
                return ParsedBankTransaction(
                    source = effectiveSource,
                    bankOrigin = bank,
                    amountCents = paymentCents,
                    merchant = "Pago tarjeta ${bank.displayName}",
                    type = TransactionType.TRANSFER_OUT,
                    rawText = rawText,
                    timestampMillis = timestampMillis,
                    accountFilterHint = null,
                    isCardPayment = true,
                    cardLastFour = extractCardLastFour(rawText)
                )
            }
        }

        // Pago de un crédito/préstamo (ej. PSE con Banco de Bogotá): NO es un gasto ni un ingreso
        // a una cuenta. Se marca como TRANSFER_OUT para que el flujo pida confirmación y luego reduzca el saldo del crédito.
        if (looksLikeDebtPayment(rawText)) {
            val paymentCents = extractCardPaymentAmountCents(rawText)
            if (paymentCents > 0L) {
                return ParsedBankTransaction(
                    source = effectiveSource,
                    bankOrigin = bank,
                    amountCents = paymentCents,
                    merchant = "Pago crédito ${bank.displayName}",
                    type = TransactionType.TRANSFER_OUT,
                    rawText = rawText,
                    timestampMillis = timestampMillis,
                    accountFilterHint = null,
                    isDebtPayment = true,
                    debtHint = extractDebtHint(rawText)
                )
            }
        }

        // Intentar parser especializado por entidad
        val specific = when (bank) {
            BankOrigin.BANCOLOMBIA -> parseBancolombia(rawText, effectiveSource, timestampMillis)
            BankOrigin.NEQUI -> parseNequi(rawText, effectiveSource, timestampMillis)
            BankOrigin.DAVIPLATA -> parseDaviplata(rawText, effectiveSource, timestampMillis)
            BankOrigin.NU_COLOMBIA -> parseNu(rawText, effectiveSource, timestampMillis)
            BankOrigin.RAPPICARD -> parseRappiCard(rawText, effectiveSource, timestampMillis)
            BankOrigin.SCOTIABANK_COLPATRIA,
            BankOrigin.BBVA,
            BankOrigin.DALE,
            BankOrigin.BANCO_DE_BOGOTA,
            BankOrigin.GENERIC_COLOMBIAN_BANK -> parseGeneric(rawText, bank, effectiveSource, timestampMillis)
        }
        if (specific != null) return specific

        // Respaldo 1: Parser genérico para no perder ningún movimiento
        val generic = parseGeneric(rawText, bank, effectiveSource, timestampMillis)
        if (generic != null) return generic

        // Respaldo 2: Extractor universal de transacciones financieras en Colombia
        return parseUniversalFallback(rawText, bank, effectiveSource, timestampMillis)
    }

    private fun parseBancolombia(text: String, source: PendingSource, timestamp: Long): ParsedBankTransaction? {
        val lowerText = text.lowercase()

        // 1. Ingreso Email/Notif: Recibiste una transferencia por $1,223,000 de YELIS MARIN
        val emailIncomeRegex = Pattern.compile("(?i)recibiste una transferencia por\\s*\\$?([\\d\\.,]+)\\s+de\\s+([^,]+?)(?:\\s+en tu cuenta|\\.|\\$)")
        val emailIncomeMatcher = emailIncomeRegex.matcher(text)
        if (emailIncomeMatcher.find()) {
            val amountStr = emailIncomeMatcher.group(1) ?: ""
            val senderName = emailIncomeMatcher.group(2)?.trim() ?: "Transferencia recibida"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.BANCOLOMBIA,
                    amountCents = cents,
                    merchant = senderName,
                    type = TransactionType.INCOME,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Bancolombia"
                )
            }
        }

        // 2. Transferencia Enviada Email: transferencia por $11,000.00 a YELIS MARIN desde tu cuenta *9483
        val emailTransferRegex = Pattern.compile("(?i)transferencia por\\s*\\$?([\\d\\.,]+)\\s+a\\s+([^,]+?)(?:\\s+desde tu cuenta\\s+[*xX]*(\\d{4}))?")
        val emailTransferMatcher = emailTransferRegex.matcher(text)
        if (emailTransferMatcher.find()) {
            val amountStr = emailTransferMatcher.group(1) ?: ""
            val targetName = emailTransferMatcher.group(2)?.trim() ?: "Transferencia"
            val lastFour = emailTransferMatcher.group(3)
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.BANCOLOMBIA,
                    amountCents = cents,
                    merchant = if (targetName.contains("NEQUI", ignoreCase = true)) "Envío Nequi" else "Transf: ${cleanMerchantName(targetName)}",
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Bancolombia",
                    cardLastFour = lastFour
                )
            }
        }

        // 3. Pago QR Email/Notif: pagaste $11,000.00 por codigo QR desde tu cuenta *9483
        val qrEmailRegex = Pattern.compile("(?i)pagaste\\s*\\$?([\\d\\.,]+)\\s+por\\s+codigo\\s+qr(?:\\s+desde\\s+tu\\s+cuenta\\s+[*xX]*(\\d{4}))?")
        val qrEmailMatcher = qrEmailRegex.matcher(text)
        if (qrEmailMatcher.find()) {
            val amountStr = qrEmailMatcher.group(1) ?: ""
            val lastFour = qrEmailMatcher.group(2)
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.BANCOLOMBIA,
                    amountCents = cents,
                    merchant = "Pago QR",
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Bancolombia",
                    cardLastFour = lastFour
                )
            }
        }

        // 4. Transferencia Entre Cuentas Email: transferencia por $364,000.00 entre tus cuentas Bancolombia desde tu cuenta *9483
        val emailInternalRegex = Pattern.compile("(?i)transferencia por\\s*\\$?([\\d\\.,]+)\\s+entre tus cuentas Bancolombia(?:\\s+desde tu cuenta\\s+[*xX]*(\\d{4}))?")
        val emailInternalMatcher = emailInternalRegex.matcher(text)
        if (emailInternalMatcher.find()) {
            val amountStr = emailInternalMatcher.group(1) ?: ""
            val lastFour = emailInternalMatcher.group(2)
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.BANCOLOMBIA,
                    amountCents = cents,
                    merchant = "Transferencia entre cuentas",
                    type = TransactionType.TRANSFER_OUT,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Bancolombia",
                    cardLastFour = lastFour
                )
            }
        }

        // 5. Ingreso Genérico (SMS/App): Recibio transferencia de X por $Y / Abono de nomina por $Y / TRANSFERENCIA CTA SUC VIRTUAL
        val incomeRegex = Pattern.compile("(?i)(?:recibio|recibiste|le transfirieron|abono(?: de nomina)?|transferencia(?: cta suc virtual)?)(?:\\s+transferencia(?:\\s+de)?)?\\s*([^,\\.\\$]*?)\\s*(?:por|de)?\\s*\\$?([\\d\\.,]+)")
        val incomeMatcher = incomeRegex.matcher(text)
        if (incomeMatcher.find()) {
            val senderName = incomeMatcher.group(1)?.trim()?.ifBlank { 
                if (text.contains("SUC VIRTUAL", ignoreCase = true)) "Transferencia Sucursal Virtual" else "Transferencia recibida" 
            } ?: "Transferencia recibida"
            val amountStr = incomeMatcher.group(2) ?: ""
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.BANCOLOMBIA,
                    amountCents = cents,
                    merchant = senderName,
                    type = TransactionType.INCOME,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Bancolombia"
                )
            }
        }

        // 2. Gasto: Compra por $X en COMERCIO / Pago por $X a COMERCIO / PAGO QR COMERCIO
        val expenseRegex = Pattern.compile("(?i)(?:compra por|pago por|pago de|pago qr)\\s*\\$?([\\d\\.,]+)?\\s*(?:en|a|de)?\\s*([^,\\.\\$]+?)(?:\\s+por\\s+\\$?([\\d\\.,]+))?(?:\\s+\\d{1,2}:\\d{2}|\\s+con|\\.|\\$|\\n|$)")
        val expenseMatcher = expenseRegex.matcher(text)
        if (expenseMatcher.find()) {
            val amountStr = expenseMatcher.group(1) ?: expenseMatcher.group(3) ?: ""
            val merchantRaw = expenseMatcher.group(2)?.trim() ?: "Bancolombia"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.BANCOLOMBIA,
                    amountCents = cents,
                    merchant = cleanMerchantName(merchantRaw),
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Bancolombia"
                )
            }
        }

        // 3. Transferencia enviada: transferencia por $X a COMERCIO / cuenta / Transferiste $X desde... a la cuenta *Y / TRANSFERENCIAS A NEQUI
        val transferRegex = Pattern.compile("(?i)(?:transferencia(?:\\s+exitosa)?\\s+por|transferiste|transferencias a nequi)\\s*\\$?([\\d\\.,]+)?\\s*(?:desde\\s+[^,]+?\\s+)?(?:a|a la cuenta)?\\s*([^,\\.\\$\\n]+?)(?:\\s+por\\s+\\$?([\\d\\.,]+))?(?:\\s+el\\s+\\d|\\.|\\$|\\n|$)")
        val transferMatcher = transferRegex.matcher(text)
        if (transferMatcher.find()) {
            val amountStr = transferMatcher.group(1) ?: transferMatcher.group(3) ?: ""
            val target = transferMatcher.group(2)?.trim()?.ifBlank { if (text.contains("NEQUI", ignoreCase = true)) "Nequi" else "Transferencia" } ?: "Transferencia"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.BANCOLOMBIA,
                    amountCents = cents,
                    merchant = if (target.contains("NEQUI", ignoreCase = true)) "Envío Nequi" else "Transf: ${cleanMerchantName(target)}",
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Bancolombia"
                )
            }
        }

        return null
    }

    private fun parseNequi(text: String, source: PendingSource, timestamp: Long): ParsedBankTransaction? {
        // 1. Ingreso: ¡Te transfirieron $X! Maria te envio plata / Recibiste $X de Y
        val incomeRegex = Pattern.compile("(?i)(?:¡?te transfirieron|recibiste)\\s*\\$?([\\d\\.,]+)(?:\\s*(?:de|!)\\s*([^!\\.,\\$]+))?")
        val incomeMatcher = incomeRegex.matcher(text)
        if (incomeMatcher.find()) {
            val amountStr = incomeMatcher.group(1) ?: ""
            val senderRaw = incomeMatcher.group(2)?.trim()?.ifBlank { "Transferencia Nequi" } ?: "Transferencia Nequi"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.NEQUI,
                    amountCents = cents,
                    merchant = cleanMerchantName(senderRaw),
                    type = TransactionType.INCOME,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Nequi"
                )
            }
        }

        // 2. Gasto compra: ¡Pagaste $X en COMERCIO con tu Nequi! / Compraste $X en COMERCIO
        val purchaseRegex = Pattern.compile("(?i)(?:¡?pagaste|compraste)\\s*\\$?([\\d\\.,]+)\\s*en\\s*([^!\\.,\\$]+?)(?:\\s*con tu Nequi|!|\\.|\\$|\\n|$)")
        val purchaseMatcher = purchaseRegex.matcher(text)
        if (purchaseMatcher.find()) {
            val amountStr = purchaseMatcher.group(1) ?: ""
            val merchantRaw = purchaseMatcher.group(2)?.trim() ?: "Nequi"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.NEQUI,
                    amountCents = cents,
                    merchant = cleanMerchantName(merchantRaw),
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Nequi"
                )
            }
        }

        // 3. Envío de plata: Enviaste $X a Y
        val sendRegex = Pattern.compile("(?i)enviaste\\s*\\$?([\\d\\.,]+)\\s*a\\s*([^!\\.,\\$]+?)(?:\\.|\\$|Tu disponible|Tu nuevo|\\n|$)")
        val sendMatcher = sendRegex.matcher(text)
        if (sendMatcher.find()) {
            val amountStr = sendMatcher.group(1) ?: ""
            val recipient = sendMatcher.group(2)?.trim() ?: "Envío Nequi"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.NEQUI,
                    amountCents = cents,
                    merchant = "Envío: ${cleanMerchantName(recipient)}",
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Nequi"
                )
            }
        }

        // 4. Retiro en cajero
        val withdrawalRegex = Pattern.compile("(?i)retiraste\\s*\\$?([\\d\\.,]+)")
        val withdrawalMatcher = withdrawalRegex.matcher(text)
        if (withdrawalMatcher.find()) {
            val amountStr = withdrawalMatcher.group(1) ?: ""
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.NEQUI,
                    amountCents = cents,
                    merchant = "Retiro Cajero",
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Nequi"
                )
            }
        }

        return null
    }

    private fun parseDaviplata(text: String, source: PendingSource, timestamp: Long): ParsedBankTransaction? {
        // 1. Ingreso: Le pasaron $X / Recibio $X
        val incomeRegex = Pattern.compile("(?i)(?:le pasaron|recibio)\\s*\\$?([\\d\\.,]+)")
        val incomeMatcher = incomeRegex.matcher(text)
        if (incomeMatcher.find()) {
            val amountStr = incomeMatcher.group(1) ?: ""
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.DAVIPLATA,
                    amountCents = cents,
                    merchant = "Transferencia Daviplata",
                    type = TransactionType.INCOME,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Daviplata"
                )
            }
        }

        // 2. Compra: Compra por $X en COMERCIO
        val purchaseRegex = Pattern.compile("(?i)compra\\s+por\\s*\\$?([\\d\\.,]+)\\s*en\\s*([^,\\.\\$]+?)(?:\\.|\\$|\\n|$)")
        val purchaseMatcher = purchaseRegex.matcher(text)
        if (purchaseMatcher.find()) {
            val amountStr = purchaseMatcher.group(1) ?: ""
            val merchantRaw = purchaseMatcher.group(2)?.trim() ?: "DaviPlata"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.DAVIPLATA,
                    amountCents = cents,
                    merchant = cleanMerchantName(merchantRaw),
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Daviplata"
                )
            }
        }

        // 3. Envío / Pasaste: Pasaste $X al celular Y
        val sendRegex = Pattern.compile("(?i)pasaste\\s*\\$?([\\d\\.,]+)\\s*(?:al celular\\s*([\\d]+)|a\\s*([^,\\.\\$]+))?")
        val sendMatcher = sendRegex.matcher(text)
        if (sendMatcher.find()) {
            val amountStr = sendMatcher.group(1) ?: ""
            val recipient = (sendMatcher.group(2) ?: sendMatcher.group(3))?.trim() ?: "Daviplata"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.DAVIPLATA,
                    amountCents = cents,
                    merchant = "Envío: $recipient",
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Daviplata"
                )
            }
        }

        return null
    }

    private fun parseNu(text: String, source: PendingSource, timestamp: Long): ParsedBankTransaction? {
        // 1. Ingreso Nu: Recibiste una transferencia de $X / Transferencia recibida por $X
        val incomeRegex = Pattern.compile("(?i)(?:recibiste\\s*(?:una\\s+transferencia\\s+(?:de|por))?|transferencia\\s+recibida\\s+por)\\s*\\$?([\\d\\.,]+)")
        val incomeMatcher = incomeRegex.matcher(text)
        if (incomeMatcher.find()) {
            val amountStr = incomeMatcher.group(1) ?: ""
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.NU_COLOMBIA,
                    amountCents = cents,
                    merchant = "Transferencia Cuenta Nu",
                    type = TransactionType.INCOME,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Nu"
                )
            }
        }

        // 2. Compra Nu: Compra aprobada en COMERCIO por $X / Compra de $X aprobada en COMERCIO
        val purchaseRegex1 = Pattern.compile("(?i)compra\\s*(?:aprobada)?\\s*en\\s*([^,\\.\\$]+?)\\s*por\\s*\\$?([\\d\\.,]+)")
        val matcher1 = purchaseRegex1.matcher(text)
        if (matcher1.find()) {
            val merchantRaw = matcher1.group(1)?.trim() ?: "Nu"
            val amountStr = matcher1.group(2) ?: ""
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.NU_COLOMBIA,
                    amountCents = cents,
                    merchant = cleanMerchantName(merchantRaw),
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Nu"
                )
            }
        }

        val purchaseRegex2 = Pattern.compile("(?i)compra\\s*(?:de)?\\s*\\$?([\\d\\.,]+)\\s*(?:aprobada)?\\s*en\\s*([^,\\.\\$]+)")
        val matcher2 = purchaseRegex2.matcher(text)
        if (matcher2.find()) {
            val amountStr = matcher2.group(1) ?: ""
            val merchantRaw = matcher2.group(2)?.trim() ?: "Nu"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.NU_COLOMBIA,
                    amountCents = cents,
                    merchant = cleanMerchantName(merchantRaw),
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Nu"
                )
            }
        }

        return null
    }

    private fun parseRappiCard(text: String, source: PendingSource, timestamp: Long): ParsedBankTransaction? {
        // 1. RappiCard Email con Comercio: Monto $73.385 ... Comercio SURTIFAMILIAR CERRITO ... Método de pago *5171
        val rappiEmailWithMerchant = Pattern.compile(
            "(?i)Monto\\s*[:\\.\\-]?\\s*\\$?([\\d\\.,]+)[\\s\\S]*?Comercio\\s*[:\\.\\-]?\\s*([^\\n\\|\\$]{2,50}?)\\s*(?:\\n|\\||Fecha)[\\s\\S]*?M\\u00e9todo de pago\\s*[:\\.\\-]?\\s*[*xX]*(\\d{4})"
        )
        val matcherWithMerchant = rappiEmailWithMerchant.matcher(text)
        if (matcherWithMerchant.find()) {
            val amountStr = matcherWithMerchant.group(1) ?: ""
            val merchantRaw = matcherWithMerchant.group(2)?.trim() ?: "RappiCard"
            val lastFour = matcherWithMerchant.group(3)
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.RAPPICARD,
                    amountCents = cents,
                    merchant = cleanMerchantName(merchantRaw),
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Rappi",
                    cardLastFour = lastFour
                )
            }
        }

        // 1b. RappiCard Email sin campo Comercio: Monto $73.385 ... Método de pago *5171
        val rappiEmailRegex = Pattern.compile("(?i)Monto\\s*[:\\.\\-]?\\s*\\$?([\\d\\.,]+)[\\s\\S]*?M\\u00e9todo de pago\\s*[:\\.\\-]?\\s*[*xX]*(\\d{4})")
        val rappiEmailMatcher = rappiEmailRegex.matcher(text)
        if (rappiEmailMatcher.find()) {
            val amountStr = rappiEmailMatcher.group(1) ?: ""
            val lastFour = rappiEmailMatcher.group(2)
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.RAPPICARD,
                    amountCents = cents,
                    merchant = "Compra RappiCard",
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Rappi",
                    cardLastFour = lastFour
                )
            }
        }

        // 2. Ingreso: Te transfirieron $X a tu RappiCuenta / Recibiste $X de Y / Tu pago de $X a tu RappiCard fue aplicado
        val incomeRegex = Pattern.compile("(?i)(?:te transfirieron|recibiste|(?:tu )?pago(?: recibido)? de)\\s*\\$?([\\d\\.,]+)(?:\\s*(?:de|a|en|para)\\s*([^!\\.,\\$]+))?")
        val incomeMatcher = incomeRegex.matcher(text)
        if (incomeMatcher.find()) {
            val amountStr = incomeMatcher.group(1) ?: ""
            val senderRaw = incomeMatcher.group(2)?.trim()?.ifBlank { "RappiPay" } ?: "RappiPay"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.RAPPICARD,
                    amountCents = cents,
                    merchant = cleanMerchantName(senderRaw),
                    type = TransactionType.INCOME,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Rappi"
                )
            }
        }

        // 2. Compra RappiCard: Tu compra con RappiCard en COMERCIO por $X fue aprobada / Compra por $X en COMERCIO con tu RappiCard
        val purchaseRegex1 = Pattern.compile("(?i)(?:tu compra (?:con RappiCard )?en|compra en)\\s+([^,\\.\\$]+?)\\s+por\\s+\\$?([\\d\\.,]+)")
        val matcher1 = purchaseRegex1.matcher(text)
        if (matcher1.find()) {
            val merchantRaw = matcher1.group(1)?.trim() ?: "RappiCard"
            val amountStr = matcher1.group(2) ?: ""
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.RAPPICARD,
                    amountCents = cents,
                    merchant = cleanMerchantName(merchantRaw),
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Rappi"
                )
            }
        }

        val purchaseRegex2 = Pattern.compile("(?i)(?:RappiCard:?\\s*)?(?:compra por|pago de|compraste)\\s*\\$?([\\d\\.,]+)\\s*(?:en|a)\\s*([^,\\.\\$]+)")
        val matcher2 = purchaseRegex2.matcher(text)
        if (matcher2.find()) {
            val amountStr = matcher2.group(1) ?: ""
            val merchantRaw = matcher2.group(2)?.trim() ?: "RappiCard"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.RAPPICARD,
                    amountCents = cents,
                    merchant = cleanMerchantName(merchantRaw),
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Rappi"
                )
            }
        }

        // 3. Envío desde RappiCuenta
        val sendRegex = Pattern.compile("(?i)enviaste\\s*\\$?([\\d\\.,]+)\\s*(?:a|desde)?\\s*([^!\\.,\\$]+)?")
        val sendMatcher = sendRegex.matcher(text)
        if (sendMatcher.find()) {
            val amountStr = sendMatcher.group(1) ?: ""
            val recipient = sendMatcher.group(2)?.trim() ?: "RappiPay"
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = BankOrigin.RAPPICARD,
                    amountCents = cents,
                    merchant = "Envío: ${cleanMerchantName(recipient)}",
                    type = TransactionType.EXPENSE,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = "Rappi"
                )
            }
        }

        return null
    }

    private fun parseGeneric(
        text: String,
        bank: BankOrigin,
        source: PendingSource,
        timestamp: Long
    ): ParsedBankTransaction? {
        val lower = text.lowercase()
        val isIncome = lower.contains("abono") ||
                lower.contains("recibio") ||
                lower.contains("recibiste") ||
                lower.contains("te transfirieron") ||
                lower.contains("intereses")
        
        val isTransfer = lower.contains("transferencia") || 
                        lower.contains("transferiste") || 
                        lower.contains("ahorro") || 
                        lower.contains("inversion") || 
                        lower.contains("inversión") ||
                        lower.contains("nequi") ||
                        lower.contains(" daviplata")

        val type = when {
            isIncome -> TransactionType.INCOME
            isTransfer -> TransactionType.TRANSFER_OUT
            else -> TransactionType.EXPENSE
        }

        // 1. Abono de intereses (especial para montos pequeños)
        if (lower.contains("abono intereses") || lower.contains("intereses ahorros")) {
            val amountPattern = Pattern.compile("(?i)(?:\\$|COP|\\$|-|\\$ -)\\s*([\\d\\.,]+)")
            val matcher = amountPattern.matcher(text)
            if (matcher.find()) {
                val cents = parseCopAmountToCents(matcher.group(1) ?: "")
                if (cents > 0) {
                    return ParsedBankTransaction(
                        source = source,
                        bankOrigin = bank,
                        amountCents = cents,
                        merchant = "Intereses Ahorros",
                        type = TransactionType.INCOME,
                        rawText = text,
                        timestampMillis = timestamp,
                        accountFilterHint = bank.displayName
                    )
                }
            }
        }

        // 2. Pago QR
        if (lower.contains("pago qr")) {
            val qrPattern = Pattern.compile("(?i)pago\\s+qr\\s+([^,\\.\\$\\n\\d]+?)(?:\\s+)?(?:por|de|cop)?\\s*\\$?\\s*-?\\s*([\\d\\.,]+)")
            val matcher = qrPattern.matcher(text)
            if (matcher.find()) {
                val merchantRaw = matcher.group(1)?.trim() ?: "Pago QR"
                val amountStr = matcher.group(2) ?: ""
                val cents = parseCopAmountToCents(amountStr)
                if (cents > 0L) {
                    return ParsedBankTransaction(
                        source = source,
                        bankOrigin = bank,
                        amountCents = cents,
                        merchant = cleanMerchantName(merchantRaw),
                        type = TransactionType.EXPENSE,
                        rawText = text,
                        timestampMillis = timestamp,
                        accountFilterHint = bank.displayName
                    )
                }
            }
        }

        // Estructura 1: Verbo -> Monto -> Comercio (ej: "Compra por $50.000 en D1" o "Pagaste $35.000 en Crepes")
        val pattern1 = Pattern.compile("(?i)(?:tu\\s+)?(?:compra|pago|pagaste|transferencia|transferiste|abono|debito)\\s*(?:aprobada|exitosa)?\\s*(?:por|de)?\\s*\\$?([\\d\\.,]+)(?:\\s*(?:en|a|para|a la cuenta)\\s*([^,\\.\\$\\n]+))?")
        val matcher1 = pattern1.matcher(text)
        if (matcher1.find()) {
            val amountStr = matcher1.group(1) ?: ""
            val target = matcher1.group(2)?.trim() ?: bank.displayName
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = bank,
                    amountCents = cents,
                    merchant = cleanMerchantName(target),
                    type = type,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = bank.displayName
                )
            }
        }

        // Estructura 2: Verbo -> Comercio -> Monto (ej: "Compra en Exito por $80.000" o "Compra aprobada en Crepes por $35.000")
        val pattern2 = Pattern.compile("(?i)(?:tu\\s+)?(?:compra|pago|pagaste)\\s*(?:aprobada|exitosa)?\\s*(?:en|a)\\s+([^,\\.\\$\\n]+?)\\s+(?:por|de)\\s*\\$?([\\d\\.,]+)")
        val matcher2 = pattern2.matcher(text)
        if (matcher2.find()) {
            val target = matcher2.group(1)?.trim() ?: bank.displayName
            val amountStr = matcher2.group(2) ?: ""
            val cents = parseCopAmountToCents(amountStr)
            if (cents > 0L) {
                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = bank,
                    amountCents = cents,
                    merchant = cleanMerchantName(target),
                    type = type,
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = bank.displayName
                )
            }
        }

        return null
    }

    private fun parseUniversalFallback(
        text: String,
        bank: BankOrigin,
        source: PendingSource,
        timestamp: Long
    ): ParsedBankTransaction? {
        // Exige una acción financiera explícita para no capturar avisos de saldo,
        // promociones ni recordatorios que solo contienen un monto.
        val lower = text.lowercase()
        val hasAction = lower.contains("compra") || lower.contains("pago") ||
                lower.contains("pagaste") || lower.contains("compraste") ||
                lower.contains("transferencia") || lower.contains("transferiste") ||
                lower.contains("transfirieron") || lower.contains("enviaste") ||
                lower.contains("abono") || lower.contains("recibiste") ||
                lower.contains("recibió") || lower.contains("recibio") ||
                lower.contains("débito") || lower.contains("debito") ||
                lower.contains("consignación") || lower.contains("consignacion")
        if (!hasAction) return null

        // Busca cualquier monto en pesos con $ acompañado de términos bancarios, 
        // soportando el signo menos opcional que usan algunos bancos (ej: -$ 11.000)
        val amountPattern = Pattern.compile("(?i)(?:\\$|COP|\\$ -|\\$ -|\\$-)\\s*([\\d\\.,]+)")
        val matcher = amountPattern.matcher(text)
        if (matcher.find()) {
            val amountStr = matcher.group(1) ?: ""
            val cents = parseCopAmountToCents(amountStr)
            
            val isIncome = lower.contains("abono") ||
                    lower.contains("recibio") ||
                    lower.contains("recibió") ||
                    lower.contains("recibiste") ||
                    lower.contains("transfirieron") ||
                    lower.contains("intereses")

            // Umbral mínimo: $1.000 COP para evitar falsos positivos, excepto para ingresos/intereses
            val minAmount = if (isIncome) 0L else 100000L

            if (cents > minAmount) {
                val isTransfer = lower.contains("transferencia") || 
                                lower.contains("transferiste") || 
                                lower.contains("ahorro") || 
                                lower.contains("inversion") || 
                                lower.contains("inversión") ||
                                lower.contains("nequi") ||
                                lower.contains(" daviplata")

                return ParsedBankTransaction(
                    source = source,
                    bankOrigin = bank,
                    amountCents = cents,
                    merchant = bank.displayName,
                    type = when {
                        isIncome -> TransactionType.INCOME
                        isTransfer -> TransactionType.TRANSFER_OUT
                        else -> TransactionType.EXPENSE
                    },
                    rawText = text,
                    timestampMillis = timestamp,
                    accountFilterHint = bank.displayName
                )
            }
        }
        return null
    }

    /**
     * Determina si el texto corresponde al pago de una tarjeta de crédito (habitualmente por PSE).
     * Requiere una referencia explícita a **tarjeta** y un verbo de pago, y descarta las
     * compras (que son gastos, no pagos de tarjeta).
     */
    private fun looksLikeCardPayment(text: String): Boolean {
        val lower = text.lowercase()
        val hasCardRef = lower.contains("tarjeta") || lower.contains(" nu ") || lower.contains("rappicard")
        val hasPseRef = lower.contains("pse")
        // Una "compra" es un gasto, no el pago de la tarjeta.
        val isPurchase = lower.contains("compra") && !lower.contains("pago de tarjeta") && !lower.contains("pago pse")
        return (hasCardRef || hasPseRef) && hasPaymentVerb(lower) && !isPurchase
    }

    /**
     * Determina si el texto corresponde al pago de un crédito/préstamo (habitualmente por PSE,
     * ej. Banco de Bogotá). Requiere referencia a crédito/préstamo/obligación y un verbo de pago,
     * y excluye todo lo que sea una tarjeta de crédito (que se maneja aparte).
     */
    private fun looksLikeDebtPayment(text: String): Boolean {
        val lower = text.lowercase()
        // "tarjeta de crédito" contiene "crédito", pero es una tarjeta: no es un préstamo.
        if (lower.contains("tarjeta")) return false
        val hasLoanRef = lower.contains("credito") || lower.contains("crédito") ||
            lower.contains("prestamo") || lower.contains("préstamo") ||
            lower.contains("obligacion") || lower.contains("obligación")
        // Una "compra" es un gasto, no el pago del crédito.
        val isPurchase = lower.contains("compra") && !lower.contains("pago")
        return hasLoanRef && hasPaymentVerb(lower) && !isPurchase
    }

    /** Verbos/frases que indican que un aviso corresponde al pago de una obligación. */
    private fun hasPaymentVerb(lower: String): Boolean =
        lower.contains("pago") ||
            lower.contains("pagaste") ||
            lower.contains("abono") ||
            lower.contains("aplicad") ||
            lower.contains("se realizó el pago") ||
            lower.contains("se realizo el pago") ||
            lower.contains("recibimos")

    /** Extrae el monto del pago de tarjeta a partir del primer valor monetario del texto. */
    private fun extractCardPaymentAmountCents(text: String): Long {
        val pattern = Pattern.compile("(?i)(?:\\$|COP)\\s*([\\d\\.,]+)")
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            val cents = parseCopAmountToCents(matcher.group(1) ?: "")
            if (cents > 0L) return cents
        }
        return 0L
    }

    /** Extrae los últimos 4 dígitos de la tarjeta si el texto referencia una (ej. "****1234"). */
    private fun extractCardLastFour(text: String): String? {
        val patterns = listOf(
            Pattern.compile("\\*{2,}\\s*(\\d{4})"),
            Pattern.compile("(?i)(?:terminada|terminación|terminacion|finaliza(?:da)?|termina)\\s*(?:en)?\\s*[*xX]*\\s*(\\d{4})"),
            Pattern.compile("(?:[*xX]{2,})(\\d{4})")
        )
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return matcher.group(1)
            }
        }
        return null
    }

    /** Extrae una pista del producto de crédito mencionado en el aviso (ej. "credito de consumo"). */
    private fun extractDebtHint(text: String): String? {
        val pattern = Pattern.compile(
            "(?i)(credito|crédito|prestamo|préstamo|obligacion|obligación)[\\s\\w]{0,25}"
        )
        val matcher = pattern.matcher(text)
        return if (matcher.find()) matcher.group(0)?.trim()?.lowercase() else null
    }

    private fun cleanMerchantName(raw: String): String {
        return raw.trim()
            .replace(Regex("(?i)^(en|a|el|la|los|las|de)\\s+"), "")
            .replace(Regex("(?i)\\s*fue aprobada.*$"), "")
            .replace(Regex("(?i)\\s*aprobada.*$"), "")
            .replace(Regex("(?i)\\s*exitosa.*$"), "")
            .replace(Regex("(?i)\\s*con (?:tu |su )?RappiCard.*$"), "")
            .replace(Regex("(?i)\\s*con (?:tu |su )?tarjeta.*$"), "")
            .replace(Regex("\\s+con\\s+su.*$"), "")
            .replace(Regex("\\s+T\\.Cred.*$"), "")
            .replace(Regex("\\s+T\\.Deb.*$"), "")
            .replace(Regex("\\s+por.*$"), "")
            .trim()
            .ifBlank { "Comercio" }
    }
}
