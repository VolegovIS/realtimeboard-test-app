package org.home.realtimeboard.configuration;

import org.home.realtimeboard.store.AdaptedWidgetStore;
import org.home.realtimeboard.store.WidgetStore;
import org.home.realtimeboard.store.adapter.CombinedStoreAdapter;
import org.home.realtimeboard.store.adapter.InnerStoreAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {
    @Bean
    public InnerStoreAdapter storeAdapter() {
        return new CombinedStoreAdapter();
    }

    @Bean
    public WidgetStore widgetStore() {
        return new AdaptedWidgetStore(storeAdapter());
    }
}
