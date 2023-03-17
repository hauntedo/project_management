package ru.simbir.projectmanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

import static ru.simbir.projectmanagement.utils.consts.ApiConsts.VERSION_REGEX;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "release request")
public class ReleaseRequest {

    @NotNull
    @Schema(name = "version")
    @JsonProperty("version")
    @Pattern(regexp = VERSION_REGEX)
    private String version;
    @NotNull
    @Schema(name = "description")
    @JsonProperty("description")
    private String description;
    @NotNull
    @Schema(name = "task_id")
    @JsonProperty("task_id")
    private UUID taskId;
}
