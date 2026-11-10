package com.personalfinance.cuentas.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.personalfinance.cuentas.data.local.entities.AccountEntity;
import com.personalfinance.cuentas.data.local.entities.AccountType;
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity;
import com.personalfinance.cuentas.data.local.entities.AllocationModelEntity;
import com.personalfinance.cuentas.data.local.entities.BlockType;
import com.personalfinance.cuentas.data.local.entities.BudgetEntity;
import com.personalfinance.cuentas.data.local.entities.CapturePatternType;
import com.personalfinance.cuentas.data.local.entities.CaptureRuleEntity;
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity;
import com.personalfinance.cuentas.data.local.entities.CategoryEntity;
import com.personalfinance.cuentas.data.local.entities.ContactEntity;
import com.personalfinance.cuentas.data.local.entities.ContactKind;
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity;
import com.personalfinance.cuentas.data.local.entities.DebtEntity;
import com.personalfinance.cuentas.data.local.entities.DebtPaymentEntity;
import com.personalfinance.cuentas.data.local.entities.IncomeAllocationEntity;
import com.personalfinance.cuentas.data.local.entities.IncomeEntity;
import com.personalfinance.cuentas.data.local.entities.IncomeFrequency;
import com.personalfinance.cuentas.data.local.entities.MerchantLearningEntity;
import com.personalfinance.cuentas.data.local.entities.PendingSource;
import com.personalfinance.cuentas.data.local.entities.PendingStatus;
import com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity;
import com.personalfinance.cuentas.data.local.entities.ReceivableEntity;
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity;
import com.personalfinance.cuentas.data.local.entities.SavingsGoalEntity;
import com.personalfinance.cuentas.data.local.entities.TagEntity;
import com.personalfinance.cuentas.data.local.entities.TransactionEntity;
import com.personalfinance.cuentas.data.local.entities.TransactionTagEntity;
import com.personalfinance.cuentas.data.local.entities.TransactionType;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BackupDao_Impl implements BackupDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AccountEntity> __insertionAdapterOfAccountEntity;

  private final EntityInsertionAdapter<CategoryEntity> __insertionAdapterOfCategoryEntity;

  private final EntityInsertionAdapter<TagEntity> __insertionAdapterOfTagEntity;

  private final EntityInsertionAdapter<AllocationModelEntity> __insertionAdapterOfAllocationModelEntity;

  private final EntityInsertionAdapter<AllocationBlockEntity> __insertionAdapterOfAllocationBlockEntity;

  private final EntityInsertionAdapter<TransactionEntity> __insertionAdapterOfTransactionEntity;

  private final EntityInsertionAdapter<TransactionTagEntity> __insertionAdapterOfTransactionTagEntity;

  private final EntityInsertionAdapter<IncomeEntity> __insertionAdapterOfIncomeEntity;

  private final EntityInsertionAdapter<IncomeAllocationEntity> __insertionAdapterOfIncomeAllocationEntity;

  private final EntityInsertionAdapter<CreditCardEntity> __insertionAdapterOfCreditCardEntity;

  private final EntityInsertionAdapter<CardPurchaseEntity> __insertionAdapterOfCardPurchaseEntity;

  private final EntityInsertionAdapter<DebtEntity> __insertionAdapterOfDebtEntity;

  private final EntityInsertionAdapter<DebtPaymentEntity> __insertionAdapterOfDebtPaymentEntity;

  private final EntityInsertionAdapter<BudgetEntity> __insertionAdapterOfBudgetEntity;

  private final EntityInsertionAdapter<SavingsGoalEntity> __insertionAdapterOfSavingsGoalEntity;

  private final EntityInsertionAdapter<RecurringItemEntity> __insertionAdapterOfRecurringItemEntity;

  private final EntityInsertionAdapter<CaptureRuleEntity> __insertionAdapterOfCaptureRuleEntity;

  private final EntityInsertionAdapter<PendingTransactionEntity> __insertionAdapterOfPendingTransactionEntity;

  private final EntityInsertionAdapter<MerchantLearningEntity> __insertionAdapterOfMerchantLearningEntity;

  private final EntityInsertionAdapter<ContactEntity> __insertionAdapterOfContactEntity;

  private final EntityInsertionAdapter<ReceivableEntity> __insertionAdapterOfReceivableEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearTransactionTags;

  private final SharedSQLiteStatement __preparedStmtOfClearTransactions;

  private final SharedSQLiteStatement __preparedStmtOfClearCardPurchases;

  private final SharedSQLiteStatement __preparedStmtOfClearCreditCards;

  private final SharedSQLiteStatement __preparedStmtOfClearDebtPayments;

  private final SharedSQLiteStatement __preparedStmtOfClearDebts;

  private final SharedSQLiteStatement __preparedStmtOfClearIncomeAllocations;

  private final SharedSQLiteStatement __preparedStmtOfClearIncomes;

  private final SharedSQLiteStatement __preparedStmtOfClearRecurringItems;

  private final SharedSQLiteStatement __preparedStmtOfClearBudgets;

  private final SharedSQLiteStatement __preparedStmtOfClearSavingsGoals;

  private final SharedSQLiteStatement __preparedStmtOfClearAllocationBlocks;

  private final SharedSQLiteStatement __preparedStmtOfClearAllocationModels;

  private final SharedSQLiteStatement __preparedStmtOfClearAccounts;

  private final SharedSQLiteStatement __preparedStmtOfClearCategories;

  private final SharedSQLiteStatement __preparedStmtOfClearTags;

  private final SharedSQLiteStatement __preparedStmtOfClearPendingTransactions;

  private final SharedSQLiteStatement __preparedStmtOfClearMerchantLearnings;

  private final SharedSQLiteStatement __preparedStmtOfClearCaptureRules;

  private final SharedSQLiteStatement __preparedStmtOfClearReceivables;

  private final SharedSQLiteStatement __preparedStmtOfClearContacts;

  public BackupDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAccountEntity = new EntityInsertionAdapter<AccountEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `accounts` (`id`,`name`,`type`,`current_balance_cents`,`color_hex`,`icon_name`,`is_archived`,`include_in_total`,`is_exempt_from_gmf`,`annual_yield_rate`,`is_static_saving`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AccountEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, __AccountType_enumToString(entity.getType()));
        statement.bindLong(4, entity.getCurrentBalanceCents());
        statement.bindString(5, entity.getColorHex());
        statement.bindString(6, entity.getIconName());
        final int _tmp = entity.isArchived() ? 1 : 0;
        statement.bindLong(7, _tmp);
        final int _tmp_1 = entity.getIncludeInTotal() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        final int _tmp_2 = entity.isExemptFromGmf() ? 1 : 0;
        statement.bindLong(9, _tmp_2);
        if (entity.getAnnualYieldRate() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getAnnualYieldRate());
        }
        final int _tmp_3 = entity.isStaticSaving() ? 1 : 0;
        statement.bindLong(11, _tmp_3);
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfCategoryEntity = new EntityInsertionAdapter<CategoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `categories` (`id`,`name`,`icon_name`,`color_hex`,`default_block_type`,`is_system`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CategoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getIconName());
        statement.bindString(4, entity.getColorHex());
        statement.bindString(5, __BlockType_enumToString(entity.getDefaultBlockType()));
        final int _tmp = entity.isSystem() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__insertionAdapterOfTagEntity = new EntityInsertionAdapter<TagEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tags` (`id`,`name`) VALUES (nullif(?, 0),?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TagEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
      }
    };
    this.__insertionAdapterOfAllocationModelEntity = new EntityInsertionAdapter<AllocationModelEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `allocation_models` (`id`,`name`,`is_active`,`created_at`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AllocationModelEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(3, _tmp);
        statement.bindLong(4, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfAllocationBlockEntity = new EntityInsertionAdapter<AllocationBlockEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `allocation_blocks` (`id`,`model_id`,`name`,`percentage`,`type`,`color_hex`,`order_index`,`absorb_remainder`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AllocationBlockEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getModelId());
        statement.bindString(3, entity.getName());
        statement.bindLong(4, entity.getPercentage());
        statement.bindString(5, __BlockType_enumToString(entity.getType()));
        statement.bindString(6, entity.getColorHex());
        statement.bindLong(7, entity.getOrderIndex());
        final int _tmp = entity.getAbsorbRemainder() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__insertionAdapterOfTransactionEntity = new EntityInsertionAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions` (`id`,`account_id`,`category_id`,`block_id`,`amount_cents`,`type`,`date_millis`,`note`,`merchant`,`linked_debt_id`,`linked_recurring_item_id`,`linked_receivable_id`,`is_leisure`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        statement.bindLong(3, entity.getCategoryId());
        if (entity.getBlockId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getBlockId());
        }
        statement.bindLong(5, entity.getAmountCents());
        statement.bindString(6, __TransactionType_enumToString(entity.getType()));
        statement.bindLong(7, entity.getDateMillis());
        if (entity.getNote() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getNote());
        }
        if (entity.getMerchant() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getMerchant());
        }
        if (entity.getLinkedDebtId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getLinkedDebtId());
        }
        if (entity.getLinkedRecurringItemId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getLinkedRecurringItemId());
        }
        if (entity.getLinkedReceivableId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getLinkedReceivableId());
        }
        final int _tmp = entity.isLeisure() ? 1 : 0;
        statement.bindLong(13, _tmp);
        statement.bindLong(14, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfTransactionTagEntity = new EntityInsertionAdapter<TransactionTagEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transaction_tags` (`transaction_id`,`tag_id`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionTagEntity entity) {
        statement.bindLong(1, entity.getTransactionId());
        statement.bindLong(2, entity.getTagId());
      }
    };
    this.__insertionAdapterOfIncomeEntity = new EntityInsertionAdapter<IncomeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `incomes` (`id`,`account_id`,`description`,`amount_cents`,`frequency`,`expected_date_millis`,`received_date_millis`,`is_received`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        statement.bindString(3, entity.getDescription());
        statement.bindLong(4, entity.getAmountCents());
        statement.bindString(5, __IncomeFrequency_enumToString(entity.getFrequency()));
        statement.bindLong(6, entity.getExpectedDateMillis());
        if (entity.getReceivedDateMillis() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getReceivedDateMillis());
        }
        final int _tmp = entity.isReceived() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfIncomeAllocationEntity = new EntityInsertionAdapter<IncomeAllocationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `income_allocations` (`id`,`income_id`,`block_id`,`assigned_cents`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeAllocationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getIncomeId());
        statement.bindLong(3, entity.getBlockId());
        statement.bindLong(4, entity.getAssignedCents());
      }
    };
    this.__insertionAdapterOfCreditCardEntity = new EntityInsertionAdapter<CreditCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `credit_cards` (`id`,`account_id`,`credit_limit_cents`,`cut_off_day`,`payment_due_day`,`interest_rate_ea`,`handling_fee_cents`,`last_four_digits`,`revolving_rate`,`monthly_insurance_cents`,`installment_type`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCardEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        statement.bindLong(3, entity.getCreditLimitCents());
        statement.bindLong(4, entity.getCutOffDay());
        statement.bindLong(5, entity.getPaymentDueDay());
        statement.bindDouble(6, entity.getInterestRateEa());
        statement.bindLong(7, entity.getHandlingFeeCents());
        if (entity.getLastFourDigits() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLastFourDigits());
        }
        statement.bindDouble(9, entity.getRevolvingRate());
        statement.bindLong(10, entity.getMonthlyInsuranceCents());
        statement.bindLong(11, entity.getInstallmentType());
      }
    };
    this.__insertionAdapterOfCardPurchaseEntity = new EntityInsertionAdapter<CardPurchaseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `card_purchases` (`id`,`card_id`,`merchant`,`purchase_date_millis`,`total_amount_cents`,`installments_total`,`installments_paid`,`interest_rate_nm`,`is_fully_paid`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CardPurchaseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCardId());
        statement.bindString(3, entity.getMerchant());
        statement.bindLong(4, entity.getPurchaseDateMillis());
        statement.bindLong(5, entity.getTotalAmountCents());
        statement.bindLong(6, entity.getInstallmentsTotal());
        statement.bindLong(7, entity.getInstallmentsPaid());
        statement.bindDouble(8, entity.getInterestRateNm());
        final int _tmp = entity.isFullyPaid() ? 1 : 0;
        statement.bindLong(9, _tmp);
      }
    };
    this.__insertionAdapterOfDebtEntity = new EntityInsertionAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `debts` (`id`,`name`,`principal_amount_cents`,`current_balance_cents`,`interest_rate_ea`,`monthly_installment_cents`,`due_day_of_month`,`start_date_millis`,`next_due_date_millis`,`monthly_insurance_cents`,`is_settled`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getPrincipalAmountCents());
        statement.bindLong(4, entity.getCurrentBalanceCents());
        statement.bindDouble(5, entity.getInterestRateEa());
        statement.bindLong(6, entity.getMonthlyInstallmentCents());
        statement.bindLong(7, entity.getDueDayOfMonth());
        statement.bindLong(8, entity.getStartDateMillis());
        if (entity.getNextDueDateMillis() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getNextDueDateMillis());
        }
        statement.bindLong(10, entity.getMonthlyInsuranceCents());
        final int _tmp = entity.isSettled() ? 1 : 0;
        statement.bindLong(11, _tmp);
      }
    };
    this.__insertionAdapterOfDebtPaymentEntity = new EntityInsertionAdapter<DebtPaymentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `debt_payments` (`id`,`debt_id`,`payment_date_millis`,`total_paid_cents`,`principal_paid_cents`,`interest_paid_cents`,`is_extra_payment`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtPaymentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDebtId());
        statement.bindLong(3, entity.getPaymentDateMillis());
        statement.bindLong(4, entity.getTotalPaidCents());
        statement.bindLong(5, entity.getPrincipalPaidCents());
        statement.bindLong(6, entity.getInterestPaidCents());
        final int _tmp = entity.isExtraPayment() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__insertionAdapterOfBudgetEntity = new EntityInsertionAdapter<BudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `budgets` (`id`,`block_id`,`category_id`,`month`,`year`,`limit_amount_cents`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BudgetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBlockId());
        if (entity.getCategoryId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getCategoryId());
        }
        statement.bindLong(4, entity.getMonth());
        statement.bindLong(5, entity.getYear());
        statement.bindLong(6, entity.getLimitAmountCents());
      }
    };
    this.__insertionAdapterOfSavingsGoalEntity = new EntityInsertionAdapter<SavingsGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `savings_goals` (`id`,`name`,`target_amount_cents`,`current_saved_cents`,`target_date_millis`,`is_emergency_fund`,`is_completed`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavingsGoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getTargetAmountCents());
        statement.bindLong(4, entity.getCurrentSavedCents());
        if (entity.getTargetDateMillis() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getTargetDateMillis());
        }
        final int _tmp = entity.isEmergencyFund() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
      }
    };
    this.__insertionAdapterOfRecurringItemEntity = new EntityInsertionAdapter<RecurringItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `recurring_items` (`id`,`account_id`,`category_id`,`block_id`,`title`,`amount_cents`,`frequency`,`next_due_date_millis`,`notify_days_before`,`is_active`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecurringItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        statement.bindLong(3, entity.getCategoryId());
        statement.bindLong(4, entity.getBlockId());
        statement.bindString(5, entity.getTitle());
        statement.bindLong(6, entity.getAmountCents());
        statement.bindString(7, __IncomeFrequency_enumToString(entity.getFrequency()));
        statement.bindLong(8, entity.getNextDueDateMillis());
        statement.bindLong(9, entity.getNotifyDaysBefore());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(10, _tmp);
      }
    };
    this.__insertionAdapterOfCaptureRuleEntity = new EntityInsertionAdapter<CaptureRuleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `capture_rules` (`id`,`sender_filter`,`pattern_type`,`regex_pattern`,`extracted_type`,`default_account_id`,`confidence_score`,`is_active`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CaptureRuleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSenderFilter());
        statement.bindString(3, __CapturePatternType_enumToString(entity.getPatternType()));
        statement.bindString(4, entity.getRegexPattern());
        statement.bindString(5, __TransactionType_enumToString(entity.getExtractedType()));
        if (entity.getDefaultAccountId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDefaultAccountId());
        }
        statement.bindDouble(7, entity.getConfidenceScore());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__insertionAdapterOfPendingTransactionEntity = new EntityInsertionAdapter<PendingTransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pending_transactions` (`id`,`source`,`source_sender`,`raw_text`,`deduplication_hash`,`suggested_amount_cents`,`suggested_merchant`,`suggested_account_id`,`suggested_category_id`,`suggested_block_id`,`suggested_type`,`suggested_card_id`,`suggested_debt_id`,`detected_date_millis`,`status`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PendingTransactionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, __PendingSource_enumToString(entity.getSource()));
        statement.bindString(3, entity.getSourceSender());
        if (entity.getRawText() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getRawText());
        }
        statement.bindString(5, entity.getDeduplicationHash());
        statement.bindLong(6, entity.getSuggestedAmountCents());
        if (entity.getSuggestedMerchant() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getSuggestedMerchant());
        }
        if (entity.getSuggestedAccountId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getSuggestedAccountId());
        }
        if (entity.getSuggestedCategoryId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getSuggestedCategoryId());
        }
        if (entity.getSuggestedBlockId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getSuggestedBlockId());
        }
        statement.bindString(11, __TransactionType_enumToString(entity.getSuggestedType()));
        if (entity.getSuggestedCardId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getSuggestedCardId());
        }
        if (entity.getSuggestedDebtId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getSuggestedDebtId());
        }
        statement.bindLong(14, entity.getDetectedDateMillis());
        statement.bindString(15, __PendingStatus_enumToString(entity.getStatus()));
      }
    };
    this.__insertionAdapterOfMerchantLearningEntity = new EntityInsertionAdapter<MerchantLearningEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `merchant_learnings` (`id`,`normalized_merchant`,`category_id`,`block_id`,`hit_count`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MerchantLearningEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNormalizedMerchant());
        statement.bindLong(3, entity.getCategoryId());
        statement.bindLong(4, entity.getBlockId());
        statement.bindLong(5, entity.getHitCount());
      }
    };
    this.__insertionAdapterOfContactEntity = new EntityInsertionAdapter<ContactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `contacts` (`id`,`name`,`kind`,`created_at`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContactEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, __ContactKind_enumToString(entity.getKind()));
        statement.bindLong(4, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfReceivableEntity = new EntityInsertionAdapter<ReceivableEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `receivables` (`id`,`contact_id`,`source_transaction_id`,`note`,`owed_cents`,`is_settled`,`settled_at_millis`,`settled_account_id`,`settled_transaction_id`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceivableEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getContactId());
        if (entity.getSourceTransactionId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getSourceTransactionId());
        }
        if (entity.getNote() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNote());
        }
        statement.bindLong(5, entity.getOwedCents());
        final int _tmp = entity.isSettled() ? 1 : 0;
        statement.bindLong(6, _tmp);
        if (entity.getSettledAtMillis() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getSettledAtMillis());
        }
        if (entity.getSettledAccountId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getSettledAccountId());
        }
        if (entity.getSettledTransactionId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getSettledTransactionId());
        }
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfClearTransactionTags = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transaction_tags";
        return _query;
      }
    };
    this.__preparedStmtOfClearTransactions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions";
        return _query;
      }
    };
    this.__preparedStmtOfClearCardPurchases = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM card_purchases";
        return _query;
      }
    };
    this.__preparedStmtOfClearCreditCards = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM credit_cards";
        return _query;
      }
    };
    this.__preparedStmtOfClearDebtPayments = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM debt_payments";
        return _query;
      }
    };
    this.__preparedStmtOfClearDebts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM debts";
        return _query;
      }
    };
    this.__preparedStmtOfClearIncomeAllocations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM income_allocations";
        return _query;
      }
    };
    this.__preparedStmtOfClearIncomes = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM incomes";
        return _query;
      }
    };
    this.__preparedStmtOfClearRecurringItems = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recurring_items";
        return _query;
      }
    };
    this.__preparedStmtOfClearBudgets = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM budgets";
        return _query;
      }
    };
    this.__preparedStmtOfClearSavingsGoals = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM savings_goals";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllocationBlocks = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM allocation_blocks";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllocationModels = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM allocation_models";
        return _query;
      }
    };
    this.__preparedStmtOfClearAccounts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM accounts";
        return _query;
      }
    };
    this.__preparedStmtOfClearCategories = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM categories";
        return _query;
      }
    };
    this.__preparedStmtOfClearTags = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tags";
        return _query;
      }
    };
    this.__preparedStmtOfClearPendingTransactions = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_transactions";
        return _query;
      }
    };
    this.__preparedStmtOfClearMerchantLearnings = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM merchant_learnings";
        return _query;
      }
    };
    this.__preparedStmtOfClearCaptureRules = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM capture_rules";
        return _query;
      }
    };
    this.__preparedStmtOfClearReceivables = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM receivables";
        return _query;
      }
    };
    this.__preparedStmtOfClearContacts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM contacts";
        return _query;
      }
    };
  }

  @Override
  public Object insertAccounts(final List<AccountEntity> accounts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAccountEntity.insert(accounts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCategories(final List<CategoryEntity> categories,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCategoryEntity.insert(categories);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTags(final List<TagEntity> tags,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTagEntity.insert(tags);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllocationModels(final List<AllocationModelEntity> models,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAllocationModelEntity.insert(models);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllocationBlocks(final List<AllocationBlockEntity> blocks,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAllocationBlockEntity.insert(blocks);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTransactions(final List<TransactionEntity> transactions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTransactionEntity.insert(transactions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTransactionTags(final List<TransactionTagEntity> transactionTags,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTransactionTagEntity.insert(transactionTags);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertIncomes(final List<IncomeEntity> incomes,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIncomeEntity.insert(incomes);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertIncomeAllocations(final List<IncomeAllocationEntity> allocations,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIncomeAllocationEntity.insert(allocations);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCreditCards(final List<CreditCardEntity> cards,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCreditCardEntity.insert(cards);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCardPurchases(final List<CardPurchaseEntity> purchases,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCardPurchaseEntity.insert(purchases);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertDebts(final List<DebtEntity> debts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDebtEntity.insert(debts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertDebtPayments(final List<DebtPaymentEntity> payments,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDebtPaymentEntity.insert(payments);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBudgets(final List<BudgetEntity> budgets,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBudgetEntity.insert(budgets);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSavingsGoals(final List<SavingsGoalEntity> goals,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSavingsGoalEntity.insert(goals);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertRecurringItems(final List<RecurringItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRecurringItemEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCaptureRules(final List<CaptureRuleEntity> rules,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCaptureRuleEntity.insert(rules);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPendingTransactions(final List<PendingTransactionEntity> pending,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPendingTransactionEntity.insert(pending);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMerchantLearnings(final List<MerchantLearningEntity> learnings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMerchantLearningEntity.insert(learnings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertContacts(final List<ContactEntity> contacts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfContactEntity.insert(contacts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertReceivables(final List<ReceivableEntity> receivables,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReceivableEntity.insert(receivables);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearTransactionTags(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearTransactionTags.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearTransactionTags.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearTransactions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearTransactions.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearTransactions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearCardPurchases(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearCardPurchases.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearCardPurchases.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearCreditCards(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearCreditCards.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearCreditCards.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearDebtPayments(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearDebtPayments.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearDebtPayments.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearDebts(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearDebts.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearDebts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearIncomeAllocations(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearIncomeAllocations.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearIncomeAllocations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearIncomes(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearIncomes.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearIncomes.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearRecurringItems(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearRecurringItems.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearRecurringItems.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearBudgets(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearBudgets.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearBudgets.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearSavingsGoals(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearSavingsGoals.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearSavingsGoals.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllocationBlocks(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllocationBlocks.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllocationBlocks.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllocationModels(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllocationModels.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllocationModels.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAccounts(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAccounts.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAccounts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearCategories(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearCategories.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearCategories.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearTags(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearTags.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearTags.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearPendingTransactions(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearPendingTransactions.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearPendingTransactions.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearMerchantLearnings(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearMerchantLearnings.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearMerchantLearnings.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearCaptureRules(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearCaptureRules.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearCaptureRules.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearReceivables(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearReceivables.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearReceivables.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearContacts(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearContacts.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearContacts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllAccounts(final Continuation<? super List<AccountEntity>> $completion) {
    final String _sql = "SELECT `accounts`.`id` AS `id`, `accounts`.`name` AS `name`, `accounts`.`type` AS `type`, `accounts`.`current_balance_cents` AS `current_balance_cents`, `accounts`.`color_hex` AS `color_hex`, `accounts`.`icon_name` AS `icon_name`, `accounts`.`is_archived` AS `is_archived`, `accounts`.`include_in_total` AS `include_in_total`, `accounts`.`is_exempt_from_gmf` AS `is_exempt_from_gmf`, `accounts`.`annual_yield_rate` AS `annual_yield_rate`, `accounts`.`is_static_saving` AS `is_static_saving`, `accounts`.`created_at` AS `created_at` FROM accounts ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AccountEntity>>() {
      @Override
      @NonNull
      public List<AccountEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfCurrentBalanceCents = 3;
          final int _cursorIndexOfColorHex = 4;
          final int _cursorIndexOfIconName = 5;
          final int _cursorIndexOfIsArchived = 6;
          final int _cursorIndexOfIncludeInTotal = 7;
          final int _cursorIndexOfIsExemptFromGmf = 8;
          final int _cursorIndexOfAnnualYieldRate = 9;
          final int _cursorIndexOfIsStaticSaving = 10;
          final int _cursorIndexOfCreatedAt = 11;
          final List<AccountEntity> _result = new ArrayList<AccountEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AccountEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final AccountType _tmpType;
            _tmpType = __AccountType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final long _tmpCurrentBalanceCents;
            _tmpCurrentBalanceCents = _cursor.getLong(_cursorIndexOfCurrentBalanceCents);
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            final boolean _tmpIsArchived;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsArchived);
            _tmpIsArchived = _tmp != 0;
            final boolean _tmpIncludeInTotal;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIncludeInTotal);
            _tmpIncludeInTotal = _tmp_1 != 0;
            final boolean _tmpIsExemptFromGmf;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsExemptFromGmf);
            _tmpIsExemptFromGmf = _tmp_2 != 0;
            final Double _tmpAnnualYieldRate;
            if (_cursor.isNull(_cursorIndexOfAnnualYieldRate)) {
              _tmpAnnualYieldRate = null;
            } else {
              _tmpAnnualYieldRate = _cursor.getDouble(_cursorIndexOfAnnualYieldRate);
            }
            final boolean _tmpIsStaticSaving;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsStaticSaving);
            _tmpIsStaticSaving = _tmp_3 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AccountEntity(_tmpId,_tmpName,_tmpType,_tmpCurrentBalanceCents,_tmpColorHex,_tmpIconName,_tmpIsArchived,_tmpIncludeInTotal,_tmpIsExemptFromGmf,_tmpAnnualYieldRate,_tmpIsStaticSaving,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllCategories(final Continuation<? super List<CategoryEntity>> $completion) {
    final String _sql = "SELECT `categories`.`id` AS `id`, `categories`.`name` AS `name`, `categories`.`icon_name` AS `icon_name`, `categories`.`color_hex` AS `color_hex`, `categories`.`default_block_type` AS `default_block_type`, `categories`.`is_system` AS `is_system` FROM categories ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CategoryEntity>>() {
      @Override
      @NonNull
      public List<CategoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfIconName = 2;
          final int _cursorIndexOfColorHex = 3;
          final int _cursorIndexOfDefaultBlockType = 4;
          final int _cursorIndexOfIsSystem = 5;
          final List<CategoryEntity> _result = new ArrayList<CategoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final BlockType _tmpDefaultBlockType;
            _tmpDefaultBlockType = __BlockType_stringToEnum(_cursor.getString(_cursorIndexOfDefaultBlockType));
            final boolean _tmpIsSystem;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSystem);
            _tmpIsSystem = _tmp != 0;
            _item = new CategoryEntity(_tmpId,_tmpName,_tmpIconName,_tmpColorHex,_tmpDefaultBlockType,_tmpIsSystem);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllTags(final Continuation<? super List<TagEntity>> $completion) {
    final String _sql = "SELECT `tags`.`id` AS `id`, `tags`.`name` AS `name` FROM tags ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TagEntity>>() {
      @Override
      @NonNull
      public List<TagEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final List<TagEntity> _result = new ArrayList<TagEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TagEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            _item = new TagEntity(_tmpId,_tmpName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllAllocationModels(
      final Continuation<? super List<AllocationModelEntity>> $completion) {
    final String _sql = "SELECT `allocation_models`.`id` AS `id`, `allocation_models`.`name` AS `name`, `allocation_models`.`is_active` AS `is_active`, `allocation_models`.`created_at` AS `created_at` FROM allocation_models ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AllocationModelEntity>>() {
      @Override
      @NonNull
      public List<AllocationModelEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfIsActive = 2;
          final int _cursorIndexOfCreatedAt = 3;
          final List<AllocationModelEntity> _result = new ArrayList<AllocationModelEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AllocationModelEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AllocationModelEntity(_tmpId,_tmpName,_tmpIsActive,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllAllocationBlocks(
      final Continuation<? super List<AllocationBlockEntity>> $completion) {
    final String _sql = "SELECT `allocation_blocks`.`id` AS `id`, `allocation_blocks`.`model_id` AS `model_id`, `allocation_blocks`.`name` AS `name`, `allocation_blocks`.`percentage` AS `percentage`, `allocation_blocks`.`type` AS `type`, `allocation_blocks`.`color_hex` AS `color_hex`, `allocation_blocks`.`order_index` AS `order_index`, `allocation_blocks`.`absorb_remainder` AS `absorb_remainder` FROM allocation_blocks ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AllocationBlockEntity>>() {
      @Override
      @NonNull
      public List<AllocationBlockEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfModelId = 1;
          final int _cursorIndexOfName = 2;
          final int _cursorIndexOfPercentage = 3;
          final int _cursorIndexOfType = 4;
          final int _cursorIndexOfColorHex = 5;
          final int _cursorIndexOfOrderIndex = 6;
          final int _cursorIndexOfAbsorbRemainder = 7;
          final List<AllocationBlockEntity> _result = new ArrayList<AllocationBlockEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AllocationBlockEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpModelId;
            _tmpModelId = _cursor.getLong(_cursorIndexOfModelId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpPercentage;
            _tmpPercentage = _cursor.getInt(_cursorIndexOfPercentage);
            final BlockType _tmpType;
            _tmpType = __BlockType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final boolean _tmpAbsorbRemainder;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAbsorbRemainder);
            _tmpAbsorbRemainder = _tmp != 0;
            _item = new AllocationBlockEntity(_tmpId,_tmpModelId,_tmpName,_tmpPercentage,_tmpType,_tmpColorHex,_tmpOrderIndex,_tmpAbsorbRemainder);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllTransactions(
      final Continuation<? super List<TransactionEntity>> $completion) {
    final String _sql = "SELECT `transactions`.`id` AS `id`, `transactions`.`account_id` AS `account_id`, `transactions`.`category_id` AS `category_id`, `transactions`.`block_id` AS `block_id`, `transactions`.`amount_cents` AS `amount_cents`, `transactions`.`type` AS `type`, `transactions`.`date_millis` AS `date_millis`, `transactions`.`note` AS `note`, `transactions`.`merchant` AS `merchant`, `transactions`.`linked_debt_id` AS `linked_debt_id`, `transactions`.`linked_recurring_item_id` AS `linked_recurring_item_id`, `transactions`.`linked_receivable_id` AS `linked_receivable_id`, `transactions`.`is_leisure` AS `is_leisure`, `transactions`.`created_at` AS `created_at` FROM transactions ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfAccountId = 1;
          final int _cursorIndexOfCategoryId = 2;
          final int _cursorIndexOfBlockId = 3;
          final int _cursorIndexOfAmountCents = 4;
          final int _cursorIndexOfType = 5;
          final int _cursorIndexOfDateMillis = 6;
          final int _cursorIndexOfNote = 7;
          final int _cursorIndexOfMerchant = 8;
          final int _cursorIndexOfLinkedDebtId = 9;
          final int _cursorIndexOfLinkedRecurringItemId = 10;
          final int _cursorIndexOfLinkedReceivableId = 11;
          final int _cursorIndexOfIsLeisure = 12;
          final int _cursorIndexOfCreatedAt = 13;
          final List<TransactionEntity> _result = new ArrayList<TransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final Long _tmpBlockId;
            if (_cursor.isNull(_cursorIndexOfBlockId)) {
              _tmpBlockId = null;
            } else {
              _tmpBlockId = _cursor.getLong(_cursorIndexOfBlockId);
            }
            final long _tmpAmountCents;
            _tmpAmountCents = _cursor.getLong(_cursorIndexOfAmountCents);
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final long _tmpDateMillis;
            _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpMerchant;
            if (_cursor.isNull(_cursorIndexOfMerchant)) {
              _tmpMerchant = null;
            } else {
              _tmpMerchant = _cursor.getString(_cursorIndexOfMerchant);
            }
            final Long _tmpLinkedDebtId;
            if (_cursor.isNull(_cursorIndexOfLinkedDebtId)) {
              _tmpLinkedDebtId = null;
            } else {
              _tmpLinkedDebtId = _cursor.getLong(_cursorIndexOfLinkedDebtId);
            }
            final Long _tmpLinkedRecurringItemId;
            if (_cursor.isNull(_cursorIndexOfLinkedRecurringItemId)) {
              _tmpLinkedRecurringItemId = null;
            } else {
              _tmpLinkedRecurringItemId = _cursor.getLong(_cursorIndexOfLinkedRecurringItemId);
            }
            final Long _tmpLinkedReceivableId;
            if (_cursor.isNull(_cursorIndexOfLinkedReceivableId)) {
              _tmpLinkedReceivableId = null;
            } else {
              _tmpLinkedReceivableId = _cursor.getLong(_cursorIndexOfLinkedReceivableId);
            }
            final boolean _tmpIsLeisure;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLeisure);
            _tmpIsLeisure = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TransactionEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpBlockId,_tmpAmountCents,_tmpType,_tmpDateMillis,_tmpNote,_tmpMerchant,_tmpLinkedDebtId,_tmpLinkedRecurringItemId,_tmpLinkedReceivableId,_tmpIsLeisure,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllTransactionTags(
      final Continuation<? super List<TransactionTagEntity>> $completion) {
    final String _sql = "SELECT `transaction_tags`.`transaction_id` AS `transaction_id`, `transaction_tags`.`tag_id` AS `tag_id` FROM transaction_tags";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TransactionTagEntity>>() {
      @Override
      @NonNull
      public List<TransactionTagEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTransactionId = 0;
          final int _cursorIndexOfTagId = 1;
          final List<TransactionTagEntity> _result = new ArrayList<TransactionTagEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionTagEntity _item;
            final long _tmpTransactionId;
            _tmpTransactionId = _cursor.getLong(_cursorIndexOfTransactionId);
            final long _tmpTagId;
            _tmpTagId = _cursor.getLong(_cursorIndexOfTagId);
            _item = new TransactionTagEntity(_tmpTransactionId,_tmpTagId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllIncomes(final Continuation<? super List<IncomeEntity>> $completion) {
    final String _sql = "SELECT `incomes`.`id` AS `id`, `incomes`.`account_id` AS `account_id`, `incomes`.`description` AS `description`, `incomes`.`amount_cents` AS `amount_cents`, `incomes`.`frequency` AS `frequency`, `incomes`.`expected_date_millis` AS `expected_date_millis`, `incomes`.`received_date_millis` AS `received_date_millis`, `incomes`.`is_received` AS `is_received`, `incomes`.`created_at` AS `created_at` FROM incomes ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IncomeEntity>>() {
      @Override
      @NonNull
      public List<IncomeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfAccountId = 1;
          final int _cursorIndexOfDescription = 2;
          final int _cursorIndexOfAmountCents = 3;
          final int _cursorIndexOfFrequency = 4;
          final int _cursorIndexOfExpectedDateMillis = 5;
          final int _cursorIndexOfReceivedDateMillis = 6;
          final int _cursorIndexOfIsReceived = 7;
          final int _cursorIndexOfCreatedAt = 8;
          final List<IncomeEntity> _result = new ArrayList<IncomeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final long _tmpAmountCents;
            _tmpAmountCents = _cursor.getLong(_cursorIndexOfAmountCents);
            final IncomeFrequency _tmpFrequency;
            _tmpFrequency = __IncomeFrequency_stringToEnum(_cursor.getString(_cursorIndexOfFrequency));
            final long _tmpExpectedDateMillis;
            _tmpExpectedDateMillis = _cursor.getLong(_cursorIndexOfExpectedDateMillis);
            final Long _tmpReceivedDateMillis;
            if (_cursor.isNull(_cursorIndexOfReceivedDateMillis)) {
              _tmpReceivedDateMillis = null;
            } else {
              _tmpReceivedDateMillis = _cursor.getLong(_cursorIndexOfReceivedDateMillis);
            }
            final boolean _tmpIsReceived;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsReceived);
            _tmpIsReceived = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new IncomeEntity(_tmpId,_tmpAccountId,_tmpDescription,_tmpAmountCents,_tmpFrequency,_tmpExpectedDateMillis,_tmpReceivedDateMillis,_tmpIsReceived,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllIncomeAllocations(
      final Continuation<? super List<IncomeAllocationEntity>> $completion) {
    final String _sql = "SELECT `income_allocations`.`id` AS `id`, `income_allocations`.`income_id` AS `income_id`, `income_allocations`.`block_id` AS `block_id`, `income_allocations`.`assigned_cents` AS `assigned_cents` FROM income_allocations ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IncomeAllocationEntity>>() {
      @Override
      @NonNull
      public List<IncomeAllocationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfIncomeId = 1;
          final int _cursorIndexOfBlockId = 2;
          final int _cursorIndexOfAssignedCents = 3;
          final List<IncomeAllocationEntity> _result = new ArrayList<IncomeAllocationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncomeAllocationEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpIncomeId;
            _tmpIncomeId = _cursor.getLong(_cursorIndexOfIncomeId);
            final long _tmpBlockId;
            _tmpBlockId = _cursor.getLong(_cursorIndexOfBlockId);
            final long _tmpAssignedCents;
            _tmpAssignedCents = _cursor.getLong(_cursorIndexOfAssignedCents);
            _item = new IncomeAllocationEntity(_tmpId,_tmpIncomeId,_tmpBlockId,_tmpAssignedCents);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllCreditCards(final Continuation<? super List<CreditCardEntity>> $completion) {
    final String _sql = "SELECT `credit_cards`.`id` AS `id`, `credit_cards`.`account_id` AS `account_id`, `credit_cards`.`credit_limit_cents` AS `credit_limit_cents`, `credit_cards`.`cut_off_day` AS `cut_off_day`, `credit_cards`.`payment_due_day` AS `payment_due_day`, `credit_cards`.`interest_rate_ea` AS `interest_rate_ea`, `credit_cards`.`handling_fee_cents` AS `handling_fee_cents`, `credit_cards`.`last_four_digits` AS `last_four_digits`, `credit_cards`.`revolving_rate` AS `revolving_rate`, `credit_cards`.`monthly_insurance_cents` AS `monthly_insurance_cents`, `credit_cards`.`installment_type` AS `installment_type` FROM credit_cards ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CreditCardEntity>>() {
      @Override
      @NonNull
      public List<CreditCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfAccountId = 1;
          final int _cursorIndexOfCreditLimitCents = 2;
          final int _cursorIndexOfCutOffDay = 3;
          final int _cursorIndexOfPaymentDueDay = 4;
          final int _cursorIndexOfInterestRateEa = 5;
          final int _cursorIndexOfHandlingFeeCents = 6;
          final int _cursorIndexOfLastFourDigits = 7;
          final int _cursorIndexOfRevolvingRate = 8;
          final int _cursorIndexOfMonthlyInsuranceCents = 9;
          final int _cursorIndexOfInstallmentType = 10;
          final List<CreditCardEntity> _result = new ArrayList<CreditCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CreditCardEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final long _tmpCreditLimitCents;
            _tmpCreditLimitCents = _cursor.getLong(_cursorIndexOfCreditLimitCents);
            final int _tmpCutOffDay;
            _tmpCutOffDay = _cursor.getInt(_cursorIndexOfCutOffDay);
            final int _tmpPaymentDueDay;
            _tmpPaymentDueDay = _cursor.getInt(_cursorIndexOfPaymentDueDay);
            final double _tmpInterestRateEa;
            _tmpInterestRateEa = _cursor.getDouble(_cursorIndexOfInterestRateEa);
            final long _tmpHandlingFeeCents;
            _tmpHandlingFeeCents = _cursor.getLong(_cursorIndexOfHandlingFeeCents);
            final String _tmpLastFourDigits;
            if (_cursor.isNull(_cursorIndexOfLastFourDigits)) {
              _tmpLastFourDigits = null;
            } else {
              _tmpLastFourDigits = _cursor.getString(_cursorIndexOfLastFourDigits);
            }
            final double _tmpRevolvingRate;
            _tmpRevolvingRate = _cursor.getDouble(_cursorIndexOfRevolvingRate);
            final long _tmpMonthlyInsuranceCents;
            _tmpMonthlyInsuranceCents = _cursor.getLong(_cursorIndexOfMonthlyInsuranceCents);
            final int _tmpInstallmentType;
            _tmpInstallmentType = _cursor.getInt(_cursorIndexOfInstallmentType);
            _item = new CreditCardEntity(_tmpId,_tmpAccountId,_tmpCreditLimitCents,_tmpCutOffDay,_tmpPaymentDueDay,_tmpInterestRateEa,_tmpHandlingFeeCents,_tmpLastFourDigits,_tmpRevolvingRate,_tmpMonthlyInsuranceCents,_tmpInstallmentType);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllCardPurchases(
      final Continuation<? super List<CardPurchaseEntity>> $completion) {
    final String _sql = "SELECT `card_purchases`.`id` AS `id`, `card_purchases`.`card_id` AS `card_id`, `card_purchases`.`merchant` AS `merchant`, `card_purchases`.`purchase_date_millis` AS `purchase_date_millis`, `card_purchases`.`total_amount_cents` AS `total_amount_cents`, `card_purchases`.`installments_total` AS `installments_total`, `card_purchases`.`installments_paid` AS `installments_paid`, `card_purchases`.`interest_rate_nm` AS `interest_rate_nm`, `card_purchases`.`is_fully_paid` AS `is_fully_paid` FROM card_purchases ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CardPurchaseEntity>>() {
      @Override
      @NonNull
      public List<CardPurchaseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfCardId = 1;
          final int _cursorIndexOfMerchant = 2;
          final int _cursorIndexOfPurchaseDateMillis = 3;
          final int _cursorIndexOfTotalAmountCents = 4;
          final int _cursorIndexOfInstallmentsTotal = 5;
          final int _cursorIndexOfInstallmentsPaid = 6;
          final int _cursorIndexOfInterestRateNm = 7;
          final int _cursorIndexOfIsFullyPaid = 8;
          final List<CardPurchaseEntity> _result = new ArrayList<CardPurchaseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CardPurchaseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final String _tmpMerchant;
            _tmpMerchant = _cursor.getString(_cursorIndexOfMerchant);
            final long _tmpPurchaseDateMillis;
            _tmpPurchaseDateMillis = _cursor.getLong(_cursorIndexOfPurchaseDateMillis);
            final long _tmpTotalAmountCents;
            _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
            final int _tmpInstallmentsTotal;
            _tmpInstallmentsTotal = _cursor.getInt(_cursorIndexOfInstallmentsTotal);
            final int _tmpInstallmentsPaid;
            _tmpInstallmentsPaid = _cursor.getInt(_cursorIndexOfInstallmentsPaid);
            final double _tmpInterestRateNm;
            _tmpInterestRateNm = _cursor.getDouble(_cursorIndexOfInterestRateNm);
            final boolean _tmpIsFullyPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFullyPaid);
            _tmpIsFullyPaid = _tmp != 0;
            _item = new CardPurchaseEntity(_tmpId,_tmpCardId,_tmpMerchant,_tmpPurchaseDateMillis,_tmpTotalAmountCents,_tmpInstallmentsTotal,_tmpInstallmentsPaid,_tmpInterestRateNm,_tmpIsFullyPaid);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllDebts(final Continuation<? super List<DebtEntity>> $completion) {
    final String _sql = "SELECT `debts`.`id` AS `id`, `debts`.`name` AS `name`, `debts`.`principal_amount_cents` AS `principal_amount_cents`, `debts`.`current_balance_cents` AS `current_balance_cents`, `debts`.`interest_rate_ea` AS `interest_rate_ea`, `debts`.`monthly_installment_cents` AS `monthly_installment_cents`, `debts`.`due_day_of_month` AS `due_day_of_month`, `debts`.`start_date_millis` AS `start_date_millis`, `debts`.`next_due_date_millis` AS `next_due_date_millis`, `debts`.`monthly_insurance_cents` AS `monthly_insurance_cents`, `debts`.`is_settled` AS `is_settled` FROM debts ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DebtEntity>>() {
      @Override
      @NonNull
      public List<DebtEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfPrincipalAmountCents = 2;
          final int _cursorIndexOfCurrentBalanceCents = 3;
          final int _cursorIndexOfInterestRateEa = 4;
          final int _cursorIndexOfMonthlyInstallmentCents = 5;
          final int _cursorIndexOfDueDayOfMonth = 6;
          final int _cursorIndexOfStartDateMillis = 7;
          final int _cursorIndexOfNextDueDateMillis = 8;
          final int _cursorIndexOfMonthlyInsuranceCents = 9;
          final int _cursorIndexOfIsSettled = 10;
          final List<DebtEntity> _result = new ArrayList<DebtEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpPrincipalAmountCents;
            _tmpPrincipalAmountCents = _cursor.getLong(_cursorIndexOfPrincipalAmountCents);
            final long _tmpCurrentBalanceCents;
            _tmpCurrentBalanceCents = _cursor.getLong(_cursorIndexOfCurrentBalanceCents);
            final double _tmpInterestRateEa;
            _tmpInterestRateEa = _cursor.getDouble(_cursorIndexOfInterestRateEa);
            final long _tmpMonthlyInstallmentCents;
            _tmpMonthlyInstallmentCents = _cursor.getLong(_cursorIndexOfMonthlyInstallmentCents);
            final int _tmpDueDayOfMonth;
            _tmpDueDayOfMonth = _cursor.getInt(_cursorIndexOfDueDayOfMonth);
            final long _tmpStartDateMillis;
            _tmpStartDateMillis = _cursor.getLong(_cursorIndexOfStartDateMillis);
            final Long _tmpNextDueDateMillis;
            if (_cursor.isNull(_cursorIndexOfNextDueDateMillis)) {
              _tmpNextDueDateMillis = null;
            } else {
              _tmpNextDueDateMillis = _cursor.getLong(_cursorIndexOfNextDueDateMillis);
            }
            final long _tmpMonthlyInsuranceCents;
            _tmpMonthlyInsuranceCents = _cursor.getLong(_cursorIndexOfMonthlyInsuranceCents);
            final boolean _tmpIsSettled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp != 0;
            _item = new DebtEntity(_tmpId,_tmpName,_tmpPrincipalAmountCents,_tmpCurrentBalanceCents,_tmpInterestRateEa,_tmpMonthlyInstallmentCents,_tmpDueDayOfMonth,_tmpStartDateMillis,_tmpNextDueDateMillis,_tmpMonthlyInsuranceCents,_tmpIsSettled);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllDebtPayments(
      final Continuation<? super List<DebtPaymentEntity>> $completion) {
    final String _sql = "SELECT `debt_payments`.`id` AS `id`, `debt_payments`.`debt_id` AS `debt_id`, `debt_payments`.`payment_date_millis` AS `payment_date_millis`, `debt_payments`.`total_paid_cents` AS `total_paid_cents`, `debt_payments`.`principal_paid_cents` AS `principal_paid_cents`, `debt_payments`.`interest_paid_cents` AS `interest_paid_cents`, `debt_payments`.`is_extra_payment` AS `is_extra_payment` FROM debt_payments ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DebtPaymentEntity>>() {
      @Override
      @NonNull
      public List<DebtPaymentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfDebtId = 1;
          final int _cursorIndexOfPaymentDateMillis = 2;
          final int _cursorIndexOfTotalPaidCents = 3;
          final int _cursorIndexOfPrincipalPaidCents = 4;
          final int _cursorIndexOfInterestPaidCents = 5;
          final int _cursorIndexOfIsExtraPayment = 6;
          final List<DebtPaymentEntity> _result = new ArrayList<DebtPaymentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtPaymentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDebtId;
            _tmpDebtId = _cursor.getLong(_cursorIndexOfDebtId);
            final long _tmpPaymentDateMillis;
            _tmpPaymentDateMillis = _cursor.getLong(_cursorIndexOfPaymentDateMillis);
            final long _tmpTotalPaidCents;
            _tmpTotalPaidCents = _cursor.getLong(_cursorIndexOfTotalPaidCents);
            final long _tmpPrincipalPaidCents;
            _tmpPrincipalPaidCents = _cursor.getLong(_cursorIndexOfPrincipalPaidCents);
            final long _tmpInterestPaidCents;
            _tmpInterestPaidCents = _cursor.getLong(_cursorIndexOfInterestPaidCents);
            final boolean _tmpIsExtraPayment;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsExtraPayment);
            _tmpIsExtraPayment = _tmp != 0;
            _item = new DebtPaymentEntity(_tmpId,_tmpDebtId,_tmpPaymentDateMillis,_tmpTotalPaidCents,_tmpPrincipalPaidCents,_tmpInterestPaidCents,_tmpIsExtraPayment);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllBudgets(final Continuation<? super List<BudgetEntity>> $completion) {
    final String _sql = "SELECT `budgets`.`id` AS `id`, `budgets`.`block_id` AS `block_id`, `budgets`.`category_id` AS `category_id`, `budgets`.`month` AS `month`, `budgets`.`year` AS `year`, `budgets`.`limit_amount_cents` AS `limit_amount_cents` FROM budgets ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfBlockId = 1;
          final int _cursorIndexOfCategoryId = 2;
          final int _cursorIndexOfMonth = 3;
          final int _cursorIndexOfYear = 4;
          final int _cursorIndexOfLimitAmountCents = 5;
          final List<BudgetEntity> _result = new ArrayList<BudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BudgetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBlockId;
            _tmpBlockId = _cursor.getLong(_cursorIndexOfBlockId);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final int _tmpMonth;
            _tmpMonth = _cursor.getInt(_cursorIndexOfMonth);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final long _tmpLimitAmountCents;
            _tmpLimitAmountCents = _cursor.getLong(_cursorIndexOfLimitAmountCents);
            _item = new BudgetEntity(_tmpId,_tmpBlockId,_tmpCategoryId,_tmpMonth,_tmpYear,_tmpLimitAmountCents);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllSavingsGoals(
      final Continuation<? super List<SavingsGoalEntity>> $completion) {
    final String _sql = "SELECT `savings_goals`.`id` AS `id`, `savings_goals`.`name` AS `name`, `savings_goals`.`target_amount_cents` AS `target_amount_cents`, `savings_goals`.`current_saved_cents` AS `current_saved_cents`, `savings_goals`.`target_date_millis` AS `target_date_millis`, `savings_goals`.`is_emergency_fund` AS `is_emergency_fund`, `savings_goals`.`is_completed` AS `is_completed` FROM savings_goals ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SavingsGoalEntity>>() {
      @Override
      @NonNull
      public List<SavingsGoalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfTargetAmountCents = 2;
          final int _cursorIndexOfCurrentSavedCents = 3;
          final int _cursorIndexOfTargetDateMillis = 4;
          final int _cursorIndexOfIsEmergencyFund = 5;
          final int _cursorIndexOfIsCompleted = 6;
          final List<SavingsGoalEntity> _result = new ArrayList<SavingsGoalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavingsGoalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpTargetAmountCents;
            _tmpTargetAmountCents = _cursor.getLong(_cursorIndexOfTargetAmountCents);
            final long _tmpCurrentSavedCents;
            _tmpCurrentSavedCents = _cursor.getLong(_cursorIndexOfCurrentSavedCents);
            final Long _tmpTargetDateMillis;
            if (_cursor.isNull(_cursorIndexOfTargetDateMillis)) {
              _tmpTargetDateMillis = null;
            } else {
              _tmpTargetDateMillis = _cursor.getLong(_cursorIndexOfTargetDateMillis);
            }
            final boolean _tmpIsEmergencyFund;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEmergencyFund);
            _tmpIsEmergencyFund = _tmp != 0;
            final boolean _tmpIsCompleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_1 != 0;
            _item = new SavingsGoalEntity(_tmpId,_tmpName,_tmpTargetAmountCents,_tmpCurrentSavedCents,_tmpTargetDateMillis,_tmpIsEmergencyFund,_tmpIsCompleted);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllRecurringItems(
      final Continuation<? super List<RecurringItemEntity>> $completion) {
    final String _sql = "SELECT `recurring_items`.`id` AS `id`, `recurring_items`.`account_id` AS `account_id`, `recurring_items`.`category_id` AS `category_id`, `recurring_items`.`block_id` AS `block_id`, `recurring_items`.`title` AS `title`, `recurring_items`.`amount_cents` AS `amount_cents`, `recurring_items`.`frequency` AS `frequency`, `recurring_items`.`next_due_date_millis` AS `next_due_date_millis`, `recurring_items`.`notify_days_before` AS `notify_days_before`, `recurring_items`.`is_active` AS `is_active` FROM recurring_items ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RecurringItemEntity>>() {
      @Override
      @NonNull
      public List<RecurringItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfAccountId = 1;
          final int _cursorIndexOfCategoryId = 2;
          final int _cursorIndexOfBlockId = 3;
          final int _cursorIndexOfTitle = 4;
          final int _cursorIndexOfAmountCents = 5;
          final int _cursorIndexOfFrequency = 6;
          final int _cursorIndexOfNextDueDateMillis = 7;
          final int _cursorIndexOfNotifyDaysBefore = 8;
          final int _cursorIndexOfIsActive = 9;
          final List<RecurringItemEntity> _result = new ArrayList<RecurringItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecurringItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpBlockId;
            _tmpBlockId = _cursor.getLong(_cursorIndexOfBlockId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final long _tmpAmountCents;
            _tmpAmountCents = _cursor.getLong(_cursorIndexOfAmountCents);
            final IncomeFrequency _tmpFrequency;
            _tmpFrequency = __IncomeFrequency_stringToEnum(_cursor.getString(_cursorIndexOfFrequency));
            final long _tmpNextDueDateMillis;
            _tmpNextDueDateMillis = _cursor.getLong(_cursorIndexOfNextDueDateMillis);
            final int _tmpNotifyDaysBefore;
            _tmpNotifyDaysBefore = _cursor.getInt(_cursorIndexOfNotifyDaysBefore);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new RecurringItemEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpBlockId,_tmpTitle,_tmpAmountCents,_tmpFrequency,_tmpNextDueDateMillis,_tmpNotifyDaysBefore,_tmpIsActive);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllCaptureRules(
      final Continuation<? super List<CaptureRuleEntity>> $completion) {
    final String _sql = "SELECT `capture_rules`.`id` AS `id`, `capture_rules`.`sender_filter` AS `sender_filter`, `capture_rules`.`pattern_type` AS `pattern_type`, `capture_rules`.`regex_pattern` AS `regex_pattern`, `capture_rules`.`extracted_type` AS `extracted_type`, `capture_rules`.`default_account_id` AS `default_account_id`, `capture_rules`.`confidence_score` AS `confidence_score`, `capture_rules`.`is_active` AS `is_active` FROM capture_rules ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CaptureRuleEntity>>() {
      @Override
      @NonNull
      public List<CaptureRuleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfSenderFilter = 1;
          final int _cursorIndexOfPatternType = 2;
          final int _cursorIndexOfRegexPattern = 3;
          final int _cursorIndexOfExtractedType = 4;
          final int _cursorIndexOfDefaultAccountId = 5;
          final int _cursorIndexOfConfidenceScore = 6;
          final int _cursorIndexOfIsActive = 7;
          final List<CaptureRuleEntity> _result = new ArrayList<CaptureRuleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CaptureRuleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSenderFilter;
            _tmpSenderFilter = _cursor.getString(_cursorIndexOfSenderFilter);
            final CapturePatternType _tmpPatternType;
            _tmpPatternType = __CapturePatternType_stringToEnum(_cursor.getString(_cursorIndexOfPatternType));
            final String _tmpRegexPattern;
            _tmpRegexPattern = _cursor.getString(_cursorIndexOfRegexPattern);
            final TransactionType _tmpExtractedType;
            _tmpExtractedType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfExtractedType));
            final Long _tmpDefaultAccountId;
            if (_cursor.isNull(_cursorIndexOfDefaultAccountId)) {
              _tmpDefaultAccountId = null;
            } else {
              _tmpDefaultAccountId = _cursor.getLong(_cursorIndexOfDefaultAccountId);
            }
            final float _tmpConfidenceScore;
            _tmpConfidenceScore = _cursor.getFloat(_cursorIndexOfConfidenceScore);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new CaptureRuleEntity(_tmpId,_tmpSenderFilter,_tmpPatternType,_tmpRegexPattern,_tmpExtractedType,_tmpDefaultAccountId,_tmpConfidenceScore,_tmpIsActive);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllPendingTransactions(
      final Continuation<? super List<PendingTransactionEntity>> $completion) {
    final String _sql = "SELECT `pending_transactions`.`id` AS `id`, `pending_transactions`.`source` AS `source`, `pending_transactions`.`source_sender` AS `source_sender`, `pending_transactions`.`raw_text` AS `raw_text`, `pending_transactions`.`deduplication_hash` AS `deduplication_hash`, `pending_transactions`.`suggested_amount_cents` AS `suggested_amount_cents`, `pending_transactions`.`suggested_merchant` AS `suggested_merchant`, `pending_transactions`.`suggested_account_id` AS `suggested_account_id`, `pending_transactions`.`suggested_category_id` AS `suggested_category_id`, `pending_transactions`.`suggested_block_id` AS `suggested_block_id`, `pending_transactions`.`suggested_type` AS `suggested_type`, `pending_transactions`.`suggested_card_id` AS `suggested_card_id`, `pending_transactions`.`suggested_debt_id` AS `suggested_debt_id`, `pending_transactions`.`detected_date_millis` AS `detected_date_millis`, `pending_transactions`.`status` AS `status` FROM pending_transactions ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PendingTransactionEntity>>() {
      @Override
      @NonNull
      public List<PendingTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfSource = 1;
          final int _cursorIndexOfSourceSender = 2;
          final int _cursorIndexOfRawText = 3;
          final int _cursorIndexOfDeduplicationHash = 4;
          final int _cursorIndexOfSuggestedAmountCents = 5;
          final int _cursorIndexOfSuggestedMerchant = 6;
          final int _cursorIndexOfSuggestedAccountId = 7;
          final int _cursorIndexOfSuggestedCategoryId = 8;
          final int _cursorIndexOfSuggestedBlockId = 9;
          final int _cursorIndexOfSuggestedType = 10;
          final int _cursorIndexOfSuggestedCardId = 11;
          final int _cursorIndexOfSuggestedDebtId = 12;
          final int _cursorIndexOfDetectedDateMillis = 13;
          final int _cursorIndexOfStatus = 14;
          final List<PendingTransactionEntity> _result = new ArrayList<PendingTransactionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PendingTransactionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final PendingSource _tmpSource;
            _tmpSource = __PendingSource_stringToEnum(_cursor.getString(_cursorIndexOfSource));
            final String _tmpSourceSender;
            _tmpSourceSender = _cursor.getString(_cursorIndexOfSourceSender);
            final String _tmpRawText;
            if (_cursor.isNull(_cursorIndexOfRawText)) {
              _tmpRawText = null;
            } else {
              _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            }
            final String _tmpDeduplicationHash;
            _tmpDeduplicationHash = _cursor.getString(_cursorIndexOfDeduplicationHash);
            final long _tmpSuggestedAmountCents;
            _tmpSuggestedAmountCents = _cursor.getLong(_cursorIndexOfSuggestedAmountCents);
            final String _tmpSuggestedMerchant;
            if (_cursor.isNull(_cursorIndexOfSuggestedMerchant)) {
              _tmpSuggestedMerchant = null;
            } else {
              _tmpSuggestedMerchant = _cursor.getString(_cursorIndexOfSuggestedMerchant);
            }
            final Long _tmpSuggestedAccountId;
            if (_cursor.isNull(_cursorIndexOfSuggestedAccountId)) {
              _tmpSuggestedAccountId = null;
            } else {
              _tmpSuggestedAccountId = _cursor.getLong(_cursorIndexOfSuggestedAccountId);
            }
            final Long _tmpSuggestedCategoryId;
            if (_cursor.isNull(_cursorIndexOfSuggestedCategoryId)) {
              _tmpSuggestedCategoryId = null;
            } else {
              _tmpSuggestedCategoryId = _cursor.getLong(_cursorIndexOfSuggestedCategoryId);
            }
            final Long _tmpSuggestedBlockId;
            if (_cursor.isNull(_cursorIndexOfSuggestedBlockId)) {
              _tmpSuggestedBlockId = null;
            } else {
              _tmpSuggestedBlockId = _cursor.getLong(_cursorIndexOfSuggestedBlockId);
            }
            final TransactionType _tmpSuggestedType;
            _tmpSuggestedType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfSuggestedType));
            final Long _tmpSuggestedCardId;
            if (_cursor.isNull(_cursorIndexOfSuggestedCardId)) {
              _tmpSuggestedCardId = null;
            } else {
              _tmpSuggestedCardId = _cursor.getLong(_cursorIndexOfSuggestedCardId);
            }
            final Long _tmpSuggestedDebtId;
            if (_cursor.isNull(_cursorIndexOfSuggestedDebtId)) {
              _tmpSuggestedDebtId = null;
            } else {
              _tmpSuggestedDebtId = _cursor.getLong(_cursorIndexOfSuggestedDebtId);
            }
            final long _tmpDetectedDateMillis;
            _tmpDetectedDateMillis = _cursor.getLong(_cursorIndexOfDetectedDateMillis);
            final PendingStatus _tmpStatus;
            _tmpStatus = __PendingStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            _item = new PendingTransactionEntity(_tmpId,_tmpSource,_tmpSourceSender,_tmpRawText,_tmpDeduplicationHash,_tmpSuggestedAmountCents,_tmpSuggestedMerchant,_tmpSuggestedAccountId,_tmpSuggestedCategoryId,_tmpSuggestedBlockId,_tmpSuggestedType,_tmpSuggestedCardId,_tmpSuggestedDebtId,_tmpDetectedDateMillis,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllMerchantLearnings(
      final Continuation<? super List<MerchantLearningEntity>> $completion) {
    final String _sql = "SELECT `merchant_learnings`.`id` AS `id`, `merchant_learnings`.`normalized_merchant` AS `normalized_merchant`, `merchant_learnings`.`category_id` AS `category_id`, `merchant_learnings`.`block_id` AS `block_id`, `merchant_learnings`.`hit_count` AS `hit_count` FROM merchant_learnings ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MerchantLearningEntity>>() {
      @Override
      @NonNull
      public List<MerchantLearningEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfNormalizedMerchant = 1;
          final int _cursorIndexOfCategoryId = 2;
          final int _cursorIndexOfBlockId = 3;
          final int _cursorIndexOfHitCount = 4;
          final List<MerchantLearningEntity> _result = new ArrayList<MerchantLearningEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MerchantLearningEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpNormalizedMerchant;
            _tmpNormalizedMerchant = _cursor.getString(_cursorIndexOfNormalizedMerchant);
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpBlockId;
            _tmpBlockId = _cursor.getLong(_cursorIndexOfBlockId);
            final int _tmpHitCount;
            _tmpHitCount = _cursor.getInt(_cursorIndexOfHitCount);
            _item = new MerchantLearningEntity(_tmpId,_tmpNormalizedMerchant,_tmpCategoryId,_tmpBlockId,_tmpHitCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllContacts(final Continuation<? super List<ContactEntity>> $completion) {
    final String _sql = "SELECT `contacts`.`id` AS `id`, `contacts`.`name` AS `name`, `contacts`.`kind` AS `kind`, `contacts`.`created_at` AS `created_at` FROM contacts ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ContactEntity>>() {
      @Override
      @NonNull
      public List<ContactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfKind = 2;
          final int _cursorIndexOfCreatedAt = 3;
          final List<ContactEntity> _result = new ArrayList<ContactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final ContactKind _tmpKind;
            _tmpKind = __ContactKind_stringToEnum(_cursor.getString(_cursorIndexOfKind));
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ContactEntity(_tmpId,_tmpName,_tmpKind,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllReceivables(final Continuation<? super List<ReceivableEntity>> $completion) {
    final String _sql = "SELECT `receivables`.`id` AS `id`, `receivables`.`contact_id` AS `contact_id`, `receivables`.`source_transaction_id` AS `source_transaction_id`, `receivables`.`note` AS `note`, `receivables`.`owed_cents` AS `owed_cents`, `receivables`.`is_settled` AS `is_settled`, `receivables`.`settled_at_millis` AS `settled_at_millis`, `receivables`.`settled_account_id` AS `settled_account_id`, `receivables`.`settled_transaction_id` AS `settled_transaction_id`, `receivables`.`created_at` AS `created_at` FROM receivables ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReceivableEntity>>() {
      @Override
      @NonNull
      public List<ReceivableEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfContactId = 1;
          final int _cursorIndexOfSourceTransactionId = 2;
          final int _cursorIndexOfNote = 3;
          final int _cursorIndexOfOwedCents = 4;
          final int _cursorIndexOfIsSettled = 5;
          final int _cursorIndexOfSettledAtMillis = 6;
          final int _cursorIndexOfSettledAccountId = 7;
          final int _cursorIndexOfSettledTransactionId = 8;
          final int _cursorIndexOfCreatedAt = 9;
          final List<ReceivableEntity> _result = new ArrayList<ReceivableEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReceivableEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpContactId;
            _tmpContactId = _cursor.getLong(_cursorIndexOfContactId);
            final Long _tmpSourceTransactionId;
            if (_cursor.isNull(_cursorIndexOfSourceTransactionId)) {
              _tmpSourceTransactionId = null;
            } else {
              _tmpSourceTransactionId = _cursor.getLong(_cursorIndexOfSourceTransactionId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpOwedCents;
            _tmpOwedCents = _cursor.getLong(_cursorIndexOfOwedCents);
            final boolean _tmpIsSettled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp != 0;
            final Long _tmpSettledAtMillis;
            if (_cursor.isNull(_cursorIndexOfSettledAtMillis)) {
              _tmpSettledAtMillis = null;
            } else {
              _tmpSettledAtMillis = _cursor.getLong(_cursorIndexOfSettledAtMillis);
            }
            final Long _tmpSettledAccountId;
            if (_cursor.isNull(_cursorIndexOfSettledAccountId)) {
              _tmpSettledAccountId = null;
            } else {
              _tmpSettledAccountId = _cursor.getLong(_cursorIndexOfSettledAccountId);
            }
            final Long _tmpSettledTransactionId;
            if (_cursor.isNull(_cursorIndexOfSettledTransactionId)) {
              _tmpSettledTransactionId = null;
            } else {
              _tmpSettledTransactionId = _cursor.getLong(_cursorIndexOfSettledTransactionId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ReceivableEntity(_tmpId,_tmpContactId,_tmpSourceTransactionId,_tmpNote,_tmpOwedCents,_tmpIsSettled,_tmpSettledAtMillis,_tmpSettledAccountId,_tmpSettledTransactionId,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __AccountType_enumToString(@NonNull final AccountType _value) {
    switch (_value) {
      case CASH: return "CASH";
      case SAVINGS: return "SAVINGS";
      case CHECKING: return "CHECKING";
      case DIGITAL_WALLET: return "DIGITAL_WALLET";
      case INVESTMENT: return "INVESTMENT";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private String __BlockType_enumToString(@NonNull final BlockType _value) {
    switch (_value) {
      case NEEDS: return "NEEDS";
      case DEBTS: return "DEBTS";
      case SAVINGS: return "SAVINGS";
      case WANTS: return "WANTS";
      case CUSTOM: return "CUSTOM";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private String __TransactionType_enumToString(@NonNull final TransactionType _value) {
    switch (_value) {
      case EXPENSE: return "EXPENSE";
      case INCOME: return "INCOME";
      case TRANSFER_OUT: return "TRANSFER_OUT";
      case TRANSFER_IN: return "TRANSFER_IN";
      case ADJUSTMENT: return "ADJUSTMENT";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private String __IncomeFrequency_enumToString(@NonNull final IncomeFrequency _value) {
    switch (_value) {
      case ONE_TIME: return "ONE_TIME";
      case WEEKLY: return "WEEKLY";
      case BIWEEKLY: return "BIWEEKLY";
      case MONTHLY: return "MONTHLY";
      case CUSTOM: return "CUSTOM";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private String __CapturePatternType_enumToString(@NonNull final CapturePatternType _value) {
    switch (_value) {
      case NOTIFICATION_APP: return "NOTIFICATION_APP";
      case SMS: return "SMS";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private String __PendingSource_enumToString(@NonNull final PendingSource _value) {
    switch (_value) {
      case NOTIFICATION: return "NOTIFICATION";
      case SMS: return "SMS";
      case EMAIL_NOTIFICATION: return "EMAIL_NOTIFICATION";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private String __PendingStatus_enumToString(@NonNull final PendingStatus _value) {
    switch (_value) {
      case PENDING: return "PENDING";
      case CONFIRMED: return "CONFIRMED";
      case DISCARDED: return "DISCARDED";
      case AUTO_CONFIRMED: return "AUTO_CONFIRMED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private String __ContactKind_enumToString(@NonNull final ContactKind _value) {
    switch (_value) {
      case PERSON: return "PERSON";
      case PLACE: return "PLACE";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private AccountType __AccountType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "CASH": return AccountType.CASH;
      case "SAVINGS": return AccountType.SAVINGS;
      case "CHECKING": return AccountType.CHECKING;
      case "DIGITAL_WALLET": return AccountType.DIGITAL_WALLET;
      case "INVESTMENT": return AccountType.INVESTMENT;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }

  private BlockType __BlockType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "NEEDS": return BlockType.NEEDS;
      case "DEBTS": return BlockType.DEBTS;
      case "SAVINGS": return BlockType.SAVINGS;
      case "WANTS": return BlockType.WANTS;
      case "CUSTOM": return BlockType.CUSTOM;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }

  private TransactionType __TransactionType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "EXPENSE": return TransactionType.EXPENSE;
      case "INCOME": return TransactionType.INCOME;
      case "TRANSFER_OUT": return TransactionType.TRANSFER_OUT;
      case "TRANSFER_IN": return TransactionType.TRANSFER_IN;
      case "ADJUSTMENT": return TransactionType.ADJUSTMENT;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }

  private IncomeFrequency __IncomeFrequency_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "ONE_TIME": return IncomeFrequency.ONE_TIME;
      case "WEEKLY": return IncomeFrequency.WEEKLY;
      case "BIWEEKLY": return IncomeFrequency.BIWEEKLY;
      case "MONTHLY": return IncomeFrequency.MONTHLY;
      case "CUSTOM": return IncomeFrequency.CUSTOM;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }

  private CapturePatternType __CapturePatternType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "NOTIFICATION_APP": return CapturePatternType.NOTIFICATION_APP;
      case "SMS": return CapturePatternType.SMS;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }

  private PendingSource __PendingSource_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "NOTIFICATION": return PendingSource.NOTIFICATION;
      case "SMS": return PendingSource.SMS;
      case "EMAIL_NOTIFICATION": return PendingSource.EMAIL_NOTIFICATION;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }

  private PendingStatus __PendingStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "PENDING": return PendingStatus.PENDING;
      case "CONFIRMED": return PendingStatus.CONFIRMED;
      case "DISCARDED": return PendingStatus.DISCARDED;
      case "AUTO_CONFIRMED": return PendingStatus.AUTO_CONFIRMED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }

  private ContactKind __ContactKind_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "PERSON": return ContactKind.PERSON;
      case "PLACE": return ContactKind.PLACE;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
