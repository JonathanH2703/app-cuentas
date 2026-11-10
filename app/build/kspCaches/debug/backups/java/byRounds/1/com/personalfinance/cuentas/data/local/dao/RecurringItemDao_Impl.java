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
import com.personalfinance.cuentas.data.local.entities.IncomeFrequency;
import com.personalfinance.cuentas.data.local.entities.RecurringItemEntity;
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
public final class RecurringItemDao_Impl implements RecurringItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RecurringItemEntity> __insertionAdapterOfRecurringItemEntity;

  private final EntityDeletionOrUpdateAdapter<RecurringItemEntity> __deletionAdapterOfRecurringItemEntity;

  private final EntityDeletionOrUpdateAdapter<RecurringItemEntity> __updateAdapterOfRecurringItemEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateNextDueDate;

  public RecurringItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
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
    this.__deletionAdapterOfRecurringItemEntity = new EntityDeletionOrUpdateAdapter<RecurringItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `recurring_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecurringItemEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfRecurringItemEntity = new EntityDeletionOrUpdateAdapter<RecurringItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `recurring_items` SET `id` = ?,`account_id` = ?,`category_id` = ?,`block_id` = ?,`title` = ?,`amount_cents` = ?,`frequency` = ?,`next_due_date_millis` = ?,`notify_days_before` = ?,`is_active` = ? WHERE `id` = ?";
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
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateNextDueDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE recurring_items SET next_due_date_millis = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRecurringItem(final RecurringItemEntity item,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRecurringItemEntity.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRecurringItem(final RecurringItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRecurringItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRecurringItem(final RecurringItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRecurringItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateNextDueDate(final long id, final long nextDueDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateNextDueDate.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, nextDueDate);
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
          __preparedStmtOfUpdateNextDueDate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RecurringItemEntity>> getAllActiveRecurringItems() {
    final String _sql = "SELECT `recurring_items`.`id` AS `id`, `recurring_items`.`account_id` AS `account_id`, `recurring_items`.`category_id` AS `category_id`, `recurring_items`.`block_id` AS `block_id`, `recurring_items`.`title` AS `title`, `recurring_items`.`amount_cents` AS `amount_cents`, `recurring_items`.`frequency` AS `frequency`, `recurring_items`.`next_due_date_millis` AS `next_due_date_millis`, `recurring_items`.`notify_days_before` AS `notify_days_before`, `recurring_items`.`is_active` AS `is_active` FROM recurring_items WHERE is_active = 1 ORDER BY next_due_date_millis ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"recurring_items"}, new Callable<List<RecurringItemEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllActiveRecurringItemsSync(
      final Continuation<? super List<RecurringItemEntity>> $completion) {
    final String _sql = "SELECT `recurring_items`.`id` AS `id`, `recurring_items`.`account_id` AS `account_id`, `recurring_items`.`category_id` AS `category_id`, `recurring_items`.`block_id` AS `block_id`, `recurring_items`.`title` AS `title`, `recurring_items`.`amount_cents` AS `amount_cents`, `recurring_items`.`frequency` AS `frequency`, `recurring_items`.`next_due_date_millis` AS `next_due_date_millis`, `recurring_items`.`notify_days_before` AS `notify_days_before`, `recurring_items`.`is_active` AS `is_active` FROM recurring_items WHERE is_active = 1 ORDER BY next_due_date_millis ASC";
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
  public Flow<List<RecurringItemEntity>> getAllRecurringItems() {
    final String _sql = "SELECT `recurring_items`.`id` AS `id`, `recurring_items`.`account_id` AS `account_id`, `recurring_items`.`category_id` AS `category_id`, `recurring_items`.`block_id` AS `block_id`, `recurring_items`.`title` AS `title`, `recurring_items`.`amount_cents` AS `amount_cents`, `recurring_items`.`frequency` AS `frequency`, `recurring_items`.`next_due_date_millis` AS `next_due_date_millis`, `recurring_items`.`notify_days_before` AS `notify_days_before`, `recurring_items`.`is_active` AS `is_active` FROM recurring_items ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"recurring_items"}, new Callable<List<RecurringItemEntity>>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllRecurringItemsSync(
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
  public Flow<RecurringItemEntity> getRecurringItemById(final long id) {
    final String _sql = "SELECT * FROM recurring_items WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"recurring_items"}, new Callable<RecurringItemEntity>() {
      @Override
      @Nullable
      public RecurringItemEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "block_id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_cents");
          final int _cursorIndexOfFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "frequency");
          final int _cursorIndexOfNextDueDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "next_due_date_millis");
          final int _cursorIndexOfNotifyDaysBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "notify_days_before");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final RecurringItemEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new RecurringItemEntity(_tmpId,_tmpAccountId,_tmpCategoryId,_tmpBlockId,_tmpTitle,_tmpAmountCents,_tmpFrequency,_tmpNextDueDateMillis,_tmpNotifyDaysBefore,_tmpIsActive);
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
  public Flow<Long> getTotalMonthlyRecurringCents() {
    final String _sql = "SELECT COALESCE(SUM(amount_cents), 0) FROM recurring_items WHERE is_active = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"recurring_items"}, new Callable<Long>() {
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
  public Object getTotalMonthlyRecurringCentsSync(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT COALESCE(SUM(amount_cents), 0) FROM recurring_items WHERE is_active = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
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
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecurringCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM recurring_items";
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
