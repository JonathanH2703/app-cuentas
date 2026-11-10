package com.personalfinance.cuentas.presentation.dashboard;

import com.personalfinance.cuentas.core.security.SecurityLifecycleObserver;
import com.personalfinance.cuentas.core.security.SecurityPreferences;
import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.AllocationDao;
import com.personalfinance.cuentas.data.local.dao.CategoryDao;
import com.personalfinance.cuentas.data.local.dao.CreditCardDao;
import com.personalfinance.cuentas.data.local.dao.DebtDao;
import com.personalfinance.cuentas.data.local.dao.EventDao;
import com.personalfinance.cuentas.data.local.dao.IncomeDao;
import com.personalfinance.cuentas.data.local.dao.ReceivableDao;
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao;
import com.personalfinance.cuentas.data.local.dao.TransactionDao;
import com.personalfinance.cuentas.domain.usecases.AnchorTransactionUseCase;
import com.personalfinance.cuentas.domain.usecases.InitializeDefaultAllocationModelUseCase;
import com.personalfinance.cuentas.domain.usecases.RecordExpenseUseCase;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<AllocationDao> allocationDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<IncomeDao> incomeDaoProvider;

  private final Provider<DebtDao> debtDaoProvider;

  private final Provider<RecurringItemDao> recurringItemDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<CreditCardDao> creditCardDaoProvider;

  private final Provider<ReceivableDao> receivableDaoProvider;

  private final Provider<EventDao> eventDaoProvider;

  private final Provider<RecordExpenseUseCase> recordExpenseUseCaseProvider;

  private final Provider<AnchorTransactionUseCase> anchorTransactionUseCaseProvider;

  private final Provider<SecurityLifecycleObserver> securityLifecycleObserverProvider;

  private final Provider<SecurityPreferences> securityPreferencesProvider;

  private final Provider<InitializeDefaultAllocationModelUseCase> initializeDefaultAllocationModelUseCaseProvider;

  public DashboardViewModel_Factory(Provider<AccountDao> accountDaoProvider,
      Provider<AllocationDao> allocationDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<IncomeDao> incomeDaoProvider,
      Provider<DebtDao> debtDaoProvider, Provider<RecurringItemDao> recurringItemDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<CreditCardDao> creditCardDaoProvider,
      Provider<ReceivableDao> receivableDaoProvider, Provider<EventDao> eventDaoProvider,
      Provider<RecordExpenseUseCase> recordExpenseUseCaseProvider,
      Provider<AnchorTransactionUseCase> anchorTransactionUseCaseProvider,
      Provider<SecurityLifecycleObserver> securityLifecycleObserverProvider,
      Provider<SecurityPreferences> securityPreferencesProvider,
      Provider<InitializeDefaultAllocationModelUseCase> initializeDefaultAllocationModelUseCaseProvider) {
    this.accountDaoProvider = accountDaoProvider;
    this.allocationDaoProvider = allocationDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.incomeDaoProvider = incomeDaoProvider;
    this.debtDaoProvider = debtDaoProvider;
    this.recurringItemDaoProvider = recurringItemDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.creditCardDaoProvider = creditCardDaoProvider;
    this.receivableDaoProvider = receivableDaoProvider;
    this.eventDaoProvider = eventDaoProvider;
    this.recordExpenseUseCaseProvider = recordExpenseUseCaseProvider;
    this.anchorTransactionUseCaseProvider = anchorTransactionUseCaseProvider;
    this.securityLifecycleObserverProvider = securityLifecycleObserverProvider;
    this.securityPreferencesProvider = securityPreferencesProvider;
    this.initializeDefaultAllocationModelUseCaseProvider = initializeDefaultAllocationModelUseCaseProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(accountDaoProvider.get(), allocationDaoProvider.get(), transactionDaoProvider.get(), incomeDaoProvider.get(), debtDaoProvider.get(), recurringItemDaoProvider.get(), categoryDaoProvider.get(), creditCardDaoProvider.get(), receivableDaoProvider.get(), eventDaoProvider.get(), recordExpenseUseCaseProvider.get(), anchorTransactionUseCaseProvider.get(), securityLifecycleObserverProvider.get(), securityPreferencesProvider.get(), initializeDefaultAllocationModelUseCaseProvider.get());
  }

  public static DashboardViewModel_Factory create(Provider<AccountDao> accountDaoProvider,
      Provider<AllocationDao> allocationDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<IncomeDao> incomeDaoProvider,
      Provider<DebtDao> debtDaoProvider, Provider<RecurringItemDao> recurringItemDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<CreditCardDao> creditCardDaoProvider,
      Provider<ReceivableDao> receivableDaoProvider, Provider<EventDao> eventDaoProvider,
      Provider<RecordExpenseUseCase> recordExpenseUseCaseProvider,
      Provider<AnchorTransactionUseCase> anchorTransactionUseCaseProvider,
      Provider<SecurityLifecycleObserver> securityLifecycleObserverProvider,
      Provider<SecurityPreferences> securityPreferencesProvider,
      Provider<InitializeDefaultAllocationModelUseCase> initializeDefaultAllocationModelUseCaseProvider) {
    return new DashboardViewModel_Factory(accountDaoProvider, allocationDaoProvider, transactionDaoProvider, incomeDaoProvider, debtDaoProvider, recurringItemDaoProvider, categoryDaoProvider, creditCardDaoProvider, receivableDaoProvider, eventDaoProvider, recordExpenseUseCaseProvider, anchorTransactionUseCaseProvider, securityLifecycleObserverProvider, securityPreferencesProvider, initializeDefaultAllocationModelUseCaseProvider);
  }

  public static DashboardViewModel newInstance(AccountDao accountDao, AllocationDao allocationDao,
      TransactionDao transactionDao, IncomeDao incomeDao, DebtDao debtDao,
      RecurringItemDao recurringItemDao, CategoryDao categoryDao, CreditCardDao creditCardDao,
      ReceivableDao receivableDao, EventDao eventDao, RecordExpenseUseCase recordExpenseUseCase,
      AnchorTransactionUseCase anchorTransactionUseCase,
      SecurityLifecycleObserver securityLifecycleObserver, SecurityPreferences securityPreferences,
      InitializeDefaultAllocationModelUseCase initializeDefaultAllocationModelUseCase) {
    return new DashboardViewModel(accountDao, allocationDao, transactionDao, incomeDao, debtDao, recurringItemDao, categoryDao, creditCardDao, receivableDao, eventDao, recordExpenseUseCase, anchorTransactionUseCase, securityLifecycleObserver, securityPreferences, initializeDefaultAllocationModelUseCase);
  }
}
