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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.personalfinance.cuentas.data.local.entities.AccountEntity;
import com.personalfinance.cuentas.data.local.entities.AccountType;
import com.personalfinance.cuentas.data.local.entities.CardPurchaseEntity;
import com.personalfinance.cuentas.data.local.entities.CreditCardEntity;
import com.personalfinance.cuentas.data.local.relations.CreditCardWithAccount;
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
public final class CreditCardDao_Impl implements CreditCardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CreditCardEntity> __insertionAdapterOfCreditCardEntity;

  private final EntityInsertionAdapter<CardPurchaseEntity> __insertionAdapterOfCardPurchaseEntity;

  private final EntityDeletionOrUpdateAdapter<CreditCardEntity> __deletionAdapterOfCreditCardEntity;

  private final EntityDeletionOrUpdateAdapter<CardPurchaseEntity> __deletionAdapterOfCardPurchaseEntity;

  private final EntityDeletionOrUpdateAdapter<CreditCardEntity> __updateAdapterOfCreditCardEntity;

  private final EntityDeletionOrUpdateAdapter<CardPurchaseEntity> __updateAdapterOfCardPurchaseEntity;

  public CreditCardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCreditCardEntity = new EntityInsertionAdapter<CreditCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `credit_cards` (`id`,`account_id`,`credit_limit_cents`,`cut_off_day`,`payment_due_day`,`interest_rate_ea`,`handling_fee_cents`,`last_four_digits`,`revolving_rate`,`monthly_insurance_cents`,`installment_type`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCardEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        statement.bindLong(3, entity.getCreditLimitCents());
        statement.bindLong(4, entity.getCutOffDay());
        statement.bindLong(5, entity.getPaymentDueDay());
        statement.bindDouble(6, entity.getInterestRateEa());
        statement.bindLong(7, entity.getHandlingFeeCents());
        if (entity.getLastFourDigits() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLastFourDigits());
        }
        statement.bindDouble(9, entity.getRevolvingRate());
        statement.bindLong(10, entity.getMonthlyInsuranceCents());
        statement.bindLong(11, entity.getInstallmentType());
      }
    };
    this.__insertionAdapterOfCardPurchaseEntity = new EntityInsertionAdapter<CardPurchaseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `card_purchases` (`id`,`card_id`,`merchant`,`purchase_date_millis`,`total_amount_cents`,`installments_total`,`installments_paid`,`interest_rate_nm`,`is_fully_paid`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CardPurchaseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCardId());
        statement.bindString(3, entity.getMerchant());
        statement.bindLong(4, entity.getPurchaseDateMillis());
        statement.bindLong(5, entity.getTotalAmountCents());
        statement.bindLong(6, entity.getInstallmentsTotal());
        statement.bindLong(7, entity.getInstallmentsPaid());
        statement.bindDouble(8, entity.getInterestRateNm());
        final int _tmp = entity.isFullyPaid() ? 1 : 0;
        statement.bindLong(9, _tmp);
      }
    };
    this.__deletionAdapterOfCreditCardEntity = new EntityDeletionOrUpdateAdapter<CreditCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `credit_cards` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCardEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfCardPurchaseEntity = new EntityDeletionOrUpdateAdapter<CardPurchaseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `card_purchases` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CardPurchaseEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCreditCardEntity = new EntityDeletionOrUpdateAdapter<CreditCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `credit_cards` SET `id` = ?,`account_id` = ?,`credit_limit_cents` = ?,`cut_off_day` = ?,`payment_due_day` = ?,`interest_rate_ea` = ?,`handling_fee_cents` = ?,`last_four_digits` = ?,`revolving_rate` = ?,`monthly_insurance_cents` = ?,`installment_type` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCardEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAccountId());
        statement.bindLong(3, entity.getCreditLimitCents());
        statement.bindLong(4, entity.getCutOffDay());
        statement.bindLong(5, entity.getPaymentDueDay());
        statement.bindDouble(6, entity.getInterestRateEa());
        statement.bindLong(7, entity.getHandlingFeeCents());
        if (entity.getLastFourDigits() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLastFourDigits());
        }
        statement.bindDouble(9, entity.getRevolvingRate());
        statement.bindLong(10, entity.getMonthlyInsuranceCents());
        statement.bindLong(11, entity.getInstallmentType());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__updateAdapterOfCardPurchaseEntity = new EntityDeletionOrUpdateAdapter<CardPurchaseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `card_purchases` SET `id` = ?,`card_id` = ?,`merchant` = ?,`purchase_date_millis` = ?,`total_amount_cents` = ?,`installments_total` = ?,`installments_paid` = ?,`interest_rate_nm` = ?,`is_fully_paid` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CardPurchaseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCardId());
        statement.bindString(3, entity.getMerchant());
        statement.bindLong(4, entity.getPurchaseDateMillis());
        statement.bindLong(5, entity.getTotalAmountCents());
        statement.bindLong(6, entity.getInstallmentsTotal());
        statement.bindLong(7, entity.getInstallmentsPaid());
        statement.bindDouble(8, entity.getInterestRateNm());
        final int _tmp = entity.isFullyPaid() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getId());
      }
    };
  }

  @Override
  public Object insertCreditCard(final CreditCardEntity card,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCreditCardEntity.insertAndReturnId(card);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPurchase(final CardPurchaseEntity purchase,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCardPurchaseEntity.insertAndReturnId(purchase);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCreditCard(final CreditCardEntity card,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCreditCardEntity.handle(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePurchase(final CardPurchaseEntity purchase,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCardPurchaseEntity.handle(purchase);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCreditCard(final CreditCardEntity card,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCreditCardEntity.handle(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePurchase(final CardPurchaseEntity purchase,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCardPurchaseEntity.handle(purchase);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CreditCardWithAccount>> getAllCreditCardsWithAccount() {
    final String _sql = "SELECT `credit_cards`.`id` AS `id`, `credit_cards`.`account_id` AS `account_id`, `credit_cards`.`credit_limit_cents` AS `credit_limit_cents`, `credit_cards`.`cut_off_day` AS `cut_off_day`, `credit_cards`.`payment_due_day` AS `payment_due_day`, `credit_cards`.`interest_rate_ea` AS `interest_rate_ea`, `credit_cards`.`handling_fee_cents` AS `handling_fee_cents`, `credit_cards`.`last_four_digits` AS `last_four_digits`, `credit_cards`.`revolving_rate` AS `revolving_rate`, `credit_cards`.`monthly_insurance_cents` AS `monthly_insurance_cents`, `credit_cards`.`installment_type` AS `installment_type` FROM credit_cards ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"accounts", "card_purchases",
        "credit_cards"}, new Callable<List<CreditCardWithAccount>>() {
      @Override
      @NonNull
      public List<CreditCardWithAccount> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = 0;
            final int _cursorIndexOfAccountId = 1;
            final int _cursorIndexOfCreditLimitCents = 2;
            final int _cursorIndexOfCutOffDay = 3;
            final int _cursorIndexOfPaymentDueDay = 4;
            final int _cursorIndexOfInterestRateEa = 5;
            final int _cursorIndexOfHandlingFeeCents = 6;
            final int _cursorIndexOfLastFourDigits = 7;
            final int _cursorIndexOfRevolvingRate = 8;
            final int _cursorIndexOfMonthlyInsuranceCents = 9;
            final int _cursorIndexOfInstallmentType = 10;
            final LongSparseArray<AccountEntity> _collectionAccount = new LongSparseArray<AccountEntity>();
            final LongSparseArray<ArrayList<CardPurchaseEntity>> _collectionPurchases = new LongSparseArray<ArrayList<CardPurchaseEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfAccountId);
              _collectionAccount.put(_tmpKey, null);
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionPurchases.containsKey(_tmpKey_1)) {
                _collectionPurchases.put(_tmpKey_1, new ArrayList<CardPurchaseEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipaccountsAscomPersonalfinanceCuentasDataLocalEntitiesAccountEntity(_collectionAccount);
            __fetchRelationshipcardPurchasesAscomPersonalfinanceCuentasDataLocalEntitiesCardPurchaseEntity(_collectionPurchases);
            final List<CreditCardWithAccount> _result = new ArrayList<CreditCardWithAccount>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final CreditCardWithAccount _item;
              final CreditCardEntity _tmpCard;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAccountId;
              _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
              final long _tmpCreditLimitCents;
              _tmpCreditLimitCents = _cursor.getLong(_cursorIndexOfCreditLimitCents);
              final int _tmpCutOffDay;
              _tmpCutOffDay = _cursor.getInt(_cursorIndexOfCutOffDay);
              final int _tmpPaymentDueDay;
              _tmpPaymentDueDay = _cursor.getInt(_cursorIndexOfPaymentDueDay);
              final double _tmpInterestRateEa;
              _tmpInterestRateEa = _cursor.getDouble(_cursorIndexOfInterestRateEa);
              final long _tmpHandlingFeeCents;
              _tmpHandlingFeeCents = _cursor.getLong(_cursorIndexOfHandlingFeeCents);
              final String _tmpLastFourDigits;
              if (_cursor.isNull(_cursorIndexOfLastFourDigits)) {
                _tmpLastFourDigits = null;
              } else {
                _tmpLastFourDigits = _cursor.getString(_cursorIndexOfLastFourDigits);
              }
              final double _tmpRevolvingRate;
              _tmpRevolvingRate = _cursor.getDouble(_cursorIndexOfRevolvingRate);
              final long _tmpMonthlyInsuranceCents;
              _tmpMonthlyInsuranceCents = _cursor.getLong(_cursorIndexOfMonthlyInsuranceCents);
              final int _tmpInstallmentType;
              _tmpInstallmentType = _cursor.getInt(_cursorIndexOfInstallmentType);
              _tmpCard = new CreditCardEntity(_tmpId,_tmpAccountId,_tmpCreditLimitCents,_tmpCutOffDay,_tmpPaymentDueDay,_tmpInterestRateEa,_tmpHandlingFeeCents,_tmpLastFourDigits,_tmpRevolvingRate,_tmpMonthlyInsuranceCents,_tmpInstallmentType);
              final AccountEntity _tmpAccount;
              final long _tmpKey_2;
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfAccountId);
              _tmpAccount = _collectionAccount.get(_tmpKey_2);
              final ArrayList<CardPurchaseEntity> _tmpPurchasesCollection;
              final long _tmpKey_3;
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId);
              _tmpPurchasesCollection = _collectionPurchases.get(_tmpKey_3);
              _item = new CreditCardWithAccount(_tmpCard,_tmpAccount,_tmpPurchasesCollection);
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
  public Object getAllCreditCardsWithAccountSync(
      final Continuation<? super List<CreditCardWithAccount>> $completion) {
    final String _sql = "SELECT `credit_cards`.`id` AS `id`, `credit_cards`.`account_id` AS `account_id`, `credit_cards`.`credit_limit_cents` AS `credit_limit_cents`, `credit_cards`.`cut_off_day` AS `cut_off_day`, `credit_cards`.`payment_due_day` AS `payment_due_day`, `credit_cards`.`interest_rate_ea` AS `interest_rate_ea`, `credit_cards`.`handling_fee_cents` AS `handling_fee_cents`, `credit_cards`.`last_four_digits` AS `last_four_digits`, `credit_cards`.`revolving_rate` AS `revolving_rate`, `credit_cards`.`monthly_insurance_cents` AS `monthly_insurance_cents`, `credit_cards`.`installment_type` AS `installment_type` FROM credit_cards ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<List<CreditCardWithAccount>>() {
      @Override
      @NonNull
      public List<CreditCardWithAccount> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = 0;
            final int _cursorIndexOfAccountId = 1;
            final int _cursorIndexOfCreditLimitCents = 2;
            final int _cursorIndexOfCutOffDay = 3;
            final int _cursorIndexOfPaymentDueDay = 4;
            final int _cursorIndexOfInterestRateEa = 5;
            final int _cursorIndexOfHandlingFeeCents = 6;
            final int _cursorIndexOfLastFourDigits = 7;
            final int _cursorIndexOfRevolvingRate = 8;
            final int _cursorIndexOfMonthlyInsuranceCents = 9;
            final int _cursorIndexOfInstallmentType = 10;
            final LongSparseArray<AccountEntity> _collectionAccount = new LongSparseArray<AccountEntity>();
            final LongSparseArray<ArrayList<CardPurchaseEntity>> _collectionPurchases = new LongSparseArray<ArrayList<CardPurchaseEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfAccountId);
              _collectionAccount.put(_tmpKey, null);
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionPurchases.containsKey(_tmpKey_1)) {
                _collectionPurchases.put(_tmpKey_1, new ArrayList<CardPurchaseEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipaccountsAscomPersonalfinanceCuentasDataLocalEntitiesAccountEntity(_collectionAccount);
            __fetchRelationshipcardPurchasesAscomPersonalfinanceCuentasDataLocalEntitiesCardPurchaseEntity(_collectionPurchases);
            final List<CreditCardWithAccount> _result = new ArrayList<CreditCardWithAccount>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final CreditCardWithAccount _item;
              final CreditCardEntity _tmpCard;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAccountId;
              _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
              final long _tmpCreditLimitCents;
              _tmpCreditLimitCents = _cursor.getLong(_cursorIndexOfCreditLimitCents);
              final int _tmpCutOffDay;
              _tmpCutOffDay = _cursor.getInt(_cursorIndexOfCutOffDay);
              final int _tmpPaymentDueDay;
              _tmpPaymentDueDay = _cursor.getInt(_cursorIndexOfPaymentDueDay);
              final double _tmpInterestRateEa;
              _tmpInterestRateEa = _cursor.getDouble(_cursorIndexOfInterestRateEa);
              final long _tmpHandlingFeeCents;
              _tmpHandlingFeeCents = _cursor.getLong(_cursorIndexOfHandlingFeeCents);
              final String _tmpLastFourDigits;
              if (_cursor.isNull(_cursorIndexOfLastFourDigits)) {
                _tmpLastFourDigits = null;
              } else {
                _tmpLastFourDigits = _cursor.getString(_cursorIndexOfLastFourDigits);
              }
              final double _tmpRevolvingRate;
              _tmpRevolvingRate = _cursor.getDouble(_cursorIndexOfRevolvingRate);
              final long _tmpMonthlyInsuranceCents;
              _tmpMonthlyInsuranceCents = _cursor.getLong(_cursorIndexOfMonthlyInsuranceCents);
              final int _tmpInstallmentType;
              _tmpInstallmentType = _cursor.getInt(_cursorIndexOfInstallmentType);
              _tmpCard = new CreditCardEntity(_tmpId,_tmpAccountId,_tmpCreditLimitCents,_tmpCutOffDay,_tmpPaymentDueDay,_tmpInterestRateEa,_tmpHandlingFeeCents,_tmpLastFourDigits,_tmpRevolvingRate,_tmpMonthlyInsuranceCents,_tmpInstallmentType);
              final AccountEntity _tmpAccount;
              final long _tmpKey_2;
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfAccountId);
              _tmpAccount = _collectionAccount.get(_tmpKey_2);
              final ArrayList<CardPurchaseEntity> _tmpPurchasesCollection;
              final long _tmpKey_3;
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId);
              _tmpPurchasesCollection = _collectionPurchases.get(_tmpKey_3);
              _item = new CreditCardWithAccount(_tmpCard,_tmpAccount,_tmpPurchasesCollection);
              _result.add(_item);
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
  public Flow<CreditCardWithAccount> getCreditCardById(final long id) {
    final String _sql = "SELECT * FROM credit_cards WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"accounts", "card_purchases",
        "credit_cards"}, new Callable<CreditCardWithAccount>() {
      @Override
      @Nullable
      public CreditCardWithAccount call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfAccountId = CursorUtil.getColumnIndexOrThrow(_cursor, "account_id");
            final int _cursorIndexOfCreditLimitCents = CursorUtil.getColumnIndexOrThrow(_cursor, "credit_limit_cents");
            final int _cursorIndexOfCutOffDay = CursorUtil.getColumnIndexOrThrow(_cursor, "cut_off_day");
            final int _cursorIndexOfPaymentDueDay = CursorUtil.getColumnIndexOrThrow(_cursor, "payment_due_day");
            final int _cursorIndexOfInterestRateEa = CursorUtil.getColumnIndexOrThrow(_cursor, "interest_rate_ea");
            final int _cursorIndexOfHandlingFeeCents = CursorUtil.getColumnIndexOrThrow(_cursor, "handling_fee_cents");
            final int _cursorIndexOfLastFourDigits = CursorUtil.getColumnIndexOrThrow(_cursor, "last_four_digits");
            final int _cursorIndexOfRevolvingRate = CursorUtil.getColumnIndexOrThrow(_cursor, "revolving_rate");
            final int _cursorIndexOfMonthlyInsuranceCents = CursorUtil.getColumnIndexOrThrow(_cursor, "monthly_insurance_cents");
            final int _cursorIndexOfInstallmentType = CursorUtil.getColumnIndexOrThrow(_cursor, "installment_type");
            final LongSparseArray<AccountEntity> _collectionAccount = new LongSparseArray<AccountEntity>();
            final LongSparseArray<ArrayList<CardPurchaseEntity>> _collectionPurchases = new LongSparseArray<ArrayList<CardPurchaseEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfAccountId);
              _collectionAccount.put(_tmpKey, null);
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionPurchases.containsKey(_tmpKey_1)) {
                _collectionPurchases.put(_tmpKey_1, new ArrayList<CardPurchaseEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipaccountsAscomPersonalfinanceCuentasDataLocalEntitiesAccountEntity(_collectionAccount);
            __fetchRelationshipcardPurchasesAscomPersonalfinanceCuentasDataLocalEntitiesCardPurchaseEntity(_collectionPurchases);
            final CreditCardWithAccount _result;
            if (_cursor.moveToFirst()) {
              final CreditCardEntity _tmpCard;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpAccountId;
              _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
              final long _tmpCreditLimitCents;
              _tmpCreditLimitCents = _cursor.getLong(_cursorIndexOfCreditLimitCents);
              final int _tmpCutOffDay;
              _tmpCutOffDay = _cursor.getInt(_cursorIndexOfCutOffDay);
              final int _tmpPaymentDueDay;
              _tmpPaymentDueDay = _cursor.getInt(_cursorIndexOfPaymentDueDay);
              final double _tmpInterestRateEa;
              _tmpInterestRateEa = _cursor.getDouble(_cursorIndexOfInterestRateEa);
              final long _tmpHandlingFeeCents;
              _tmpHandlingFeeCents = _cursor.getLong(_cursorIndexOfHandlingFeeCents);
              final String _tmpLastFourDigits;
              if (_cursor.isNull(_cursorIndexOfLastFourDigits)) {
                _tmpLastFourDigits = null;
              } else {
                _tmpLastFourDigits = _cursor.getString(_cursorIndexOfLastFourDigits);
              }
              final double _tmpRevolvingRate;
              _tmpRevolvingRate = _cursor.getDouble(_cursorIndexOfRevolvingRate);
              final long _tmpMonthlyInsuranceCents;
              _tmpMonthlyInsuranceCents = _cursor.getLong(_cursorIndexOfMonthlyInsuranceCents);
              final int _tmpInstallmentType;
              _tmpInstallmentType = _cursor.getInt(_cursorIndexOfInstallmentType);
              _tmpCard = new CreditCardEntity(_tmpId,_tmpAccountId,_tmpCreditLimitCents,_tmpCutOffDay,_tmpPaymentDueDay,_tmpInterestRateEa,_tmpHandlingFeeCents,_tmpLastFourDigits,_tmpRevolvingRate,_tmpMonthlyInsuranceCents,_tmpInstallmentType);
              final AccountEntity _tmpAccount;
              final long _tmpKey_2;
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfAccountId);
              _tmpAccount = _collectionAccount.get(_tmpKey_2);
              final ArrayList<CardPurchaseEntity> _tmpPurchasesCollection;
              final long _tmpKey_3;
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId);
              _tmpPurchasesCollection = _collectionPurchases.get(_tmpKey_3);
              _result = new CreditCardWithAccount(_tmpCard,_tmpAccount,_tmpPurchasesCollection);
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
  public Flow<List<CardPurchaseEntity>> getPurchasesForCard(final long cardId) {
    final String _sql = "SELECT * FROM card_purchases WHERE card_id = ? ORDER BY purchase_date_millis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"card_purchases"}, new Callable<List<CardPurchaseEntity>>() {
      @Override
      @NonNull
      public List<CardPurchaseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "card_id");
          final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
          final int _cursorIndexOfPurchaseDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "purchase_date_millis");
          final int _cursorIndexOfTotalAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "total_amount_cents");
          final int _cursorIndexOfInstallmentsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "installments_total");
          final int _cursorIndexOfInstallmentsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "installments_paid");
          final int _cursorIndexOfInterestRateNm = CursorUtil.getColumnIndexOrThrow(_cursor, "interest_rate_nm");
          final int _cursorIndexOfIsFullyPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "is_fully_paid");
          final List<CardPurchaseEntity> _result = new ArrayList<CardPurchaseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CardPurchaseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final String _tmpMerchant;
            _tmpMerchant = _cursor.getString(_cursorIndexOfMerchant);
            final long _tmpPurchaseDateMillis;
            _tmpPurchaseDateMillis = _cursor.getLong(_cursorIndexOfPurchaseDateMillis);
            final long _tmpTotalAmountCents;
            _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
            final int _tmpInstallmentsTotal;
            _tmpInstallmentsTotal = _cursor.getInt(_cursorIndexOfInstallmentsTotal);
            final int _tmpInstallmentsPaid;
            _tmpInstallmentsPaid = _cursor.getInt(_cursorIndexOfInstallmentsPaid);
            final double _tmpInterestRateNm;
            _tmpInterestRateNm = _cursor.getDouble(_cursorIndexOfInterestRateNm);
            final boolean _tmpIsFullyPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFullyPaid);
            _tmpIsFullyPaid = _tmp != 0;
            _item = new CardPurchaseEntity(_tmpId,_tmpCardId,_tmpMerchant,_tmpPurchaseDateMillis,_tmpTotalAmountCents,_tmpInstallmentsTotal,_tmpInstallmentsPaid,_tmpInterestRateNm,_tmpIsFullyPaid);
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
  public Flow<List<CardPurchaseEntity>> getActivePurchasesForCard(final long cardId) {
    final String _sql = "SELECT * FROM card_purchases WHERE card_id = ? AND is_fully_paid = 0 ORDER BY purchase_date_millis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"card_purchases"}, new Callable<List<CardPurchaseEntity>>() {
      @Override
      @NonNull
      public List<CardPurchaseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "card_id");
          final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
          final int _cursorIndexOfPurchaseDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "purchase_date_millis");
          final int _cursorIndexOfTotalAmountCents = CursorUtil.getColumnIndexOrThrow(_cursor, "total_amount_cents");
          final int _cursorIndexOfInstallmentsTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "installments_total");
          final int _cursorIndexOfInstallmentsPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "installments_paid");
          final int _cursorIndexOfInterestRateNm = CursorUtil.getColumnIndexOrThrow(_cursor, "interest_rate_nm");
          final int _cursorIndexOfIsFullyPaid = CursorUtil.getColumnIndexOrThrow(_cursor, "is_fully_paid");
          final List<CardPurchaseEntity> _result = new ArrayList<CardPurchaseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CardPurchaseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final String _tmpMerchant;
            _tmpMerchant = _cursor.getString(_cursorIndexOfMerchant);
            final long _tmpPurchaseDateMillis;
            _tmpPurchaseDateMillis = _cursor.getLong(_cursorIndexOfPurchaseDateMillis);
            final long _tmpTotalAmountCents;
            _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
            final int _tmpInstallmentsTotal;
            _tmpInstallmentsTotal = _cursor.getInt(_cursorIndexOfInstallmentsTotal);
            final int _tmpInstallmentsPaid;
            _tmpInstallmentsPaid = _cursor.getInt(_cursorIndexOfInstallmentsPaid);
            final double _tmpInterestRateNm;
            _tmpInterestRateNm = _cursor.getDouble(_cursorIndexOfInterestRateNm);
            final boolean _tmpIsFullyPaid;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFullyPaid);
            _tmpIsFullyPaid = _tmp != 0;
            _item = new CardPurchaseEntity(_tmpId,_tmpCardId,_tmpMerchant,_tmpPurchaseDateMillis,_tmpTotalAmountCents,_tmpInstallmentsTotal,_tmpInstallmentsPaid,_tmpInterestRateNm,_tmpIsFullyPaid);
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
  public Flow<Long> getTotalUsedCreditCents() {
    final String _sql = "SELECT COALESCE(SUM(total_amount_cents), 0) FROM card_purchases WHERE is_fully_paid = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"card_purchases"}, new Callable<Long>() {
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
  public Object getCardCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM credit_cards";
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
  public Flow<Long> getPendingCycleCents(final long cardId) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(total_amount_cents), 0)\n"
            + "        FROM card_purchases\n"
            + "        WHERE card_id = ? AND is_fully_paid = 0\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"card_purchases"}, new Callable<Long>() {
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

  private void __fetchRelationshipcardPurchasesAscomPersonalfinanceCuentasDataLocalEntitiesCardPurchaseEntity(
      @NonNull final LongSparseArray<ArrayList<CardPurchaseEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipcardPurchasesAscomPersonalfinanceCuentasDataLocalEntitiesCardPurchaseEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`card_id`,`merchant`,`purchase_date_millis`,`total_amount_cents`,`installments_total`,`installments_paid`,`interest_rate_nm`,`is_fully_paid` FROM `card_purchases` WHERE `card_id` IN (");
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
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "card_id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfCardId = 1;
      final int _cursorIndexOfMerchant = 2;
      final int _cursorIndexOfPurchaseDateMillis = 3;
      final int _cursorIndexOfTotalAmountCents = 4;
      final int _cursorIndexOfInstallmentsTotal = 5;
      final int _cursorIndexOfInstallmentsPaid = 6;
      final int _cursorIndexOfInterestRateNm = 7;
      final int _cursorIndexOfIsFullyPaid = 8;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<CardPurchaseEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final CardPurchaseEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpCardId;
          _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
          final String _tmpMerchant;
          _tmpMerchant = _cursor.getString(_cursorIndexOfMerchant);
          final long _tmpPurchaseDateMillis;
          _tmpPurchaseDateMillis = _cursor.getLong(_cursorIndexOfPurchaseDateMillis);
          final long _tmpTotalAmountCents;
          _tmpTotalAmountCents = _cursor.getLong(_cursorIndexOfTotalAmountCents);
          final int _tmpInstallmentsTotal;
          _tmpInstallmentsTotal = _cursor.getInt(_cursorIndexOfInstallmentsTotal);
          final int _tmpInstallmentsPaid;
          _tmpInstallmentsPaid = _cursor.getInt(_cursorIndexOfInstallmentsPaid);
          final double _tmpInterestRateNm;
          _tmpInterestRateNm = _cursor.getDouble(_cursorIndexOfInterestRateNm);
          final boolean _tmpIsFullyPaid;
          final int _tmp;
          _tmp = _cursor.getInt(_cursorIndexOfIsFullyPaid);
          _tmpIsFullyPaid = _tmp != 0;
          _item_1 = new CardPurchaseEntity(_tmpId,_tmpCardId,_tmpMerchant,_tmpPurchaseDateMillis,_tmpTotalAmountCents,_tmpInstallmentsTotal,_tmpInstallmentsPaid,_tmpInterestRateNm,_tmpIsFullyPaid);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
