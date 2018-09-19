package org.home.realtimeboard.model;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Тесты для {@link Widget}
 */
public class WidgetTests extends AbstractTestNGSpringContextTests {
    @Test
    public void testPushOut() {
        Integer widgetZIndex = 100;

        Widget widget = Widget.builder().zIndex(widgetZIndex).build();
        assertEquals(widget.getZIndex(), widgetZIndex);
        widget.pushOut();
        assertEquals(widget.getZIndex(), (Integer) (widgetZIndex + 1));
    }
}
