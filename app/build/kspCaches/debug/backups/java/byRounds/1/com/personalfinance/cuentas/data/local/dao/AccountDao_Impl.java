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
import com.personalfinance.cuentas.data.local.entities.AccountEntity;
import com.personalfinance.cuentas.data.local.entities.AccountType;
import java.lang.Class;
import java.lang.Double;
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
public final class AccountDao_Impl implements AccountDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AccountEntity> __insertionAdapterOfAccountEntity;

  private final EntityDeletionOrUpdateAdapter<AccountEntity> __deletionAdapterOfAccountEntity;

  private final EntityDeletionOrUpdateAdapter<AccountEntity> __updateAdapterOfAccountEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBalance;

  private final SharedSQLiteStatement __preparedStmtOfUpdateIncludeInTotal;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStaticSaving;

  private final SharedSQLiteStatement __preparedStmtOfUpdateExemptFromGmf;

  public AccountDao_Impl(@NonNull final RoomDatabase __db) {
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
    this.__deletionAdapterOfAccountEntity = new EntityDeletionOrUpdateAdapter<AccountEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `accounts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AccountEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAccountEntity = new EntityDeletionOrUpdateAdapter<AccountEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `accounts` SET `id` = ?,`name` = ?,`type` = ?,`current_balance_cents` = ?,`color_hex` = ?,`icon_name` = ?,`is_archived` = ?,`include_in_total` = ?,`is_exempt_from_gmf` = ?,`annual_yield_rate` = ?,`is_static_saving` = ?,`created_at` = ? WHERE `id` = ?";
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
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateBalance = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE accounts SET current_balance_cents = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateIncludeInTotal = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE accounts SET include_in_total = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateStaticSaving = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE accounts SET is_static_saving = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateExemptFromGmf = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE accounts SET is_exempt_from_gmf = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAccount(final AccountEntity account,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAccountEntity.insertAndReturnId(account);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAccount(final AccountEntity account,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAccountEntity.handle(account);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAccount(final AccountEntity account,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAccountEntity.handle(account);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBalance(final long id, final long newBalanceCents,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBalance.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, newBalanceCents);
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
          __preparedStmtOfUpdateBalance.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateIncludeInTotal(final long id, final boolean include,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateIncludeInTotal.acquire();
        int _argIndex = 1;
        final int _tmp = include ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfUpdateIncludeInTotal.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStaticSaving(final long id, final boolean isStatic,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStaticSaving.acquire();
        int _argIndex = 1;
        final int _tmp = isStatic ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfUpdateStaticSaving.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateExemptFromGmf(final long id, final boolean isExempt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateExemptFromGmf.acquire();
        int _argIndex = 1;
        final int _tmp = isExempt ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfUpdateExemptFromGmf.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AccountEntity>> getAllActiveAccounts() {
    final String _sql = "SELECT `accounts`.`id` AS `id`, `accounts`.`name` AS `name`, `accounts`.`type` AS `type`, `accounts`.`current_balance_cents` AS `current_balance_cents`, `accounts`.`color_hex` AS `color_hex`, `accounts`.`icon_name` AS `icon_name`, `accounts`.`is_archived` AS `is_archived`, `accounts`.`include_in_total` AS `include_in_total`, `accounts`.`is_exempt_from_gmf` AS `is_exempt_from_gmf`, `accounts`.`annual_yield_rate` AS `annual_yield_rate`, `accounts`.`is_static_saving` AS `is_static_saving`, `accounts`.`created_at` AS `created_at` FROM accounts WHERE is_archived = 0 ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<List<AccountEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllActiveAccountsSync(
      final Continuation<? super List<AccountEntity>> $completion) {
    final String _sql = "SELECT `accounts`.`id` AS `id`, `accounts`.`name` AS `name`, `accounts`.`type` AS `type`, `accounts`.`current_balance_cents` AS `current_balance_cents`, `accounts`.`color_hex` AS `color_hex`, `accounts`.`icon_name` AS `icon_name`, `accounts`.`is_archived` AS `is_archived`, `accounts`.`include_in_total` AS `include_in_total`, `accounts`.`is_exempt_from_gmf` AS `is_exempt_from_gmf`, `accounts`.`annual_yield_rate` AS `annual_yield_rate`, `accounts`.`is_static_saving` AS `is_static_saving`, `accounts`.`created_at` AS `created_at` FROM accounts WHERE is_archived = 0 ORDER BY id ASC";
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
  public Flow<AccountEntity> getAccountById(final long id) {
    final String _sql = "SELECT * FROM accounts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<AccountEntity>() {
      @Override
      @Nullable
      public AccountEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfCurrentBalanceCents = CursorUtil.getColumnIndexOrThrow(_cursor, "current_balance_cents");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_name");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIncludeInTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "include_in_total");
          final int _cursorIndexOfIsExemptFromGmf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_exempt_from_gmf");
          final int _cursorIndexOfAnnualYieldRate = CursorUtil.getColumnIndexOrThrow(_cursor, "annual_yield_rate");
          final int _cursorIndexOfIsStaticSaving = CursorUtil.getColumnIndexOrThrow(_cursor, "is_static_saving");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final AccountEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new AccountEntity(_tmpId,_tmpName,_tmpType,_tmpCurrentBalanceCents,_tmpColorHex,_tmpIconName,_tmpIsArchived,_tmpIncludeInTotal,_tmpIsExemptFromGmf,_tmpAnnualYieldRate,_tmpIsStaticSaving,_tmpCreatedAt);
          } else {
            _result = null;
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
  public Object getAccountByIdSync(final long id,
      final Continuation<? super AccountEntity> $completion) {
    final String _sql = "SELECT * FROM accounts WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AccountEntity>() {
      @Override
      @Nullable
      public AccountEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfCurrentBalanceCents = CursorUtil.getColumnIndexOrThrow(_cursor, "current_balance_cents");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_name");
          final int _cursorIndexOfIsArchived = CursorUtil.getColumnIndexOrThrow(_cursor, "is_archived");
          final int _cursorIndexOfIncludeInTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "include_in_total");
          final int _cursorIndexOfIsExemptFromGmf = CursorUtil.getColumnIndexOrThrow(_cursor, "is_exempt_from_gmf");
          final int _cursorIndexOfAnnualYieldRate = CursorUtil.getColumnIndexOrThrow(_cursor, "annual_yield_rate");
          final int _cursorIndexOfIsStaticSaving = CursorUtil.getColumnIndexOrThrow(_cursor, "is_static_saving");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final AccountEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new AccountEntity(_tmpId,_tmpName,_tmpType,_tmpCurrentBalanceCents,_tmpColorHex,_tmpIconName,_tmpIsArchived,_tmpIncludeInTotal,_tmpIsExemptFromGmf,_tmpAnnualYieldRate,_tmpIsStaticSaving,_tmpCreatedAt);
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
  public Flow<Long> getTotalConsolidatedBalanceCents() {
    final String _sql = "SELECT COALESCE(SUM(current_balance_cents), 0) FROM accounts WHERE is_archived = 0 AND include_in_total = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<Long>() {
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
  public Flow<Long> getSpendableBalanceCents() {
    final String _sql = "SELECT COALESCE(SUM(current_balance_cents), 0) FROM accounts WHERE is_archived = 0 AND include_in_total = 1 AND is_static_saving = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<Long>() {
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
  public Flow<Long> getStaticSavingsBalanceCents() {
    final String _sql = "SELECT COALESCE(SUM(current_balance_cents), 0) FROM accounts WHERE is_archived = 0 AND include_in_total = 1 AND is_static_saving = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<Long>() {
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
  public Object getAccountCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM accounts";
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

  @Override
  public Flow<Long> getBalanceCents(final long accountId) {
    final String _sql = "SELECT current_balance_cents FROM accounts WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, accountId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final long _result;
          if (_cursor.moveToFirst()) {
            _result = _cursor.getLong(0);
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
}
