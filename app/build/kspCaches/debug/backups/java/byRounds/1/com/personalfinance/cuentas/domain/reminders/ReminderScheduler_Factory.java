package com.personalfinance.cuentas.domain.reminders;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ReminderScheduler_Factory implements Factory<ReminderScheduler> {
  private final Provider<Context> contextProvider;

  private final Provider<ReminderPreferences> reminderPreferencesProvider;

  public ReminderScheduler_Factory(Provider<Context> contextProvider,
      Provider<ReminderPreferences> reminderPreferencesProvider) {
    this.contextProvider = contextProvider;
    this.reminderPreferencesProvider = reminderPreferencesProvider;
  }

  @Override
  public ReminderScheduler get() {
    return newInstance(contextProvider.get(), reminderPreferencesProvider.get());
  }

  public static ReminderScheduler_Factory create(Provider<Context> contextProvider,
      Provider<ReminderPreferences> reminderPreferencesProvider) {
    return new ReminderScheduler_Factory(contextProvider, reminderPreferencesProvider);
  }

  public static ReminderScheduler newInstance(Context context,
      ReminderPreferences reminderPreferences) {
    return new ReminderScheduler(context, reminderPreferences);
  }
}
