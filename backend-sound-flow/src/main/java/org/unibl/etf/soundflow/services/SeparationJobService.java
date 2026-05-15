package org.unibl.etf.soundflow.services;

import org.springframework.core.io.FileSystemResource;
import org.unibl.etf.soundflow.models.dto.SeparationJob;
import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;

import java.util.List;

public interface SeparationJobService {
    List<SeparationJob> getAllByClientId(Integer clientId);
    SeparationJob getById(String id);
    SeparationJobEntity save(SeparationJobEntity separationJobEntity);
    void delete(String id);
    SeparationJobEntity getSeparationJob(String jobId);
    FileSystemResource getSeparatedZip(String jobId);
}
