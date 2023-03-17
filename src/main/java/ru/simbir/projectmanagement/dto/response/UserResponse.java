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
@Schema(name = "user response")
public class UserResponse {

    @Schema(name = "id")
    @JsonProperty("id")
    private UUID id;

    @Schema(name = "email")
    @JsonProperty("email")
    private String email;

    @Schema(name = "role")
    @JsonProperty("role")
    private String role;

    @Schema(name = "full_name")
    @JsonProperty("full_name")
    private String fullName;

}
