package org.home.realtimeboard.store.adapter;

import org.home.realtimeboard.model.Widget;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Адаптер для более низкоуровнего хранилища. Используется для сравнения разных реализаций в бенчмарке
 */
public interface InnerStoreAdapter {
    /**
     * Добавляет виджет
     *
     * @param widget виджет для сохранения
     */
    void add(Widget widget);

    /**
     * Ищет виджет с указанным идентификатором в хранилище
     *
     * @param id uuid виджета
     * @return информацию о запрощенном виджете
     */
    Optional<Widget> get(String id);

    /**
     * Удаляет виджет из хранилища
     *
     * @param widget вижет для удаления
     * @return факт удаления виджета
     */
    boolean remove(Widget widget);

    /**
     * Возвращает размер хранилища
     */
    int size();

    /**
     * Возвращает поток для обработки элементов хранилища
     */
    Stream<Widget> stream();

    /**
     * Выпихивает вышележащие виджеты в случаее коллизии
     *
     * @param zIndex     z-index нового виджета
     * @param pusherUuid uuid обновленного виджета для исключения его из обработки
     */
    void pushOut(Integer zIndex, String pusherUuid);

    /**
     * Является ли поток виджетов, переданный в {@link InnerStoreAdapter#stream()} отсортированным по zIndex
     * Для корректного обновления zIndex у элементов такого хранилища требуется перевставка виджета (удаление до
     * обновления и повторная его вставка после).
     */
    boolean isSortedByZIndex();
    
    /**
     * Ищет максимальный z-index а хранилище
     *
     * @return максимальный z-index или {@code 0}, если хранилище пусто
     */
    Integer getMaxZIndex();
}
