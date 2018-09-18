package org.home.realtimeboard.store.adapter;

import lombok.Getter;
import org.testng.annotations.BeforeMethod;

public class CombinedStoreAdapterTests extends AbstractStoreAdapterTests {
    @Getter
    private CombinedStoreAdapter storeAdapter;

    @BeforeMethod
    public void setUp() {
        storeAdapter = new CombinedStoreAdapter();
    }
}
