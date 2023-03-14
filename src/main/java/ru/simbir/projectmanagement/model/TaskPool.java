package ru.simbir.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.simbir.projectmanagement.utils.enums.TaskPoolState;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "task_pools")
public class TaskPool extends AbstractEntity {

    @OneToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "task_pool_state")
    private TaskPoolState taskPoolState;

}
