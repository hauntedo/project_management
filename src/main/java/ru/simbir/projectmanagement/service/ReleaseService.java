package ru.simbir.projectmanagement.service;

import ru.simbir.projectmanagement.dto.request.ReleaseRequest;
import ru.simbir.projectmanagement.dto.response.ReleaseResponse;

import java.util.UUID;

public interface ReleaseService {
    ReleaseResponse addRelease(ReleaseRequest releaseRequest, String username);

    ReleaseResponse getReleaseById(UUID releaseId);
}
