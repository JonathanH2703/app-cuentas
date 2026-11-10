package com.personalfinance.cuentas.di;

import com.personalfinance.cuentas.core.database.AppDatabase;
import com.personalfinance.cuentas.data.local.dao.AllocationDao;
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
public final class DatabaseModule_ProvideAllocationDaoFactory implements Factory<AllocationDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideAllocationDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AllocationDao get() {
    return provideAllocationDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideAllocationDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideAllocationDaoFactory(dbProvider);
  }

  public static AllocationDao provideAllocationDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAllocationDao(db));
  }
}
