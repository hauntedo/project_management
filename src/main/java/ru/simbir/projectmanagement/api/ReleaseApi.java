package ru.simbir.projectmanagement.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.simbir.projectmanagement.dto.request.ReleaseRequest;
import ru.simbir.projectmanagement.dto.response.ReleaseResponse;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/api/releases")
public interface ReleaseApi {

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<ReleaseResponse> addRelease(@RequestBody @Valid ReleaseRequest releaseRequest,
                                               @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails);

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/{release-id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ReleaseResponse> getReleaseById(@PathVariable("release-id") UUID releaseId);

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/{release-id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<ReleaseResponse> updateReleaseById(@PathVariable("release-id") UUID releaseId,
                                                      @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody @Valid ReleaseRequest releaseRequest);

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/{release-id}/close", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<ReleaseResponse> closeRelease(@PathVariable("release-id") UUID releaseId,
                                                 @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails);


}
