package com.personalfinance.cuentas.presentation.events;

import com.personalfinance.cuentas.data.local.dao.EventDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class EventsViewModel_Factory implements Factory<EventsViewModel> {
  private final Provider<EventDao> eventDaoProvider;

  public EventsViewModel_Factory(Provider<EventDao> eventDaoProvider) {
    this.eventDaoProvider = eventDaoProvider;
  }

  @Override
  public EventsViewModel get() {
    return newInstance(eventDaoProvider.get());
  }

  public static EventsViewModel_Factory create(Provider<EventDao> eventDaoProvider) {
    return new EventsViewModel_Factory(eventDaoProvider);
  }

  public static EventsViewModel newInstance(EventDao eventDao) {
    return new EventsViewModel(eventDao);
  }
}
