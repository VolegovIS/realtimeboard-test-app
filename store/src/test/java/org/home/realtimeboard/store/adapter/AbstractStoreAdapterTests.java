package org.home.realtimeboard.store.adapter;

import org.home.realtimeboard.model.Widget;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

/**
 * Общий класс тестов для {@link InnerStoreAdapter}
 */
public abstract class AbstractStoreAdapterTests extends AbstractTestNGSpringContextTests {
    public abstract InnerStoreAdapter getStoreAdapter();
    // Генератор уникальных zIndex'ов, необходим для корректной работы хранилищ с сортировкой
    private Integer zIndexGenerator;

    @BeforeMethod
    public void setUpGenerator() {
        zIndexGenerator = 0;
    }

    @Test
    public void testAdd() {
        assertEquals(getStoreAdapter().size(), 0);
        getStoreAdapter().add(getWidget());
        assertEquals(getStoreAdapter().size(), 1);
    }

    @Test
    public void testGet() {
        Widget initial = getWidget();
        assertFalse(getStoreAdapter().get(initial.getId()).isPresent());
        getStoreAdapter().add(initial);
        assertTrue(getStoreAdapter().get(initial.getId()).isPresent());
        assertEquals(getStoreAdapter().get(initial.getId()).get(), initial);
    }

    @Test
    public void testRemove() {
        Widget widget = getWidget();
        getStoreAdapter().add(widget);
        assertEquals(getStoreAdapter().size(), 1);
        getStoreAdapter().remove(widget);
        assertEquals(getStoreAdapter().size(), 0);
    }

    @Test
    public void testSize() {
        assertEquals(getStoreAdapter().size(), 0);
        getStoreAdapter().add(getWidget());
        getStoreAdapter().add(getWidget());
        getStoreAdapter().add(getWidget());
        assertEquals(getStoreAdapter().size(), 3);
    }

    @Test
    public void testStream() {
        getStoreAdapter().add(getWidget());
        getStoreAdapter().add(getWidget());
        getStoreAdapter().add(getWidget());
        assertNotNull(getStoreAdapter().stream());
        assertEquals(getStoreAdapter().stream().count(), 3);
    }

    @DataProvider(name = "testPushOutData")
    public Object[][] getTestPushOutData() {
        Widget fixedWidget = getWidget(5);

        return new Object[][] {
                {
                        Arrays.asList(getWidget(1), getWidget(5), getWidget(10)),
                        3,
                        UUID.randomUUID().toString(),
                        Arrays.asList(1, 5, 10)
                },
                {
                        Arrays.asList(getWidget(1), getWidget(5), getWidget(10)),
                        5,
                        UUID.randomUUID().toString(),
                        Arrays.asList(1, 6, 11)
                },
                {
                        Arrays.asList(getWidget(1), fixedWidget, getWidget(10)),
                        fixedWidget.getZIndex(),
                        fixedWidget.getId(),
                        getStoreAdapter().isSortedByZIndex() ? Arrays.asList(1, 6, 11) : Arrays.asList(1, 5, 10)
                }
        };
    }

    @Test(dataProvider = "testPushOutData")
    public void testPushOut(List<Widget> widgets, Integer pusherZIndex, String pusherId,
                            List<Integer> expectedIndexes) {

        widgets.forEach(w -> getStoreAdapter().add(w));
        getStoreAdapter().pushOut(pusherZIndex, pusherId);
        List<Integer> zIndexes = getStoreAdapter().stream()
                .map(Widget::getZIndex)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(zIndexes, expectedIndexes);
    }

    @Test
    public void testIsSortedByZIndex() {
        if (getStoreAdapter().isSortedByZIndex()) {
            getStoreAdapter().add(getWidget(100));
            getStoreAdapter().add(getWidget(10));
            getStoreAdapter().add(getWidget(200));
            getStoreAdapter().add(getWidget(50));
            getStoreAdapter().add(getWidget(250));
            getStoreAdapter().add(getWidget(300));

            List<Integer> zIndexes = getStoreAdapter().stream().map(Widget::getZIndex).collect(Collectors.toList());
            List<Integer> zIndexesSorted = getStoreAdapter().stream()
                    .map(Widget::getZIndex)
                    .sorted()
                    .collect(Collectors.toList());
            assertEquals(zIndexes, zIndexesSorted);
        }
    }

    @Test
    public void testGetMaxZIndex() {
        assertEquals(getStoreAdapter().getMaxZIndex().intValue(), 0);
        getStoreAdapter().add(getWidget(100));
        assertEquals(getStoreAdapter().getMaxZIndex().intValue(), 100);
        getStoreAdapter().add(getWidget(10));
        assertEquals(getStoreAdapter().getMaxZIndex().intValue(), 100);
        getStoreAdapter().add(getWidget(200));
        assertEquals(getStoreAdapter().getMaxZIndex().intValue(), 200);
        getStoreAdapter().add(getWidget(50));
        assertEquals(getStoreAdapter().getMaxZIndex().intValue(), 200);
        getStoreAdapter().add(getWidget(250));
        assertEquals(getStoreAdapter().getMaxZIndex().intValue(), 250);
        getStoreAdapter().add(getWidget(300));
        assertEquals(getStoreAdapter().getMaxZIndex().intValue(), 300);
    }

    private Widget getWidget() {
        return Widget.builder()
                .id(UUID.randomUUID().toString())
                .zIndex(zIndexGenerator++)
                .build();
    }

    private Widget getWidget(Integer zIndex) {
        return Widget.builder()
                .id(UUID.randomUUID().toString())
                .zIndex(zIndex)
                .build();
    }
}
