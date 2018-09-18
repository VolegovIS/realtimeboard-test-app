package org.home.realtimeboard.store.adapter;

import org.home.realtimeboard.model.Widget;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

/**
 * Комбинированное хранилище, {@link SortedSet} используется как основное хранилище, поддерживающее сортировку по zIndex,
 * а {@link Map} как индекс для быстрого доступа к элементам по id
 *
 * @see SortedSetStoreAdapter
 */
public class CombinedStoreAdapter implements InnerStoreAdapter {
    private Map<String, Widget> index;
    private SortedSet<Widget> store;
    private Integer maxZIndex;

    public CombinedStoreAdapter() {
        this(new ConcurrentHashMap<>(), new ConcurrentSkipListSet<>(Comparator.comparingInt(Widget::getZIndex)));
    }

    public CombinedStoreAdapter(Map<String, Widget> index, SortedSet<Widget> store) {
        this.index = index;
        this.store = store;
    }

    @Override
    public void add(Widget widget) {
        this.store.add(widget);
        this.index.put(widget.getId(), widget);
        if (Objects.isNull(maxZIndex) || (widget.getZIndex() > maxZIndex)) {
            maxZIndex = widget.getZIndex();
        }
    }

    @Override
    public Optional<Widget> get(String id) {
        return Optional.ofNullable(this.index.get(id));
    }

    @Override
    public boolean remove(Widget widget) {
        this.index.remove(widget.getId());
        return this.store.remove(widget);
    }

    @Override
    public int size() {
        return this.store.size();
    }

    @Override
    public Stream<Widget> stream() {
        return this.store.stream();
    }

    @Override
    public void pushOut(Integer zIndex, String pusherUuid) {
        // Бинарное дерево поиска построено по z-index, именно по нему и происходит сравнение сущностей в этой коллекции,
        // так что можно создать "поисковую заглушку"
        // Решение не самое чистое, но рабочее
        boolean hasCollision = store.contains(Widget.builder().zIndex(zIndex).build());

        // Т.к. дерево поиска построено по z-index, то для изменения его значения нужно сначала удалить элемент из дерева,
        // обновить и снова вставить, чтобы структура дерева не сломалась. Но увеличение больших ключей, равно как и
        // уменьшение меньшах ключей не сломает структуру дерева, этим можно воспользоваться.
        // Решение не самое чистое, но рабочее
        if (hasCollision) {
            store.forEach(w -> {
                if ((w.getZIndex() >= zIndex)) {
                    w.pushOut();
                }
                if (w.getZIndex() > maxZIndex) {
                    maxZIndex = w.getZIndex();
                }
            });
        }
    }

    @Override
    public boolean isSortedByZIndex() {
        return true;
    }

    public Integer getMaxZIndex() {
        if (Objects.isNull(maxZIndex)) {
            maxZIndex = store.stream().map(Widget::getZIndex).reduce(Math::max).orElse(0);
        }
        return maxZIndex;
    }
}
