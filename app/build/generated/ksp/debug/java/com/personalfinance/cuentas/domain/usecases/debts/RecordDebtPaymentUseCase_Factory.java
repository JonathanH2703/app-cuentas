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
public final class RecordDebtPaymentUseCase_Factory implements Factory<RecordDebtPaymentUseCase> {
  private final Provider<DebtDao> debtDaoProvider;

  public RecordDebtPaymentUseCase_Factory(Provider<DebtDao> debtDaoProvider) {
    this.debtDaoProvider = debtDaoProvider;
  }

  @Override
  public RecordDebtPaymentUseCase get() {
    return newInstance(debtDaoProvider.get());
  }

  public static RecordDebtPaymentUseCase_Factory create(Provider<DebtDao> debtDaoProvider) {
    return new RecordDebtPaymentUseCase_Factory(debtDaoProvider);
  }

  public static RecordDebtPaymentUseCase newInstance(DebtDao debtDao) {
    return new RecordDebtPaymentUseCase(debtDao);
  }
}
