package com.personalfinance.cuentas.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.personalfinance.cuentas.data.local.entities.ReceivableEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class ReceivableDao_Impl implements ReceivableDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReceivableEntity> __insertionAdapterOfReceivableEntity;

  private final EntityDeletionOrUpdateAdapter<ReceivableEntity> __deletionAdapterOfReceivableEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkSettled;

  public ReceivableDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
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
    this.__deletionAdapterOfReceivableEntity = new EntityDeletionOrUpdateAdapter<ReceivableEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `receivables` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceivableEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfMarkSettled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE receivables\n"
                + "        SET is_settled = 1,\n"
                + "            settled_at_millis = ?,\n"
                + "            settled_account_id = ?,\n"
                + "            settled_transaction_id = ?\n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object insertReceivable(final ReceivableEntity receivable,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfReceivableEntity.insertAndReturnId(receivable);
          __db.setTransactionSuccessful();
          return _result;
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
  public Object deleteReceivable(final ReceivableEntity receivable,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfReceivableEntity.handle(receivable);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markSettled(final long id, final long settledAtMillis, final long accountId,
      final Long transactionId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSettled.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, settledAtMillis);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 3;
        if (transactionId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, transactionId);
        }
        _argIndex = 4;
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
          __preparedStmtOfMarkSettled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ReceivableEntity>> getPendingReceivables() {
    final String _sql = "SELECT `receivables`.`id` AS `id`, `receivables`.`contact_id` AS `contact_id`, `receivables`.`source_transaction_id` AS `source_transaction_id`, `receivables`.`note` AS `note`, `receivables`.`owed_cents` AS `owed_cents`, `receivables`.`is_settled` AS `is_settled`, `receivables`.`settled_at_millis` AS `settled_at_millis`, `receivables`.`settled_account_id` AS `settled_account_id`, `receivables`.`settled_transaction_id` AS `settled_transaction_id`, `receivables`.`created_at` AS `created_at` FROM receivables WHERE is_settled = 0 ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receivables"}, new Callable<List<ReceivableEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ReceivableEntity>> getSettledReceivables() {
    final String _sql = "SELECT `receivables`.`id` AS `id`, `receivables`.`contact_id` AS `contact_id`, `receivables`.`source_transaction_id` AS `source_transaction_id`, `receivables`.`note` AS `note`, `receivables`.`owed_cents` AS `owed_cents`, `receivables`.`is_settled` AS `is_settled`, `receivables`.`settled_at_millis` AS `settled_at_millis`, `receivables`.`settled_account_id` AS `settled_account_id`, `receivables`.`settled_transaction_id` AS `settled_transaction_id`, `receivables`.`created_at` AS `created_at` FROM receivables WHERE is_settled = 1 ORDER BY settled_at_millis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receivables"}, new Callable<List<ReceivableEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ReceivableEntity>> getAllReceivables() {
    final String _sql = "SELECT `receivables`.`id` AS `id`, `receivables`.`contact_id` AS `contact_id`, `receivables`.`source_transaction_id` AS `source_transaction_id`, `receivables`.`note` AS `note`, `receivables`.`owed_cents` AS `owed_cents`, `receivables`.`is_settled` AS `is_settled`, `receivables`.`settled_at_millis` AS `settled_at_millis`, `receivables`.`settled_account_id` AS `settled_account_id`, `receivables`.`settled_transaction_id` AS `settled_transaction_id`, `receivables`.`created_at` AS `created_at` FROM receivables ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"receivables"}, new Callable<List<ReceivableEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getReceivablesForTransaction(final long transactionId,
      final Continuation<? super List<ReceivableEntity>> $completion) {
    final String _sql = "SELECT * FROM receivables WHERE source_transaction_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, transactionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReceivableEntity>>() {
      @Override
      @NonNull
      public List<ReceivableEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contact_id");
          final int _cursorIndexOfSourceTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "source_transaction_id");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfOwedCents = CursorUtil.getColumnIndexOrThrow(_cursor, "owed_cents");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_at_millis");
          final int _cursorIndexOfSettledAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_account_id");
          final int _cursorIndexOfSettledTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_transaction_id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
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
}
