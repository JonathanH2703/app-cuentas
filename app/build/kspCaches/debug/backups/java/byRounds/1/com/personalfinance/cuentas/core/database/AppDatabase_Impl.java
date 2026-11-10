package com.personalfinance.cuentas.core.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.AccountDao_Impl;
import com.personalfinance.cuentas.data.local.dao.AllocationDao;
import com.personalfinance.cuentas.data.local.dao.AllocationDao_Impl;
import com.personalfinance.cuentas.data.local.dao.BackupDao;
import com.personalfinance.cuentas.data.local.dao.BackupDao_Impl;
import com.personalfinance.cuentas.data.local.dao.CategoryDao;
import com.personalfinance.cuentas.data.local.dao.CategoryDao_Impl;
import com.personalfinance.cuentas.data.local.dao.ContactDao;
import com.personalfinance.cuentas.data.local.dao.ContactDao_Impl;
import com.personalfinance.cuentas.data.local.dao.CreditCardDao;
import com.personalfinance.cuentas.data.local.dao.CreditCardDao_Impl;
import com.personalfinance.cuentas.data.local.dao.DebtDao;
import com.personalfinance.cuentas.data.local.dao.DebtDao_Impl;
import com.personalfinance.cuentas.data.local.dao.EventDao;
import com.personalfinance.cuentas.data.local.dao.EventDao_Impl;
import com.personalfinance.cuentas.data.local.dao.IncomeDao;
import com.personalfinance.cuentas.data.local.dao.IncomeDao_Impl;
import com.personalfinance.cuentas.data.local.dao.MerchantLearningDao;
import com.personalfinance.cuentas.data.local.dao.MerchantLearningDao_Impl;
import com.personalfinance.cuentas.data.local.dao.PendingTransactionDao;
import com.personalfinance.cuentas.data.local.dao.PendingTransactionDao_Impl;
import com.personalfinance.cuentas.data.local.dao.ReceivableDao;
import com.personalfinance.cuentas.data.local.dao.ReceivableDao_Impl;
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao;
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao_Impl;
import com.personalfinance.cuentas.data.local.dao.TagDao;
import com.personalfinance.cuentas.data.local.dao.TagDao_Impl;
import com.personalfinance.cuentas.data.local.dao.TransactionDao;
import com.personalfinance.cuentas.data.local.dao.TransactionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile AccountDao _accountDao;

  private volatile TransactionDao _transactionDao;

  private volatile AllocationDao _allocationDao;

  private volatile CategoryDao _categoryDao;

  private volatile IncomeDao _incomeDao;

  private volatile TagDao _tagDao;

  private volatile CreditCardDao _creditCardDao;

  private volatile DebtDao _debtDao;

  private volatile PendingTransactionDao _pendingTransactionDao;

  private volatile MerchantLearningDao _merchantLearningDao;

  private volatile BackupDao _backupDao;

  private volatile RecurringItemDao _recurringItemDao;

  private volatile ContactDao _contactDao;

  private volatile ReceivableDao _receivableDao;

  private volatile EventDao _eventDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(17) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `accounts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `current_balance_cents` INTEGER NOT NULL, `color_hex` TEXT NOT NULL, `icon_name` TEXT NOT NULL, `is_archived` INTEGER NOT NULL, `include_in_total` INTEGER NOT NULL, `is_exempt_from_gmf` INTEGER NOT NULL, `annual_yield_rate` REAL, `is_static_saving` INTEGER NOT NULL, `created_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `icon_name` TEXT NOT NULL, `color_hex` TEXT NOT NULL, `default_block_type` TEXT NOT NULL, `is_system` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_name` ON `tags` (`name`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `account_id` INTEGER NOT NULL, `category_id` INTEGER NOT NULL, `block_id` INTEGER, `amount_cents` INTEGER NOT NULL, `type` TEXT NOT NULL, `date_millis` INTEGER NOT NULL, `note` TEXT, `merchant` TEXT, `linked_debt_id` INTEGER, `linked_recurring_item_id` INTEGER, `linked_receivable_id` INTEGER, `is_leisure` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, FOREIGN KEY(`account_id`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`block_id`) REFERENCES `allocation_blocks`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_date_millis` ON `transactions` (`date_millis`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_account_id_date_millis` ON `transactions` (`account_id`, `date_millis`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_category_id` ON `transactions` (`category_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_block_id_date_millis` ON `transactions` (`block_id`, `date_millis`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_linked_debt_id` ON `transactions` (`linked_debt_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_linked_recurring_item_id` ON `transactions` (`linked_recurring_item_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_linked_receivable_id` ON `transactions` (`linked_receivable_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `transaction_tags` (`transaction_id` INTEGER NOT NULL, `tag_id` INTEGER NOT NULL, PRIMARY KEY(`transaction_id`, `tag_id`), FOREIGN KEY(`transaction_id`) REFERENCES `transactions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`tag_id`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_tags_transaction_id` ON `transaction_tags` (`transaction_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_tags_tag_id` ON `transaction_tags` (`tag_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `allocation_models` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `is_active` INTEGER NOT NULL, `created_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `allocation_blocks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `model_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `percentage` INTEGER NOT NULL, `type` TEXT NOT NULL, `color_hex` TEXT NOT NULL, `order_index` INTEGER NOT NULL, `absorb_remainder` INTEGER NOT NULL, FOREIGN KEY(`model_id`) REFERENCES `allocation_models`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_allocation_blocks_model_id` ON `allocation_blocks` (`model_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `incomes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `account_id` INTEGER NOT NULL, `description` TEXT NOT NULL, `amount_cents` INTEGER NOT NULL, `frequency` TEXT NOT NULL, `expected_date_millis` INTEGER NOT NULL, `received_date_millis` INTEGER, `is_received` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, FOREIGN KEY(`account_id`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_incomes_account_id` ON `incomes` (`account_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_incomes_expected_date_millis` ON `incomes` (`expected_date_millis`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_incomes_is_received` ON `incomes` (`is_received`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `income_allocations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `income_id` INTEGER NOT NULL, `block_id` INTEGER NOT NULL, `assigned_cents` INTEGER NOT NULL, FOREIGN KEY(`income_id`) REFERENCES `incomes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`block_id`) REFERENCES `allocation_blocks`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_income_allocations_income_id` ON `income_allocations` (`income_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_income_allocations_block_id` ON `income_allocations` (`block_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `credit_cards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `account_id` INTEGER NOT NULL, `credit_limit_cents` INTEGER NOT NULL, `cut_off_day` INTEGER NOT NULL, `payment_due_day` INTEGER NOT NULL, `interest_rate_ea` REAL NOT NULL, `handling_fee_cents` INTEGER NOT NULL, `last_four_digits` TEXT, `revolving_rate` REAL NOT NULL, `monthly_insurance_cents` INTEGER NOT NULL, `installment_type` INTEGER NOT NULL, FOREIGN KEY(`account_id`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_credit_cards_account_id` ON `credit_cards` (`account_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `card_purchases` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `card_id` INTEGER NOT NULL, `merchant` TEXT NOT NULL, `purchase_date_millis` INTEGER NOT NULL, `total_amount_cents` INTEGER NOT NULL, `installments_total` INTEGER NOT NULL, `installments_paid` INTEGER NOT NULL, `interest_rate_nm` REAL NOT NULL, `is_fully_paid` INTEGER NOT NULL, FOREIGN KEY(`card_id`) REFERENCES `credit_cards`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_card_purchases_card_id` ON `card_purchases` (`card_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_card_purchases_card_id_is_fully_paid` ON `card_purchases` (`card_id`, `is_fully_paid`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `debts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `principal_amount_cents` INTEGER NOT NULL, `current_balance_cents` INTEGER NOT NULL, `interest_rate_ea` REAL NOT NULL, `monthly_installment_cents` INTEGER NOT NULL, `due_day_of_month` INTEGER NOT NULL, `start_date_millis` INTEGER NOT NULL, `next_due_date_millis` INTEGER, `monthly_insurance_cents` INTEGER NOT NULL, `is_settled` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `debt_payments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `debt_id` INTEGER NOT NULL, `payment_date_millis` INTEGER NOT NULL, `total_paid_cents` INTEGER NOT NULL, `principal_paid_cents` INTEGER NOT NULL, `interest_paid_cents` INTEGER NOT NULL, `is_extra_payment` INTEGER NOT NULL, FOREIGN KEY(`debt_id`) REFERENCES `debts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_debt_payments_debt_id` ON `debt_payments` (`debt_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `block_id` INTEGER NOT NULL, `category_id` INTEGER, `month` INTEGER NOT NULL, `year` INTEGER NOT NULL, `limit_amount_cents` INTEGER NOT NULL, FOREIGN KEY(`block_id`) REFERENCES `allocation_blocks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_budgets_block_id` ON `budgets` (`block_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_budgets_category_id` ON `budgets` (`category_id`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_budgets_block_id_category_id_year_month` ON `budgets` (`block_id`, `category_id`, `year`, `month`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `savings_goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `target_amount_cents` INTEGER NOT NULL, `current_saved_cents` INTEGER NOT NULL, `target_date_millis` INTEGER, `is_emergency_fund` INTEGER NOT NULL, `is_completed` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recurring_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `account_id` INTEGER NOT NULL, `category_id` INTEGER NOT NULL, `block_id` INTEGER NOT NULL, `title` TEXT NOT NULL, `amount_cents` INTEGER NOT NULL, `frequency` TEXT NOT NULL, `next_due_date_millis` INTEGER NOT NULL, `notify_days_before` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, FOREIGN KEY(`account_id`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`block_id`) REFERENCES `allocation_blocks`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recurring_items_account_id` ON `recurring_items` (`account_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recurring_items_category_id` ON `recurring_items` (`category_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recurring_items_block_id` ON `recurring_items` (`block_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recurring_items_next_due_date_millis` ON `recurring_items` (`next_due_date_millis`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `capture_rules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sender_filter` TEXT NOT NULL, `pattern_type` TEXT NOT NULL, `regex_pattern` TEXT NOT NULL, `extracted_type` TEXT NOT NULL, `default_account_id` INTEGER, `confidence_score` REAL NOT NULL, `is_active` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pending_transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `source` TEXT NOT NULL, `source_sender` TEXT NOT NULL, `raw_text` TEXT, `deduplication_hash` TEXT NOT NULL, `suggested_amount_cents` INTEGER NOT NULL, `suggested_merchant` TEXT, `suggested_account_id` INTEGER, `suggested_category_id` INTEGER, `suggested_block_id` INTEGER, `suggested_type` TEXT NOT NULL, `suggested_card_id` INTEGER, `suggested_debt_id` INTEGER, `detected_date_millis` INTEGER NOT NULL, `status` TEXT NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_pending_transactions_deduplication_hash` ON `pending_transactions` (`deduplication_hash`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_pending_transactions_status_detected_date_millis` ON `pending_transactions` (`status`, `detected_date_millis`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `merchant_learnings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `normalized_merchant` TEXT NOT NULL, `category_id` INTEGER NOT NULL, `block_id` INTEGER NOT NULL, `hit_count` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_merchant_learnings_normalized_merchant` ON `merchant_learnings` (`normalized_merchant`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `contacts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `kind` TEXT NOT NULL, `created_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `receivables` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `contact_id` INTEGER NOT NULL, `source_transaction_id` INTEGER, `note` TEXT, `owed_cents` INTEGER NOT NULL, `is_settled` INTEGER NOT NULL, `settled_at_millis` INTEGER, `settled_account_id` INTEGER, `settled_transaction_id` INTEGER, `created_at` INTEGER NOT NULL, FOREIGN KEY(`contact_id`) REFERENCES `contacts`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`source_transaction_id`) REFERENCES `transactions`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_receivables_contact_id` ON `receivables` (`contact_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_receivables_source_transaction_id` ON `receivables` (`source_transaction_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `type` TEXT NOT NULL, `day_of_month` INTEGER NOT NULL, `month_of_year` INTEGER NOT NULL, `year` INTEGER, `person_name` TEXT, `notify_days_before` INTEGER NOT NULL, `is_active` INTEGER NOT NULL, `total_amount_cents` INTEGER, `accumulated_cents` INTEGER NOT NULL, `coverage_percent` INTEGER NOT NULL, `block_type` TEXT, `created_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0881108c7caf5f10aa6b5e6fe3308999')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `accounts`");
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `transactions`");
        db.execSQL("DROP TABLE IF EXISTS `transaction_tags`");
        db.execSQL("DROP TABLE IF EXISTS `allocation_models`");
        db.execSQL("DROP TABLE IF EXISTS `allocation_blocks`");
        db.execSQL("DROP TABLE IF EXISTS `incomes`");
        db.execSQL("DROP TABLE IF EXISTS `income_allocations`");
        db.execSQL("DROP TABLE IF EXISTS `credit_cards`");
        db.execSQL("DROP TABLE IF EXISTS `card_purchases`");
        db.execSQL("DROP TABLE IF EXISTS `debts`");
        db.execSQL("DROP TABLE IF EXISTS `debt_payments`");
        db.execSQL("DROP TABLE IF EXISTS `budgets`");
        db.execSQL("DROP TABLE IF EXISTS `savings_goals`");
        db.execSQL("DROP TABLE IF EXISTS `recurring_items`");
        db.execSQL("DROP TABLE IF EXISTS `capture_rules`");
        db.execSQL("DROP TABLE IF EXISTS `pending_transactions`");
        db.execSQL("DROP TABLE IF EXISTS `merchant_learnings`");
        db.execSQL("DROP TABLE IF EXISTS `contacts`");
        db.execSQL("DROP TABLE IF EXISTS `receivables`");
        db.execSQL("DROP TABLE IF EXISTS `events`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAccounts = new HashMap<String, TableInfo.Column>(12);
        _columnsAccounts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("current_balance_cents", new TableInfo.Column("current_balance_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("icon_name", new TableInfo.Column("icon_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("is_archived", new TableInfo.Column("is_archived", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("include_in_total", new TableInfo.Column("include_in_total", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("is_exempt_from_gmf", new TableInfo.Column("is_exempt_from_gmf", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("annual_yield_rate", new TableInfo.Column("annual_yield_rate", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("is_static_saving", new TableInfo.Column("is_static_saving", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAccounts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAccounts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAccounts = new TableInfo("accounts", _columnsAccounts, _foreignKeysAccounts, _indicesAccounts);
        final TableInfo _existingAccounts = TableInfo.read(db, "accounts");
        if (!_infoAccounts.equals(_existingAccounts)) {
          return new RoomOpenHelper.ValidationResult(false, "accounts(com.personalfinance.cuentas.data.local.entities.AccountEntity).\n"
                  + " Expected:\n" + _infoAccounts + "\n"
                  + " Found:\n" + _existingAccounts);
        }
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(6);
        _columnsCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("icon_name", new TableInfo.Column("icon_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("default_block_type", new TableInfo.Column("default_block_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("is_system", new TableInfo.Column("is_system", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.personalfinance.cuentas.data.local.entities.CategoryEntity).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsTags = new HashMap<String, TableInfo.Column>(2);
        _columnsTags.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTags = new HashSet<TableInfo.Index>(1);
        _indicesTags.add(new TableInfo.Index("index_tags_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        final TableInfo _infoTags = new TableInfo("tags", _columnsTags, _foreignKeysTags, _indicesTags);
        final TableInfo _existingTags = TableInfo.read(db, "tags");
        if (!_infoTags.equals(_existingTags)) {
          return new RoomOpenHelper.ValidationResult(false, "tags(com.personalfinance.cuentas.data.local.entities.TagEntity).\n"
                  + " Expected:\n" + _infoTags + "\n"
                  + " Found:\n" + _existingTags);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(14);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("account_id", new TableInfo.Column("account_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("category_id", new TableInfo.Column("category_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("block_id", new TableInfo.Column("block_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount_cents", new TableInfo.Column("amount_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("date_millis", new TableInfo.Column("date_millis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("merchant", new TableInfo.Column("merchant", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("linked_debt_id", new TableInfo.Column("linked_debt_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("linked_recurring_item_id", new TableInfo.Column("linked_recurring_item_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("linked_receivable_id", new TableInfo.Column("linked_receivable_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("is_leisure", new TableInfo.Column("is_leisure", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(3);
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("accounts", "RESTRICT", "NO ACTION", Arrays.asList("account_id"), Arrays.asList("id")));
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("categories", "RESTRICT", "NO ACTION", Arrays.asList("category_id"), Arrays.asList("id")));
        _foreignKeysTransactions.add(new TableInfo.ForeignKey("allocation_blocks", "SET NULL", "NO ACTION", Arrays.asList("block_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(7);
        _indicesTransactions.add(new TableInfo.Index("index_transactions_date_millis", false, Arrays.asList("date_millis"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_account_id_date_millis", false, Arrays.asList("account_id", "date_millis"), Arrays.asList("ASC", "ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_category_id", false, Arrays.asList("category_id"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_block_id_date_millis", false, Arrays.asList("block_id", "date_millis"), Arrays.asList("ASC", "ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_linked_debt_id", false, Arrays.asList("linked_debt_id"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_linked_recurring_item_id", false, Arrays.asList("linked_recurring_item_id"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_linked_receivable_id", false, Arrays.asList("linked_receivable_id"), Arrays.asList("ASC")));
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.personalfinance.cuentas.data.local.entities.TransactionEntity).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsTransactionTags = new HashMap<String, TableInfo.Column>(2);
        _columnsTransactionTags.put("transaction_id", new TableInfo.Column("transaction_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactionTags.put("tag_id", new TableInfo.Column("tag_id", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactionTags = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysTransactionTags.add(new TableInfo.ForeignKey("transactions", "CASCADE", "NO ACTION", Arrays.asList("transaction_id"), Arrays.asList("id")));
        _foreignKeysTransactionTags.add(new TableInfo.ForeignKey("tags", "CASCADE", "NO ACTION", Arrays.asList("tag_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesTransactionTags = new HashSet<TableInfo.Index>(2);
        _indicesTransactionTags.add(new TableInfo.Index("index_transaction_tags_transaction_id", false, Arrays.asList("transaction_id"), Arrays.asList("ASC")));
        _indicesTransactionTags.add(new TableInfo.Index("index_transaction_tags_tag_id", false, Arrays.asList("tag_id"), Arrays.asList("ASC")));
        final TableInfo _infoTransactionTags = new TableInfo("transaction_tags", _columnsTransactionTags, _foreignKeysTransactionTags, _indicesTransactionTags);
        final TableInfo _existingTransactionTags = TableInfo.read(db, "transaction_tags");
        if (!_infoTransactionTags.equals(_existingTransactionTags)) {
          return new RoomOpenHelper.ValidationResult(false, "transaction_tags(com.personalfinance.cuentas.data.local.entities.TransactionTagEntity).\n"
                  + " Expected:\n" + _infoTransactionTags + "\n"
                  + " Found:\n" + _existingTransactionTags);
        }
        final HashMap<String, TableInfo.Column> _columnsAllocationModels = new HashMap<String, TableInfo.Column>(4);
        _columnsAllocationModels.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationModels.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationModels.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationModels.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAllocationModels = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAllocationModels = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAllocationModels = new TableInfo("allocation_models", _columnsAllocationModels, _foreignKeysAllocationModels, _indicesAllocationModels);
        final TableInfo _existingAllocationModels = TableInfo.read(db, "allocation_models");
        if (!_infoAllocationModels.equals(_existingAllocationModels)) {
          return new RoomOpenHelper.ValidationResult(false, "allocation_models(com.personalfinance.cuentas.data.local.entities.AllocationModelEntity).\n"
                  + " Expected:\n" + _infoAllocationModels + "\n"
                  + " Found:\n" + _existingAllocationModels);
        }
        final HashMap<String, TableInfo.Column> _columnsAllocationBlocks = new HashMap<String, TableInfo.Column>(8);
        _columnsAllocationBlocks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationBlocks.put("model_id", new TableInfo.Column("model_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationBlocks.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationBlocks.put("percentage", new TableInfo.Column("percentage", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationBlocks.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationBlocks.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationBlocks.put("order_index", new TableInfo.Column("order_index", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllocationBlocks.put("absorb_remainder", new TableInfo.Column("absorb_remainder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAllocationBlocks = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysAllocationBlocks.add(new TableInfo.ForeignKey("allocation_models", "CASCADE", "NO ACTION", Arrays.asList("model_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAllocationBlocks = new HashSet<TableInfo.Index>(1);
        _indicesAllocationBlocks.add(new TableInfo.Index("index_allocation_blocks_model_id", false, Arrays.asList("model_id"), Arrays.asList("ASC")));
        final TableInfo _infoAllocationBlocks = new TableInfo("allocation_blocks", _columnsAllocationBlocks, _foreignKeysAllocationBlocks, _indicesAllocationBlocks);
        final TableInfo _existingAllocationBlocks = TableInfo.read(db, "allocation_blocks");
        if (!_infoAllocationBlocks.equals(_existingAllocationBlocks)) {
          return new RoomOpenHelper.ValidationResult(false, "allocation_blocks(com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity).\n"
                  + " Expected:\n" + _infoAllocationBlocks + "\n"
                  + " Found:\n" + _existingAllocationBlocks);
        }
        final HashMap<String, TableInfo.Column> _columnsIncomes = new HashMap<String, TableInfo.Column>(9);
        _columnsIncomes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("account_id", new TableInfo.Column("account_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("amount_cents", new TableInfo.Column("amount_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("frequency", new TableInfo.Column("frequency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("expected_date_millis", new TableInfo.Column("expected_date_millis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("received_date_millis", new TableInfo.Column("received_date_millis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("is_received", new TableInfo.Column("is_received", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncomes = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysIncomes.add(new TableInfo.ForeignKey("accounts", "RESTRICT", "NO ACTION", Arrays.asList("account_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIncomes = new HashSet<TableInfo.Index>(3);
        _indicesIncomes.add(new TableInfo.Index("index_incomes_account_id", false, Arrays.asList("account_id"), Arrays.asList("ASC")));
        _indicesIncomes.add(new TableInfo.Index("index_incomes_expected_date_millis", false, Arrays.asList("expected_date_millis"), Arrays.asList("ASC")));
        _indicesIncomes.add(new TableInfo.Index("index_incomes_is_received", false, Arrays.asList("is_received"), Arrays.asList("ASC")));
        final TableInfo _infoIncomes = new TableInfo("incomes", _columnsIncomes, _foreignKeysIncomes, _indicesIncomes);
        final TableInfo _existingIncomes = TableInfo.read(db, "incomes");
        if (!_infoIncomes.equals(_existingIncomes)) {
          return new RoomOpenHelper.ValidationResult(false, "incomes(com.personalfinance.cuentas.data.local.entities.IncomeEntity).\n"
                  + " Expected:\n" + _infoIncomes + "\n"
                  + " Found:\n" + _existingIncomes);
        }
        final HashMap<String, TableInfo.Column> _columnsIncomeAllocations = new HashMap<String, TableInfo.Column>(4);
        _columnsIncomeAllocations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeAllocations.put("income_id", new TableInfo.Column("income_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeAllocations.put("block_id", new TableInfo.Column("block_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomeAllocations.put("assigned_cents", new TableInfo.Column("assigned_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncomeAllocations = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysIncomeAllocations.add(new TableInfo.ForeignKey("incomes", "CASCADE", "NO ACTION", Arrays.asList("income_id"), Arrays.asList("id")));
        _foreignKeysIncomeAllocations.add(new TableInfo.ForeignKey("allocation_blocks", "RESTRICT", "NO ACTION", Arrays.asList("block_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIncomeAllocations = new HashSet<TableInfo.Index>(2);
        _indicesIncomeAllocations.add(new TableInfo.Index("index_income_allocations_income_id", false, Arrays.asList("income_id"), Arrays.asList("ASC")));
        _indicesIncomeAllocations.add(new TableInfo.Index("index_income_allocations_block_id", false, Arrays.asList("block_id"), Arrays.asList("ASC")));
        final TableInfo _infoIncomeAllocations = new TableInfo("income_allocations", _columnsIncomeAllocations, _foreignKeysIncomeAllocations, _indicesIncomeAllocations);
        final TableInfo _existingIncomeAllocations = TableInfo.read(db, "income_allocations");
        if (!_infoIncomeAllocations.equals(_existingIncomeAllocations)) {
          return new RoomOpenHelper.ValidationResult(false, "income_allocations(com.personalfinance.cuentas.data.local.entities.IncomeAllocationEntity).\n"
                  + " Expected:\n" + _infoIncomeAllocations + "\n"
                  + " Found:\n" + _existingIncomeAllocations);
        }
        final HashMap<String, TableInfo.Column> _columnsCreditCards = new HashMap<String, TableInfo.Column>(11);
        _columnsCreditCards.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("account_id", new TableInfo.Column("account_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("credit_limit_cents", new TableInfo.Column("credit_limit_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("cut_off_day", new TableInfo.Column("cut_off_day", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("payment_due_day", new TableInfo.Column("payment_due_day", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("interest_rate_ea", new TableInfo.Column("interest_rate_ea", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("handling_fee_cents", new TableInfo.Column("handling_fee_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("last_four_digits", new TableInfo.Column("last_four_digits", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("revolving_rate", new TableInfo.Column("revolving_rate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("monthly_insurance_cents", new TableInfo.Column("monthly_insurance_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("installment_type", new TableInfo.Column("installment_type", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCreditCards = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCreditCards.add(new TableInfo.ForeignKey("accounts", "CASCADE", "NO ACTION", Arrays.asList("account_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCreditCards = new HashSet<TableInfo.Index>(1);
        _indicesCreditCards.add(new TableInfo.Index("index_credit_cards_account_id", false, Arrays.asList("account_id"), Arrays.asList("ASC")));
        final TableInfo _infoCreditCards = new TableInfo("credit_cards", _columnsCreditCards, _foreignKeysCreditCards, _indicesCreditCards);
        final TableInfo _existingCreditCards = TableInfo.read(db, "credit_cards");
        if (!_infoCreditCards.equals(_existingCreditCards)) {
          return new RoomOpenHelper.ValidationResult(false, "credit_cards(com.personalfinance.cuentas.data.local.entities.CreditCardEntity).\n"
                  + " Expected:\n" + _infoCreditCards + "\n"
                  + " Found:\n" + _existingCreditCards);
        }
        final HashMap<String, TableInfo.Column> _columnsCardPurchases = new HashMap<String, TableInfo.Column>(9);
        _columnsCardPurchases.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardPurchases.put("card_id", new TableInfo.Column("card_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardPurchases.put("merchant", new TableInfo.Column("merchant", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardPurchases.put("purchase_date_millis", new TableInfo.Column("purchase_date_millis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardPurchases.put("total_amount_cents", new TableInfo.Column("total_amount_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardPurchases.put("installments_total", new TableInfo.Column("installments_total", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardPurchases.put("installments_paid", new TableInfo.Column("installments_paid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardPurchases.put("interest_rate_nm", new TableInfo.Column("interest_rate_nm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardPurchases.put("is_fully_paid", new TableInfo.Column("is_fully_paid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCardPurchases = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCardPurchases.add(new TableInfo.ForeignKey("credit_cards", "CASCADE", "NO ACTION", Arrays.asList("card_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCardPurchases = new HashSet<TableInfo.Index>(2);
        _indicesCardPurchases.add(new TableInfo.Index("index_card_purchases_card_id", false, Arrays.asList("card_id"), Arrays.asList("ASC")));
        _indicesCardPurchases.add(new TableInfo.Index("index_card_purchases_card_id_is_fully_paid", false, Arrays.asList("card_id", "is_fully_paid"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoCardPurchases = new TableInfo("card_purchases", _columnsCardPurchases, _foreignKeysCardPurchases, _indicesCardPurchases);
        final TableInfo _existingCardPurchases = TableInfo.read(db, "card_purchases");
        if (!_infoCardPurchases.equals(_existingCardPurchases)) {
          return new RoomOpenHelper.ValidationResult(false, "card_purchases(com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity).\n"
                  + " Expected:\n" + _infoCardPurchases + "\n"
                  + " Found:\n" + _existingCardPurchases);
        }
        final HashMap<String, TableInfo.Column> _columnsDebts = new HashMap<String, TableInfo.Column>(11);
        _columnsDebts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("principal_amount_cents", new TableInfo.Column("principal_amount_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("current_balance_cents", new TableInfo.Column("current_balance_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("interest_rate_ea", new TableInfo.Column("interest_rate_ea", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("monthly_installment_cents", new TableInfo.Column("monthly_installment_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("due_day_of_month", new TableInfo.Column("due_day_of_month", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("start_date_millis", new TableInfo.Column("start_date_millis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("next_due_date_millis", new TableInfo.Column("next_due_date_millis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("monthly_insurance_cents", new TableInfo.Column("monthly_insurance_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebts.put("is_settled", new TableInfo.Column("is_settled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDebts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDebts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDebts = new TableInfo("debts", _columnsDebts, _foreignKeysDebts, _indicesDebts);
        final TableInfo _existingDebts = TableInfo.read(db, "debts");
        if (!_infoDebts.equals(_existingDebts)) {
          return new RoomOpenHelper.ValidationResult(false, "debts(com.personalfinance.cuentas.data.local.entities.DebtEntity).\n"
                  + " Expected:\n" + _infoDebts + "\n"
                  + " Found:\n" + _existingDebts);
        }
        final HashMap<String, TableInfo.Column> _columnsDebtPayments = new HashMap<String, TableInfo.Column>(7);
        _columnsDebtPayments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("debt_id", new TableInfo.Column("debt_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("payment_date_millis", new TableInfo.Column("payment_date_millis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("total_paid_cents", new TableInfo.Column("total_paid_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("principal_paid_cents", new TableInfo.Column("principal_paid_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("interest_paid_cents", new TableInfo.Column("interest_paid_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDebtPayments.put("is_extra_payment", new TableInfo.Column("is_extra_payment", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDebtPayments = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDebtPayments.add(new TableInfo.ForeignKey("debts", "CASCADE", "NO ACTION", Arrays.asList("debt_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDebtPayments = new HashSet<TableInfo.Index>(1);
        _indicesDebtPayments.add(new TableInfo.Index("index_debt_payments_debt_id", false, Arrays.asList("debt_id"), Arrays.asList("ASC")));
        final TableInfo _infoDebtPayments = new TableInfo("debt_payments", _columnsDebtPayments, _foreignKeysDebtPayments, _indicesDebtPayments);
        final TableInfo _existingDebtPayments = TableInfo.read(db, "debt_payments");
        if (!_infoDebtPayments.equals(_existingDebtPayments)) {
          return new RoomOpenHelper.ValidationResult(false, "debt_payments(com.personalfinance.cuentas.data.local.entities.DebtPaymentEntity).\n"
                  + " Expected:\n" + _infoDebtPayments + "\n"
                  + " Found:\n" + _existingDebtPayments);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgets = new HashMap<String, TableInfo.Column>(6);
        _columnsBudgets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("block_id", new TableInfo.Column("block_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("category_id", new TableInfo.Column("category_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("month", new TableInfo.Column("month", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("year", new TableInfo.Column("year", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgets.put("limit_amount_cents", new TableInfo.Column("limit_amount_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgets = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysBudgets.add(new TableInfo.ForeignKey("allocation_blocks", "CASCADE", "NO ACTION", Arrays.asList("block_id"), Arrays.asList("id")));
        _foreignKeysBudgets.add(new TableInfo.ForeignKey("categories", "CASCADE", "NO ACTION", Arrays.asList("category_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesBudgets = new HashSet<TableInfo.Index>(3);
        _indicesBudgets.add(new TableInfo.Index("index_budgets_block_id", false, Arrays.asList("block_id"), Arrays.asList("ASC")));
        _indicesBudgets.add(new TableInfo.Index("index_budgets_category_id", false, Arrays.asList("category_id"), Arrays.asList("ASC")));
        _indicesBudgets.add(new TableInfo.Index("index_budgets_block_id_category_id_year_month", true, Arrays.asList("block_id", "category_id", "year", "month"), Arrays.asList("ASC", "ASC", "ASC", "ASC")));
        final TableInfo _infoBudgets = new TableInfo("budgets", _columnsBudgets, _foreignKeysBudgets, _indicesBudgets);
        final TableInfo _existingBudgets = TableInfo.read(db, "budgets");
        if (!_infoBudgets.equals(_existingBudgets)) {
          return new RoomOpenHelper.ValidationResult(false, "budgets(com.personalfinance.cuentas.data.local.entities.BudgetEntity).\n"
                  + " Expected:\n" + _infoBudgets + "\n"
                  + " Found:\n" + _existingBudgets);
        }
        final HashMap<String, TableInfo.Column> _columnsSavingsGoals = new HashMap<String, TableInfo.Column>(7);
        _columnsSavingsGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("target_amount_cents", new TableInfo.Column("target_amount_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("current_saved_cents", new TableInfo.Column("current_saved_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("target_date_millis", new TableInfo.Column("target_date_millis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("is_emergency_fund", new TableInfo.Column("is_emergency_fund", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavingsGoals.put("is_completed", new TableInfo.Column("is_completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSavingsGoals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSavingsGoals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSavingsGoals = new TableInfo("savings_goals", _columnsSavingsGoals, _foreignKeysSavingsGoals, _indicesSavingsGoals);
        final TableInfo _existingSavingsGoals = TableInfo.read(db, "savings_goals");
        if (!_infoSavingsGoals.equals(_existingSavingsGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "savings_goals(com.personalfinance.cuentas.data.local.entities.SavingsGoalEntity).\n"
                  + " Expected:\n" + _infoSavingsGoals + "\n"
                  + " Found:\n" + _existingSavingsGoals);
        }
        final HashMap<String, TableInfo.Column> _columnsRecurringItems = new HashMap<String, TableInfo.Column>(10);
        _columnsRecurringItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringItems.put("account_id", new TableInfo.Column("account_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringItems.put("category_id", new TableInfo.Column("category_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringItems.put("block_id", new TableInfo.Column("block_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringItems.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringItems.put("amount_cents", new TableInfo.Column("amount_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringItems.put("frequency", new TableInfo.Column("frequency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringItems.put("next_due_date_millis", new TableInfo.Column("next_due_date_millis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringItems.put("notify_days_before", new TableInfo.Column("notify_days_before", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecurringItems.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecurringItems = new HashSet<TableInfo.ForeignKey>(3);
        _foreignKeysRecurringItems.add(new TableInfo.ForeignKey("accounts", "RESTRICT", "NO ACTION", Arrays.asList("account_id"), Arrays.asList("id")));
        _foreignKeysRecurringItems.add(new TableInfo.ForeignKey("categories", "RESTRICT", "NO ACTION", Arrays.asList("category_id"), Arrays.asList("id")));
        _foreignKeysRecurringItems.add(new TableInfo.ForeignKey("allocation_blocks", "RESTRICT", "NO ACTION", Arrays.asList("block_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRecurringItems = new HashSet<TableInfo.Index>(4);
        _indicesRecurringItems.add(new TableInfo.Index("index_recurring_items_account_id", false, Arrays.asList("account_id"), Arrays.asList("ASC")));
        _indicesRecurringItems.add(new TableInfo.Index("index_recurring_items_category_id", false, Arrays.asList("category_id"), Arrays.asList("ASC")));
        _indicesRecurringItems.add(new TableInfo.Index("index_recurring_items_block_id", false, Arrays.asList("block_id"), Arrays.asList("ASC")));
        _indicesRecurringItems.add(new TableInfo.Index("index_recurring_items_next_due_date_millis", false, Arrays.asList("next_due_date_millis"), Arrays.asList("ASC")));
        final TableInfo _infoRecurringItems = new TableInfo("recurring_items", _columnsRecurringItems, _foreignKeysRecurringItems, _indicesRecurringItems);
        final TableInfo _existingRecurringItems = TableInfo.read(db, "recurring_items");
        if (!_infoRecurringItems.equals(_existingRecurringItems)) {
          return new RoomOpenHelper.ValidationResult(false, "recurring_items(com.personalfinance.cuentas.data.local.entities.RecurringItemEntity).\n"
                  + " Expected:\n" + _infoRecurringItems + "\n"
                  + " Found:\n" + _existingRecurringItems);
        }
        final HashMap<String, TableInfo.Column> _columnsCaptureRules = new HashMap<String, TableInfo.Column>(8);
        _columnsCaptureRules.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCaptureRules.put("sender_filter", new TableInfo.Column("sender_filter", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCaptureRules.put("pattern_type", new TableInfo.Column("pattern_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCaptureRules.put("regex_pattern", new TableInfo.Column("regex_pattern", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCaptureRules.put("extracted_type", new TableInfo.Column("extracted_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCaptureRules.put("default_account_id", new TableInfo.Column("default_account_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCaptureRules.put("confidence_score", new TableInfo.Column("confidence_score", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCaptureRules.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCaptureRules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCaptureRules = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCaptureRules = new TableInfo("capture_rules", _columnsCaptureRules, _foreignKeysCaptureRules, _indicesCaptureRules);
        final TableInfo _existingCaptureRules = TableInfo.read(db, "capture_rules");
        if (!_infoCaptureRules.equals(_existingCaptureRules)) {
          return new RoomOpenHelper.ValidationResult(false, "capture_rules(com.personalfinance.cuentas.data.local.entities.CaptureRuleEntity).\n"
                  + " Expected:\n" + _infoCaptureRules + "\n"
                  + " Found:\n" + _existingCaptureRules);
        }
        final HashMap<String, TableInfo.Column> _columnsPendingTransactions = new HashMap<String, TableInfo.Column>(15);
        _columnsPendingTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("source_sender", new TableInfo.Column("source_sender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("raw_text", new TableInfo.Column("raw_text", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("deduplication_hash", new TableInfo.Column("deduplication_hash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("suggested_amount_cents", new TableInfo.Column("suggested_amount_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("suggested_merchant", new TableInfo.Column("suggested_merchant", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("suggested_account_id", new TableInfo.Column("suggested_account_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("suggested_category_id", new TableInfo.Column("suggested_category_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("suggested_block_id", new TableInfo.Column("suggested_block_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("suggested_type", new TableInfo.Column("suggested_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("suggested_card_id", new TableInfo.Column("suggested_card_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("suggested_debt_id", new TableInfo.Column("suggested_debt_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("detected_date_millis", new TableInfo.Column("detected_date_millis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingTransactions.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPendingTransactions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPendingTransactions = new HashSet<TableInfo.Index>(2);
        _indicesPendingTransactions.add(new TableInfo.Index("index_pending_transactions_deduplication_hash", false, Arrays.asList("deduplication_hash"), Arrays.asList("ASC")));
        _indicesPendingTransactions.add(new TableInfo.Index("index_pending_transactions_status_detected_date_millis", false, Arrays.asList("status", "detected_date_millis"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoPendingTransactions = new TableInfo("pending_transactions", _columnsPendingTransactions, _foreignKeysPendingTransactions, _indicesPendingTransactions);
        final TableInfo _existingPendingTransactions = TableInfo.read(db, "pending_transactions");
        if (!_infoPendingTransactions.equals(_existingPendingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "pending_transactions(com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity).\n"
                  + " Expected:\n" + _infoPendingTransactions + "\n"
                  + " Found:\n" + _existingPendingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsMerchantLearnings = new HashMap<String, TableInfo.Column>(5);
        _columnsMerchantLearnings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMerchantLearnings.put("normalized_merchant", new TableInfo.Column("normalized_merchant", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMerchantLearnings.put("category_id", new TableInfo.Column("category_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMerchantLearnings.put("block_id", new TableInfo.Column("block_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMerchantLearnings.put("hit_count", new TableInfo.Column("hit_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMerchantLearnings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMerchantLearnings = new HashSet<TableInfo.Index>(1);
        _indicesMerchantLearnings.add(new TableInfo.Index("index_merchant_learnings_normalized_merchant", true, Arrays.asList("normalized_merchant"), Arrays.asList("ASC")));
        final TableInfo _infoMerchantLearnings = new TableInfo("merchant_learnings", _columnsMerchantLearnings, _foreignKeysMerchantLearnings, _indicesMerchantLearnings);
        final TableInfo _existingMerchantLearnings = TableInfo.read(db, "merchant_learnings");
        if (!_infoMerchantLearnings.equals(_existingMerchantLearnings)) {
          return new RoomOpenHelper.ValidationResult(false, "merchant_learnings(com.personalfinance.cuentas.data.local.entities.MerchantLearningEntity).\n"
                  + " Expected:\n" + _infoMerchantLearnings + "\n"
                  + " Found:\n" + _existingMerchantLearnings);
        }
        final HashMap<String, TableInfo.Column> _columnsContacts = new HashMap<String, TableInfo.Column>(4);
        _columnsContacts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("kind", new TableInfo.Column("kind", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoContacts = new TableInfo("contacts", _columnsContacts, _foreignKeysContacts, _indicesContacts);
        final TableInfo _existingContacts = TableInfo.read(db, "contacts");
        if (!_infoContacts.equals(_existingContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "contacts(com.personalfinance.cuentas.data.local.entities.ContactEntity).\n"
                  + " Expected:\n" + _infoContacts + "\n"
                  + " Found:\n" + _existingContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsReceivables = new HashMap<String, TableInfo.Column>(10);
        _columnsReceivables.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivables.put("contact_id", new TableInfo.Column("contact_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivables.put("source_transaction_id", new TableInfo.Column("source_transaction_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivables.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivables.put("owed_cents", new TableInfo.Column("owed_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivables.put("is_settled", new TableInfo.Column("is_settled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivables.put("settled_at_millis", new TableInfo.Column("settled_at_millis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivables.put("settled_account_id", new TableInfo.Column("settled_account_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivables.put("settled_transaction_id", new TableInfo.Column("settled_transaction_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivables.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReceivables = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysReceivables.add(new TableInfo.ForeignKey("contacts", "CASCADE", "NO ACTION", Arrays.asList("contact_id"), Arrays.asList("id")));
        _foreignKeysReceivables.add(new TableInfo.ForeignKey("transactions", "SET NULL", "NO ACTION", Arrays.asList("source_transaction_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesReceivables = new HashSet<TableInfo.Index>(2);
        _indicesReceivables.add(new TableInfo.Index("index_receivables_contact_id", false, Arrays.asList("contact_id"), Arrays.asList("ASC")));
        _indicesReceivables.add(new TableInfo.Index("index_receivables_source_transaction_id", false, Arrays.asList("source_transaction_id"), Arrays.asList("ASC")));
        final TableInfo _infoReceivables = new TableInfo("receivables", _columnsReceivables, _foreignKeysReceivables, _indicesReceivables);
        final TableInfo _existingReceivables = TableInfo.read(db, "receivables");
        if (!_infoReceivables.equals(_existingReceivables)) {
          return new RoomOpenHelper.ValidationResult(false, "receivables(com.personalfinance.cuentas.data.local.entities.ReceivableEntity).\n"
                  + " Expected:\n" + _infoReceivables + "\n"
                  + " Found:\n" + _existingReceivables);
        }
        final HashMap<String, TableInfo.Column> _columnsEvents = new HashMap<String, TableInfo.Column>(14);
        _columnsEvents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("day_of_month", new TableInfo.Column("day_of_month", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("month_of_year", new TableInfo.Column("month_of_year", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("year", new TableInfo.Column("year", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("person_name", new TableInfo.Column("person_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("notify_days_before", new TableInfo.Column("notify_days_before", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("is_active", new TableInfo.Column("is_active", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("total_amount_cents", new TableInfo.Column("total_amount_cents", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("accumulated_cents", new TableInfo.Column("accumulated_cents", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("coverage_percent", new TableInfo.Column("coverage_percent", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("block_type", new TableInfo.Column("block_type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvents.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEvents = new TableInfo("events", _columnsEvents, _foreignKeysEvents, _indicesEvents);
        final TableInfo _existingEvents = TableInfo.read(db, "events");
        if (!_infoEvents.equals(_existingEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "events(com.personalfinance.cuentas.data.local.entities.EventEntity).\n"
                  + " Expected:\n" + _infoEvents + "\n"
                  + " Found:\n" + _existingEvents);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0881108c7caf5f10aa6b5e6fe3308999", "b3103888808441e1eda029bc5f22250e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "accounts","categories","tags","transactions","transaction_tags","allocation_models","allocation_blocks","incomes","income_allocations","credit_cards","card_purchases","debts","debt_payments","budgets","savings_goals","recurring_items","capture_rules","pending_transactions","merchant_learnings","contacts","receivables","events");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `accounts`");
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `tags`");
      _db.execSQL("DELETE FROM `transactions`");
      _db.execSQL("DELETE FROM `transaction_tags`");
      _db.execSQL("DELETE FROM `allocation_models`");
      _db.execSQL("DELETE FROM `allocation_blocks`");
      _db.execSQL("DELETE FROM `incomes`");
      _db.execSQL("DELETE FROM `income_allocations`");
      _db.execSQL("DELETE FROM `credit_cards`");
      _db.execSQL("DELETE FROM `card_purchases`");
      _db.execSQL("DELETE FROM `debts`");
      _db.execSQL("DELETE FROM `debt_payments`");
      _db.execSQL("DELETE FROM `budgets`");
      _db.execSQL("DELETE FROM `savings_goals`");
      _db.execSQL("DELETE FROM `recurring_items`");
      _db.execSQL("DELETE FROM `capture_rules`");
      _db.execSQL("DELETE FROM `pending_transactions`");
      _db.execSQL("DELETE FROM `merchant_learnings`");
      _db.execSQL("DELETE FROM `contacts`");
      _db.execSQL("DELETE FROM `receivables`");
      _db.execSQL("DELETE FROM `events`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AccountDao.class, AccountDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AllocationDao.class, AllocationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IncomeDao.class, IncomeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TagDao.class, TagDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CreditCardDao.class, CreditCardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DebtDao.class, DebtDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PendingTransactionDao.class, PendingTransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MerchantLearningDao.class, MerchantLearningDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BackupDao.class, BackupDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecurringItemDao.class, RecurringItemDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ContactDao.class, ContactDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReceivableDao.class, ReceivableDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EventDao.class, EventDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AccountDao accountDao() {
    if (_accountDao != null) {
      return _accountDao;
    } else {
      synchronized(this) {
        if(_accountDao == null) {
          _accountDao = new AccountDao_Impl(this);
        }
        return _accountDao;
      }
    }
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public AllocationDao allocationDao() {
    if (_allocationDao != null) {
      return _allocationDao;
    } else {
      synchronized(this) {
        if(_allocationDao == null) {
          _allocationDao = new AllocationDao_Impl(this);
        }
        return _allocationDao;
      }
    }
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public IncomeDao incomeDao() {
    if (_incomeDao != null) {
      return _incomeDao;
    } else {
      synchronized(this) {
        if(_incomeDao == null) {
          _incomeDao = new IncomeDao_Impl(this);
        }
        return _incomeDao;
      }
    }
  }

  @Override
  public TagDao tagDao() {
    if (_tagDao != null) {
      return _tagDao;
    } else {
      synchronized(this) {
        if(_tagDao == null) {
          _tagDao = new TagDao_Impl(this);
        }
        return _tagDao;
      }
    }
  }

  @Override
  public CreditCardDao creditCardDao() {
    if (_creditCardDao != null) {
      return _creditCardDao;
    } else {
      synchronized(this) {
        if(_creditCardDao == null) {
          _creditCardDao = new CreditCardDao_Impl(this);
        }
        return _creditCardDao;
      }
    }
  }

  @Override
  public DebtDao debtDao() {
    if (_debtDao != null) {
      return _debtDao;
    } else {
      synchronized(this) {
        if(_debtDao == null) {
          _debtDao = new DebtDao_Impl(this);
        }
        return _debtDao;
      }
    }
  }

  @Override
  public PendingTransactionDao pendingTransactionDao() {
    if (_pendingTransactionDao != null) {
      return _pendingTransactionDao;
    } else {
      synchronized(this) {
        if(_pendingTransactionDao == null) {
          _pendingTransactionDao = new PendingTransactionDao_Impl(this);
        }
        return _pendingTransactionDao;
      }
    }
  }

  @Override
  public MerchantLearningDao merchantLearningDao() {
    if (_merchantLearningDao != null) {
      return _merchantLearningDao;
    } else {
      synchronized(this) {
        if(_merchantLearningDao == null) {
          _merchantLearningDao = new MerchantLearningDao_Impl(this);
        }
        return _merchantLearningDao;
      }
    }
  }

  @Override
  public BackupDao backupDao() {
    if (_backupDao != null) {
      return _backupDao;
    } else {
      synchronized(this) {
        if(_backupDao == null) {
          _backupDao = new BackupDao_Impl(this);
        }
        return _backupDao;
      }
    }
  }

  @Override
  public RecurringItemDao recurringItemDao() {
    if (_recurringItemDao != null) {
      return _recurringItemDao;
    } else {
      synchronized(this) {
        if(_recurringItemDao == null) {
          _recurringItemDao = new RecurringItemDao_Impl(this);
        }
        return _recurringItemDao;
      }
    }
  }

  @Override
  public ContactDao contactDao() {
    if (_contactDao != null) {
      return _contactDao;
    } else {
      synchronized(this) {
        if(_contactDao == null) {
          _contactDao = new ContactDao_Impl(this);
        }
        return _contactDao;
      }
    }
  }

  @Override
  public ReceivableDao receivableDao() {
    if (_receivableDao != null) {
      return _receivableDao;
    } else {
      synchronized(this) {
        if(_receivableDao == null) {
          _receivableDao = new ReceivableDao_Impl(this);
        }
        return _receivableDao;
      }
    }
  }

  @Override
  public EventDao eventDao() {
    if (_eventDao != null) {
      return _eventDao;
    } else {
      synchronized(this) {
        if(_eventDao == null) {
          _eventDao = new EventDao_Impl(this);
        }
        return _eventDao;
      }
    }
  }
}
