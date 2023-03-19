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
@Schema(name = "user update request")
public class UserUpdateRequest {

    @Schema(name = "full_name")
    @NotNull
    @JsonProperty("full_name")
    private String fullName;

    @Schema(name = "password")
    @NotNull
    @JsonProperty("password")
    private String password;
}
