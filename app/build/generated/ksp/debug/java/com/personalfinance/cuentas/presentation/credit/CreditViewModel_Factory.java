package com.personalfinance.cuentas.presentation.credit;

import com.personalfinance.cuentas.data.local.dao.AccountDao;
import com.personalfinance.cuentas.data.local.dao.CategoryDao;
import com.personalfinance.cuentas.data.local.dao.CreditCardDao;
import com.personalfinance.cuentas.data.local.dao.DebtDao;
import com.personalfinance.cuentas.domain.usecases.creditcards.CreateCreditCardUseCase;
import com.personalfinance.cuentas.domain.usecases.creditcards.PayCreditCardUseCase;
import com.personalfinance.cuentas.domain.usecases.creditcards.RecordCardPurchaseUseCase;
import com.personalfinance.cuentas.domain.usecases.debts.CreateDebtUseCase;
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
public final class CreditViewModel_Factory implements Factory<CreditViewModel> {
  private final Provider<CreditCardDao> creditCardDaoProvider;

  private final Provider<DebtDao> debtDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<CreateCreditCardUseCase> createCreditCardUseCaseProvider;

  private final Provider<RecordCardPurchaseUseCase> recordCardPurchaseUseCaseProvider;

  private final Provider<PayCreditCardUseCase> payCreditCardUseCaseProvider;

  private final Provider<CreateDebtUseCase> createDebtUseCaseProvider;

  private final Provider<RecordDebtPaymentUseCase> recordDebtPaymentUseCaseProvider;

  public CreditViewModel_Factory(Provider<CreditCardDao> creditCardDaoProvider,
      Provider<DebtDao> debtDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<AccountDao> accountDaoProvider,
      Provider<CreateCreditCardUseCase> createCreditCardUseCaseProvider,
      Provider<RecordCardPurchaseUseCase> recordCardPurchaseUseCaseProvider,
      Provider<PayCreditCardUseCase> payCreditCardUseCaseProvider,
      Provider<CreateDebtUseCase> createDebtUseCaseProvider,
      Provider<RecordDebtPaymentUseCase> recordDebtPaymentUseCaseProvider) {
    this.creditCardDaoProvider = creditCardDaoProvider;
    this.debtDaoProvider = debtDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.accountDaoProvider = accountDaoProvider;
    this.createCreditCardUseCaseProvider = createCreditCardUseCaseProvider;
    this.recordCardPurchaseUseCaseProvider = recordCardPurchaseUseCaseProvider;
    this.payCreditCardUseCaseProvider = payCreditCardUseCaseProvider;
    this.createDebtUseCaseProvider = createDebtUseCaseProvider;
    this.recordDebtPaymentUseCaseProvider = recordDebtPaymentUseCaseProvider;
  }

  @Override
  public CreditViewModel get() {
    return newInstance(creditCardDaoProvider.get(), debtDaoProvider.get(), categoryDaoProvider.get(), accountDaoProvider.get(), createCreditCardUseCaseProvider.get(), recordCardPurchaseUseCaseProvider.get(), payCreditCardUseCaseProvider.get(), createDebtUseCaseProvider.get(), recordDebtPaymentUseCaseProvider.get());
  }

  public static CreditViewModel_Factory create(Provider<CreditCardDao> creditCardDaoProvider,
      Provider<DebtDao> debtDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<AccountDao> accountDaoProvider,
      Provider<CreateCreditCardUseCase> createCreditCardUseCaseProvider,
      Provider<RecordCardPurchaseUseCase> recordCardPurchaseUseCaseProvider,
      Provider<PayCreditCardUseCase> payCreditCardUseCaseProvider,
      Provider<CreateDebtUseCase> createDebtUseCaseProvider,
      Provider<RecordDebtPaymentUseCase> recordDebtPaymentUseCaseProvider) {
    return new CreditViewModel_Factory(creditCardDaoProvider, debtDaoProvider, categoryDaoProvider, accountDaoProvider, createCreditCardUseCaseProvider, recordCardPurchaseUseCaseProvider, payCreditCardUseCaseProvider, createDebtUseCaseProvider, recordDebtPaymentUseCaseProvider);
  }

  public static CreditViewModel newInstance(CreditCardDao creditCardDao, DebtDao debtDao,
      CategoryDao categoryDao, AccountDao accountDao,
      CreateCreditCardUseCase createCreditCardUseCase,
      RecordCardPurchaseUseCase recordCardPurchaseUseCase,
      PayCreditCardUseCase payCreditCardUseCase, CreateDebtUseCase createDebtUseCase,
      RecordDebtPaymentUseCase recordDebtPaymentUseCase) {
    return new CreditViewModel(creditCardDao, debtDao, categoryDao, accountDao, createCreditCardUseCase, recordCardPurchaseUseCase, payCreditCardUseCase, createDebtUseCase, recordDebtPaymentUseCase);
  }
}
