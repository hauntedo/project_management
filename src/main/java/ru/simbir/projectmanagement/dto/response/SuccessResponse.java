package ru.simbir.projectmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "success response")
public class SuccessResponse {

    @Schema(name = "message")
    @JsonProperty("message")
    private String message;

    @Schema(name = "time")
    @JsonProperty("time")
    private Instant time;
}
