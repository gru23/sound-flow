package org.unibl.etf.soundflow.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AudioService {
    /**
     * Gets audio file sent from fronted and storage it.
     * @param file uploaded file through HTTP request
     * @throws IOException internal problem with saving file on disc
     */
    void uploadAudio(MultipartFile file) throws IOException;
    // ovdje ce ici metoda za source separaciju?
}
