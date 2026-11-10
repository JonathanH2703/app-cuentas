package com.personalfinance.cuentas.domain.usecases.capture;

import android.content.Context;
import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.AllocationDao;
import com.personalfinance.cuentas.data.local.dao.CreditCardDao;
import com.personalfinance.cuentas.data.local.dao.DebtDao;
import com.personalfinance.cuentas.data.local.dao.PendingTransactionDao;
import com.personalfinance.cuentas.data.local.dao.TransactionDao;
import com.personalfinance.cuentas.domain.capture.MerchantCategorizer;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ProcessBankCaptureUseCase_Factory implements Factory<ProcessBankCaptureUseCase> {
  private final Provider<Context> contextProvider;

  private final Provider<PendingTransactionDao> pendingTransactionDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<AllocationDao> allocationDaoProvider;

  private final Provider<MerchantCategorizer> merchantCategorizerProvider;

  private final Provider<CreditCardDao> creditCardDaoProvider;

  private final Provider<DebtDao> debtDaoProvider;

  public ProcessBankCaptureUseCase_Factory(Provider<Context> contextProvider,
      Provider<PendingTransactionDao> pendingTransactionDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<AccountDao> accountDaoProvider,
      Provider<AllocationDao> allocationDaoProvider,
      Provider<MerchantCategorizer> merchantCategorizerProvider,
      Provider<CreditCardDao> creditCardDaoProvider, Provider<DebtDao> debtDaoProvider) {
    this.contextProvider = contextProvider;
    this.pendingTransactionDaoProvider = pendingTransactionDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
    this.allocationDaoProvider = allocationDaoProvider;
    this.merchantCategorizerProvider = merchantCategorizerProvider;
    this.creditCardDaoProvider = creditCardDaoProvider;
    this.debtDaoProvider = debtDaoProvider;
  }

  @Override
  public ProcessBankCaptureUseCase get() {
    return newInstance(contextProvider.get(), pendingTransactionDaoProvider.get(), transactionDaoProvider.get(), accountDaoProvider.get(), allocationDaoProvider.get(), merchantCategorizerProvider.get(), creditCardDaoProvider.get(), debtDaoProvider.get());
  }

  public static ProcessBankCaptureUseCase_Factory create(Provider<Context> contextProvider,
      Provider<PendingTransactionDao> pendingTransactionDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<AccountDao> accountDaoProvider,
      Provider<AllocationDao> allocationDaoProvider,
      Provider<MerchantCategorizer> merchantCategorizerProvider,
      Provider<CreditCardDao> creditCardDaoProvider, Provider<DebtDao> debtDaoProvider) {
    return new ProcessBankCaptureUseCase_Factory(contextProvider, pendingTransactionDaoProvider, transactionDaoProvider, accountDaoProvider, allocationDaoProvider, merchantCategorizerProvider, creditCardDaoProvider, debtDaoProvider);
  }

  public static ProcessBankCaptureUseCase newInstance(Context context,
      PendingTransactionDao pendingTransactionDao, TransactionDao transactionDao,
      AccountDao accountDao, AllocationDao allocationDao, MerchantCategorizer merchantCategorizer,
      CreditCardDao creditCardDao, DebtDao debtDao) {
    return new ProcessBankCaptureUseCase(context, pendingTransactionDao, transactionDao, accountDao, allocationDao, merchantCategorizer, creditCardDao, debtDao);
  }
}
