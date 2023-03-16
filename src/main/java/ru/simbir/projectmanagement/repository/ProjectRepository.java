package ru.simbir.projectmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.simbir.projectmanagement.model.Project;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

}
