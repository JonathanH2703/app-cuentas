package com.personalfinance.cuentas.domain.usecases;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.AllocationDao;
import com.personalfinance.cuentas.data.local.dao.CategoryDao;
import com.personalfinance.cuentas.data.local.dao.TagDao;
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
public final class RecordExpenseUseCase_Factory implements Factory<RecordExpenseUseCase> {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<AllocationDao> allocationDaoProvider;

  private final Provider<TagDao> tagDaoProvider;

  public RecordExpenseUseCase_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<AccountDao> accountDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<AllocationDao> allocationDaoProvider, Provider<TagDao> tagDaoProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.allocationDaoProvider = allocationDaoProvider;
    this.tagDaoProvider = tagDaoProvider;
  }

  @Override
  public RecordExpenseUseCase get() {
    return newInstance(transactionDaoProvider.get(), accountDaoProvider.get(), categoryDaoProvider.get(), allocationDaoProvider.get(), tagDaoProvider.get());
  }

  public static RecordExpenseUseCase_Factory create(Provider<TransactionDao> transactionDaoProvider,
      Provider<AccountDao> accountDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<AllocationDao> allocationDaoProvider, Provider<TagDao> tagDaoProvider) {
    return new RecordExpenseUseCase_Factory(transactionDaoProvider, accountDaoProvider, categoryDaoProvider, allocationDaoProvider, tagDaoProvider);
  }

  public static RecordExpenseUseCase newInstance(TransactionDao transactionDao,
      AccountDao accountDao, CategoryDao categoryDao, AllocationDao allocationDao, TagDao tagDao) {
    return new RecordExpenseUseCase(transactionDao, accountDao, categoryDao, allocationDao, tagDao);
  }
}
