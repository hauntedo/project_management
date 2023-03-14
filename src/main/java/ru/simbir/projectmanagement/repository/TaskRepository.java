package ru.simbir.projectmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.simbir.projectmanagement.model.Task;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
