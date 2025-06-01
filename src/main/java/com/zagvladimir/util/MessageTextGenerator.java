package com.zagvladimir.util;

import com.zagvladimir.model.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageTextGenerator {

    private static final String SCHEDULE_ENTRY_FORMAT = "%s — %s в %s, ауд. %s (%s)";
    private static final String DAILY_HEADER = "Расписание на %s:\n\n";

    public String createTextForMessage(List<Schedule> schedules) {
        Map<LocalDate, List<Schedule>> groupedByDate = schedules.stream()
                .sorted(Comparator.comparing(Schedule::getDate)
                        .thenComparing(Schedule::getStartTime))
                .collect(Collectors.groupingBy(Schedule::getDate, LinkedHashMap::new, Collectors.toList()));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")
                .withLocale(new Locale("ru")); // День недели по-русски

        StringBuilder messageBuilder = new StringBuilder();

        for (Map.Entry<LocalDate, List<Schedule>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Schedule> dailySchedules = entry.getValue();

            String dateHeader = capitalizeFirstLetter(date.format(dateFormatter));
            messageBuilder.append(dateHeader).append("\n");

            for (Schedule schedule : dailySchedules) {
                messageBuilder.append(formatScheduleEntry(schedule)).append("\n");
            }

            messageBuilder.append("\n");
        }

        return messageBuilder.toString().trim();
    }

    public String createDailyScheduleText(List<Schedule> schedules, LocalDate date) {
        String header = String.format(DAILY_HEADER, date.format(DateTimeFormatter.ISO_DATE));
        return header + createTextForMessage(schedules);
    }

    private String formatScheduleEntry(Schedule schedule) {
        return String.format(SCHEDULE_ENTRY_FORMAT,
                schedule.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                schedule.getSubject().getName(),
                schedule.getClassroom().getBuilding().getName(),
                schedule.getClassroom().getNumber(),
                schedule.getTeacher().getFullName()
        );
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}