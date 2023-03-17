package ru.simbir.projectmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "task response")
public class TaskResponse {

    @Schema(name = "id")
    @JsonProperty("id")
    private UUID id;

    @Schema(name = "name")
    @JsonProperty("name")
    private String name;

    @Schema(name = "description")
    @JsonProperty("description")
    private String description;

    @Schema(name = "task_state")
    @JsonProperty("task_state")
    private String taskState;

    @Schema(name = "author")
    @JsonProperty("author")
    private UserResponse author;

    @Schema(name = "developer")
    @JsonProperty("developer")
    private UserResponse developer;

    @Schema(name = "releases")
    @JsonProperty("releases")
    private List<ReleaseResponse> releases;
}
