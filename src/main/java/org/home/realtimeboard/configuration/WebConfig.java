package org.home.realtimeboard.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Конфигурация для Spring MVC, добавляет {@link HandlerMethodArgumentResolver} для запросов с пагинацией
 */
@Configuration
@EnableHypermediaSupport(type= {EnableHypermediaSupport.HypermediaType.HAL})
public class WebConfig implements WebMvcConfigurer {
    // Размер страницы с данными по умолчанию
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    // Максимальный размер страницы с данными
    private static final Integer MAX_PAGE_SIZE = 500;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageableResolver());
    }

    @Bean
    public HateoasPageableHandlerMethodArgumentResolver pageableResolver() {
        HateoasPageableHandlerMethodArgumentResolver pageableResolver =
                new HateoasPageableHandlerMethodArgumentResolver();
        pageableResolver.setMaxPageSize(MAX_PAGE_SIZE);
        pageableResolver.setFallbackPageable(PageRequest.of(0, DEFAULT_PAGE_SIZE));
        return pageableResolver;
    }
}
