package ru.simbir.projectmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "release response")
public class ReleaseResponse {

    @Schema(name = "id")
    @JsonProperty("id")
    private UUID id;
    @Schema(name = "version")
    @JsonProperty("version")
    private String version;

    @Schema(name = "description")
    @JsonProperty("description")
    private String description;

    @Schema(name = "start")
    @JsonProperty("start")
    private Instant start;

    @Schema(name = "end")
    @JsonProperty("end")
    private Instant end;

    @Schema(name = "developer")
    @JsonProperty("developer")
    private UserResponse developer;

}
