package ru.simbir.projectmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

import static ru.simbir.projectmanagement.utils.consts.ApiConsts.VERSION_REGEX;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReleaseRequest {

    @NotNull
    @Pattern(regexp = VERSION_REGEX)
    private String version;
    @NotNull
    private String description;
    @NotNull
    private UUID taskId;
}
