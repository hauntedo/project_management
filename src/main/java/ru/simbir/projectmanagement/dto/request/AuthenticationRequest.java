package ru.simbir.projectmanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "authentication request")
public class AuthenticationRequest {

    @NotNull
    @Schema(name = "email")
    @JsonProperty("email")
    private String email;

    @NotNull
    @Schema(name = "password")
    @JsonProperty("password")
    private String password;

}
