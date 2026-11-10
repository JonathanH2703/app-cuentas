package com.personalfinance.cuentas.domain.usecases;

import com.personalfinance.cuentas.data.local.dao.DebtDao;
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao;
import com.personalfinance.cuentas.data.local.dao.TransactionDao;
import com.personalfinance.cuentas.domain.usecases.debts.RecordDebtPaymentUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class AnchorTransactionUseCase_Factory implements Factory<AnchorTransactionUseCase> {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<DebtDao> debtDaoProvider;

  private final Provider<RecurringItemDao> recurringItemDaoProvider;

  private final Provider<RecordDebtPaymentUseCase> recordDebtPaymentUseCaseProvider;

  public AnchorTransactionUseCase_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<DebtDao> debtDaoProvider, Provider<RecurringItemDao> recurringItemDaoProvider,
      Provider<RecordDebtPaymentUseCase> recordDebtPaymentUseCaseProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.debtDaoProvider = debtDaoProvider;
    this.recurringItemDaoProvider = recurringItemDaoProvider;
    this.recordDebtPaymentUseCaseProvider = recordDebtPaymentUseCaseProvider;
  }

  @Override
  public AnchorTransactionUseCase get() {
    return newInstance(transactionDaoProvider.get(), debtDaoProvider.get(), recurringItemDaoProvider.get(), recordDebtPaymentUseCaseProvider.get());
  }

  public static AnchorTransactionUseCase_Factory create(
      Provider<TransactionDao> transactionDaoProvider, Provider<DebtDao> debtDaoProvider,
      Provider<RecurringItemDao> recurringItemDaoProvider,
      Provider<RecordDebtPaymentUseCase> recordDebtPaymentUseCaseProvider) {
    return new AnchorTransactionUseCase_Factory(transactionDaoProvider, debtDaoProvider, recurringItemDaoProvider, recordDebtPaymentUseCaseProvider);
  }

  public static AnchorTransactionUseCase newInstance(TransactionDao transactionDao, DebtDao debtDao,
      RecurringItemDao recurringItemDao, RecordDebtPaymentUseCase recordDebtPaymentUseCase) {
    return new AnchorTransactionUseCase(transactionDao, debtDao, recurringItemDao, recordDebtPaymentUseCase);
  }
}
