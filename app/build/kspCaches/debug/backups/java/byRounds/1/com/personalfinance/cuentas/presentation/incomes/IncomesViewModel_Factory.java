package com.personalfinance.cuentas.presentation.incomes;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.AllocationDao;
import com.personalfinance.cuentas.data.local.dao.DebtDao;
import com.personalfinance.cuentas.data.local.dao.IncomeDao;
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao;
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
public final class IncomesViewModel_Factory implements Factory<IncomesViewModel> {
  private final Provider<IncomeDao> incomeDaoProvider;

  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<DebtDao> debtDaoProvider;

  private final Provider<RecurringItemDao> recurringItemDaoProvider;

  private final Provider<AllocationDao> allocationDaoProvider;

  private final Provider<ConfirmIncomeUseCase> confirmIncomeUseCaseProvider;

  public IncomesViewModel_Factory(Provider<IncomeDao> incomeDaoProvider,
      Provider<AccountDao> accountDaoProvider, Provider<DebtDao> debtDaoProvider,
      Provider<RecurringItemDao> recurringItemDaoProvider,
      Provider<AllocationDao> allocationDaoProvider,
      Provider<ConfirmIncomeUseCase> confirmIncomeUseCaseProvider) {
    this.incomeDaoProvider = incomeDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
    this.debtDaoProvider = debtDaoProvider;
    this.recurringItemDaoProvider = recurringItemDaoProvider;
    this.allocationDaoProvider = allocationDaoProvider;
    this.confirmIncomeUseCaseProvider = confirmIncomeUseCaseProvider;
  }

  @Override
  public IncomesViewModel get() {
    return newInstance(incomeDaoProvider.get(), accountDaoProvider.get(), debtDaoProvider.get(), recurringItemDaoProvider.get(), allocationDaoProvider.get(), confirmIncomeUseCaseProvider.get());
  }

  public static IncomesViewModel_Factory create(Provider<IncomeDao> incomeDaoProvider,
      Provider<AccountDao> accountDaoProvider, Provider<DebtDao> debtDaoProvider,
      Provider<RecurringItemDao> recurringItemDaoProvider,
      Provider<AllocationDao> allocationDaoProvider,
      Provider<ConfirmIncomeUseCase> confirmIncomeUseCaseProvider) {
    return new IncomesViewModel_Factory(incomeDaoProvider, accountDaoProvider, debtDaoProvider, recurringItemDaoProvider, allocationDaoProvider, confirmIncomeUseCaseProvider);
  }

  public static IncomesViewModel newInstance(IncomeDao incomeDao, AccountDao accountDao,
      DebtDao debtDao, RecurringItemDao recurringItemDao, AllocationDao allocationDao,
      ConfirmIncomeUseCase confirmIncomeUseCase) {
    return new IncomesViewModel(incomeDao, accountDao, debtDao, recurringItemDao, allocationDao, confirmIncomeUseCase);
  }
}
