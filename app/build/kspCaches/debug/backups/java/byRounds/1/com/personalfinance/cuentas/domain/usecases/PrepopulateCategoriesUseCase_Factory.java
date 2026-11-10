package com.personalfinance.cuentas.domain.usecases;

import com.personalfinance.cuentas.data.local.dao.CategoryDao;
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
public final class PrepopulateCategoriesUseCase_Factory implements Factory<PrepopulateCategoriesUseCase> {
  private final Provider<CategoryDao> categoryDaoProvider;

  public PrepopulateCategoriesUseCase_Factory(Provider<CategoryDao> categoryDaoProvider) {
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public PrepopulateCategoriesUseCase get() {
    return newInstance(categoryDaoProvider.get());
  }

  public static PrepopulateCategoriesUseCase_Factory create(
      Provider<CategoryDao> categoryDaoProvider) {
    return new PrepopulateCategoriesUseCase_Factory(categoryDaoProvider);
  }

  public static PrepopulateCategoriesUseCase newInstance(CategoryDao categoryDao) {
    return new PrepopulateCategoriesUseCase(categoryDao);
  }
}
