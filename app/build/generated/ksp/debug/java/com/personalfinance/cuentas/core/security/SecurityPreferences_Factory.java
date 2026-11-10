package com.personalfinance.cuentas.core.security;

import android.content.Context;
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
public final class SecurityPreferences_Factory implements Factory<SecurityPreferences> {
  private final Provider<Context> contextProvider;

  public SecurityPreferences_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SecurityPreferences get() {
    return newInstance(contextProvider.get());
  }

  public static SecurityPreferences_Factory create(Provider<Context> contextProvider) {
    return new SecurityPreferences_Factory(contextProvider);
  }

  public static SecurityPreferences newInstance(Context context) {
    return new SecurityPreferences(context);
  }
}
