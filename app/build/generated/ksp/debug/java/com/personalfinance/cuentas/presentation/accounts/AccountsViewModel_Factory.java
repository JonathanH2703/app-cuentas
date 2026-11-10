package com.personalfinance.cuentas.presentation.accounts;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.domain.usecases.AdjustAccountBalanceUseCase;
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
public final class AccountsViewModel_Factory implements Factory<AccountsViewModel> {
  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<AdjustAccountBalanceUseCase> adjustAccountBalanceUseCaseProvider;

  public AccountsViewModel_Factory(Provider<AccountDao> accountDaoProvider,
      Provider<AdjustAccountBalanceUseCase> adjustAccountBalanceUseCaseProvider) {
    this.accountDaoProvider = accountDaoProvider;
    this.adjustAccountBalanceUseCaseProvider = adjustAccountBalanceUseCaseProvider;
  }

  @Override
  public AccountsViewModel get() {
    return newInstance(accountDaoProvider.get(), adjustAccountBalanceUseCaseProvider.get());
  }

  public static AccountsViewModel_Factory create(Provider<AccountDao> accountDaoProvider,
      Provider<AdjustAccountBalanceUseCase> adjustAccountBalanceUseCaseProvider) {
    return new AccountsViewModel_Factory(accountDaoProvider, adjustAccountBalanceUseCaseProvider);
  }

  public static AccountsViewModel newInstance(AccountDao accountDao,
      AdjustAccountBalanceUseCase adjustAccountBalanceUseCase) {
    return new AccountsViewModel(accountDao, adjustAccountBalanceUseCase);
  }
}
