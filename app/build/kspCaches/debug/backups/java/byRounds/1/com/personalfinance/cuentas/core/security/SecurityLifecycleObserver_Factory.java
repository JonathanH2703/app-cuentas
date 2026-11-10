package com.personalfinance.cuentas.core.security;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SecurityLifecycleObserver_Factory implements Factory<SecurityLifecycleObserver> {
  private final Provider<SecurityPreferences> securityPreferencesProvider;

  public SecurityLifecycleObserver_Factory(
      Provider<SecurityPreferences> securityPreferencesProvider) {
    this.securityPreferencesProvider = securityPreferencesProvider;
  }

  @Override
  public SecurityLifecycleObserver get() {
    return newInstance(securityPreferencesProvider.get());
  }

  public static SecurityLifecycleObserver_Factory create(
      Provider<SecurityPreferences> securityPreferencesProvider) {
    return new SecurityLifecycleObserver_Factory(securityPreferencesProvider);
  }

  public static SecurityLifecycleObserver newInstance(SecurityPreferences securityPreferences) {
    return new SecurityLifecycleObserver(securityPreferences);
  }
}
