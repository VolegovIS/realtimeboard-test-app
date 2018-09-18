package org.home.realtimeboard.store;

import org.home.realtimeboard.model.Filter;
import org.home.realtimeboard.model.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

/**
 * Хранилище виджетов, предоставляет базовые CRUDL операции
 */
public interface WidgetStore {
    /**
     * Добавляет новый виджет, выпихивает наверх все виджеты с таким же или большим z-index при коллизии
     *
     * @param widget описание нового виджета
     * @return созданный виджет
     */
    Widget add(Widget widget);

    /**
     * Ищет виджет по указанному идентификатору
     *
     * @param id uuid виджета
     * @return найденный виджет или {@code null} если виджет не был найден
     */
    Widget findOne(String id);

    /**
     * Обновляет виджет с указанным id переданными данными, выпихивает наверх все виджеты с таким же или большим z-index при коллизии
     *
     * @param id     uuid виджета
     * @param widget обновленные данные виджета
     * @return обновленный виджет
     * @throws ResourceNotFoundException если виджет с указанным идентификатором не был найден
     */
    Widget update(String id, Widget widget);

    /**
     * Ищет виджеты, удовлетворяющие фильтрации
     *
     * @param filter   фильтр для выборки виджетов
     * @param pageable параметры пагинации
     * @return найденная страница виджетов
     */
    Page<Widget> findAll(Filter filter, Pageable pageable);

    /**
     * Удаляет виджет с указанным идентификатором
     *
     * @param id uuid виджета для удаления
     * @throws ResourceNotFoundException если виджет с указанным идентификатором не был найден
     */
    void delete(String id);
}
