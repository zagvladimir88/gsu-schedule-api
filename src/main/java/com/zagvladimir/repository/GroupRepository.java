package com.zagvladimir.repository;

import com.zagvladimir.model.Group;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface GroupRepository extends JpaRepository<Group, Integer> {
    Optional<Group> findGroupByName(@Size(max = 255) @NotNull String name);
}