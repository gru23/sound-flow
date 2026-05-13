package org.unibl.etf.soundflow.services.impl;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.models.enums.SeparationStatus;
import org.unibl.etf.soundflow.repositories.SeparationJobEntityRepository;
import org.unibl.etf.soundflow.services.SeparationJobService;

import java.io.File;

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
    public SeparationJobEntity getSeparationJob(String jobId) {
        return separationJobEntityRepository
                .findById(jobId)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public FileSystemResource getSeparatedZip(String jobId) {
        SeparationJobEntity job = getSeparationJob(jobId);

        if(isNotReadyToDownload(job))
            throw new NotFoundException("Separation job not found");

        File zipFile = new File(job.getSeparatedPath());
        if (!zipFile.exists()) {
            throw new NotFoundException("Separation job not found");
        }

        return new FileSystemResource(zipFile);
    }

    private boolean isNotReadyToDownload(SeparationJobEntity job) {
        return job.getStatus() != SeparationStatus.DONE || job.getSeparatedPath() == null;
    }
}
