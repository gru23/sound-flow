package org.unibl.etf.soundflow.separationworker.services;

import org.unibl.etf.soundflow.separationworker.exceptions.AudioOperationException;
import org.unibl.etf.soundflow.separationworker.models.entities.SeparationJobEntity;

public interface AudioService {
//    FileSystemResource archiveStems(SeparationJobEntity job);
    void archiveStems(SeparationJobEntity job) throws AudioOperationException;
}
