package com.zagvladimir.repository;

import com.zagvladimir.model.Group;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface GroupRepository extends JpaRepository<Group, Integer> {
    Optional<Group> findGroupByName(@Size(max = 255) @NotNull String name);

    @Query("SELECT g.course, g.name FROM Group g ORDER BY g.course, g.name")
    List<Object[]> findAllGroupedAndSorted();
}