package ru.simbir.projectmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectResponse {

    private UUID id;
    private String name;
    private String description;
    private String projectState;
    private UserResponse owner;
}
