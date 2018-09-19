package org.home.realtimeboard.store;

import lombok.extern.slf4j.Slf4j;
import org.home.realtimeboard.model.Filter;
import org.home.realtimeboard.model.Widget;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

/**
 * Общий класс тестов для {@link WidgetStore}
 */
@Slf4j
public abstract class AbstractWidgetStoreTests {
    public abstract WidgetStore getWidgetStore();

    @Test
    public void testAdd() {
        Widget widget = getWidget();
        assertNull(widget.getId());
        widget = getWidgetStore().add(widget);
        assertNotNull(widget.getId());
        assertNotNull(getWidgetStore().findOne(widget.getId()));
    }

    @Test
    public void testAddTop() {
        Widget widget = getWidgetStore().add(getWidget());
        assertEquals(widget.getZIndex().intValue(), 1);
        widget = getWidgetStore().add(getWidget());
        assertEquals(widget.getZIndex().intValue(), 2);
        widget = getWidgetStore().add(getWidget(200));
        assertEquals(widget.getZIndex().intValue(), 200);
        widget = getWidgetStore().add(getWidget());
        assertEquals(widget.getZIndex().intValue(), 201);
    }

    @Test
    public void testAddPush() {
        getWidgetStore().add(getWidget(1));
        getWidgetStore().add(getWidget(1));
        getWidgetStore().add(getWidget(1));

        getWidgetStore().add(getWidget(10));
        getWidgetStore().add(getWidget(10));
        getWidgetStore().add(getWidget(10));

        getWidgetStore().add(getWidget(5));
        getWidgetStore().add(getWidget(5));
        getWidgetStore().add(getWidget(5));

        List<Integer> zIndexes = getWidgetStore().findAll(Filter.builder().build(), PageRequest.of(0, 10))
                .stream()
                .map(Widget::getZIndex)
                .collect(Collectors.toList());
        List<Integer> expected = Arrays.asList(1, 2, 3, 5, 6, 7, 12, 13, 14);
        assertEquals(zIndexes, expected);
    }

    @Test
    public void testFindOne() {
        Widget widget = getWidgetStore().add(getWidget());
        assertEquals(getWidgetStore().findOne(widget.getId()), widget);
    }

    @Test
    public void testFindOneMissing() {
        assertNull(getWidgetStore().findOne(UUID.randomUUID().toString()));
    }

    @Test
    public void testUpdate() {
        Integer x = 10;
        Integer y = 20;
        Integer height = 30;
        Integer width = 40;
        Integer zIndex = 50;

        Widget widget = getWidgetStore().add(getWidget());
        String id = widget.getId();

        widget.setId(UUID.randomUUID().toString());
        widget.setX(x);
        widget.setY(y);
        widget.setHeight(height);
        widget.setWidth(width);
        widget.setZIndex(zIndex);

        widget = getWidgetStore().update(id, widget);

        assertEquals(widget.getId(), id);
        assertEquals(widget.getX(), x);
        assertEquals(widget.getY(), y);
        assertEquals(widget.getHeight(), height);
        assertEquals(widget.getWidth(), width);
        assertEquals(widget.getZIndex(), zIndex);
    }

    @Test
    public void testUpdatePush() {
        getWidgetStore().add(getWidget(1));
        getWidgetStore().add(getWidget(1));
        getWidgetStore().add(getWidget(1));

        getWidgetStore().add(getWidget(5));
        getWidgetStore().add(getWidget(5));
        getWidgetStore().add(getWidget(5));

        getWidgetStore().add(getWidget(10));
        getWidgetStore().add(getWidget(10));
        Widget widget = getWidgetStore().add(getWidget(10));

        widget.setZIndex(5);
        getWidgetStore().update(widget.getId(), widget);

        List<Integer> zIndexes = getWidgetStore().findAll(Filter.builder().build(), PageRequest.of(0, 10))
                .stream()
                .map(Widget::getZIndex)
                .collect(Collectors.toList());
        List<Integer> expected = Arrays.asList(1, 2, 3, 5, 6, 7, 8, 12, 13);
        assertEquals(zIndexes, expected);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testUpdateMissing() {
        getWidgetStore().update(UUID.randomUUID().toString(), getWidget());
    }

    @Test
    public void testImmutability() {
        Integer x = 10;
        Integer y = 20;
        Integer height = 30;
        Integer width = 40;
        Integer zIndex = 50;

        Widget widget = getWidgetStore().add(getWidget(x, y, width, height, zIndex));
        String id = widget.getId();

        widget.setId(UUID.randomUUID().toString());
        widget.setX(x * 2);
        widget.setY(y * 2);
        widget.setHeight(height * 2);
        widget.setWidth(width * 2);
        widget.setZIndex(zIndex * 2);

        widget = getWidgetStore().findOne(id);

        assertEquals(widget.getId(), id);
        assertEquals(widget.getX(), x);
        assertEquals(widget.getY(), y);
        assertEquals(widget.getHeight(), height);
        assertEquals(widget.getWidth(), width);
        assertEquals(widget.getZIndex(), zIndex);
    }

    private List<Widget> getSampleWidgets() {
        return Arrays.asList(
                getWidget(0, 0, 5, 5, 4),
                getWidget(0, 5, 5, 5, 2),
                getWidget(5, 0, 5, 5, 5),
                getWidget(5, 5, 5, 5, 7),
                getWidget(-1, 0, 5, 5, 1),
                getWidget(6, 0, 5, 5, 6),
                getWidget(0, -1, 5, 5, 3),
                getWidget(0, 6, 5, 5, 8)
        );
    }

    @DataProvider(name = "testFindAllData")
    public Object[][] getTestFindAllData() {
        return new Object[][]{
                // Полная выборка
                {
                        getSampleWidgets(),
                        Filter.builder().build(),
                        PageRequest.of(0, 10),
                        Arrays.asList(4, 1, 6, 0, 2, 5, 3, 7)
                },

                // Тесты пагинации
                {
                        getSampleWidgets(),
                        Filter.builder().build(),
                        PageRequest.of(0, 2),
                        Arrays.asList(4, 1)
                },
                {
                        getSampleWidgets(),
                        Filter.builder().build(),
                        PageRequest.of(0, 5),
                        Arrays.asList(4, 1, 6, 0, 2)
                },
                {
                        getSampleWidgets(),
                        Filter.builder().build(),
                        PageRequest.of(2, 2),
                        Arrays.asList(2, 5)
                },
                {
                        getSampleWidgets(),
                        Filter.builder().build(),
                        PageRequest.of(1, 3),
                        Arrays.asList(0, 2, 5)
                },
                {
                        getSampleWidgets(),
                        Filter.builder().build(),
                        PageRequest.of(2, 3),
                        Arrays.asList(3, 7)
                },
                {
                        getSampleWidgets(),
                        Filter.builder().build(),
                        PageRequest.of(1, 10),
                        Collections.emptyList()
                },

                // Тесты фильтра
                {
                        getSampleWidgets(),
                        Filter.builder().top(-10).bottom(20).left(-10).right(20).build(),
                        PageRequest.of(0, 10),
                        Arrays.asList(4, 1, 6, 0, 2, 5, 3, 7)
                },
                {
                        getSampleWidgets(),
                        Filter.builder().top(0).bottom(10).left(0).right(10).build(),
                        PageRequest.of(0, 10),
                        Arrays.asList(1, 0, 2, 3)
                },
                {
                        getSampleWidgets(),
                        Filter.builder().top(5).bottom(10).left(5).right(10).build(),
                        PageRequest.of(0, 10),
                        Arrays.asList(3)
                },
                {
                        getSampleWidgets(),
                        Filter.builder().top(6).bottom(20).left(6).right(20).build(),
                        PageRequest.of(0, 10),
                        Collections.emptyList()
                }
        };
    }

    @Test(dataProvider = "testFindAllData")
    public void testFindAll(List<Widget> widgets, Filter filter, Pageable pageable, List<Integer> results) {
        List<String> ids = new ArrayList<>();
        for (Widget widget : widgets) {
            ids.add(getWidgetStore().add(widget).getId());
        }
        List<String> expected = results.stream().map(ids::get).collect(Collectors.toList());
        List<String> searchResult = getWidgetStore().findAll(filter, pageable).stream()
                .map(Widget::getId)
                .collect(Collectors.toList());

        assertEquals(searchResult, expected);
    }

    @Test
    public void testDelete() {
        Widget widget = getWidgetStore().add(getWidget());
        assertNotNull(getWidgetStore().findOne(widget.getId()));
        getWidgetStore().delete(widget.getId());
        assertNull(getWidgetStore().findOne(widget.getId()));
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void testDeleteMissing() {
        getWidgetStore().delete(UUID.randomUUID().toString());
    }

    private Widget getWidget() {
        return Widget.builder().build();
    }

    private Widget getWidget(Integer zIndex) {
        return Widget.builder()
                .zIndex(zIndex)
                .build();
    }

    private Widget getWidget(Integer x, Integer y, Integer width, Integer height, Integer zIndex) {
        return Widget.builder()
                .x(x)
                .y(y)
                .height(height)
                .width(width)
                .zIndex(zIndex)
                .build();
    }
}
