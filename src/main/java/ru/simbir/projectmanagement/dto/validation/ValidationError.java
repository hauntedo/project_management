package ru.simbir.projectmanagement.dto.validation;

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
public class ValidationError {

    private HttpStatus status;
    private Map<String, String> errors;
}
