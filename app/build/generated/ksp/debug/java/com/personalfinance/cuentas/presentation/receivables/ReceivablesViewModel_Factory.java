package com.personalfinance.cuentas.presentation.receivables;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.ContactDao;
import com.personalfinance.cuentas.data.local.dao.IncomeDao;
import com.personalfinance.cuentas.data.local.dao.ReceivableDao;
import com.personalfinance.cuentas.data.local.dao.TransactionDao;
import com.personalfinance.cuentas.domain.usecases.ConfirmIncomeUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ReceivablesViewModel_Factory implements Factory<ReceivablesViewModel> {
  private final Provider<ContactDao> contactDaoProvider;

  private final Provider<ReceivableDao> receivableDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<IncomeDao> incomeDaoProvider;

  private final Provider<ConfirmIncomeUseCase> confirmIncomeUseCaseProvider;

  public ReceivablesViewModel_Factory(Provider<ContactDao> contactDaoProvider,
      Provider<ReceivableDao> receivableDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<AccountDao> accountDaoProvider,
      Provider<IncomeDao> incomeDaoProvider,
      Provider<ConfirmIncomeUseCase> confirmIncomeUseCaseProvider) {
    this.contactDaoProvider = contactDaoProvider;
    this.receivableDaoProvider = receivableDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
    this.incomeDaoProvider = incomeDaoProvider;
    this.confirmIncomeUseCaseProvider = confirmIncomeUseCaseProvider;
  }

  @Override
  public ReceivablesViewModel get() {
    return newInstance(contactDaoProvider.get(), receivableDaoProvider.get(), transactionDaoProvider.get(), accountDaoProvider.get(), incomeDaoProvider.get(), confirmIncomeUseCaseProvider.get());
  }

  public static ReceivablesViewModel_Factory create(Provider<ContactDao> contactDaoProvider,
      Provider<ReceivableDao> receivableDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<AccountDao> accountDaoProvider,
      Provider<IncomeDao> incomeDaoProvider,
      Provider<ConfirmIncomeUseCase> confirmIncomeUseCaseProvider) {
    return new ReceivablesViewModel_Factory(contactDaoProvider, receivableDaoProvider, transactionDaoProvider, accountDaoProvider, incomeDaoProvider, confirmIncomeUseCaseProvider);
  }

  public static ReceivablesViewModel newInstance(ContactDao contactDao, ReceivableDao receivableDao,
      TransactionDao transactionDao, AccountDao accountDao, IncomeDao incomeDao,
      ConfirmIncomeUseCase confirmIncomeUseCase) {
    return new ReceivablesViewModel(contactDao, receivableDao, transactionDao, accountDao, incomeDao, confirmIncomeUseCase);
  }
}
