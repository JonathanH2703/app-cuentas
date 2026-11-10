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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.personalfinance.cuentas.data.local.entities.EventEntity;
import com.personalfinance.cuentas.data.local.entities.EventType;
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
public final class EventDao_Impl implements EventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EventEntity> __insertionAdapterOfEventEntity;

  private final EntityDeletionOrUpdateAdapter<EventEntity> __deletionAdapterOfEventEntity;

  private final EntityDeletionOrUpdateAdapter<EventEntity> __updateAdapterOfEventEntity;

  public EventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEventEntity = new EntityInsertionAdapter<EventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `events` (`id`,`title`,`type`,`day_of_month`,`month_of_year`,`year`,`person_name`,`notify_days_before`,`is_active`,`total_amount_cents`,`accumulated_cents`,`coverage_percent`,`block_type`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, __EventType_enumToString(entity.getType()));
        statement.bindLong(4, entity.getDayOfMonth());
        statement.bindLong(5, entity.getMonthOfYear());
        if (entity.getYear() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getYear());
        }
        if (entity.getPersonName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPersonName());
        }
        statement.bindLong(8, entity.getNotifyDaysBefore());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(9, _tmp);
        if (entity.getTotalAmountCents() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getTotalAmountCents());
        }
        statement.bindLong(11, entity.getAccumulatedCents());
        statement.bindLong(12, entity.getCoveragePercent());
        if (entity.getBlockType() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getBlockType());
        }
        statement.bindLong(14, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfEventEntity = new EntityDeletionOrUpdateAdapter<EventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `events` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfEventEntity = new EntityDeletionOrUpdateAdapter<EventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `events` SET `id` = ?,`title` = ?,`type` = ?,`day_of_month` = ?,`month_of_year` = ?,`year` = ?,`person_name` = ?,`notify_days_before` = ?,`is_active` = ?,`total_amount_cents` = ?,`accumulated_cents` = ?,`coverage_percent` = ?,`block_type` = ?,`created_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, __EventType_enumToString(entity.getType()));
        statement.bindLong(4, entity.getDayOfMonth());
        statement.bindLong(5, entity.getMonthOfYear());
        if (entity.getYear() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getYear());
        }
        if (entity.getPersonName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPersonName());
        }
        statement.bindLong(8, entity.getNotifyDaysBefore());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(9, _tmp);
        if (entity.getTotalAmountCents() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getTotalAmountCents());
        }
        statement.bindLong(11, entity.getAccumulatedCents());
        statement.bindLong(12, entity.getCoveragePercent());
        if (entity.getBlockType() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getBlockType());
        }
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getId());
      }
    };
  }

  @Override
  public Object insertEvent(final EventEntity event, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEventEntity.insertAndReturnId(event);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEvent(final EventEntity event, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEventEntity.handle(event);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEvent(final EventEntity event, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEventEntity.handle(event);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EventEntity>> getAllActiveEvents() {
    final String _sql = "SELECT `events`.`id` AS `id`, `events`.`title` AS `title`, `events`.`type` AS `type`, `events`.`day_of_month` AS `day_of_month`, `events`.`month_of_year` AS `month_of_year`, `events`.`year` AS `year`, `events`.`person_name` AS `person_name`, `events`.`notify_days_before` AS `notify_days_before`, `events`.`is_active` AS `is_active`, `events`.`total_amount_cents` AS `total_amount_cents`, `events`.`accumulated_cents` AS `accumulated_cents`, `events`.`coverage_percent` AS `coverage_percent`, `events`.`block_type` AS `block_type`, `events`.`created_at` AS `created_at` FROM events WHERE is_active = 1 ORDER BY month_of_year ASC, day_of_month ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"events"}, new Callable<List<EventEntity>>() {
      @Override
      @NonNull
      public List<EventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfTitle = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfDayOfMonth = 3;
          final int _cursorIndexOfMonthOfYear = 4;
          final int _cursorIndexOfYear = 5;
          final int _cursorIndexOfPersonName = 6;
          final int _cursorIndexOfNotifyDaysBefore = 7;
          final int _cursorIndexOfIsActive = 8;
          final int _cursorIndexOfTotalAmountCents = 9;
          final int _cursorIndexOfAccumulatedCents = 10;
          final int _cursorIndexOfCoveragePercent = 11;
          final int _cursorIndexOfBlockType = 12;
          final int _cursorIndexOfCreatedAt = 13;
          final List<EventEntity> _result = new ArrayList<EventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final EventType _tmpType;
            _tmpType = __EventType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final int _tmpDayOfMonth;
            _tmpDayOfMonth = _cursor.getInt(_cursorIndexOfDayOfMonth);
            final int _tmpMonthOfYear;
            _tmpMonthOfYear = _cursor.getInt(_cursorIndexOfMonthOfYear);
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final int _tmpNotifyDaysBefore;
            _tmpNotifyDaysBefore = _cursor.getInt(_cursorIndexOfNotifyDaysBefore);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final Long _tmpTotalAmountCents;
            if (_cursor.isNull(_cursorIndexOfTotalAmountCents)) {
              _tmpTotalAmountCents = null;
            } else {
              _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
            }
            final long _tmpAccumulatedCents;
            _tmpAccumulatedCents = _cursor.getLong(_cursorIndexOfAccumulatedCents);
            final int _tmpCoveragePercent;
            _tmpCoveragePercent = _cursor.getInt(_cursorIndexOfCoveragePercent);
            final String _tmpBlockType;
            if (_cursor.isNull(_cursorIndexOfBlockType)) {
              _tmpBlockType = null;
            } else {
              _tmpBlockType = _cursor.getString(_cursorIndexOfBlockType);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new EventEntity(_tmpId,_tmpTitle,_tmpType,_tmpDayOfMonth,_tmpMonthOfYear,_tmpYear,_tmpPersonName,_tmpNotifyDaysBefore,_tmpIsActive,_tmpTotalAmountCents,_tmpAccumulatedCents,_tmpCoveragePercent,_tmpBlockType,_tmpCreatedAt);
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
  public Flow<List<EventEntity>> getAllEvents() {
    final String _sql = "SELECT `events`.`id` AS `id`, `events`.`title` AS `title`, `events`.`type` AS `type`, `events`.`day_of_month` AS `day_of_month`, `events`.`month_of_year` AS `month_of_year`, `events`.`year` AS `year`, `events`.`person_name` AS `person_name`, `events`.`notify_days_before` AS `notify_days_before`, `events`.`is_active` AS `is_active`, `events`.`total_amount_cents` AS `total_amount_cents`, `events`.`accumulated_cents` AS `accumulated_cents`, `events`.`coverage_percent` AS `coverage_percent`, `events`.`block_type` AS `block_type`, `events`.`created_at` AS `created_at` FROM events ORDER BY month_of_year ASC, day_of_month ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"events"}, new Callable<List<EventEntity>>() {
      @Override
      @NonNull
      public List<EventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfTitle = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfDayOfMonth = 3;
          final int _cursorIndexOfMonthOfYear = 4;
          final int _cursorIndexOfYear = 5;
          final int _cursorIndexOfPersonName = 6;
          final int _cursorIndexOfNotifyDaysBefore = 7;
          final int _cursorIndexOfIsActive = 8;
          final int _cursorIndexOfTotalAmountCents = 9;
          final int _cursorIndexOfAccumulatedCents = 10;
          final int _cursorIndexOfCoveragePercent = 11;
          final int _cursorIndexOfBlockType = 12;
          final int _cursorIndexOfCreatedAt = 13;
          final List<EventEntity> _result = new ArrayList<EventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final EventType _tmpType;
            _tmpType = __EventType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final int _tmpDayOfMonth;
            _tmpDayOfMonth = _cursor.getInt(_cursorIndexOfDayOfMonth);
            final int _tmpMonthOfYear;
            _tmpMonthOfYear = _cursor.getInt(_cursorIndexOfMonthOfYear);
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final int _tmpNotifyDaysBefore;
            _tmpNotifyDaysBefore = _cursor.getInt(_cursorIndexOfNotifyDaysBefore);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final Long _tmpTotalAmountCents;
            if (_cursor.isNull(_cursorIndexOfTotalAmountCents)) {
              _tmpTotalAmountCents = null;
            } else {
              _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
            }
            final long _tmpAccumulatedCents;
            _tmpAccumulatedCents = _cursor.getLong(_cursorIndexOfAccumulatedCents);
            final int _tmpCoveragePercent;
            _tmpCoveragePercent = _cursor.getInt(_cursorIndexOfCoveragePercent);
            final String _tmpBlockType;
            if (_cursor.isNull(_cursorIndexOfBlockType)) {
              _tmpBlockType = null;
            } else {
              _tmpBlockType = _cursor.getString(_cursorIndexOfBlockType);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new EventEntity(_tmpId,_tmpTitle,_tmpType,_tmpDayOfMonth,_tmpMonthOfYear,_tmpYear,_tmpPersonName,_tmpNotifyDaysBefore,_tmpIsActive,_tmpTotalAmountCents,_tmpAccumulatedCents,_tmpCoveragePercent,_tmpBlockType,_tmpCreatedAt);
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
  public Object getAllActiveEventsSync(final Continuation<? super List<EventEntity>> $completion) {
    final String _sql = "SELECT `events`.`id` AS `id`, `events`.`title` AS `title`, `events`.`type` AS `type`, `events`.`day_of_month` AS `day_of_month`, `events`.`month_of_year` AS `month_of_year`, `events`.`year` AS `year`, `events`.`person_name` AS `person_name`, `events`.`notify_days_before` AS `notify_days_before`, `events`.`is_active` AS `is_active`, `events`.`total_amount_cents` AS `total_amount_cents`, `events`.`accumulated_cents` AS `accumulated_cents`, `events`.`coverage_percent` AS `coverage_percent`, `events`.`block_type` AS `block_type`, `events`.`created_at` AS `created_at` FROM events WHERE is_active = 1 ORDER BY month_of_year ASC, day_of_month ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EventEntity>>() {
      @Override
      @NonNull
      public List<EventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfTitle = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfDayOfMonth = 3;
          final int _cursorIndexOfMonthOfYear = 4;
          final int _cursorIndexOfYear = 5;
          final int _cursorIndexOfPersonName = 6;
          final int _cursorIndexOfNotifyDaysBefore = 7;
          final int _cursorIndexOfIsActive = 8;
          final int _cursorIndexOfTotalAmountCents = 9;
          final int _cursorIndexOfAccumulatedCents = 10;
          final int _cursorIndexOfCoveragePercent = 11;
          final int _cursorIndexOfBlockType = 12;
          final int _cursorIndexOfCreatedAt = 13;
          final List<EventEntity> _result = new ArrayList<EventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final EventType _tmpType;
            _tmpType = __EventType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final int _tmpDayOfMonth;
            _tmpDayOfMonth = _cursor.getInt(_cursorIndexOfDayOfMonth);
            final int _tmpMonthOfYear;
            _tmpMonthOfYear = _cursor.getInt(_cursorIndexOfMonthOfYear);
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final int _tmpNotifyDaysBefore;
            _tmpNotifyDaysBefore = _cursor.getInt(_cursorIndexOfNotifyDaysBefore);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final Long _tmpTotalAmountCents;
            if (_cursor.isNull(_cursorIndexOfTotalAmountCents)) {
              _tmpTotalAmountCents = null;
            } else {
              _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
            }
            final long _tmpAccumulatedCents;
            _tmpAccumulatedCents = _cursor.getLong(_cursorIndexOfAccumulatedCents);
            final int _tmpCoveragePercent;
            _tmpCoveragePercent = _cursor.getInt(_cursorIndexOfCoveragePercent);
            final String _tmpBlockType;
            if (_cursor.isNull(_cursorIndexOfBlockType)) {
              _tmpBlockType = null;
            } else {
              _tmpBlockType = _cursor.getString(_cursorIndexOfBlockType);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new EventEntity(_tmpId,_tmpTitle,_tmpType,_tmpDayOfMonth,_tmpMonthOfYear,_tmpYear,_tmpPersonName,_tmpNotifyDaysBefore,_tmpIsActive,_tmpTotalAmountCents,_tmpAccumulatedCents,_tmpCoveragePercent,_tmpBlockType,_tmpCreatedAt);
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
  public Object getEventByIdSync(final long id,
      final Continuation<? super EventEntity> $completion) {
    final String _sql = "SELECT * FROM events WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EventEntity>() {
      @Override
      @Nullable
      public EventEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDayOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "day_of_month");
          final int _cursorIndexOfMonthOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "month_of_year");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfNotifyDaysBefore = CursorUtil.getColumnIndexOrThrow(_cursor, "notify_days_before");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "is_active");
          final int _cursorIndexOfTotalAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "total_amount_cents");
          final int _cursorIndexOfAccumulatedCents = CursorUtil.getColumnIndexOrThrow(_cursor, "accumulated_cents");
          final int _cursorIndexOfCoveragePercent = CursorUtil.getColumnIndexOrThrow(_cursor, "coverage_percent");
          final int _cursorIndexOfBlockType = CursorUtil.getColumnIndexOrThrow(_cursor, "block_type");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final EventEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final EventType _tmpType;
            _tmpType = __EventType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final int _tmpDayOfMonth;
            _tmpDayOfMonth = _cursor.getInt(_cursorIndexOfDayOfMonth);
            final int _tmpMonthOfYear;
            _tmpMonthOfYear = _cursor.getInt(_cursorIndexOfMonthOfYear);
            final Integer _tmpYear;
            if (_cursor.isNull(_cursorIndexOfYear)) {
              _tmpYear = null;
            } else {
              _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final int _tmpNotifyDaysBefore;
            _tmpNotifyDaysBefore = _cursor.getInt(_cursorIndexOfNotifyDaysBefore);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final Long _tmpTotalAmountCents;
            if (_cursor.isNull(_cursorIndexOfTotalAmountCents)) {
              _tmpTotalAmountCents = null;
            } else {
              _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
            }
            final long _tmpAccumulatedCents;
            _tmpAccumulatedCents = _cursor.getLong(_cursorIndexOfAccumulatedCents);
            final int _tmpCoveragePercent;
            _tmpCoveragePercent = _cursor.getInt(_cursorIndexOfCoveragePercent);
            final String _tmpBlockType;
            if (_cursor.isNull(_cursorIndexOfBlockType)) {
              _tmpBlockType = null;
            } else {
              _tmpBlockType = _cursor.getString(_cursorIndexOfBlockType);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new EventEntity(_tmpId,_tmpTitle,_tmpType,_tmpDayOfMonth,_tmpMonthOfYear,_tmpYear,_tmpPersonName,_tmpNotifyDaysBefore,_tmpIsActive,_tmpTotalAmountCents,_tmpAccumulatedCents,_tmpCoveragePercent,_tmpBlockType,_tmpCreatedAt);
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
  public Object getEventCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM events";
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

  private String __EventType_enumToString(@NonNull final EventType _value) {
    switch (_value) {
      case BIRTHDAY: return "BIRTHDAY";
      case ANNIVERSARY: return "ANNIVERSARY";
      case CUSTOM: return "CUSTOM";
      case OBLIGATION_ANNUAL: return "OBLIGATION_ANNUAL";
      case OBLIGATION_SPORADIC: return "OBLIGATION_SPORADIC";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private EventType __EventType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "BIRTHDAY": return EventType.BIRTHDAY;
      case "ANNIVERSARY": return EventType.ANNIVERSARY;
      case "CUSTOM": return EventType.CUSTOM;
      case "OBLIGATION_ANNUAL": return EventType.OBLIGATION_ANNUAL;
      case "OBLIGATION_SPORADIC": return EventType.OBLIGATION_SPORADIC;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
