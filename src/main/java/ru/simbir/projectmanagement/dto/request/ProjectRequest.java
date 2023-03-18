package ru.simbir.projectmanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "project request")
public class ProjectRequest {

    @NotNull
    @Schema(name = "name")
    @JsonProperty("name")
    private String name;

    @Schema(name = "description")
    @JsonProperty("description")
    private String description;

    @NotNull
    @Schema(name = "code")
    @JsonProperty("code")
    private String code;

}
