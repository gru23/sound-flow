package org.unibl.etf.soundflow.services;

import org.springframework.core.io.FileSystemResource;
import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;

public interface SeparationJobService {
    SeparationJobEntity save(SeparationJobEntity separationJobEntity);
    SeparationJobEntity getSeparationJob(String jobId);
    FileSystemResource getSeparatedZip(String jobId);
}
