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
import com.personalfinance.cuentas.data.local.entities.DebtEntity;
import com.personalfinance.cuentas.data.local.entities.DebtPaymentEntity;
import com.personalfinance.cuentas.data.local.relations.DebtWithPayments;
import java.lang.Class;
import java.lang.Exception;
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
public final class DebtDao_Impl implements DebtDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DebtEntity> __insertionAdapterOfDebtEntity;

  private final EntityInsertionAdapter<DebtPaymentEntity> __insertionAdapterOfDebtPaymentEntity;

  private final EntityDeletionOrUpdateAdapter<DebtEntity> __deletionAdapterOfDebtEntity;

  private final EntityDeletionOrUpdateAdapter<DebtPaymentEntity> __deletionAdapterOfDebtPaymentEntity;

  private final EntityDeletionOrUpdateAdapter<DebtEntity> __updateAdapterOfDebtEntity;

  private final SharedSQLiteStatement __preparedStmtOfRestoreBalance;

  private final SharedSQLiteStatement __preparedStmtOfUpdateNextDueDate;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBalance;

  public DebtDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDebtEntity = new EntityInsertionAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `debts` (`id`,`name`,`principal_amount_cents`,`current_balance_cents`,`interest_rate_ea`,`monthly_installment_cents`,`due_day_of_month`,`start_date_millis`,`next_due_date_millis`,`monthly_insurance_cents`,`is_settled`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getPrincipalAmountCents());
        statement.bindLong(4, entity.getCurrentBalanceCents());
        statement.bindDouble(5, entity.getInterestRateEa());
        statement.bindLong(6, entity.getMonthlyInstallmentCents());
        statement.bindLong(7, entity.getDueDayOfMonth());
        statement.bindLong(8, entity.getStartDateMillis());
        if (entity.getNextDueDateMillis() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getNextDueDateMillis());
        }
        statement.bindLong(10, entity.getMonthlyInsuranceCents());
        final int _tmp = entity.isSettled() ? 1 : 0;
        statement.bindLong(11, _tmp);
      }
    };
    this.__insertionAdapterOfDebtPaymentEntity = new EntityInsertionAdapter<DebtPaymentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `debt_payments` (`id`,`debt_id`,`payment_date_millis`,`total_paid_cents`,`principal_paid_cents`,`interest_paid_cents`,`is_extra_payment`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtPaymentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDebtId());
        statement.bindLong(3, entity.getPaymentDateMillis());
        statement.bindLong(4, entity.getTotalPaidCents());
        statement.bindLong(5, entity.getPrincipalPaidCents());
        statement.bindLong(6, entity.getInterestPaidCents());
        final int _tmp = entity.isExtraPayment() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__deletionAdapterOfDebtEntity = new EntityDeletionOrUpdateAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `debts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfDebtPaymentEntity = new EntityDeletionOrUpdateAdapter<DebtPaymentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `debt_payments` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtPaymentEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfDebtEntity = new EntityDeletionOrUpdateAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `debts` SET `id` = ?,`name` = ?,`principal_amount_cents` = ?,`current_balance_cents` = ?,`interest_rate_ea` = ?,`monthly_installment_cents` = ?,`due_day_of_month` = ?,`start_date_millis` = ?,`next_due_date_millis` = ?,`monthly_insurance_cents` = ?,`is_settled` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getPrincipalAmountCents());
        statement.bindLong(4, entity.getCurrentBalanceCents());
        statement.bindDouble(5, entity.getInterestRateEa());
        statement.bindLong(6, entity.getMonthlyInstallmentCents());
        statement.bindLong(7, entity.getDueDayOfMonth());
        statement.bindLong(8, entity.getStartDateMillis());
        if (entity.getNextDueDateMillis() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getNextDueDateMillis());
        }
        statement.bindLong(10, entity.getMonthlyInsuranceCents());
        final int _tmp = entity.isSettled() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfRestoreBalance = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE debts SET current_balance_cents = current_balance_cents + ?, is_settled = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateNextDueDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE debts SET next_due_date_millis = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateBalance = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE debts SET current_balance_cents = ?, is_settled = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDebt(final DebtEntity debt, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDebtEntity.insertAndReturnId(debt);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPayment(final DebtPaymentEntity payment,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDebtPaymentEntity.insertAndReturnId(payment);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDebt(final DebtEntity debt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDebtEntity.handle(debt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePayment(final DebtPaymentEntity payment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDebtPaymentEntity.handle(payment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDebt(final DebtEntity debt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDebtEntity.handle(debt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreBalance(final long debtId, final long principalCents,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRestoreBalance.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, principalCents);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, debtId);
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
          __preparedStmtOfRestoreBalance.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateNextDueDate(final long debtId, final Long nextDueDateMillis,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateNextDueDate.acquire();
        int _argIndex = 1;
        if (nextDueDateMillis == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, nextDueDateMillis);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, debtId);
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
  public Object updateBalance(final long debtId, final long newBalanceCents, final boolean settled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBalance.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, newBalanceCents);
        _argIndex = 2;
        final int _tmp = settled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, debtId);
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
  public Flow<List<DebtWithPayments>> getAllDebtsWithPayments() {
    final String _sql = "SELECT `debts`.`id` AS `id`, `debts`.`name` AS `name`, `debts`.`principal_amount_cents` AS `principal_amount_cents`, `debts`.`current_balance_cents` AS `current_balance_cents`, `debts`.`interest_rate_ea` AS `interest_rate_ea`, `debts`.`monthly_installment_cents` AS `monthly_installment_cents`, `debts`.`due_day_of_month` AS `due_day_of_month`, `debts`.`start_date_millis` AS `start_date_millis`, `debts`.`next_due_date_millis` AS `next_due_date_millis`, `debts`.`monthly_insurance_cents` AS `monthly_insurance_cents`, `debts`.`is_settled` AS `is_settled` FROM debts ORDER BY is_settled ASC, current_balance_cents DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"debt_payments",
        "debts"}, new Callable<List<DebtWithPayments>>() {
      @Override
      @NonNull
      public List<DebtWithPayments> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
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
            final LongSparseArray<ArrayList<DebtPaymentEntity>> _collectionPayments = new LongSparseArray<ArrayList<DebtPaymentEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionPayments.containsKey(_tmpKey)) {
                _collectionPayments.put(_tmpKey, new ArrayList<DebtPaymentEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipdebtPaymentsAscomPersonalfinanceCuentasDataLocalEntitiesDebtPaymentEntity(_collectionPayments);
            final List<DebtWithPayments> _result = new ArrayList<DebtWithPayments>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final DebtWithPayments _item;
              final DebtEntity _tmpDebt;
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
              _tmpDebt = new DebtEntity(_tmpId,_tmpName,_tmpPrincipalAmountCents,_tmpCurrentBalanceCents,_tmpInterestRateEa,_tmpMonthlyInstallmentCents,_tmpDueDayOfMonth,_tmpStartDateMillis,_tmpNextDueDateMillis,_tmpMonthlyInsuranceCents,_tmpIsSettled);
              final ArrayList<DebtPaymentEntity> _tmpPaymentsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpPaymentsCollection = _collectionPayments.get(_tmpKey_1);
              _item = new DebtWithPayments(_tmpDebt,_tmpPaymentsCollection);
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
  public Flow<List<DebtEntity>> getActiveDebts() {
    final String _sql = "SELECT `debts`.`id` AS `id`, `debts`.`name` AS `name`, `debts`.`principal_amount_cents` AS `principal_amount_cents`, `debts`.`current_balance_cents` AS `current_balance_cents`, `debts`.`interest_rate_ea` AS `interest_rate_ea`, `debts`.`monthly_installment_cents` AS `monthly_installment_cents`, `debts`.`due_day_of_month` AS `due_day_of_month`, `debts`.`start_date_millis` AS `start_date_millis`, `debts`.`next_due_date_millis` AS `next_due_date_millis`, `debts`.`monthly_insurance_cents` AS `monthly_insurance_cents`, `debts`.`is_settled` AS `is_settled` FROM debts WHERE is_settled = 0 ORDER BY current_balance_cents DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debts"}, new Callable<List<DebtEntity>>() {
      @Override
      @NonNull
      public List<DebtEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
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
          final List<DebtEntity> _result = new ArrayList<DebtEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtEntity _item;
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
            _item = new DebtEntity(_tmpId,_tmpName,_tmpPrincipalAmountCents,_tmpCurrentBalanceCents,_tmpInterestRateEa,_tmpMonthlyInstallmentCents,_tmpDueDayOfMonth,_tmpStartDateMillis,_tmpNextDueDateMillis,_tmpMonthlyInsuranceCents,_tmpIsSettled);
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
  public Object getActiveDebtsSync(final Continuation<? super List<DebtEntity>> $completion) {
    final String _sql = "SELECT `debts`.`id` AS `id`, `debts`.`name` AS `name`, `debts`.`principal_amount_cents` AS `principal_amount_cents`, `debts`.`current_balance_cents` AS `current_balance_cents`, `debts`.`interest_rate_ea` AS `interest_rate_ea`, `debts`.`monthly_installment_cents` AS `monthly_installment_cents`, `debts`.`due_day_of_month` AS `due_day_of_month`, `debts`.`start_date_millis` AS `start_date_millis`, `debts`.`next_due_date_millis` AS `next_due_date_millis`, `debts`.`monthly_insurance_cents` AS `monthly_insurance_cents`, `debts`.`is_settled` AS `is_settled` FROM debts WHERE is_settled = 0 ORDER BY current_balance_cents DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DebtEntity>>() {
      @Override
      @NonNull
      public List<DebtEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
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
          final List<DebtEntity> _result = new ArrayList<DebtEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtEntity _item;
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
            _item = new DebtEntity(_tmpId,_tmpName,_tmpPrincipalAmountCents,_tmpCurrentBalanceCents,_tmpInterestRateEa,_tmpMonthlyInstallmentCents,_tmpDueDayOfMonth,_tmpStartDateMillis,_tmpNextDueDateMillis,_tmpMonthlyInsuranceCents,_tmpIsSettled);
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
  public Flow<DebtEntity> getDebtById(final long id) {
    final String _sql = "SELECT * FROM debts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debts"}, new Callable<DebtEntity>() {
      @Override
      @Nullable
      public DebtEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPrincipalAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "principal_amount_cents");
          final int _cursorIndexOfCurrentBalanceCents = CursorUtil.getColumnIndexOrThrow(_cursor, "current_balance_cents");
          final int _cursorIndexOfInterestRateEa = CursorUtil.getColumnIndexOrThrow(_cursor, "interest_rate_ea");
          final int _cursorIndexOfMonthlyInstallmentCents = CursorUtil.getColumnIndexOrThrow(_cursor, "monthly_installment_cents");
          final int _cursorIndexOfDueDayOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "due_day_of_month");
          final int _cursorIndexOfStartDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date_millis");
          final int _cursorIndexOfNextDueDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "next_due_date_millis");
          final int _cursorIndexOfMonthlyInsuranceCents = CursorUtil.getColumnIndexOrThrow(_cursor, "monthly_insurance_cents");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final DebtEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new DebtEntity(_tmpId,_tmpName,_tmpPrincipalAmountCents,_tmpCurrentBalanceCents,_tmpInterestRateEa,_tmpMonthlyInstallmentCents,_tmpDueDayOfMonth,_tmpStartDateMillis,_tmpNextDueDateMillis,_tmpMonthlyInsuranceCents,_tmpIsSettled);
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
  public Flow<List<DebtPaymentEntity>> getPaymentsForDebt(final long debtId) {
    final String _sql = "SELECT * FROM debt_payments WHERE debt_id = ? ORDER BY payment_date_millis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, debtId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debt_payments"}, new Callable<List<DebtPaymentEntity>>() {
      @Override
      @NonNull
      public List<DebtPaymentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "debt_id");
          final int _cursorIndexOfPaymentDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_date_millis");
          final int _cursorIndexOfTotalPaidCents = CursorUtil.getColumnIndexOrThrow(_cursor, "total_paid_cents");
          final int _cursorIndexOfPrincipalPaidCents = CursorUtil.getColumnIndexOrThrow(_cursor, "principal_paid_cents");
          final int _cursorIndexOfInterestPaidCents = CursorUtil.getColumnIndexOrThrow(_cursor, "interest_paid_cents");
          final int _cursorIndexOfIsExtraPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "is_extra_payment");
          final List<DebtPaymentEntity> _result = new ArrayList<DebtPaymentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtPaymentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDebtId;
            _tmpDebtId = _cursor.getLong(_cursorIndexOfDebtId);
            final long _tmpPaymentDateMillis;
            _tmpPaymentDateMillis = _cursor.getLong(_cursorIndexOfPaymentDateMillis);
            final long _tmpTotalPaidCents;
            _tmpTotalPaidCents = _cursor.getLong(_cursorIndexOfTotalPaidCents);
            final long _tmpPrincipalPaidCents;
            _tmpPrincipalPaidCents = _cursor.getLong(_cursorIndexOfPrincipalPaidCents);
            final long _tmpInterestPaidCents;
            _tmpInterestPaidCents = _cursor.getLong(_cursorIndexOfInterestPaidCents);
            final boolean _tmpIsExtraPayment;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsExtraPayment);
            _tmpIsExtraPayment = _tmp != 0;
            _item = new DebtPaymentEntity(_tmpId,_tmpDebtId,_tmpPaymentDateMillis,_tmpTotalPaidCents,_tmpPrincipalPaidCents,_tmpInterestPaidCents,_tmpIsExtraPayment);
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
  public Object findPayment(final long debtId, final long amountCents, final long dateMillis,
      final Continuation<? super DebtPaymentEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM debt_payments \n"
            + "        WHERE debt_id = ? AND total_paid_cents = ? AND payment_date_millis = ? \n"
            + "        ORDER BY id DESC LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, debtId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, amountCents);
    _argIndex = 3;
    _statement.bindLong(_argIndex, dateMillis);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DebtPaymentEntity>() {
      @Override
      @Nullable
      public DebtPaymentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDebtId = CursorUtil.getColumnIndexOrThrow(_cursor, "debt_id");
          final int _cursorIndexOfPaymentDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_date_millis");
          final int _cursorIndexOfTotalPaidCents = CursorUtil.getColumnIndexOrThrow(_cursor, "total_paid_cents");
          final int _cursorIndexOfPrincipalPaidCents = CursorUtil.getColumnIndexOrThrow(_cursor, "principal_paid_cents");
          final int _cursorIndexOfInterestPaidCents = CursorUtil.getColumnIndexOrThrow(_cursor, "interest_paid_cents");
          final int _cursorIndexOfIsExtraPayment = CursorUtil.getColumnIndexOrThrow(_cursor, "is_extra_payment");
          final DebtPaymentEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDebtId;
            _tmpDebtId = _cursor.getLong(_cursorIndexOfDebtId);
            final long _tmpPaymentDateMillis;
            _tmpPaymentDateMillis = _cursor.getLong(_cursorIndexOfPaymentDateMillis);
            final long _tmpTotalPaidCents;
            _tmpTotalPaidCents = _cursor.getLong(_cursorIndexOfTotalPaidCents);
            final long _tmpPrincipalPaidCents;
            _tmpPrincipalPaidCents = _cursor.getLong(_cursorIndexOfPrincipalPaidCents);
            final long _tmpInterestPaidCents;
            _tmpInterestPaidCents = _cursor.getLong(_cursorIndexOfInterestPaidCents);
            final boolean _tmpIsExtraPayment;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsExtraPayment);
            _tmpIsExtraPayment = _tmp != 0;
            _result = new DebtPaymentEntity(_tmpId,_tmpDebtId,_tmpPaymentDateMillis,_tmpTotalPaidCents,_tmpPrincipalPaidCents,_tmpInterestPaidCents,_tmpIsExtraPayment);
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
  public Flow<Long> getTotalDebtBalanceCents() {
    final String _sql = "SELECT COALESCE(SUM(current_balance_cents), 0) FROM debts WHERE is_settled = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debts"}, new Callable<Long>() {
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
  public Object getDebtCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM debts";
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

  private void __fetchRelationshipdebtPaymentsAscomPersonalfinanceCuentasDataLocalEntitiesDebtPaymentEntity(
      @NonNull final LongSparseArray<ArrayList<DebtPaymentEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipdebtPaymentsAscomPersonalfinanceCuentasDataLocalEntitiesDebtPaymentEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`debt_id`,`payment_date_millis`,`total_paid_cents`,`principal_paid_cents`,`interest_paid_cents`,`is_extra_payment` FROM `debt_payments` WHERE `debt_id` IN (");
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
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "debt_id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfDebtId = 1;
      final int _cursorIndexOfPaymentDateMillis = 2;
      final int _cursorIndexOfTotalPaidCents = 3;
      final int _cursorIndexOfPrincipalPaidCents = 4;
      final int _cursorIndexOfInterestPaidCents = 5;
      final int _cursorIndexOfIsExtraPayment = 6;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<DebtPaymentEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final DebtPaymentEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpDebtId;
          _tmpDebtId = _cursor.getLong(_cursorIndexOfDebtId);
          final long _tmpPaymentDateMillis;
          _tmpPaymentDateMillis = _cursor.getLong(_cursorIndexOfPaymentDateMillis);
          final long _tmpTotalPaidCents;
          _tmpTotalPaidCents = _cursor.getLong(_cursorIndexOfTotalPaidCents);
          final long _tmpPrincipalPaidCents;
          _tmpPrincipalPaidCents = _cursor.getLong(_cursorIndexOfPrincipalPaidCents);
          final long _tmpInterestPaidCents;
          _tmpInterestPaidCents = _cursor.getLong(_cursorIndexOfInterestPaidCents);
          final boolean _tmpIsExtraPayment;
          final int _tmp;
          _tmp = _cursor.getInt(_cursorIndexOfIsExtraPayment);
          _tmpIsExtraPayment = _tmp != 0;
          _item_1 = new DebtPaymentEntity(_tmpId,_tmpDebtId,_tmpPaymentDateMillis,_tmpTotalPaidCents,_tmpPrincipalPaidCents,_tmpInterestPaidCents,_tmpIsExtraPayment);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
