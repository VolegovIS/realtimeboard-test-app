package org.home.realtimeboard.store.adapter;

import org.home.realtimeboard.model.Widget;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

/**
 * Хранилище основанное на {@link SortedSet}
 * <p>
 * Хранилище в первую очередь рассчитано на {@link TreeSet} и его производные, т.к. в их основе лежит красно-черное
 * дерево, что позволит хранить коллекцию виджетов в автоматически отсортированном по z-index состоянии, сравнительно
 * быстро искать по z-index коллизии при вставке новых виджетов, т.к. дерево поиска построено именно по z-index, а также
 * без перевставки элементов обновлять z-index в большую сторону при вставке.
 */
public class SortedSetStoreAdapter implements InnerStoreAdapter {
    private SortedSet<Widget> store;
    private Integer maxZIndex;

    public SortedSetStoreAdapter() {
        this(new ConcurrentSkipListSet<>(Comparator.comparingInt(Widget::getZIndex)));
    }

    public SortedSetStoreAdapter(SortedSet<Widget> store) {
        this.store = store;
    }

    @Override
    public void add(Widget widget) {
        store.add(widget);
        if (Objects.isNull(maxZIndex) || (widget.getZIndex() > maxZIndex)) {
            maxZIndex = widget.getZIndex();
        }
    }

    @Override
    public Optional<Widget> get(String id) {
        return store.stream().filter(w -> Objects.equals(w.getId(), id)).findAny();
    }

    @Override
    public boolean remove(Widget widget) {
        return store.remove(widget);
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public Stream<Widget> stream() {
        return store.stream();
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
