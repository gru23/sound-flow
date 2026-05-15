package org.unibl.etf.soundflow.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.soundflow.models.dto.SeparationJob;
import org.unibl.etf.soundflow.services.SeparationJobService;

@RestController
@RequestMapping("/separations")
public class SeparationController {
    private final SeparationJobService separationJobService;

    public SeparationController(SeparationJobService separationJobService) {
        this.separationJobService = separationJobService;
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
}
