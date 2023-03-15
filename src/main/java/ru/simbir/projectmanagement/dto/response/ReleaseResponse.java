package ru.simbir.projectmanagement.dto.response;

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
public class ReleaseResponse {

    private UUID id;
    private String version;
    private String description;
    private Instant start;
    private Instant end;
    private UserResponse developer;

}
