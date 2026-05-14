package org.unibl.etf.soundflow.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
