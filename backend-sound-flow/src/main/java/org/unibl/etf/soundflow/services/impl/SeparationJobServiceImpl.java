package org.unibl.etf.soundflow.services.impl;

import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.repositories.SeparationJobEntityRepository;
import org.unibl.etf.soundflow.services.SeparationJobService;

@Service
public class SeparationJobServiceImpl implements SeparationJobService {
    private final SeparationJobEntityRepository separationJobEntityRepository;

    public SeparationJobServiceImpl(SeparationJobEntityRepository separationJobEntityRepository) {
        this.separationJobEntityRepository = separationJobEntityRepository;
    }

    public SeparationJobEntity save(SeparationJobEntity separationJobEntity) {
        return separationJobEntityRepository.saveAndFlush(separationJobEntity);
    }
}
