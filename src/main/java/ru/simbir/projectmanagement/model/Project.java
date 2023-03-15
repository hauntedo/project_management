package ru.simbir.projectmanagement.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.simbir.projectmanagement.utils.enums.ProjectState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projects")
@SuperBuilder
@Getter
@Setter
public class Project extends AbstractEntity {

    @Column(name = "project_name", nullable = false)
    private String name;

    @Column(name = "project_description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "project_state")
    private ProjectState projectState;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks = new ArrayList<>();

}
