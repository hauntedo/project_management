package ru.simbir.projectmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.simbir.projectmanagement.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);

    Page<User> findAllByProjects_Id(UUID projects_id, Pageable pageable);
}
