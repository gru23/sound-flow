package org.unibl.etf.soundflow.controllers;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.soundflow.models.dto.SeparationJob;
import org.unibl.etf.soundflow.models.dto.SeparationStatusResponse;
import org.unibl.etf.soundflow.models.requests.SeparationRequest;
import org.unibl.etf.soundflow.services.AudioService;
import org.unibl.etf.soundflow.services.SeparationJobService;

@RestController
@RequestMapping("/separations")
public class SeparationController {
    private final SeparationJobService separationJobService;
    private final AudioService audioService;

    public SeparationController(SeparationJobService separationJobService, AudioService audioService) {
        this.separationJobService = separationJobService;
        this.audioService = audioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeparationJob> getById(@PathVariable String id) {
        return ResponseEntity.ok(separationJobService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        separationJobService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/separate")
    public ResponseEntity<SeparationStatusResponse> separate(@ModelAttribute SeparationRequest request) {
        return ResponseEntity.ok(audioService.submitSeparationRequest(request));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<SeparationStatusResponse> getStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(separationJobService.getSeparationStatus(jobId));
    }

    @GetMapping("/download/{jobId}")
    public ResponseEntity<Resource> downloadSeparated(
            @PathVariable String jobId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        return audioService.downloadSeparatedZip(jobId, token);
    }
}
