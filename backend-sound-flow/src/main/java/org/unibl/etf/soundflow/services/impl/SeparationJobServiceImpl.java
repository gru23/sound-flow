package org.unibl.etf.soundflow.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.dto.SeparationJob;
import org.unibl.etf.soundflow.models.dto.SeparationStatusResponse;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.models.enums.SeparationStatus;
import org.unibl.etf.soundflow.repositories.SeparationJobEntityRepository;
import org.unibl.etf.soundflow.services.ClientService;
import org.unibl.etf.soundflow.services.SeparationJobService;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeparationJobServiceImpl implements SeparationJobService {
    private final SeparationJobEntityRepository separationJobEntityRepository;
    private final ClientService clientService;
    private final ModelMapper modelMapper;

    public SeparationJobServiceImpl(SeparationJobEntityRepository separationJobEntityRepository, ClientService clientService, ModelMapper modelMapper) {
        this.separationJobEntityRepository = separationJobEntityRepository;
        this.clientService = clientService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<SeparationJob> getAllByClientId(Integer clientId) {
        ClientEntity client = clientService.findById(clientId);
        return client.getJobs()
                .stream()
                .map(j -> modelMapper.map(j, SeparationJob.class))
                .collect(Collectors.toList());
    }

    @Override
    public SeparationJob getById(String id) {
        SeparationJobEntity entity = separationJobEntityRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Not found separation with id: " + id));
        return modelMapper.map(entity, SeparationJob.class);
    }

    @Override
    public SeparationJobEntity save(SeparationJobEntity separationJobEntity) {
        return separationJobEntityRepository.saveAndFlush(separationJobEntity);
    }

    @Override
    public void delete(String id) {
        getById(id);
        separationJobEntityRepository.deleteById(id);
    }

    @Override
    public SeparationJobEntity getSeparationJob(String jobId) {
        return separationJobEntityRepository
                .findById(jobId)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public SeparationStatusResponse getSeparationStatus(String jobId) {
        SeparationJobEntity job = getSeparationJob(jobId);

        String resultUrl = null;
        if(job.getStatus() == SeparationStatus.DONE && job.getSeparatedPath() != null)
            resultUrl = "http://localhost:8080/separations/download/" + job.getId();

        return new SeparationStatusResponse(job.getId(), job.getStatus(), resultUrl);
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
