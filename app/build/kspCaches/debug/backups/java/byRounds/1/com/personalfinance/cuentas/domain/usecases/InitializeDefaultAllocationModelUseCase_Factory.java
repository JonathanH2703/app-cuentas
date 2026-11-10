package com.personalfinance.cuentas.domain.usecases;

import com.personalfinance.cuentas.data.local.dao.AllocationDao;
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
public final class InitializeDefaultAllocationModelUseCase_Factory implements Factory<InitializeDefaultAllocationModelUseCase> {
  private final Provider<AllocationDao> allocationDaoProvider;

  public InitializeDefaultAllocationModelUseCase_Factory(
      Provider<AllocationDao> allocationDaoProvider) {
    this.allocationDaoProvider = allocationDaoProvider;
  }

  @Override
  public InitializeDefaultAllocationModelUseCase get() {
    return newInstance(allocationDaoProvider.get());
  }

  public static InitializeDefaultAllocationModelUseCase_Factory create(
      Provider<AllocationDao> allocationDaoProvider) {
    return new InitializeDefaultAllocationModelUseCase_Factory(allocationDaoProvider);
  }

  public static InitializeDefaultAllocationModelUseCase newInstance(AllocationDao allocationDao) {
    return new InitializeDefaultAllocationModelUseCase(allocationDao);
  }
}
