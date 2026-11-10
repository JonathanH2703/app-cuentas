package com.personalfinance.cuentas.di;

import com.personalfinance.cuentas.core.database.AppDatabase;
import com.personalfinance.cuentas.data.local.dao.BackupDao;
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
public final class DatabaseModule_ProvideBackupDaoFactory implements Factory<BackupDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideBackupDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BackupDao get() {
    return provideBackupDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideBackupDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideBackupDaoFactory(dbProvider);
  }

  public static BackupDao provideBackupDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBackupDao(db));
  }
}
