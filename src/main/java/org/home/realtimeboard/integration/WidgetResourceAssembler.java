package org.home.realtimeboard.integration;

import org.home.realtimeboard.model.Widget;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Сборщик HAL ресерса с данными о виджете
 */
@Component
public class WidgetResourceAssembler implements ResourceAssembler<Widget, WidgetResource> {
    private final EntityLinks entityLinks;


    public WidgetResourceAssembler(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public WidgetResource toResource(Widget entity) {
        Assert.notNull(entity, "Entity instance must not be null");

        WidgetResource resource = new WidgetResource(entity);

        // Установка ссылок на допустимые методы
        resource.add(entityLinks.linkToSingleResource(entity).withSelfRel());
        resource.add(entityLinks.linkToSingleResource(entity).withRel("update"));
        resource.add(entityLinks.linkToSingleResource(entity).withRel("delete"));

        return resource;
    }
}
