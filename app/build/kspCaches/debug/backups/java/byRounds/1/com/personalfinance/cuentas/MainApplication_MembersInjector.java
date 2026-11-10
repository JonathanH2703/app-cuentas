package com.personalfinance.cuentas;

import com.personalfinance.cuentas.core.security.SecurityLifecycleObserver;
import com.personalfinance.cuentas.domain.reminders.ReminderScheduler;
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
public final class MainApplication_MembersInjector implements MembersInjector<MainApplication> {
  private final Provider<SecurityLifecycleObserver> securityLifecycleObserverProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  public MainApplication_MembersInjector(
      Provider<SecurityLifecycleObserver> securityLifecycleObserverProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    this.securityLifecycleObserverProvider = securityLifecycleObserverProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
  }

  public static MembersInjector<MainApplication> create(
      Provider<SecurityLifecycleObserver> securityLifecycleObserverProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    return new MainApplication_MembersInjector(securityLifecycleObserverProvider, reminderSchedulerProvider);
  }

  @Override
  public void injectMembers(MainApplication instance) {
    injectSecurityLifecycleObserver(instance, securityLifecycleObserverProvider.get());
    injectReminderScheduler(instance, reminderSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.personalfinance.cuentas.MainApplication.securityLifecycleObserver")
  public static void injectSecurityLifecycleObserver(MainApplication instance,
      SecurityLifecycleObserver securityLifecycleObserver) {
    instance.securityLifecycleObserver = securityLifecycleObserver;
  }

  @InjectedFieldSignature("com.personalfinance.cuentas.MainApplication.reminderScheduler")
  public static void injectReminderScheduler(MainApplication instance,
      ReminderScheduler reminderScheduler) {
    instance.reminderScheduler = reminderScheduler;
  }
}
