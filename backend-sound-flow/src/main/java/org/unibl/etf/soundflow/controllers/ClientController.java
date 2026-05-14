package org.unibl.etf.soundflow.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.dto.SeparationJob;
import org.unibl.etf.soundflow.models.requests.ClientUpdateRequest;
import org.unibl.etf.soundflow.models.requests.auth.ChangePasswordRequest;
import org.unibl.etf.soundflow.services.ClientService;
import org.unibl.etf.soundflow.services.SeparationJobService;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;
    private final SeparationJobService separationJobService;

    public ClientController(ClientService clientService, SeparationJobService separationJobService) {
        this.clientService = clientService;
        this.separationJobService = separationJobService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable Integer id, @RequestBody @Valid ClientUpdateRequest client) {
        return ResponseEntity.ok(clientService.update(id, client));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid ChangePasswordRequest request) {
        String token = authHeader.replace("Bearer ", "");
        clientService.changePassword(token, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/username-available")
    public ResponseEntity<Boolean> isUsernameAvailable(@RequestParam String username) {
        return ResponseEntity.ok(!clientService.doesUsernameExist(username));
    }

    @GetMapping("/{clientId}/separations")
    public ResponseEntity<List<SeparationJob>> getAllSeparations(@PathVariable Integer clientId) {
        return ResponseEntity.ok(separationJobService.getAllByClientId(clientId));
    }
}
