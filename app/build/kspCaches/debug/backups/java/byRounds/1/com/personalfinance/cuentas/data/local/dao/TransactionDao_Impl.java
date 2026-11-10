package com.personalfinance.cuentas.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.personalfinance.cuentas.data.local.entities.AccountEntity;
import com.personalfinance.cuentas.data.local.entities.AccountType;
import com.personalfinance.cuentas.data.local.entities.BlockType;
import com.personalfinance.cuentas.data.local.entities.CategoryEntity;
import com.personalfinance.cuentas.data.local.entities.DebtEntity;
import com.personalfinance.cuentas.data.local.entities.IncomeFrequency;
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity;
import com.personalfinance.cuentas.data.local.entities.TransactionEntity;
import com.personalfinance.cuentas.data.local.entities.TransactionType;
import com.personalfinance.cuentas.data.local.relations.TransactionWithDetails;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TransactionEntity> __insertionAdapterOfTransactionEntity;

  private final EntityDeletionOrUpdateAdapter<TransactionEntity> __deletionAdapterOfTransactionEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAnchor;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTransactionDetails;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTransactionType;

  private final SharedSQLiteStatement __preparedStmtOfUpdateReceivableAnchor;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
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
    this.__deletionAdapterOfTransactionEntity = new EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `transactions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TransactionEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateAnchor = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE transactions \n"
                + "        SET linked_debt_id = ?, linked_recurring_item_id = ? \n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTransactionDetails = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE transactions\n"
                + "        SET category_id = ?, block_id = ?, amount_cents = ?, note = ?, is_leisure = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTransactionType = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE transactions SET type = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateReceivableAnchor = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE transactions SET linked_receivable_id = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTransaction(final TransactionEntity transaction,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTransactionEntity.insertAndReturnId(transaction);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTransaction(final TransactionEntity transaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTransactionEntity.handle(transaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAnchor(final long transactionId, final Long debtId,
      final Long recurringItemId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAnchor.acquire();
        int _argIndex = 1;
        if (debtId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, debtId);
        }
        _argIndex = 2;
        if (recurringItemId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, recurringItemId);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, transactionId);
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
          __preparedStmtOfUpdateAnchor.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTransactionDetails(final long transactionId, final long categoryId,
      final Long blockId, final long amountCents, final String note, final boolean isLeisure,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTransactionDetails.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, categoryId);
        _argIndex = 2;
        if (blockId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, blockId);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, amountCents);
        _argIndex = 4;
        if (note == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, note);
        }
        _argIndex = 5;
        final int _tmp = isLeisure ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 6;
        _stmt.bindLong(_argIndex, transactionId);
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
          __preparedStmtOfUpdateTransactionDetails.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTransactionType(final long transactionId, final TransactionType type,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTransactionType.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, __TransactionType_enumToString(type));
        _argIndex = 2;
        _stmt.bindLong(_argIndex, transactionId);
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
          __preparedStmtOfUpdateTransactionType.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReceivableAnchor(final long transactionId, final Long receivableId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateReceivableAnchor.acquire();
        int _argIndex = 1;
        if (receivableId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, receivableId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, transactionId);
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
          __preparedStmtOfUpdateReceivableAnchor.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TransactionEntity>> getAllTransactions() {
    final String _sql = "SELECT `transactions`.`id` AS `id`, `transactions`.`account_id` AS `account_id`, `transactions`.`category_id` AS `category_id`, `transactions`.`block_id` AS `block_id`, `transactions`.`amount_cents` AS `amount_cents`, `transactions`.`type` AS `type`, `transactions`.`date_millis` AS `date_millis`, `transactions`.`note` AS `note`, `transactions`.`merchant` AS `merchant`, `transactions`.`linked_debt_id` AS `linked_debt_id`, `transactions`.`linked_recurring_item_id` AS `linked_recurring_item_id`, `transactions`.`linked_receivable_id` AS `linked_receivable_id`, `transactions`.`is_leisure` AS `is_leisure`, `transactions`.`created_at` AS `created_at` FROM transactions ORDER BY date_millis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TransactionWithDetails>> getAllTransactionsWithDetails() {
    final String _sql = "SELECT `transactions`.`id` AS `id`, `transactions`.`account_id` AS `account_id`, `transactions`.`category_id` AS `category_id`, `transactions`.`block_id` AS `block_id`, `transactions`.`amount_cents` AS `amount_cents`, `transactions`.`type` AS `type`, `transactions`.`date_millis` AS `date_millis`, `transactions`.`note` AS `note`, `transactions`.`merchant` AS `merchant`, `transactions`.`linked_debt_id` AS `linked_debt_id`, `transactions`.`linked_recurring_item_id` AS `linked_recurring_item_id`, `transactions`.`linked_receivable_id` AS `linked_receivable_id`, `transactions`.`is_leisure` AS `is_leisure`, `transactions`.`created_at` AS `created_at` FROM transactions ORDER BY date_millis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"accounts", "categories", "debts",
        "recurring_items", "transactions"}, new Callable<List<TransactionWithDetails>>() {
      @Override
      @NonNull
      public List<TransactionWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
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
            final LongSparseArray<AccountEntity> _collectionAccount = new LongSparseArray<AccountEntity>();
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            final LongSparseArray<DebtEntity> _collectionLinkedDebt = new LongSparseArray<DebtEntity>();
            final LongSparseArray<RecurringItemEntity> _collectionLinkedRecurringItem = new LongSparseArray<RecurringItemEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfAccountId);
              _collectionAccount.put(_tmpKey, null);
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              _collectionCategory.put(_tmpKey_1, null);
              final Long _tmpKey_2;
              if (_cursor.isNull(_cursorIndexOfLinkedDebtId)) {
                _tmpKey_2 = null;
              } else {
                _tmpKey_2 = _cursor.getLong(_cursorIndexOfLinkedDebtId);
              }
              if (_tmpKey_2 != null) {
                _collectionLinkedDebt.put(_tmpKey_2, null);
              }
              final Long _tmpKey_3;
              if (_cursor.isNull(_cursorIndexOfLinkedRecurringItemId)) {
                _tmpKey_3 = null;
              } else {
                _tmpKey_3 = _cursor.getLong(_cursorIndexOfLinkedRecurringItemId);
              }
              if (_tmpKey_3 != null) {
                _collectionLinkedRecurringItem.put(_tmpKey_3, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipaccountsAscomPersonalfinanceCuentasDataLocalEntitiesAccountEntity(_collectionAccount);
            __fetchRelationshipcategoriesAscomPersonalfinanceCuentasDataLocalEntitiesCategoryEntity(_collectionCategory);
            __fetchRelationshipdebtsAscomPersonalfinanceCuentasDataLocalEntitiesDebtEntity(_collectionLinkedDebt);
            __fetchRelationshiprecurringItemsAscomPersonalfinanceCuentasDataLocalEntitiesRecurringItemEntity(_collectionLinkedRecurringItem);
            final List<TransactionWithDetails> _result = new ArrayList<TransactionWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final TransactionWithDetails _item;
              final TransactionEntity _tmpTransaction;
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
              _tmpTransaction = new TransactionEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpBlockId,_tmpAmountCents,_tmpType,_tmpDateMillis,_tmpNote,_tmpMerchant,_tmpLinkedDebtId,_tmpLinkedRecurringItemId,_tmpLinkedReceivableId,_tmpIsLeisure,_tmpCreatedAt);
              final AccountEntity _tmpAccount;
              final long _tmpKey_4;
              _tmpKey_4 = _cursor.getLong(_cursorIndexOfAccountId);
              _tmpAccount = _collectionAccount.get(_tmpKey_4);
              final CategoryEntity _tmpCategory;
              final long _tmpKey_5;
              _tmpKey_5 = _cursor.getLong(_cursorIndexOfCategoryId);
              _tmpCategory = _collectionCategory.get(_tmpKey_5);
              final DebtEntity _tmpLinkedDebt;
              final Long _tmpKey_6;
              if (_cursor.isNull(_cursorIndexOfLinkedDebtId)) {
                _tmpKey_6 = null;
              } else {
                _tmpKey_6 = _cursor.getLong(_cursorIndexOfLinkedDebtId);
              }
              if (_tmpKey_6 != null) {
                _tmpLinkedDebt = _collectionLinkedDebt.get(_tmpKey_6);
              } else {
                _tmpLinkedDebt = null;
              }
              final RecurringItemEntity _tmpLinkedRecurringItem;
              final Long _tmpKey_7;
              if (_cursor.isNull(_cursorIndexOfLinkedRecurringItemId)) {
                _tmpKey_7 = null;
              } else {
                _tmpKey_7 = _cursor.getLong(_cursorIndexOfLinkedRecurringItemId);
              }
              if (_tmpKey_7 != null) {
                _tmpLinkedRecurringItem = _collectionLinkedRecurringItem.get(_tmpKey_7);
              } else {
                _tmpLinkedRecurringItem = null;
              }
              _item = new TransactionWithDetails(_tmpTransaction,_tmpAccount,_tmpCategory,_tmpLinkedDebt,_tmpLinkedRecurringItem);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TransactionWithDetails>> getRecentTransactionsWithDetails(final int limit) {
    final String _sql = "SELECT * FROM transactions ORDER BY date_millis DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"accounts", "categories", "debts",
        "recurring_items", "transactions"}, new Callable<List<TransactionWithDetails>>() {
      @Override
      @NonNull
      public List<TransactionWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "block_id");
            final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_cents");
            final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
            final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "date_millis");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
            final int _cursorIndexOfLinkedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_debt_id");
            final int _cursorIndexOfLinkedRecurringItemId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_recurring_item_id");
            final int _cursorIndexOfLinkedReceivableId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_receivable_id");
            final int _cursorIndexOfIsLeisure = CursorUtil.getColumnIndexOrThrow(_cursor, "is_leisure");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final LongSparseArray<AccountEntity> _collectionAccount = new LongSparseArray<AccountEntity>();
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            final LongSparseArray<DebtEntity> _collectionLinkedDebt = new LongSparseArray<DebtEntity>();
            final LongSparseArray<RecurringItemEntity> _collectionLinkedRecurringItem = new LongSparseArray<RecurringItemEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfAccountId);
              _collectionAccount.put(_tmpKey, null);
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              _collectionCategory.put(_tmpKey_1, null);
              final Long _tmpKey_2;
              if (_cursor.isNull(_cursorIndexOfLinkedDebtId)) {
                _tmpKey_2 = null;
              } else {
                _tmpKey_2 = _cursor.getLong(_cursorIndexOfLinkedDebtId);
              }
              if (_tmpKey_2 != null) {
                _collectionLinkedDebt.put(_tmpKey_2, null);
              }
              final Long _tmpKey_3;
              if (_cursor.isNull(_cursorIndexOfLinkedRecurringItemId)) {
                _tmpKey_3 = null;
              } else {
                _tmpKey_3 = _cursor.getLong(_cursorIndexOfLinkedRecurringItemId);
              }
              if (_tmpKey_3 != null) {
                _collectionLinkedRecurringItem.put(_tmpKey_3, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipaccountsAscomPersonalfinanceCuentasDataLocalEntitiesAccountEntity(_collectionAccount);
            __fetchRelationshipcategoriesAscomPersonalfinanceCuentasDataLocalEntitiesCategoryEntity(_collectionCategory);
            __fetchRelationshipdebtsAscomPersonalfinanceCuentasDataLocalEntitiesDebtEntity(_collectionLinkedDebt);
            __fetchRelationshiprecurringItemsAscomPersonalfinanceCuentasDataLocalEntitiesRecurringItemEntity(_collectionLinkedRecurringItem);
            final List<TransactionWithDetails> _result = new ArrayList<TransactionWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final TransactionWithDetails _item;
              final TransactionEntity _tmpTransaction;
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
              _tmpTransaction = new TransactionEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpBlockId,_tmpAmountCents,_tmpType,_tmpDateMillis,_tmpNote,_tmpMerchant,_tmpLinkedDebtId,_tmpLinkedRecurringItemId,_tmpLinkedReceivableId,_tmpIsLeisure,_tmpCreatedAt);
              final AccountEntity _tmpAccount;
              final long _tmpKey_4;
              _tmpKey_4 = _cursor.getLong(_cursorIndexOfAccountId);
              _tmpAccount = _collectionAccount.get(_tmpKey_4);
              final CategoryEntity _tmpCategory;
              final long _tmpKey_5;
              _tmpKey_5 = _cursor.getLong(_cursorIndexOfCategoryId);
              _tmpCategory = _collectionCategory.get(_tmpKey_5);
              final DebtEntity _tmpLinkedDebt;
              final Long _tmpKey_6;
              if (_cursor.isNull(_cursorIndexOfLinkedDebtId)) {
                _tmpKey_6 = null;
              } else {
                _tmpKey_6 = _cursor.getLong(_cursorIndexOfLinkedDebtId);
              }
              if (_tmpKey_6 != null) {
                _tmpLinkedDebt = _collectionLinkedDebt.get(_tmpKey_6);
              } else {
                _tmpLinkedDebt = null;
              }
              final RecurringItemEntity _tmpLinkedRecurringItem;
              final Long _tmpKey_7;
              if (_cursor.isNull(_cursorIndexOfLinkedRecurringItemId)) {
                _tmpKey_7 = null;
              } else {
                _tmpKey_7 = _cursor.getLong(_cursorIndexOfLinkedRecurringItemId);
              }
              if (_tmpKey_7 != null) {
                _tmpLinkedRecurringItem = _collectionLinkedRecurringItem.get(_tmpKey_7);
              } else {
                _tmpLinkedRecurringItem = null;
              }
              _item = new TransactionWithDetails(_tmpTransaction,_tmpAccount,_tmpCategory,_tmpLinkedDebt,_tmpLinkedRecurringItem);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TransactionWithDetails>> getFilteredTransactions(final Long accountId,
      final Long categoryId, final String type) {
    final String _sql = "\n"
            + "        SELECT * FROM transactions \n"
            + "        WHERE (? IS NULL OR account_id = ?)\n"
            + "          AND (? IS NULL OR category_id = ?)\n"
            + "          AND (? IS NULL OR type = ?)\n"
            + "        ORDER BY date_millis DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 6);
    int _argIndex = 1;
    if (accountId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, accountId);
    }
    _argIndex = 2;
    if (accountId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, accountId);
    }
    _argIndex = 3;
    if (categoryId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, categoryId);
    }
    _argIndex = 4;
    if (categoryId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, categoryId);
    }
    _argIndex = 5;
    if (type == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, type);
    }
    _argIndex = 6;
    if (type == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, type);
    }
    return CoroutinesRoom.createFlow(__db, true, new String[] {"accounts", "categories", "debts",
        "recurring_items", "transactions"}, new Callable<List<TransactionWithDetails>>() {
      @Override
      @NonNull
      public List<TransactionWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
            final int _cursorIndexOfBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "block_id");
            final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_cents");
            final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
            final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "date_millis");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
            final int _cursorIndexOfLinkedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_debt_id");
            final int _cursorIndexOfLinkedRecurringItemId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_recurring_item_id");
            final int _cursorIndexOfLinkedReceivableId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_receivable_id");
            final int _cursorIndexOfIsLeisure = CursorUtil.getColumnIndexOrThrow(_cursor, "is_leisure");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final LongSparseArray<AccountEntity> _collectionAccount = new LongSparseArray<AccountEntity>();
            final LongSparseArray<CategoryEntity> _collectionCategory = new LongSparseArray<CategoryEntity>();
            final LongSparseArray<DebtEntity> _collectionLinkedDebt = new LongSparseArray<DebtEntity>();
            final LongSparseArray<RecurringItemEntity> _collectionLinkedRecurringItem = new LongSparseArray<RecurringItemEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfAccountId);
              _collectionAccount.put(_tmpKey, null);
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              _collectionCategory.put(_tmpKey_1, null);
              final Long _tmpKey_2;
              if (_cursor.isNull(_cursorIndexOfLinkedDebtId)) {
                _tmpKey_2 = null;
              } else {
                _tmpKey_2 = _cursor.getLong(_cursorIndexOfLinkedDebtId);
              }
              if (_tmpKey_2 != null) {
                _collectionLinkedDebt.put(_tmpKey_2, null);
              }
              final Long _tmpKey_3;
              if (_cursor.isNull(_cursorIndexOfLinkedRecurringItemId)) {
                _tmpKey_3 = null;
              } else {
                _tmpKey_3 = _cursor.getLong(_cursorIndexOfLinkedRecurringItemId);
              }
              if (_tmpKey_3 != null) {
                _collectionLinkedRecurringItem.put(_tmpKey_3, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipaccountsAscomPersonalfinanceCuentasDataLocalEntitiesAccountEntity(_collectionAccount);
            __fetchRelationshipcategoriesAscomPersonalfinanceCuentasDataLocalEntitiesCategoryEntity(_collectionCategory);
            __fetchRelationshipdebtsAscomPersonalfinanceCuentasDataLocalEntitiesDebtEntity(_collectionLinkedDebt);
            __fetchRelationshiprecurringItemsAscomPersonalfinanceCuentasDataLocalEntitiesRecurringItemEntity(_collectionLinkedRecurringItem);
            final List<TransactionWithDetails> _result = new ArrayList<TransactionWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final TransactionWithDetails _item;
              final TransactionEntity _tmpTransaction;
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
              _tmpTransaction = new TransactionEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpBlockId,_tmpAmountCents,_tmpType,_tmpDateMillis,_tmpNote,_tmpMerchant,_tmpLinkedDebtId,_tmpLinkedRecurringItemId,_tmpLinkedReceivableId,_tmpIsLeisure,_tmpCreatedAt);
              final AccountEntity _tmpAccount;
              final long _tmpKey_4;
              _tmpKey_4 = _cursor.getLong(_cursorIndexOfAccountId);
              _tmpAccount = _collectionAccount.get(_tmpKey_4);
              final CategoryEntity _tmpCategory;
              final long _tmpKey_5;
              _tmpKey_5 = _cursor.getLong(_cursorIndexOfCategoryId);
              _tmpCategory = _collectionCategory.get(_tmpKey_5);
              final DebtEntity _tmpLinkedDebt;
              final Long _tmpKey_6;
              if (_cursor.isNull(_cursorIndexOfLinkedDebtId)) {
                _tmpKey_6 = null;
              } else {
                _tmpKey_6 = _cursor.getLong(_cursorIndexOfLinkedDebtId);
              }
              if (_tmpKey_6 != null) {
                _tmpLinkedDebt = _collectionLinkedDebt.get(_tmpKey_6);
              } else {
                _tmpLinkedDebt = null;
              }
              final RecurringItemEntity _tmpLinkedRecurringItem;
              final Long _tmpKey_7;
              if (_cursor.isNull(_cursorIndexOfLinkedRecurringItemId)) {
                _tmpKey_7 = null;
              } else {
                _tmpKey_7 = _cursor.getLong(_cursorIndexOfLinkedRecurringItemId);
              }
              if (_tmpKey_7 != null) {
                _tmpLinkedRecurringItem = _collectionLinkedRecurringItem.get(_tmpKey_7);
              } else {
                _tmpLinkedRecurringItem = null;
              }
              _item = new TransactionWithDetails(_tmpTransaction,_tmpAccount,_tmpCategory,_tmpLinkedDebt,_tmpLinkedRecurringItem);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTransactionById(final long id,
      final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "block_id");
          final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_cents");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "date_millis");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
          final int _cursorIndexOfLinkedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_debt_id");
          final int _cursorIndexOfLinkedRecurringItemId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_recurring_item_id");
          final int _cursorIndexOfLinkedReceivableId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_receivable_id");
          final int _cursorIndexOfIsLeisure = CursorUtil.getColumnIndexOrThrow(_cursor, "is_leisure");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new TransactionEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpBlockId,_tmpAmountCents,_tmpType,_tmpDateMillis,_tmpNote,_tmpMerchant,_tmpLinkedDebtId,_tmpLinkedRecurringItemId,_tmpLinkedReceivableId,_tmpIsLeisure,_tmpCreatedAt);
          } else {
            _result = null;
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
  public Flow<List<TransactionEntity>> getUnanchoredIncomeTransactions(final int limit) {
    final String _sql = "\n"
            + "        SELECT * FROM transactions \n"
            + "        WHERE type = 'INCOME' AND linked_receivable_id IS NULL \n"
            + "        ORDER BY date_millis DESC \n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<List<TransactionEntity>>() {
      @Override
      @NonNull
      public List<TransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "block_id");
          final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_cents");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "date_millis");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
          final int _cursorIndexOfLinkedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_debt_id");
          final int _cursorIndexOfLinkedRecurringItemId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_recurring_item_id");
          final int _cursorIndexOfLinkedReceivableId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_receivable_id");
          final int _cursorIndexOfIsLeisure = CursorUtil.getColumnIndexOrThrow(_cursor, "is_leisure");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object findRecentSimilarTransaction(final long accountId, final long amountCents,
      final TransactionType type, final long sinceMillis,
      final Continuation<? super TransactionEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM transactions \n"
            + "        WHERE account_id = ? \n"
            + "          AND amount_cents = ? \n"
            + "          AND type = ? \n"
            + "          AND date_millis >= ? \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, accountId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, amountCents);
    _argIndex = 3;
    _statement.bindString(_argIndex, __TransactionType_enumToString(type));
    _argIndex = 4;
    _statement.bindLong(_argIndex, sinceMillis);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TransactionEntity>() {
      @Override
      @Nullable
      public TransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "block_id");
          final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_cents");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "date_millis");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
          final int _cursorIndexOfLinkedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_debt_id");
          final int _cursorIndexOfLinkedRecurringItemId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_recurring_item_id");
          final int _cursorIndexOfLinkedReceivableId = CursorUtil.getColumnIndexOrThrow(_cursor, "linked_receivable_id");
          final int _cursorIndexOfIsLeisure = CursorUtil.getColumnIndexOrThrow(_cursor, "is_leisure");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final TransactionEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new TransactionEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpBlockId,_tmpAmountCents,_tmpType,_tmpDateMillis,_tmpNote,_tmpMerchant,_tmpLinkedDebtId,_tmpLinkedRecurringItemId,_tmpLinkedReceivableId,_tmpIsLeisure,_tmpCreatedAt);
          } else {
            _result = null;
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
  public Flow<Long> getTotalExpensesBetween(final long startMillis, final long endMillis) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(amount_cents), 0) FROM transactions \n"
            + "        WHERE type = 'EXPENSE' AND date_millis BETWEEN ? AND ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Long> getTotalSpentInBlockBetween(final long blockId, final long startMillis,
      final long endMillis) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(amount_cents), 0) FROM transactions \n"
            + "        WHERE block_id = ? AND type = 'EXPENSE' AND date_millis BETWEEN ? AND ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, blockId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startMillis);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTransactionCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM transactions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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

  private void __fetchRelationshipaccountsAscomPersonalfinanceCuentasDataLocalEntitiesAccountEntity(
      @NonNull final LongSparseArray<AccountEntity> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipaccountsAscomPersonalfinanceCuentasDataLocalEntitiesAccountEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`name`,`type`,`current_balance_cents`,`color_hex`,`icon_name`,`is_archived`,`include_in_total`,`is_exempt_from_gmf`,`annual_yield_rate`,`is_static_saving`,`created_at` FROM `accounts` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
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
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final AccountEntity _item_1;
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
          _item_1 = new AccountEntity(_tmpId,_tmpName,_tmpType,_tmpCurrentBalanceCents,_tmpColorHex,_tmpIconName,_tmpIsArchived,_tmpIncludeInTotal,_tmpIsExemptFromGmf,_tmpAnnualYieldRate,_tmpIsStaticSaving,_tmpCreatedAt);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
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

  private void __fetchRelationshipcategoriesAscomPersonalfinanceCuentasDataLocalEntitiesCategoryEntity(
      @NonNull final LongSparseArray<CategoryEntity> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipcategoriesAscomPersonalfinanceCuentasDataLocalEntitiesCategoryEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`name`,`icon_name`,`color_hex`,`default_block_type`,`is_system` FROM `categories` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfName = 1;
      final int _cursorIndexOfIconName = 2;
      final int _cursorIndexOfColorHex = 3;
      final int _cursorIndexOfDefaultBlockType = 4;
      final int _cursorIndexOfIsSystem = 5;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final CategoryEntity _item_1;
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
          _item_1 = new CategoryEntity(_tmpId,_tmpName,_tmpIconName,_tmpColorHex,_tmpDefaultBlockType,_tmpIsSystem);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }

  private void __fetchRelationshipdebtsAscomPersonalfinanceCuentasDataLocalEntitiesDebtEntity(
      @NonNull final LongSparseArray<DebtEntity> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipdebtsAscomPersonalfinanceCuentasDataLocalEntitiesDebtEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`name`,`principal_amount_cents`,`current_balance_cents`,`interest_rate_ea`,`monthly_installment_cents`,`due_day_of_month`,`start_date_millis`,`next_due_date_millis`,`monthly_insurance_cents`,`is_settled` FROM `debts` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
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
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final DebtEntity _item_1;
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
          _item_1 = new DebtEntity(_tmpId,_tmpName,_tmpPrincipalAmountCents,_tmpCurrentBalanceCents,_tmpInterestRateEa,_tmpMonthlyInstallmentCents,_tmpDueDayOfMonth,_tmpStartDateMillis,_tmpNextDueDateMillis,_tmpMonthlyInsuranceCents,_tmpIsSettled);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
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

  private void __fetchRelationshiprecurringItemsAscomPersonalfinanceCuentasDataLocalEntitiesRecurringItemEntity(
      @NonNull final LongSparseArray<RecurringItemEntity> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshiprecurringItemsAscomPersonalfinanceCuentasDataLocalEntitiesRecurringItemEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`account_id`,`category_id`,`block_id`,`title`,`amount_cents`,`frequency`,`next_due_date_millis`,`notify_days_before`,`is_active` FROM `recurring_items` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
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
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final RecurringItemEntity _item_1;
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
          _item_1 = new RecurringItemEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpBlockId,_tmpTitle,_tmpAmountCents,_tmpFrequency,_tmpNextDueDateMillis,_tmpNotifyDaysBefore,_tmpIsActive);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
