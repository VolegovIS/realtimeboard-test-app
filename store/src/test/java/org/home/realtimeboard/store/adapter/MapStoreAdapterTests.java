package org.home.realtimeboard.store.adapter;

import lombok.Getter;
import org.testng.annotations.BeforeMethod;

public class MapStoreAdapterTests extends AbstractStoreAdapterTests {
    @Getter
    private MapStoreAdapter storeAdapter;

    @BeforeMethod
    public void setUp() {
        storeAdapter = new MapStoreAdapter();
    }
}
