package com.zagvladimir.repository;

import com.zagvladimir.model.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonTypeRepository extends JpaRepository<LessonType, Long> {
    Optional<LessonType> findByName(String name);
}
