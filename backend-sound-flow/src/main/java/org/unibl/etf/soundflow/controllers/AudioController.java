package org.unibl.etf.soundflow.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.soundflow.models.dto.SeparationStatusResponse;
import org.unibl.etf.soundflow.models.requests.SeparationRequest;
import org.unibl.etf.soundflow.services.AudioService;

@RestController
@RequestMapping("/audio")
public class AudioController {
    private final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            audioService.uploadAudio(file);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch(Exception e) {
            System.err.println("File upload failed.");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading file.");
        }
    }

    @PostMapping("/separation")
    public ResponseEntity<SeparationStatusResponse> separate(@ModelAttribute SeparationRequest request) {
        return ResponseEntity.ok(audioService.submitSeparationRequest(request));
    }
}
