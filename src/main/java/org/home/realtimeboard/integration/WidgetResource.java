package org.home.realtimeboard.integration;

import org.home.realtimeboard.model.Widget;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

/**
 * HAL ресурс с данными о виджете
 */
public class WidgetResource extends Resource<Widget> {
    public WidgetResource(Widget content, Link... links) {
        super(content, links);
    }

    public WidgetResource(Widget content, Iterable<Link> links) {
        super(content, links);
    }
}
