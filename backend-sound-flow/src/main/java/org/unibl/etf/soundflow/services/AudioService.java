package org.unibl.etf.soundflow.services;

import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.soundflow.models.dto.SeparationStatusResponse;
import org.unibl.etf.soundflow.models.requests.SeparationRequest;

import java.io.IOException;

public interface AudioService {
    /**
     * Gets audio file sent from fronted and storage it.
     * @param file uploaded file through HTTP request
     * @throws IOException internal problem with saving file on disc
     */
    void uploadAudio(MultipartFile file) throws IOException;
    SeparationStatusResponse submitSeparationRequest(SeparationRequest request);
}
