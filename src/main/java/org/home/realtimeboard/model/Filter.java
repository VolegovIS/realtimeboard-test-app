package org.home.realtimeboard.model;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Фильтр для выборки виджетов в указанной области
 */
@Data
public class Filter {
    /**
     * Минимальная допустима Y кордината
     */
    private Integer top;

    /**
     * Максимальная допустима Y кордината
     */
    private Integer bottom;

    /**
     * Минимальная допустима X кордината
     */
    private Integer left;

    /**
     * Максимальная допустима X кордината
     */
    private Integer right;

    /**
     * Проверяет фильтр на валидность. Допустим или пустой фильтр, просто так подвязанный spring'ом
     * или полностью заполненный
     *
     * @return факт валидности фильтра
     */
    @AssertTrue(message = "Filter must contains fields 'top', 'bottom', 'left' and 'right'")
    private boolean isValid() {
        return isEmpty() || Objects.nonNull(this.top) && Objects.nonNull(this.bottom) &&
                Objects.nonNull(this.left) && Objects.nonNull(this.right);
    }

    /**
     * Проверяет фильтр на пустоту
     *
     * @return факт пустоты фильтра
     */
    public boolean isEmpty() {
        return Objects.isNull(this.top) && Objects.isNull(this.bottom) &&
                Objects.isNull(this.left) && Objects.isNull(this.right);
    }

    /**
     * Преобразует фильтр в предикат
     *
     * @return предикат для фильтрации
     * @throws IllegalArgumentException если фильтр пуст
     */
    public Predicate<Widget> toPredicate() {
        if (isEmpty()) {
            throw new IllegalArgumentException("Filter is empty");
        }

        return w -> (w.getX() >= this.left) && (w.getX() + w.getWidth() <= this.right) &&
                (w.getY() >= this.top) && (w.getY() + w.getHeight() <= this.bottom);
    }
}
