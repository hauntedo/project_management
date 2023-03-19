package ru.simbir.projectmanagement.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.simbir.projectmanagement.dto.request.AuthenticationRequest;
import ru.simbir.projectmanagement.dto.response.TokenResponse;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/api/authenticate")
public interface AuthApi {


    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<TokenResponse> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest);


}
