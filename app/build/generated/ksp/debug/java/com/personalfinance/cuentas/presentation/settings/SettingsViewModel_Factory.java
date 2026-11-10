package com.personalfinance.cuentas.presentation.settings;

import android.content.Context;
import com.personalfinance.cuentas.core.security.SecurityPreferences;
import com.personalfinance.cuentas.domain.backup.BackupManager;
import com.personalfinance.cuentas.domain.reminders.ReminderPreferences;
import com.personalfinance.cuentas.domain.reminders.ReminderScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<SecurityPreferences> securityPreferencesProvider;

  private final Provider<ReminderPreferences> reminderPreferencesProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  private final Provider<BackupManager> backupManagerProvider;

  public SettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<SecurityPreferences> securityPreferencesProvider,
      Provider<ReminderPreferences> reminderPreferencesProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider,
      Provider<BackupManager> backupManagerProvider) {
    this.contextProvider = contextProvider;
    this.securityPreferencesProvider = securityPreferencesProvider;
    this.reminderPreferencesProvider = reminderPreferencesProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
    this.backupManagerProvider = backupManagerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(contextProvider.get(), securityPreferencesProvider.get(), reminderPreferencesProvider.get(), reminderSchedulerProvider.get(), backupManagerProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<SecurityPreferences> securityPreferencesProvider,
      Provider<ReminderPreferences> reminderPreferencesProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider,
      Provider<BackupManager> backupManagerProvider) {
    return new SettingsViewModel_Factory(contextProvider, securityPreferencesProvider, reminderPreferencesProvider, reminderSchedulerProvider, backupManagerProvider);
  }

  public static SettingsViewModel newInstance(Context context,
      SecurityPreferences securityPreferences, ReminderPreferences reminderPreferences,
      ReminderScheduler reminderScheduler, BackupManager backupManager) {
    return new SettingsViewModel(context, securityPreferences, reminderPreferences, reminderScheduler, backupManager);
  }
}
