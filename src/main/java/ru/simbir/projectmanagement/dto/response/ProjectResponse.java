package ru.simbir.projectmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "project response")
public class ProjectResponse {

    @Schema(name = "id")
    @JsonProperty("id")
    private UUID id;

    @Schema(name = "name")
    @JsonProperty("name")
    private String name;

    @Schema(name = "description")
    @JsonProperty("description")
    private String description;

    @Schema(name = "project_state")
    @JsonProperty("project_state")
    private String projectState;

    @Schema(name = "code")
    @JsonProperty("code")
    private String code;

    @Schema(name = "owner")
    @JsonProperty("owner")
    private UserResponse owner;
}
