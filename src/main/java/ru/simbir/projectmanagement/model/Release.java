package ru.simbir.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(name = "releases")
public class Release extends AbstractEntity {

    @Column(name = "release_version", nullable = false)
    private String version;

    @Column(name = "release_description", nullable = false)
    private String description;

    @Column(name = "start_time")
    private Instant start;

    @Column(name = "end_time")
    private Instant end;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;

}
