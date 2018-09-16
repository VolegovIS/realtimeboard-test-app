package org.home.realtimeboard.integration;

import org.home.realtimeboard.model.Widget;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Component;

/**
 * Сборщик HAL ресурса с данными о странице виджетов.
 * Устанавливает ссылки на соседние и крайние страницы в запросах с пагинацией
 */
@Component
public class WidgetPagedResourcesAssembler extends PagedResourcesAssembler<Widget> {
    public WidgetPagedResourcesAssembler(HateoasPageableHandlerMethodArgumentResolver resolver) {
        super(resolver, null);
    }
}
