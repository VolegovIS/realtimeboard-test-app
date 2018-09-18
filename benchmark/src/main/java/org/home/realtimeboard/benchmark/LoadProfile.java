package org.home.realtimeboard.benchmark;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Профиль нагрузки на хранилище
 */
public class LoadProfile {
    /**
     * Список допустимых операций и их частот
     */
    private Map<ActionType, Integer> profile;
    private Integer total;

    public LoadProfile(Map<ActionType, Integer> profile) {
        this.profile = profile;
        this.total = profile.values().stream().mapToInt(i -> i).sum();
    }

    /**
     * Выбирает тип следующей операции над хранилищем
     *
     * @param random ГПСЧ
     * @return следующую операцию
     */
    public ActionType nextAction(Random random) {
        Integer rnd = random.nextInt(total);
        Integer partSum = 0;

        for (Map.Entry<ActionType, Integer> entry : profile.entrySet()) {
            partSum += entry.getValue();
            if (rnd < partSum) {
                return entry.getKey();
            }
        }

        return ActionType.ADD;
    }

    /**
     * Формирует профиль нагрузки по его типу
     *
     * @param profileType тип профиля нагрузки
     * @return профиль нагрузки
     */
    public static LoadProfile forType(LoadProfileType profileType) {
        Map<ActionType, Integer> profile = new HashMap<>();

        switch (profileType) {
            case FULL:
                profile.put(ActionType.ADD, 5);
                profile.put(ActionType.FIND_ONE, 50);
                profile.put(ActionType.FIND_ALL, 20);
                profile.put(ActionType.UPDATE, 10);
                profile.put(ActionType.DELETE, 1);
                break;
            case ADD_ONLY:
                profile.put(ActionType.ADD, 1);
                break;
            case FIND_ONE_ONLY:
                profile.put(ActionType.FIND_ONE, 1);
                break;
            case FIND_ALL_ONLY:
                profile.put(ActionType.FIND_ALL, 1);
                break;
            case UPDATE_ONLY:
                profile.put(ActionType.UPDATE, 1);
                break;
            case DELETE_ADD:
                // Операции добавления виджета присутствуют для сохранения наполненности хранилища
                profile.put(ActionType.ADD, 1);
                profile.put(ActionType.DELETE, 1);
                break;
        }

        return new LoadProfile(profile);
    }
}
