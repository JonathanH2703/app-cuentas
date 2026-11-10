package com.personalfinance.cuentas.domain.usecases.creditcards;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
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
public final class PayCreditCardUseCase_Factory implements Factory<PayCreditCardUseCase> {
  private final Provider<CreditCardDao> creditCardDaoProvider;

  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<AllocationDao> allocationDaoProvider;

  public PayCreditCardUseCase_Factory(Provider<CreditCardDao> creditCardDaoProvider,
      Provider<AccountDao> accountDaoProvider, Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<AllocationDao> allocationDaoProvider) {
    this.creditCardDaoProvider = creditCardDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.allocationDaoProvider = allocationDaoProvider;
  }

  @Override
  public PayCreditCardUseCase get() {
    return newInstance(creditCardDaoProvider.get(), accountDaoProvider.get(), transactionDaoProvider.get(), categoryDaoProvider.get(), allocationDaoProvider.get());
  }

  public static PayCreditCardUseCase_Factory create(Provider<CreditCardDao> creditCardDaoProvider,
      Provider<AccountDao> accountDaoProvider, Provider<TransactionDao> transactionDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<AllocationDao> allocationDaoProvider) {
    return new PayCreditCardUseCase_Factory(creditCardDaoProvider, accountDaoProvider, transactionDaoProvider, categoryDaoProvider, allocationDaoProvider);
  }

  public static PayCreditCardUseCase newInstance(CreditCardDao creditCardDao, AccountDao accountDao,
      TransactionDao transactionDao, CategoryDao categoryDao, AllocationDao allocationDao) {
    return new PayCreditCardUseCase(creditCardDao, accountDao, transactionDao, categoryDao, allocationDao);
  }
}
