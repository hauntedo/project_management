package ru.simbir.projectmanagement.dto.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "validation error")
public class ValidationError {

    @Schema(name = "http_status")
    @JsonProperty("http_status")
    private HttpStatus status;

    @Schema(name = "errors")
    @JsonProperty("error")
    private Map<String, String> errors;
}
