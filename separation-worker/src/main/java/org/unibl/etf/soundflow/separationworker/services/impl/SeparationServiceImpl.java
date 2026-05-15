package org.unibl.etf.soundflow.separationworker.services.impl;

import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.separationworker.exceptions.NotFoundException;
import org.unibl.etf.soundflow.separationworker.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.separationworker.models.enums.SeparationStatus;
import org.unibl.etf.soundflow.separationworker.repositories.SeparationJobEntityRepository;
import org.unibl.etf.soundflow.separationworker.services.SeparationService;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

            // Čitanje standardnog izlaza
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("OUT: " + line);
                }
            }

            // Čitanje error izlaza
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("ERR: " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Process finished with exit code: " + exitCode);


//            process.waitFor();

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

        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("--rm");
        command.add("-v");
        command.add(mapSource);
        command.add("-v");
        command.add(mapDestination);
        command.add("voxextractlabs/vox-demucs:1.0.0");
        command.add("demucs");
        command.add("-n");
        command.add("htdemucs");
        if (!job.getOption().getCommand().isEmpty()) {
            command.add(job.getOption().getCommand());
        }
        command.add(filePath);
        return command.toArray(new String[0]);
//        return new String[]{
//                "docker", "run",
//                "-v", mapSource,
//                "-v", mapDestination,
//                "voxextractlabs/vox-demucs:1.0.0",
//                 "demucs", "-n", "htdemucs", job.getOption().getCommand(), filePath
////                "demucs", "-n", "htdemucs", filePath
//        };
    }
}
/*
docker run
-v C:\Users\Administrator\Desktop\Fakultet\SoundFlow\demucs\input:/app/input
-v C:\Users\Administrator\Desktop\Fakultet\SoundFlow\demucs\separated:/app/separated
voxextractlabs/vox-demucs:1.0.0
demucs -n htdemucs /app/input/song1.mp3

 */