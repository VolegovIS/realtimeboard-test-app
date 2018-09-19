package org.home.realtimeboard.store;

import lombok.Getter;
import org.home.realtimeboard.store.adapter.CombinedStoreAdapter;
import org.testng.annotations.BeforeMethod;

/**
 * Тесты для {@link AdaptedWidgetStore}
 */
public class AdaptedWidgetStoreTests extends AbstractWidgetStoreTests {
    @Getter
    private AdaptedWidgetStore widgetStore;

    @BeforeMethod
    public void setUp() {
        widgetStore = new AdaptedWidgetStore(new CombinedStoreAdapter());
    }
}
