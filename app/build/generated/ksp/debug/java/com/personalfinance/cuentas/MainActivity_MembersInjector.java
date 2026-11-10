package com.personalfinance.cuentas;

import com.personalfinance.cuentas.core.security.BiometricAuthManager;
import com.personalfinance.cuentas.core.security.SecurityLifecycleObserver;
import com.personalfinance.cuentas.core.security.SecurityPreferences;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<BiometricAuthManager> biometricAuthManagerProvider;

  private final Provider<SecurityLifecycleObserver> securityLifecycleObserverProvider;

  private final Provider<SecurityPreferences> securityPreferencesProvider;

  public MainActivity_MembersInjector(Provider<BiometricAuthManager> biometricAuthManagerProvider,
      Provider<SecurityLifecycleObserver> securityLifecycleObserverProvider,
      Provider<SecurityPreferences> securityPreferencesProvider) {
    this.biometricAuthManagerProvider = biometricAuthManagerProvider;
    this.securityLifecycleObserverProvider = securityLifecycleObserverProvider;
    this.securityPreferencesProvider = securityPreferencesProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<BiometricAuthManager> biometricAuthManagerProvider,
      Provider<SecurityLifecycleObserver> securityLifecycleObserverProvider,
      Provider<SecurityPreferences> securityPreferencesProvider) {
    return new MainActivity_MembersInjector(biometricAuthManagerProvider, securityLifecycleObserverProvider, securityPreferencesProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectBiometricAuthManager(instance, biometricAuthManagerProvider.get());
    injectSecurityLifecycleObserver(instance, securityLifecycleObserverProvider.get());
    injectSecurityPreferences(instance, securityPreferencesProvider.get());
  }

  @InjectedFieldSignature("com.personalfinance.cuentas.MainActivity.biometricAuthManager")
  public static void injectBiometricAuthManager(MainActivity instance,
      BiometricAuthManager biometricAuthManager) {
    instance.biometricAuthManager = biometricAuthManager;
  }

  @InjectedFieldSignature("com.personalfinance.cuentas.MainActivity.securityLifecycleObserver")
  public static void injectSecurityLifecycleObserver(MainActivity instance,
      SecurityLifecycleObserver securityLifecycleObserver) {
    instance.securityLifecycleObserver = securityLifecycleObserver;
  }

  @InjectedFieldSignature("com.personalfinance.cuentas.MainActivity.securityPreferences")
  public static void injectSecurityPreferences(MainActivity instance,
      SecurityPreferences securityPreferences) {
    instance.securityPreferences = securityPreferences;
  }
}
