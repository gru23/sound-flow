package org.unibl.etf.soundflow.separationworker.services.impl;

import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.separationworker.exceptions.AudioOperationException;
import org.unibl.etf.soundflow.separationworker.models.entities.SeparationJobEntity;
import org.unibl.etf.soundflow.separationworker.models.enums.SeparationStatus;
import org.unibl.etf.soundflow.separationworker.services.AudioService;
import org.unibl.etf.soundflow.separationworker.services.SeparationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class AudioServiceImpl implements AudioService {
    private final SeparationService separationService;

    public AudioServiceImpl(SeparationService separationService) {
        this.separationService = separationService;
    }

    @Override
    public void archiveStems(SeparationJobEntity job) throws AudioOperationException {
        File jobFile = new File(job.getSourcePath());
        String stemsFolderPath = jobFile.getParent() + File.separator +
                "htdemucs" + File.separator +
                jobFile.getName().substring(0, jobFile.getName().lastIndexOf('.'));
        File stemsFolder = new File(stemsFolderPath);

        System.out.println("Looking for stems in: " + stemsFolder.getAbsolutePath());

        File[] stemFiles = stemsFolder.listFiles();
        if (!stemsFolder.exists() || stemFiles == null || stemFiles.length == 0) {
            throw new AudioOperationException("No stems found to archive (separation failed)");
        }
        System.out.println("Found " + stemFiles.length + " stem files to archive.");

        File zipFile = new File(jobFile.getParent() + "_separation.zip");

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            byte[] buffer = new byte[8192];
            for (File file : stemFiles) {
                System.out.println("Adding file to zip: " + file.getName());
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    int length;
                    while ((length = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
            zos.flush();
            fos.flush();
            job.setStatus(SeparationStatus.DONE);
            separationService.update(job);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AudioOperationException("Unable to archive separation job: " + e.getMessage());
        }

        System.out.println("Zip file created at: " + zipFile.getAbsolutePath());
        job.setSeparatedPath(zipFile.getAbsolutePath());
        separationService.update(job);
    }

}
