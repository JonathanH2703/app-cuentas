package com.personalfinance.cuentas.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.personalfinance.cuentas.data.local.entities.PendingSource;
import com.personalfinance.cuentas.data.local.entities.PendingStatus;
import com.personalfinance.cuentas.data.local.entities.PendingTransactionEntity;
import com.personalfinance.cuentas.data.local.entities.TransactionType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PendingTransactionDao_Impl implements PendingTransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PendingTransactionEntity> __insertionAdapterOfPendingTransactionEntity;

  private final EntityDeletionOrUpdateAdapter<PendingTransactionEntity> __updateAdapterOfPendingTransactionEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSimilarCapture;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByDeduplicationHash;

  public PendingTransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
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
    this.__updateAdapterOfPendingTransactionEntity = new EntityDeletionOrUpdateAdapter<PendingTransactionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `pending_transactions` SET `id` = ?,`source` = ?,`source_sender` = ?,`raw_text` = ?,`deduplication_hash` = ?,`suggested_amount_cents` = ?,`suggested_merchant` = ?,`suggested_account_id` = ?,`suggested_category_id` = ?,`suggested_block_id` = ?,`suggested_type` = ?,`suggested_card_id` = ?,`suggested_debt_id` = ?,`detected_date_millis` = ?,`status` = ? WHERE `id` = ?";
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
        statement.bindLong(16, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE pending_transactions SET status = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_transactions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteSimilarCapture = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_transactions WHERE suggested_amount_cents = ? AND (suggested_account_id = ? OR ? = 0)";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByDeduplicationHash = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_transactions WHERE deduplication_hash = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PendingTransactionEntity pendingTransaction,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPendingTransactionEntity.insertAndReturnId(pendingTransaction);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final PendingTransactionEntity pendingTransaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPendingTransactionEntity.handle(pendingTransaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final long id, final PendingStatus status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, __PendingStatus_enumToString(status));
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSimilarCapture(final long amountCents, final long accountId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSimilarCapture.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, amountCents);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, accountId);
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
          __preparedStmtOfDeleteSimilarCapture.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByDeduplicationHash(final String hash,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByDeduplicationHash.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, hash);
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
          __preparedStmtOfDeleteByDeduplicationHash.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PendingTransactionEntity>> getTransactionsByStatus(final PendingStatus status) {
    final String _sql = "SELECT * FROM pending_transactions WHERE status = ? ORDER BY detected_date_millis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, __PendingStatus_enumToString(status));
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pending_transactions"}, new Callable<List<PendingTransactionEntity>>() {
      @Override
      @NonNull
      public List<PendingTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceSender = CursorUtil.getColumnIndexOrThrow(_cursor, "source_sender");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_text");
          final int _cursorIndexOfDeduplicationHash = CursorUtil.getColumnIndexOrThrow(_cursor, "deduplication_hash");
          final int _cursorIndexOfSuggestedAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_amount_cents");
          final int _cursorIndexOfSuggestedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_merchant");
          final int _cursorIndexOfSuggestedAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_account_id");
          final int _cursorIndexOfSuggestedCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_category_id");
          final int _cursorIndexOfSuggestedBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_block_id");
          final int _cursorIndexOfSuggestedType = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_type");
          final int _cursorIndexOfSuggestedCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_card_id");
          final int _cursorIndexOfSuggestedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_debt_id");
          final int _cursorIndexOfDetectedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "detected_date_millis");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PendingTransactionEntity>> getRecentCaptures(final int limit) {
    final String _sql = "SELECT * FROM pending_transactions ORDER BY detected_date_millis DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pending_transactions"}, new Callable<List<PendingTransactionEntity>>() {
      @Override
      @NonNull
      public List<PendingTransactionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceSender = CursorUtil.getColumnIndexOrThrow(_cursor, "source_sender");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_text");
          final int _cursorIndexOfDeduplicationHash = CursorUtil.getColumnIndexOrThrow(_cursor, "deduplication_hash");
          final int _cursorIndexOfSuggestedAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_amount_cents");
          final int _cursorIndexOfSuggestedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_merchant");
          final int _cursorIndexOfSuggestedAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_account_id");
          final int _cursorIndexOfSuggestedCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_category_id");
          final int _cursorIndexOfSuggestedBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_block_id");
          final int _cursorIndexOfSuggestedType = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_type");
          final int _cursorIndexOfSuggestedCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_card_id");
          final int _cursorIndexOfSuggestedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_debt_id");
          final int _cursorIndexOfDetectedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "detected_date_millis");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getPendingCount() {
    final String _sql = "SELECT COUNT(*) FROM pending_transactions WHERE status = 'PENDING'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pending_transactions"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PendingTransactionEntity>> getPendingObligationPayments() {
    final String _sql = "\n"
            + "        SELECT `pending_transactions`.`id` AS `id`, `pending_transactions`.`source` AS `source`, `pending_transactions`.`source_sender` AS `source_sender`, `pending_transactions`.`raw_text` AS `raw_text`, `pending_transactions`.`deduplication_hash` AS `deduplication_hash`, `pending_transactions`.`suggested_amount_cents` AS `suggested_amount_cents`, `pending_transactions`.`suggested_merchant` AS `suggested_merchant`, `pending_transactions`.`suggested_account_id` AS `suggested_account_id`, `pending_transactions`.`suggested_category_id` AS `suggested_category_id`, `pending_transactions`.`suggested_block_id` AS `suggested_block_id`, `pending_transactions`.`suggested_type` AS `suggested_type`, `pending_transactions`.`suggested_card_id` AS `suggested_card_id`, `pending_transactions`.`suggested_debt_id` AS `suggested_debt_id`, `pending_transactions`.`detected_date_millis` AS `detected_date_millis`, `pending_transactions`.`status` AS `status` FROM pending_transactions \n"
            + "        WHERE status = 'PENDING' \n"
            + "          AND (suggested_card_id IS NOT NULL OR suggested_debt_id IS NOT NULL)\n"
            + "        ORDER BY detected_date_millis DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pending_transactions"}, new Callable<List<PendingTransactionEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object findByDeduplicationHash(final String hash,
      final Continuation<? super PendingTransactionEntity> $completion) {
    final String _sql = "SELECT * FROM pending_transactions WHERE deduplication_hash = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, hash);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PendingTransactionEntity>() {
      @Override
      @Nullable
      public PendingTransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceSender = CursorUtil.getColumnIndexOrThrow(_cursor, "source_sender");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_text");
          final int _cursorIndexOfDeduplicationHash = CursorUtil.getColumnIndexOrThrow(_cursor, "deduplication_hash");
          final int _cursorIndexOfSuggestedAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_amount_cents");
          final int _cursorIndexOfSuggestedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_merchant");
          final int _cursorIndexOfSuggestedAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_account_id");
          final int _cursorIndexOfSuggestedCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_category_id");
          final int _cursorIndexOfSuggestedBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_block_id");
          final int _cursorIndexOfSuggestedType = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_type");
          final int _cursorIndexOfSuggestedCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_card_id");
          final int _cursorIndexOfSuggestedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_debt_id");
          final int _cursorIndexOfDetectedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "detected_date_millis");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final PendingTransactionEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new PendingTransactionEntity(_tmpId,_tmpSource,_tmpSourceSender,_tmpRawText,_tmpDeduplicationHash,_tmpSuggestedAmountCents,_tmpSuggestedMerchant,_tmpSuggestedAccountId,_tmpSuggestedCategoryId,_tmpSuggestedBlockId,_tmpSuggestedType,_tmpSuggestedCardId,_tmpSuggestedDebtId,_tmpDetectedDateMillis,_tmpStatus);
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
  public Object findRecentByHash(final String hash, final long sinceMillis,
      final Continuation<? super PendingTransactionEntity> $completion) {
    final String _sql = "SELECT * FROM pending_transactions WHERE deduplication_hash = ? AND detected_date_millis >= ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, hash);
    _argIndex = 2;
    _statement.bindLong(_argIndex, sinceMillis);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PendingTransactionEntity>() {
      @Override
      @Nullable
      public PendingTransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceSender = CursorUtil.getColumnIndexOrThrow(_cursor, "source_sender");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_text");
          final int _cursorIndexOfDeduplicationHash = CursorUtil.getColumnIndexOrThrow(_cursor, "deduplication_hash");
          final int _cursorIndexOfSuggestedAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_amount_cents");
          final int _cursorIndexOfSuggestedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_merchant");
          final int _cursorIndexOfSuggestedAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_account_id");
          final int _cursorIndexOfSuggestedCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_category_id");
          final int _cursorIndexOfSuggestedBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_block_id");
          final int _cursorIndexOfSuggestedType = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_type");
          final int _cursorIndexOfSuggestedCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_card_id");
          final int _cursorIndexOfSuggestedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_debt_id");
          final int _cursorIndexOfDetectedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "detected_date_millis");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final PendingTransactionEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new PendingTransactionEntity(_tmpId,_tmpSource,_tmpSourceSender,_tmpRawText,_tmpDeduplicationHash,_tmpSuggestedAmountCents,_tmpSuggestedMerchant,_tmpSuggestedAccountId,_tmpSuggestedCategoryId,_tmpSuggestedBlockId,_tmpSuggestedType,_tmpSuggestedCardId,_tmpSuggestedDebtId,_tmpDetectedDateMillis,_tmpStatus);
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
  public Object findRecentSimilarCapture(final long amountCents, final String sender,
      final long sinceMillis, final Continuation<? super PendingTransactionEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM pending_transactions \n"
            + "        WHERE suggested_amount_cents = ? \n"
            + "          AND (source_sender = ? OR source_sender LIKE '%' || ? || '%')\n"
            + "          AND detected_date_millis >= ? \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, amountCents);
    _argIndex = 2;
    _statement.bindString(_argIndex, sender);
    _argIndex = 3;
    _statement.bindString(_argIndex, sender);
    _argIndex = 4;
    _statement.bindLong(_argIndex, sinceMillis);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PendingTransactionEntity>() {
      @Override
      @Nullable
      public PendingTransactionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceSender = CursorUtil.getColumnIndexOrThrow(_cursor, "source_sender");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_text");
          final int _cursorIndexOfDeduplicationHash = CursorUtil.getColumnIndexOrThrow(_cursor, "deduplication_hash");
          final int _cursorIndexOfSuggestedAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_amount_cents");
          final int _cursorIndexOfSuggestedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_merchant");
          final int _cursorIndexOfSuggestedAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_account_id");
          final int _cursorIndexOfSuggestedCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_category_id");
          final int _cursorIndexOfSuggestedBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_block_id");
          final int _cursorIndexOfSuggestedType = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_type");
          final int _cursorIndexOfSuggestedCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_card_id");
          final int _cursorIndexOfSuggestedDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "suggested_debt_id");
          final int _cursorIndexOfDetectedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "detected_date_millis");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final PendingTransactionEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new PendingTransactionEntity(_tmpId,_tmpSource,_tmpSourceSender,_tmpRawText,_tmpDeduplicationHash,_tmpSuggestedAmountCents,_tmpSuggestedMerchant,_tmpSuggestedAccountId,_tmpSuggestedCategoryId,_tmpSuggestedBlockId,_tmpSuggestedType,_tmpSuggestedCardId,_tmpSuggestedDebtId,_tmpDetectedDateMillis,_tmpStatus);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __PendingSource_enumToString(@NonNull final PendingSource _value) {
    switch (_value) {
      case NOTIFICATION: return "NOTIFICATION";
      case SMS: return "SMS";
      case EMAIL_NOTIFICATION: return "EMAIL_NOTIFICATION";
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

  private String __PendingStatus_enumToString(@NonNull final PendingStatus _value) {
    switch (_value) {
      case PENDING: return "PENDING";
      case CONFIRMED: return "CONFIRMED";
      case DISCARDED: return "DISCARDED";
      case AUTO_CONFIRMED: return "AUTO_CONFIRMED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
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

  private PendingStatus __PendingStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "PENDING": return PendingStatus.PENDING;
      case "CONFIRMED": return PendingStatus.CONFIRMED;
      case "DISCARDED": return PendingStatus.DISCARDED;
      case "AUTO_CONFIRMED": return PendingStatus.AUTO_CONFIRMED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
