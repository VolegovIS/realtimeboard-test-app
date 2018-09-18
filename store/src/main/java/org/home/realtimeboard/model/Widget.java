package org.home.realtimeboard.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Instant;

/**
 * Виджет
 */
@Data
@Builder(toBuilder = true)
@Relation("widgets")
public class Widget implements Identifiable {
    // Проекция валидации для метода создания виджета
    public static interface CreateValidation {}
    // Проекция валидации для метода обновления виджета
    public static interface UpdateValidation extends CreateValidation {}

    /**
     * Идентификатор
     */
    private String id;

    /**
     * X координата
     */
    @NotNull(groups = CreateValidation.class)
    private Integer x;

    /**
     * Y координата
     */
    @NotNull(groups = CreateValidation.class)
    private Integer y;

    /**
     * Ширина виджета
     */
    @NotNull(groups = CreateValidation.class)
    @Positive(groups = CreateValidation.class)
    private Integer width;

    /**
     * Высота виджета
     */
    @NotNull(groups = CreateValidation.class)
    @Positive(groups = CreateValidation.class)
    private Integer height;

    /**
     * z-index
     */
    @NotNull(groups = UpdateValidation.class)
    @Positive(groups = CreateValidation.class)
    private Integer zIndex;

    /**
     * Дата последней модификации
     */
    private Instant lastModified;

    /**
     * Выпихивает виджет на один уровень вверх
     */
    public void pushOut() {
        this.zIndex += 1;
    }

    /**
     * Обновляет виджет указанными данными
     *
     * @param source данные для обновления
     * @return обновленный виджет
     */
    public Widget merge(Widget source) {
        this.x = source.x;
        this.y = source.y;
        this.width = source.getWidth();
        this.height = source.getHeight();
        this.zIndex = source.getZIndex();
        this.lastModified = Instant.now();

        return this;
    }
}
