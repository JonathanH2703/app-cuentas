package com.personalfinance.cuentas.di;

import com.personalfinance.cuentas.core.database.AppDatabase;
import com.personalfinance.cuentas.data.local.dao.DebtDao;
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
public final class DatabaseModule_ProvideDebtDaoFactory implements Factory<DebtDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideDebtDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DebtDao get() {
    return provideDebtDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideDebtDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideDebtDaoFactory(dbProvider);
  }

  public static DebtDao provideDebtDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDebtDao(db));
  }
}
