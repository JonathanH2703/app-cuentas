package com.personalfinance.cuentas.domain.backup

import com.personalfinance.cuentas.data.local.entities.AccountEntity
import com.personalfinance.cuentas.data.local.entities.AccountType
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.AllocationModelEntity
import com.personalfinance.cuentas.data.local.entities.BlockType
import com.personalfinance.cuentas.data.local.entities.BudgetEntity
import com.personalfinance.cuentas.data.local.entities.CapturePatternType
import com.personalfinance.cuentas.data.local.entities.CaptureRuleEntity
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import com.personalfinance.cuentas.data.local.entities.ContactEntity
import com.personalfinance.cuentas.data.local.entities.ContactKind
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity
import com.personalfinance.cuentas.data.local.entities.DebtEntity
import com.personalfinance.cuentas.data.local.entities.DebtPaymentEntity
import com.personalfinance.cuentas.data.local.entities.IncomeAllocationEntity
import com.personalfinance.cuentas.data.local.entities.IncomeEntity
import com.personalfinance.cuentas.data.local.entities.IncomeFrequency
import com.personalfinance.cuentas.data.local.entities.MerchantLearningEntity
import com.personalfinance.cuentas.data.local.entities.PendingSource
import com.personalfinance.cuentas.data.local.entities.PendingStatus
import com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity
import com.personalfinance.cuentas.data.local.entities.ReceivableEntity
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity
import com.personalfinance.cuentas.data.local.entities.SavingsGoalEntity
import com.personalfinance.cuentas.data.local.entities.TagEntity
import com.personalfinance.cuentas.data.local.entities.TransactionEntity
import com.personalfinance.cuentas.data.local.entities.TransactionTagEntity
import com.personalfinance.cuentas.data.local.entities.TransactionType
import org.json.JSONArray
import org.json.JSONObject

object BackupJsonSerializer {

    fun serialize(payload: BackupPayload): String {
        val root = JSONObject().apply {
            put("schema_version", payload.schemaVersion)
            put("exported_at_millis", payload.exportedAtMillis)
            put("app_version", payload.appVersion)

            // Accounts
            put("accounts", JSONArray().apply {
                payload.accounts.forEach { acc ->
                    put(JSONObject().apply {
                        put("id", acc.id)
                        put("name", acc.name)
                        put("type", acc.type.name)
                        put("current_balance_cents", acc.currentBalanceCents)
                        put("color_hex", acc.colorHex)
                        put("icon_name", acc.iconName)
                        put("is_archived", acc.isArchived)
                        put("include_in_total", acc.includeInTotal)
                        put("is_exempt_from_gmf", acc.isExemptFromGmf)
                        if (acc.annualYieldRate != null) {
                            put("annual_yield_rate", acc.annualYieldRate)
                        } else {
                            put("annual_yield_rate", JSONObject.NULL)
                        }
                        put("is_static_saving", acc.isStaticSaving)
                        put("created_at", acc.createdAt)
                    })
                }
            })

            // Categories
            put("categories", JSONArray().apply {
                payload.categories.forEach { cat ->
                    put(JSONObject().apply {
                        put("id", cat.id)
                        put("name", cat.name)
                        put("icon_name", cat.iconName)
                        put("color_hex", cat.colorHex)
                        put("default_block_type", cat.defaultBlockType.name)
                        put("is_system", cat.isSystem)
                    })
                }
            })

            // Tags
            put("tags", JSONArray().apply {
                payload.tags.forEach { tag ->
                    put(JSONObject().apply {
                        put("id", tag.id)
                        put("name", tag.name)
                    })
                }
            })

            // Allocation Models & Blocks
            put("allocation_models", JSONArray().apply {
                payload.allocationModels.forEach { model ->
                    put(JSONObject().apply {
                        put("id", model.id)
                        put("name", model.name)
                        put("is_active", model.isActive)
                        put("created_at", model.createdAt)
                    })
                }
            })

            put("allocation_blocks", JSONArray().apply {
                payload.allocationBlocks.forEach { block ->
                    put(JSONObject().apply {
                        put("id", block.id)
                        put("model_id", block.modelId)
                        put("name", block.name)
                        put("percentage", block.percentage)
                        put("type", block.type.name)
                        put("color_hex", block.colorHex)
                        put("order_index", block.orderIndex)
                        put("absorb_remainder", block.absorbRemainder)
                    })
                }
            })

            // Transactions
            put("transactions", JSONArray().apply {
                payload.transactions.forEach { tx ->
                    put(JSONObject().apply {
                        put("id", tx.id)
                        put("account_id", tx.accountId)
                        put("category_id", tx.categoryId)
                        if (tx.blockId != null) put("block_id", tx.blockId)
                        put("amount_cents", tx.amountCents)
                        put("type", tx.type.name)
                        put("date_millis", tx.dateMillis)
                        if (tx.note != null) put("note", tx.note)
                        if (tx.merchant != null) put("merchant", tx.merchant)
                        if (tx.linkedDebtId != null) put("linked_debt_id", tx.linkedDebtId)
                        if (tx.linkedRecurringItemId != null) put("linked_recurring_item_id", tx.linkedRecurringItemId)
                        if (tx.linkedReceivableId != null) put("linked_receivable_id", tx.linkedReceivableId)
                        put("is_leisure", tx.isLeisure)
                        put("created_at", tx.createdAt)
                    })
                }
            })

            // Transaction Tags
            put("transaction_tags", JSONArray().apply {
                payload.transactionTags.forEach { tt ->
                    put(JSONObject().apply {
                        put("transaction_id", tt.transactionId)
                        put("tag_id", tt.tagId)
                    })
                }
            })

            // Incomes
            put("incomes", JSONArray().apply {
                payload.incomes.forEach { inc ->
                    put(JSONObject().apply {
                        put("id", inc.id)
                        put("account_id", inc.accountId)
                        put("description", inc.description)
                        put("amount_cents", inc.amountCents)
                        put("frequency", inc.frequency.name)
                        put("expected_date_millis", inc.expectedDateMillis)
                        if (inc.receivedDateMillis != null) put("received_date_millis", inc.receivedDateMillis)
                        put("is_received", inc.isReceived)
                        put("created_at", inc.createdAt)
                    })
                }
            })

            put("income_allocations", JSONArray().apply {
                payload.incomeAllocations.forEach { ia ->
                    put(JSONObject().apply {
                        put("id", ia.id)
                        put("income_id", ia.incomeId)
                        put("block_id", ia.blockId)
                        put("assigned_cents", ia.assignedCents)
                    })
                }
            })

            // Credit Cards
            put("credit_cards", JSONArray().apply {
                payload.creditCards.forEach { card ->
                    put(JSONObject().apply {
                        put("id", card.id)
                        put("account_id", card.accountId)
                        put("credit_limit_cents", card.creditLimitCents)
                        put("cut_off_day", card.cutOffDay)
                        put("payment_due_day", card.paymentDueDay)
                        put("interest_rate_ea", card.interestRateEa)
                        put("handling_fee_cents", card.handlingFeeCents)
                        put("revolving_rate", card.revolvingRate)
                        put("monthly_insurance_cents", card.monthlyInsuranceCents)
                        if (card.lastFourDigits != null) put("last_four_digits", card.lastFourDigits)
                    })
                }
            })

            put("card_purchases", JSONArray().apply {
                payload.cardPurchases.forEach { cp ->
                    put(JSONObject().apply {
                        put("id", cp.id)
                        put("card_id", cp.cardId)
                        put("merchant", cp.merchant)
                        put("purchase_date_millis", cp.purchaseDateMillis)
                        put("total_amount_cents", cp.totalAmountCents)
                        put("installments_total", cp.installmentsTotal)
                        put("installments_paid", cp.installmentsPaid)
                        put("interest_rate_nm", cp.interestRateNm)
                        put("is_fully_paid", cp.isFullyPaid)
                    })
                }
            })

            // Debts
            put("debts", JSONArray().apply {
                payload.debts.forEach { debt ->
                    put(JSONObject().apply {
                        put("id", debt.id)
                        put("name", debt.name)
                        put("principal_amount_cents", debt.principalAmountCents)
                        put("current_balance_cents", debt.currentBalanceCents)
                        put("interest_rate_ea", debt.interestRateEa)
                        put("monthly_installment_cents", debt.monthlyInstallmentCents)
                        put("due_day_of_month", debt.dueDayOfMonth)
                        put("monthly_insurance_cents", debt.monthlyInsuranceCents)
                        put("start_date_millis", debt.startDateMillis)
                        if (debt.nextDueDateMillis != null) put("next_due_date_millis", debt.nextDueDateMillis)
                        put("is_settled", debt.isSettled)
                    })
                }
            })

            put("debt_payments", JSONArray().apply {
                payload.debtPayments.forEach { dp ->
                    put(JSONObject().apply {
                        put("id", dp.id)
                        put("debt_id", dp.debtId)
                        put("payment_date_millis", dp.paymentDateMillis)
                        put("total_paid_cents", dp.totalPaidCents)
                        put("principal_paid_cents", dp.principalPaidCents)
                        put("interest_paid_cents", dp.interestPaidCents)
                        put("is_extra_payment", dp.isExtraPayment)
                    })
                }
            })

            // Budgets & Savings
            put("budgets", JSONArray().apply {
                payload.budgets.forEach { b ->
                    put(JSONObject().apply {
                        put("id", b.id)
                        put("block_id", b.blockId)
                        if (b.categoryId != null) put("category_id", b.categoryId)
                        put("month", b.month)
                        put("year", b.year)
                        put("limit_amount_cents", b.limitAmountCents)
                    })
                }
            })

            put("savings_goals", JSONArray().apply {
                payload.savingsGoals.forEach { sg ->
                    put(JSONObject().apply {
                        put("id", sg.id)
                        put("name", sg.name)
                        put("target_amount_cents", sg.targetAmountCents)
                        put("current_saved_cents", sg.currentSavedCents)
                        if (sg.targetDateMillis != null) put("target_date_millis", sg.targetDateMillis)
                        put("is_emergency_fund", sg.isEmergencyFund)
                        put("is_completed", sg.isCompleted)
                    })
                }
            })

            // Recurring & Rules
            put("recurring_items", JSONArray().apply {
                payload.recurringItems.forEach { ri ->
                    put(JSONObject().apply {
                        put("id", ri.id)
                        put("account_id", ri.accountId)
                        put("category_id", ri.categoryId)
                        put("block_id", ri.blockId)
                        put("title", ri.title)
                        put("amount_cents", ri.amountCents)
                        put("frequency", ri.frequency.name)
                        put("next_due_date_millis", ri.nextDueDateMillis)
                        put("notify_days_before", ri.notifyDaysBefore)
                        put("is_active", ri.isActive)
                    })
                }
            })

            put("capture_rules", JSONArray().apply {
                payload.captureRules.forEach { cr ->
                    put(JSONObject().apply {
                        put("id", cr.id)
                        put("sender_filter", cr.senderFilter)
                        put("pattern_type", cr.patternType.name)
                        put("regex_pattern", cr.regexPattern)
                        put("extracted_type", cr.extractedType.name)
                        if (cr.defaultAccountId != null) put("default_account_id", cr.defaultAccountId)
                        put("confidence_score", cr.confidenceScore.toDouble())
                        put("is_active", cr.isActive)
                    })
                }
            })

            // Pending Transactions & Learnings
            put("pending_transactions", JSONArray().apply {
                payload.pendingTransactions.forEach { pt ->
                    put(JSONObject().apply {
                        put("id", pt.id)
                        put("source", pt.source.name)
                        put("source_sender", pt.sourceSender)
                        if (pt.rawText != null) put("raw_text", pt.rawText)
                        put("deduplication_hash", pt.deduplicationHash)
                        put("suggested_amount_cents", pt.suggestedAmountCents)
                        if (pt.suggestedMerchant != null) put("suggested_merchant", pt.suggestedMerchant)
                        if (pt.suggestedAccountId != null) put("suggested_account_id", pt.suggestedAccountId)
                        if (pt.suggestedCategoryId != null) put("suggested_category_id", pt.suggestedCategoryId)
                        if (pt.suggestedBlockId != null) put("suggested_block_id", pt.suggestedBlockId)
                        if (pt.suggestedCardId != null) put("suggested_card_id", pt.suggestedCardId)
                        if (pt.suggestedDebtId != null) put("suggested_debt_id", pt.suggestedDebtId)
                        put("suggested_type", pt.suggestedType.name)
                        put("detected_date_millis", pt.detectedDateMillis)
                        put("status", pt.status.name)
                    })
                }
            })

            put("merchant_learnings", JSONArray().apply {
                payload.merchantLearnings.forEach { ml ->
                    put(JSONObject().apply {
                        put("id", ml.id)
                        put("normalized_merchant", ml.normalizedMerchant)
                        put("category_id", ml.categoryId)
                        put("block_id", ml.blockId)
                        put("hit_count", ml.hitCount)
                    })
                }
            })

            // Cuentas por cobrar ("Me deben")
            put("contacts", JSONArray().apply {
                payload.contacts.forEach { c ->
                    put(JSONObject().apply {
                        put("id", c.id)
                        put("name", c.name)
                        put("kind", c.kind.name)
                        put("created_at", c.createdAt)
                    })
                }
            })

            put("receivables", JSONArray().apply {
                payload.receivables.forEach { r ->
                    put(JSONObject().apply {
                        put("id", r.id)
                        put("contact_id", r.contactId)
                        if (r.sourceTransactionId != null) put("source_transaction_id", r.sourceTransactionId)
                        if (r.note != null) put("note", r.note)
                        put("owed_cents", r.owedCents)
                        put("is_settled", r.isSettled)
                        if (r.settledAtMillis != null) put("settled_at_millis", r.settledAtMillis)
                        if (r.settledAccountId != null) put("settled_account_id", r.settledAccountId)
                        if (r.settledTransactionId != null) put("settled_transaction_id", r.settledTransactionId)
                        put("created_at", r.createdAt)
                    })
                }
            })
        }

        return root.toString(2)
    }

    fun deserialize(jsonString: String): BackupPayload {
        val root = JSONObject(jsonString)

        val schemaVersion = root.optInt("schema_version", 1)
        val exportedAtMillis = root.optLong("exported_at_millis", System.currentTimeMillis())
        val appVersion = root.optString("app_version", "1.0.0")

        val accounts = mutableListOf<AccountEntity>()
        root.optJSONArray("accounts")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                accounts.add(
                    AccountEntity(
                        id = o.getLong("id"),
                        name = o.getString("name"),
                        type = AccountType.valueOf(o.getString("type")),
                        currentBalanceCents = o.getLong("current_balance_cents"),
                        colorHex = o.optString("color_hex", "#006C4C"),
                        iconName = o.optString("icon_name", "account_balance"),
                        isArchived = o.optBoolean("is_archived", false),
                        includeInTotal = o.optBoolean("include_in_total", true),
                        isExemptFromGmf = o.optBoolean("is_exempt_from_gmf", false),
                        annualYieldRate = if (o.has("annual_yield_rate") && !o.isNull("annual_yield_rate")) o.optDouble("annual_yield_rate") else null,
                        isStaticSaving = o.optBoolean("is_static_saving", false),
                        createdAt = o.optLong("created_at", System.currentTimeMillis())
                    )
                )
            }
        }

        val categories = mutableListOf<CategoryEntity>()
        root.optJSONArray("categories")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                categories.add(
                    CategoryEntity(
                        id = o.getLong("id"),
                        name = o.getString("name"),
                        iconName = o.optString("icon_name", "category"),
                        colorHex = o.optString("color_hex", "#3F51B5"),
                        defaultBlockType = BlockType.valueOf(o.optString("default_block_type", BlockType.NEEDS.name)),
                        isSystem = o.optBoolean("is_system", false)
                    )
                )
            }
        }

        val tags = mutableListOf<TagEntity>()
        root.optJSONArray("tags")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                tags.add(TagEntity(id = o.getLong("id"), name = o.getString("name")))
            }
        }

        val allocationModels = mutableListOf<AllocationModelEntity>()
        root.optJSONArray("allocation_models")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                allocationModels.add(
                    AllocationModelEntity(
                        id = o.getLong("id"),
                        name = o.getString("name"),
                        isActive = o.optBoolean("is_active", false),
                        createdAt = o.optLong("created_at", System.currentTimeMillis())
                    )
                )
            }
        }

        val allocationBlocks = mutableListOf<AllocationBlockEntity>()
        root.optJSONArray("allocation_blocks")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                allocationBlocks.add(
                    AllocationBlockEntity(
                        id = o.getLong("id"),
                        modelId = o.getLong("model_id"),
                        name = o.getString("name"),
                        percentage = o.getInt("percentage"),
                        type = BlockType.valueOf(o.getString("type")),
                        colorHex = o.optString("color_hex", "#2E7D32"),
                        orderIndex = o.optInt("order_index", 0),
                        absorbRemainder = o.optBoolean("absorb_remainder", false)
                    )
                )
            }
        }

        val transactions = mutableListOf<TransactionEntity>()
        root.optJSONArray("transactions")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                transactions.add(
                    TransactionEntity(
                        id = o.getLong("id"),
                        accountId = o.getLong("account_id"),
                        categoryId = o.getLong("category_id"),
                        blockId = if (o.has("block_id")) o.getLong("block_id") else null,
                        amountCents = o.getLong("amount_cents"),
                        type = TransactionType.valueOf(o.getString("type")),
                        dateMillis = o.getLong("date_millis"),
                        note = if (o.has("note")) o.getString("note") else null,
                        merchant = if (o.has("merchant")) o.getString("merchant") else null,
                        linkedDebtId = if (o.has("linked_debt_id")) o.getLong("linked_debt_id") else null,
                        linkedRecurringItemId = if (o.has("linked_recurring_item_id")) o.getLong("linked_recurring_item_id") else null,
                        linkedReceivableId = if (o.has("linked_receivable_id")) o.getLong("linked_receivable_id") else null,
                        isLeisure = o.optBoolean("is_leisure", false),
                        createdAt = o.optLong("created_at", System.currentTimeMillis())
                    )
                )
            }
        }

        val transactionTags = mutableListOf<TransactionTagEntity>()
        root.optJSONArray("transaction_tags")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                transactionTags.add(
                    TransactionTagEntity(
                        transactionId = o.getLong("transaction_id"),
                        tagId = o.getLong("tag_id")
                    )
                )
            }
        }

        val incomes = mutableListOf<IncomeEntity>()
        root.optJSONArray("incomes")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                incomes.add(
                    IncomeEntity(
                        id = o.getLong("id"),
                        accountId = o.getLong("account_id"),
                        description = o.getString("description"),
                        amountCents = o.getLong("amount_cents"),
                        frequency = IncomeFrequency.valueOf(o.optString("frequency", IncomeFrequency.MONTHLY.name)),
                        expectedDateMillis = o.getLong("expected_date_millis"),
                        receivedDateMillis = if (o.has("received_date_millis")) o.getLong("received_date_millis") else null,
                        isReceived = o.optBoolean("is_received", false),
                        createdAt = o.optLong("created_at", System.currentTimeMillis())
                    )
                )
            }
        }

        val incomeAllocations = mutableListOf<IncomeAllocationEntity>()
        root.optJSONArray("income_allocations")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                incomeAllocations.add(
                    IncomeAllocationEntity(
                        id = o.getLong("id"),
                        incomeId = o.getLong("income_id"),
                        blockId = o.getLong("block_id"),
                        assignedCents = o.getLong("assigned_cents")
                    )
                )
            }
        }

        val creditCards = mutableListOf<CreditCardEntity>()
        root.optJSONArray("credit_cards")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                creditCards.add(
                    CreditCardEntity(
                        id = o.getLong("id"),
                        accountId = o.getLong("account_id"),
                        creditLimitCents = o.getLong("credit_limit_cents"),
                        cutOffDay = o.getInt("cut_off_day"),
                        paymentDueDay = o.getInt("payment_due_day"),
                        interestRateEa = o.getDouble("interest_rate_ea"),
                        handlingFeeCents = o.optLong("handling_fee_cents", 0L),
                        revolvingRate = o.optDouble("revolving_rate", 1.0),
                        monthlyInsuranceCents = o.optLong("monthly_insurance_cents", 0L),
                        lastFourDigits = if (o.has("last_four_digits")) o.getString("last_four_digits") else null
                    )
                )
            }
        }

        val cardPurchases = mutableListOf<CardPurchaseEntity>()
        root.optJSONArray("card_purchases")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                cardPurchases.add(
                    CardPurchaseEntity(
                        id = o.getLong("id"),
                        cardId = o.getLong("card_id"),
                        merchant = o.getString("merchant"),
                        purchaseDateMillis = o.getLong("purchase_date_millis"),
                        totalAmountCents = o.getLong("total_amount_cents"),
                        installmentsTotal = o.optInt("installments_total", 1),
                        installmentsPaid = o.optInt("installments_paid", 0),
                        interestRateNm = o.optDouble("interest_rate_nm", 0.0),
                        isFullyPaid = o.optBoolean("is_fully_paid", false)
                    )
                )
            }
        }

        val debts = mutableListOf<DebtEntity>()
        root.optJSONArray("debts")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                debts.add(
                    DebtEntity(
                        id = o.getLong("id"),
                        name = o.getString("name"),
                        principalAmountCents = o.getLong("principal_amount_cents"),
                        currentBalanceCents = o.getLong("current_balance_cents"),
                        interestRateEa = o.getDouble("interest_rate_ea"),
                        monthlyInstallmentCents = o.getLong("monthly_installment_cents"),
                        dueDayOfMonth = o.getInt("due_day_of_month"),
                        monthlyInsuranceCents = o.optLong("monthly_insurance_cents", 0L),
                        startDateMillis = o.optLong("start_date_millis", System.currentTimeMillis()),
                        nextDueDateMillis = if (o.has("next_due_date_millis")) o.getLong("next_due_date_millis") else null,
                        isSettled = o.optBoolean("is_settled", false)
                    )
                )
            }
        }

        val debtPayments = mutableListOf<DebtPaymentEntity>()
        root.optJSONArray("debt_payments")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                debtPayments.add(
                    DebtPaymentEntity(
                        id = o.getLong("id"),
                        debtId = o.getLong("debt_id"),
                        paymentDateMillis = o.getLong("payment_date_millis"),
                        totalPaidCents = o.getLong("total_paid_cents"),
                        principalPaidCents = o.getLong("principal_paid_cents"),
                        interestPaidCents = o.getLong("interest_paid_cents"),
                        isExtraPayment = o.optBoolean("is_extra_payment", false)
                    )
                )
            }
        }

        val budgets = mutableListOf<BudgetEntity>()
        root.optJSONArray("budgets")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                budgets.add(
                    BudgetEntity(
                        id = o.getLong("id"),
                        blockId = o.getLong("block_id"),
                        categoryId = if (o.has("category_id")) o.getLong("category_id") else null,
                        month = o.getInt("month"),
                        year = o.getInt("year"),
                        limitAmountCents = o.getLong("limit_amount_cents")
                    )
                )
            }
        }

        val savingsGoals = mutableListOf<SavingsGoalEntity>()
        root.optJSONArray("savings_goals")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                savingsGoals.add(
                    SavingsGoalEntity(
                        id = o.getLong("id"),
                        name = o.getString("name"),
                        targetAmountCents = o.getLong("target_amount_cents"),
                        currentSavedCents = o.optLong("current_saved_cents", 0L),
                        targetDateMillis = if (o.has("target_date_millis")) o.getLong("target_date_millis") else null,
                        isEmergencyFund = o.optBoolean("is_emergency_fund", false),
                        isCompleted = o.optBoolean("is_completed", false)
                    )
                )
            }
        }

        val recurringItems = mutableListOf<RecurringItemEntity>()
        root.optJSONArray("recurring_items")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                recurringItems.add(
                    RecurringItemEntity(
                        id = o.getLong("id"),
                        accountId = o.getLong("account_id"),
                        categoryId = o.getLong("category_id"),
                        blockId = o.getLong("block_id"),
                        title = o.getString("title"),
                        amountCents = o.getLong("amount_cents"),
                        frequency = IncomeFrequency.valueOf(o.optString("frequency", IncomeFrequency.MONTHLY.name)),
                        nextDueDateMillis = o.getLong("next_due_date_millis"),
                        notifyDaysBefore = o.optInt("notify_days_before", 2),
                        isActive = o.optBoolean("is_active", true)
                    )
                )
            }
        }

        val captureRules = mutableListOf<CaptureRuleEntity>()
        root.optJSONArray("capture_rules")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                captureRules.add(
                    CaptureRuleEntity(
                        id = o.getLong("id"),
                        senderFilter = o.getString("sender_filter"),
                        patternType = CapturePatternType.valueOf(o.getString("pattern_type")),
                        regexPattern = o.getString("regex_pattern"),
                        extractedType = TransactionType.valueOf(o.optString("extracted_type", TransactionType.EXPENSE.name)),
                        defaultAccountId = if (o.has("default_account_id")) o.getLong("default_account_id") else null,
                        confidenceScore = o.optDouble("confidence_score", 0.9).toFloat(),
                        isActive = o.optBoolean("is_active", true)
                    )
                )
            }
        }

        val pendingTransactions = mutableListOf<PendingTransactionEntity>()
        root.optJSONArray("pending_transactions")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                pendingTransactions.add(
                    PendingTransactionEntity(
                        id = o.getLong("id"),
                        source = PendingSource.valueOf(o.getString("source")),
                        sourceSender = o.getString("source_sender"),
                        rawText = if (o.has("raw_text")) o.getString("raw_text") else null,
                        deduplicationHash = o.getString("deduplication_hash"),
                        suggestedAmountCents = o.getLong("suggested_amount_cents"),
                        suggestedMerchant = if (o.has("suggested_merchant")) o.getString("suggested_merchant") else null,
                        suggestedAccountId = if (o.has("suggested_account_id")) o.getLong("suggested_account_id") else null,
                        suggestedCategoryId = if (o.has("suggested_category_id")) o.getLong("suggested_category_id") else null,
                        suggestedBlockId = if (o.has("suggested_block_id")) o.getLong("suggested_block_id") else null,
                        suggestedCardId = if (o.has("suggested_card_id")) o.getLong("suggested_card_id") else null,
                        suggestedDebtId = if (o.has("suggested_debt_id")) o.getLong("suggested_debt_id") else null,
                        suggestedType = TransactionType.valueOf(o.optString("suggested_type", TransactionType.EXPENSE.name)),
                        detectedDateMillis = o.optLong("detected_date_millis", System.currentTimeMillis()),
                        status = PendingStatus.valueOf(o.optString("status", PendingStatus.PENDING.name))
                    )
                )
            }
        }

        val merchantLearnings = mutableListOf<MerchantLearningEntity>()
        root.optJSONArray("merchant_learnings")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                merchantLearnings.add(
                    MerchantLearningEntity(
                        id = o.getLong("id"),
                        normalizedMerchant = o.getString("normalized_merchant"),
                        categoryId = o.getLong("category_id"),
                        blockId = o.getLong("block_id"),
                        hitCount = o.optInt("hit_count", 1)
                    )
                )
            }
        }

        val contacts = mutableListOf<ContactEntity>()
        root.optJSONArray("contacts")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                contacts.add(
                    ContactEntity(
                        id = o.getLong("id"),
                        name = o.getString("name"),
                        kind = ContactKind.valueOf(o.optString("kind", ContactKind.PERSON.name)),
                        createdAt = o.optLong("created_at", System.currentTimeMillis())
                    )
                )
            }
        }

        val receivables = mutableListOf<ReceivableEntity>()
        root.optJSONArray("receivables")?.let { array ->
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                receivables.add(
                    ReceivableEntity(
                        id = o.getLong("id"),
                        contactId = o.getLong("contact_id"),
                        sourceTransactionId = if (o.has("source_transaction_id")) o.getLong("source_transaction_id") else null,
                        note = if (o.has("note")) o.getString("note") else null,
                        owedCents = o.getLong("owed_cents"),
                        isSettled = o.optBoolean("is_settled", false),
                        settledAtMillis = if (o.has("settled_at_millis")) o.getLong("settled_at_millis") else null,
                        settledAccountId = if (o.has("settled_account_id")) o.getLong("settled_account_id") else null,
                        settledTransactionId = if (o.has("settled_transaction_id")) o.getLong("settled_transaction_id") else null,
                        createdAt = o.optLong("created_at", System.currentTimeMillis())
                    )
                )
            }
        }

        return BackupPayload(
            schemaVersion = schemaVersion,
            exportedAtMillis = exportedAtMillis,
            appVersion = appVersion,
            accounts = accounts,
            categories = categories,
            tags = tags,
            allocationModels = allocationModels,
            allocationBlocks = allocationBlocks,
            transactions = transactions,
            transactionTags = transactionTags,
            incomes = incomes,
            incomeAllocations = incomeAllocations,
            creditCards = creditCards,
            cardPurchases = cardPurchases,
            debts = debts,
            debtPayments = debtPayments,
            budgets = budgets,
            savingsGoals = savingsGoals,
            recurringItems = recurringItems,
            captureRules = captureRules,
            pendingTransactions = pendingTransactions,
            merchantLearnings = merchantLearnings,
            contacts = contacts,
            receivables = receivables
        )
    }
}
