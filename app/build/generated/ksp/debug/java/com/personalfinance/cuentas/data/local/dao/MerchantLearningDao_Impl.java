package com.personalfinance.cuentas.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.personalfinance.cuentas.data.local.entities.MerchantLearningEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MerchantLearningDao_Impl implements MerchantLearningDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MerchantLearningEntity> __insertionAdapterOfMerchantLearningEntity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementHitCount;

  public MerchantLearningDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
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
    this.__preparedStmtOfIncrementHitCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE merchant_learnings SET hit_count = hit_count + 1 WHERE normalized_merchant = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final MerchantLearningEntity learning,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMerchantLearningEntity.insertAndReturnId(learning);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementHitCount(final String normalizedMerchant,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementHitCount.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, normalizedMerchant);
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
          __preparedStmtOfIncrementHitCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getLearningForMerchant(final String normalizedMerchant,
      final Continuation<? super MerchantLearningEntity> $completion) {
    final String _sql = "SELECT * FROM merchant_learnings WHERE normalized_merchant = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, normalizedMerchant);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MerchantLearningEntity>() {
      @Override
      @Nullable
      public MerchantLearningEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNormalizedMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "normalized_merchant");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "block_id");
          final int _cursorIndexOfHitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "hit_count");
          final MerchantLearningEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new MerchantLearningEntity(_tmpId,_tmpNormalizedMerchant,_tmpCategoryId,_tmpBlockId,_tmpHitCount);
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
}
