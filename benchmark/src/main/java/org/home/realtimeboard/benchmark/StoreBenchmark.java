package org.home.realtimeboard.benchmark;

import lombok.extern.slf4j.Slf4j;
import org.home.realtimeboard.model.Filter;
import org.home.realtimeboard.model.Widget;
import org.home.realtimeboard.store.AdaptedWidgetStore;
import org.home.realtimeboard.store.SimpleWidgetStore;
import org.home.realtimeboard.store.WidgetStore;
import org.home.realtimeboard.store.adapter.CombinedStoreAdapter;
import org.home.realtimeboard.store.adapter.MapStoreAdapter;
import org.home.realtimeboard.store.adapter.SortedSetStoreAdapter;
import org.openjdk.jmh.annotations.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Бенчмарк производительности хранилища
 */
@Fork(warmups = 0, value = 1)
@Warmup(iterations = 1, time = 10)
@Measurement(iterations = 4, time = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(value = Scope.Benchmark)
@Slf4j
public class StoreBenchmark {
    // Максимальная допустимая координата виджета
    private final static Integer MAX_COORDINATE = 10000;
    // Максимальный допустимый размер виджета
    private final static Integer MAX_SIZE = 1000;
    // Максимальный допустимый номер страницы запрашиваемых данных
    private final static Integer MAX_PAGE = 50;
    // Максимальный допустимый размер страницы данных
    private final static Integer MAX_PAGE_SIZE = 100;

    // Тестируемое хранилище
    private WidgetStore store;
    // Список идентификаторов виджетов, для формирования корректных запросов на обновление/удаление
    private List<String> ids = new ArrayList<>();
    // Глобальный ГПСЧ, используется для сравнительно детерменированного тестирования, т.к. каждый раз инициализируется
    // одним и темже числом
    private Random random;
    // Профиль нагрузки
    private LoadProfile loadProfile;
    // Статистика по выполненным операциям
    private Map<ActionType, Integer> statistics;

    /**
     * План тестирования, содержит параметры для проведения тестов
     */
    @State(Scope.Benchmark)
    public static class ExecutionPlan {
        /**
         * Список проверяемях профилей нагрузки
         */
        @Param
        public LoadProfileType profileType;

        /**
         * Список проверяемых хранилищ
         */
        @Param
        public StoreType storeType;
    }

    /**
     * Метод подготовки тестового окружения, создает и наполняет нужное хранилище
     *
     * @param executionPlan описание сценария тестирования
     */
    @Setup(Level.Iteration)
    public void setUp(ExecutionPlan executionPlan) {
        switch (executionPlan.storeType) {
            case SIMPLE_STORE:
                store = new SimpleWidgetStore();
                break;
            case MAP_STORE:
                store = new AdaptedWidgetStore(new MapStoreAdapter());
                break;
            case TREE_SET_STORE:
                store = new AdaptedWidgetStore(new SortedSetStoreAdapter());
                break;
            case COMBINED_STORE:
                store = new AdaptedWidgetStore(new CombinedStoreAdapter());
                break;
        }

        ids = new ArrayList<>();
        // seed всегда одинаковый, чтобы тесты выполнялись одинакого
        random = new Random(1);
        statistics = new HashMap<>();
        loadProfile = LoadProfile.forType(executionPlan.profileType);

        for (Integer idx = 0; idx < 5000; idx++) {
            Widget widget = getWidget(false);

            widget = store.add(widget);
            ids.add(widget.getId());
        }
    }

    /**
     * Очистка тествого окружения
     * Формально она не нужна, т.к. все делается в инициализации, тут только дебажный вывод статистики
     */
    @TearDown(Level.Iteration)
    public void printStat() {
        log.info("statistics: {}", statistics);
    }

    /**
     * Профилируемый метод, имитирует запрос к хранилищу согласно заданнному профилю
     */
    @Benchmark
    public void testStore() {
        executeOperation();
    }

    /**
     * Выполняет случайную операцию над хранилищем, согласно заданнному профилю
     */
    private void executeOperation() {
        ActionType action = loadProfile.nextAction(random);
        statistics.put(action, statistics.getOrDefault(action, 0) + 1);

        switch (action) {
            case ADD:
                Widget widget = store.add(getWidget(true));
                ids.add(widget.getId());
                break;
            case FIND_ONE:
                store.findOne(getId(random.nextBoolean()));
                break;
            case UPDATE:
                if (!ids.isEmpty()) {
                    store.update(getId(false), getWidget(false));
                }
                break;
            case DELETE:
                if (!ids.isEmpty()) {
                    String id = getId(false);
                    store.delete(id);
                    ids.remove(id);
                }
                break;
            case FIND_ALL:
                store.findAll(getFilter(), getPageable());
                break;
        }
    }

    /**
     * Формирует случайный виджет
     *
     * @param allowNullZIndex факт допустимости пустых zIndex'ов, они допустимы только при доавблении новых виджетов
     * @return новый виджет
     */
    private Widget getWidget(boolean allowNullZIndex) {
        return Widget.builder()
                .x(random.nextInt(MAX_COORDINATE))
                .y(random.nextInt(MAX_COORDINATE))
                .width(random.nextInt(MAX_SIZE))
                .height(random.nextInt(MAX_SIZE))
                .zIndex(allowNullZIndex && random.nextBoolean() ? null : random.nextInt(MAX_SIZE))
                .build();
    }

    /**
     * Формирует случайных фильтр по области видимости
     */
    private Filter getFilter() {
        if (random.nextBoolean()) {
            return Filter.builder().build();
        } else {
            return Filter.builder()
                    .top(random.nextInt(MAX_COORDINATE / 2))
                    .bottom(MAX_COORDINATE / 2 + random.nextInt(MAX_COORDINATE / 2))
                    .left(random.nextInt(MAX_COORDINATE / 2))
                    .right(MAX_COORDINATE / 2 + random.nextInt(MAX_COORDINATE / 2))
                    .build();
        }
    }

    /**
     * Формирует случайную страницу для запросов с пагинацией
     */
    private Pageable getPageable() {
        return PageRequest.of(random.nextInt(MAX_PAGE), random.nextInt(MAX_PAGE_SIZE) + 1);
    }

    /**
     * Формирует идентификатор виджета
     *
     * @param isRandom факт допустимости случайных виджетов, допустимы только в запросах на поиск
     * @return идентификатор виджета для запроса
     */
    private String getId(Boolean isRandom) {
        if (isRandom || ids.isEmpty()) {
            return UUID.randomUUID().toString();
        } else {
            return ids.get(random.nextInt(ids.size()));
        }
    }
}

