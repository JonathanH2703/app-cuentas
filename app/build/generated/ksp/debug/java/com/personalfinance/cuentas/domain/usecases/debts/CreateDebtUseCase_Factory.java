package com.personalfinance.cuentas.domain.usecases.debts;

import com.personalfinance.cuentas.data.local.dao.DebtDao;
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
public final class CreateDebtUseCase_Factory implements Factory<CreateDebtUseCase> {
  private final Provider<DebtDao> debtDaoProvider;

  public CreateDebtUseCase_Factory(Provider<DebtDao> debtDaoProvider) {
    this.debtDaoProvider = debtDaoProvider;
  }

  @Override
  public CreateDebtUseCase get() {
    return newInstance(debtDaoProvider.get());
  }

  public static CreateDebtUseCase_Factory create(Provider<DebtDao> debtDaoProvider) {
    return new CreateDebtUseCase_Factory(debtDaoProvider);
  }

  public static CreateDebtUseCase newInstance(DebtDao debtDao) {
    return new CreateDebtUseCase(debtDao);
  }
}
