package org.unibl.etf.soundflow.services.impl;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.JobNotReadyException;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.models.enums.SeparationStatus;
import org.unibl.etf.soundflow.repositories.SeparationJobEntityRepository;
import org.unibl.etf.soundflow.services.SeparationJobService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

        File folder = new File(job.getSeparatedPath());
        File zipFile = new File(folder.getParent(), job.getId() + "_separation.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            for (File file : folder.listFiles()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
            return new FileSystemResource(zipFile);
        } catch(IOException e) {
            throw new JobNotReadyException(e.getMessage());
        }
    }

    private boolean isNotReadyToDownload(SeparationJobEntity job) {
        return job.getStatus() != SeparationStatus.DONE || job.getSeparatedPath() == null;
    }
}
