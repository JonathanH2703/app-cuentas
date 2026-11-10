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
import com.personalfinance.cuentas.data.local.entities.IncomeEntity;
import com.personalfinance.cuentas.data.local.entities.IncomeFrequency;
import java.lang.Class;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IncomeDao_Impl implements IncomeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IncomeEntity> __insertionAdapterOfIncomeEntity;

  private final EntityDeletionOrUpdateAdapter<IncomeEntity> __deletionAdapterOfIncomeEntity;

  private final EntityDeletionOrUpdateAdapter<IncomeEntity> __updateAdapterOfIncomeEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsReceived;

  public IncomeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
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
    this.__deletionAdapterOfIncomeEntity = new EntityDeletionOrUpdateAdapter<IncomeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `incomes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncomeEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfIncomeEntity = new EntityDeletionOrUpdateAdapter<IncomeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `incomes` SET `id` = ?,`account_id` = ?,`description` = ?,`amount_cents` = ?,`frequency` = ?,`expected_date_millis` = ?,`received_date_millis` = ?,`is_received` = ?,`created_at` = ? WHERE `id` = ?";
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
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfMarkAsReceived = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE incomes SET is_received = 1, received_date_millis = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertIncome(final IncomeEntity income,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIncomeEntity.insertAndReturnId(income);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteIncome(final IncomeEntity income,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIncomeEntity.handle(income);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateIncome(final IncomeEntity income,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIncomeEntity.handle(income);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsReceived(final long id, final long receivedMillis,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsReceived.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, receivedMillis);
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
          __preparedStmtOfMarkAsReceived.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<IncomeEntity>> getAllIncomes() {
    final String _sql = "SELECT `incomes`.`id` AS `id`, `incomes`.`account_id` AS `account_id`, `incomes`.`description` AS `description`, `incomes`.`amount_cents` AS `amount_cents`, `incomes`.`frequency` AS `frequency`, `incomes`.`expected_date_millis` AS `expected_date_millis`, `incomes`.`received_date_millis` AS `received_date_millis`, `incomes`.`is_received` AS `is_received`, `incomes`.`created_at` AS `created_at` FROM incomes ORDER BY expected_date_millis ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incomes"}, new Callable<List<IncomeEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<IncomeEntity>> getPendingIncomes() {
    final String _sql = "SELECT `incomes`.`id` AS `id`, `incomes`.`account_id` AS `account_id`, `incomes`.`description` AS `description`, `incomes`.`amount_cents` AS `amount_cents`, `incomes`.`frequency` AS `frequency`, `incomes`.`expected_date_millis` AS `expected_date_millis`, `incomes`.`received_date_millis` AS `received_date_millis`, `incomes`.`is_received` AS `is_received`, `incomes`.`created_at` AS `created_at` FROM incomes WHERE is_received = 0 ORDER BY expected_date_millis ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incomes"}, new Callable<List<IncomeEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<IncomeEntity>> getReceivedIncomes() {
    final String _sql = "SELECT `incomes`.`id` AS `id`, `incomes`.`account_id` AS `account_id`, `incomes`.`description` AS `description`, `incomes`.`amount_cents` AS `amount_cents`, `incomes`.`frequency` AS `frequency`, `incomes`.`expected_date_millis` AS `expected_date_millis`, `incomes`.`received_date_millis` AS `received_date_millis`, `incomes`.`is_received` AS `is_received`, `incomes`.`created_at` AS `created_at` FROM incomes WHERE is_received = 1 ORDER BY received_date_millis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incomes"}, new Callable<List<IncomeEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getIncomeById(final long id, final Continuation<? super IncomeEntity> $completion) {
    final String _sql = "SELECT * FROM incomes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncomeEntity>() {
      @Override
      @Nullable
      public IncomeEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_cents");
          final int _cursorIndexOfFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "frequency");
          final int _cursorIndexOfExpectedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "expected_date_millis");
          final int _cursorIndexOfReceivedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "received_date_millis");
          final int _cursorIndexOfIsReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_received");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final IncomeEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new IncomeEntity(_tmpId,_tmpAccountId,_tmpDescription,_tmpAmountCents,_tmpFrequency,_tmpExpectedDateMillis,_tmpReceivedDateMillis,_tmpIsReceived,_tmpCreatedAt);
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
  public Flow<Long> getTotalPendingIncomesCents() {
    final String _sql = "SELECT COALESCE(SUM(amount_cents), 0) FROM incomes WHERE is_received = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incomes"}, new Callable<Long>() {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
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
}
