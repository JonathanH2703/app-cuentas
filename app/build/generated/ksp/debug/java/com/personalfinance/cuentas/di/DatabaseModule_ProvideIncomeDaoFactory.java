package com.personalfinance.cuentas.di;

import com.personalfinance.cuentas.core.database.AppDatabase;
import com.personalfinance.cuentas.data.local.dao.IncomeDao;
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
public final class DatabaseModule_ProvideIncomeDaoFactory implements Factory<IncomeDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideIncomeDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public IncomeDao get() {
    return provideIncomeDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideIncomeDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideIncomeDaoFactory(dbProvider);
  }

  public static IncomeDao provideIncomeDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideIncomeDao(db));
  }
}
