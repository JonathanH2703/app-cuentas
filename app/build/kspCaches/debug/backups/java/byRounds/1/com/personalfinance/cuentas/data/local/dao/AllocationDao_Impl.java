package com.personalfinance.cuentas.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity;
import com.personalfinance.cuentas.data.local.entities.AllocationModelEntity;
import com.personalfinance.cuentas.data.local.entities.BlockType;
import com.personalfinance.cuentas.data.local.entities.BudgetEntity;
import com.personalfinance.cuentas.data.local.entities.IncomeAllocationEntity;
import com.personalfinance.cuentas.data.local.relations.AllocationModelWithBlocks;
import java.lang.Class;
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
public final class AllocationDao_Impl implements AllocationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AllocationModelEntity> __insertionAdapterOfAllocationModelEntity;

  private final EntityInsertionAdapter<AllocationBlockEntity> __insertionAdapterOfAllocationBlockEntity;

  private final EntityInsertionAdapter<BudgetEntity> __insertionAdapterOfBudgetEntity;

  private final EntityInsertionAdapter<IncomeAllocationEntity> __insertionAdapterOfIncomeAllocationEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateAllModels;

  private final SharedSQLiteStatement __preparedStmtOfActivateModel;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBudgetForBlock;

  public AllocationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
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
    this.__preparedStmtOfDeactivateAllModels = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE allocation_models SET is_active = 0";
        return _query;
      }
    };
    this.__preparedStmtOfActivateModel = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE allocation_models SET is_active = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteBudgetForBlock = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM budgets WHERE block_id = ? AND month = ? AND year = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertModel(final AllocationModelEntity model,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAllocationModelEntity.insertAndReturnId(model);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBlock(final AllocationBlockEntity block,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAllocationBlockEntity.insertAndReturnId(block);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBlocks(final List<AllocationBlockEntity> blocks,
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
  public Object insertBudget(final BudgetEntity budget,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBudgetEntity.insertAndReturnId(budget);
          __db.setTransactionSuccessful();
          return _result;
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
  public Object setActiveModel(final long modelId, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> AllocationDao.DefaultImpls.setActiveModel(AllocationDao_Impl.this, modelId, __cont), $completion);
  }

  @Override
  public Object deactivateAllModels(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateAllModels.acquire();
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
          __preparedStmtOfDeactivateAllModels.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object activateModel(final long modelId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActivateModel.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, modelId);
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
          __preparedStmtOfActivateModel.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBudgetForBlock(final long blockId, final int month, final int year,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBudgetForBlock.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, blockId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, month);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, year);
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
          __preparedStmtOfDeleteBudgetForBlock.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<AllocationModelWithBlocks> getActiveModelWithBlocks() {
    final String _sql = "SELECT `allocation_models`.`id` AS `id`, `allocation_models`.`name` AS `name`, `allocation_models`.`is_active` AS `is_active`, `allocation_models`.`created_at` AS `created_at` FROM allocation_models WHERE is_active = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"allocation_blocks",
        "allocation_models"}, new Callable<AllocationModelWithBlocks>() {
      @Override
      @Nullable
      public AllocationModelWithBlocks call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = 0;
            final int _cursorIndexOfName = 1;
            final int _cursorIndexOfIsActive = 2;
            final int _cursorIndexOfCreatedAt = 3;
            final LongSparseArray<ArrayList<AllocationBlockEntity>> _collectionBlocks = new LongSparseArray<ArrayList<AllocationBlockEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionBlocks.containsKey(_tmpKey)) {
                _collectionBlocks.put(_tmpKey, new ArrayList<AllocationBlockEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipallocationBlocksAscomPersonalfinanceCuentasDataLocalEntitiesAllocationBlockEntity(_collectionBlocks);
            final AllocationModelWithBlocks _result;
            if (_cursor.moveToFirst()) {
              final AllocationModelEntity _tmpModel;
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
              _tmpModel = new AllocationModelEntity(_tmpId,_tmpName,_tmpIsActive,_tmpCreatedAt);
              final ArrayList<AllocationBlockEntity> _tmpBlocksCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpBlocksCollection = _collectionBlocks.get(_tmpKey_1);
              _result = new AllocationModelWithBlocks(_tmpModel,_tmpBlocksCollection);
            } else {
              _result = null;
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
  public Object getActiveModelWithBlocksSync(
      final Continuation<? super AllocationModelWithBlocks> $completion) {
    final String _sql = "SELECT `allocation_models`.`id` AS `id`, `allocation_models`.`name` AS `name`, `allocation_models`.`is_active` AS `is_active`, `allocation_models`.`created_at` AS `created_at` FROM allocation_models WHERE is_active = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<AllocationModelWithBlocks>() {
      @Override
      @Nullable
      public AllocationModelWithBlocks call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = 0;
            final int _cursorIndexOfName = 1;
            final int _cursorIndexOfIsActive = 2;
            final int _cursorIndexOfCreatedAt = 3;
            final LongSparseArray<ArrayList<AllocationBlockEntity>> _collectionBlocks = new LongSparseArray<ArrayList<AllocationBlockEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionBlocks.containsKey(_tmpKey)) {
                _collectionBlocks.put(_tmpKey, new ArrayList<AllocationBlockEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipallocationBlocksAscomPersonalfinanceCuentasDataLocalEntitiesAllocationBlockEntity(_collectionBlocks);
            final AllocationModelWithBlocks _result;
            if (_cursor.moveToFirst()) {
              final AllocationModelEntity _tmpModel;
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
              _tmpModel = new AllocationModelEntity(_tmpId,_tmpName,_tmpIsActive,_tmpCreatedAt);
              final ArrayList<AllocationBlockEntity> _tmpBlocksCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpBlocksCollection = _collectionBlocks.get(_tmpKey_1);
              _result = new AllocationModelWithBlocks(_tmpModel,_tmpBlocksCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<AllocationModelEntity> getActiveModel() {
    final String _sql = "SELECT `allocation_models`.`id` AS `id`, `allocation_models`.`name` AS `name`, `allocation_models`.`is_active` AS `is_active`, `allocation_models`.`created_at` AS `created_at` FROM allocation_models WHERE is_active = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"allocation_models"}, new Callable<AllocationModelEntity>() {
      @Override
      @Nullable
      public AllocationModelEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfIsActive = 2;
          final int _cursorIndexOfCreatedAt = 3;
          final AllocationModelEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new AllocationModelEntity(_tmpId,_tmpName,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<List<AllocationModelWithBlocks>> getAllModelsWithBlocks() {
    final String _sql = "SELECT `allocation_models`.`id` AS `id`, `allocation_models`.`name` AS `name`, `allocation_models`.`is_active` AS `is_active`, `allocation_models`.`created_at` AS `created_at` FROM allocation_models ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"allocation_blocks",
        "allocation_models"}, new Callable<List<AllocationModelWithBlocks>>() {
      @Override
      @NonNull
      public List<AllocationModelWithBlocks> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = 0;
            final int _cursorIndexOfName = 1;
            final int _cursorIndexOfIsActive = 2;
            final int _cursorIndexOfCreatedAt = 3;
            final LongSparseArray<ArrayList<AllocationBlockEntity>> _collectionBlocks = new LongSparseArray<ArrayList<AllocationBlockEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionBlocks.containsKey(_tmpKey)) {
                _collectionBlocks.put(_tmpKey, new ArrayList<AllocationBlockEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipallocationBlocksAscomPersonalfinanceCuentasDataLocalEntitiesAllocationBlockEntity(_collectionBlocks);
            final List<AllocationModelWithBlocks> _result = new ArrayList<AllocationModelWithBlocks>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final AllocationModelWithBlocks _item;
              final AllocationModelEntity _tmpModel;
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
              _tmpModel = new AllocationModelEntity(_tmpId,_tmpName,_tmpIsActive,_tmpCreatedAt);
              final ArrayList<AllocationBlockEntity> _tmpBlocksCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpBlocksCollection = _collectionBlocks.get(_tmpKey_1);
              _item = new AllocationModelWithBlocks(_tmpModel,_tmpBlocksCollection);
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
  public Flow<List<AllocationBlockEntity>> getBlocksForModel(final long modelId) {
    final String _sql = "SELECT * FROM allocation_blocks WHERE model_id = ? ORDER BY order_index ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, modelId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"allocation_blocks"}, new Callable<List<AllocationBlockEntity>>() {
      @Override
      @NonNull
      public List<AllocationBlockEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfModelId = CursorUtil.getColumnIndexOrThrow(_cursor, "model_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "percentage");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "order_index");
          final int _cursorIndexOfAbsorbRemainder = CursorUtil.getColumnIndexOrThrow(_cursor, "absorb_remainder");
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getBlockById(final long blockId,
      final Continuation<? super AllocationBlockEntity> $completion) {
    final String _sql = "SELECT * FROM allocation_blocks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, blockId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AllocationBlockEntity>() {
      @Override
      @Nullable
      public AllocationBlockEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfModelId = CursorUtil.getColumnIndexOrThrow(_cursor, "model_id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "percentage");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "order_index");
          final int _cursorIndexOfAbsorbRemainder = CursorUtil.getColumnIndexOrThrow(_cursor, "absorb_remainder");
          final AllocationBlockEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new AllocationBlockEntity(_tmpId,_tmpModelId,_tmpName,_tmpPercentage,_tmpType,_tmpColorHex,_tmpOrderIndex,_tmpAbsorbRemainder);
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
  public Object getModelCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM allocation_models";
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
  public Flow<List<BudgetEntity>> getBudgetsForMonth(final int month, final int year) {
    final String _sql = "SELECT * FROM budgets WHERE month = ? AND year = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, month);
    _argIndex = 2;
    _statement.bindLong(_argIndex, year);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"budgets"}, new Callable<List<BudgetEntity>>() {
      @Override
      @NonNull
      public List<BudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "block_id");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "category_id");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfLimitAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "limit_amount_cents");
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllocationsForIncome(final long incomeId,
      final Continuation<? super List<IncomeAllocationEntity>> $completion) {
    final String _sql = "SELECT * FROM income_allocations WHERE income_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, incomeId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<IncomeAllocationEntity>>() {
      @Override
      @NonNull
      public List<IncomeAllocationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfIncomeId = CursorUtil.getColumnIndexOrThrow(_cursor, "income_id");
          final int _cursorIndexOfBlockId = CursorUtil.getColumnIndexOrThrow(_cursor, "block_id");
          final int _cursorIndexOfAssignedCents = CursorUtil.getColumnIndexOrThrow(_cursor, "assigned_cents");
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
  public Flow<Long> getTotalAllocatedToBlockBetween(final long blockId, final long startMillis,
      final long endMillis) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(ia.assigned_cents), 0)\n"
            + "        FROM income_allocations ia\n"
            + "        INNER JOIN incomes i ON ia.income_id = i.id\n"
            + "        WHERE ia.block_id = ? AND i.is_received = 1\n"
            + "          AND i.received_date_millis BETWEEN ? AND ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, blockId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startMillis);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"income_allocations",
        "incomes"}, new Callable<Long>() {
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

  private void __fetchRelationshipallocationBlocksAscomPersonalfinanceCuentasDataLocalEntitiesAllocationBlockEntity(
      @NonNull final LongSparseArray<ArrayList<AllocationBlockEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipallocationBlocksAscomPersonalfinanceCuentasDataLocalEntitiesAllocationBlockEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`model_id`,`name`,`percentage`,`type`,`color_hex`,`order_index`,`absorb_remainder` FROM `allocation_blocks` WHERE `model_id` IN (");
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
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "model_id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfModelId = 1;
      final int _cursorIndexOfName = 2;
      final int _cursorIndexOfPercentage = 3;
      final int _cursorIndexOfType = 4;
      final int _cursorIndexOfColorHex = 5;
      final int _cursorIndexOfOrderIndex = 6;
      final int _cursorIndexOfAbsorbRemainder = 7;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<AllocationBlockEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final AllocationBlockEntity _item_1;
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
          _item_1 = new AllocationBlockEntity(_tmpId,_tmpModelId,_tmpName,_tmpPercentage,_tmpType,_tmpColorHex,_tmpOrderIndex,_tmpAbsorbRemainder);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
