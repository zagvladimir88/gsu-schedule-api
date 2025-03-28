package com.zagvladimir.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageConstants {
    public static final String ANSWER_TEXT_QUEUE = "ANSWER_TEXT";
    public static final String SUCCESS_SUBSCRIPTION = "Подписка успешно оформлена! Ежедневное расписание будет приходить в 9:00";
    public static final String FAILED_SUBSCRIPTION = "Ошибка: группа не найдена. Пример: /set_groups ПОЗ-31";
    public static final String NO_SCHEDULE_FOR_TODAY = "На сегодня расписания нет";
    public static final String FAILED_SCHEDULE_FETCH = "Сначала выберите группу с помощью /set_groups";
    public static final String NO_GROUPS_AVAILABLE = "Доступные группы не найдены";
    public static final String GROUP_NOT_FOUND_TEMPLATE = "%s\nДоступные группы: /get_group";
    public static final String INVALID_COMMAND_MESSAGE = "Некорректный формат команды";
    public static final String UNKNOWN_COMMAND_MESSAGE = "Неизвестная команда";
}