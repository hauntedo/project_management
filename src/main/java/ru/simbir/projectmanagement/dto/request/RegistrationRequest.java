package ru.simbir.projectmanagement.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationRequest {

    private String fullName;

    @NotNull
    private String email;
    @NotNull
    private String password;


}
