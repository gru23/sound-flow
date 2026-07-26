package org.unibl.etf.soundflow.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.enums.AuthProvider;
import org.unibl.etf.soundflow.models.requests.ClientRequest;
import org.unibl.etf.soundflow.models.requests.auth.*;
import org.unibl.etf.soundflow.services.AuthService;
import org.unibl.etf.soundflow.services.ClientService;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final ClientService clientService;

    public AuthController(AuthService authService, ClientService clientService) {
        this.authService = authService;
        this.clientService = clientService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/registration")
    public Client registration(@RequestBody @Valid ClientRequest clientRequest) {
        return clientService.registration(clientRequest, AuthProvider.LOCAL);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Client> check(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(authService.checkClient(token));
    }

    /**
     * Refreshing session generating new Access token based on Refresh token
     * @param request contains refresh token which will be used for generating new access token
     * @return new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody @Valid RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    /**
     * Verifying client's account
     * @param token client's access token
     * @return message of confirmation
     */
//    @GetMapping("/verify")
//    public ResponseEntity<String> verify(@RequestParam("token") String token) {
//        authService.verify(token);
//        return ResponseEntity.ok("Account verified successfully");
//    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verify(@RequestParam("token") String token) {
        try {
            authService.verify(token);
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create("/verification-success.html"))
                    .build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create("/verification-failed.html"))
                    .build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset(@RequestBody @Valid ResetPasswordRequest request) {
        authService.requestResetingPassword(request);
        return ResponseEntity.ok("Password reset request has been sent");
    }

    @GetMapping("/reset-confirm")
    public ResponseEntity<Void> resetConfirmPage(@RequestParam("token") String token) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/reset-password.html?token=" + token))
                .build();
    }

    @PostMapping("/reset-confirm")
    public ResponseEntity<String> resetConfirm(@RequestBody @Valid ConfirmResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
