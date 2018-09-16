package org.home.realtimeboard.store;

import org.home.realtimeboard.model.Filter;
import org.home.realtimeboard.model.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Базовая реализация {@link WidgetStore}. Гарантирует потокобезопасность и изолированность хранилища
 */
@Service
public class SimpleWidgetStore implements WidgetStore {
    private final Object lock = new Object();

    /**
     * Внутренне хранилище виджетов
     */
    private List<Widget> store = new ArrayList<>();

    @Override
    public Widget add(Widget widget) {
        // Глобальная блокировка нужна для корректного выпихивания списка вышележащих виджетов
        synchronized (lock) {
            Widget.WidgetBuilder builder = widget.toBuilder()
                    .id(UUID.randomUUID().toString())
                    .lastModified(Instant.now());

            if (Objects.isNull(widget.getZIndex())) {
                builder.zIndex(getMaxZIndex() + 1);
            } else {
                pushOut(widget.getZIndex(), null);
            }
            widget = builder.build();

            store.add(widget);
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
        return store.stream().filter(w -> Objects.equals(w.getId(), id)).findAny();
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

            widget = persisted.get().merge(widget);
            pushOut(widget.getZIndex(), widget.getId());
        }

        return wrapResult(widget);
    }

    @Override
    public Page<Widget> findAll(Filter filter, Pageable pageable) {
        long count = filter.isEmpty() ? store.size() : store.stream().filter(filter.toPredicate()).count();

        List<Widget> pageContent;
        if (count > pageable.getOffset()) {
            Stream<Widget> stream = store.stream();
            if (!filter.isEmpty()) {
                stream = stream.filter(filter.toPredicate());
            }

            pageContent = stream
                    .sorted(Comparator.comparingInt(Widget::getZIndex))
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(this::wrapResult)
                    .collect(Collectors.toList());
        } else {
            pageContent = Collections.emptyList();
        }

        return new PageImpl<>(pageContent, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                count);
    }

    @Override
    public void delete(String id) {
        boolean removed = store.removeIf(w -> Objects.equals(w.getId(), id));
        if (!removed) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Выпихивает вышележащие виджеты в случаее коллизии
     *
     * @param zIndex     z-index нового виджета
     * @param pusherUuid uuid обновленного виджета для исключения его из обработки
     */
    private void pushOut(Integer zIndex, String pusherUuid) {
        boolean hasCollision = store.stream().anyMatch(w -> Objects.equals(w.getZIndex(), zIndex) &&
                !Objects.equals(w.getId(), pusherUuid));

        if (hasCollision) {
            store.forEach(w -> {
                if ((w.getZIndex() >= zIndex) && !Objects.equals(w.getId(), pusherUuid)) {
                    w.pushOut();
                }
            });
        }
    }

    /**
     * Ищет максимальный z-index а хранилище
     *
     * @return максимальный z-index или {@code 0}, если хранилище пусто
     */
    private int getMaxZIndex() {
        return store.stream().map(Widget::getZIndex).reduce(Math::max).orElse(0);
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
