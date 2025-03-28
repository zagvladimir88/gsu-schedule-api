package com.zagvladimir.util;

import com.zagvladimir.model.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageTextGenerator {

    private static final String SCHEDULE_ENTRY_FORMAT = "%s: %s - %s %s (%s)";
    private static final String DAILY_HEADER = "Расписание на %s:\n\n";

    public String createTextForMessage(List<Schedule> schedules) {
        return schedules.stream()
                .map(this::formatScheduleEntry)
                .collect(Collectors.joining("\n"));
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
}