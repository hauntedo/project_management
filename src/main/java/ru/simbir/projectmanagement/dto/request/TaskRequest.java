package ru.simbir.projectmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskRequest {

    @NotNull
    private String name;

    private String description;

    @NotNull
    private UUID projectId;

}
