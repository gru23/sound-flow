package org.unibl.etf.soundflow.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.requests.ClientRequest;
import org.unibl.etf.soundflow.models.requests.auth.*;
import org.unibl.etf.soundflow.services.AuthService;
import org.unibl.etf.soundflow.services.ClientService;

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
        return clientService.registration(clientRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check")
    public ResponseEntity<Client> check(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(authService.checkClient(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody @Valid RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("token") String token) {
        authService.verify(token);
        return ResponseEntity.ok("Account verified successfully");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset(@RequestBody @Valid ResetPasswordRequest request) {
        authService.requestResetingPassword(request);
        return ResponseEntity.ok("Password reset request has been sent");
    }

    @PostMapping("/reset-confirm")
    public ResponseEntity<String> resetConfirm(@RequestBody @Valid ConfirmResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
