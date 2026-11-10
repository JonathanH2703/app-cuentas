package com.personalfinance.cuentas.domain.usecases;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.AllocationDao;
import com.personalfinance.cuentas.data.local.dao.CategoryDao;
import com.personalfinance.cuentas.data.local.dao.IncomeDao;
import com.personalfinance.cuentas.data.local.dao.TransactionDao;
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
public final class ConfirmIncomeUseCase_Factory implements Factory<ConfirmIncomeUseCase> {
  private final Provider<IncomeDao> incomeDaoProvider;

  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<AllocationDao> allocationDaoProvider;

  public ConfirmIncomeUseCase_Factory(Provider<IncomeDao> incomeDaoProvider,
      Provider<AccountDao> accountDaoProvider, Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<AllocationDao> allocationDaoProvider) {
    this.incomeDaoProvider = incomeDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.allocationDaoProvider = allocationDaoProvider;
  }

  @Override
  public ConfirmIncomeUseCase get() {
    return newInstance(incomeDaoProvider.get(), accountDaoProvider.get(), transactionDaoProvider.get(), categoryDaoProvider.get(), allocationDaoProvider.get());
  }

  public static ConfirmIncomeUseCase_Factory create(Provider<IncomeDao> incomeDaoProvider,
      Provider<AccountDao> accountDaoProvider, Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<AllocationDao> allocationDaoProvider) {
    return new ConfirmIncomeUseCase_Factory(incomeDaoProvider, accountDaoProvider, transactionDaoProvider, categoryDaoProvider, allocationDaoProvider);
  }

  public static ConfirmIncomeUseCase newInstance(IncomeDao incomeDao, AccountDao accountDao,
      TransactionDao transactionDao, CategoryDao categoryDao, AllocationDao allocationDao) {
    return new ConfirmIncomeUseCase(incomeDao, accountDao, transactionDao, categoryDao, allocationDao);
  }
}
