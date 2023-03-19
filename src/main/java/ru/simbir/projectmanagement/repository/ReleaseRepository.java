package ru.simbir.projectmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.simbir.projectmanagement.model.Release;

import java.util.UUID;

public interface ReleaseRepository extends JpaRepository<Release, UUID> {
}
