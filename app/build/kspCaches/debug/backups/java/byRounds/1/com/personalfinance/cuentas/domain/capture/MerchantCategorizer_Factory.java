package com.personalfinance.cuentas.domain.capture;

import com.personalfinance.cuentas.data.local.dao.CategoryDao;
import com.personalfinance.cuentas.data.local.dao.MerchantLearningDao;
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
public final class MerchantCategorizer_Factory implements Factory<MerchantCategorizer> {
  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<MerchantLearningDao> merchantLearningDaoProvider;

  public MerchantCategorizer_Factory(Provider<CategoryDao> categoryDaoProvider,
      Provider<MerchantLearningDao> merchantLearningDaoProvider) {
    this.categoryDaoProvider = categoryDaoProvider;
    this.merchantLearningDaoProvider = merchantLearningDaoProvider;
  }

  @Override
  public MerchantCategorizer get() {
    return newInstance(categoryDaoProvider.get(), merchantLearningDaoProvider.get());
  }

  public static MerchantCategorizer_Factory create(Provider<CategoryDao> categoryDaoProvider,
      Provider<MerchantLearningDao> merchantLearningDaoProvider) {
    return new MerchantCategorizer_Factory(categoryDaoProvider, merchantLearningDaoProvider);
  }

  public static MerchantCategorizer newInstance(CategoryDao categoryDao,
      MerchantLearningDao merchantLearningDao) {
    return new MerchantCategorizer(categoryDao, merchantLearningDao);
  }
}
