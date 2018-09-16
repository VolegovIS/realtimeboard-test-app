package org.home.realtimeboard.controller;

import lombok.extern.slf4j.Slf4j;
import org.home.realtimeboard.integration.WidgetPagedResourcesAssembler;
import org.home.realtimeboard.integration.WidgetResource;
import org.home.realtimeboard.integration.WidgetResourceAssembler;
import org.home.realtimeboard.model.Filter;
import org.home.realtimeboard.model.Widget;
import org.home.realtimeboard.store.WidgetStore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * REST контроллер для управления виджетами, поддерживает HAL
 */
@RestController
@RequestMapping("widgets")
@ExposesResourceFor(Widget.class)
@Slf4j
public class WidgetController implements ResourceProcessor<RepositoryLinksResource> {
    private final WidgetStore widgetStore;
    private final WidgetResourceAssembler widgetResourceAssembler;
    private final WidgetPagedResourcesAssembler pagedAssembler;

    public WidgetController(WidgetStore widgetStore,
                            WidgetResourceAssembler widgetResourceAssembler,
                            WidgetPagedResourcesAssembler pagedAssembler) {

        this.widgetStore = widgetStore;
        this.widgetResourceAssembler = widgetResourceAssembler;
        this.pagedAssembler = pagedAssembler;
    }

    /**
     * Добавляет новый виджет
     *
     * @param widget описание нового виджета
     * @return HAL ресурс с данными о виджете и доступными операциями над ним
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WidgetResource addWidget(@RequestBody @Validated(Widget.CreateValidation.class) Widget widget) {
        return widgetResourceAssembler.toResource(widgetStore.add(widget));
    }

    /**
     * Ищет виджет по указанному идентификатору
     *
     * @param id uuid виджета
     * @return HAL ресурс с данными о виджете и доступными операциями над ним
     * @throws ResourceNotFoundException если виджет с указанным id не найден
     */
    @GetMapping("{id}")
    public WidgetResource getWidget(@PathVariable String id) {
        Widget widget = widgetStore.findOne(id);

        if (Objects.isNull(widget)) {
            throw new ResourceNotFoundException();
        }

        return widgetResourceAssembler.toResource(widget);
    }

    /**
     * Обновляет виджет с указанным id переданными данными
     *
     * @param id     uuid виджета
     * @param widget обновленные данные виджета
     * @return HAL ресурс с данными о виджете и доступными операциями над ним
     */
    @PutMapping("{id}")
    public WidgetResource updateWidget(@PathVariable String id,
                                       @RequestBody @Validated(Widget.UpdateValidation.class) Widget widget) {
        return widgetResourceAssembler.toResource(widgetStore.update(id, widget));
    }

    /**
     * Ищет виджеты, удовлетворяющие фильтрации
     *
     * @param filter   фильтр для выборки виджетов
     * @param pageable параметры пагинации
     * @return HAL ресурс с описанием страницы виджетов и допустимых операций
     */
    @GetMapping
    public PagedResources<WidgetResource> findAllWidgets(@Valid Filter filter, Pageable pageable) {
        return pagedAssembler.toResource(widgetStore.findAll(filter, pageable), widgetResourceAssembler);
    }

    /**
     * Удаляет виджет с указанным идентификатором
     *
     * @param id uuid виджета для удаления
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWidget(@PathVariable String id) {
        widgetStore.delete(id);
    }

    /**
     * Добавляет ссылку на контроллер в корневой HAL ресурс
     */
    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(linkTo(methodOn(WidgetController.class).findAllWidgets(null, null)).withRel("widgets"));
        return resource;
    }
}
