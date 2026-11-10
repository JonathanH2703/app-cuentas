package com.personalfinance.cuentas.di;

import com.personalfinance.cuentas.core.database.AppDatabase;
import com.personalfinance.cuentas.data.local.dao.CreditCardDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideCreditCardDaoFactory implements Factory<CreditCardDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideCreditCardDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CreditCardDao get() {
    return provideCreditCardDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideCreditCardDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideCreditCardDaoFactory(dbProvider);
  }

  public static CreditCardDao provideCreditCardDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCreditCardDao(db));
  }
}
