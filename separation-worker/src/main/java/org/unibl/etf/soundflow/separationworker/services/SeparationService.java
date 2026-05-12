package org.unibl.etf.soundflow.separationworker.services;

import org.unibl.etf.soundflow.separationworker.models.entities.SeparationJobEntity;

public interface SeparationService {
    SeparationJobEntity getById(String id);
    SeparationJobEntity update(SeparationJobEntity separationJobEntity);
    void failSeparationJob(SeparationJobEntity job, String message);
    void separate(SeparationJobEntity job);
}
