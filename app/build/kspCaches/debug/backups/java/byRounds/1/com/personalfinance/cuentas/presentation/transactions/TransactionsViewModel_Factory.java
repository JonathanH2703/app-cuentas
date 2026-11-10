package com.personalfinance.cuentas.presentation.transactions;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.AllocationDao;
import com.personalfinance.cuentas.data.local.dao.CategoryDao;
import com.personalfinance.cuentas.data.local.dao.CreditCardDao;
import com.personalfinance.cuentas.data.local.dao.DebtDao;
import com.personalfinance.cuentas.data.local.dao.PendingTransactionDao;
import com.personalfinance.cuentas.data.local.dao.RecurringItemDao;
import com.personalfinance.cuentas.data.local.dao.TransactionDao;
import com.personalfinance.cuentas.domain.usecases.AnchorTransactionUseCase;
import com.personalfinance.cuentas.domain.usecases.PrepopulateCategoriesUseCase;
import com.personalfinance.cuentas.domain.usecases.RecordExpenseUseCase;
import com.personalfinance.cuentas.domain.usecases.capture.ProcessBankCaptureUseCase;
import com.personalfinance.cuentas.domain.usecases.creditcards.PayCreditCardUseCase;
import com.personalfinance.cuentas.domain.usecases.debts.RecordDebtPaymentUseCase;
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
public final class TransactionsViewModel_Factory implements Factory<TransactionsViewModel> {
  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<AllocationDao> allocationDaoProvider;

  private final Provider<DebtDao> debtDaoProvider;

  private final Provider<RecurringItemDao> recurringItemDaoProvider;

  private final Provider<PendingTransactionDao> pendingTransactionDaoProvider;

  private final Provider<CreditCardDao> creditCardDaoProvider;

  private final Provider<RecordExpenseUseCase> recordExpenseUseCaseProvider;

  private final Provider<AnchorTransactionUseCase> anchorTransactionUseCaseProvider;

  private final Provider<PrepopulateCategoriesUseCase> prepopulateCategoriesUseCaseProvider;

  private final Provider<ProcessBankCaptureUseCase> processBankCaptureUseCaseProvider;

  private final Provider<PayCreditCardUseCase> payCreditCardUseCaseProvider;

  private final Provider<RecordDebtPaymentUseCase> recordDebtPaymentUseCaseProvider;

  public TransactionsViewModel_Factory(Provider<TransactionDao> transactionDaoProvider,
      Provider<AccountDao> accountDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<AllocationDao> allocationDaoProvider, Provider<DebtDao> debtDaoProvider,
      Provider<RecurringItemDao> recurringItemDaoProvider,
      Provider<PendingTransactionDao> pendingTransactionDaoProvider,
      Provider<CreditCardDao> creditCardDaoProvider,
      Provider<RecordExpenseUseCase> recordExpenseUseCaseProvider,
      Provider<AnchorTransactionUseCase> anchorTransactionUseCaseProvider,
      Provider<PrepopulateCategoriesUseCase> prepopulateCategoriesUseCaseProvider,
      Provider<ProcessBankCaptureUseCase> processBankCaptureUseCaseProvider,
      Provider<PayCreditCardUseCase> payCreditCardUseCaseProvider,
      Provider<RecordDebtPaymentUseCase> recordDebtPaymentUseCaseProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.allocationDaoProvider = allocationDaoProvider;
    this.debtDaoProvider = debtDaoProvider;
    this.recurringItemDaoProvider = recurringItemDaoProvider;
    this.pendingTransactionDaoProvider = pendingTransactionDaoProvider;
    this.creditCardDaoProvider = creditCardDaoProvider;
    this.recordExpenseUseCaseProvider = recordExpenseUseCaseProvider;
    this.anchorTransactionUseCaseProvider = anchorTransactionUseCaseProvider;
    this.prepopulateCategoriesUseCaseProvider = prepopulateCategoriesUseCaseProvider;
    this.processBankCaptureUseCaseProvider = processBankCaptureUseCaseProvider;
    this.payCreditCardUseCaseProvider = payCreditCardUseCaseProvider;
    this.recordDebtPaymentUseCaseProvider = recordDebtPaymentUseCaseProvider;
  }

  @Override
  public TransactionsViewModel get() {
    return newInstance(transactionDaoProvider.get(), accountDaoProvider.get(), categoryDaoProvider.get(), allocationDaoProvider.get(), debtDaoProvider.get(), recurringItemDaoProvider.get(), pendingTransactionDaoProvider.get(), creditCardDaoProvider.get(), recordExpenseUseCaseProvider.get(), anchorTransactionUseCaseProvider.get(), prepopulateCategoriesUseCaseProvider.get(), processBankCaptureUseCaseProvider.get(), payCreditCardUseCaseProvider.get(), recordDebtPaymentUseCaseProvider.get());
  }

  public static TransactionsViewModel_Factory create(
      Provider<TransactionDao> transactionDaoProvider, Provider<AccountDao> accountDaoProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<AllocationDao> allocationDaoProvider,
      Provider<DebtDao> debtDaoProvider, Provider<RecurringItemDao> recurringItemDaoProvider,
      Provider<PendingTransactionDao> pendingTransactionDaoProvider,
      Provider<CreditCardDao> creditCardDaoProvider,
      Provider<RecordExpenseUseCase> recordExpenseUseCaseProvider,
      Provider<AnchorTransactionUseCase> anchorTransactionUseCaseProvider,
      Provider<PrepopulateCategoriesUseCase> prepopulateCategoriesUseCaseProvider,
      Provider<ProcessBankCaptureUseCase> processBankCaptureUseCaseProvider,
      Provider<PayCreditCardUseCase> payCreditCardUseCaseProvider,
      Provider<RecordDebtPaymentUseCase> recordDebtPaymentUseCaseProvider) {
    return new TransactionsViewModel_Factory(transactionDaoProvider, accountDaoProvider, categoryDaoProvider, allocationDaoProvider, debtDaoProvider, recurringItemDaoProvider, pendingTransactionDaoProvider, creditCardDaoProvider, recordExpenseUseCaseProvider, anchorTransactionUseCaseProvider, prepopulateCategoriesUseCaseProvider, processBankCaptureUseCaseProvider, payCreditCardUseCaseProvider, recordDebtPaymentUseCaseProvider);
  }

  public static TransactionsViewModel newInstance(TransactionDao transactionDao,
      AccountDao accountDao, CategoryDao categoryDao, AllocationDao allocationDao, DebtDao debtDao,
      RecurringItemDao recurringItemDao, PendingTransactionDao pendingTransactionDao,
      CreditCardDao creditCardDao, RecordExpenseUseCase recordExpenseUseCase,
      AnchorTransactionUseCase anchorTransactionUseCase,
      PrepopulateCategoriesUseCase prepopulateCategoriesUseCase,
      ProcessBankCaptureUseCase processBankCaptureUseCase,
      PayCreditCardUseCase payCreditCardUseCase,
      RecordDebtPaymentUseCase recordDebtPaymentUseCase) {
    return new TransactionsViewModel(transactionDao, accountDao, categoryDao, allocationDao, debtDao, recurringItemDao, pendingTransactionDao, creditCardDao, recordExpenseUseCase, anchorTransactionUseCase, prepopulateCategoriesUseCase, processBankCaptureUseCase, payCreditCardUseCase, recordDebtPaymentUseCase);
  }
}
