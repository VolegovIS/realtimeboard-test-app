package org.home.realtimeboard.store.adapter;

import lombok.Getter;
import org.testng.annotations.BeforeMethod;

public class SortedSetStoreAdapterTests extends AbstractStoreAdapterTests {
    @Getter
    private SortedSetStoreAdapter storeAdapter;

    @BeforeMethod
    public void setUp() {
        storeAdapter = new SortedSetStoreAdapter();
    }
}
