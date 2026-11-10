package com.personalfinance.cuentas.domain.backup;

import com.personalfinance.cuentas.core.database.AppDatabase;
import com.personalfinance.cuentas.data.local.dao.BackupDao;
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
public final class BackupManager_Factory implements Factory<BackupManager> {
  private final Provider<BackupDao> backupDaoProvider;

  private final Provider<AppDatabase> appDatabaseProvider;

  public BackupManager_Factory(Provider<BackupDao> backupDaoProvider,
      Provider<AppDatabase> appDatabaseProvider) {
    this.backupDaoProvider = backupDaoProvider;
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public BackupManager get() {
    return newInstance(backupDaoProvider.get(), appDatabaseProvider.get());
  }

  public static BackupManager_Factory create(Provider<BackupDao> backupDaoProvider,
      Provider<AppDatabase> appDatabaseProvider) {
    return new BackupManager_Factory(backupDaoProvider, appDatabaseProvider);
  }

  public static BackupManager newInstance(BackupDao backupDao, AppDatabase appDatabase) {
    return new BackupManager(backupDao, appDatabase);
  }
}
