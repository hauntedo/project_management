package ru.simbir.projectmanagement.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import static ru.simbir.projectmanagement.utils.consts.ApiConsts.EMAIL_REGEX;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "registration request")
public class RegistrationRequest {

    @Schema(name = "full_name")
    @JsonProperty("full_name")
    private String fullName;

    @NotNull
    @Email(regexp = EMAIL_REGEX)
    @Schema(name = "email")
    @JsonProperty("email")
    private String email;
    @NotNull
    @Schema(name = "password")
    @JsonProperty("password")
    private String password;


}
