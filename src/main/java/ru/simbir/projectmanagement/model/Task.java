package ru.simbir.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.simbir.projectmanagement.utils.enums.TaskState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
@SuperBuilder
@Entity
@Setter
@Getter
public class Task extends AbstractEntity {

    @Column(name = "task_name", nullable = false)
    private String name;

    @Column(name = "task_description")
    private String description;

    @OneToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @OneToOne
    @JoinColumn(name = "developer_id", referencedColumnName = "id")
    private User developer;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "task_state", nullable = false)
    private TaskState taskState;

    @OneToMany(mappedBy = "task")
    private List<Release> releases = new ArrayList<>();


}
