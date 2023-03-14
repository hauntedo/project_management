package ru.simbir.projectmanagement.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.simbir.projectmanagement.utils.enums.TaskState;

import javax.persistence.*;
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
    @JoinColumn(name = "task_pool_id", referencedColumnName = "id")
    private TaskPool taskPool;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "task_state", nullable = false)
    private TaskState taskState;


}
