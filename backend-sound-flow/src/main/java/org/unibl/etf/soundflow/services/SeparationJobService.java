package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;

public interface SeparationJobService {
    SeparationJobEntity save(SeparationJobEntity separationJobEntity);
    SeparationJobEntity getSeparationJob(Integer jobId);
}
