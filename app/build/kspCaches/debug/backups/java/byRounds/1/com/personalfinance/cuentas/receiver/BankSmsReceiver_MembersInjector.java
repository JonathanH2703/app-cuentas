package com.personalfinance.cuentas.receiver;

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
public final class BankSmsReceiver_MembersInjector implements MembersInjector<BankSmsReceiver> {
  private final Provider<ProcessBankCaptureUseCase> processBankCaptureUseCaseProvider;

  public BankSmsReceiver_MembersInjector(
      Provider<ProcessBankCaptureUseCase> processBankCaptureUseCaseProvider) {
    this.processBankCaptureUseCaseProvider = processBankCaptureUseCaseProvider;
  }

  public static MembersInjector<BankSmsReceiver> create(
      Provider<ProcessBankCaptureUseCase> processBankCaptureUseCaseProvider) {
    return new BankSmsReceiver_MembersInjector(processBankCaptureUseCaseProvider);
  }

  @Override
  public void injectMembers(BankSmsReceiver instance) {
    injectProcessBankCaptureUseCase(instance, processBankCaptureUseCaseProvider.get());
  }

  @InjectedFieldSignature("com.personalfinance.cuentas.receiver.BankSmsReceiver.processBankCaptureUseCase")
  public static void injectProcessBankCaptureUseCase(BankSmsReceiver instance,
      ProcessBankCaptureUseCase processBankCaptureUseCase) {
    instance.processBankCaptureUseCase = processBankCaptureUseCase;
  }
}
