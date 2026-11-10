package com.personalfinance.cuentas.domain.usecases.creditcards;

import com.personalfinance.cuentas.data.local.dao.AllocationDao;
import com.personalfinance.cuentas.data.local.dao.CategoryDao;
import com.personalfinance.cuentas.data.local.dao.CreditCardDao;
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
public final class RecordCardPurchaseUseCase_Factory implements Factory<RecordCardPurchaseUseCase> {
  private final Provider<CreditCardDao> creditCardDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<AllocationDao> allocationDaoProvider;

  public RecordCardPurchaseUseCase_Factory(Provider<CreditCardDao> creditCardDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<AllocationDao> allocationDaoProvider) {
    this.creditCardDaoProvider = creditCardDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.allocationDaoProvider = allocationDaoProvider;
  }

  @Override
  public RecordCardPurchaseUseCase get() {
    return newInstance(creditCardDaoProvider.get(), transactionDaoProvider.get(), categoryDaoProvider.get(), allocationDaoProvider.get());
  }

  public static RecordCardPurchaseUseCase_Factory create(
      Provider<CreditCardDao> creditCardDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<AllocationDao> allocationDaoProvider) {
    return new RecordCardPurchaseUseCase_Factory(creditCardDaoProvider, transactionDaoProvider, categoryDaoProvider, allocationDaoProvider);
  }

  public static RecordCardPurchaseUseCase newInstance(CreditCardDao creditCardDao,
      TransactionDao transactionDao, CategoryDao categoryDao, AllocationDao allocationDao) {
    return new RecordCardPurchaseUseCase(creditCardDao, transactionDao, categoryDao, allocationDao);
  }
}
