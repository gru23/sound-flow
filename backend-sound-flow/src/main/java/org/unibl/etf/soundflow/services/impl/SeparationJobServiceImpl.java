package org.unibl.etf.soundflow.services.impl;

import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.repositories.SeparationJobEntityRepository;
import org.unibl.etf.soundflow.services.SeparationJobService;

@Service
public class SeparationJobServiceImpl implements SeparationJobService {
    private final SeparationJobEntityRepository separationJobEntityRepository;

    public SeparationJobServiceImpl(SeparationJobEntityRepository separationJobEntityRepository) {
        this.separationJobEntityRepository = separationJobEntityRepository;
    }

    @Override
    public SeparationJobEntity save(SeparationJobEntity separationJobEntity) {
        return separationJobEntityRepository.saveAndFlush(separationJobEntity);
    }

    @Override
    public SeparationJobEntity getSeparationJob(Integer jobId) {
        return separationJobEntityRepository
                .findById(jobId)
                .orElseThrow(NotFoundException::new);
    }
}
