package com.personalfinance.cuentas.domain.usecases.creditcards;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.CreditCardDao;
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
public final class CreateCreditCardUseCase_Factory implements Factory<CreateCreditCardUseCase> {
  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<CreditCardDao> creditCardDaoProvider;

  public CreateCreditCardUseCase_Factory(Provider<AccountDao> accountDaoProvider,
      Provider<CreditCardDao> creditCardDaoProvider) {
    this.accountDaoProvider = accountDaoProvider;
    this.creditCardDaoProvider = creditCardDaoProvider;
  }

  @Override
  public CreateCreditCardUseCase get() {
    return newInstance(accountDaoProvider.get(), creditCardDaoProvider.get());
  }

  public static CreateCreditCardUseCase_Factory create(Provider<AccountDao> accountDaoProvider,
      Provider<CreditCardDao> creditCardDaoProvider) {
    return new CreateCreditCardUseCase_Factory(accountDaoProvider, creditCardDaoProvider);
  }

  public static CreateCreditCardUseCase newInstance(AccountDao accountDao,
      CreditCardDao creditCardDao) {
    return new CreateCreditCardUseCase(accountDao, creditCardDao);
  }
}
