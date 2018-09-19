package org.home.realtimeboard.store.adapter;

import lombok.Getter;
import org.testng.annotations.BeforeMethod;

/**
 * Тесты для {@link SortedSetStoreAdapter}
 */
public class SortedSetStoreAdapterTests extends AbstractStoreAdapterTests {
    @Getter
    private SortedSetStoreAdapter storeAdapter;

    @BeforeMethod
    public void setUp() {
        storeAdapter = new SortedSetStoreAdapter();
    }
}
