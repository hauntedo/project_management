package ru.simbir.projectmanagement.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.simbir.projectmanagement.utils.enums.ProjectState;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projects")
@SuperBuilder
@Getter
@Setter
public class Project extends AbstractEntity {

    @Enumerated(value = EnumType.STRING)
    @Column(name = "project_state")
    private ProjectState projectState;

}
