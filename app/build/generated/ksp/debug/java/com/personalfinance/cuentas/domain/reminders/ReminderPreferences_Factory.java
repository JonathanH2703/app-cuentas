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
public final class ReminderPreferences_Factory implements Factory<ReminderPreferences> {
  private final Provider<Context> contextProvider;

  public ReminderPreferences_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ReminderPreferences get() {
    return newInstance(contextProvider.get());
  }

  public static ReminderPreferences_Factory create(Provider<Context> contextProvider) {
    return new ReminderPreferences_Factory(contextProvider);
  }

  public static ReminderPreferences newInstance(Context context) {
    return new ReminderPreferences(context);
  }
}
