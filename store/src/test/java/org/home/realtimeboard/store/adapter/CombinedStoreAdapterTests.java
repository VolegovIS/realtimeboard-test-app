package org.home.realtimeboard.store.adapter;

import lombok.Getter;
import org.testng.annotations.BeforeMethod;

/**
 * Тесты для {@link CombinedStoreAdapter}
 */
public class CombinedStoreAdapterTests extends AbstractStoreAdapterTests {
    @Getter
    private CombinedStoreAdapter storeAdapter;

    @BeforeMethod
    public void setUp() {
        storeAdapter = new CombinedStoreAdapter();
    }
}
