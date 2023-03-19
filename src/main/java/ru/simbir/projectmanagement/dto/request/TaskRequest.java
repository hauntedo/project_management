package ru.simbir.projectmanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "task request")
public class TaskRequest {

    @NotNull
    @Schema(name = "name")
    @JsonProperty("name")
    private String name;

    @Schema(name = "description")
    @JsonProperty("description")
    private String description;

    @Schema(name = "project_id")
    @JsonProperty("project_id")
    @NotNull
    private UUID projectId;

}
