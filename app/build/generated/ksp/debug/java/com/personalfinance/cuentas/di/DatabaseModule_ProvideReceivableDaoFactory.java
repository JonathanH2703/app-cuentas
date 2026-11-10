package com.personalfinance.cuentas.di;

import com.personalfinance.cuentas.core.database.AppDatabase;
import com.personalfinance.cuentas.data.local.dao.ReceivableDao;
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
public final class DatabaseModule_ProvideReceivableDaoFactory implements Factory<ReceivableDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideReceivableDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ReceivableDao get() {
    return provideReceivableDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideReceivableDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideReceivableDaoFactory(dbProvider);
  }

  public static ReceivableDao provideReceivableDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideReceivableDao(db));
  }
}
