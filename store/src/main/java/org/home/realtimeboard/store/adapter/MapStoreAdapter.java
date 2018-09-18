package org.home.realtimeboard.store.adapter;

import org.home.realtimeboard.model.Widget;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Хранилище основанное на {@link Map}
 */
public class MapStoreAdapter implements InnerStoreAdapter {
    private Map<String, Widget> store;
    private Integer maxZIndex;

    public MapStoreAdapter() {
        this(new ConcurrentHashMap<>());
    }

    public MapStoreAdapter(Map<String, Widget> store) {
        this.store = store;
    }

    @Override
    public void add(Widget widget) {
        store.put(widget.getId(), widget);
        if (Objects.isNull(maxZIndex) || (widget.getZIndex() > maxZIndex)) {
            maxZIndex = widget.getZIndex();
        }
    }

    @Override
    public Optional<Widget> get(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean remove(Widget widget) {
        return !Objects.isNull(store.remove(widget.getId()));
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public Stream<Widget> stream() {
        return store.values().stream();
    }

    @Override
    public void pushOut(Integer zIndex, String pusherUuid) {
        boolean hasCollision = store.values().stream().anyMatch(w -> Objects.equals(w.getZIndex(), zIndex) &&
                !Objects.equals(w.getId(), pusherUuid));

        if (hasCollision) {
            store.values().forEach(w -> {
                if ((w.getZIndex() >= zIndex) && !Objects.equals(w.getId(), pusherUuid)) {
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
        return false;
    }

    public Integer getMaxZIndex() {
        if (Objects.isNull(maxZIndex)) {
            maxZIndex = store.values().stream().map(Widget::getZIndex).reduce(Math::max).orElse(0);
        }
        return maxZIndex;
    }
}
