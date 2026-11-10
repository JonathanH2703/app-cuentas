package com.personalfinance.cuentas.service;

import com.personalfinance.cuentas.domain.usecases.capture.ProcessBankCaptureUseCase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class FinanceNotificationListenerService_MembersInjector implements MembersInjector<FinanceNotificationListenerService> {
  private final Provider<ProcessBankCaptureUseCase> processBankCaptureUseCaseProvider;

  public FinanceNotificationListenerService_MembersInjector(
      Provider<ProcessBankCaptureUseCase> processBankCaptureUseCaseProvider) {
    this.processBankCaptureUseCaseProvider = processBankCaptureUseCaseProvider;
  }

  public static MembersInjector<FinanceNotificationListenerService> create(
      Provider<ProcessBankCaptureUseCase> processBankCaptureUseCaseProvider) {
    return new FinanceNotificationListenerService_MembersInjector(processBankCaptureUseCaseProvider);
  }

  @Override
  public void injectMembers(FinanceNotificationListenerService instance) {
    injectProcessBankCaptureUseCase(instance, processBankCaptureUseCaseProvider.get());
  }

  @InjectedFieldSignature("com.personalfinance.cuentas.service.FinanceNotificationListenerService.processBankCaptureUseCase")
  public static void injectProcessBankCaptureUseCase(FinanceNotificationListenerService instance,
      ProcessBankCaptureUseCase processBankCaptureUseCase) {
    instance.processBankCaptureUseCase = processBankCaptureUseCase;
  }
}
