package ru.simbir.projectmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.simbir.projectmanagement.model.Project;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Page<Project> findAllByUsers_Id(UUID user_id, Pageable pageable);

    Optional<Project> findByCode(String code);
}
