package ru.simbir.projectmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import ru.simbir.projectmanagement.api.ReleaseApi;
import ru.simbir.projectmanagement.dto.request.ReleaseRequest;
import ru.simbir.projectmanagement.dto.response.ReleaseResponse;
import ru.simbir.projectmanagement.service.ReleaseService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReleaseController implements ReleaseApi {

    private final ReleaseService releaseService;

    @Override
    public ResponseEntity<ReleaseResponse> addRelease(ReleaseRequest releaseRequest, UserDetails userDetails) {
        return ResponseEntity.status(201).body(releaseService.addRelease(releaseRequest, userDetails.getUsername()));
    }

    @Override
    public ResponseEntity<ReleaseResponse> getReleaseById(UUID releaseId) {
        return ResponseEntity.ok(releaseService.getReleaseById(releaseId));
    }

    @Override
    public ResponseEntity<ReleaseResponse> updateReleaseById(UUID releaseId, UserDetails userDetails, ReleaseRequest releaseRequest) {
        return null;
    }

    @Override
    public ResponseEntity<ReleaseResponse> closeRelease(String parameter, UserDetails userDetails) {
        return null;
    }
}
