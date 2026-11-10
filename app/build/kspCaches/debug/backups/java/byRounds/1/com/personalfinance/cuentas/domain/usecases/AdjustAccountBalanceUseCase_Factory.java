package com.personalfinance.cuentas.domain.usecases;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.CategoryDao;
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
public final class AdjustAccountBalanceUseCase_Factory implements Factory<AdjustAccountBalanceUseCase> {
  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public AdjustAccountBalanceUseCase_Factory(Provider<AccountDao> accountDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    this.accountDaoProvider = accountDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public AdjustAccountBalanceUseCase get() {
    return newInstance(accountDaoProvider.get(), transactionDaoProvider.get(), categoryDaoProvider.get());
  }

  public static AdjustAccountBalanceUseCase_Factory create(Provider<AccountDao> accountDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    return new AdjustAccountBalanceUseCase_Factory(accountDaoProvider, transactionDaoProvider, categoryDaoProvider);
  }

  public static AdjustAccountBalanceUseCase newInstance(AccountDao accountDao,
      TransactionDao transactionDao, CategoryDao categoryDao) {
    return new AdjustAccountBalanceUseCase(accountDao, transactionDao, categoryDao);
  }
}
