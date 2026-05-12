package org.unibl.etf.soundflow.separationworker.services.impl;

import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.separationworker.exceptions.NotFoundException;
import org.unibl.etf.soundflow.separationworker.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.separationworker.models.enums.SeparationStatus;
import org.unibl.etf.soundflow.separationworker.repositories.SeparationJobEntityRepository;
import org.unibl.etf.soundflow.separationworker.services.SeparationService;

import java.io.File;
import java.time.LocalDateTime;

@Service
public class SeparationServiceImpl implements SeparationService {
    private final SeparationJobEntityRepository separationJobEntityRepository;

    public SeparationServiceImpl(SeparationJobEntityRepository separationJobEntityRepository) {
        this.separationJobEntityRepository = separationJobEntityRepository;
    }

    @Override
    public SeparationJobEntity getById(String id) {
        return separationJobEntityRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public SeparationJobEntity update(SeparationJobEntity separationJobEntity) {
        return separationJobEntityRepository.saveAndFlush(separationJobEntity);
    }

    @Override
    public void failSeparationJob(SeparationJobEntity job, String message) {
        System.err.println(message);
        job.setErrorMessage(message);
        job.setStatus(SeparationStatus.FAILED);
        job.setFinishedAt(LocalDateTime.now());
        separationJobEntityRepository.save(job);
    }

    @Override
    public void separate(SeparationJobEntity job) {
        job.setStatus(SeparationStatus.PROCESSING);
        job.setStartedAt(LocalDateTime.now());
        separationJobEntityRepository.saveAndFlush(job);

        try {
            ProcessBuilder pb = new ProcessBuilder(getSeparationCommand(job));
            Process process = pb.start();
            process.waitFor();

            job.setStatus(SeparationStatus.DONE);
            job.setFinishedAt(LocalDateTime.now());
            separationJobEntityRepository.saveAndFlush(job);
        } catch(Exception e) {
            failSeparationJob(job, e.getMessage());
        }
    }
    // -v je neki binding putanje mog racunara i dokera, treba obratiti paznju sta
    // ako radim u istom kontejneru sve vrijeme jer to znaci svi fajlovi idu u iste foldere
    private String[] getSeparationCommand(SeparationJobEntity job) {
        String audioRootPath = new File(job.getSourcePath()).getParent();
        String mapSource = audioRootPath + ":/app/input";
        String mapDestination = audioRootPath + ":/app/separated";
        String fileName = new File(job.getSourcePath()).getName();
        String filePath = "/app/input/" + fileName;
        return new String[]{
                "docker", "run",
                "-v", mapSource,
                "-v", mapDestination,
                "voxextractlabs/vox-demucs:1.0.0",
                "demucs", "-n", "htdemucs", filePath
        };
    }
}
/*
docker run
-v C:\Users\Administrator\Desktop\Fakultet\SoundFlow\demucs\input:/app/input
-v C:\Users\Administrator\Desktop\Fakultet\SoundFlow\demucs\separated:/app/separated
voxextractlabs/vox-demucs:1.0.0
demucs -n htdemucs /app/input/song1.mp3

 */