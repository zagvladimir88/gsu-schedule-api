package com.zagvladimir.repository;

import com.zagvladimir.model.Group;
import com.zagvladimir.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByGroupAndDate(Group group, LocalDate date);
}
