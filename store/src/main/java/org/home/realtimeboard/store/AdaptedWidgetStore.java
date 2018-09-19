package org.home.realtimeboard.store;

import lombok.extern.slf4j.Slf4j;
import org.home.realtimeboard.model.Filter;
import org.home.realtimeboard.model.Widget;
import org.home.realtimeboard.store.adapter.InnerStoreAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Реализация {@link WidgetStore}, принимающая {@link InnerStoreAdapter} в качестве внутренней реализации хранилища
 */
@Slf4j
public class AdaptedWidgetStore implements WidgetStore {
    private final Object lock = new Object();

    private final InnerStoreAdapter storeAdapter;

    public AdaptedWidgetStore(InnerStoreAdapter storeAdapter) {
        this.storeAdapter = storeAdapter;
    }

    @Override
    public Widget add(Widget widget) {
        // Глобальная блокировка нужна для корректного выпихивания списка вышележащих виджетов
        synchronized (lock) {
            Widget.WidgetBuilder builder = widget.toBuilder()
                    .id(UUID.randomUUID().toString())
                    .lastModified(Instant.now());

            if (Objects.isNull(widget.getZIndex())) {
                builder.zIndex(storeAdapter.getMaxZIndex() + 1);
            } else {
                storeAdapter.pushOut(widget.getZIndex(), null);
            }
            widget = builder.build();

            storeAdapter.add(widget);
        }

        return wrapResult(widget);
    }

    /**
     * Ищет виджет с указанным идентификатором во внетреннем хранилище
     *
     * @param id uuid виджета
     * @return информацию о результате поиска
     */
    private Optional<Widget> findOneInternal(String id) {
        return storeAdapter.get(id);
    }

    @Override
    public Widget findOne(String id) {
        Optional<Widget> result = findOneInternal(id);
        return result.isPresent() ? wrapResult(result.get()) : null;
    }

    @Override
    public Widget update(String id, Widget widget) {
        // Глобальная блокировка нужна для корректного выпихивания списка вышележащих виджетов
        synchronized (lock) {
            Optional<Widget> persisted = findOneInternal(id);

            if (!persisted.isPresent()) {
                throw new ResourceNotFoundException();
            }

            // Для корректного обновления позиции виджета в отсортированной коллекции, его нужно перевставить в нее
            if (storeAdapter.isSortedByZIndex()) {
                storeAdapter.remove(persisted.get());
            }
            widget = persisted.get().merge(widget);
            storeAdapter.pushOut(widget.getZIndex(), widget.getId());
            if (storeAdapter.isSortedByZIndex()) {
                storeAdapter.add(widget);
            }
        }

        return wrapResult(widget);
    }

    @Override
    public Page<Widget> findAll(Filter filter, Pageable pageable) {
        long count = filter.isEmpty() ?
                storeAdapter.size() : storeAdapter.stream().filter(filter.toPredicate()).count();

        List<Widget> pageContent;
        if (count > pageable.getOffset()) {
            Stream<Widget> stream = storeAdapter.stream();
            if (!filter.isEmpty()) {
                stream = stream.filter(filter.toPredicate());
            }
            if (!storeAdapter.isSortedByZIndex()) {
                stream = stream.sorted(Comparator.comparingInt(Widget::getZIndex));
            }

            pageContent = stream
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(this::wrapResult)
                    .collect(Collectors.toList());
        } else {
            pageContent = Collections.emptyList();
        }

        return new PageImpl<>(pageContent, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), count);
    }

    @Override
    public void delete(String id) {
        // Глобальная блокировка нужна для корректного выпихивания списка вышележащих виджетов
        synchronized (lock) {
            Widget widget = findOne(id);
            boolean removed = Objects.nonNull(widget) && storeAdapter.remove(widget);
            if (!removed) {
                throw new ResourceNotFoundException();
            }
        }
    }

    /**
     * Формирует копию виджета, для изоляции внетреннго хранилища от модификации извне
     *
     * @param widget виджет для клонирования
     * @return новую копию виджета
     */
    private Widget wrapResult(Widget widget) {
        return Objects.nonNull(widget) ? widget.toBuilder().build() : null;
    }
}
