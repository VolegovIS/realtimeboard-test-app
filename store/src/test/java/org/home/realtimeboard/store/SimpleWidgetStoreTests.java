package org.home.realtimeboard.store;

import lombok.Getter;
import org.testng.annotations.BeforeMethod;

/**
 * Тесты для {@link SimpleWidgetStore}
 */
public class SimpleWidgetStoreTests extends AbstractWidgetStoreTests {
    @Getter
    private SimpleWidgetStore widgetStore;

    @BeforeMethod
    public void setUp() {
        widgetStore = new SimpleWidgetStore();
    }
}
