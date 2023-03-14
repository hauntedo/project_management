package ru.simbir.projectmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ExceptionResponse {

    private String message;
    private String exceptionName;
    private String date;
    private String description;
}
